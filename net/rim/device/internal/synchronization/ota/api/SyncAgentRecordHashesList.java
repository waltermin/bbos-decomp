package net.rim.device.internal.synchronization.ota.api;

import java.util.Vector;

public final class SyncAgentRecordHashesList extends Vector {
   public final void Sort() {
      SyncAgentRecordHashesList$QuickSort.SetComparator(new SyncAgentRecordHashesList$RecordUIDComparator(null));
      SyncAgentRecordHashesList$QuickSort.Sort(super.elementData, 0, super.elementCount - 1);
   }
}
