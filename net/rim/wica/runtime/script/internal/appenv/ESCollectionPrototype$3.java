package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;

class ESCollectionPrototype$3 extends HostFunction {
   private final ESCollectionPrototype this$0;

   ESCollectionPrototype$3(ESCollectionPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESCollection thiz = (ESCollection)this.getThis();
      String name = thiz.getId();
      DataCollection collection = thiz.getCollection();
      long dataHandler = -1;
      if (collection instanceof KeyDataCollection) {
         if (this.getNumParms() > 0) {
            Object keyValue = this.this$0.createKeyFromParams(this.getParm(0), (KeyDataCollection)collection);
            dataHandler = ((KeyDataCollection)collection).find(keyValue);
         } else {
            EcmaUtilities.throwESError(name, RuntimeResources.getString(80, name));
         }
      } else {
         EcmaUtilities.throwESError(name, RuntimeResources.getString(81, name));
      }

      return this.this$0._context.createDataInstance(collection, dataHandler);
   }
}
