package net.rim.device.api.crypto;

import java.io.OutputStream;

public class StreamEncryptor extends EncryptorOutputStream {
   private byte[] _buffer = new byte[128];
   private static final int BUFFER_LENGTH = 128;

   protected StreamEncryptor(OutputStream output) {
      super(output);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void write(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         try {
            while (length > 0) {
               int len = Math.min(length, 128);
               this.encrypt(data, offset, len, this._buffer);
               super._out.write(this._buffer, 0, len);
               length -= len;
               offset += len;
            }
         } catch (Throwable var6) {
            throw new CryptoIOException(e);
         }
      } else {
         throw new Object();
      }
   }

   protected void encrypt(byte[] _1, int _2, int _3, byte[] _4) {
      throw null;
   }

   @Override
   public void flush(boolean pad) {
      super.flush();
   }
}
