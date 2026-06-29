package net.rim.device.apps.internal.supl;

final class HorAndVerVel extends Velocity {
   private byte verDirect;
   private short verSpeed;
   static final byte VER_DIRECT_BIT_SIZE = 1;
   static final byte VER_SPEED_BIT_SIZE = 8;

   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      this.verDirect = (byte)nib.getBitsLarge(1);
      super.bearing = (short)nib.getBitsLarge(9);
      super.horSpeed = nib.getBitsLarge(16);
      this.verSpeed = (short)nib.getBitsLarge(8);
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(1, 2);
      stuff.putBit(false);
      stuff.putBits(this.verDirect, 1);
      stuff.putBits(super.bearing, 9);
      stuff.putBits(super.horSpeed, 16);
      stuff.putBits(this.verSpeed, 8);
   }

   @Override
   final void print() {
      System.out.println("Hor and Ver Velocity: ");
      System.out.println("Ver Direct: " + this.verDirect);
      System.out.println("Bearing: " + super.bearing);
      System.out.println("Hor Speed: " + super.horSpeed);
      System.out.println("Ver Speed: " + this.verSpeed);
   }
}
