package net.rim.ecmascript.runtime;

class ESDatePrototype$28 extends ESDatePrototype$ESDateFetchFunction {
   private final ESDatePrototype this$0;

   ESDatePrototype$28(ESDatePrototype _1, String x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   long fetch() {
      return Value.makeIntegerValue((int)((super._t - ESDatePrototype.localTime(super._t)) / 60000));
   }
}
