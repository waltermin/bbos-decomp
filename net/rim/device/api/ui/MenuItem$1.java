package net.rim.device.api.ui;

import net.rim.device.api.util.Comparator;

class MenuItem$1 implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      return ((MenuItem)o1).getOrdinal() - ((MenuItem)o2).getOrdinal();
   }
}
