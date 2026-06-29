package net.rim.device.api.ui.theme;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class ThemeManager$1 implements Comparator {
   private final ThemeManager this$0;

   ThemeManager$1(ThemeManager _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      Theme$Factory l1 = (Theme$Factory)o1;
      Theme$Factory l2 = (Theme$Factory)o2;
      return StringUtilities.compareToIgnoreCase(l1.getName(), l2.getName(), 1701707776);
   }
}
