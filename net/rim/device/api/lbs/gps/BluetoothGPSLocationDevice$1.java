package net.rim.device.api.lbs.gps;

import net.rim.device.api.system.EventLogger;

class BluetoothGPSLocationDevice$1 implements Runnable {
   private final BluetoothGPSLocationDevice$BluetoothDataThread this$1;

   BluetoothGPSLocationDevice$1(BluetoothGPSLocationDevice$BluetoothDataThread this$1) {
      this.this$1 = this$1;
   }

   @Override
   public void run() {
      if (this.this$1.this$0._status != 1) {
         this.this$1.this$0._status = 10;
         GPSProvider.getInstance().fireLocationDeviceEvent(this.this$1.this$0, this.this$1.this$0.getString(6));
         EventLogger.logEvent(
            4560142210062134028L,
            ((StringBuffer)(new Object()))
               .append(this.this$1.this$0._name)
               .append(" not GPS device. Force disconnect. Status: ")
               .append(this.this$1.this$0._status)
               .toString()
               .getBytes(),
            5
         );
         this.this$1.this$0.stopReporting();
      }
   }
}
