package net.rim.device.apps.internal.supl;

final class SuplEnd extends UlpMessage {
   private Position position;
   private StatusCode statusCode;
   private char[] ver = new char[8];
   byte optionals;
   static final byte SUPL_END_OPT_POSITION = 4;
   static final byte SUPL_END_OPT_STATUSCODE = 2;
   static final byte SUPL_END_OPT_VER = 1;
   static final byte NUM_OPT_ELEMENTS = 3;
   static final byte MAX_VER_SIZE = 8;
   static final byte MAX_VER_BIT_SIZE = 64;

   SuplEnd() {
      this.optionals = 0;
   }

   SuplEnd(byte status) {
      this.optionals = 2;
      this.statusCode = new StatusCode(status);
   }

   SuplEnd(byte status, byte[] ver) {
      this.optionals = 3;
      this.statusCode = new StatusCode(status);

      for (int i = 0; i < 8; i++) {
         this.ver[i] = (char)(0xFF & (char)ver[i]);
      }
   }

   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      this.optionals = (byte)nib.getBitsLarge(3);
      if (Nibbler.IsBitSet(this.optionals, (byte)4)) {
         this.position = new Position();
         this.position.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         this.statusCode = new StatusCode();
         this.statusCode.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         for (int i = 0; i < 8; i++) {
            this.ver[i] = (char)(0xFF & (char)nib.getByte());
         }
      }
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(5, 3);
      stuff.putBit(false);
      stuff.putBits(this.optionals, 3);
      if (Nibbler.IsBitSet(this.optionals, (byte)4)) {
         this.position.encode(stuff);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         this.statusCode.encode(stuff);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         for (int i = 0; i < 8; i++) {
            stuff.putBits(0xFF & this.ver[i], 8);
         }
      }
   }

   @Override
   final void print() {
      System.out.println("SUPL END: ");
      System.out.println("Optionals: " + this.optionals);
      if ((this.optionals & 4) == 4) {
         this.position.print();
      }

      if ((this.optionals & 2) == 2) {
         this.statusCode.print();
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Ver: ");

         for (int i = 0; i < 8; i++) {
            System.out.print(Integer.toHexString(255 & this.ver[i]) + " ");
         }

         System.out.print("\n");
      }
   }
}
