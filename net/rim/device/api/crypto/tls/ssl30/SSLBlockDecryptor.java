package net.rim.device.api.crypto.tls.ssl30;

import java.io.InputStream;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.CryptoIOException;
import net.rim.device.api.crypto.DecryptorInputStream;
import net.rim.device.api.crypto.tls.TLSBlockUnformatterEngine;
import net.rim.device.api.util.DataBuffer;

final class SSLBlockDecryptor extends DecryptorInputStream {
   private TLSBlockUnformatterEngine _unformatter;
   private DataBuffer _inBuffer;
   private static final int BUFFER = 256;

   public SSLBlockDecryptor(BlockDecryptorEngine engine, InputStream input, boolean paddingVerification) {
      super(input);
      this._unformatter = new TLSBlockUnformatterEngine(engine, paddingVerification);
      this._inBuffer = new DataBuffer();
   }

   @Override
   public final int available() {
      return this._inBuffer != null ? this._inBuffer.available() : 0;
   }

   @Override
   public final String getAlgorithm() {
      return this._unformatter.getAlgorithm();
   }

   @Override
   public final InputStream getInputStream() {
      throw new RuntimeException();
   }

   @Override
   public final boolean markSupported() {
      return false;
   }

   @Override
   public final int read() {
      throw new RuntimeException();
   }

   @Override
   public final int read(byte[] data) {
      return this.read(data, 0, data != null ? data.length : 0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final int read(byte[] data, int offset, int length) throws CryptoIOException {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         try {
            byte[] newData = new byte[length];
            int bytesRead = super._inputStream.read(newData);
            if (bytesRead > -1) {
               DataBuffer inBuffer = new DataBuffer(newData, 0, bytesRead, true);
               DataBuffer outBuffer = new DataBuffer(bytesRead, true);
               int decryptedBytes = this._unformatter.decryptAndUnformat(inBuffer, outBuffer);
               System.arraycopy(outBuffer.getArray(), 0, data, offset, decryptedBytes);
               return decryptedBytes;
            } else {
               return -1;
            }
         } catch (Throwable var10) {
            throw new CryptoIOException(e);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
