package net.rim.wica.common.debug.io;

import java.io.ByteArrayOutputStream;

public final class ByteArrayOutputByteStream extends AbstractOutputByteStreamAdapter {
   ByteArrayOutputStream _byteArrayStream = (ByteArrayOutputStream)(new Object());

   public ByteArrayOutputByteStream() {
      this.init(this._byteArrayStream);
   }

   public final byte[] getByteArray() {
      return this._byteArrayStream.toByteArray();
   }
}
