package net.rim.device.cldc.io.btspp;

import java.io.InputStream;

class BluetoothSerialConnection$BluetoothInputStream extends InputStream {
   private final BluetoothSerialConnection this$0;

   public BluetoothSerialConnection$BluetoothInputStream(BluetoothSerialConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public int read() {
      synchronized (this.this$0._semaphore) {
         while (this.this$0._bytesAvailable <= 0) {
            if (this.this$0._closed || !this.this$0._connected) {
               return -1;
            }

            label53:
            try {
               this.this$0._semaphore.wait(this.this$0._readTimeout);
            } finally {
               break label53;
            }

            if (this.this$0._bytesAvailable == 0) {
               return -1;
            }
         }

         BluetoothSerialConnection.access$110(this.this$0);
         return this.this$0._port.read();
      }
   }

   @Override
   public int read(byte[] b, int off, int len) {
      if (off < 0 || len < 0 || b.length - len < off) {
         throw new Object();
      }

      if (len == 0) {
         return 0;
      }

      synchronized (this.this$0._semaphore) {
         while (this.this$0._bytesAvailable <= 0) {
            if (this.this$0._closed || !this.this$0._connected) {
               return -1;
            }

            label66:
            try {
               this.this$0._semaphore.wait(this.this$0._readTimeout);
            } finally {
               break label66;
            }

            if (this.this$0._bytesAvailable == 0) {
               return -1;
            }
         }

         int read = this.this$0._port.read(b, off, len);
         BluetoothSerialConnection.access$120(this.this$0, read);
         return read;
      }
   }

   @Override
   public int available() {
      return this.this$0._bytesAvailable;
   }
}
