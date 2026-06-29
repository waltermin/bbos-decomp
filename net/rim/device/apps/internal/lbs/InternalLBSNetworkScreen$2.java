package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;

final class InternalLBSNetworkScreen$2 extends MenuItem {
   private final InternalLBSNetworkScreen this$0;

   InternalLBSNetworkScreen$2(InternalLBSNetworkScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      Application.getApplication().cancelInvokeLater(this.this$0._procID);
      this.this$0._procID = 0;
      this.this$0._counter = 0;
      this.this$0._pingAvg = 0;
   }
}
