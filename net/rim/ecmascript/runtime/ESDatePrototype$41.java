package net.rim.ecmascript.runtime;

class ESDatePrototype$41 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$41(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      if (super._nParms < 2) {
         super._p1 = ESDatePrototype.dateFromTime(super._t);
      }

      this.setUTCDate(ESDatePrototype.yearFromTime(super._t), super._p0, super._p1, ESDatePrototype.timeWithinDay(super._t));
   }
}
