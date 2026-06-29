package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.Comparator;

final class PeerContactListCollection$1 implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int hash1 = this.getHash(o1);
      int hash2 = this.getHash(o2);
      if (hash1 > hash2) {
         return 1;
      } else {
         return hash1 < hash2 ? -1 : 0;
      }
   }

   private final int getHash(Object o) {
      int hash = 0;
      if (!(o instanceof PeerContact)) {
         if (o instanceof Object) {
            hash = ((String)o).toUpperCase().hashCode();
         }

         return hash;
      } else {
         return ((PeerContact)o).getIdHash();
      }
   }

   @Override
   public final boolean equals(Object o) {
      return false;
   }
}
