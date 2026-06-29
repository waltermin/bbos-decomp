package net.rim.ecmascript.runtime;

class ESArrayPrototype$4 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$4(ESArrayPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      long indexHalf = ESMath.floorDivide(length, 2);
      long indexLower = 0;

      for (long indexUpper = length - 1; indexLower != indexHalf; indexUpper -= 1) {
         if (thiz.hasIndex(indexLower)) {
            long vLower = thiz.getIndex(indexLower);
            if (thiz.hasIndex(indexUpper)) {
               thiz.putIndex(indexLower, thiz.getIndex(indexUpper));
            } else {
               thiz.deleteIndex(indexLower);
            }

            thiz.putIndex(indexUpper, vLower);
         } else if (thiz.hasIndex(indexUpper)) {
            thiz.putIndex(indexLower, thiz.getIndex(indexUpper));
            thiz.deleteIndex(indexUpper);
         }

         indexLower += 1;
      }

      return Value.makeObjectValue(thiz);
   }
}
