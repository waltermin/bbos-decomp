package net.rim.device.apps.internal.supl;

final class FrequencyInfoTDD extends FrequencyInfo {
   private short uarfcnNt;

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBit(false);
      stuff.putBits(1, 1);
      stuff.putBit(false);
      stuff.putBits(this.uarfcnNt, 14);
   }

   @Override
   final void print() {
      System.out.println("Frequency Info TDD: ");
      System.out.println("UARFCN NT: " + this.uarfcnNt);
   }
}
