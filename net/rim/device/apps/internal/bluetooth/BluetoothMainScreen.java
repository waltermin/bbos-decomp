package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class BluetoothMainScreen extends MainScreenOptionsListItem implements ListFieldCallback, BluetoothDeviceManagerListener {
   private BluetoothDeviceManagerImpl _btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
   private ListField _pairedDevicesListField;
   private BluetoothMainScreen$AddDeviceDialog _addDeviceDialog;
   private BluetoothMainScreen$DeviceSelectionDialog _deviceSelectionDialog;
   private BluetoothMainScreen$PleaseWaitDialog _pleaseWaitDialog;
   private Vector _pairedDevices;
   private UiApplication _app = UiApplication.getUiApplication();
   private boolean _isPowerOn;
   private LabelField _titleField;
   private String _backdoorPairingAddress;
   private boolean _userRequestedPowerChange;
   private boolean _setupMode;
   private boolean _turnPowerOffOnExit;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(2718987090951705092L, "net.rim.device.apps.internal.resource.Bluetooth");

   static final String getString(int id) {
      return _rb.getString(id);
   }

   static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }

   static final ResourceBundleFamily getResourceBundle() {
      return _rb;
   }

   static final void init() {
      OptionsProviderRegistration$OptionsProvider op = new BluetoothMainScreen$1();
      OptionsProviderRegistration.registerOptionsProvider(op);
   }

   BluetoothMainScreen() {
      super(getString(0), new ContextObject());
      ContextObject.put(super._context, 244, "bluetooth");
   }

   BluetoothMainScreen(boolean setup) {
      this();
      this._setupMode = true;
   }

   @Override
   protected final Verb getCloseVerb() {
      return new BluetoothMainScreen$BluetoothMainScreenCloseVerb(this);
   }

   private final String getTitle(boolean colon) {
      StringBuffer title = new StringBuffer(getString(0));
      if (colon) {
         title.append(':');
      }

      title.append(' ');
      title.append(getString(this._isPowerOn ? 15 : 16));
      return title.toString();
   }

   private final void updateScreenContents() {
      super._mainScreen.deleteRange(0, super._mainScreen.getFieldCount());
      this._titleField.setText(this.getTitle(true));
      if (this._isPowerOn) {
         super._mainScreen.add(new LabelField(getString(2)));
         super._mainScreen.add(this._pairedDevicesListField);
         this.updatePairedDevices();
      }
   }

   private final void updatePairedDevices() {
      int index = this._pairedDevicesListField.getSelectedIndex();
      this._pairedDevices = this._btManager.getPairedDevices();
      this._pairedDevicesListField.setSize(this._pairedDevices.size());
      this._pairedDevicesListField.setSelectedIndex(index);
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      super._mainScreen = mainScreen;
      this._pairedDevicesListField = new ListField();
      this._pairedDevicesListField.setCallback(this);
      this._isPowerOn = BluetoothME.isPowerOn();
      this.updateScreenContents();
      this._btManager.addListener(this);
   }

   @Override
   protected final void open() {
      super.open();
      if (this._setupMode) {
         new BluetoothMainScreen$AddDeviceVerb(this).invoke(null);
      }
   }

   @Override
   protected final Field getTitleField() {
      this._titleField = new LabelField(this.getTitle(true), 64);
      return this._titleField;
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Verb defaultVerb = null;
      int index = -1;
      if (super._mainScreen.getLeafFieldWithFocus() == this._pairedDevicesListField) {
         index = this._pairedDevicesListField.getSelectedIndex();
      }

      if (instance == 65536) {
         if (index == -1) {
            defaultVerb = new BluetoothMainScreen$AddDeviceVerb(this);
            verbToMenu.addVerb(defaultVerb);
         }
      } else {
         verbToMenu.addVerb(new BluetoothMainScreen$EnableDisableVerb(this, !this._isPowerOn));
         defaultVerb = new BluetoothMainScreen$AddDeviceVerb(this);
         verbToMenu.addVerb(defaultVerb);
         verbToMenu.addVerb(new BluetoothMainScreen$OptionsVerb());
      }

      if (index != -1) {
         BluetoothDevice device = (BluetoothDevice)this._pairedDevices.elementAt(index);
         Verb verb = device.addVerbs(verbToMenu);
         if (verb != null) {
            defaultVerb = verb;
         }
      }

      return defaultVerb;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (!super.confirm(verb, context)) {
         return false;
      }

      this._btManager.removeListener(this);
      return true;
   }

   @Override
   protected final boolean doCloseVerb() {
      boolean invoked = super.doCloseVerb();
      this._btManager.removeListener(this);
      return invoked;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      ((BluetoothDevice)this._pairedDevices.elementAt(index)).paint(graphics, y, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   private final void inquiryDone() {
      if (this._addDeviceDialog != null) {
         this._addDeviceDialog.close();
         Vector devices = this._btManager.getInRangeDevices(true);
         if (devices.size() == 0) {
            if (Dialog.ask(3, getString(9)) == 4) {
               this._addDeviceDialog = new BluetoothMainScreen$AddDeviceDialog(this, true);
               this._addDeviceDialog.start();
            }
         } else {
            this._deviceSelectionDialog = new BluetoothMainScreen$DeviceSelectionDialog(this, devices);
            this._app.pushScreen(this._deviceSelectionDialog);
         }

         this._btManager.fetchDeviceNames();
      }
   }

   @Override
   public final void inquiryComplete() {
      this.inquiryDone();
   }

   @Override
   public final void inquiryCancelled() {
      this.inquiryDone();
   }

   @Override
   public final void deviceAdded() {
      this.updatePairedDevices();
      this._turnPowerOffOnExit = false;
   }

   @Override
   public final void deviceRemoved() {
      this.updatePairedDevices();
   }

   @Override
   public final void deviceListUpdated() {
      if (this._deviceSelectionDialog != null) {
         this._deviceSelectionDialog.redraw();
      }

      this.updatePairedDevices();
   }

   @Override
   public final void deviceInfoUpdated() {
   }

   @Override
   public final void stateChanged() {
      if (this._pleaseWaitDialog != null) {
         this._app.popScreen(this._pleaseWaitDialog);
         this._pleaseWaitDialog = null;
      }

      boolean isPowerOn = BluetoothME.isPowerOn();
      if (isPowerOn != this._isPowerOn) {
         this._isPowerOn = isPowerOn;
         if (this._userRequestedPowerChange) {
            Status.show(this.getTitle(false), this._btManager.getDialogImage(), 3000, 0, true, false, 0);
            this._userRequestedPowerChange = false;
         } else if (isPowerOn && this._addDeviceDialog != null) {
            this._addDeviceDialog.start();
         }
      }

      this.updateScreenContents();
   }

   @Override
   public final void inquiryResult(RemoteDevice remoteDevice, DeviceClass deviceClass) {
      if (this._addDeviceDialog != null) {
         this._addDeviceDialog.deviceAdded();
      }
   }

   @Override
   public final void servicesDiscovered(int transactionID, ServiceRecord[] services) {
   }

   @Override
   public final void serviceSearchCompleted(int transactionID, int result) {
   }

   @Override
   public final void pairingInProgress() {
      if (this._addDeviceDialog != null) {
         this._addDeviceDialog.close();
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1346455889:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1346455890:
         default:
            SimpleInputDialog d = new SimpleInputDialog(4, "Enter MAC address of device:", 12, 12, 0);
            d.setText(this._backdoorPairingAddress);
            d.show();
            this._backdoorPairingAddress = d.getText();
            if (this._backdoorPairingAddress != null && this._backdoorPairingAddress.length() == 12) {
               byte[] address = new byte[6];

               for (int i = 0; i < 6; i++) {
                  int j = i * 2;
                  String s = this._backdoorPairingAddress.substring(j, j + 2);
                  address[5 - i] = (byte)Integer.parseInt(s, 16);
               }

               this._btManager.pairDevice(address);
               return true;
            } else {
               return true;
            }
      }
   }
}
