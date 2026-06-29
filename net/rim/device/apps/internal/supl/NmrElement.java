package net.rim.device.apps.internal.supl;

final class NmrElement {
   private short arfcn;
   private byte bsic;
   private byte rxLev;
   static final byte ARFCN_BIT_SIZE;
   static final byte BSIC_BIT_SIZE;
   static final byte RXLEV_BIT_SIZE;

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.arfcn, 10);
      stuff.putBits(this.bsic, 6);
      stuff.putBits(this.rxLev, 6);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("Arfcn: "))).append(this.arfcn).toString());
      System.out.println(((StringBuffer)(new Object("Bsic: "))).append(this.bsic).toString());
      System.out.println(((StringBuffer)(new Object("RxLev: "))).append(this.rxLev).toString());
   }
}
