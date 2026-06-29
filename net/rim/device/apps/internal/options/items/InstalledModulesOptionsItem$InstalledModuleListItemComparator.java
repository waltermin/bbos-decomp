package net.rim.device.apps.internal.options.items;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class InstalledModulesOptionsItem$InstalledModuleListItemComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      String name1 = ((InstalledModulesOptionsItem$InstalledModuleListItem)o1).getModuleName();
      String name2 = ((InstalledModulesOptionsItem$InstalledModuleListItem)o2).getModuleName();
      return StringUtilities.compareToIgnoreCase(name1, name2, 1701707776);
   }
}
