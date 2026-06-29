package net.rim.device.cldc.io.btspp;

import java.io.InputStream;

class BluetoothStreamConnection$BluetoothInputStream extends InputStream {
   private final BluetoothStreamConnection this$0;

   BluetoothStreamConnection$BluetoothInputStream(BluetoothStreamConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public int read() {
      synchronized (this.this$0._readSemaphore) {
         while (this.this$0._bytesAvailable <= 0) {
            if (!this.this$0._connected) {
               return -1;
            }

            try {
               this.this$0._readSemaphore.wait();
            } finally {
               continue;
            }
         }

         BluetoothStreamConnection.access$110(this.this$0);
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

      synchronized (this.this$0._readSemaphore) {
         while (this.this$0._bytesAvailable <= 0) {
            if (!this.this$0._connected) {
               return -1;
            }

            try {
               this.this$0._readSemaphore.wait();
            } finally {
               continue;
            }
         }

         int read = this.this$0._port.read(b, off, len);
         BluetoothStreamConnection.access$120(this.this$0, read);
         return read;
      }
   }

   @Override
   public int available() {
      return this.this$0._bytesAvailable;
   }
}
