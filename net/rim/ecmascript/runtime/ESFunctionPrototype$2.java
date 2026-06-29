package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESFunctionPrototype$2 extends HostFunction {
   private final ESFunctionPrototype this$0;

   ESFunctionPrototype$2(ESFunctionPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      Object savedState = this.startRecurse();
      boolean var10 = false /* VF: Semaphore variable */;

      long var6;
      try {
         var10 = true;
         int length = this.getNumParms() - 1;
         if (length < 0) {
            length = 0;
         }

         long[] callParms = Misc.newMixedArray(length);

         for (int i = 0; i < length; i++) {
            callParms[i] = this.getParm(i + 1);
         }

         long rc = ESFunctionPrototype.callFunction(this.getGlobalInstance(), this, callParms);
         Misc.freeMixedArray(callParms);
         var6 = rc;
         var10 = false;
      } finally {
         if (var10) {
            this.endRecurse(savedState);
         }
      }

      this.endRecurse(savedState);
      return var6;
   }
}
