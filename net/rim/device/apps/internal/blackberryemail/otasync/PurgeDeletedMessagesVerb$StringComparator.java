package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class PurgeDeletedMessagesVerb$StringComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      return StringUtilities.compareObjectToStringIgnoreCase(o1, o2);
   }
}
