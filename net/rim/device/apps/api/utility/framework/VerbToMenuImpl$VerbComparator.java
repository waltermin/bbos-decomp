package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.verb.Verb;

final class VerbToMenuImpl$VerbComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int i1 = ((Verb)o1).getOrdering();
      int i2 = ((Verb)o2).getOrdering();
      return i1 - i2;
   }
}
