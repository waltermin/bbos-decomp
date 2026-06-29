package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;

class ESDataPrototype$1 extends HostFunction {
   private final ESDataPrototype this$0;

   ESDataPrototype$1(ESDataPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESData thiz = (ESData)this.getThis();
      if (this.getNumParms() != 0) {
         EcmaUtilities.throwESError(thiz.getId(), RuntimeResources.getString(76, this.getName() + "()"));
      }

      DataCollection collection = thiz.getCollection();
      long handle = thiz.getHandle();
      if (!(collection instanceof KeyDataCollection)) {
         EcmaUtilities.throwESError(thiz.getId(), RuntimeResources.getString(101, thiz.getId()));
      } else {
         ((KeyDataCollection)collection).remove(handle);
      }

      return Value.NULL;
   }
}
