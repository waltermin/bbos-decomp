package net.rim.ecmascript.runtime;

class ESDatePrototype$36 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$36(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      super._t = ESDatePrototype.localTime(super._t);
      if (super._nParms < 2) {
         super._p1 = ESDatePrototype.minuteFromTime(super._t);
      }

      if (super._nParms < 3) {
         super._p2 = ESDatePrototype.secFromTime(super._t);
      }

      if (super._nParms < 4) {
         super._p3 = ESDatePrototype.msFromTime(super._t);
      }

      this.setDate(super._p0, super._p1, super._p2, super._p3, ESDatePrototype.day(super._t));
   }
}
