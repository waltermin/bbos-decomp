package net.rim.device.internal.synchronization;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.io.ReservableSize;

class OTAUpgradeControl$CUSafe$ReservableByteArrayOutputStream extends ByteArrayOutputStream implements ReservableSize {
   private OTAUpgradeControl$CUSafe$ReservableByteArrayOutputStream() {
   }

   @Override
   public void reserveSize(long size) {
      if (size > 0) {
         byte[] newBuff = new byte[(int)size];
         if (super.count != 0) {
            System.arraycopy(super.buf, 0, newBuff, 0, super.count);
         }

         super.buf = newBuff;
      }
   }

   OTAUpgradeControl$CUSafe$ReservableByteArrayOutputStream(OTAUpgradeControl$1 x0) {
      this();
   }
}
