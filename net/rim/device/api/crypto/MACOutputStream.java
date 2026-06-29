package net.rim.device.api.crypto;

import java.io.OutputStream;

public class MACOutputStream extends CryptoOutputStream {
   protected MAC _mac;
   protected boolean _on = true;

   public MACOutputStream(MAC mac, OutputStream out) {
      super(out == null ? new CryptoDummyOutputStream() : out);
      if (mac == null) {
         throw new Object();
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
   public void write(byte[] buffer, int offset, int length) throws CryptoIOException {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         try {
            if (this._on) {
               this._mac.update(buffer, offset, length);
            }

            super._out.write(buffer, offset, length);
         } catch (Throwable var6) {
            throw new CryptoIOException(e);
         }
      } else {
         throw new Object();
      }
   }

   public MAC getMAC() {
      return this._mac;
   }
}
