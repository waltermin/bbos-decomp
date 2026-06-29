package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.util.SerializationUtilities;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

class ESMDSArrayPrototype$5 extends HostFunction {
   private final ESMDSArrayPrototype this$0;

   ESMDSArrayPrototype$5(ESMDSArrayPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESMDSArray thiz = (ESMDSArray)this.getThis();
      StringBuffer buffer = new StringBuffer();
      Object value = thiz.getValue();
      switch (thiz.getType()) {
         case 32767:
         case 32775:
            break;
         case 32768:
         default:
            SerializationUtilities.serializeBooleanArray(buffer, (IntVector)value);
            break;
         case 32769:
            SerializationUtilities.serializeIntArray(buffer, (IntVector)value);
            break;
         case 32770:
            SerializationUtilities.serializeDoubleArray(buffer, (DoubleVector)value);
            break;
         case 32771:
            SerializationUtilities.serializeStringArray(buffer, (Vector)value);
            break;
         case 32772:
            SerializationUtilities.serializeDateArray(buffer, (LongVector)value);
            break;
         case 32773:
            SerializationUtilities.serializeEnumArray(buffer, thiz.getContext().getWiclet(), thiz.getEnumId(), (IntVector)value);
            break;
         case 32774:
            SerializationUtilities.serializeObjectArray(buffer, thiz.getContext().getWiclet(), thiz.getCollection(), (LongVector)value);
            break;
         case 32776:
            SerializationUtilities.serializeLongArray(buffer, (LongVector)value);
      }

      return Value.makeStringValue(buffer.toString());
   }
}
