package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

final class CallLogItemScreen extends AppsMainScreen implements EditNotesVerb$CallNotesListener, FocusChangeListener {
   private CallLogItem _callLogItem;
   private CallSummaryField _callSummaryField;
   private VerticalFieldManager _totalCallsField;
   private CallHistoryField _historyField;
   private RichTextField _notesField;
   private CallSummaryInfo _callSummaryInfo;
   private Verb[] _verbCache = new Verb[0];
   private static final int NUM_SEPARATORS = 4;
   private static final int SEPARATOR_HEIGHT = 3;
   static final int MENU_EDIT_NOTES = 1332224;
   static final int MENU_ADD_NOTES = 1332225;
   static final int MENU_FORWARD = 1332226;
   static final int MENU_PARTICIPANTS = 1332227;

   CallLogItemScreen(CallLogItem callLogItem) {
      super(562951027163136L);
      this.setSupportClickAndHoldKeyEvents(true);
      this._callLogItem = callLogItem;
      PhoneCallModelImpl callLog = callLogItem.getCallLog();
      if (callLog.isUnopened()) {
         callLog.perform(5803508244060051872L, null);
      }

      this._callSummaryInfo = new CallSummaryInfo(callLogItem);
      this.setSupportClickAndHoldKeyEvents(true);
      this.populate();
   }

   CallLogItemScreen(PhoneCallModelImpl phoneCallLog) {
      this(new CallLogItem(phoneCallLog));
   }

   static final void view(Object callLog) {
      if (callLog instanceof CallLogItem) {
         UiApplication.getUiApplication().pushScreen(new CallLogItemScreen((CallLogItem)callLog));
      } else {
         if (callLog instanceof PhoneCallModelImpl) {
            UiApplication.getUiApplication().pushScreen(new CallLogItemScreen((PhoneCallModelImpl)callLog));
         }
      }
   }

   private final void populate() {
      int heightAvailable = Display.getHeight() - 12 - 4;
      int numLines = 0;
      LabelField titleField = new LabelField(PhoneResources.getResourceBundle(), 6284);
      this.setTitle(titleField);
      numLines++;
      String totalCalls = MessageFormat.format(PhoneResources.getString(6249), new String[]{"" + this._callLogItem.getHistoryItemCount()});
      this._totalCallsField = new VerticalFieldManager();
      this._totalCallsField.add(new SeparatorField());
      this._totalCallsField.add(new LabelField(totalCalls));
      this.setStatus(this._totalCallsField);
      numLines++;
      PhoneCallModelImpl primaryCallLog = this._callLogItem.getCallLog();
      this._callSummaryField = new CallSummaryField(this._callSummaryInfo, primaryCallLog);
      this.add(this._callSummaryField);
      numLines += this._callSummaryField.getNumLines();
      int historyItemCount = this._callLogItem.getHistoryItemCount();
      if (historyItemCount > 1) {
         this.add(new SeparatorField());
         this._historyField = new CallHistoryField(this._callLogItem, this._callSummaryInfo, this);
         this.add(this._historyField);
         numLines += 2;
      } else if (historyItemCount == 1) {
         this.add(new LabelField(PhoneResources.getString(162) + primaryCallLog.getDateTimeString(4)));
         this.add(new SeparatorField());
         this.addNotesField(null);
         numLines += 2;
      }

      Font currFont = Font.getDefault();
      int currFontHeight = currFont.getHeight();
      int maxFontHeight = heightAvailable / numLines;
      maxFontHeight = Math.min(currFontHeight, maxFontHeight);
      if (maxFontHeight < currFontHeight) {
         Font newFont = currFont.derive(currFont.getStyle(), maxFontHeight);
         titleField.getManager().setFont(newFont);
         Manager mgr = this.getDelegate();
         if (mgr != null) {
            mgr.setFont(newFont);
         }
      }
   }

   private final void addNotesField(String notes) {
      if (notes == null) {
         notes = this._callLogItem.getCallLog().getNotes();
      }

      if (!PhoneUtilities.isEmptyString(notes)) {
         long flags = 2306142076376449024L;
         VerticalFieldManager vfm = new VerticalFieldManager(flags);
         this._notesField = new RichTextField(notes, 9007199254740992L);
         vfm.add(this._notesField);
         this.add(vfm);
      }
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (eventType == 2) {
         this.updateCallSummary();
      }
   }

   @Override
   public final void onNotesSaved(String newNotes) {
      if (this._notesField == null) {
         if (this._historyField == null) {
            this.addNotesField(newNotes);
            return;
         }
      } else {
         this._notesField.setText(newNotes);
      }
   }

   private final PhoneCallModelImpl getCurrentCallLog() {
      return this._historyField != null ? this._historyField.getCurrentCallLog() : this._callLogItem.getCallLog();
   }

   private final void updateCallSummary() {
      PhoneCallModelImpl call = null;
      if (this._historyField != null) {
         call = this._historyField.getCurrentCallLog();
      } else {
         call = this._callLogItem.getCallLog();
      }

      if (call != null) {
         this._callSummaryField.update(call);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      Array.resize(this._verbCache, 0);
      PhoneCallModelImpl currCallLog = this.getCurrentCallLog();
      if (currCallLog != null) {
         ContextObject context = new ContextObject(2);
         PhoneUtilities.setPrivateFlag(context, 38);
         PhoneUtilities.setPrivateFlag(context, 74);
         PhoneUtilities.setPrivateFlag(context, 72);
         Verb defaultVerb = currCallLog.getVerbs(context, this._verbCache);
         menu.add(this._verbCache);
         if (defaultVerb != null) {
            menu.setDefault(defaultVerb);
         }

         if (instance == 0) {
            String notes = currCallLog.getNotes();
            if (!PhoneUtilities.isEmptyString(notes)) {
               menu.add(new EditNotesVerb(this, currCallLog, 1332224, 433, notes));
            } else if (notes != null) {
               menu.add(new EditNotesVerb(this, currCallLog, 1332225, 435, notes));
            }

            menu.add(currCallLog.getForwardCallLogVerb(1332226));
            if (currCallLog.isConferenceCallLog()) {
               menu.add(new ConferenceMembersVerb(currCallLog, 1332227, 6313));
            }
         }
      }
   }

   @Override
   protected final boolean handleSendKey() {
      PhoneCallModelImpl call = this.getCurrentCallLog();
      if (call != null) {
         CallerIDInfo cidi = (CallerIDInfo)call.getCallerIDInfo();
         ContextObject sendContext = new ContextObject(119);
         Verb[] verbs = new Verb[0];
         Verb sendKeyVerb = cidi.getVerbs(sendContext, verbs);
         if (sendKeyVerb != null) {
            Object result = sendKeyVerb.invoke(null);
            if (ContextObject.getFlag(result, 39)) {
               this.close();
            }

            return true;
         }
      }

      return false;
   }

   @Override
   protected final Object invokeVerb(Verb verb, Object parameter) {
      if (verb instanceof DialVerb) {
         UiApplication.getUiApplication().popScreen(this);
      }

      return super.invokeVerb(verb, parameter);
   }
}
