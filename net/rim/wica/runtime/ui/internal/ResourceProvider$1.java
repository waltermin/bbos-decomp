package net.rim.wica.runtime.ui.internal;

import net.rim.wica.runtime.persistence.Resource;

class ResourceProvider$1 implements Runnable {
   private final ResourceListener val$listener;
   private final Resource val$r;
   private final ResourceProvider this$0;

   ResourceProvider$1(ResourceProvider this$0, ResourceListener val$listener, Resource val$r) {
      this.this$0 = this$0;
      this.val$listener = val$listener;
      this.val$r = val$r;
   }

   @Override
   public void run() {
      this.val$listener.resourceRetrieved(this.val$r);
   }
}
