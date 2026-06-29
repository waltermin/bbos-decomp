package net.rim.device.apps.internal.browser.stack;

import java.io.InputStream;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipeContext;
import net.rim.device.internal.browser.util.PipeInput;
import net.rim.device.internal.browser.util.PipePtr;
import net.rim.vm.Array;

public final class MarkableInputStream extends InputStream implements PipeInput {
   InputStream _in;
   private byte[] _buffer;
   private int _bufferPos = -1;
   private boolean _marking;
   private DataBuffer _copy = (DataBuffer)(new Object());
   private boolean _saveContent;
   private byte[] _tmpArray;
   private int _tmpArraySize;
   private static final int TMP_ARRAY_SIZE_INCREMENT = 200;

   public final synchronized void save() {
      this._saveContent = !this._saveContent;
   }

   @Override
   public final synchronized int readByteArray(PipePtr ptr, int size) {
      byte[] data = new byte[size];
      int readSize = this.read(data, 0, size);
      ptr.setData(data, 0, readSize, false);
      return readSize;
   }

   @Override
   public final synchronized int readCompressedInt() {
      int result = 0;

      int i;
      do {
         i = this.read();
         if (i == -1) {
            throw new Object();
         }

         result = result << 7 | i & 127;
      } while ((i & 128) != 0);

      return result;
   }

   @Override
   public final synchronized Pipe getPipe() {
      byte[] data = this._copy.toArray();
      return (Pipe)(new Object(data, data.length));
   }

   @Override
   public final synchronized void skipInlineString() {
      int c = this.read();

      while (c != -1 && c != 0) {
         c = this.read();
      }

      if (c == -1) {
         throw new Object();
      }
   }

   @Override
   public final synchronized String readInlineString(String encoding) {
      if (this._tmpArray == null) {
         this._tmpArray = new byte[200];
      }

      boolean carriageReturn = false;
      int c = this.read();
      int i = 0;

      while (c != -1 && c != 0) {
         if (c == 10 && carriageReturn) {
            carriageReturn = false;
            i--;
         } else {
            if (c == 13) {
               carriageReturn = true;
               c = 10;
            } else {
               carriageReturn = false;
            }

            if (i == this._tmpArraySize) {
               this._tmpArraySize += 200;
               Array.resize(this._tmpArray, this._tmpArraySize);
            }

            this._tmpArray[i] = (byte)c;
         }

         i++;
         c = this.read();
      }

      if (c == -1) {
         throw new Object();
      } else {
         return (String)(new Object(this._tmpArray, 0, i, encoding));
      }
   }

   @Override
   public final synchronized PipeContext getPosition() {
      return null;
   }

   @Override
   public final synchronized int read(byte[] b, int off, int len) {
      if (b == null) {
         throw new Object();
      }

      if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new Object();
      }

      if (len == 0) {
         return 0;
      }

      if (this._buffer != null && this._bufferPos > -1 && this._buffer.length > this._bufferPos) {
         int bufferRead = Math.min(len, this._buffer.length - this._bufferPos);
         System.arraycopy(this._buffer, this._bufferPos, b, off, bufferRead);
         this._bufferPos += bufferRead;
         if (!this._marking && this._bufferPos >= this._buffer.length) {
            this._bufferPos = -1;
            this._buffer = null;
         }

         return bufferRead < len ? bufferRead + Math.max(0, this.read(b, off + bufferRead, len - bufferRead)) : bufferRead;
      } else {
         int inRead = this._in.read(b, off, len);
         if (this._marking && inRead > 0) {
            if (this._buffer == null) {
               throw new Object();
            }

            int originalLength = this._buffer.length;
            Array.resize(this._buffer, this._buffer.length + inRead);
            System.arraycopy(b, off, this._buffer, originalLength, inRead);
         }

         if (this._saveContent && inRead > 0) {
            this._copy.write(b, off, inRead);
         }

         return inRead;
      }
   }

   @Override
   public final synchronized boolean markSupported() {
      return true;
   }

   public MarkableInputStream(InputStream in) {
      this._in = in;
   }

   @Override
   public final synchronized void mark(int readlimit) {
      if (this._in.markSupported()) {
         this._in.mark(readlimit);
      } else {
         if (this._buffer == null || this._bufferPos <= -1) {
            this._buffer = new byte[0];
         } else if (this._bufferPos != 0) {
            System.arraycopy(this._buffer, this._bufferPos, this._buffer, 0, this._buffer.length - this._bufferPos);
            Array.resize(this._buffer, this._buffer.length - this._bufferPos);
            this._bufferPos = 0;
         }

         this._marking = true;
      }
   }

   @Override
   public final synchronized void reset() {
      if (this._in.markSupported()) {
         this._in.reset();
      } else {
         if (this._marking) {
            this._bufferPos = 0;
            this._marking = false;
         }
      }
   }

   @Override
   public final synchronized long skip(long n) {
      if (this._buffer != null && this._bufferPos > -1 && this._buffer.length > this._bufferPos && !this._marking) {
         long bufferSkipped = Math.min(this._buffer.length & 4095, n);
         this._bufferPos = (int)(this._bufferPos + bufferSkipped);
         if (this._bufferPos >= this._buffer.length) {
            this._bufferPos = -1;
            this._buffer = null;
         }

         return bufferSkipped < n ? bufferSkipped + this.skip(n - bufferSkipped) : bufferSkipped;
      } else {
         return this._marking && n > 0 ? this.read(new byte[(int)n]) : this._in.skip(n);
      }
   }

   @Override
   public final synchronized int available() {
      return this._in.available();
   }

   @Override
   public final void close() {
      this._in.close();
   }

   @Override
   public final synchronized int read() {
      if (this._buffer != null && this._bufferPos > -1 && this._buffer.length > this._bufferPos) {
         int value = this._buffer[this._bufferPos] & 255;
         this._bufferPos++;
         if (!this._marking && this._bufferPos >= this._buffer.length) {
            this._bufferPos = -1;
            this._buffer = null;
         }

         return value;
      } else {
         int value = this._in.read();
         if (this._marking) {
            if (this._buffer == null) {
               throw new Object();
            }

            Array.resize(this._buffer, this._buffer.length + 1);
            this._buffer[this._buffer.length - 1] = (byte)value;
         }

         if (this._saveContent) {
            this._copy.write(value);
         }

         return value;
      }
   }
}
