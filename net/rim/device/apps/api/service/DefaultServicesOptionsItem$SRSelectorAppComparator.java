package net.rim.device.apps.api.service;

import net.rim.device.api.servicebook.selector.SRSelectorApp;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class DefaultServicesOptionsItem$SRSelectorAppComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      return StringUtilities.compareToIgnoreCase(((SRSelectorApp)o1).getName(), ((SRSelectorApp)o2).getName(), 1701707776);
   }
}
