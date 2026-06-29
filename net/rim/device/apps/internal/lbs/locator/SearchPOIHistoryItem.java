package net.rim.device.apps.internal.lbs.locator;

import net.rim.vm.Persistable;

final class SearchPOIHistoryItem implements Persistable {
   String keywords;

   SearchPOIHistoryItem(String searchString) {
      this.keywords = searchString;
   }

   @Override
   public final String toString() {
      return this.keywords;
   }
}
