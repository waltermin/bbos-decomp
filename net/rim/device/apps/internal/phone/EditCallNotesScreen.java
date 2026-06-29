package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.NotesContainer;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.ActivePhoneScreenHeader;
import net.rim.device.apps.internal.phone.api.ui.NotesFieldEditor;
import net.rim.device.apps.internal.phone.api.ui.PhoneAwareScreen;
import net.rim.device.internal.i18n.CommonResource;

final class EditCallNotesScreen extends PhoneAwareScreen implements Confirmation, Runnable {
   private EditCallNotesScreen$EditCallNotesManager _mgr;
   private CallContainer _callContainer;
   private LiveCall _liveCall;
   private VoiceApp _voiceApp;
   private EditField _notesField;
   private Verb _exitVerb;
   private int[] _updateEvents = new int[50];
   private int _updateEventIndex;

   final void updateCalls(Vector calls) {
      this._callContainer.update(calls);
   }

   public final void appDeactivated() {
      this.saveNotes();
   }

   @Override
   public final void run() {
      int event = this.popUpdateEvent();
      switch (event) {
         case 1001:
         case 1003:
         case 1006:
         case 3004:
         case 150040:
            this.invalidate();
         default:
            return;
         case 1002:
         case 1004:
            this.handleCallChanged();
      }
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (!this._notesField.isDirty()) {
         return true;
      }

      if (!this._liveCall.getFlag(4)) {
         this.saveNotes();
         return true;
      }

      switch (Dialog.ask(1, CommonResource.getString(10003))) {
         case -1:
            return false;
         case 1:
            this.saveNotes();
         case -2:
         case 0:
            return true;
         case 2:
         default:
            this._notesField.setText("");
            return true;
      }
   }

   EditCallNotesScreen(LiveCall liveCall, VoiceApp voiceApp) {
      super(voiceApp, null, 0);
      this._voiceApp = voiceApp;
      this.setTag(Tag.create("client"));
      this.setId("activecallnotes");
      this.removeAllMenuItems();
      if (!(liveCall instanceof Object)) {
         throw new Object();
      }

      this._liveCall = liveCall;
      this._exitVerb = new EditCallNotesScreen$ExitNotesVerb(this);
      this.setCloseVerb(this._exitVerb);
      this.populateScreen();
   }

   private final void populateScreen() {
      this._mgr = new EditCallNotesScreen$EditCallNotesManager();
      this.add(this._mgr);
      ActivePhoneScreenHeader header = (ActivePhoneScreenHeader)(new Object(this._liveCall, this));
      this._mgr.add(header);
      ContextObject context = (ContextObject)(new Object(35, 0, 20));
      if (ApplicationManager.getApplicationManager().isSystemLocked()) {
         context.setFlag(120);
      }

      this._notesField = (EditField)this._liveCall.getField(context);
      VerticalFieldManager notesVFM = (VerticalFieldManager)(new Object());
      NotesFieldEditor notesFieldEditor = (NotesFieldEditor)(new Object(this._notesField, false));
      SeparatorField separator = (SeparatorField)(new Object());
      notesVFM.add(separator);
      notesFieldEditor.setPrefHeight((Display.getHeight() >> 1) - separator.getPreferredHeight());
      notesVFM.add(notesFieldEditor);
      this._mgr.add(notesVFM);
      notesFieldEditor.setFocus();
      int length = this._notesField.getTextLength();
      if (length > 0) {
         this._notesField.setCursorPosition(length);
      }

      this._callContainer = new CallContainer(this._liveCall, 2);
      this._mgr.add(this._callContainer);
   }

   private final synchronized void pushUpdateEvent(int eventId) {
      this._updateEvents[this._updateEventIndex] = eventId;
      this._updateEventIndex++;
      this.refreshOnCorrectThread(this);
   }

   private final synchronized int popUpdateEvent() {
      int lastEvent = this._updateEvents[this._updateEventIndex - 1];
      this._updateEvents[this._updateEventIndex] = 0;
      this._updateEventIndex--;
      return lastEvent;
   }

   @Override
   protected final void onEvent(int eventId, int param1, Object param2) {
      if (this._voiceApp.isForeground() && this._voiceApp.getActiveScreen() == this && eventId == 3004) {
         this.invalidate();
      } else {
         this.pushUpdateEvent(eventId);
      }
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject context = ContextObject.clone(this.getContext());
      PhoneUtilities.setPrivateFlag(context, 37);
      return context;
   }

   private final void handleCallChanged() {
      LiveCall currentCall = (LiveCall)CallManager.getInstance().getCurrentCall();
      if (currentCall != null && !this._liveCall.equals(currentCall)) {
         this.saveNotes();
         this._liveCall = currentCall;
         this.deleteAll();
         this.populateScreen();
      } else {
         this.invalidate();
      }
   }

   @Override
   public final void show() {
      super._app.pushModalScreen(this);
   }

   private final void saveNotes() {
      if (this._notesField.isDirty()) {
         this._notesField.setDirty(false);
         String notes = this._notesField.getText();
         ((NotesContainer)this._liveCall).setNotes(notes);
      }
   }

   @Override
   protected final void setDefaultMenuItem(SystemEnabledMenu menu, ContextObject menuContext) {
      menu.setDefault(this._exitVerb);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27 && !this._notesField.isSelecting()) {
         if (this.confirm(null, null)) {
            this.close();
         }

         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
