package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.utility.framework.WaitForUnlock;

final class SearchCollection$MyWaitForUnlock extends WaitForUnlock {
   private final SearchCollection this$0;

   public SearchCollection$MyWaitForUnlock(SearchCollection _1) {
      super(_1.getContentProtectionEnabledMessage());
      this.this$0 = _1;
   }

   @Override
   public final void performWork(Object cookie) {
      if (this.this$0.size() == 0) {
         this.this$0._search.establishPrecannedSearches((SearchCollection)cookie);
      }
   }
}
