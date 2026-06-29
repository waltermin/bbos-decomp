package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class Ipv4Address extends IpAddress {
   private short[] addr = new short[4];
   static final byte IP_V4_ADDR_SIZE;
   static final byte OCTET_BIT_SIZE;

   @Override
   public final void decode(Nibbler nib) {
      for (int i = 0; i < 4; i++) {
         this.addr[i] = (short)(0xFF & nib.getByte());
      }
   }

   @Override
   public final void encode(Stuffer stuff) {
      stuff.putBits(0, 1);

      for (int i = 0; i < 4; i++) {
         stuff.putBits(this.addr[i], 8);
      }
   }

   @Override
   public final boolean equals(Object obj) {
      boolean retVal = false;
      if (obj instanceof Ipv4Address) {
         Ipv4Address ipAddr = (Ipv4Address)obj;
         retVal = Arrays.equals(this.addr, ipAddr.addr);
      }

      return retVal;
   }

   public final void print() {
      System.out
         .println(
            ((StringBuffer)(new Object("Ipv4address: ")))
               .append(255 & this.addr[0])
               .append(".")
               .append(255 & this.addr[1])
               .append(".")
               .append(255 & this.addr[2])
               .append(".")
               .append(255 & this.addr[3])
               .toString()
         );
   }
}
