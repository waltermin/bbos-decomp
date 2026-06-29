package net.rim.device.apps.internal.supl;

final class AltitudeInfo {
   private AltitudeDirection altitudeDirection;
   private short altitude;
   private byte altUncert;
   static final byte ALTITUDE_BIT_SIZE;
   static final byte ALT_UNCERT_BIT_SIZE;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.altitudeDirection = new AltitudeDirection();
      this.altitudeDirection.decode(nib);
      this.altitude = (short)nib.getBitsLarge(15);
      this.altUncert = (byte)nib.getBitsLarge(7);
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      this.altitudeDirection.encode(stuff);
      stuff.putBits(this.altitude, 15);
      stuff.putBits(this.altUncert, 7);
   }

   final void print() {
      System.out.println("Altitude Info: ");
      this.altitudeDirection.print();
      System.out.println(((StringBuffer)(new Object("Altitude: "))).append(this.altitude).toString());
      System.out.println(((StringBuffer)(new Object("Altitude Uncertainty: "))).append(this.altUncert).toString());
   }
}
