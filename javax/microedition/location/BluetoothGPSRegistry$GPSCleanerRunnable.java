package javax.microedition.location;

final class BluetoothGPSRegistry$GPSCleanerRunnable implements Runnable {
   int _processId;
   private final BluetoothGPSRegistry this$0;

   public BluetoothGPSRegistry$GPSCleanerRunnable(BluetoothGPSRegistry _1, int processId) {
      this.this$0 = _1;
      this._processId = processId;
   }

   @Override
   public final void run() {
      this.this$0.removeGPSConsumer(this._processId);
   }
}
