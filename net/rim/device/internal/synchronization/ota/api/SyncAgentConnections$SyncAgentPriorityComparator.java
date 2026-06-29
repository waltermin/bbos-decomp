package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.IntComparator;

final class SyncAgentConnections$SyncAgentPriorityComparator implements IntComparator {
   private SyncAgentConnections$SyncAgentPriorityComparator() {
   }

   @Override
   public final int compare(int obj1, int obj2) {
      int p1 = 1275;
      int p2 = 1275;
      if (SyncAgentConnections.isConnectionRegistered(obj1)) {
         p1 = ((SyncAgentConnection)SyncAgentConnections.getInstance()._syncAgentConnections.get(obj1)).getPacketLevelPriority();
      }

      if (SyncAgentConnections.isConnectionRegistered(obj2)) {
         p2 = ((SyncAgentConnection)SyncAgentConnections.getInstance()._syncAgentConnections.get(obj2)).getPacketLevelPriority();
      }

      return p1 - p2;
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof SyncAgentConnections$SyncAgentPriorityComparator;
   }

   SyncAgentConnections$SyncAgentPriorityComparator(SyncAgentConnections$1 x0) {
      this();
   }
}
