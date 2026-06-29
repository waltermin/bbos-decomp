package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.util.Comparator;

final class PriorityComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      LabelPath label1 = (LabelPath)o1;
      LabelPath label2 = (LabelPath)o2;
      return label2._priority > 4 && label1._priority > 4 ? label1._priority - label2._priority : label2._priority - label1._priority;
   }
}
