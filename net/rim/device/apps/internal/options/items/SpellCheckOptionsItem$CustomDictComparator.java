package net.rim.device.apps.internal.options.items;

import net.rim.device.api.util.Comparator;

final class SpellCheckOptionsItem$CustomDictComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      return o1 instanceof String && o2 instanceof String ? ((String)o1).compareTo((String)o2) : 0;
   }
}
