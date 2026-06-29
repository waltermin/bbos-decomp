package net.rim.device.cldc.io.btspp;

import java.io.IOException;
import java.io.OutputStream;

class BluetoothSerialConnection$BluetoothOutputStream extends OutputStream {
   private final BluetoothSerialConnection this$0;

   public BluetoothSerialConnection$BluetoothOutputStream(BluetoothSerialConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public void write(int b) {
      synchronized (this.this$0._writeSemaphore) {
         while (!this.this$0._closed && this.this$0._connected) {
            if (this.this$0._port.write(b) == 1) {
               return;
            }

            try {
               this.this$0._writeSemaphore.wait();
            } finally {
               continue;
            }
         }

         throw new IOException();
      }
   }

   @Override
   public void write(byte[] b, int off, int len) {
      if (off >= 0 && len >= 0 && b.length - len >= off) {
         if (len != 0) {
            synchronized (this.this$0._writeSemaphore) {
               while (!this.this$0._closed && this.this$0._connected) {
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

               throw new IOException();
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
