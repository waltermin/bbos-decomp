package net.rim.device.apps.internal.iota;

final class HttpUserAgent$CloseThread extends Thread {
   private final HttpUserAgent this$0;

   HttpUserAgent$CloseThread(HttpUserAgent _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.closeConnections();
   }
}
