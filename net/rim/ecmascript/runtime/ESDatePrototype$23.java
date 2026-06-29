package net.rim.ecmascript.runtime;

class ESDatePrototype$23 extends ESDatePrototype$ESDateFetchFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$23(ESDatePrototype _1, String x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   long fetch() {
      return Value.makeIntegerValue(ESDatePrototype.minuteFromTime(super._t));
   }
}
