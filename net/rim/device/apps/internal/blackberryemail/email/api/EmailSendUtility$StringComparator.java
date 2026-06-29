package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class EmailSendUtility$StringComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      return StringUtilities.compareObjectToStringIgnoreCase(o1, o2);
   }
}
