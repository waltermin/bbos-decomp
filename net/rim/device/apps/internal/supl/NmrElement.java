package net.rim.device.apps.internal.supl;

final class NmrElement {
   private short arfcn;
   private byte bsic;
   private byte rxLev;
   static final byte ARFCN_BIT_SIZE = 10;
   static final byte BSIC_BIT_SIZE = 6;
   static final byte RXLEV_BIT_SIZE = 6;

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.arfcn, 10);
      stuff.putBits(this.bsic, 6);
      stuff.putBits(this.rxLev, 6);
   }

   final void print() {
      System.out.println("Arfcn: " + this.arfcn);
      System.out.println("Bsic: " + this.bsic);
      System.out.println("RxLev: " + this.rxLev);
   }
}
