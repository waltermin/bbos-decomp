package net.rim.device.apps.internal.manageconnections;

final class ConnectionsPopupScreen$2 implements Runnable {
   private final ConnectionsPopupScreen this$0;

   ConnectionsPopupScreen$2(ConnectionsPopupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.close();
   }
}
