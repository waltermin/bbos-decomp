package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class Msisdn implements SetId {
   private char[] msisdn = new char[8];
   static final byte MSISDN_SIZE = 8;
   static final byte OCTET_BIT_SIZE = 8;

   @Override
   public final void decode(Nibbler nib) {
      for (int i = 0; i < 8; i++) {
         this.msisdn[i] = (char)(0xFF & (char)nib.getByte());
      }
   }

   @Override
   public final void encode(Stuffer stuff) {
      stuff.putBits(0, 3);

      for (int i = 0; i < 8; i++) {
         stuff.putBits(this.msisdn[i], 8);
      }
   }

   public final boolean equals(SetId setId) {
      boolean retVal = false;
      if (setId instanceof Msisdn) {
         Msisdn msisdn = (Msisdn)setId;
         retVal = Arrays.equals(this.msisdn, msisdn.msisdn);
      }

      return retVal;
   }

   final void print() {
      System.out.println("Msisdn: ");

      for (int i = 0; i < 8; i++) {
         System.out.print(Integer.toHexString(255 & this.msisdn[i]) + " ");
      }

      System.out.print("\n");
   }
}
