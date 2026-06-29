package net.rim.device.internal.util;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.vm.Array;

public class RingBuffer {
   private int _startIndex = 0;
   private int _endIndex = 0;
   private byte[] _buffer;
   private int _dataLength = 0;
   private boolean _isOpen = true;
   private int _maxBytesToReadEntirely = Integer.MAX_VALUE;
   private static final int BUFFER_SECTION_SIZE;

   public RingBuffer(int size) {
      this._buffer = new byte[size];
      Array.setSectionSize(this._buffer, 2048);
   }

   public RingBuffer(byte[] buffer) {
      this._buffer = buffer;
      this._endIndex = this._dataLength = this._buffer.length;
   }

   public synchronized void clear() {
      this._startIndex = this._endIndex = this._dataLength = 0;
      this._isOpen = true;
   }

   public synchronized void close() {
      this._isOpen = false;
      this.notify();
   }

   public int getBufferSize() {
      return this._buffer.length;
   }

   public int getDataSize() {
      return this._dataLength;
   }

   int getEndIndex() {
      return this._endIndex;
   }

   int getStartIndex() {
      return this._startIndex;
   }

   public boolean isEmpty() {
      return this._dataLength == 0;
   }

   public boolean isFull() {
      return this._dataLength == this._buffer.length;
   }

   public synchronized int read(byte[] buffer, int start, int length) {
      int bytesRead = Math.min(length, this._dataLength);
      int len1 = Math.min(bytesRead, this._buffer.length - this._startIndex);
      System.arraycopy(this._buffer, this._startIndex, buffer, start, len1);
      this._startIndex += len1;
      if (len1 < bytesRead) {
         start += len1;
         this._startIndex = 0;
         len1 = bytesRead - len1;
         System.arraycopy(this._buffer, this._startIndex, buffer, start, len1);
         this._startIndex += len1;
      }

      if (this._startIndex == this._buffer.length) {
         this._startIndex = 0;
         if (this._endIndex == this._buffer.length) {
            this._endIndex = 0;
         }
      }

      this._dataLength -= bytesRead;
      return bytesRead;
   }

   public synchronized int read(OutputStream stream, int length) {
      int bytesToRead = Math.min(this._dataLength, length);
      int bytesRead = 0;

      while (bytesToRead > 0) {
         int len1 = Math.min(bytesToRead, this._buffer.length - this._startIndex);
         stream.write(this._buffer, this._startIndex, len1);
         this._startIndex += len1;
         bytesRead += len1;
         this._dataLength -= len1;
         bytesToRead -= len1;
         if (this._startIndex == this._buffer.length) {
            this._startIndex = 0;
            if (this._endIndex == this._buffer.length) {
               this._endIndex = 0;
            }
         }
      }

      return bytesRead;
   }

   public synchronized int read(RingBuffer$UnassertiveOutputStream stream, int length) {
      int bytesToRead = Math.min(this._dataLength, length);
      int bytesRead = 0;
      if (bytesToRead == 0) {
         stream.write(this._buffer, 0, 0);
      }

      while (bytesToRead > 0) {
         int len1 = Math.min(bytesToRead, this._buffer.length - this._startIndex);
         int written = stream.write(this._buffer, this._startIndex, len1);
         if (written == 0) {
            break;
         }

         this._startIndex += written;
         bytesRead += written;
         this._dataLength -= written;
         bytesToRead -= written;
         if (this._startIndex == this._buffer.length) {
            this._startIndex = 0;
            if (this._endIndex == this._buffer.length) {
               this._endIndex = 0;
            }
         }
      }

      return bytesRead;
   }

   public synchronized int read(OutputStream stream) {
      return this.read(stream, Integer.MAX_VALUE);
   }

   public synchronized void readEntirely(OutputStream stream) {
      this.readEntirely(stream, 0);
   }

   public synchronized void readEntirely(OutputStream stream, int bytesToIgnore) {
      int bytesRead = 0;

      while (this._isOpen && bytesRead < this._maxBytesToReadEntirely) {
         if (bytesToIgnore > 0) {
            while (this._dataLength < bytesToIgnore) {
               try {
                  this.wait();
               } catch (InterruptedException var6) {
               }
            }

            this._startIndex += bytesToIgnore;
            this._dataLength -= bytesToIgnore;
            bytesToIgnore = 0;
         }

         if (this._maxBytesToReadEntirely == Integer.MAX_VALUE) {
            bytesRead += this.read(stream);
         } else {
            bytesRead += this.read(stream, this._maxBytesToReadEntirely - bytesRead);
            if (bytesRead >= this._maxBytesToReadEntirely) {
               return;
            }
         }

         try {
            this.wait();
         } catch (InterruptedException var5) {
         }
      }
   }

   public synchronized void setReadEntirelySizeLimit(int maxNumBytes) {
      this._maxBytesToReadEntirely = maxNumBytes;
      this.notify();
   }

   public synchronized int write(byte[] buffer, int start, int length) {
      int bytesWritten = Math.min(length, this._buffer.length - this._dataLength);
      int len1 = Math.min(bytesWritten, this._buffer.length - this._endIndex);
      System.arraycopy(buffer, start, this._buffer, this._endIndex, len1);
      this._endIndex += len1;
      if (len1 < bytesWritten) {
         start += len1;
         this._endIndex = 0;
         len1 = bytesWritten - len1;
         System.arraycopy(buffer, start, this._buffer, this._endIndex, len1);
         this._endIndex += len1;
      }

      this._dataLength += bytesWritten;
      this.notify();
      return bytesWritten;
   }

   public synchronized int write(InputStream stream) {
      int bytesWritten = 0;
      int written = 0;

      while (this._dataLength < this._buffer.length) {
         if (this._endIndex == this._buffer.length) {
            this._endIndex = 0;
         }

         int len1 = Math.min(this._buffer.length - this._endIndex, this._buffer.length - this._dataLength);
         written = stream.read(this._buffer, this._endIndex, len1);
         if (written <= 0) {
            break;
         }

         bytesWritten += written;
         this._dataLength += written;
         this._endIndex += written;
      }

      if (bytesWritten == 0 && written < 0) {
         bytesWritten = -1;
      }

      if (this._endIndex == 0 && this._dataLength != 0) {
         this._endIndex = this._buffer.length;
      }

      this.notify();
      return bytesWritten;
   }
}
