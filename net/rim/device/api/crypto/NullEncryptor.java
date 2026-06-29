package net.rim.device.api.crypto;

import java.io.OutputStream;

public final class NullEncryptor extends EncryptorOutputStream {
   public NullEncryptor(OutputStream output) {
      super(output);
   }

   @Override
   public final String getAlgorithm() {
      return "Null";
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      super._out.write(data, offset, length);
   }
}
