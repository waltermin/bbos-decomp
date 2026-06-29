package net.rim.device.internal.media;

import java.io.InputStream;
import net.rim.device.api.system.UnsupportedOperationException;

class MediaStreamingManagerImpl$OSInputStream extends InputStream {
   private int _session;
   private final MediaStreamingManagerImpl this$0;

   MediaStreamingManagerImpl$OSInputStream(MediaStreamingManagerImpl _1, int session) {
      this.this$0 = _1;
      this._session = session;
   }

   @Override
   public int read() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      return this.this$0.readBuffer(this._session, buffer, offset, length);
   }
}
