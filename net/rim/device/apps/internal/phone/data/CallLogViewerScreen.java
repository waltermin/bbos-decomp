package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.LastUsedDefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.ui.ViewFolderVerb;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.ShowAddressBookVerb;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

class CallLogViewerScreen extends ModelScreen implements ListFieldCallback, EditNotesVerb$CallNotesListener {
   private ListField _callInfoListField;
   private Field _notesField;
   private Verb[] _verbCache = new Verb[0];
   private DefaultProvider _defaultVerbProvider;
   private Object _model;
   private static final int DATE_TIME_INDEX = 0;
   private static final int CALL_TYPE_INDEX = 1;
   private static final int CALL_STATUS_INDEX = 2;
   private static final int CALL_LINE_ID_INDEX = 3;
   private static final int NUM_CALL_INFO_FIELD_ENTRIES = PhoneUtilities.getAllLineIds().length > 1 ? 4 : 3;

   public CallLogViewerScreen(String title, Object context) {
      super(0, false, title, context);
      this.setDefaultClose(false);
      this.setLeaveScreenVerb(ExitVerb.createCloseVerb(0, null));
      this._defaultVerbProvider = (DefaultProvider)ContextObject.get(context, -3439275261910296683L);
   }

   @Override
   public void setModel(Object model) {
      super.setModel(model);
      this._model = model;
      this.deleteAll();
      this.populateScreen();
      this.setSupportClickAndHoldKeyEvents(true);
   }

   private PhoneCallModelImpl getCallLog() {
      return (PhoneCallModelImpl)this._model;
   }

   private void populateScreen() {
      this._callInfoListField = new ListField(NUM_CALL_INFO_FIELD_ENTRIES, 36028797018963968L);
      this._callInfoListField.setCallback(this);
      this.add(this._callInfoListField);
      PhoneCallModelImpl callLog = this.getCallLog();
      int errorCode = callLog.getErrorCode();
      if (errorCode != 0) {
         int labelId = errorCode == 1 ? 169 : 170;
         String label = PhoneResources.getString(labelId);
         String msg = PhoneUtilities.getCallFailureErrorString(errorCode);
         this.add(new RichTextField(label + msg, null, null, null, 36028797018963968L));
      }

      this.add(new SeparatorField());
      ContextObject context = new ContextObject(58, 24, 118);
      Field field = callLog.getField(context);
      if (field != null) {
         this.add(field);
      }

      this.add(new SeparatorField());
      this._notesField = this.getNotesField();
      if (this._notesField != null) {
         this.add(this._notesField);
         if (this._notesField.isFocusable()) {
            this._notesField.setFocus();
         }
      }
   }

   private Field getNotesField() {
      return this.getCallLog().getField(new ContextObject(35));
   }

   @Override
   protected boolean handleKeyChar(char key, int status, int time) {
      return DeviceInfo.isInHolster() ? true : super.handleKeyChar(key, status, time);
   }

