package net.rim.device.internal.media;

class StreamingMediaPlayer$2 implements Runnable {
   private final StreamingMediaPlayer this$0;

   StreamingMediaPlayer$2(StreamingMediaPlayer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      try {
         this.this$0.start();
      } finally {
         return;
      }
   }
}
