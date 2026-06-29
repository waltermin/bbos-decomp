package net.rim.device.cldc.util;

import net.rim.device.api.util.Comparator;

class TimeZoneDataObject$TimeZoneDataObjectComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      int gmtOffsetObject1 = ((TimeZoneDataObject)o1).getGMTOffset();
      int gmtOffsetObject2 = ((TimeZoneDataObject)o2).getGMTOffset();
      if (gmtOffsetObject1 < gmtOffsetObject2) {
         return -1;
      } else {
         return gmtOffsetObject1 == gmtOffsetObject2 ? 0 : 1;
      }
   }
}
