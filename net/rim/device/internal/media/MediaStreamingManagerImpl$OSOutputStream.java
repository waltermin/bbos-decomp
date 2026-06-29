package net.rim.device.internal.media;

import net.rim.device.internal.util.RingBuffer$UnassertiveOutputStream;

class MediaStreamingManagerImpl$OSOutputStream extends RingBuffer$UnassertiveOutputStream {
   private int _session;
   private final MediaStreamingManagerImpl this$0;

   MediaStreamingManagerImpl$OSOutputStream(MediaStreamingManagerImpl _1, int session) {
      this.this$0 = _1;
      this._session = session;
   }

   @Override
   public int write(byte[] buffer, int offset, int length) {
      return this.this$0.writeBuffer(this._session, buffer, offset, length);
   }
}
