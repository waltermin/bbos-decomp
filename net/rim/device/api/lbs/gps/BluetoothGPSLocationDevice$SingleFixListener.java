package net.rim.device.api.lbs.gps;

class BluetoothGPSLocationDevice$SingleFixListener implements GPSProvider$Listener, Runnable {
   private GPSLocationData _gpsData;
   private boolean _deviceReportedBack;
   private final BluetoothGPSLocationDevice this$0;

   public void stop() {
      GPSProvider provider = GPSProvider.getInstance();
      provider.stopReportingInternal(this.this$0, false);
      provider.removeLocationListener(this);
   }

   public boolean isStopped() {
      return this._deviceReportedBack;
   }

   @Override
   public void run() {
      GPSProvider provider = GPSProvider.getInstance();
      provider.addLocationListener(this, this._gpsData);
      provider.startReporting(this.this$0);
   }

   @Override
   public void deviceStateChanged(GPSDevice device, String userMessage) {
      switch (this.this$0._status) {
         case 4:
         case 10:
            this._deviceReportedBack = true;
            this.stop();
      }
   }

   @Override
   public void locationUpdated() {
      this._deviceReportedBack = this.this$0._status == 1 && this._gpsData._isValid;
      if (this._deviceReportedBack) {
         this.stop();
         this._gpsData._isValid = true;
      }
   }

   BluetoothGPSLocationDevice$SingleFixListener(BluetoothGPSLocationDevice this$0, GPSLocationData data) {
      this.this$0 = this$0;
      this._gpsData = data;
   }
}
