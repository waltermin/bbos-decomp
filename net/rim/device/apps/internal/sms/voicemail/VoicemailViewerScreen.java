package net.rim.device.apps.internal.sms.voicemail;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.apps.internal.sms.SMSChangeStatusVerb;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.internal.ui.component.ImageField;

public final class VoicemailViewerScreen extends ModelScreen {
   private SMSChangeStatusVerb _smsMarkOpenedVerb = new SMSChangeStatusVerb(602448, 14, 1, 0, 0, null, null);
   private SMSChangeStatusVerb _smsSaveVerb = new SMSChangeStatusVerb(602480, 18, 16, 0, 0, null, null);

   public VoicemailViewerScreen(Object context) {
      super(0, null, context);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   private static final boolean isOneKey(char key) {
      return key == '1' || Keypad.getAltedChar(key) == '1';
   }

   @Override
   protected final boolean isClickAndHoldKey(char key) {
      return QuickContactList.isValidQuickContactKey(key);
   }

   protected final boolean keyClickedAndHeld(char key, int status, int time) {
      if (isOneKey(key) && QuickContactList.getInstance().invokeQuickContactItemByKey(key)) {
         this.close();
      }

      return false;
   }

   @Override
   public final void setModel(Object model) {
      if (!(model instanceof SMSModel)) {
         throw new IllegalArgumentException();
      }

      super.setModel(model);
      this.deleteAll();
      this.populateScreen();
      this.setSecurityServiceColours(false);
   }

   protected final void populateScreen() {
      SMSModel message = (SMSModel)super._model;
      if (message instanceof FieldProvider) {
         FieldProvider fieldProvider = message;
         Field field = fieldProvider.getField(null);
         if (field != null) {
            this.add(field);
            this.setFocusToTimestamp(field);
            return;
         }
      }

      StringBuffer stringBuffer = new StringBuffer();
      ImageField iconField = new ImageField(36028797019029504L);
      iconField.setImage(MessageIcons.getIcons().getImage(message.getOverallStatusIcon()));
      HorizontalFieldManager statusHfm = new HorizontalFieldManager();
      statusHfm.add(iconField);
      stringBuffer.append(' ');
      DateFormat.getInstance(53).formatLocal(stringBuffer, message._payload.getDisplayDate());
      statusHfm.add(new LabelField(stringBuffer, 18014398509481984L));
      this.add(statusHfm);
      String text = message._payload.getText();
      RichTextField richTextField = new RichTextField(text, 18014398509481984L);
      this.add(richTextField);
      richTextField.setFocus();
   }

   private final void setFocusToTimestamp(Field field) {
      if (field.isFocusable()) {
         field.setFocus();
         if (field instanceof VerticalFieldManager) {
            VerticalFieldManager vfm = (VerticalFieldManager)field;
            if (vfm.getFieldCount() >= 3) {
               vfm.getField(2).setFocus();
            }
         }
      }
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject context = ContextObject.clone(this.getContext());
      context.setFlag(91);
      return context;
   }

   private final Verb getCallVoicemailVerb() {
      VerbRepository vr = VerbRepository.getVerbRepository(-1180661228443544094L);
      Verb[] vmailVerbs = vr.getVerbs(3797587162219887872L);
      return vmailVerbs != null && vmailVerbs.length > 0 ? vmailVerbs[0] : null;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance == 65536 && VoicemailIconManager.getInstance().isIndicatorOn()) {
         Verb voicemailVerb = this.getCallVoicemailVerb();
         if (voicemailVerb != null) {
            menu.add(voicemailVerb);
            menu.setDefault(voicemailVerb);
         }
      }

      SMSModel message = (SMSModel)super._model;
      if (!message.flagsSet(1)) {
         this._smsMarkOpenedVerb.setParameters(message);
         menu.add(this._smsMarkOpenedVerb);
      }

      if (!message.flagsSet(16)) {
         this._smsSaveVerb.setParameters(message);
         menu.add(this._smsSaveVerb);
      }

      VerbFactory outerVerbFactory = (VerbFactory)((ContextObject)super._context).get(-2846768035584909703L);
      if (outerVerbFactory != null) {
         Verb[] verbs = outerVerbFactory.getVerbs(null);
         menu.add(verbs);
      }

      VerbRepository verbRepository = VerbRepository.getVerbRepository(3433073725342984424L);
      if (verbRepository != null) {
         Verb[] verbs = verbRepository.getVerbs(null);
         menu.add(verbs);
      }

      menu.coalesce(-3072555018635390988L, null);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char keyPress = Keypad.map(keycode);
      if (super.keyDown(keycode, time)) {
         return true;
      }

      if (keyPress != 127 && Keypad.getAltedChar(keyPress) != 127) {
         HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
         return hotkey != null && hotkey.invokeHotkey(MessageHotkeys.map(keycode), super._context);
      }

      DeleteSingleItemVerb deleteVerb = new DeleteSingleItemVerb(611472, 1000);
      deleteVerb.setParameters(super._model, super._context);
      super._returnValue = deleteVerb.invoke(null);
      if (ContextObject.getFlag(super._returnValue, 39)) {
         UiApplication.getUiApplication().popScreen(this);
      }

      return true;
   }
}
