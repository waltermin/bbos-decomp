package net.rim.ecmascript.runtime;

class ESDatePrototype$34 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$34(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      super._t = ESDatePrototype.localTime(super._t);
      if (super._nParms < 2) {
         super._p1 = ESDatePrototype.secFromTime(super._t);
      }

      if (super._nParms < 3) {
         super._p2 = ESDatePrototype.msFromTime(super._t);
      }

      this.setDate(ESDatePrototype.hourFromTime(super._t), super._p0, super._p1, super._p2, ESDatePrototype.day(super._t));
   }
}
