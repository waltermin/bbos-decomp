package net.rim.device.apps.internal.supl;

import net.rim.device.api.system.SIMCardInfo;

final class SetSessionId {
   int sessionId;
   SetId setId;
   static final byte SUPL_SET_SESSION_ID_BIT_SIZE;
   static final boolean SET_ID_USE_IMSI;

   SetSessionId() {
   }

   SetSessionId(int sessionId) {
      this.sessionId = sessionId;

      try {
         char[] data = new char[8];
         byte[] buf = new byte[16];
         buf = SIMCardInfo.getIMSI();

         for (byte i = 0; i < 15; i++) {
            if (i % 2 == 0) {
               data[i >> 1] = (char)(data[i >> 1] | (char)(buf[i] & 15));
            } else {
               data[i >> 1] = (char)(data[i >> 1] | (char)(buf[i] << 4) & 240);
            }
         }

         data[7] = (char)(data[7] | 240);
         Imsi imsi = new Imsi(data);
         this.setId = imsi;
      } finally {
         return;
      }
   }

   final void decode(Nibbler nib) {
      this.sessionId = nib.getBitsLarge(16);
      this.setId = SetIdFactory.DecodeChoiceIndex(nib);
      if (this.setId != null) {
         this.setId.decode(nib);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.sessionId, 16);
      stuff.putBit(false);
      if (this.setId instanceof IpAddress) {
         stuff.putBits(5, 3);
      }

      this.setId.encode(stuff);
   }

   final boolean equals(SetSessionId setSessionId) {
      boolean retVal = true;
      if (this.sessionId == setSessionId.sessionId) {
         retVal = true;
      } else {
         retVal = false;
      }

      if (retVal) {
         if (this.setId instanceof Imsi) {
            Imsi imsi = (Imsi)this.setId;
            return imsi.equals(setSessionId.setId);
         }

         if (this.setId instanceof Msisdn) {
            Msisdn msisdn = (Msisdn)this.setId;
            return msisdn.equals(setSessionId.setId);
         }

         if (this.setId instanceof Mdn) {
            Mdn mdn = (Mdn)this.setId;
            return mdn.equals(setSessionId.setId);
         }

         if (this.setId instanceof Ipv4Address) {
            Ipv4Address addr = (Ipv4Address)this.setId;
            return addr.equals(setSessionId.setId);
         }

         if (this.setId instanceof Ipv6Address) {
            Ipv6Address addr = (Ipv6Address)this.setId;
            return addr.equals(setSessionId.setId);
         }

         System.out.println("Unknown SetId");
         retVal = false;
      }

      return retVal;
   }

   final void print() {
      System.out.println("SetSessionId: ");
      System.out.println(((StringBuffer)(new Object("SessionId: "))).append(this.sessionId).toString());
      System.out.println("SetId: ");
      if (!(this.setId instanceof Imsi)) {
         if (!(this.setId instanceof Msisdn)) {
            if (!(this.setId instanceof Mdn)) {
               if (!(this.setId instanceof Ipv4Address)) {
                  if (!(this.setId instanceof Ipv6Address)) {
                     System.out.println("Unknown SetId type");
                  } else {
                     Ipv6Address addr = (Ipv6Address)this.setId;
                     addr.print();
                  }
               } else {
                  Ipv4Address addr = (Ipv4Address)this.setId;
                  addr.print();
               }
            } else {
               Mdn mdn = (Mdn)this.setId;
               mdn.print();
            }
         } else {
            Msisdn msisdn = (Msisdn)this.setId;
            msisdn.print();
         }
      } else {
         Imsi imsi = (Imsi)this.setId;
         imsi.print();
      }
   }
}
