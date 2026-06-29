package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.ConnectionIcons;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.vm.PersistentInteger;
import net.rim.vm.Process;

public final class ConnectionsPopupScreen extends PopupScreen implements Runnable, FieldChangeListener, PhoneEventListener {
   private final boolean _wifiEnabled;
   private final int TOGGLE_ALL_IDX;
   private final int SERVICES_STATUS_IDX;
   private final int SETUP_WIFI_IDX;
   private final int SETUP_BLUETOOTH_IDX;
   private final int WIRELESS_OPTIONS_IDX;
   private final int WIFI_OPTIONS_IDX;
   private final int BLUETOOTH_OPTIONS_IDX;
   private final boolean _bluetoothEnabled;
   private int _globalToggleState;
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(-348546850453906601L, "net.rim.device.apps.internal.manageconnections.ManageConnections");
   private ManageConnectionsApp _app = (ManageConnectionsApp)Application.getApplication();
   private ServicesStatusScreen _statusScreen = new ServicesStatusScreen();
   private MyRadioStatusListener _wirelessListener;
   private MyRadioStatusListener _wifiListener;
   private MyServiceRoutingListener2 _srListener;
   private MyBluetoothListener _bluetoothListener;
   private Image _indicatorOff;
   private Image _indicatorNotConnected;
   private Image _indicatorConnected;
   private ImageField _wirelessIndicator;
   private ImageField _wifiIndicator;
   private ImageField _bluetoothIndicator;
   private LabelField _globalToggleLabel;
   private CheckboxField _wirelessToggle;
   private CheckboxField _wifiToggle;
   private CheckboxField _bluetoothToggle;
   private LabelField _wirelessStatus;
   private LabelField _wifiStatus;
   private LabelField _bluetoothStatus;
   private boolean _updatePending;
   private static final int STATE_NORMAL = 0;
   private static final int STATE_OFF_RESTORE = 1;
   private static final int STATE_OFF = 2;
   private static final String MC_ID = "manage-connections";
   private static final Tag STATUS_TAG = Tag.create("status");
   private static int _restoreStateHandle;

