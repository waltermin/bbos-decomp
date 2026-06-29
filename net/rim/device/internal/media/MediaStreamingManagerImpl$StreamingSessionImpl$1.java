package net.rim.device.internal.media;

import net.rim.device.internal.util.RingBuffer;

class MediaStreamingManagerImpl$StreamingSessionImpl$1 extends Thread {
   private final RingBuffer val$_tmpBuffer;
   private final MediaStreamingManagerImpl$StreamingSessionImpl this$1;

   MediaStreamingManagerImpl$StreamingSessionImpl$1(MediaStreamingManagerImpl$StreamingSessionImpl _1, RingBuffer _2) {
      this.this$1 = _1;
      this.val$_tmpBuffer = _2;
   }

   @Override
   public void run() {
      this.val$_tmpBuffer.close();
   }
}
