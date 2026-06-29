package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.MenuItem;

final class SearchableHistoryList$2 extends MenuItem {
   private final SearchableHistoryList$LookupListField this$1;

   SearchableHistoryList$2(SearchableHistoryList$LookupListField this$1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$1 = this$1;
   }

   @Override
   public final void run() {
      this.this$1.this$0.editHistoryItem();
   }
}
