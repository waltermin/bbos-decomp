package net.rim.device.api.lbs.gps;

class BluetoothGPSLocationDevice$2 implements Runnable {
   private final BluetoothGPSLocationDevice this$0;

   BluetoothGPSLocationDevice$2(BluetoothGPSLocationDevice this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      System.out.println("attemptReconnect,reconnectThread, status=" + this.this$0._status + " reconnectAttempt=" + this.this$0._reconnectAttempt);
      BluetoothGPSLocationDevice.access$208(this.this$0);
      this.this$0.startReporting();
   }
}
