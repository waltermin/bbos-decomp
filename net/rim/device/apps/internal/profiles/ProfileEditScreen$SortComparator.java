package net.rim.device.apps.internal.profiles;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.KeyUtilities;

final class ProfileEditScreen$SortComparator implements Comparator {
   @Override
   public final int compare(Object object1, Object object2) {
      int result = 0;
      if (!(object1 instanceof Object)) {
         throw new Object("Cannot perform comparison.  Please check sources.");
      }

      KeyProvider kp1 = (KeyProvider)object1;
      if (!(object2 instanceof Object)) {
         throw new Object("Cannot perform comparison.  Please check sources.");
      }

      KeyProvider kp2 = (KeyProvider)object2;
      String s1 = null;
      String s2 = null;

      try {
         s1 = KeyUtilities.getStringKey(kp1);
         s2 = KeyUtilities.getStringKey(kp2);
         return StringUtilities.compareToIgnoreCase(s1, s2);
      } finally {
         throw new Object(((StringBuffer)(new Object("Comparing "))).append(s1).append(" to ").append(s2).toString());
      }
   }
}
