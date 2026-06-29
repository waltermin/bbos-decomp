package net.rim.device.api.crypto;

import java.io.InputStream;

public class MACInputStream extends CryptoInputStream {
   protected MAC _mac;
   protected boolean _on = true;

   public MACInputStream(MAC mac, InputStream inputStream) {
      super(inputStream);
      if (mac == null) {
         throw new IllegalArgumentException();
      }

      this._mac = mac;
   }

   @Override
   public String getAlgorithm() {
      return this._mac.getAlgorithm();
   }

   public void on(boolean on) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int read(byte[] data, int offset, int length) throws CryptoIOException {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         try {
            int bytesRead = super._inputStream.read(data, offset, length);
            if (this._on && bytesRead > 0) {
               this._mac.update(data, offset, bytesRead);
            }

            return bytesRead;
         } catch (Throwable var6) {
            throw new CryptoIOException(e);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public MAC getMAC() {
      return this._mac;
   }
}
