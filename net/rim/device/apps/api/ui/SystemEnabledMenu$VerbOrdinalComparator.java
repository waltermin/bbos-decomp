package net.rim.device.apps.api.ui;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.verb.Verb;

final class SystemEnabledMenu$VerbOrdinalComparator implements Comparator {
   private SystemEnabledMenu$VerbOrdinalComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      Verb v1 = (Verb)o1;
      Verb v2 = (Verb)o2;
      return v1.getOrdering() - v2.getOrdering();
   }

   @Override
   public final boolean equals(Object obj) {
      return false;
   }

   SystemEnabledMenu$VerbOrdinalComparator(SystemEnabledMenu$1 x0) {
      this();
   }
}
