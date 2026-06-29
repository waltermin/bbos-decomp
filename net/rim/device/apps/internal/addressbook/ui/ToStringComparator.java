package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class ToStringComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      return StringUtilities.compareObjectToStringIgnoreCase(o1, o2);
   }
}
