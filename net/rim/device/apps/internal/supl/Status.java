package net.rim.device.apps.internal.supl;

import net.rim.device.api.system.RadioInfo;

final class Status {
   private byte status;
   static final byte STALE = 0;
   static final byte CURRENT = 1;
   static final byte UNKNOWN = 2;
   static final byte STATUS_NUM_BITS = 2;

   Status() {
      int networkService = RadioInfo.getNetworkService();
      if (networkService != -256) {
         this.status = 1;
      } else {
         this.status = 0;
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.status, 2);
   }

   final void print() {
      System.out.println("Status: " + this.status);
   }
}
