package javax.bluetooth;

final class DiscoveryAgent$DeviceEventHandler extends DiscoveryAgent$EventHandler {
   private final DiscoveryAgent this$0;

   DiscoveryAgent$DeviceEventHandler(DiscoveryAgent _1, DiscoveryListener listener) {
      super(_1, listener);
      this.this$0 = _1;
   }

   @Override
   public final void inquiryComplete() {
      super._listener.inquiryCompleted(0);
      this.destroy();
   }

   @Override
   public final void inquiryCancelled() {
      super._listener.inquiryCompleted(5);
      this.destroy();
   }

   @Override
   public final void inquiryResult(RemoteDevice remoteDevice, DeviceClass deviceClass) {
      super._listener.deviceDiscovered(remoteDevice, deviceClass);
   }
}
