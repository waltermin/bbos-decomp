package net.rim.device.api.crypto;

import java.io.InputStream;

public class DecryptorInputStream extends CryptoInputStream {
   protected DecryptorInputStream(InputStream input) {
      super(input);
   }
}
