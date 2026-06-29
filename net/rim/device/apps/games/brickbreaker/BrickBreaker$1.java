package net.rim.device.apps.games.brickbreaker;

final class BrickBreaker$1 implements Runnable {
   private final BrickBreaker this$0;

   BrickBreaker$1(BrickBreaker _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._game = new Game();
      this.this$0._menuScreen.setGame(this.this$0._game);
   }
}
