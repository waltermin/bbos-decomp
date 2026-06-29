package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class MessageServicesCMIMEOptionsProvider$StringComparator implements Comparator {
   private MessageServicesCMIMEOptionsProvider$StringComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      String s1 = o1 != null ? o1.toString() : null;
      String s2 = o2 != null ? o2.toString() : null;
      return StringUtilities.compareToIgnoreCase(s1, s2);
   }

   MessageServicesCMIMEOptionsProvider$StringComparator(MessageServicesCMIMEOptionsProvider$1 x0) {
      this();
   }
}
