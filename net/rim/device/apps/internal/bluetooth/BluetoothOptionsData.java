package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.vm.Persistable;

final class BluetoothOptionsData implements Persistable {
   public boolean _powerOn;
   public String _localName;
   public boolean _discoverable;
   public Vector _pairedDeviceData = new Vector();
   public int _allowOutgoingCalls = 0;
   public boolean _ledIndicatorEnabled;
   public int _addressBookTransferMode;
   public CategoriesModel _addressBookCategories;
   public int _securityMode;
   public byte[] _lastConnectedDevice;
   public boolean _connectOnPowerUpEnabled;

   BluetoothOptionsData() {
      this._localName = "BlackBerry " + DeviceInfo.getDeviceName();
      this._ledIndicatorEnabled = true;
      this._addressBookTransferMode = 1;
      this._addressBookCategories = new CategoriesModel();
      this._securityMode = 0;
      this._connectOnPowerUpEnabled = true;
      if (DeviceInfo.isSimulator()) {
         this._localName = this._localName + " Simulator";
      }
   }

   final byte[] getLocalNameAsBytes() {
      try {
         return this._localName.getBytes("UTF8");
      } finally {
         ;
      }
   }
}
