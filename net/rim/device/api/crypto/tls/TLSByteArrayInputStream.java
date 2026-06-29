package net.rim.device.api.crypto.tls;

import java.io.ByteArrayInputStream;

public final class TLSByteArrayInputStream extends ByteArrayInputStream {
   public TLSByteArrayInputStream() {
      super(new byte[0]);
   }

   public final void setData(byte[] data) {
      super.buf = data;
      super.pos = 0;
      super.count = super.buf.length;
   }

   public final void setData(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         super.buf = data;
         super.pos = offset;
         super.count = Math.min(offset + length, super.buf.length);
         super.mark = offset;
      } else {
         throw new Object();
      }
   }
}
