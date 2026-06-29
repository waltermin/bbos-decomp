package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.api.util.Comparator;

public class AttendeeUtilities$EmailAddressComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      String address1 = (String)o1;
      String address2 = (String)o2;
      return address1.compareTo(address2);
   }

   @Override
   public boolean equals(Object obj) {
      return this.equals(obj);
   }
}