   public final void updateLater() {
      if (!this._updatePending) {
         this._updatePending = true;
         this._app.invokeLater(this);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if ((context & -2147483648) != 1) {
         if (field != this._wirelessToggle) {
            if (this._wifiEnabled && field == this._wifiToggle) {
               if (this._wifiToggle.getChecked()) {
                  if ((DeviceInfo.getBatteryStatus() & 16384) != 0) {
                     Status.show(CommonResources.getString(9099), Bitmap.getPredefinedBitmap(2), 8000, 33554432, true, false, -2147483646);
                     this._wifiToggle.setChecked(false);
                  } else if (!RadioInternal.activateRadios(4)) {
                     ManageConnectionsApp.informUser(33);
                     this._wifiToggle.setChecked(false);
                  } else {
                     PersistentInteger.set(_restoreStateHandle, 0);
                  }
               } else {
                  RadioInternal.deactivateRadios(4);
               }

               this.updateGlobalToggle();
            } else {
               if (field == this._bluetoothToggle) {
                  if (this._bluetoothToggle.getChecked()) {
                     BluetoothDeviceManager.getInstance().setPowerOn(true);
                     PersistentInteger.set(_restoreStateHandle, 0);
                  } else {
                     BluetoothDeviceManager.getInstance().setPowerOn(false);
                  }

                  this.updateGlobalToggle();
               }
            }
         } else {
            if (this._wirelessToggle.getChecked()) {
               if ((DeviceInfo.getBatteryStatus() & 16384) != 0) {
                  Status.show(CommonResources.getString(9099), Bitmap.getPredefinedBitmap(2), 8000, 33554432, true, false, -2147483646);
                  this._wirelessToggle.setChecked(false);
               } else if (!RadioInternal.activateRadios(ManageConnectionsApp.getEnabledRadios())) {
                  if (RadioInternal.getGANPreference() == 2) {
                     ManageConnectionsApp.informUser(40);
                  } else {
                     ManageConnectionsApp.informUser(32);
                  }

                  this._wirelessToggle.setChecked(false);
               } else {
                  PersistentInteger.set(_restoreStateHandle, 0);
               }
            } else {
               Object o = ApplicationRegistry.getApplicationRegistry().get(-6196519223735961895L);
               if (o instanceof SetParameter) {
                  ((SetParameter)o).setParameter(this);

                  label102:
                  try {
                     synchronized (this) {
                        this.wait(5000);
                     }
                  } finally {
                     break label102;
                  }
               }

               RadioInternal.deactivateRadios(ManageConnectionsApp.CELL_RADIOS);
            }

            this.updateGlobalToggle();
         }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      if (eventId == 1000) {
         this._app.invokeLater(new ConnectionsPopupScreen$2(this));
      }
   }

   @Override
   public final void run() {
      this._updatePending = false;
      this._statusScreen.update();
      this.updateStatus();
   }

   @Override
   protected final boolean keyChar(char keyChar, int statusInt, int timeInt) {
      boolean consumed = false;
      if (keyChar == '\n') {
         consumed = this.executeCurrentSelection();
      }

      if (!consumed) {
         consumed = super.keyChar(keyChar, statusInt, timeInt);
      }

      return consumed;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 27) {
         this.close();
         return true;
      }

      if (key != 17 && key != 18 && key != 19 && key != 21 && key != 4098) {
         return super.keyDown(keycode, time);
      }

      this.close();
      return false;
   }

   private final boolean executeCurrentSelection() {
      boolean consumed = false;
      int idx = this.getFieldWithFocusIndex();
      if (idx != this.TOGGLE_ALL_IDX) {
         if (idx == this.SERVICES_STATUS_IDX) {
            this._app._acceptsForeground = true;
            this._app.requestForeground();
            this._app.popScreen(this);
            this._app.pushScreen(this._statusScreen);
            return true;
         }

         if (idx == this.SETUP_WIFI_IDX) {
            label464:
            try {
               RibbonLauncher.getInstance().launch("net_rim_wlan_wizard");
            } finally {
               break label464;
            }

            this.close();
            return true;
         } else if (idx == this.SETUP_BLUETOOTH_IDX) {
            label469:
            try {
               RibbonLauncher.getInstance().launch("net_rim_bluetooth?btsetup");
            } finally {
               break label469;
            }

            this.close();
            return true;
         } else if (idx == this.WIRELESS_OPTIONS_IDX) {
            label474:
            try {
               RibbonLauncher.getInstance().launch("net_rim_bb_options_app.Network");
            } finally {
               break label474;
            }

            this.close();
            return true;
         } else if (idx == this.WIFI_OPTIONS_IDX) {
            label479:
            try {
               RibbonLauncher.getInstance().launch("net_rim_bb_options_app?net.rim.device.apps.internal.options.items.WLANOptionsItem");
            } finally {
               break label479;
            }

            this.close();
            return true;
         } else {
            if (idx == this.BLUETOOTH_OPTIONS_IDX) {
               label484:
               try {
                  RibbonLauncher.getInstance().launch("net_rim_bb_options_app?net.rim.device.apps.internal.bluetooth.BluetoothMainScreen");
               } finally {
                  break label484;
               }

               this.close();
               consumed = true;
            }

            return consumed;
         }
      } else {
         boolean wirelessOn = (RadioInternal.getActiveRadios() & ManageConnectionsApp.CELL_RADIOS) != 0;
         boolean wifiOn = WLAN.isRadioOn();
         boolean bluetoothOn = BluetoothME.isPowerOn();
         if (this._globalToggleState != 1) {
            if (this._globalToggleState == 0) {
               int restoreState = 1;
               if (wirelessOn) {
                  restoreState |= 2;
               }

               if (wifiOn) {
                  restoreState |= 4;
               }

               if (bluetoothOn) {
                  restoreState |= 8;
               }

               PersistentInteger.set(_restoreStateHandle, restoreState);
               if (wirelessOn) {
                  RadioInternal.deactivateRadios(ManageConnectionsApp.CELL_RADIOS);
                  this._wirelessToggle.setChecked(false);
               }

               if (wifiOn) {
                  RadioInternal.deactivateRadios(4);
                  this._wifiToggle.setChecked(false);
               }

               if (bluetoothOn) {
                  BluetoothDeviceManager.getInstance().setPowerOn(false);
                  this._bluetoothToggle.setChecked(false);
               }
            } else {
               if (!RadioInternal.activateRadios(ManageConnectionsApp.CELL_RADIOS)) {
                  if (RadioInternal.getGANPreference() == 2) {
                     ManageConnectionsApp.informUser(40);
                  } else {
                     ManageConnectionsApp.informUser(32);
                  }
               } else {
                  this._wirelessToggle.setChecked(true);
               }

               if (this._wifiEnabled) {
                  if (!RadioInternal.activateRadios(4)) {
                     ManageConnectionsApp.informUser(33);
                  } else {
                     this._wifiToggle.setChecked(true);
                  }
               }

               if (this._bluetoothEnabled) {
                  BluetoothDeviceManager.getInstance().setPowerOn(true);
                  this._bluetoothToggle.setChecked(true);
               }
            }
         } else {
            int restoreState = PersistentInteger.get(_restoreStateHandle);
            PersistentInteger.set(_restoreStateHandle, 0);
            if ((restoreState & 2) != 0) {
               if (!RadioInternal.activateRadios(ManageConnectionsApp.CELL_RADIOS)) {
                  if (RadioInternal.getGANPreference() == 2) {
                     ManageConnectionsApp.informUser(40);
                  } else {
                     ManageConnectionsApp.informUser(32);
                  }
               } else {
                  this._wirelessToggle.setChecked(true);
               }
            }

            if ((restoreState & 4) != 0) {
               if (!RadioInternal.activateRadios(4)) {
                  ManageConnectionsApp.informUser(33);
               } else {
                  this._wifiToggle.setChecked(true);
               }
            }

            if (this._bluetoothEnabled && (restoreState & 8) != 0) {
               BluetoothDeviceManager.getInstance().setPowerOn(true);
               this._bluetoothToggle.setChecked(true);
            }
         }

         this.updateGlobalToggle();
         return true;
      }
   }

   @Override
   public final void close() {
      if (this.isDisplayed()) {
         super.close();
         System.exit(0);
      }
   }

   public ConnectionsPopupScreen() {
      super(new VerticalFieldManager(299067162755072L));
      _restoreStateHandle = PersistentInteger.getId(-8312528208360996369L, 0);
      if (WLAN.isWLANAllowed()) {
         this._wifiEnabled = true;
      } else {
         this._wifiEnabled = false;
      }

      this._bluetoothEnabled = BluetoothME.isSupported() && !ITPolicy.getBoolean(34, 1, false);
      IconCollection indicatorIcons = ConnectionIcons.ICONS;
      this._indicatorOff = indicatorIcons.getImage(0);
      this._indicatorNotConnected = indicatorIcons.getImage(1);
      this._indicatorConnected = indicatorIcons.getImage(2);
      this._wirelessIndicator = new ImageField(8589934597L);
      this._wifiIndicator = new ImageField(8589934597L);
      this._bluetoothIndicator = new ImageField(8589934597L);
      this._globalToggleLabel = new LabelField("", 1170935903116328960L);
      this._wirelessToggle = new CheckboxField(this._rbf.getString(2), false, 18014398509481984L);
      this._wirelessStatus = new LabelField("", 64);
      this._wirelessStatus.setId("manage-connections");
      this._wirelessStatus.setTag(STATUS_TAG);
      if (this._wifiEnabled) {
         this._wifiToggle = new CheckboxField(this._rbf.getString(3), false, 18014398509481984L);
         this._wifiToggle.setChecked(WLAN.isRadioOn());
         this._wifiToggle.setChangeListener(this);
         this._wifiStatus = new LabelField("", 64);
         this._wifiStatus.setId("manage-connections");
         this._wifiStatus.setTag(STATUS_TAG);
      }

      this._bluetoothToggle = new CheckboxField(this._rbf.getString(34), false, 18014398509481984L);
      this._bluetoothStatus = new LabelField("", 64);
      this._bluetoothStatus.setId("manage-connections");
      this._bluetoothStatus.setTag(STATUS_TAG);
      boolean wirelessOn = (RadioInternal.getActiveRadios() & ManageConnectionsApp.CELL_RADIOS) != 0;
      boolean bluetoothOn = BluetoothME.isPowerOn();
      this._wirelessToggle.setChecked(wirelessOn);
      this._bluetoothToggle.setChecked(bluetoothOn);
      this._wirelessToggle.setChangeListener(this);
      this._bluetoothToggle.setChangeListener(this);
      this.updateStatus();
      this.updateGlobalToggle();
      this.add(this._globalToggleLabel);
      this.TOGGLE_ALL_IDX = this.getFieldCount() - 1;
      this.add(new SeparatorField());
      MyHorizontalFieldManager hm = new MyHorizontalFieldManager(this._wirelessToggle, this._wirelessStatus, this._wirelessIndicator);
      this.add(hm);
      if (this._wifiEnabled) {
         hm = new MyHorizontalFieldManager(this._wifiToggle, this._wifiStatus, this._wifiIndicator);
         this.add(hm);
      }

      if (this._bluetoothEnabled) {
         hm = new MyHorizontalFieldManager(this._bluetoothToggle, this._bluetoothStatus, this._bluetoothIndicator);
         this.add(hm);
      }

      this.add(new SeparatorField());
      this.add(new LabelField(this._rbf.getString(24), 1170935903116328960L));
      this.SERVICES_STATUS_IDX = this.getFieldCount() - 1;
      if (this._wifiEnabled) {
         this.add(new LabelField(this._rbf.getString(25), 1170935903116328960L));
         this.SETUP_WIFI_IDX = this.getFieldCount() - 1;
      } else {
         this.SETUP_WIFI_IDX = -1;
      }

      if (this._bluetoothEnabled) {
         this.add(new LabelField(this._rbf.getString(37), 1170935903116328960L));
         this.SETUP_BLUETOOTH_IDX = this.getFieldCount() - 1;
      } else {
         this.SETUP_BLUETOOTH_IDX = -1;
      }

      this.add(new SeparatorField());
      this.add(new LabelField(this._rbf.getString(26), 1170935903116328960L));
      this.WIRELESS_OPTIONS_IDX = this.getFieldCount() - 1;
      if (this._wifiEnabled) {
         this.add(new LabelField(this._rbf.getString(27), 1170935903116328960L));
         this.WIFI_OPTIONS_IDX = this.getFieldCount() - 1;
      } else {
         this.WIFI_OPTIONS_IDX = -1;
      }

      if (this._bluetoothEnabled) {
         this.add(new LabelField(this._rbf.getString(28), 1170935903116328960L));
         this.BLUETOOTH_OPTIONS_IDX = this.getFieldCount() - 1;
      } else {
         this.BLUETOOTH_OPTIONS_IDX = -1;
      }

      this._wirelessListener = new MyRadioStatusListener(ManageConnectionsApp.CELL_WAFS, this);
      this._app.addRadioListener(ManageConnectionsApp.CELL_WAFS, this._wirelessListener);
      if (this._wifiEnabled) {
         this._wifiListener = new MyRadioStatusListener(4, this);
         this._app.addRadioListener(4, this._wifiListener);
      }

      if (this._bluetoothEnabled) {
         this._bluetoothListener = new MyBluetoothListener(this);
         BluetoothME.addListener(this._app, this._bluetoothListener);
      }

      ServiceRouting sr = ServiceRouting.getInstance();
      if (sr != null) {
         this._srListener = new MyServiceRoutingListener2(this);
         sr.addListener(this._srListener);
      }

      VoiceServices.addPhoneEventListener(this);
      Runnable cleanupRunnable = new ConnectionsPopupScreen$1(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(cleanupRunnable);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.executeCurrentSelection();
   }

   private final void updateStatus() {
      if ((RadioInternal.getActiveRadios() & ManageConnectionsApp.CELL_RADIOS) != 0) {
         if ((RadioInfo.getNetworkService() & 4) != 0) {
            this._wirelessIndicator.setImage(this._indicatorConnected);
            this._wirelessStatus.setText(this._statusScreen.getData().getProvider());
         } else {
            this._wirelessIndicator.setImage(this._indicatorNotConnected);
            this._wirelessStatus.setText(this._rbf.getString(12));
         }
      } else {
         if (this._wirelessToggle.getChecked() && !RadioInternal.activateRadios(ManageConnectionsApp.getEnabledRadios())) {
            this._wirelessToggle.setChecked(false);
         }

         this._wirelessIndicator.setImage(this._indicatorOff);
         this._wirelessStatus.setText("");
      }

      if (this._wifiEnabled) {
         if ((RadioInternal.getActiveRadios() & 4) != 0) {
            String networkName = this._statusScreen.getData().getWiFiNetworkName();
            if (networkName == null) {
               this._wifiStatus.setText(this._rbf.getString(12));
            } else {
               this._wifiStatus.setText(networkName);
            }

            if (!ServicesStatusData.dataServiceLinkTypeRoutable(3) && (RadioInfo.getNetworkService() & 16384) == 0) {
               this._wifiIndicator.setImage(this._indicatorNotConnected);
            } else {
               this._wifiIndicator.setImage(this._indicatorConnected);
            }
         } else if (this._wifiToggle.getChecked()) {
            RadioInternal.activateRadios(4);
         } else {
            this._wifiIndicator.setImage(this._indicatorOff);
            this._wifiStatus.setText("");
         }
      }

      if (BluetoothME.isPowerOn()) {
         if (!this._bluetoothToggle.getChecked()) {
            BluetoothDeviceManager.getInstance().setPowerOn(false);
         }

         if (BluetoothME.isAnyDeviceConnected()) {
            this._bluetoothIndicator.setImage(this._indicatorConnected);
            this._bluetoothStatus.setText(this._rbf.getString(14));
         } else {
            this._bluetoothIndicator.setImage(this._indicatorNotConnected);
            this._bluetoothStatus.setText(this._rbf.getString(12));
         }
      } else {
         if (this._bluetoothEnabled) {
            this._bluetoothIndicator.setImage(this._indicatorOff);
            this._bluetoothStatus.setText("");
         }
      }
   }

   private final void updateGlobalToggle() {
      if ((PersistentInteger.get(_restoreStateHandle) & 1) != 0) {
         this._globalToggleState = 1;
         this._globalToggleLabel.setText(this._rbf.getString(31));
      } else if (!this._wirelessToggle.getChecked() && (!this._wifiEnabled || !this._wifiToggle.getChecked()) && !this._bluetoothToggle.getChecked()) {
         this._globalToggleState = 2;
         this._globalToggleLabel.setText(this._rbf.getString(19));
      } else {
         this._globalToggleState = 0;
         this._globalToggleLabel.setText(this._rbf.getString(18));
      }
   }
}
