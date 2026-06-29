package net.rim.device.apps.internal.explorer.player;

final class PlayerApplication$1 implements Runnable {
   private final PlayerApplication this$0;

   PlayerApplication$1(PlayerApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._verbManager.deRegister();
   }
}
