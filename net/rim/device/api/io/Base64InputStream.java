package net.rim.device.api.io;

import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class Base64InputStream extends InputStream {
   private byte[] _inputBuffer;
   private int _inputBufferLength;
   private byte[] _outputBuffer;
   private int _outputBufferLength;
   private int _outputBufferOffset;
   private InputStream _inputStream;
   private IOException _lastException;
   private boolean _treatErrorAsEOF;
   private boolean _exceptionThrown;
   private static final int INPUT_BUFFER_LENGTH;
   private static final int OUTPUT_BUFFER_LENGTH;

   public Base64InputStream(InputStream inputStream) {
      this(inputStream, false);
   }

   public Base64InputStream(InputStream inputStream, boolean treatErrorAsEOF) {
      if (inputStream == null) {
         throw new IllegalArgumentException();
      }

      this._treatErrorAsEOF = treatErrorAsEOF;
      this._exceptionThrown = false;
      this._inputBuffer = new byte[2048];
      this._inputBufferLength = 0;
      this._outputBuffer = new byte[1536];
      this._outputBufferOffset = 0;
      this._outputBufferLength = 0;
      this._inputStream = inputStream;
      this._lastException = null;
   }

   @Override
   public int read() {
      label33: {
         if (this._outputBufferLength != 0) {
            this._outputBufferLength--;
            return this._outputBuffer[this._outputBufferOffset++] & 0xFF;
         }

         if (this._lastException != null) {
            throw this._lastException;
         }

         try {
            if (this._inputStream == null) {
               throw new IOException("Stream closed");
            }

            this._outputBufferLength = this.decode(this._outputBuffer, 0);
            if (this._outputBufferLength == 0) {
               return -1;
            }

            this._outputBufferOffset = 0;
            break label33;
         } catch (IOException e) {
            this._lastException = e;
            throw e;
         }
      }
   }

   @Override
   public int read(byte[] buffer) {
      return this.read(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public int read(byte[] buffer, int bufferOffset, int bufferLength) {
      if (buffer == null || bufferOffset < 0 || bufferLength < 0 || buffer.length - bufferLength < bufferOffset) {
         throw new IllegalArgumentException();
      }

      if (this._lastException != null) {
         throw this._lastException;
      }

      try {
         if (this._inputStream == null) {
            throw new IOException("Stream closed");
         }

         if (bufferLength == 0) {
            return 0;
         }

         int bufferOffsetBase = bufferOffset;
         if (this._outputBufferLength > 0) {
            int copyLength = bufferLength < this._outputBufferLength ? bufferLength : this._outputBufferLength;
            System.arraycopy(this._outputBuffer, this._outputBufferOffset, buffer, bufferOffset, copyLength);
            bufferOffset += copyLength;
            bufferLength -= copyLength;
            this._outputBufferOffset += copyLength;
            this._outputBufferLength -= copyLength;
            if (bufferLength == 0) {
               return bufferOffset - bufferOffsetBase;
            }
         }

         if (bufferLength >= 1536) {
            do {
               int decodeLength = this.decode(buffer, bufferOffset);
               if (decodeLength == 0) {
                  return bufferOffset == bufferOffsetBase ? -1 : bufferOffset - bufferOffsetBase;
               }

               bufferOffset += decodeLength;
               bufferLength -= decodeLength;
            } while (bufferLength >= 1536);

            if (bufferLength == 0) {
               return bufferOffset - bufferOffsetBase;
            }
         }

         this._outputBufferLength = this.decode(this._outputBuffer, 0);
         if (this._outputBufferLength == 0) {
            return bufferOffset == bufferOffsetBase ? -1 : bufferOffset - bufferOffsetBase;
         }

         int copyLength = bufferLength < this._outputBufferLength ? bufferLength : this._outputBufferLength;
         System.arraycopy(this._outputBuffer, 0, buffer, bufferOffset, copyLength);
         bufferOffset += copyLength;
         this._outputBufferOffset = copyLength;
         this._outputBufferLength -= copyLength;
         return bufferOffset - bufferOffsetBase;
      } catch (IOException e) {
         this._lastException = e;
         throw e;
      }
   }

   @Override
   public int available() {
      if (this._outputBufferLength == 0) {
         if (this._lastException != null) {
            throw this._lastException;
         }

         if (this._inputStream == null) {
            this._lastException = new IOException("Stream closed");
            throw this._lastException;
         }
      }

      return this._outputBufferLength;
   }

   @Override
   public void close() {
      if (this._lastException != null) {
         throw this._lastException;
      }

      try {
         if (this._inputStream != null) {
            this._inputStream.close();
            this._inputBuffer = null;
            this._inputBufferLength = 0;
            this._outputBuffer = null;
            this._outputBufferOffset = 0;
            this._outputBufferLength = 0;
            this._inputStream = null;
         }
      } catch (IOException e) {
         this._lastException = e;
         throw e;
      }
   }

   private int decode(byte[] buffer, int bufferOffset) {
      if (this._inputBuffer == null) {
         return 0;
      }

      if (this._exceptionThrown) {
         throw new IOException("Base64: decoding error");
      }

      int bufferLength;
      do {
         int inputBufferLength = this._inputStream.read(this._inputBuffer, this._inputBufferLength, 2048 - this._inputBufferLength);
         if (inputBufferLength < 0) {
            inputBufferLength = 0;
         }

         inputBufferLength += this._inputBufferLength;
         bufferLength = decode(this._inputBuffer, inputBufferLength, buffer, bufferOffset);
         this._inputBufferLength = bufferLength >> 16;
         bufferLength &= 65535;
         if (inputBufferLength < 2048) {
            if (this._inputBufferLength > 0) {
               if (!this._treatErrorAsEOF) {
                  throw new IOException("Base64: decoding error");
               }

               this._exceptionThrown = true;
            }

            this._inputBuffer = null;
            return bufferLength;
         }
      } while (bufferLength <= 0);

      if (bufferLength % 3 != 0) {
         int var8;
         do {
            var8 = this._inputStream.read(this._inputBuffer, 0, 2048);
            if (var8 < 0) {
               var8 = 0;
            }

            for (int i = 0; i < var8; i++) {
               byte b = this._inputBuffer[i];
               if (b != 13 && b != 10) {
                  throw new IOException("Base64: decoding error");
               }
            }
         } while (var8 == 2048);

         this._inputBuffer = null;
      }

      return bufferLength;
   }

   public static byte[] decode(String input) {
      return decode(input.getBytes(), 0, input.length());
   }

   public static byte[] decode(String input, int inputOffset, int inputLength) {
      return decode(input.getBytes(), inputOffset, inputLength);
   }

   public static byte[] decode(byte[] input, int inputOffset, int inputLength) {
      if (input != null && inputOffset >= 0 && inputLength >= 0 && input.length - inputLength >= inputOffset) {
         byte[] output = new byte[inputLength];
         if (inputOffset > 0) {
            input = Arrays.copy(input, inputOffset, inputLength);
         }

         int outputLength = decode(input, inputLength, output, 0);
         int remainder = outputLength >> 16;
         outputLength &= 65535;
         if (remainder > 0) {
            throw new IOException();
         }

         Array.resize(output, outputLength);
         return output;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static native int decode(byte[] var0, int var1, byte[] var2, int var3);
}
