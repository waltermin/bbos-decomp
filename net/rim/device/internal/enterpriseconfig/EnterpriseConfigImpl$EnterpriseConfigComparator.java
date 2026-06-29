package net.rim.device.internal.enterpriseconfig;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Comparator;

final class EnterpriseConfigImpl$EnterpriseConfigComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof Object && o2 instanceof Object) {
         int uid1 = ((SyncObject)o1).getUID();
         int uid2 = ((SyncObject)o2).getUID();
         if (uid1 == uid2) {
            return 0;
         } else {
            return uid1 > uid2 ? 1 : -1;
         }
      } else {
         return -1;
      }
   }
}
