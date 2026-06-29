package net.rim.ecmascript.runtime;

class ESDatePrototype$43 extends ESDatePrototype$ESDatePutFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$43(ESDatePrototype _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   void put() {
      super._t = ESDatePrototype.localTime(super._t);
      super._p1 = ESDatePrototype.monthFromTime(super._t);
      super._p2 = ESDatePrototype.dateFromTime(super._t);
      if (ESDatePrototype.finite(super._p0)) {
         int y = (int)((long)super._p0);
         if (y >= 0 && y <= 99) {
            y += 1900;
         }

         super._p0 = y;
      }

      this.setDate(super._p0, super._p1, super._p2, ESDatePrototype.timeWithinDay(super._t));
   }
}
