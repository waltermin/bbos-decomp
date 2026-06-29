package net.rim.device.apps.internal.supl;

import net.rim.device.api.system.RadioInfo;

final class Status {
   private byte status;
   static final byte STALE;
   static final byte CURRENT;
   static final byte UNKNOWN;
   static final byte STATUS_NUM_BITS;

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
      System.out.println(((StringBuffer)(new Object("Status: "))).append(this.status).toString());
   }
}
