package net.rim.device.apps.internal.options.items.network;

final class PrefNetworkListOptions$StatusScreen$1 implements Runnable {
   private final PrefNetworkListOptions$StatusScreen this$0;

   PrefNetworkListOptions$StatusScreen$1(PrefNetworkListOptions$StatusScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._timeoutExpired = true;
      this.this$0.close();
   }
}
