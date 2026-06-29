package net.rim.device.apps.internal.supl;

final class SuplResponse extends UlpMessage {
   PosMethod posMethod;
   private SlpAddress slpAddress;
   private SetAuthKey authKey;
   private byte[] keyIdentity4 = new byte[16];
   private byte optionals;
   static final byte SUPL_RESP_OPT_SLP_ADDR = 4;
   static final byte SUPL_RESP_OPT_SET_AUTH_KEY = 2;
   static final byte SUPL_RESP_OPT_KEY_ID_4 = 1;
   static final byte NUM_OPT_ELEMENTS = 3;
   static final byte MAX_KEY_IDENTITY4_SIZE = 16;

   @Override
   final void decode(Nibbler nib) {
      boolean decodeExt = nib.getBit();
      this.optionals = (byte)nib.getBitsLarge(3);
      if (!nib.getBit()) {
         this.posMethod = new PosMethod();
         this.posMethod.decode(nib);
      }

      if ((this.optionals & 4) == 4) {
         this.slpAddress = SlpAddressFactory.DecodeChoiceIndex(nib);
         if (this.slpAddress != null) {
            this.slpAddress.decode(nib);
         }
      }

      if ((this.optionals & 2) == 2) {
         this.authKey = new SetAuthKey();
         this.authKey.decode(nib);
      }

      if ((this.optionals & 1) == 1) {
         for (byte i = 0; i < 16; i++) {
            this.keyIdentity4[i] = nib.getByte();
         }
      }

      if (decodeExt) {
      }
   }

   @Override
   final void encode(Stuffer stuff) {
   }

   @Override
   final void print() {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }
}
