package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

class ESMDSArrayPrototype$4 extends HostFunction {
   private final ESMDSArrayPrototype this$0;

   ESMDSArrayPrototype$4(ESMDSArrayPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      int numParams = this.getNumParms();
      ESMDSArray thiz = (ESMDSArray)this.getThis();
      Object value = thiz.getValue();
      if (numParams != 0) {
         EcmaUtilities.throwESError(thiz.getId(), RuntimeResources.getString(76, "Array.pop()"));
      }

      switch (thiz.getType()) {
         case 32767:
         case 32775:
            break;
         case 32768:
         case 32769:
         case 32773:
         default:
            IntVector iV = (IntVector)value;
            iV.removeElement(iV.lastElement());
            break;
         case 32770:
            DoubleVector dV = (DoubleVector)value;
            dV.removeElement(dV.lastElement());
            break;
         case 32771:
            Vector v = (Vector)value;
            v.removeElement(v.lastElement());
            break;
         case 32772:
         case 32774:
         case 32776:
            LongVector lV = (LongVector)value;
            lV.removeElement(lV.lastElement());
      }

      if (thiz.getControl() != null) {
         thiz.getControl().setValue(value, false);
      }

      return Value.NULL;
   }
}
