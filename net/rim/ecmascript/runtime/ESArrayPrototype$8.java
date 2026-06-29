package net.rim.ecmascript.runtime;

class ESArrayPrototype$8 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$8(ESArrayPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESArray a = new ESArray();
      long result = Value.makeObjectValue(a);
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      long start = ESArrayPrototype.bound(Convert.toInteger(this.getParm(0)), length);
      long deleteCount = Convert.toInteger(this.getParm(1));
      if (deleteCount < 0) {
         deleteCount = 0;
      } else if (deleteCount > length - start) {
         deleteCount = length - start;
      }

      if (this.getVersion() == 120) {
         if (deleteCount == 0) {
            result = Value.UNDEFINED;
         } else if (deleteCount == 1) {
            result = thiz.getIndex(start);
         }
      }

      for (int k = 0; k < deleteCount; k++) {
         if (thiz.hasIndex(start + k)) {
            a.putIndex(k, thiz.getIndex(start + k));
         }
      }

      a.putField("length", Value.makeLongValue(deleteCount));
      int nParms = this.getNumParms() - 2;
      if (nParms < 0) {
         nParms = 0;
      }

      if (nParms > deleteCount) {
         long k = length - deleteCount;
         long x1 = k + deleteCount - 1;

         for (long x2 = k + nParms - 1; k > start; x2 -= 1) {
            if (thiz.hasIndex(x1)) {
               thiz.putIndex(x2, thiz.getIndex(x1));
            } else {
               thiz.deleteIndex(x2);
            }

            k -= 1;
            x1 -= 1;
         }
      } else if (nParms < deleteCount) {
         long k = start;
         long x1 = k + deleteCount;
         long x2 = k + nParms;

         for (long end = length - deleteCount; k < end; x2 += 1) {
            if (thiz.hasIndex(x1)) {
               thiz.putIndex(x2, thiz.getIndex(x1));
            } else {
               thiz.deleteIndex(x2);
            }

            k += 1;
            x1 += 1;
         }

         k = length - 1;

         for (long var27 = k - deleteCount + nParms; k > var27; k -= 1) {
            thiz.deleteIndex(k);
         }
      }

      long k = start;

      for (int i = 0; i < nParms; i++) {
         thiz.putIndex(k, this.getParm(i + 2));
         k += 1;
      }

      thiz.putField("length", length - deleteCount + nParms);
      return result;
   }
}
