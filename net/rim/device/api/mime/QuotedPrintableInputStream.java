package net.rim.device.api.mime;

import java.io.InputStream;

final class QuotedPrintableInputStream extends InputStream {
   private InputStream _input;
   private byte[] _readBuffer;
   private byte[] _buffer;
   private int _bufferLength;
   private int _bufferOffset;
   private static final int BUFFER_SIZE;
   private static final int ANY_STATE;
   private static final int HEX1_STATE;
   private static final int HEX2_STATE;
   private static final int STRIP_SPACES_STATE;
   private static final int SOFT_BREAK_STATE;

   public QuotedPrintableInputStream(InputStream input) {
      if (input == null) {
         throw new Object();
      }

      this._input = input;
      this._buffer = new byte[100];
      this._bufferOffset = 0;
      this._bufferLength = this._input.read(this._buffer);
   }

   @Override
   public final int read() {
      if (this._readBuffer == null) {
         this._readBuffer = new byte[1];
      }

      return this.read(this._readBuffer, 0, 1) < 0 ? -1 : this._readBuffer[0] & 0xFF;
   }

   @Override
   public final int read(byte[] buffer) {
      return this.read(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         byte combined = 0;
         int lenRead = 0;
         int state = 0;

         while (lenRead < length) {
            if (this._bufferOffset >= this._bufferLength) {
               if (this._bufferLength > 0) {
                  this._buffer[0] = this._buffer[this._bufferLength - 1];
               }

               int readResult = this._input.read(this._buffer, 1, this._buffer.length - 1);
               if (readResult < 0) {
                  break;
               }

               this._bufferOffset = 1;
               this._bufferLength = readResult + 1;
            }

            byte current = this._buffer[this._bufferOffset++];
            switch (state) {
               case -1:
               case 3:
                  break;
               case 0:
               default:
                  if (current == 61) {
                     state = 1;
                  } else {
                     buffer[offset++] = current;
                     lenRead++;
                  }
                  break;
               case 1:
                  combined = this.fromHexChar(current);
                  if (combined == -1) {
                     if (current == 13) {
                        state = 4;
                     } else {
                        state = 0;
                        buffer[offset++] = 61;
                        lenRead++;
                        this._bufferOffset--;
                     }
                  } else {
                     state = 2;
                  }
                  break;
               case 2:
                  byte test = this.fromHexChar(current);
                  if (test == -1) {
                     state = 0;
                     buffer[offset++] = 61;
                     lenRead++;
                     this._bufferOffset -= 2;
                  } else {
                     combined = (byte)(combined << 4 | test & 15);
                     buffer[offset++] = combined;
                     lenRead++;
                     state = 0;
                  }
                  break;
               case 4:
                  if (current != 10) {
                     buffer[offset++] = 61;
                     lenRead++;
                     this._bufferOffset -= 2;
                  }

                  state = 0;
            }
         }

         return lenRead == 0 && length != 0 ? -1 : lenRead;
      } else {
         throw new Object();
      }
   }

   private final byte fromHexChar(byte a) {
      if (a - 48 >= 0 && a - 48 <= 9) {
         return (byte)(a - 48);
      } else if (a >= 97 && a <= 102) {
         return (byte)(a - 97 + 10);
      } else {
         return a >= 65 && a <= 70 ? (byte)(a - 65 + 10) : -1;
      }
   }

   @Override
   public final long skip(long n) {
      byte[] temp = new byte[(int)n];
      return this.read(temp);
   }

   @Override
   public final int available() {
      int bytesLeft = Math.max(this._bufferLength - this._bufferOffset, 0) + this._input.available();
      return bytesLeft / 3;
   }

   @Override
   public final void close() {
      this._input.close();
   }
}
