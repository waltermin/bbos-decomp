package net.rim.device.cldc.io.btgoep;

import java.io.InputStream;

class OBEXSession$OperationImpl$OperationInputStream extends InputStream {
   private boolean _closed;
   private final OBEXSession$OperationImpl this$1;

   OBEXSession$OperationImpl$OperationInputStream(OBEXSession$OperationImpl _1) {
      this.this$1 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int read() {
      synchronized (this.this$1.this$0) {
         if (this.this$1._receiveBuffer.available() == 0) {
            if (this._closed) {
               throw new Object("Stream closed");
            }

            this.this$1.doSendReceive(true, false);
            if (this._closed) {
               throw new Object("Stream closed");
            }
         }

         boolean var6 = false /* VF: Semaphore variable */;

         byte var10000;
         try {
            var6 = true;
            var10000 = this.this$1._receiveBuffer.readByte();
            var6 = false;
         } finally {
            if (var6) {
               return -1;
            }
         }

         return var10000;
      }
   }

   @Override
   public int read(byte[] b, int off, int len) {
      synchronized (this.this$1.this$0) {
         if (b == null) {
            throw new Object();
         }

         if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new Object();
         }

         if (len == 0) {
            return 0;
         }

         if (this.this$1._receiveBuffer.available() == 0) {
            if (this._closed) {
               throw new Object("Stream closed");
            }

            this.this$1.doSendReceive(true, false);
            if (this._closed) {
               throw new Object("Stream closed");
            }
         }

         return this.this$1._receiveBuffer.read(b, off, len);
      }
   }

   @Override
   public int available() {
      synchronized (this.this$1.this$0) {
         return this.this$1._receiveBuffer.available();
      }
   }

   @Override
   public void close() {
      synchronized (this.this$1.this$0) {
         super.close();
         this._closed = true;
      }
   }
}
