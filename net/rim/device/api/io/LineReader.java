package net.rim.device.api.io;

import java.io.EOFException;
import java.io.InputStream;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class LineReader {
   protected InputStream _stream;
   protected byte[] _buffer;
   protected int _bufferOffset;
   protected int _bufferLength;
   protected static final int BUFFER_LENGTH;
   private static final byte CR;
   private static final byte LF;

   public LineReader(InputStream stream) {
      if (stream == null) {
         throw new IllegalArgumentException();
      }

      this._stream = stream;
      this._buffer = new byte[1024];
      this._bufferOffset = 0;
      this._bufferLength = 0;
   }

   public InputStream getStream() {
      return this._stream;
   }

   public byte[] getBuffer() {
      return this._buffer;
   }

   public int getBufferOffset() {
      return this._bufferOffset;
   }

   public int getBufferLength() {
      return this._bufferLength;
   }

   public void setBufferOffset(int bufferOffset) {
      this._bufferOffset = bufferOffset;
   }

   public void setBufferLength(int bufferLength) {
      this._bufferLength = bufferLength;
   }

   public int lengthUnreadData() {
      return this._bufferLength - this._bufferOffset;
   }

   public byte[] readLine() {
      if (this._bufferOffset == this._bufferLength) {
         this._bufferOffset = 0;
         this._bufferLength = this._stream.read(this._buffer, 0, 1024);
         if (this._bufferLength <= 0) {
            this._bufferLength = 0;
            throw new EOFException();
         }
      }

      int lineStart = this._bufferOffset;
      int lineLength = 0;
      byte[] line = new byte[0];
      boolean crRead = false;

      do {
         do {
            byte b = this._buffer[this._bufferOffset++];
            if (b == 13) {
               if (this._bufferOffset != this._bufferLength) {
                  if (this._buffer[this._bufferOffset] == 10) {
                     this._bufferOffset++;
                     if (lineLength == 0) {
                        return Arrays.copy(this._buffer, lineStart, this._bufferOffset - 2 - lineStart);
                     }

                     return this.concatenate(line, this._buffer, lineStart, this._bufferOffset - 2 - lineStart);
                  }

                  if (lineLength == 0) {
                     return Arrays.copy(this._buffer, lineStart, this._bufferOffset - 1 - lineStart);
                  }

                  return this.concatenate(line, this._buffer, lineStart, this._bufferOffset - 1 - lineStart);
               }

               crRead = true;
               break;
            }

            if (b == 10) {
               if (lineLength == 0) {
                  return Arrays.copy(this._buffer, lineStart, this._bufferOffset - 1 - lineStart);
               }

               return this.concatenate(line, this._buffer, lineStart, this._bufferOffset - 1 - lineStart);
            }
         } while (this._bufferOffset < this._bufferLength);

         int oldLineLength = line.length;
         lineLength = line.length + (this._bufferOffset - lineStart);
         Array.resize(line, lineLength);
         System.arraycopy(this._buffer, lineStart, line, oldLineLength, this._bufferOffset - lineStart);
         lineStart = 0;
         this._bufferOffset = 0;
         this._bufferLength = this._stream.read(this._buffer, 0, 1024);
         if (this._bufferLength < 0) {
            this._bufferLength = 0;
            if (crRead) {
               return Arrays.copy(line, 0, line.length - 1);
            }

            return line;
         }
      } while (!crRead);

      if (this._buffer[this._bufferOffset] == 10) {
         this._bufferOffset++;
      }

      return Arrays.copy(line, 0, line.length - 1);
   }

   private byte[] concatenate(byte[] original, byte[] newData, int offset, int length) {
      int originalLength = original.length;
      Array.resize(original, original.length + length);
      System.arraycopy(newData, offset, original, originalLength, length);
      return original;
   }
}
