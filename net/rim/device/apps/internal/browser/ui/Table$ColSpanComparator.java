package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.util.Comparator;

final class Table$ColSpanComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int colSpan1 = ((TableCell)o1).getColSpan();
      int colSpan2 = ((TableCell)o2).getColSpan();
      if (colSpan1 < colSpan2) {
         return -1;
      } else {
         return colSpan1 == colSpan2 ? 0 : 1;
      }
   }
}
