package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class SearchPOIDialog$2 extends MenuItem {
   private final SearchPOIDialog this$0;

   SearchPOIDialog$2(SearchPOIDialog this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      if (Dialog.ask(3, LBSResources.getString(114), -1) == 4) {
         this.this$0.deleteSelected();
      }
   }
}
