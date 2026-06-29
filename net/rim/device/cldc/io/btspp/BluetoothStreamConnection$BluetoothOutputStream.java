package net.rim.device.cldc.io.btspp;

import java.io.OutputStream;

class BluetoothStreamConnection$BluetoothOutputStream extends OutputStream {
   private final BluetoothStreamConnection this$0;

   BluetoothStreamConnection$BluetoothOutputStream(BluetoothStreamConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public void write(int b) {
      synchronized (this.this$0._writeSemaphore) {
         while (this.this$0._connected) {
            if (this.this$0._port.write(b) == 1) {
               return;
            }

            try {
               this.this$0._writeSemaphore.wait();
            } finally {
               continue;
            }
         }

         throw new Object();
      }
   }

   @Override
   public void write(byte[] b, int off, int len) {
      if (off < 0 || len < 0 || b.length - len < off) {
         throw new Object();
      }

      if (len != 0) {
         synchronized (this.this$0._writeSemaphore) {
            while (this.this$0._connected) {
               int written = this.this$0._port.write(b, off, len);
               if (written == len) {
                  return;
               }

               off += written;
               len -= written;

               try {
                  this.this$0._writeSemaphore.wait();
               } finally {
                  continue;
               }
            }

            throw new Object();
         }
      }
   }
}
