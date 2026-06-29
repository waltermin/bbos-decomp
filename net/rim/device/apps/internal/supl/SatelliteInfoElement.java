package net.rim.device.apps.internal.supl;

final class SatelliteInfoElement {
   byte satId;
   short iODE;
   static final byte SAT_ID_BIT_SIZE;
   static final byte IODE_BIT_SIZE;

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.satId, 6);
      stuff.putBits(this.iODE, 8);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("Sat Id: "))).append(this.satId).toString());
      System.out.println(((StringBuffer)(new Object("IODE: "))).append(this.iODE).toString());
   }
}
