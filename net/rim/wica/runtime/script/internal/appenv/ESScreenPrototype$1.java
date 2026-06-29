package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;

class ESScreenPrototype$1 extends HostFunction {
   private final ESScreenPrototype this$0;

   ESScreenPrototype$1(ESScreenPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() throws ThrownValue {
      int numParams = this.getNumParms();
      ESScreen thiz = (ESScreen)this.getThis();
      if (numParams <= 0) {
         thiz.getScreenManager().display(thiz.getScreenModel());
      } else {
         long[] parms = new long[numParams];

         for (int i = 0; i < numParams; i++) {
            long ecmaParam = this.getParm(i);
            switch (Value.getType(ecmaParam)) {
               case 3:
                  parms[i] = -1;
                  break;
               case 6:
                  ESObject data = Convert.toObject(ecmaParam);
                  if (!(data instanceof ESData)) {
                     throw ThrownValue.typeError(RuntimeResources.getString(75, "display()"));
                  }

                  parms[i] = ((ESData)data).getHandle();
                  break;
               default:
                  throw ThrownValue.typeError(RuntimeResources.getString(75, "display()"));
            }
         }

         thiz.getScreenManager().display(thiz.getScreenModel(), parms);
      }

      return Value.NULL;
   }
}
