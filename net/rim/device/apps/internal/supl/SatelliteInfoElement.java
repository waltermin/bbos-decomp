package net.rim.device.apps.internal.supl;

final class SatelliteInfoElement {
   byte satId;
   short iODE;
   static final byte SAT_ID_BIT_SIZE = 6;
   static final byte IODE_BIT_SIZE = 8;

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.satId, 6);
      stuff.putBits(this.iODE, 8);
   }

   final void print() {
      System.out.println("Sat Id: " + this.satId);
      System.out.println("IODE: " + this.iODE);
   }
}
