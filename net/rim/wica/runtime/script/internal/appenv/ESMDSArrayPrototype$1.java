package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.handler.CmpFieldHandler;
import net.rim.wica.runtime.script.internal.handler.DateFieldHandler;
import net.rim.wica.runtime.script.internal.handler.EnumFieldHandler;
import net.rim.wica.runtime.script.internal.handler.StringFieldHandler;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

class ESMDSArrayPrototype$1 extends HostFunction {
   private final ESMDSArrayPrototype this$0;

   ESMDSArrayPrototype$1(ESMDSArrayPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESMDSArray thiz = (ESMDSArray)this.getThis();
      boolean result = false;
      Object value = thiz.getValue();
      if (this.getNumParms() != 1) {
         EcmaUtilities.throwESError(
            thiz.getId(), RuntimeResources.getString(135, ((StringBuffer)(new Object())).append(thiz.getObjectClass()).append(".contains()").toString())
         );
      } else {
         long param = this.getParm(0);
         if (param == Value.NULL) {
            return Value.makeBooleanValue(result);
         }

         switch (thiz.getType()) {
            case 32767:
            case 32775:
               break;
            case 32768:
            default:
               result = ((IntVector)value).contains(Convert.toBoolean(param) ? 1 : 0);
               break;
            case 32769:
               result = ((IntVector)value).contains(Convert.toInt32(param));
               break;
            case 32770:
               result = ((DoubleVector)value).contains(Convert.toDouble(param));
               break;
            case 32771:
               result = ((Vector)value).contains(StringFieldHandler.getValue(param));
               break;
            case 32772:
               result = ((LongVector)value).contains(DateFieldHandler.getValue(param));
               break;
            case 32773:
               result = ((IntVector)value).contains(EnumFieldHandler.getValue(thiz.getEnumId(), param));
               break;
            case 32774:
               result = ((LongVector)value).contains(CmpFieldHandler.getValue(thiz.getCollection().getDef().getId(), param));
               break;
            case 32776:
               result = ((LongVector)value).contains((long)Convert.toDouble(param));
         }
      }

      return Value.makeBooleanValue(result);
   }
}
