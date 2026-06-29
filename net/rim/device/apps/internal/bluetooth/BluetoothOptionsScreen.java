package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.SecurityDialog;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.DisplayCategoriesForFieldVerb;
import net.rim.device.internal.bluetooth.BluetoothA2DP;
import net.rim.device.internal.bluetooth.BluetoothAVRCP;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.HandsfreeGateway;
import net.rim.device.internal.bluetooth.HeadsetGateway;
import net.rim.device.internal.ui.component.PropertyField;

final class BluetoothOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private BluetoothDeviceManagerImpl _btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
   private BooleanChoiceField _discoverableField;
   private EditField _localNameField;
   private ObjectChoiceField _allowOutgoingCallsField;
   private int _minAllowOutgoingCallsChoice;
   private ObjectChoiceField _addressBookTransferField;
   private Field _addressBookCategoriesField;
   private BooleanChoiceField _ledField;
   private ObjectChoiceField _securityModeField;
   private BooleanChoiceField _connectOnPowerUpField;

   private BluetoothOptionsScreen() {
      super(BluetoothMainScreen.getString(12));
      ContextObject.put(super._context, 244, "bluetooth");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._localNameField = new EditField(BluetoothMainScreen.getString(1), this._btManager.getLocalName(), 64, 0);
      mainScreen.add(this._localNameField);
      this._discoverableField = new BooleanChoiceField(BluetoothMainScreen.getString(3) + ':', 0, this._btManager.isDiscoverable(), 268435456);
      this._discoverableField.setEditable(!ITPolicy.getBoolean(34, 6, false));
      mainScreen.add(this._discoverableField);
      String[] allChoices = BluetoothMainScreen.getStringArray(55);
      int numChoices = allChoices.length;
      this._minAllowOutgoingCallsChoice = MathUtilities.clamp(0, ITPolicy.getInteger(34, 7, 0), numChoices - 1);
      numChoices -= this._minAllowOutgoingCallsChoice;
      String[] choices = new String[numChoices];
      System.arraycopy(allChoices, this._minAllowOutgoingCallsChoice, choices, 0, numChoices);
      this._allowOutgoingCallsField = new ObjectChoiceField(
         BluetoothMainScreen.getString(54) + ':', choices, this._btManager.getAllowOutgoingCalls() - this._minAllowOutgoingCallsChoice, 268435456
      );
      mainScreen.add(this._allowOutgoingCallsField);
      int mode = this._btManager.getAddressBookTransferMode();
      this._addressBookTransferField = new ObjectChoiceField(BluetoothMainScreen.getString(52) + ':', BluetoothMainScreen.getStringArray(76), mode, 268435456);
      this._addressBookTransferField.setEditable(!ITPolicy.getBoolean(34, 8, false));
      mainScreen.add(this._addressBookTransferField);
      this._addressBookTransferField.setChangeListener(this);
      ContextObject context = new ContextObject();
      context.setFlag(0);
      context.put(3986845832244503196L, BluetoothMainScreen.getString(75));
      this._addressBookCategoriesField = this._btManager.getAddressBookCategories().getField(context);
      if (mode == 3) {
         mainScreen.add(this._addressBookCategoriesField);
      }

      this._ledField = new BooleanChoiceField(BluetoothMainScreen.getString(56) + ':', 1, this._btManager.isLEDIndicatorEnabled(), 268435456);
      this._ledField.setEditable(!ITPolicy.getBoolean(34, 15, false));
      mainScreen.add(this._ledField);
      mode = this._btManager.getSecurityMode();
      this._securityModeField = new ObjectChoiceField(BluetoothMainScreen.getString(93), BluetoothMainScreen.getStringArray(94), mode, 268435456);
      this._securityModeField.setEditable(!ITPolicy.getBoolean(34, 13, false));
      mainScreen.add(this._securityModeField);
      this._connectOnPowerUpField = new BooleanChoiceField(BluetoothMainScreen.getString(95), 0, this._btManager.isConnectOnPowerUpEnabled());
      mainScreen.add(this._connectOnPowerUpField);
      byte[] addr = BluetoothME.getLocalDeviceAddress();
      if (addr != null) {
         mainScreen.add(new SeparatorField());
         mainScreen.add(new PropertyField(BluetoothMainScreen.getString(77), BluetoothME.deviceAddressToString(addr, true), 36028797018963968L));
      }

      mainScreen.add(new SeparatorField());
      mainScreen.add(new LabelField(BluetoothMainScreen.getString(34)));
      if (HeadsetGateway.isEnabled()) {
         this.addServiceLabel(mainScreen, 37);
      }

      if (HandsfreeGateway.isEnabled()) {
         this.addServiceLabel(mainScreen, 38);
      }

      if (BluetoothDeviceManager.isDesktopConnectivityEnabled()) {
         this.addServiceLabel(mainScreen, 57);
      }

      if (BluetoothDeviceManager.isWirelessBypassEnabled()) {
         this.addServiceLabel(mainScreen, 58);
      }

      if (BluetoothDeviceManager.isDUNEnabled()) {
         this.addServiceLabel(mainScreen, 83);
      }

      if (BluetoothA2DP.isSupported() && BluetoothA2DP.isEnabled()) {
         this.addServiceLabel(mainScreen, 92);
      }

      if (BluetoothAVRCP.isSupported() && BluetoothAVRCP.isEnabled()) {
         this.addServiceLabel(mainScreen, 79);
      }
   }

   private final void addServiceLabel(MainScreen mainScreen, int rc) {
      mainScreen.add(new LabelField("  " + BluetoothMainScreen.getString(rc), 1170935903116329024L));
   }

   @Override
   protected final boolean save() {
      String localName = this._localNameField.getText().trim();
      if (localName.length() == 0) {
         Status.show(BluetoothMainScreen.getString(46));
         return false;
      }

      boolean discoverable = this._discoverableField.isAffirmative();
      if (discoverable && ITPolicy.getBoolean(34, 12, false) && !SecurityDialog.challengeUser(BluetoothMainScreen.getString(60), false, true, '\u0000', true)) {
         return false;
      }

      this._btManager.setDiscoverable(discoverable);
      this._btManager.setAllowOutgoingCalls(this._allowOutgoingCallsField.getSelectedIndex() + this._minAllowOutgoingCallsChoice);
      this._btManager.setAddressBookTransferMode(this._addressBookTransferField.getSelectedIndex());
      CategoriesModel categories = this._btManager.getAddressBookCategories();
      categories.grabDataFromField(this._addressBookCategoriesField, null);
      this._btManager.enableLEDIndicator(this._ledField.isAffirmative());
      this._btManager.setLocalName(localName);
      this._btManager.setSecurityMode(this._securityModeField.getSelectedIndex());
      this._btManager.enableConnectOnPowerUp(this._connectOnPowerUpField.isAffirmative());
      return super.save();
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      if (super._mainScreen.getFieldWithFocus() == this._addressBookCategoriesField) {
         verbToMenu.addVerb(new DisplayCategoriesForFieldVerb(this._addressBookCategoriesField));
      }

      super.addScreenVerbs(verbToMenu, instance);
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            return true;
         default:
            return false;
      }
   }

   public static final void show() {
      BluetoothOptionsScreen optionsScreen = new BluetoothOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._addressBookTransferField) {
         int index = this._addressBookTransferField.getSelectedIndex();
         if (index == 3) {
            super._mainScreen.insert(this._addressBookCategoriesField, field.getIndex() + 1);
            return;
         }

         if (this._addressBookCategoriesField.getManager() != null) {
            super._mainScreen.delete(this._addressBookCategoriesField);
         }
      }
   }
}
