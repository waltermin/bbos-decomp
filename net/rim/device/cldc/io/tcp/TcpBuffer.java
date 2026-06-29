package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpConstants;

final class TcpBuffer implements TcpConstants {
   private int _originalSize;
   private byte[] _buffer;
   private int _size;
   private int _start;
   private int _length;
   private int _avail;
   private int _startSeqNumber;
   protected boolean _freshnessSealed = true;
   protected Object _startSeqNumberLock = new Object();
   private static final int TCP_MAX_WINDOW_SIZE = 65536;

   TcpBuffer() {
      this(7200);
   }

   TcpBuffer(int bufferSize) {
      if (bufferSize > 65536) {
         throw new IllegalArgumentException("Buffer size requested is too big");
      }

      if (bufferSize < 536) {
         bufferSize = 536;
      }

      this._originalSize = bufferSize;
      this._buffer = new byte[bufferSize];
      this.reset();
   }

   final void setStartSeqNumber(int startSeqNumber) {
      synchronized (this._startSeqNumberLock) {
         this._startSeqNumber = startSeqNumber;
      }
   }

   final int getStartSeqNumber() {
      synchronized (this._startSeqNumberLock) {
         return this._startSeqNumber;
      }
   }

   final int getBufferSize() {
      return this._size;
   }

   final int getLength() {
      return this._length;
   }

   final int getAvailableSpace() {
      return this._avail;
   }

   final void reset() {
      this.restoreOriginalSize();
      this._size = this._buffer.length;
      this._avail = this._size;
      if (!this._freshnessSealed) {
         this._start = 0;
         this._length = 0;
         this._startSeqNumber = 0;
      }

      this._freshnessSealed = false;
   }

   final void restoreOriginalSize() {
      if (this._buffer.length != this._originalSize) {
         this._buffer = new byte[this._originalSize];
      }
   }

   final int readBySeqNumbers(byte[] output, int outputOff, int leftEdge, int rightEdge) {
      if (output == null) {
         throw new NullPointerException();
      }

      synchronized (this._startSeqNumberLock) {
         int bytesToSend = rightEdge - leftEdge;
         if (bytesToSend >= 0 && leftEdge >= this._startSeqNumber) {
            if (outputOff < 0 || outputOff + bytesToSend > output.length) {
               throw new IndexOutOfBoundsException();
            } else if (bytesToSend > this._length) {
               throw new IllegalArgumentException();
            } else {
               return this.read(output, outputOff, leftEdge - this._startSeqNumber, bytesToSend);
            }
         } else {
            throw new IllegalArgumentException("negative value");
         }
      }
   }

   final int read(byte[] output, int outputOff, int thisOffset, int len) {
      if (thisOffset + len + this._start > this._size && this._start + thisOffset < this._size) {
         int copyLength = this._size - this._start - thisOffset;
         System.arraycopy(this._buffer, this._start + thisOffset, output, outputOff, copyLength);
         System.arraycopy(this._buffer, 0, output, outputOff + copyLength, len - copyLength);
         return len;
      } else {
         int startCopyPos = (this._start + thisOffset) % this._size;
         System.arraycopy(this._buffer, startCopyPos, output, outputOff, len);
         return len;
      }
   }

   final int write(byte[] input, int inputOff, int len) {
      this.writeInternal(input, inputOff, this._length, len);
      this._length += len;
      this._avail -= len;
      return len;
   }

   private final void writeInternal(byte[] input, int inputOff, int thisOffset, int len) {
      if (this._start + thisOffset + len > this._size && this._start + thisOffset < this._size) {
         int copyLength = this._size - this._start - thisOffset;
         System.arraycopy(input, inputOff, this._buffer, this._start + thisOffset, copyLength);
         System.arraycopy(input, inputOff + copyLength, this._buffer, 0, len - copyLength);
      } else {
         int startLocation = (this._start + thisOffset) % this._size;
         System.arraycopy(input, inputOff, this._buffer, startLocation, len);
      }
   }

   final void resizeBuffer(int newSize) {
      synchronized (this._startSeqNumberLock) {
         if (newSize < this._length) {
            newSize = this._length;
         }

         byte[] newBuffer = new byte[newSize];
         this.read(newBuffer, 0, this._start, this._length);
         this._start = 0;
         this._buffer = newBuffer;
         this._size = newSize;
         this._avail = this._size - this._length;
      }
   }

   final int removeUpTo(int ackNumber, boolean finPresent) {
      synchronized (this._startSeqNumberLock) {
         int bytesToRemove = ackNumber - this._startSeqNumber;
         bytesToRemove = bytesToRemove <= 65536 ? bytesToRemove : this._startSeqNumber - ackNumber;
         if (bytesToRemove > this._length) {
            if (bytesToRemove == this._length + 1 && !finPresent) {
               throw new RuntimeException();
            }

            bytesToRemove = this._length;
         }

         this._start = (this._start + bytesToRemove) % this._size;
         this._length -= bytesToRemove;
         this._avail += bytesToRemove;
         this._startSeqNumber += bytesToRemove;
         return bytesToRemove;
      }
   }

   final int remove(int len, boolean finPresent) {
      if (len <= 0) {
         return 0;
      }

      int newStartSeqNumber;
      synchronized (this._startSeqNumberLock) {
         if (len > this._length) {
            len = this._length;
         }

         newStartSeqNumber = this._startSeqNumber + len;
      }

      return this.removeUpTo(newStartSeqNumber, finPresent);
   }
}
