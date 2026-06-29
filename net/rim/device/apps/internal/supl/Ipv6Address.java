package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class Ipv6Address extends IpAddress {
   private short[] addr = new short[16];
   static final byte IP_V6_ADDR_SIZE;
   static final byte OCTET_BIT_SIZE;

   @Override
   public final void decode(Nibbler nib) {
      for (int i = 0; i < 16; i++) {
         this.addr[i] = (short)(0xFF & nib.getByte());
      }
   }

   @Override
   public final void encode(Stuffer stuff) {
      stuff.putBits(1, 1);

      for (int i = 0; i < 16; i++) {
         stuff.putBits(this.addr[i], 8);
      }
   }

   @Override
   public final boolean equals(Object obj) {
      boolean retVal = false;
      if (obj instanceof Ipv6Address) {
         Ipv6Address ipAddr = (Ipv6Address)obj;
         retVal = Arrays.equals(this.addr, ipAddr.addr);
      }

      return retVal;
   }

   public final void print() {
      System.out.println("Ipv6address: ");

      for (int i = 0; i < 16; i++) {
         System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.addr[i])).append(" ").toString());
      }

      System.out.print("\n");
   }
}
