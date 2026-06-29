package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.util.Comparator;

class OTAUpgradeControl$SCComp implements Comparator {
   private boolean _restoreOrder;

   private OTAUpgradeControl$SCComp(boolean restoreOrder) {
      this._restoreOrder = restoreOrder;
   }

   @Override
   public int compare(Object o1, Object o2) {
      int brOrder = this._restoreOrder ? 1 : -1;
      int p1 = 5;
      int p2 = 5;
      if (o1 instanceof Object) {
         OTASyncPriorityProvider pp = (OTASyncPriorityProvider)o1;
         p1 = pp.getSyncPriority();
      }

      if (o2 instanceof Object) {
         OTASyncPriorityProvider pp = (OTASyncPriorityProvider)o2;
         p2 = pp.getSyncPriority();
      }

      int comp = p2 - p1;
      if (comp != 0) {
         if (!this._restoreOrder) {
            if (p1 >= 10) {
               brOrder = 1;
            } else if (p2 >= 10) {
               brOrder = 1;
            }
         }

         return brOrder * comp;
      } else {
         int dep1 = 255;
         int dep2 = 255;
         if (o1 instanceof Object) {
            OTASyncPriorityAndDependencyProvider pdp = (OTASyncPriorityAndDependencyProvider)o1;
            dep1 = pdp.getDependencyLevel();
            dep1 = this.getValidDependency(dep1);
         }

         if (o2 instanceof Object) {
            OTASyncPriorityAndDependencyProvider pdp = (OTASyncPriorityAndDependencyProvider)o2;
            dep2 = pdp.getDependencyLevel();
            dep2 = this.getValidDependency(dep2);
         }

         comp = dep2 - dep1;
         if (comp != 0) {
            return brOrder * comp;
         } else {
            SyncCollection s1 = (SyncCollection)o1;
            SyncCollection s2 = (SyncCollection)o2;
            String name1 = OTAUpgradeControl$SCSafe.getSyncName(s1);
            String name2 = OTAUpgradeControl$SCSafe.getSyncName(s2);
            if (name1 == null) {
               return brOrder;
            } else {
               return name2 == null ? -brOrder : brOrder * -name1.compareTo(name2);
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      return this.equals(o);
   }

   private int getValidDependency(int dep) {
      return dep < 1 ? 255 : dep;
   }

   OTAUpgradeControl$SCComp(boolean x0, OTAUpgradeControl$1 x1) {
      this(x0);
   }
}
