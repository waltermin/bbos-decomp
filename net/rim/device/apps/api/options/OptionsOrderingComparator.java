package net.rim.device.apps.api.options;

import net.rim.device.api.util.Comparator;

public class OptionsOrderingComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      if (!(o1 instanceof OptionsListItem) && !(o2 instanceof OptionsListItem)) {
         return 0;
      }

      if (!(o1 instanceof OptionsListItem)) {
         return 1;
      }

      if (!(o2 instanceof OptionsListItem)) {
         return -1;
      }

      OptionsListItem p1 = (OptionsListItem)o1;
      OptionsListItem p2 = (OptionsListItem)o2;
      return p1.getOptionsScreenOrder() - p2.getOptionsScreenOrder();
   }
}
