package net.rim.device.apps.internal.supl;

final class AltitudeDirection {
   private byte direction;
   static final byte HEIGHT = 0;
   static final byte DEPTH = 1;

   final void decode(Nibbler nib) {
      this.direction = (byte)nib.getBitsLarge(1);
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.direction, 1);
   }

   final void print() {
      System.out.println(this.direction == 0 ? "Height" : "Depth");
   }
}
