package net.rim.wica.runtime.lifecycle.internal;

import net.rim.wica.runtime.lifecycle.LifecycleException;

class WicletImpl$1 implements Runnable {
   private final WicletImpl this$0;

   WicletImpl$1(WicletImpl this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      try {
         this.this$0.launchImpl();
      } catch (LifecycleException var2) {
      }
   }
}
