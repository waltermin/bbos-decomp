package net.rim.ecmascript.runtime;

class ESDatePrototype$12 extends ESDatePrototype$ESDateFetchFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$12(ESDatePrototype _1, String x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   long fetch() {
      int year = ESDatePrototype.yearFromTime(ESDatePrototype.localTime(super._t));
      switch (this.getVersion()) {
         case 0:
         case 100:
         case 110:
         case 120:
            if (year >= 1900 && year < 2000) {
               year -= 1900;
            }
            break;
         default:
            year -= 1900;
      }

      return Value.makeIntegerValue(year);
   }
}
