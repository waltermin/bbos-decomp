package net.rim.device.api.crypto;

import java.io.InputStream;

public final class NullDecryptor extends DecryptorInputStream {
   public NullDecryptor(InputStream input) {
      super(input);
   }

   @Override
   public final String getAlgorithm() {
      return "Null";
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      return super._inputStream.read(data, offset, length);
   }
}
