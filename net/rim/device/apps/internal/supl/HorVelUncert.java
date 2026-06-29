package net.rim.device.apps.internal.supl;

final class HorVelUncert extends Velocity {
   private short uncertSpeed;
   static final byte UNCERT_SPEED_BIT_SIZE;

   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      super.bearing = (short)nib.getBitsLarge(9);
      super.horSpeed = nib.getBitsLarge(16);
      this.uncertSpeed = (short)nib.getBitsLarge(8);
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(2, 2);
      stuff.putBit(false);
      stuff.putBits(super.bearing, 9);
      stuff.putBits(super.horSpeed, 16);
      stuff.putBits(this.uncertSpeed, 8);
   }

   @Override
   final void print() {
      System.out.println("Hor Velocity Uncertainty: ");
      System.out.println(((StringBuffer)(new Object("Bearing: "))).append(super.bearing).toString());
      System.out.println(((StringBuffer)(new Object("Hor Speed: "))).append(super.horSpeed).toString());
      System.out.println(((StringBuffer)(new Object("Uncert Speed: "))).append(this.uncertSpeed).toString());
   }
}
