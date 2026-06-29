package net.rim.device.internal.media;

class StreamingPlayer$1 implements Runnable {
   private final StreamingPlayer this$0;

   StreamingPlayer$1(StreamingPlayer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.doStart();
   }
}
