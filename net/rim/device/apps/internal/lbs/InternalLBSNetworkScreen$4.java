package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Application;

final class InternalLBSNetworkScreen$4 implements Runnable {
   private final long val$ping;
   private final InternalLBSNetworkScreen this$0;

   InternalLBSNetworkScreen$4(InternalLBSNetworkScreen this$0, long val$ping) {
      this.this$0 = this$0;
      this.val$ping = val$ping;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         InternalLBSNetworkScreen.access$384(this.this$0, "Trip time: " + this.val$ping + " ms\n");
         if (this.this$0._counter > 2) {
            InternalLBSNetworkScreen.access$384(this.this$0, "Avg. time: " + this.this$0._pingAvg + " ms");
         }

         this.this$0._pingValues.setText(this.this$0._pingText);
         this.this$0.invalidate();
      }
   }
}
