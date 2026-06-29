package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.handler.DateFieldHandler;
import net.rim.wica.runtime.script.internal.handler.EnumFieldHandler;
import net.rim.wica.runtime.script.internal.handler.StringFieldHandler;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

class ESMDSArrayPrototype$2 extends HostFunction {
   private final ESMDSArrayPrototype this$0;

   ESMDSArrayPrototype$2(ESMDSArrayPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESMDSArray thiz = (ESMDSArray)this.getThis();
      Object value = thiz.getValue();
      int numParams = this.getNumParms();
      if (numParams <= 0) {
         EcmaUtilities.throwESError(
            thiz.getId(), RuntimeResources.getString(73, ((StringBuffer)(new Object())).append(thiz.getObjectClass()).append(".remove()").toString())
         );
      } else {
         for (int i = 0; i < numParams; i++) {
            switch (thiz.getType()) {
               case 32767:
               case 32775:
                  break;
               case 32768:
               default:
                  ((IntVector)value).removeElement(Convert.toBoolean(this.getParm(i)) ? 1 : 0);
                  break;
               case 32769:
                  ((IntVector)value).removeElement(Convert.toInt32(this.getParm(i)));
                  break;
               case 32770:
                  ((DoubleVector)value).removeElement(Convert.toDouble(this.getParm(i)));
                  break;
               case 32771:
                  ((Vector)value).removeElement(StringFieldHandler.getValue(this.getParm(i)));
                  break;
               case 32772:
                  ((LongVector)value).removeElement(DateFieldHandler.getValue(this.getParm(i)));
                  break;
               case 32773:
                  ((IntVector)value).removeElement(EnumFieldHandler.getValue(thiz.getEnumId(), this.getParm(i)));
                  break;
               case 32774:
                  thiz.removeDataElement(this.getParm(i));
                  break;
               case 32776:
                  ((LongVector)value).removeElement((long)Convert.toDouble(this.getParm(i)));
            }
         }

         if (thiz.getControl() != null) {
            thiz.getControl().setValue(value, false);
         }
      }

      return Value.NULL;
   }
}
