package net.rim.ecmascript.runtime;

class ESDatePrototype$38 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$38(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      super._t = ESDatePrototype.localTime(super._t);
      this.setDate(ESDatePrototype.yearFromTime(super._t), ESDatePrototype.monthFromTime(super._t), super._p0, ESDatePrototype.timeWithinDay(super._t));
   }
}
