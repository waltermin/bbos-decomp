package net.rim.device.api.bluetooth;

final class BluetoothSerialPort$BluetoothSerialPortCleanupRunnable implements Runnable {
   private final BluetoothSerialPort this$0;

   BluetoothSerialPort$BluetoothSerialPortCleanupRunnable(BluetoothSerialPort _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.closePort();
   }
}
