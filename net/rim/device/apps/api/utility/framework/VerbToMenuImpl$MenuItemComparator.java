package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.util.Comparator;

final class VerbToMenuImpl$MenuItemComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int i1 = ((MenuItem)o1).getOrdinal();
      int i2 = ((MenuItem)o2).getOrdinal();
      return i1 - i2;
   }
}
