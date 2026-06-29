package net.rim.device.internal.media;

class PlayerImpl$2 implements Runnable {
   private final PlayerImpl this$0;

   PlayerImpl$2(PlayerImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.kill();
   }
}
