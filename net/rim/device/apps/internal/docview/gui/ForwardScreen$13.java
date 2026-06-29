package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.framework.model.ContextObject;

class ForwardScreen$13 implements Runnable {
   private final DocViewMoreVerb val$retrieveMoreVerb;
   private final ContextObject val$cloneContext;
   private final ForwardScreen this$0;

   ForwardScreen$13(ForwardScreen _1, DocViewMoreVerb _2, ContextObject _3) {
      this.this$0 = _1;
      this.val$retrieveMoreVerb = _2;
      this.val$cloneContext = _3;
   }

   @Override
   public void run() {
      this.val$retrieveMoreVerb.invoke(this.val$cloneContext);
   }
}
