package net.rim.device.apps.internal.supl;

final class HorAndVerUncert extends Velocity {
   private byte verDirect;
   private short verSpeed;
   private short horUncertSpeed;
   private short verUncertSpeed;
   static final byte VER_DIRECT_BIT_SIZE = 1;
   static final byte VER_SPEED_BIT_SIZE = 8;
   static final byte HOR_UNCERT_SPEED_BIT_SIZE = 8;
   static final byte VER_UNCERT_SPEED_BIT_SIZE = 8;

   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      this.verDirect = (byte)nib.getBitsLarge(1);
      super.bearing = (short)nib.getBitsLarge(9);
      super.horSpeed = nib.getBitsLarge(16);
      this.verSpeed = (short)nib.getBitsLarge(8);
      this.horUncertSpeed = (short)nib.getBitsLarge(8);
      this.verUncertSpeed = (short)nib.getBitsLarge(8);
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(3, 2);
      stuff.putBit(false);
      stuff.putBits(this.verDirect, 1);
      stuff.putBits(super.bearing, 9);
      stuff.putBits(super.horSpeed, 16);
      stuff.putBits(this.verSpeed, 8);
      stuff.putBits(this.horUncertSpeed, 8);
      stuff.putBits(this.verUncertSpeed, 8);
   }

   @Override
   final void print() {
      System.out.println("Hor and Ver Velocity Uncertainty: ");
      System.out.println(((StringBuffer)(new Object("Ver Direct: "))).append(this.verDirect).toString());
      System.out.println(((StringBuffer)(new Object("Bearing: "))).append(super.bearing).toString());
      System.out.println(((StringBuffer)(new Object("Hor Speed: "))).append(super.horSpeed).toString());
      System.out.println(((StringBuffer)(new Object("Ver Speed: "))).append(this.verSpeed).toString());
      System.out.println(((StringBuffer)(new Object("Hor Uncert Speed: "))).append(this.horUncertSpeed).toString());
      System.out.println(((StringBuffer)(new Object("Ver Uncert Speed: "))).append(this.verUncertSpeed).toString());
   }
}
