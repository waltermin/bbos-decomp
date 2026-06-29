package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;

class ESDataPrototype$2 extends HostFunction {
   private final ESDataPrototype this$0;

   ESDataPrototype$2(ESDataPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESData thiz = (ESData)this.getThis();
      DataCollection collection = thiz.getCollection();
      long handle = thiz.getHandle();
      long newHandle = -1;
      if (collection instanceof KeyDataCollection) {
         EcmaUtilities.throwESError(thiz.getId(), RuntimeResources.getString(102, thiz.getId()));
      } else {
         newHandle = collection.create();
         collection.copyFields(newHandle, handle, true);
      }

      return newHandle == -1 ? Value.NULL : this.this$0._context.createDataInstance(collection, newHandle);
   }
}
