package net.rim.device.api.crypto.keystore;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class KeyStoreOptionsItem$StringComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      return StringUtilities.compareToIgnoreCase((String)o1, (String)o2, 1701707776);
   }
}
