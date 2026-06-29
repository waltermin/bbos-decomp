package net.rim.device.apps.internal.supl;

final class LatitudeSign {
   private byte latSign;
   static final byte NORTH;
   static final byte SOUTH;

   final void decode(Nibbler nib) {
      this.latSign = (byte)nib.getBitsLarge(1);
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.latSign, 1);
   }
}
