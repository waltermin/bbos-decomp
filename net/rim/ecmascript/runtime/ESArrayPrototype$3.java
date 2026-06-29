package net.rim.ecmascript.runtime;

class ESArrayPrototype$3 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$3(ESArrayPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int parm = 0;
      int nParms = this.getNumParms();
      ESArray a = new ESArray();
      long e = Value.makeObjectValue(this.getThis());

      while (true) {
         ESArray eArray = Value.checkIfArrayValue(e);
         if (eArray != null) {
            long eLength = eArray.getArrayLength();

            for (long k = 0; k < eLength; k += 1) {
               if (eArray.hasOwnIndex(k)) {
                  a.append(eArray.getIndex(k));
               }
            }
         } else {
            a.append(e);
         }

         if (parm == nParms) {
            return Value.makeObjectValue(a);
         }

         e = this.getParm(parm++);
      }
   }
}
