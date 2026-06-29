package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.util.Comparator;

final class ConvenienceKeyOptionsImpl$1 implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      return o1 instanceof ConvenienceKeyOptionsImpl$ApplicationEntryWrapper && o2 instanceof ConvenienceKeyOptionsImpl$ApplicationEntryWrapper
         ? o1.toString().compareTo(o2.toString())
         : 0;
   }
}
