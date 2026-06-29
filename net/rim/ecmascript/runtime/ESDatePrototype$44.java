package net.rim.ecmascript.runtime;

class ESDatePrototype$44 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$44(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      if (super._nParms < 2) {
         super._p1 = ESDatePrototype.monthFromTime(super._t);
      }

      if (super._nParms < 3) {
         super._p2 = ESDatePrototype.dateFromTime(super._t);
      }

      this.setUTCDate(super._p0, super._p1, super._p2, ESDatePrototype.timeWithinDay(super._t));
   }
}
