package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class Mdn implements SetId {
   private byte[] mdn = new byte[8];
   static final byte MDN_SIZE = 8;
   static final byte OCTET_BIT_SIZE = 8;

   @Override
   public final void decode(Nibbler nib) {
      for (int i = 0; i < 8; i++) {
         this.mdn[i] = nib.getByte();
      }
   }

   @Override
   public final void encode(Stuffer stuff) {
      stuff.putBits(1, 3);

      for (int i = 0; i < 8; i++) {
         stuff.putBits(this.mdn[i], 8);
      }
   }

   public final boolean equals(SetId setId) {
      boolean retVal = false;
      if (setId instanceof Mdn) {
         Mdn mdn = (Mdn)setId;
         retVal = Arrays.equals(this.mdn, mdn.mdn);
      }

      return retVal;
   }

   final void print() {
      System.out.println("Mdn: ");

      for (int i = 0; i < 8; i++) {
         System.out.print(Integer.toHexString(255 & this.mdn[i]) + " ");
      }

      System.out.print("\n");
   }
}
