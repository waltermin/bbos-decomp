package net.rim.device.api.lbs.gps;

class BluetoothGPSLocationDevice$2 implements Runnable {
   private final BluetoothGPSLocationDevice this$0;

   BluetoothGPSLocationDevice$2(BluetoothGPSLocationDevice this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      System.out
         .println(
            ((StringBuffer)(new Object("attemptReconnect,reconnectThread, status=")))
               .append(this.this$0._status)
               .append(" reconnectAttempt=")
               .append(this.this$0._reconnectAttempt)
               .toString()
         );
      BluetoothGPSLocationDevice.access$208(this.this$0);
      this.this$0.startReporting();
   }
}
