package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class EmailFilterVerb$StringComparator implements Comparator {
   @Override
   public final int compare(Object objA, Object objB) {
      return StringUtilities.compareObjectToStringIgnoreCase((String)objA, (String)objB);
   }
}
