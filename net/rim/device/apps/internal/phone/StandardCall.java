package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.phone.api.NotesContainer;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.apps.internal.phone.options.CallTimers;
import net.rim.device.internal.system.AudioInternal;

class StandardCall extends LiveCall implements NotesContainer {
   protected BodyModel _notesModel;
   private AfterDialToneHandler _afterDialToneHandler;
   private boolean _updatedCallMeters;
   private static final int MAX_AFTER_DIAL_DELAY;

   void addedToConference() {
      this.setFlag(16);
      this.setFlag(8192);
   }

   void removedFromConference(LiveCall conferenceCall) {
      if (conferenceCall.isMuted()) {
         this.setFlag(64);
      } else {
         this.clearFlag(64);
      }

      this.clearFlag(16);
   }

   public BodyModel getNotesModel() {
      return this._notesModel;
   }

   public synchronized void setNotesModel(BodyModel notesModel) {
      this._notesModel = notesModel;
      if (this.getFlag(4)) {
         this.save();
      }
   }

   @Override
   public String getNotes() {
      return this._notesModel == null ? null : this._notesModel.getText();
   }

   @Override
   public synchronized void setNotes(String notes) {
      if (this._notesModel == null) {
         this._notesModel = (BodyModel)FactoryUtil.createInstance(2096811533660483L, null);
      }

      this._notesModel.setText(notes);
      if (this.getFlag(4)) {
         this.save();
      }
   }

   @Override
   protected void doResaveWork() {
      PhoneFolders.replaceCallLogNotes(this.getPhoneCall(), this.getNotes());
      super.doResaveWork();
   }

   @Override
   protected void onCallConnected(Object context) {
      super.onCallConnected(context);
      AudioInternal.mute(this.isMuted());
      if (this._afterDialToneHandler != null) {
         this._afterDialToneHandler.start(false);
      }
   }

   @Override
   protected void onCallResumed(Object context) {
      super.onCallResumed(context);
      if (this.isMuted()) {
         AudioInternal.mute(true);
      } else {
         AudioInternal.mute(false);
      }
   }

   @Override
   public void updateCallMeters() {
      if (!this._updatedCallMeters) {
         this._updatedCallMeters = true;
         CallTimers.getCallTimers().updateTimers(0, 1, this.getElapsedTime());
      }
   }

   @Override
   protected void doSaveWork() {
      PhoneCallModel phoneCallModel = this.getPhoneCall();
      BodyModel notesModel = (BodyModel)phoneCallModel.getAt(2);
      if (notesModel != null) {
         BodyModel model = this.getNotesModel();
         if (model != null) {
            notesModel.setTextEncoding(model.getTextEncoding());
         }
      }

      super.doSaveWork();
   }

   @Override
   public boolean isMuted() {
      if (this.getFlag(16)) {
         ConferenceCall conferenceCall = CallManager.getInstance().getConferenceCall();
         if (conferenceCall != null) {
            return conferenceCall.isMuted();
         }
      }

      return super.isMuted();
   }

   @Override
   protected boolean logRequired() {
      String notes = this.getNotes();
      return notes != null && notes.length() > 0 ? true : super.logRequired();
   }

   public StandardCall(PhoneCallInitialData data, Object context) {
      super(data, context);
      if (data != null) {
         String additionalNumbers = (String)ContextObject.get(data._context, 7528018505720453076L);
         if (this.isOutgoing() && additionalNumbers != null && additionalNumbers.length() > 0) {
            UiApplication app = UiApplication.getUiApplication();
            this._afterDialToneHandler = new AfterDialToneHandler(this, additionalNumbers, app, data._context);
            char ch = additionalNumbers.charAt(0);
            if (ch == '!') {
               app.invokeLater(new StandardCall$1(this), 5000, false);
            }
         }
      }
   }

   @Override
   public Field getField(Object context) {
      if (ContextObject.getFlag(context, 35)) {
         if (this._notesModel == null) {
            this._notesModel = (BodyModel)FactoryUtil.createInstance(2096811533660483L, null);
         }

         if (this._notesModel instanceof Object) {
            FieldProvider fp = (FieldProvider)this._notesModel;
            return fp.getField(context);
         }
      }

      return super.getField(context);
   }
}
