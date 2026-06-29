package net.rim.ecmascript.runtime;

class ESDatePrototype$31 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$31(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      this.setUTCDate(
         ESDatePrototype.hourFromTime(super._t),
         ESDatePrototype.minuteFromTime(super._t),
         ESDatePrototype.secFromTime(super._t),
         super._p0,
         ESDatePrototype.day(super._t)
      );
   }
}
