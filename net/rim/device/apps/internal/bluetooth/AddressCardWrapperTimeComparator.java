package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.util.Comparator;

class AddressCardWrapperTimeComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      AddressCardWrapper card1 = (AddressCardWrapper)o1;
      AddressCardWrapper card2 = (AddressCardWrapper)o2;
      return card2._timestamp > card1._timestamp ? 1 : -1;
   }
}
