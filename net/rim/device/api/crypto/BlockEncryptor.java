package net.rim.device.api.crypto;

import java.io.OutputStream;

public class BlockEncryptor extends EncryptorOutputStream {
   private int _inputBlockLength;
   private int _outputBlockLength;
   private byte[] _inputBuffer;
   private int _inputOffset;
   private byte[] _outputBuffer;
   private BlockFormatterEngine _formatterEngine;
   private boolean _closed;

   public BlockEncryptor(BlockFormatterEngine formatterEngine, OutputStream out) {
      super(out);
      if (formatterEngine == null) {
         throw new Object();
      }

      this._formatterEngine = formatterEngine;
      this._inputBlockLength = formatterEngine.getInputBlockLength();
      this._outputBlockLength = formatterEngine.getOutputBlockLength();
      this._inputBuffer = new byte[this._inputBlockLength];
      this._outputBuffer = new byte[this._outputBlockLength];
      this._inputOffset = 0;
   }

   public BlockEncryptor(BlockEncryptorEngine encryptorEngine, OutputStream out) {
      this(new BlockEncryptor$EncryptorFormatterConverter(encryptorEngine), out);
   }

   @Override
   public String getAlgorithm() {
      return this._formatterEngine.getAlgorithm();
   }

   public int getInputBlockLength() {
      return this._inputBlockLength;
   }

   public int getOutputBlockLength() {
      return this._outputBlockLength;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void write(byte[] data, int offset, int length) {
      if (data == null || offset < 0 || length < 0 || data.length - length < offset) {
         throw new Object();
      }

      if (this._closed) {
         throw new Object();
      }

      try {
         if (this._inputOffset > 0) {
            int copyLength = Math.min(this._inputBlockLength - this._inputOffset, length);
            System.arraycopy(data, offset, this._inputBuffer, this._inputOffset, copyLength);
            this._inputOffset += copyLength;
            offset += copyLength;
            length -= copyLength;
            if (this._inputOffset == this._inputBlockLength) {
               this._formatterEngine.formatAndEncrypt(this._inputBuffer, 0, this._inputBlockLength, this._outputBuffer, 0);
               super._out.write(this._outputBuffer, 0, this._outputBlockLength);
               this._inputOffset = 0;
            }
         }

         while (length >= this._inputBlockLength) {
            this._formatterEngine.formatAndEncrypt(data, offset, this._inputBlockLength, this._outputBuffer, 0);
            super._out.write(this._outputBuffer, 0, this._outputBlockLength);
            offset += this._inputBlockLength;
            length -= this._inputBlockLength;
         }

         if (length > 0) {
            System.arraycopy(data, offset, this._inputBuffer, 0, length);
            this._inputOffset = length;
         }
      } catch (Throwable var6) {
         throw new CryptoIOException(e);
      }
   }

   @Override
   public void flush() {
      if (this._closed) {
         throw new Object();
      }

      super._out.flush();
   }

   @Override
   public void flush(boolean pad) {
      if (this._closed) {
         throw new Object();
      }

      if (pad) {
         this.pad();
      }

      this.flush();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void pad() {
      try {
         if (this._formatterEngine instanceof BlockEncryptor$EncryptorFormatterConverter
            && this._inputOffset != 0
            && this._inputOffset != this._inputBlockLength) {
            throw new BadPaddingException();
         }

         int length = this._formatterEngine.formatAndEncrypt(this._inputBuffer, 0, this._inputOffset, this._outputBuffer, 0, true);
         super._out.write(this._outputBuffer, 0, length);
         this._inputOffset = 0;
      } catch (Throwable var3) {
         throw new CryptoIOException(e);
      }
   }

   @Override
   public void close() {
      this.pad();
      this._closed = true;
      super._out.close();
   }
}
