package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.i18n.CommonResource;

public final class GPSDeviceSelectionDialog extends PopupScreen implements FieldChangeListener, ListFieldCallback {
   GPSDevice[] _devices;
   boolean _result;
   int _selectedIndex = -1;
   VerticalFieldManager _buttonManager;
   ButtonField _cancelButton;
   ButtonField _bluetoothConfigButton;
   ListField _list;
   private boolean _bluetoothInformed;

   public GPSDeviceSelectionDialog() {
      super((Manager)(new Object()));
      this.createDialog();
   }

   private final void createDialog() {
      LabelField dialogTitle = (LabelField)(new Object(LBSResources.getString(57)));
      LabelField deviceSelectionTitle = (LabelField)(new Object(LBSResources.getString(58)));
      this._list = (ListField)(new Object());
      this._list.setCallback(this);
      this._cancelButton = (ButtonField)(new Object(CommonResource.getString(10005), 12884901888L));
      this._cancelButton.setChangeListener(this);
      this._bluetoothConfigButton = (ButtonField)(new Object(LBSResources.getString(101), 12884901888L));
      this._buttonManager = (VerticalFieldManager)(new Object(12884901888L));
      if (BluetoothME.isSupported()) {
         this._buttonManager.add(this._bluetoothConfigButton);
         this._bluetoothConfigButton.setChangeListener(this);
      }

      this._buttonManager.add(this._cancelButton);
      this.add(dialogTitle);
      this.add((Field)(new Object()));
      this.add(deviceSelectionTitle);
      this.add(this._list);
      this.add((Field)(new Object()));
      this.add(this._buttonManager);
   }

   private final void close(boolean result) {
      this._result = result;
      this._selectedIndex = this._list.getSelectedIndex();
      this.close();
   }

   private final int getInitialSelectedIndex() {
      GPSDevice device = GPSProvider.getInstance().getDeviceInUse();
      if (device == null || device.equals(GPSDevice.NO_DEVICE)) {
         String deviceID = LBSOptions.getString(6531936621597631078L, null);
         if (deviceID != null) {
            for (int i = 0; i < this._devices.length; i++) {
               if (this._devices[i].equals(deviceID)) {
                  return i;
               }
            }
         }
      }

      if (device == null || device.equals(GPSDevice.NO_DEVICE)) {
         for (int i = 0; i < this._devices.length; i++) {
            if (this._devices[i].toString().indexOf("GPS") > -1) {
               device = this._devices[i];
               break;
            }
         }
      }

      if (device != null) {
         int index = Arrays.getIndex(this._devices, device);
         return index >= 0 ? index : 0;
      } else {
         return 0;
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._devices = GPSProvider.getInstance().getLocationDevices(false);
         this._list.setSize(this._devices.length);
         this._list.setSelectedIndex(this.getInitialSelectedIndex());
         if (this._devices.length == 0) {
            if (DeviceInfo.isSimulator()) {
               Dialog.inform("No SimulatorGPSDevice, please restart with net_rim_bb_lbs_simulator_gps.cod");
               this.close(false);
               return;
            }

            if (this._buttonManager.getManager() != null && !this._bluetoothInformed) {
               this._bluetoothInformed = true;
               Dialog.inform(LBSResources.getString(54));
               this._buttonManager.setFocus();
               return;
            }

            this._cancelButton.setFocus();
         }
      }
   }

   @Override
   public final void actionPerformed(int action, Object parameter) {
      this.onDisplay();
   }

   public final boolean doModal() {
      this._bluetoothInformed = false;
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   public final GPSDevice getSelectedDevice() {
      return (GPSDevice)this.get(null, this._selectedIndex);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index >= 0 && index < this._devices.length) {
         String name = this._devices[index].toString();
         graphics.drawText(name, 0, y);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return index >= 0 && index < this._devices.length ? this._devices[index] : null;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(false);
         return true;
      }

      if (key == '\n') {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._list && this._list.getSize() > 0) {
            this.close(true);
            return true;
         } else {
            return super.keyChar(key, status, time);
         }
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._cancelButton) {
         this.close(false);
         return true;
      } else if (field == this._list) {
         this.close(true);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._cancelButton) {
         this.close(false);
      } else if (field == this._bluetoothConfigButton) {
         try {
            RibbonLauncher.getInstance().launch("net_rim_bb_options_app.BluetoothConfig");
         } finally {
            return;
         }
      }
   }
}
