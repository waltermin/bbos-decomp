package net.rim.device.apps.internal.options.items;

import net.rim.device.api.util.Comparator;

final class SpellCheckOptionsItem$CustomDictComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      return o1 instanceof Object && o2 instanceof Object ? ((String)o1).compareTo((String)o2) : 0;
   }
}
