package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;

class ESCollectionPrototype$5 extends HostFunction {
   private final ESCollectionPrototype this$0;

   ESCollectionPrototype$5(ESCollectionPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESCollection thiz = (ESCollection)this.getThis();
      String name = thiz.getId();
      DataCollection collection = thiz.getCollection();
      int numParams = this.getNumParms();
      if (!(collection instanceof KeyDataCollection)) {
         EcmaUtilities.throwESError(name, RuntimeResources.getString(85, name));
      } else {
         KeyDataCollection keyedCollection = (KeyDataCollection)collection;
         if (numParams != 0) {
            EcmaUtilities.throwESError(name, RuntimeResources.getString(76, this.getName() + "()"));
         }

         keyedCollection.removeAll();
      }

      return Value.NULL;
   }
}
