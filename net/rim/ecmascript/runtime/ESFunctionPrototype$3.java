package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

class ESFunctionPrototype$3 extends HostFunction {
   private final ESFunctionPrototype this$0;

   ESFunctionPrototype$3(ESFunctionPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      Object savedState = this.startRecurse();
      boolean var13 = false /* VF: Semaphore variable */;

      long argArray;
      label54: {
         long var9;
         try {
            var13 = true;
            long arrayValue = this.getParm(1);
            switch (Value.getType(arrayValue)) {
               case 1:
                  ESArray argArrayx = Value.checkIfArrayValue(arrayValue);
                  if (argArrayx == null) {
                     throw ThrownValue.typeError(Resources.getString(30));
                  }

                  int length = Convert.toInt32(argArrayx.getField("length"));
                  long[] callParms = Misc.newMixedArray(length);

                  for (int i = 0; i < length; i++) {
                     callParms[i] = argArrayx.getElement(Value.makeIntegerValue(i));
                  }

                  long rc = ESFunctionPrototype.callFunction(this.getGlobalInstance(), this, callParms);
                  Misc.freeMixedArray(callParms);
                  var9 = rc;
                  var13 = false;
                  break;
               case 2:
               case 3:
               default:
                  argArray = ESFunctionPrototype.callFunction(this.getGlobalInstance(), this, Names.NoParms);
                  var13 = false;
                  break label54;
            }
         } finally {
            if (var13) {
               this.endRecurse(savedState);
            }
         }

         this.endRecurse(savedState);
         return var9;
      }

      this.endRecurse(savedState);
      return argArray;
   }
}
