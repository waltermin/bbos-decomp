package net.rim.device.apps.internal.ribbon.skin.svg;

class MediaLayout$1 implements Runnable {
   private final MediaLayout this$0;

   MediaLayout$1(MediaLayout _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      try {
         if (this.this$0._state == 1) {
            this.this$0._state = 2;
            this.this$0._player.start();
            return;
         }
      } finally {
         return;
      }
   }
}
