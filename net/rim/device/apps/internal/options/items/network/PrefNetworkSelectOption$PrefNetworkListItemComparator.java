package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.NetworkInfo;

final class PrefNetworkSelectOption$PrefNetworkListItemComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      String name1 = ((NetworkInfo)o1).getName();
      String name2 = ((NetworkInfo)o2).getName();
      return StringUtilities.compareToIgnoreCase(name1, name2, 1701707776);
   }
}
