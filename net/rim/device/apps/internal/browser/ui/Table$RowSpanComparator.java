package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.util.Comparator;

final class Table$RowSpanComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int rowSpan1 = ((TableCell)o1).getRowSpan();
      int rowSpan2 = ((TableCell)o2).getRowSpan();
      if (rowSpan1 < rowSpan2) {
         return -1;
      } else {
         return rowSpan1 == rowSpan2 ? 0 : 1;
      }
   }
}
