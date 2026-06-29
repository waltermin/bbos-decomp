package net.rim.device.apps.internal.ribbon.indicators;

import net.rim.device.api.util.Comparator;

final class IndicatorComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int priority1;
      try {
         priority1 = ((IndicatorDisplay)o1)._indicator.getPriority();
      } finally {
         ;
      }

      int priority2;
      try {
         priority2 = ((IndicatorDisplay)o2)._indicator.getPriority();
      } finally {
         ;
      }

      return priority1 - priority2;
   }
}
