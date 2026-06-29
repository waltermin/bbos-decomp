package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.Comparator;

final class PeerContactListCollection$3 implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int id1 = !(o1 instanceof PeerContactList) ? 0 : ((PeerContactList)o1).getIdHash();
      int id2 = !(o2 instanceof PeerContactList) ? 0 : ((PeerContactList)o2).getIdHash();
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
