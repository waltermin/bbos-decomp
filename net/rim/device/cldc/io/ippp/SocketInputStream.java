package net.rim.device.cldc.io.ippp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

public class SocketInputStream extends InputStream {
   boolean _isClosed;
   StreamProtocol _streamProtocol;
   private ByteArrayInputStream _in;
   private ByteArrayInputStream[] _markedIn;
   private boolean _marking;
   private static final byte[] zeroLengthByteArray = new byte[0];

   public SocketInputStream(StreamProtocol streamProtocol) {
      this._in = new ByteArrayInputStream(zeroLengthByteArray);
      this._streamProtocol = streamProtocol;
   }

   @Override
   public int read() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      int c = -1;

      try {
         c = this._in.read();
         if (c == -1) {
            this.fillBuffer();
            return this._in.read();
         }
      } catch (ConnectionClosedException e) {
         c = -1;
      }

      return c;
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      int bytesLeftToRead = len;
      int currentOffset = off;
      boolean exceptionThrown = false;

      while (bytesLeftToRead > 0) {
         int bytesRead = this._in.read(b, currentOffset, bytesLeftToRead);
         if (bytesRead == bytesLeftToRead) {
            break;
         }

         if (bytesRead != -1) {
            currentOffset += bytesRead;
            bytesLeftToRead -= bytesRead;
         }

         try {
            this.fillBuffer();
         } catch (ConnectionClosedException e) {
            len -= bytesLeftToRead;
            bytesLeftToRead = 0;
            exceptionThrown = true;
         }
      }

      if (exceptionThrown && len == 0) {
         len = -1;
      }

      return len;
   }

   @Override
   public void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._streamProtocol.inputStreamClosed();
      }
   }

   @Override
   public int available() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._in.available();
      }
   }

   public boolean isClosed() {
      return this._isClosed;
   }

   @Override
   public void mark(int size) {
      if (size > 0) {
         this._markedIn = new ByteArrayInputStream[1];
         this._markedIn[0] = this._in;
         this._in.mark(size);
         this._marking = true;
      } else {
         this._marking = false;
         this._markedIn = null;
      }
   }

   @Override
   public void reset() {
      if (this._markedIn != null) {
         this._in = new ByteArrayInputStream(zeroLengthByteArray);
      }

      this._marking = false;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   private void fillBuffer() {
      if (this._markedIn != null && !this._marking) {
         ByteArrayInputStream temp = this._markedIn[0];
         if (this._markedIn.length == 1) {
            this._markedIn = null;
         } else {
            System.arraycopy(this._markedIn, 1, this._markedIn, 0, this._markedIn.length - 1);
            Array.resize(this._markedIn, this._markedIn.length - 1);
         }

         this._in = temp;
         this._in.reset();
      } else {
         DataBuffer nextBuffer;
         do {
            nextBuffer = this._streamProtocol.getMoreInput();
         } while (nextBuffer == null || nextBuffer.available() <= 0);

         this._in = new ByteArrayInputStream(nextBuffer.getArray(), nextBuffer.getArrayPosition(), nextBuffer.available());
         if (this._markedIn != null) {
            this._in.mark(Integer.MAX_VALUE);
            Array.resize(this._markedIn, this._markedIn.length + 1);
            this._markedIn[this._markedIn.length - 1] = this._in;
         }
      }
   }

   public InputStream getPipeInputStream(boolean ykEncoded, boolean ykMixedEncoded) {
      return new SocketPipeInputStream(this, ykEncoded, ykMixedEncoded);
   }

   DataBuffer getMoreInput() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._streamProtocol.getMoreInput();
      }
   }

   byte[] getRemainingBuffer() {
      byte[] bytes = new byte[this._in.available()];
      this._in.read(bytes);
      return bytes;
   }
}
