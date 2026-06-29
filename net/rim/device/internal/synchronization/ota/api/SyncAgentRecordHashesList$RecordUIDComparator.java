package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Comparator;

class SyncAgentRecordHashesList$RecordUIDComparator implements Comparator {
   private SyncAgentRecordHashesList$RecordUIDComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      return ((SyncAgentRecordHashes)o1).getUid() - ((SyncAgentRecordHashes)o2).getUid();
   }

   SyncAgentRecordHashesList$RecordUIDComparator(SyncAgentRecordHashesList$1 x0) {
      this();
   }
}
