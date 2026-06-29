package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class StringComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      String name1 = null;
      String name2 = null;
      if (!(o1 instanceof PeerContact)) {
         if (o1 instanceof Object) {
            name1 = (String)o1;
         }
      } else {
         name1 = ((PeerContact)o1).getDisplayName();
      }

      if (!(o2 instanceof PeerContact)) {
         if (o2 instanceof Object) {
            name2 = (String)o2;
         }
      } else {
         name2 = ((PeerContact)o2).getDisplayName();
      }

      return StringUtilities.compareToIgnoreCase(name1, name2);
   }

   @Override
   public final boolean equals(Object obj) {
      return false;
   }
}
