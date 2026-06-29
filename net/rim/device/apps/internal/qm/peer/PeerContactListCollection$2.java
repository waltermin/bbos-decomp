package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.Comparator;

final class PeerContactListCollection$2 implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int id1 = !(o1 instanceof PeerRequest) ? 0 : ((PeerRequest)o1).getRequestId();
      int id2 = !(o2 instanceof PeerRequest) ? 0 : ((PeerRequest)o2).getRequestId();
      if (id1 > id2) {
         return 1;
      } else {
         return id1 < id2 ? -1 : 0;
      }
   }

   @Override
   public final boolean equals(Object o) {
      return false;
   }
}
