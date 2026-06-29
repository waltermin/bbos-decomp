package net.rim.device.apps.internal.supl;

final class Mac {
   private byte[] mac = new byte[8];
   static final byte MAC_BIT_SIZE = 64;
   static final byte MAC_OCTET_SIZE = 8;

   final void decode(Nibbler nib) {
      for (int i = 0; i < 8; i++) {
         this.mac[i] = nib.getByte();
      }
   }

   final void print() {
      System.out.println("Mac: ");

      for (int i = 0; i < 8; i++) {
         System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.mac[i])).append(" ").toString());
      }

      System.out.print("\n");
   }
}
