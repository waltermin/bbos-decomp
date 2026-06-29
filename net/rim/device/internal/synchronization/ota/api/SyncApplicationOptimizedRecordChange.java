package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.TLEFieldsList;

public final class SyncApplicationOptimizedRecordChange extends SyncApplicationRecordChange implements Persistable, SyncApplicationOptimizableChange {
   @Override
   public final boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      }

      if (anObject instanceof SyncApplicationOptimizedRecordChange) {
         SyncApplicationOptimizedRecordChange aSyncApplicationOptimizedRecordChange = (SyncApplicationOptimizedRecordChange)anObject;
         if (aSyncApplicationOptimizedRecordChange.getRecordUid() == this.getRecordUid()) {
            return super.equals(anObject);
         }
      }

      return false;
   }

   @Override
   public final boolean optimizeWith(SyncApplicationOptimizableChange aChange) {
      if (aChange instanceof SyncApplicationOptimizedRecordChange) {
         SyncApplicationOptimizedRecordChange xNewChange = (SyncApplicationOptimizedRecordChange)aChange;
         int xNewOperation = xNewChange.getOperation();
         int xOldOperation = this.getOperation();
         this.setRefId(xNewChange.getRefId());
         if (xNewOperation == 2) {
            if (xOldOperation == 1) {
               return false;
            }

            this.setOperation(xNewOperation);
            this.setParameters(null);
            this.shouldBeFilled(false);
            return true;
         }

         if (xNewOperation == 10 || xNewOperation == 4) {
            if (xOldOperation != 1) {
               this.setOperation(xNewOperation);
            }

            if (!this.shouldBeFilled() && !xNewChange.shouldBeFilled()) {
               TLEFieldsList xOldTLEFieldsList = new TLEFieldsList(this.getRecordFields(false));
               TLEFieldsList xNewTLEFieldsList = new TLEFieldsList(xNewChange.getRecordFields(false));
               TLEFieldsList xMergedTLEFieldsList = xNewTLEFieldsList.mergeInto(xOldTLEFieldsList, true);
               this.setParameters(xMergedTLEFieldsList.toByteArray());
            }
         }
      }

      return true;
   }
}
