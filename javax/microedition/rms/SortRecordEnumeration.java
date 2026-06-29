package javax.microedition.rms;

import net.rim.vm.Array;

class SortRecordEnumeration extends BaseRecordEnumeration {
   private RecordComparator _comparator;

   SortRecordEnumeration(RecordStore recordStore, RecordEventGenerator recordSource, RecordComparator comparator, boolean listen) {
      super(recordStore, recordSource, listen);
      this._comparator = comparator;
      this.rebuild();
   }

   @Override
   public synchronized void rebuild() {
      this.mustBeValid();
      super.rebuild();
      this.sort(0, super._recordIds.length - 1);
   }

   @Override
   public void destroy() {
      this._comparator = null;
      super.destroy();
   }

   private void sort(int start, int end) {
      try {
         while (end - start >= 8) {
            int left = start;
            int right = end;
            int mid = (start + end) / 2;
            int pivotID = super._recordIds[mid];
            byte[] pivotData = super._recordStore.getRecordReadOnly(pivotID);
            super._recordIds[mid] = super._recordIds[left];

            label61:
            while (true) {
               if (this._comparator.compare(pivotData, super._recordStore.getRecordReadOnly(super._recordIds[right])) != 1) {
                  if (left == --right) {
                     break;
                  }
               } else {
                  super._recordIds[left] = super._recordIds[right];
                  if (++left == right) {
                     break;
                  }

                  while (this._comparator.compare(super._recordStore.getRecordReadOnly(super._recordIds[left]), pivotData) != 1) {
                     if (++left == right) {
                        break label61;
                     }
                  }

                  super._recordIds[right] = super._recordIds[left];
                  if (--right == left) {
                     break;
                  }
               }
            }

            super._recordIds[left] = pivotID;
            if (left - start < end - left) {
               this.sort(start, left - 1);
               start = left + 1;
            } else {
               this.sort(left + 1, end);
               end = left - 1;
            }
         }

         for (int i = start; i <= end; i++) {
            boolean anySwapped = false;

            for (int j = end; j > i; j--) {
               int pivotID = super._recordIds[j - 1];
               if (this._comparator.compare(super._recordStore.getRecordReadOnly(pivotID), super._recordStore.getRecordReadOnly(super._recordIds[j])) == 1) {
                  super._recordIds[j - 1] = super._recordIds[j];
                  super._recordIds[j] = pivotID;
                  anySwapped = true;
               }
            }

            if (!anySwapped) {
               return;
            }
         }
      } catch (InvalidRecordIDException var11) {
      }
   }

   @Override
   public synchronized void recordAdded(RecordStore recordStore, int recordID) {
      this.mustBeValid();
      int count = super._recordIds.length;

      try {
         Array.resize(super._recordIds, count + 1);
         byte[] newData = recordStore.getRecordReadOnly(recordID);

         int i;
         for (i = count - 1; i >= 0; i--) {
            int id = super._recordIds[i];
            byte[] testData = recordStore.getRecordReadOnly(id);
            if (this._comparator.compare(newData, testData) == 1) {
               break;
            }

            super._recordIds[i + 1] = id;
         }

         super._recordIds[i + 1] = recordID;
      } catch (InvalidRecordIDException var9) {
      }

      this.notifyRecordAdded(recordStore, recordID);
   }

   @Override
   public void recordChanged(RecordStore recordStore, int recordID) {
      this.mustBeValid();
      int count = super._recordIds.length;

      try {
         int oldPosition = -1;
         byte[] newData = recordStore.getRecordReadOnly(recordID);

         int i;
         for (i = count; i > 0; i--) {
            int id = super._recordIds[i - 1];
            if (id == recordID) {
               oldPosition = i - 1;
            } else {
               byte[] testData = recordStore.getRecordReadOnly(id);
               if (this._comparator.compare(newData, testData) == 1) {
                  break;
               }
            }
         }

         if (oldPosition < 0) {
            int id = recordID;

            while (--i >= 0) {
               int saveID = super._recordIds[i];
               super._recordIds[i] = id;
               id = saveID;
               if (id == recordID) {
                  break;
               }
            }
         } else if (oldPosition != i) {
            while (oldPosition > i) {
               super._recordIds[oldPosition] = super._recordIds[oldPosition - 1];
               oldPosition--;
            }

            super._recordIds[i] = recordID;
         }
      } catch (InvalidRecordIDException var10) {
      }

      this.notifyRecordChanged(recordStore, recordID);
   }
}
