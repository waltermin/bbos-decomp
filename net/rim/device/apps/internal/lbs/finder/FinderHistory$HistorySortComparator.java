package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.util.Comparator;

final class FinderHistory$HistorySortComparator implements Comparator {
   private FinderHistory$HistorySortComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      LocationPersistObject lo1 = (LocationPersistObject)o1;
      LocationPersistObject lo2 = (LocationPersistObject)o2;
      return lo2._label.compareTo(lo1._label) * -1;
   }

   FinderHistory$HistorySortComparator(FinderHistory$1 x0) {
      this();
   }
}
