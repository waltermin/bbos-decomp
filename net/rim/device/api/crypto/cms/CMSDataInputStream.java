package net.rim.device.api.crypto.cms;

import java.io.InputStream;

public final class CMSDataInputStream extends CMSInputStream {
   CMSDataInputStream(InputStream inputStream) {
      super(inputStream);
   }

   @Override
   public final boolean isSigned() {
      return !(super._input instanceof CMSInputStream) ? false : ((CMSInputStream)super._input).isSigned();
   }

   @Override
   public final boolean isEncrypted() {
      return !(super._input instanceof CMSInputStream) ? false : ((CMSInputStream)super._input).isEncrypted();
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         return super._input.read(buffer, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int available() {
      return super._input.available();
   }

   @Override
   public final long skip(long n) {
      return super._input.skip(n);
   }

   @Override
   public final void setData(InputStream data) {
   }

   @Override
   public final boolean isContentComplete() {
      return true;
   }
}
