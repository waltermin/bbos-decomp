package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Persistence;

public class SyncApplicationOptimizedChangeList extends SyncApplicationChangeList implements Persistable {
   @Override
   public void add(SyncApplicationChange aNewChange) {
      if ((!this.isLocked() || aNewChange.shouldBeFilled()) && aNewChange instanceof SyncApplicationOptimizableChange) {
         SyncApplicationOptimizableChange xNewOptChange = (SyncApplicationOptimizableChange)aNewChange;
         int xHashCode = xNewOptChange.hashCode();
         SyncApplicationChange xOldOptChange = (SyncApplicationChange)super._changes.get(xHashCode);
         if (xOldOptChange != null) {
            int xOldRefId = xOldOptChange.getRefId();
            if (((SyncApplicationOptimizableChange)xOldOptChange).optimizeWith(xNewOptChange)) {
               super._refIdToHashCodeMap.remove(xOldRefId);
               super._refIdToHashCodeMap.put(xOldOptChange.getRefId(), xOldOptChange.hashCode());
            } else {
               super._changes.remove(xHashCode);
               super._refIdToHashCodeMap.remove(xOldRefId);
            }
         } else {
            super._changes.put(xHashCode, aNewChange);
            super._refIdToHashCodeMap.put(aNewChange.getRefId(), xHashCode);
         }
      }

      Persistence.commit(this, true);
   }
}
