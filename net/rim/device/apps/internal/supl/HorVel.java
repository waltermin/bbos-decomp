package net.rim.device.apps.internal.supl;

final class HorVel extends Velocity {
   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      super.bearing = (short)nib.getBitsLarge(9);
      super.horSpeed = nib.getBitsLarge(16);
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(0, 2);
      stuff.putBit(false);
      stuff.putBits(super.bearing, 9);
      stuff.putBits(super.horSpeed, 16);
   }

   @Override
   final void print() {
      System.out.println("Hor Velocity: ");
      System.out.println("Bearing: " + super.bearing);
      System.out.println("Hor Speed: " + super.horSpeed);
   }
}
