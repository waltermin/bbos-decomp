package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.MenuItem;

final class SearchableHistoryList$5 extends MenuItem {
   private final FinderHistory val$history;
   private final SearchableHistoryList$LookupListField this$1;

   SearchableHistoryList$5(SearchableHistoryList$LookupListField this$1, String x0, int x1, int x2, FinderHistory val$history) {
      super(x0, x1, x2);
      this.this$1 = this$1;
      this.val$history = val$history;
   }

   @Override
   public final void run() {
      this.val$history.sort();
      this.this$1.invalidate();
   }
}
