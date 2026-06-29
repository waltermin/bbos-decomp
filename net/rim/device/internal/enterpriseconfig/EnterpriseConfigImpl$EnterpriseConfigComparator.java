package net.rim.device.internal.enterpriseconfig;

import net.rim.device.api.enterpriseconfig.EnterpriseConfigRecord;
import net.rim.device.api.util.Comparator;

final class EnterpriseConfigImpl$EnterpriseConfigComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof EnterpriseConfigRecord && o2 instanceof EnterpriseConfigRecord) {
         int uid1 = ((EnterpriseConfigRecord)o1).getUID();
         int uid2 = ((EnterpriseConfigRecord)o2).getUID();
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
