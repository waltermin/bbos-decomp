package net.rim.device.api.ui.theme;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class ThemeManager$2 implements Comparator {
   private final ThemeManager this$0;

   ThemeManager$2(ThemeManager _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      String l1 = (String)o1;
      Theme$Factory l2 = (Theme$Factory)o2;
      return StringUtilities.compareToIgnoreCase(l1, l2.getName(), 1701707776);
   }
}
