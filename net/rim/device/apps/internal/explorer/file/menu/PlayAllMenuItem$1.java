package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class PlayAllMenuItem$1 implements Comparator {
   private final PlayAllMenuItem this$0;

   PlayAllMenuItem$1(PlayAllMenuItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return StringUtilities.compareToIgnoreCase((String)o1, (String)o2);
   }
}
