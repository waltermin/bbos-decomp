package net.rim.device.api.crypto;

import java.io.InputStream;

public class StreamDecryptor extends DecryptorInputStream {
   public StreamDecryptor(InputStream input) {
      super(input);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int read(byte[] data, int offset, int length) throws CryptoIOException {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         try {
            int bytesRead = super._inputStream.read(data, offset, length);
            if (bytesRead > 0) {
               this.decrypt(data, offset, bytesRead);
            }

            return bytesRead;
         } catch (Throwable var6) {
            throw new CryptoIOException(e);
         }
      } else {
         throw new Object();
      }
   }

   protected void decrypt(byte[] _1, int _2, int _3) {
      throw null;
   }
}
