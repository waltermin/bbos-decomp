package net.rim.device.apps.internal.options.items;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class ApplicationListItemComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      String name1 = ((ApplicationList$ApplicationListItem)o1)._group.getFriendlyName();
      String name2 = ((ApplicationList$ApplicationListItem)o2)._group.getFriendlyName();
      return StringUtilities.compareToIgnoreCase(name1, name2, 1701707776);
   }
}