   @Override
   protected boolean handleSendKey() {
      Field focusedField = this.getLeafFieldWithFocus();
      PhoneCallModelImpl callLog = this.getCallLog();
      if (focusedField == this._notesField) {
         if (callLog.isConferenceCallLog()) {
            return false;
         }

         if (super.handleSendKey()) {
            return true;
         }
      }

      ContextObject sendContext = new ContextObject(119);
      if (focusedField != null) {
         sendContext.put(6780852635736686874L, focusedField);
      }

      Verb sendKeyVerb = callLog.getVerbs(sendContext, this._verbCache);
      if (sendKeyVerb != null) {
         sendKeyVerb.invoke(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      char key = Keypad.map(keycode);
      if (super.keyDown(keycode, time)) {
         return true;
      }

      if (key != 127 && Keypad.getAltedChar(key) != 127) {
         int k = MessageHotkeys.map(keycode);
         HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
         return hotkey != null && hotkey.invokeHotkey(k, super._context);
      }

      DeleteSingleItemVerb deleteVerb = new DeleteSingleItemVerb(611472, 1000);
      deleteVerb.setParameters(this._model, super._context);
      super._returnValue = deleteVerb.invoke(null);
      if (ContextObject.getFlag(super._returnValue, 39)) {
         UiApplication.getUiApplication().popScreen(this);
      }

      return true;
   }

   @Override
   protected ContextObject getMenuContextObject() {
      if (this.getCallLog().isVoicemailCallLog() && VoicemailIconManager.getInstance().isIndicatorOn()) {
         ContextObject context = ContextObject.clone(this.getContext());
         context.setFlag(91);
         ContextObject.put(context, 3696141428889703675L, this._model);
         return context;
      } else {
         ContextObject co = this.getContext();
         co = ContextObject.castOrCreate(co);
         ContextObject.put(co, 3696141428889703675L, this._model);
         return co;
      }
   }

   @Override
   public void onNotesSaved(String newNotes) {
      if (this._notesField != null) {
         this.delete(this._notesField);
      }

      this._notesField = this.getNotesField();
      this.add(this._notesField);
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      Verb defaultVerb = null;
      if (this._defaultVerbProvider != null) {
         defaultVerb = (Verb)this._defaultVerbProvider.getDefault(null, null);
      }

      RIMModel model = (RIMModel)this.getModel();
      ContextObject verbContext = PhoneContexts.GET_VERBS_CONTEXT_WR.getContextObject();
      verbContext.reset();
      verbContext.setFlag(24, 2);
      Field leafFieldWithFocus = this.getLeafFieldWithFocus();
      if (leafFieldWithFocus != null) {
         verbContext.put(6780852635736686874L, leafFieldWithFocus);
      }

      PhoneUtilities.setPrivateFlag(verbContext, 38);
      PhoneUtilities.setPrivateFlag(verbContext, 44);
      PhoneUtilities.setPrivateFlag(verbContext, 74);
      verbContext.setFlag(49);
      Array.resize(this._verbCache, 0);
      Verb newDefault = null;
      if (model instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)model;
         newDefault = verbProvider.getVerbs(verbContext, this._verbCache);
      }

      if (this._verbCache.length > 0) {
         menu.add(this._verbCache);
      }

      if (defaultVerb == null) {
         defaultVerb = newDefault;
      }

      if (this._notesField != null) {
         PhoneCallModelImpl callLog = this.getCallLog();
         String notes = callLog.getNotes();
         int resourceId = !PhoneUtilities.isEmptyString(notes) ? 433 : 435;
         menu.add(new EditNotesVerb(this, callLog, 16908880, resourceId, notes));
      }

      Folder folder = PhoneFolders.getPhoneFolder(this.getCallLog());
      if (folder != null) {
         ViewFolderVerb viewFolderVerb = ViewFolderVerb.getInstance();
         if (viewFolderVerb != null) {
            viewFolderVerb.setDefaultFolder(folder);
            menu.add(viewFolderVerb);
         }
      }

      VerbFactory outerVerbFactory = (VerbFactory)ContextObject.get(super._context, -2846768035584909703L);
      if (outerVerbFactory != null) {
         menu.add(outerVerbFactory.getVerbs(null));
      }

      VerbRepository vr = VerbRepository.getVerbRepository(2810978548042580997L);
      if (vr != null) {
         menu.add(vr.getVerbs(null));
      }

      menu.add(new ShowAddressBookVerb(16777280));
      menu.add(super._leaveScreenVerb);
      if (defaultVerb != null) {
         menu.setDefault(defaultVerb);
      } else if (super._leaveScreenVerb != null) {
         menu.setDefault(super._leaveScreenVerb);
      }

      DefaultVerbProvider defaultProvider = null;
      if (model != null) {
         defaultProvider = new LastUsedDefaultVerbProvider(model);
      }

      menu.coalesce(-3072555018635390988L, defaultProvider);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int height = listField.getFont().getHeight();
      RIMModel phoneCall = (RIMModel)this.getModel();
      ContextObject context = new ContextObject();
      switch (index) {
         case -1:
            break;
         case 0:
         default:
            context.setFlag(47);
            break;
         case 1:
            context.setFlag(34);
            break;
         case 2:
            context.setFlag(27);
            break;
         case 3:
            if (PhoneUtilities.getAllLineIds().length > 1 && phoneCall instanceof PhoneCallModel) {
               PhoneCallModel pcm = (PhoneCallModel)phoneCall;
               graphics.drawText(PhoneUtilities.getLineDescription(pcm.getLineId()), 0, y, 64, width);
            }
      }

      if (phoneCall instanceof PaintProvider) {
         PaintProvider paintProvider = (PaintProvider)phoneCall;
         paintProvider.paint(graphics, 0, y, width, height, context);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
