package net.rim.device.apps.internal.supl;

final class FrequencyInfoFDD extends FrequencyInfo {
   private short uarfcnUl;
   private short uarfcnDl;
   private byte optionals;
   static final byte NUM_OPT_ELEMENTS;
   static final byte FREQ_INFO_FDD_OPT_UARFCN_UL;

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
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      if ((this.optionals & 1) == 1) {
         System.out.println(((StringBuffer)(new Object("UARFCN UL: "))).append(this.uarfcnUl).toString());
      }

      System.out.println(((StringBuffer)(new Object("UARFCN DL: "))).append(this.uarfcnDl).toString());
   }
}
