package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.CryptoSmartCardUtilities;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.SecurityDialog;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

final class SmartCardOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private BooleanChoiceField _allowLockOnCardRemovalField;
   private BooleanChoiceField _allowPINCachingField;
   private BooleanChoiceField _enableLEDFlashingOnOpenSessionField;
   private ObjectListField _registeredReadersListField;
   private ObjectListField _registeredCardsListField;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   public SmartCardOptionsItem() {
      super(_rb.getFamily(), 24, 5294015899860238835L);
      ContextObject.put(super._context, 244, "net_rim_bb_secureemail_help/smime_smart_card");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      Font font = Font.getDefault();
      Font boldFont = font.derive(font.getStyle() | 1);
      SmartCardOptions options = SmartCardOptions.getInstance();
      this._allowLockOnCardRemovalField = new BooleanChoiceField(_rb.getString(25), 2, options.getAllowLockOnCardRemoval(), 268435456);
      this._allowLockOnCardRemovalField.setEditable(!ITPolicy.getBoolean(24, 1, false));
      this._allowLockOnCardRemovalField.setChangeListener(this);
      mainScreen.add(this._allowLockOnCardRemovalField);
      this._allowPINCachingField = new BooleanChoiceField(_rb.getString(26), 2, options.getAllowPINCaching(), 268435456);
      this._allowPINCachingField.setEditable(ITPolicy.getBoolean(24, 50, false));
      mainScreen.add(this._allowPINCachingField);
      if (LED.isPolychromatic()) {
         this._enableLEDFlashingOnOpenSessionField = new BooleanChoiceField(_rb.getString(32), 2, options.getEnableLEDFlashingOnOpenSession());
         mainScreen.add(this._enableLEDFlashingOnOpenSessionField);
      }

      SmartCardReader[] registeredReaders = SmartCardReaderFactory.getReaders();
      if (registeredReaders != null) {
         mainScreen.add(new SeparatorField());
         LabelField labelField = new LabelField(_rb.getString(27));
         labelField.setFont(boldFont);
         mainScreen.add(labelField);
         this._registeredReadersListField = new ObjectListField();
         this._registeredReadersListField.set(registeredReaders);
         VerticalIndentFieldManager vfm = new VerticalIndentFieldManager();
         vfm.add(this._registeredReadersListField, 6);
         mainScreen.add(vfm);
      }

      SmartCard[] registeredCards = SmartCardFactory.getSmartCards();
      if (registeredCards != null) {
         mainScreen.add(new SeparatorField());
         LabelField labelField = new LabelField(_rb.getString(28));
         labelField.setFont(boldFont);
         mainScreen.add(labelField);
         this._registeredCardsListField = new ObjectListField();
         this._registeredCardsListField.set(registeredCards);
         VerticalIndentFieldManager vfm = new VerticalIndentFieldManager();
         vfm.add(this._registeredCardsListField, 6);
         mainScreen.add(vfm);
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (CryptoSmartCardUtilities.isImportCertificatesAvailable()) {
         verbToMenu.addVerb(new SmartCardImportVerb());
      }
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Verb defaultVerb = super.addCurrentItemVerbs(verbToMenu, instance);
      Verb showSettingsVerb = null;
      SmartCard cardDriver = this.getSelectedSmartCardWithSettings();
      if (cardDriver != null) {
         showSettingsVerb = new SmartCardOptionsItem$ShowDriverSettingsVerb(cardDriver);
      } else {
         SmartCardReader readerDriver = this.getSelectedSmartCardReaderWithSettings();
         if (readerDriver != null) {
            showSettingsVerb = new SmartCardOptionsItem$ShowDriverSettingsVerb(readerDriver);
         }
      }

      if (showSettingsVerb != null) {
         verbToMenu.addVerb(showSettingsVerb);
         return showSettingsVerb;
      } else {
         return defaultVerb;
      }
   }

   private final SmartCardReader getSelectedSmartCardReaderWithSettings() {
      Field fieldWithFocus = super._mainScreen.getLeafFieldWithFocus();
      if (this._registeredReadersListField != null && fieldWithFocus == this._registeredReadersListField) {
         int selectedIndex = this._registeredReadersListField.getSelectedIndex();
         if (selectedIndex != -1) {
            SmartCardReader readerDriver = (SmartCardReader)this._registeredReadersListField.get(null, selectedIndex);
            if (readerDriver.isDisplaySettingsAvailable(null)) {
               return readerDriver;
            }
         }
      }

      return null;
   }

   private final SmartCard getSelectedSmartCardWithSettings() {
      Field fieldWithFocus = super._mainScreen.getLeafFieldWithFocus();
      if (this._registeredCardsListField != null && fieldWithFocus == this._registeredCardsListField) {
         int selectedIndex = this._registeredCardsListField.getSelectedIndex();
         if (selectedIndex != -1) {
            SmartCard cardDriver = (SmartCard)this._registeredCardsListField.get(null, selectedIndex);
            if (cardDriver.isDisplaySettingsAvailable(null)) {
               return cardDriver;
            }
         }
      }

      return null;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char keyPressed = (char)Keypad.key(keycode);
      if (keyPressed == '\n') {
         SmartCard cardDriver = this.getSelectedSmartCardWithSettings();
         if (cardDriver != null) {
            cardDriver.displaySettings(null);
            return true;
         }

         SmartCardReader readerDriver = this.getSelectedSmartCardReaderWithSettings();
         if (readerDriver != null) {
            readerDriver.displaySettings(null);
            return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected final boolean save() {
      if (this.isChanged()) {
         SmartCardOptions options = SmartCardOptions.getInstance();
         boolean oldLockOnCardRemovalSetting = options.getAllowLockOnCardRemoval();
         boolean oldAllowPINCachingSetting = options.getAllowPINCaching();
         boolean oldLEDFlashingOnOpenSessionSetting = options.getEnableLEDFlashingOnOpenSession();
         options.setAllowLockOnCardRemoval(this._allowLockOnCardRemovalField.isAffirmative());
         options.setAllowPINCaching(this._allowPINCachingField.isAffirmative());
         if (this._enableLEDFlashingOnOpenSessionField != null) {
            options.setEnableLEDFlashingOnOpenSession(this._enableLEDFlashingOnOpenSessionField.isAffirmative());
         }

         if (!SecurityDialog.challengeUser(null, false, true, '\u0000', true)) {
            options.setAllowLockOnCardRemoval(oldLockOnCardRemovalSetting);
            options.setAllowPINCaching(oldAllowPINCachingSetting);
            options.setEnableLEDFlashingOnOpenSession(oldLEDFlashingOnOpenSessionSetting);
            return false;
         }
      }

      return super.save();
   }

   private final boolean isChanged() {
      SmartCardOptions options = SmartCardOptions.getInstance();
      return this._allowLockOnCardRemovalField.isAffirmative() != options.getAllowLockOnCardRemoval()
         || this._allowPINCachingField.isAffirmative() != options.getAllowPINCaching()
         || this._enableLEDFlashingOnOpenSessionField != null
            && this._enableLEDFlashingOnOpenSessionField.isAffirmative() != options.getEnableLEDFlashingOnOpenSession();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._allowLockOnCardRemovalField
         && this._allowLockOnCardRemovalField.isAffirmative()
         && Security.getInstance().getUserAuthenticator() == null) {
         Dialog.inform(_rb.getString(30));
      }
   }
}
