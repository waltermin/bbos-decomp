package net.rim.device.cldc.io.ippp;

import java.io.OutputStream;
import net.rim.device.api.util.DataBuffer;

public class SocketOutputStream extends OutputStream {
   private StreamProtocol _streamProtocol;
   private boolean _isClosed;
   private DataBuffer _buffer;
   private int _flushLength;
   private int _bufferLength;
   private boolean _autoFlush;
   private boolean _sendFirstEmptyPacket = true;

   public SocketOutputStream(StreamProtocol streamProtocol, boolean sendFirstEmptyPacket) {
      if (streamProtocol != null) {
         this._streamProtocol = streamProtocol;
         this._flushLength = streamProtocol.getOutputStreamSize();
         this._buffer = (DataBuffer)(new Object(this._flushLength, false));
         this._bufferLength = 0;
         this._sendFirstEmptyPacket = sendFirstEmptyPacket;
         this._autoFlush = true;
      } else {
         throw new Object();
      }
   }

   @Override
   public void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._streamProtocol.outputStreamClosed();
         this.sendBuffer();
      }
   }

   @Override
   public void write(byte[] byteArray, int offset, int length) {
      if (this._isClosed) {
         throw new Object();
      }

      if (byteArray == null) {
         throw new Object();
      }

      synchronized (this._buffer) {
         if (this._autoFlush) {
            int endOfOutput = offset + length;

            while (offset < endOfOutput) {
               int bufferRemaining = this._flushLength - this._bufferLength;
               int outputRemaining = endOfOutput - offset;
               int amountToCopy = bufferRemaining > outputRemaining ? outputRemaining : bufferRemaining;
               this._buffer.write(byteArray, offset, amountToCopy);
               offset += amountToCopy;
               this._bufferLength += amountToCopy;
               if (this._bufferLength >= this._flushLength) {
                  this.sendBuffer();
               }
            }
         } else {
            this._buffer.write(byteArray, offset, length);
            this._bufferLength += length;
         }
      }
   }

   @Override
   public void write(int someChar) {
      synchronized (this._buffer) {
         this._buffer.write(someChar);
         this._bufferLength++;
         if (this._autoFlush && this._bufferLength >= this._flushLength) {
            this.sendBuffer();
         }
      }
   }

   @Override
   public void write(byte[] byteArray) {
      if (byteArray != null) {
         this.write(byteArray, 0, byteArray.length);
      } else {
         throw new Object();
      }
   }

   public boolean isClosed() {
      return this._isClosed;
   }

   @Override
   public void flush() {
      this.sendBuffer();
   }

   public void setAutoFlushMode(boolean mode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private void sendBuffer() {
      synchronized (this._buffer) {
         this._autoFlush = true;
         if (this._bufferLength != 0 || this._sendFirstEmptyPacket) {
            this._streamProtocol.sendOutput(this._buffer.getArray(), 0, this._bufferLength);
            this._bufferLength = 0;
            this._sendFirstEmptyPacket = false;
            this._buffer.rewind();
         }
      }
   }
}
