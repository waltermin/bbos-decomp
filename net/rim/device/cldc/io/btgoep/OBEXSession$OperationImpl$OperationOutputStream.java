package net.rim.device.cldc.io.btgoep;

import java.io.IOException;
import java.io.OutputStream;

class OBEXSession$OperationImpl$OperationOutputStream extends OutputStream {
   private boolean _closed;
   private final OBEXSession$OperationImpl this$1;

   OBEXSession$OperationImpl$OperationOutputStream(OBEXSession$OperationImpl _1) {
      this.this$1 = _1;
   }

   @Override
   public void write(int b) {
      synchronized (this.this$1.this$0) {
         if (this._closed) {
            throw new IOException("Stream closed");
         }

         this.this$1._sendBuffer.writeByte(b);
         if (this.this$1._sendBuffer.getLength() >= this.this$1.this$0._maxDataSize) {
            this.this$1.doSendReceive(false, false);
         }
      }
   }

   @Override
   public void write(byte[] b, int off, int len) {
      synchronized (this.this$1.this$0) {
         if (this._closed) {
            throw new IOException("Stream closed");
         }

         if (b == null) {
            throw new NullPointerException();
         }

         if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
         }

         if (len != 0) {
            this.this$1._sendBuffer.write(b, off, len);
            this.this$1.doSendReceive(false, false);
         }
      }
   }

   @Override
   public void close() {
      synchronized (this.this$1.this$0) {
         super.close();
         this._closed = true;
      }
   }

   boolean isClosed() {
      return this._closed;
   }
}
