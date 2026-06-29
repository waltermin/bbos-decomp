package net.rim.device.internal.media;

class StreamingMediaPlayer$1 implements Runnable {
   private final StreamingMediaPlayer this$0;

   StreamingMediaPlayer$1(StreamingMediaPlayer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      boolean playAgain = this.this$0._loopCount == -1;
      if (!playAgain && this.this$0._currentLoopIteration < this.this$0._loopCount - 1) {
         this.this$0._currentLoopIteration++;
         playAgain = true;
      }

      if (playAgain) {
         try {
            this.this$0.start();
         } finally {
            return;
         }
      } else {
         this.this$0._currentLoopIteration = 0;
      }
   }
}
