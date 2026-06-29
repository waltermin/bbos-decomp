package javax.microedition.rms;

import net.rim.vm.Array;

class FilterRecordEnumeration extends BaseRecordEnumeration {
   private RecordFilter _filter;

   FilterRecordEnumeration(RecordStore recordStore, RecordEventGenerator recordSource, RecordFilter filter, boolean listen) {
      super(recordStore, recordSource, listen);
      this._filter = filter;
      this.rebuild();
   }

   @Override
   public synchronized void rebuild() {
      this.mustBeValid();
      synchronized (super._recordIds) {
         super.rebuild();
         int count = super._recordIds.length;
         int dest = 0;

         try {
            for (int i = 0; i < count; i++) {
               int recordId = super._recordIds[i];
               if (this._filter.matches(super._recordStore.getRecordReadOnly(recordId))) {
                  super._recordIds[dest++] = recordId;
               }
            }
         } catch (InvalidRecordIDException var7) {
         }

         Array.resize(super._recordIds, dest);
      }
   }

   @Override
   public void destroy() {
      this._filter = null;
      super.destroy();
   }

   @Override
   public void recordAdded(RecordStore recordStore, int recordID) {
      this.mustBeValid();
      synchronized (super._recordIds) {
         try {
            if (this._filter.matches(recordStore.getRecordReadOnly(recordID))) {
               int count = super._recordIds.length;
               Array.resize(super._recordIds, count + 1);
               super._recordIds[count] = recordID;
               this.notifyRecordAdded(recordStore, recordID);
            }
         } catch (InvalidRecordIDException var6) {
         }
      }
   }

   @Override
   public void recordChanged(RecordStore recordStore, int recordID) {
      this.mustBeValid();
      synchronized (super._recordIds) {
         int count = super._recordIds.length;
         int index = 0;

         while (index < count && super._recordIds[index] != recordID) {
            index++;
         }

         try {
            if (this._filter.matches(recordStore.getRecordReadOnly(recordID))) {
               if (index == count) {
                  this.recordAdded(recordStore, recordID);
               } else {
                  this.notifyRecordChanged(recordStore, recordID);
               }
            } else if (index < count) {
               this.recordDeleted(recordStore, recordID);
            }
         } catch (InvalidRecordIDException var8) {
         }
      }
   }
}
