package net.rim.device.api.crypto;

import java.io.IOException;
import java.io.InputStream;

public class BlockDecryptor extends DecryptorInputStream {
   private int _inputBlockLength;
   private int _outputBlockLength;
   private byte[] _currentCiphertext;
   private byte[] _pendingCiphertext;
   private byte[] _plaintext;
   private int _plaintextOffset;
   private int _plaintextMaxOffset;
   private BlockUnformatterEngine _unformatterEngine;
   private boolean _firstRun;
   private IOException _lastException;
   private boolean _closed;

   public BlockDecryptor(BlockUnformatterEngine unformatterEngine, InputStream in) {
      super(in);
      if (unformatterEngine == null) {
         throw new Object();
      }

      this._unformatterEngine = unformatterEngine;
      this._inputBlockLength = unformatterEngine.getInputBlockLength();
      this._outputBlockLength = unformatterEngine.getOutputBlockLength();
      this._currentCiphertext = new byte[this._inputBlockLength];
      this._pendingCiphertext = new byte[this._inputBlockLength];
      this._plaintext = new byte[this._outputBlockLength];
      this._plaintextOffset = 0;
      this._plaintextMaxOffset = 0;
      this._firstRun = true;
   }

   public BlockDecryptor(BlockDecryptorEngine decryptorEngine, InputStream in) {
      this(new BlockDecryptor$DecryptorUnformatterConverter(decryptorEngine), in);
   }

   @Override
   public String getAlgorithm() {
      return this._unformatterEngine.getAlgorithm();
   }

   public int getInputBlockLength() {
      return this._inputBlockLength;
   }

   public int getOutputBlockLength() {
      return this._outputBlockLength;
   }

   private boolean loadCurrentCiphertext() throws BadPaddingException {
      if (this._pendingCiphertext == null) {
         return false;
      }

      byte[] temp = this._currentCiphertext;
      this._currentCiphertext = this._pendingCiphertext;
      this._pendingCiphertext = temp;
      int result = super._inputStream.read(this._pendingCiphertext, 0, this._inputBlockLength);
      if (result != this._inputBlockLength) {
         if (result != -1) {
            throw new BadPaddingException();
         }

         this._pendingCiphertext = null;
      }

      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int read(byte[] buffer, int bufferOffset, int bufferLength) throws IOException {
      if (buffer == null || bufferOffset < 0 || bufferLength < 0 || buffer.length - bufferLength < bufferOffset) {
         throw new Object();
      }

      if (this._closed) {
         throw new Object();
      }

      if (this._lastException != null) {
         throw this._lastException;
      }

      if (bufferLength == 0) {
         return 0;
      }

      int numBytesRead = 0;

      try {
         if (this._firstRun) {
            this._firstRun = false;
            this.loadCurrentCiphertext();
         }

         while (true) {
            if (this._plaintextOffset < this._plaintextMaxOffset) {
               int copyLength = Math.min(bufferLength, this._plaintextMaxOffset - this._plaintextOffset);
               System.arraycopy(this._plaintext, this._plaintextOffset, buffer, bufferOffset, copyLength);
               bufferOffset += copyLength;
               this._plaintextOffset += copyLength;
               bufferLength -= copyLength;
               numBytesRead += copyLength;
            }

            while (bufferLength > this._outputBlockLength) {
               if (!this.loadCurrentCiphertext()) {
                  return numBytesRead == 0 ? -1 : numBytesRead;
               }

               int numBytes = this._unformatterEngine.decryptAndUnformat(this._currentCiphertext, 0, buffer, bufferOffset, this._pendingCiphertext == null);
               bufferOffset += numBytes;
               bufferLength -= numBytes;
               numBytesRead += numBytes;
            }

            if (bufferLength == 0 || !this.loadCurrentCiphertext()) {
               return numBytesRead == 0 ? -1 : numBytesRead;
            }

            this._plaintextOffset = 0;
            this._plaintextMaxOffset = this._unformatterEngine
               .decryptAndUnformat(this._currentCiphertext, 0, this._plaintext, 0, this._pendingCiphertext == null);
         }
      } catch (Throwable var7) {
         this._lastException = new CryptoIOException(e);
         if (numBytesRead == 0) {
            throw this._lastException;
         } else {
            return numBytesRead == 0 ? -1 : numBytesRead;
         }
      }
   }

   @Override
   public int available() {
      return this._closed ? 0 : this._plaintextMaxOffset - this._plaintextOffset + Math.max(0, super._inputStream.available() - this._outputBlockLength);
   }

   @Override
   public void close() {
      this._closed = true;
      super.close();
   }
}
