package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class VerbOrderingComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      int verb1 = ((Verb)o1).getOrdering();
      int verb2 = ((Verb)o2).getOrdering();
      if (verb1 < verb2) {
         return -1;
      } else {
         return verb1 > verb2 ? 1 : StringUtilities.compareToIgnoreCase(o1.toString(), o2.toString());
      }
   }
}
