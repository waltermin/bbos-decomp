package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.util.Comparator;

class SimplePersistentSyncCollection$1 implements Comparator {
   private final Comparator val$comparator;
   private final SimplePersistentSyncCollection this$0;

   SimplePersistentSyncCollection$1(SimplePersistentSyncCollection _1, Comparator _2) {
      this.this$0 = _1;
      this.val$comparator = _2;
   }

   @Override
   public int compare(Object object1, Object object2) {
      if (object1 == null) {
         return object2 == null ? 0 : -1;
      } else {
         return object2 == null ? 1 : this.val$comparator.compare(object1, object2);
      }
   }
}
