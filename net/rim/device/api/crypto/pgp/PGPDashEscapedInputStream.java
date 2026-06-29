package net.rim.device.api.crypto.pgp;

import java.io.InputStream;

final class PGPDashEscapedInputStream extends InputStream {
   private int _currentState;
   private InputStream _input;
   private byte[] _readBuffer;
   private byte[] _buffer;
   private int _bufferLength;
   private int _bufferOffset;
   private static final int STATE_LINE_START = 0;
   private static final int STATE_LINE_MIDDLE = 1;
   private static final int STATE_CR_READ = 2;
   private static final int STATE_DASH_READ = 3;
   private static final byte CR = 13;
   private static final byte LF = 10;
   private static final byte DASH = 45;
   private static final byte SPACE = 32;
   private static final int READ_LENGTH = 128;

   public PGPDashEscapedInputStream(InputStream input) {
      if (input == null) {
         throw new IllegalArgumentException();
      }

      this._input = input;
      this._currentState = 0;
      this._buffer = new byte[128];
      this._bufferLength = this._input.read(this._buffer);
      this._bufferOffset = 0;
   }

   @Override
   public final int read() {
      if (this._readBuffer == null) {
         this._readBuffer = new byte[1];
      }

      int bytesRead = 0;

      while (bytesRead < 1) {
         bytesRead = this.read(this._readBuffer, 0, 1);
         if (bytesRead < 0) {
            return -1;
         }
      }

      return this._readBuffer[0] & 0xFF;
   }

   @Override
   public final int read(byte[] b) {
      return this.read(b, 0, b == null ? 0 : b.length);
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      if (b == null || off < 0 || len < 0 || b.length - len < off) {
         throw new IllegalArgumentException();
      }

      if (this._bufferLength < 0) {
         return this._bufferLength;
      }

      int bytesRead = 0;

      while (bytesRead < len) {
         while (this._bufferOffset >= this._bufferLength) {
            this._bufferLength = this._input.read(this._buffer);
            this._bufferOffset = 0;
            if (this._bufferLength < 0) {
               return bytesRead;
            }
         }

         byte currentByte;
         currentByte = this._buffer[this._bufferOffset++];
         label51:
         switch (this._currentState) {
            case -1:
               throw new IllegalStateException();
            case 0:
            default:
               switch (currentByte) {
                  case 10:
                     break label51;
                  case 13:
                     this._currentState = 2;
                     break label51;
                  case 45:
                     this._currentState = 3;
                     continue;
                  default:
                     this._currentState = 1;
                     break label51;
               }
            case 1:
               switch (currentByte) {
                  case 10:
                     this._currentState = 0;
                     break label51;
                  case 13:
                     this._currentState = 2;
                  default:
                     break label51;
               }
            case 2:
               switch (currentByte) {
                  case 10:
                     this._currentState = 0;
                  case 13:
                     break label51;
                  default:
                     this._currentState = 1;
                     break label51;
               }
            case 3:
               switch (currentByte) {
                  case 32:
                     this._currentState = 1;
                     continue;
                  default:
                     b[off++] = 45;
                     bytesRead++;
                     this._currentState = 1;
                     this._bufferOffset--;
                     continue;
               }
         }

         b[off++] = currentByte;
         bytesRead++;
      }

      return bytesRead;
   }

   @Override
   public final long skip(long n) {
      return this._input.skip(n);
   }

   @Override
   public final int available() {
      return this._input.available();
   }

   @Override
   public final void close() {
      this._input.close();
   }
}
