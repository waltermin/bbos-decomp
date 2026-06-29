package net.rim.device.apps.internal.ldap;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class LDAPBrowser$StringComparator implements Comparator {
   private LDAPBrowser$StringComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      return StringUtilities.compareObjectToStringIgnoreCase((String)o1, (String)o2, 1701707776);
   }

   LDAPBrowser$StringComparator(LDAPBrowser$1 x0) {
      this();
   }
}
