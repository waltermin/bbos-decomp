package net.rim.device.api.lbs.gps;

class BluetoothGPSLocationDevice$3 implements Runnable {
   private final BluetoothGPSLocationDevice this$0;

   BluetoothGPSLocationDevice$3(BluetoothGPSLocationDevice this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      String PSRF100 = "$PSRF100,1,4800,8,1,0*";
      PSRF100 = ((StringBuffer)(new Object())).append(PSRF100).append(this.this$0.getChecksum(PSRF100)).toString();
      this.this$0.messageDevice(PSRF100.getBytes());

      for (int i = 0; i < BluetoothGPSLocationDevice.NMEAdiscards.length; i++) {
         String PSRF103 = ((StringBuffer)(new Object("$PSRF103,"))).append(BluetoothGPSLocationDevice.NMEAdiscards[i]).append(",255,00,01*").toString();
         PSRF103 = ((StringBuffer)(new Object())).append(PSRF103).append(this.this$0.getChecksum(PSRF103)).toString();
         this.this$0.messageDevice(PSRF103.getBytes());
      }

      for (int i = 0; i < BluetoothGPSLocationDevice.NMEAwanted.length; i++) {
         String PSRF103 = ((StringBuffer)(new Object("$PSRF103,"))).append(BluetoothGPSLocationDevice.NMEAwanted[i]).append(",00,01,01*").toString();
         PSRF103 = ((StringBuffer)(new Object())).append(PSRF103).append(this.this$0.getChecksum(PSRF103)).toString();
         this.this$0.messageDevice(PSRF103.getBytes());
      }
   }
}
