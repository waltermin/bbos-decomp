package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.media.protocol.SourceStream;

class DataSourceInputStream extends InputStream {
   private SourceStream _source;
   private static byte[] ONE_BYTE = new byte[1];

   public DataSourceInputStream(SourceStream source) {
      this._source = source;
   }

   @Override
   public int read() {
      synchronized (ONE_BYTE) {
         if (this._source.read(ONE_BYTE, 0, 1) == 1) {
            int i = ONE_BYTE[0] & 255;
            ONE_BYTE[0] = 0;
            return i;
         } else {
            return -1;
         }
      }
   }

   @Override
   public int read(byte[] b, int off, int len) {
      return this._source.read(b, off, len);
   }

   @Override
   public long skip(long n) {
      if (n <= 0) {
         return 0;
      }

      if (this._source.getSeekType() == 2) {
         long originalPosition = this._source.tell();
         if (originalPosition >= 0) {
            return this._source.seek(originalPosition + n) - originalPosition;
         }
      }

      return super.skip(n);
   }
}
