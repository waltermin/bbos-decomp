package net.rim.device.apps.internal.supl;

final class LatitudeSign {
   private byte latSign;
   static final byte NORTH = 0;
   static final byte SOUTH = 1;

   final void decode(Nibbler nib) {
      this.latSign = (byte)nib.getBitsLarge(1);
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.latSign, 1);
   }
}
