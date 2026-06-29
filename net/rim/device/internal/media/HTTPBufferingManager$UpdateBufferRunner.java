package net.rim.device.internal.media;

class HTTPBufferingManager$UpdateBufferRunner implements Runnable {
   private final HTTPBufferingManager this$0;

   HTTPBufferingManager$UpdateBufferRunner(HTTPBufferingManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (!this.this$0._shutdown && this.this$0._totalBytesBuffered > 0) {
         this.this$0.notifyCallbackBufferStatus(this.this$0._callback, this.this$0._totalBytesBuffered, this.this$0._totalInputLength);
      }
   }
}
