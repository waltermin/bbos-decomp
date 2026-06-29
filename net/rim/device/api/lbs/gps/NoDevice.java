package net.rim.device.api.lbs.gps;

import net.rim.device.api.gps.GPS;

final class NoDevice extends GPSDevice {
   NoDevice(Object param) {
      super(param);
      super._name = this.getString(13);
      super._deviceID = "None";
   }

   @Override
   public final void setDeviceToUse() {
      GPS.setLAPIDataSource(super._name);
   }

   @Override
   protected final GPSLocationData singleFix() {
      return null;
   }

   @Override
   public final boolean isInternalGPS() {
      return false;
   }

   @Override
   public final boolean messageDevice(Object contextMessage) {
      return false;
   }

   @Override
   protected final boolean startReporting() {
      return false;
   }

   @Override
   protected final boolean stopReporting() {
      return false;
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof NoDevice) {
         return true;
      } else {
         return obj instanceof Object ? super._deviceID.equals(obj) : super.equals(obj);
      }
   }
}
