package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class Imsi implements SetId {
   private char[] imsi = new char[8];
   static final byte IMSI_SIZE = 8;
   static final byte OCTET_BIT_SIZE = 8;

   Imsi() {
   }

   Imsi(char[] imsi) {
      if (imsi.length > 8) {
         throw new Object();
      }

      for (int i = 0; i < 8; i++) {
         this.imsi[i] = imsi[i];
      }
   }

   @Override
   public final void decode(Nibbler nib) {
      for (int i = 0; i < 8; i++) {
         this.imsi[i] = (char)(0xFF & (char)nib.getByte());
      }
   }

   @Override
   public final void encode(Stuffer stuff) {
      stuff.putBits(3, 3);

      for (int i = 0; i < 8; i++) {
         stuff.putBits(this.imsi[i], 8);
      }
   }

   public final boolean equals(SetId setId) {
      boolean retVal = false;
      if (setId instanceof Imsi) {
         Imsi imsi = (Imsi)setId;
         retVal = Arrays.equals(this.imsi, imsi.imsi);
      }

      return retVal;
   }

   final void print() {
      System.out.println("Imsi: ");

      for (int i = 0; i < 8; i++) {
         System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.imsi[i])).append(" ").toString());
      }

      System.out.print("\n");
   }
}
