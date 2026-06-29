package net.rim.device.apps.internal.supl;

final class KeyIdentity {
   private byte[] keyIdentity = new byte[16];
   static final short KEY_IDENTITY_BIT_SIZE = 128;
   static final short KEY_IDENTITY_OCTET_SIZE = 16;

   final void decode(Nibbler nib) {
      for (int i = 0; i < 16; i++) {
         this.keyIdentity[i] = nib.getByte();
      }
   }

   final void print() {
      System.out.println("Key Identity: ");

      for (int i = 0; i < 16; i++) {
         System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.keyIdentity[i])).append(" ").toString());
      }

      System.out.print("\n");
   }
}
