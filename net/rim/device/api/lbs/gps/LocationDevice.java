package net.rim.device.api.lbs.gps;

import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.gps.GPS;
import net.rim.device.apps.internal.bluetooth.BluetoothDevice;

public class LocationDevice extends GPSDevice {
   private boolean _internal;

   public LocationDevice(boolean internal, Object activationParam) {
      super(activationParam);
      this._internal = internal;
      if (this._internal) {
         super._friendlyName = this.getString(0);
         super._name = GPS.GPS_SOURCE_DEVICE;
         super._deviceID = super._name;
      } else if (!(activationParam instanceof Object)) {
         if (activationParam instanceof Object) {
            BluetoothDevice device = (BluetoothDevice)activationParam;
            super._name = device.getName();
            super._friendlyName = device.getFriendlyName();
            super._deviceID = super._name;
         }
      } else {
         BluetoothSerialPortInfo port = (BluetoothSerialPortInfo)activationParam;
         super._name = port.getDeviceName();
         super._deviceID = port.getDeviceAddress();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Object) {
         return this._internal ? obj.equals(super._name) : obj.equals(this.getDeviceID());
      } else {
         return obj instanceof LocationDevice ? this.equals(((LocationDevice)obj).getName()) : super.equals(obj);
      }
   }

   @Override
   public boolean messageDevice(Object contextMessage) {
      return false;
   }

   @Override
   public void setDeviceToUse() {
      LocationWorker.getInstance().setLocationDevice(this);
   }

   @Override
   protected boolean startReporting() {
      this.setDeviceToUse();
      return LocationWorker.getInstance().startReporting();
   }

   @Override
   protected boolean stopReporting() {
      boolean success = LocationWorker.getInstance().stopReporting();
      LocationWorker.getInstance().setLocationDevice(null);
      return success;
   }

   @Override
   protected GPSLocationData singleFix() {
      LocationWorker.getInstance().setLocationDevice(this);
      return LocationWorker.getInstance().singleFix();
   }

   @Override
   public boolean isInternalGPS() {
      return this._internal;
   }
}
