package net.rim.ecmascript.runtime;

import net.rim.device.api.util.Arrays;

class ESArrayPrototype$6 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$6(ESArrayPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      Object savedState = this.startRecurse();
      boolean var11 = false /* VF: Semaphore variable */;

      long var14;
      try {
         var11 = true;
         GlobalObject global = this.getGlobalInstance();
         ESObject thiz = this.getThis();
         long[] values = thiz.getArray();
         if (values != null && values.length >= 2) {
            ESFunction compareFn = Value.checkIfFunctionValue(this.getParm(0));
            ESArrayPrototype$SortComparator comparator = new ESArrayPrototype$SortComparator(this.this$0, global, compareFn, values);

            try {
               Arrays.sort(comparator.getIndexArray(), 0, values.length, comparator);
            } catch (ESArrayPrototype$SortException rte) {
               throw comparator.getThrownValue();
            }

            thiz.putArray(comparator.reorder());
         }

         var14 = Value.makeObjectValue(thiz);
         var11 = false;
      } finally {
         if (var11) {
            this.endRecurse(savedState);
         }
      }

      this.endRecurse(savedState);
      return var14;
   }
}
