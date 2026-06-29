package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class TransitoryMessagePropertiesModel$StringComparator implements Comparator {
   private TransitoryMessagePropertiesModel$StringComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      String s1 = o1 != null ? o1.toString() : null;
      String s2 = o2 != null ? o2.toString() : null;
      return StringUtilities.compareToIgnoreCase(s1, s2);
   }

   TransitoryMessagePropertiesModel$StringComparator(TransitoryMessagePropertiesModel$1 x0) {
      this();
   }
}
