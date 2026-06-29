package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.util.LongVector;

class ESCollectionPrototype$4 extends HostFunction {
   private final ESCollectionPrototype this$0;

   ESCollectionPrototype$4(ESCollectionPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      ESCollection thiz = (ESCollection)this.getThis();
      String name = thiz.getId();
      DataCollection collection = thiz.getCollection();
      long[] dataHandlers = null;
      LongVector results = null;
      int numParams = this.getNumParms();
      if (!(collection instanceof KeyDataCollection)) {
         EcmaUtilities.throwESError(name, RuntimeResources.getString(81, name));
      } else {
         KeyDataCollection keyedCollection = (KeyDataCollection)collection;
         if (numParams != 1) {
            EcmaUtilities.throwESError(name, RuntimeResources.getString(82, ((StringBuffer)(new Object())).append(this.getName()).append("()").toString()));
         }

         long param = this.getParm(0);
         String expression = Convert.toString(param);
         boolean var14 = false /* VF: Semaphore variable */;

         label42:
         try {
            var14 = true;
            dataHandlers = keyedCollection.findWhere(expression, null);
            var14 = false;
         } finally {
            if (var14) {
               EcmaUtilities.throwESError(RuntimeResources.getString(84));
               break label42;
            }
         }

         int numResults = dataHandlers.length;
         results = new LongVector(numResults);

         for (int i = 0; i < numResults; i++) {
            results.addElement(dataHandlers[i]);
         }
      }

      return Value.makeObjectValue(new ESMDSArray(32774, results, collection, "Object[]", this.this$0._context));
   }
}
