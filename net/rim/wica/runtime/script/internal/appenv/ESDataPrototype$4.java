package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.util.SerializationUtilities;

class ESDataPrototype$4 extends HostFunction {
   private final ESDataPrototype this$0;

   ESDataPrototype$4(ESDataPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESData thiz = (ESData)this.getThis();
      return Value.makeStringValue(
         SerializationUtilities.serializeObject(thiz.getId(), this.this$0._context.getWiclet(), thiz.getCollection(), thiz.getHandle())
      );
   }
}
