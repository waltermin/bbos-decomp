package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class SearchableHistoryList$4 extends MenuItem {
   private final FinderHistory val$history;
   private final SearchableHistoryList$LookupListField this$1;

   SearchableHistoryList$4(SearchableHistoryList$LookupListField this$1, String x0, int x1, int x2, FinderHistory val$history) {
      super(x0, x1, x2);
      this.this$1 = this$1;
      this.val$history = val$history;
   }

   @Override
   public final void run() {
      if (Dialog.ask(3, LBSResources.getString(75), -1) == 4) {
         this.val$history.delete(-1);
         this.this$1.this$0._historyList.setSize(0, 0);
      }
   }
}
