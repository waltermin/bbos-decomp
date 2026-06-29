package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.util.Comparator;

final class InternalApplicationFolder$1 implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      ApplicationEntry a1 = (ApplicationEntry)o1;
      ApplicationEntry a2 = (ApplicationEntry)o2;
      return a1.getPriority() - a2.getPriority();
   }
}
