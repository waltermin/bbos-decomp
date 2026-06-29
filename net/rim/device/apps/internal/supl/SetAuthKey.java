package net.rim.device.apps.internal.supl;

final class SetAuthKey {
   private byte index;
   private byte[] key = new byte[32];
   static final byte SHORT_KEY_INDEX;
   static final byte LONG_KEY_INDEX;
   static final byte MAX_SHORT_KEY_SIZE;
   static final byte MAX_LONG_KEY_SIZE;

   final void decode(Nibbler nib) {
      if (!nib.getBit()) {
         this.index = (byte)nib.getBitsLarge(1);
         byte length;
         if (this.index == 0) {
            length = 16;
         } else {
            if (this.index != 1) {
               return;
            }

            length = 32;
         }

         for (byte i = 0; i < length; i++) {
            this.key[i] = nib.getByte();
         }
      }
   }

   final void print() {
   }
}
