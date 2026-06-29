package net.rim.device.apps.internal.options.items;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class DatabaseStatisticsScreen$StatisticsListItemComparator implements Comparator {
   private DatabaseStatisticsScreen$StatisticsListItemComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 == o2) {
         return 0;
      } else if (o2 == null) {
         return 1;
      } else {
         return o1 == null
            ? -1
            : StringUtilities.compareToIgnoreCase(
               ((DatabaseStatisticsScreen$StatisticsListItem)o1)._name, ((DatabaseStatisticsScreen$StatisticsListItem)o2)._name
            );
      }
   }

   DatabaseStatisticsScreen$StatisticsListItemComparator(DatabaseStatisticsScreen$1 x0) {
      this();
   }
}
