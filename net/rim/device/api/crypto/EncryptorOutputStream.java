package net.rim.device.api.crypto;

import java.io.OutputStream;

public class EncryptorOutputStream extends CryptoOutputStream {
   public EncryptorOutputStream(OutputStream output) {
      super(output);
   }

   public void flush(boolean pad) {
      super.flush();
   }
}
