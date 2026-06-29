package net.rim.ecmascript.runtime;

class ESDatePrototype$3 extends ESDatePrototype$ESDateToString {
   private final ESDatePrototype this$0;

   ESDatePrototype$3(ESDatePrototype _1, String x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   String makeString(long time) {
      return ESDatePrototype.toUTCString(time);
   }
}
