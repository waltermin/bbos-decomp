package net.rim.device.apps.internal.supl;

final class QOP {
   private byte horAcc;
   private byte verAcc;
   int maxLocAge;
   byte delay;
   byte optionals;
   static final byte QOP_MASK_VERACC = 4;
   static final byte QOP_MASK_MAXLOCAGE = 2;
   static final byte QOP_MASK_DELAY = 1;
   static final byte QOP_OPTIONALS_BIT_SIZE = 3;
   static final byte QOP_HORACC_BIT_SIZE = 7;
   static final byte QOP_VERACC_BIT_SIZE = 7;
   static final byte QOP_MAXLOCAGE_BIT_SIZE = 16;
   static final byte QOP_DELAY_BIT_SIZE = 3;

   final void decode(Nibbler nib) {
      if (nib.getBit()) {
         System.out.println("ext bit set??");
      }

      this.optionals = (byte)nib.getBitsLarge(3);
      this.horAcc = (byte)nib.getBitsLarge(7);
      if (Nibbler.IsBitSet(this.optionals, (byte)4)) {
         this.verAcc = (byte)nib.getBitsLarge(7);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         this.maxLocAge = (short)nib.getBitsLarge(16);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         this.delay = (byte)nib.getBitsLarge(3);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.optionals, 3);
      stuff.putBits(this.horAcc, 7);
      if ((this.optionals & 4) == 4) {
         stuff.putBits(this.verAcc, 7);
      }

      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.maxLocAge, 16);
      }

      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.delay, 3);
      }
   }

   final double getHorizontalAccuracy() {
      double C = (double)4621819117588971520L;
      double X = (double)4607632778762754458L;
      double uncertainty = (double)4607182418800017408L;
      if (this.horAcc == 0) {
         return (double)0L;
      }

      for (int i = 0; i < this.horAcc; i++) {
         uncertainty *= 4607632778762754458L;
      }

      return 4621819117588971520L * (uncertainty - 4607182418800017408L);
   }

   final double getVerticalAccuracy() {
      double C = (double)4631530004285489152L;
      double X = (double)4607295008790701670L;
      double uncertainty = (double)4607182418800017408L;
      if (this.horAcc == 0) {
         return (double)0L;
      }

      for (int i = 0; i < this.horAcc; i++) {
         uncertainty *= 4607295008790701670L;
      }

      return 4631530004285489152L * (uncertainty - 4607182418800017408L);
   }

   final void print() {
      System.out.println("QOP: ");
      System.out.println("Optionals: " + this.optionals);
      System.out.println("HorAcc: " + this.horAcc);
      if ((this.optionals & 4) == 4) {
         System.out.println("VerAcc: " + this.verAcc);
      }

      if ((this.optionals & 2) == 2) {
         System.out.println("MaxLocAge: " + this.maxLocAge);
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Delay: " + this.delay);
      }
   }
}
