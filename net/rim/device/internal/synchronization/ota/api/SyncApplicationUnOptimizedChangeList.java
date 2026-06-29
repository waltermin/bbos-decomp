package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Persistence;

public class SyncApplicationUnOptimizedChangeList extends SyncApplicationChangeList implements Persistable {
   @Override
   public void add(SyncApplicationChange aNewChange) {
      if (!this.isLocked() || aNewChange.shouldBeFilled()) {
         int xHashCode = aNewChange.hashCode();
         SyncApplicationChange xOldChange = (SyncApplicationChange)super._changes.get(xHashCode);
         if (xOldChange != null) {
            super._refIdToHashCodeMap.remove(xOldChange.getRefId());
            int xNewOperation = aNewChange.getOperation();
            int xOldOperation = xOldChange.getOperation();
            if (xNewOperation == 2) {
               if (xOldOperation == 1) {
                  super._changes.remove(xHashCode);
                  return;
               }

               aNewChange.setParameters(null);
               aNewChange.shouldBeFilled(false);
            } else {
               boolean xOldChangeShouldBeFilled = xOldChange.shouldBeFilled();
               if (xOldChangeShouldBeFilled) {
                  aNewChange.shouldBeFilled(xOldChangeShouldBeFilled);
                  aNewChange.setParameters(null);
               }

               if (xNewOperation == 1) {
                  if (xOldOperation != 2) {
                     return;
                  }

                  aNewChange.setOperation(4);
               }
            }

            if (xOldOperation == 1 || xOldOperation == 10) {
               aNewChange.setOperation(xOldOperation);
            }
         }

         super._changes.put(xHashCode, aNewChange);
         super._refIdToHashCodeMap.put(aNewChange.getRefId(), xHashCode);
         Persistence.commit(this, true);
      }
   }
}
