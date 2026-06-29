package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.Service;
import net.rim.device.api.util.Comparator;

final class SelectServiceDialog$1 implements Comparator {
   private final SelectServiceDialog this$0;

   SelectServiceDialog$1(SelectServiceDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return o1 instanceof Service && o2 instanceof Service ? ((ServiceRunnable)o1).getName().compareTo(((ServiceRunnable)o2).getName()) : 0;
   }

   @Override
   public final boolean equals(Object obj) {
      return true;
   }
}
