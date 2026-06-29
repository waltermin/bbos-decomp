package net.rim.device.api.crypto.pgp;

import java.io.InputStream;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.SharedInputStream;

public final class PGPCompressedInputStream extends PGPInputStream {
   private int _compressionAlgorithm;
   private SharedInputStream _compressedStream;
   private boolean _displayUI;

   PGPCompressedInputStream(InputStream input) {
      this(input, null, true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   PGPCompressedInputStream(InputStream input, KeyStore keyStore, boolean displayUI) throws PGPEncodingException {
      super(input, keyStore);

      try {
         this._compressionAlgorithm = input.read();
      } catch (Throwable var6) {
         throw new PGPEncodingException(e.toString());
      }

      this._displayUI = displayUI;
      switch (this._compressionAlgorithm) {
         case -1:
            throw new Object(((StringBuffer)(new Object("Comp:"))).append(this._compressionAlgorithm).toString());
         case 0:
         default:
            this._compressedStream = SharedInputStream.getSharedInputStream(input);
            break;
         case 1:
            this._compressedStream = SharedInputStream.getSharedInputStream((InputStream)(new Object(input, true)));
            break;
         case 2:
            this._compressedStream = SharedInputStream.getSharedInputStream((InputStream)(new Object(input, false)));
      }

      this.getNextStream();
   }

   @Override
   public final PGPInputStream getNextStream() {
      try {
         super._input = PGPInputStream.getPGPInputStream(this._compressedStream, super._keyStore, this._displayUI);
         return (PGPInputStream)super._input;
      } catch (PGPException var4) {
         return null;
      } finally {
         ;
      }
   }

   public final int getAlgorithm() {
      return this._compressionAlgorithm;
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (super._input == null) {
         return -1;
      } else if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         return super._input.read(buffer, offset, length);
      } else {
         throw new Object();
      }
   }

   @Override
   public final int available() {
      return super._input == null ? 0 : super._input.available();
   }

   @Override
   public final long skip(long n) {
      return super._input == null ? 0 : super._input.skip(n);
   }

   @Override
   public final void close() {
      if (super._input != null) {
         super._input.close();
      }
   }

   @Override
   public final String getType() {
      return "Compressed";
   }
}
