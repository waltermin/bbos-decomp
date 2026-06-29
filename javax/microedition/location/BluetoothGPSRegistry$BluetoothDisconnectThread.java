package javax.microedition.location;

final class BluetoothGPSRegistry$BluetoothDisconnectThread extends Thread {
   private final BluetoothGPSRegistry this$0;

   BluetoothGPSRegistry$BluetoothDisconnectThread(BluetoothGPSRegistry _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._port != null) {
         this.this$0._port.disconnect();
         this.this$0._state = 2;
      }
   }
}
