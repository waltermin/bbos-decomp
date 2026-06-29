package net.rim.wica.common.debug.io;

import java.io.ByteArrayInputStream;

public final class ByteArrayInputByteStream extends AbstractInputByteStreamAdapter {
   ByteArrayInputStream _byteArrayStream;

   public ByteArrayInputByteStream(byte[] bytes) {
      this._byteArrayStream = new ByteArrayInputStream(bytes);
      this.init(this._byteArrayStream);
   }
}
