package javax.bluetooth;

final class DiscoveryAgent$ServiceEventHandler extends DiscoveryAgent$EventHandler {
   private final DiscoveryAgent this$0;

   DiscoveryAgent$ServiceEventHandler(DiscoveryAgent _1, DiscoveryListener listener) {
      super(_1, listener);
      this.this$0 = _1;
   }

   @Override
   public final void servicesDiscovered(int transactionID, ServiceRecord[] services) {
      super._listener.servicesDiscovered(transactionID, services);
   }

   @Override
   public final void serviceSearchCompleted(int transactionID, int result) {
      super._listener.serviceSearchCompleted(transactionID, result);
      this.destroy();
   }
}
