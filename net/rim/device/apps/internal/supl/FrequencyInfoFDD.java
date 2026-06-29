package net.rim.device.apps.internal.supl;

final class FrequencyInfoFDD extends FrequencyInfo {
   private short uarfcnUl;
   private short uarfcnDl;
   private byte optionals;
   static final byte NUM_OPT_ELEMENTS = 1;
   static final byte FREQ_INFO_FDD_OPT_UARFCN_UL = 1;

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBit(false);
      stuff.putBits(0, 1);
      stuff.putBit(false);
      stuff.putBits(this.optionals, 1);
      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.uarfcnUl, 14);
      }

      stuff.putBits(this.uarfcnDl, 14);
   }

   @Override
   final void print() {
      System.out.println("Frequency Info FDD: ");
      System.out.println("Optionals: " + this.optionals);
      if ((this.optionals & 1) == 1) {
         System.out.println("UARFCN UL: " + this.uarfcnUl);
      }

      System.out.println("UARFCN DL: " + this.uarfcnDl);
   }
}
