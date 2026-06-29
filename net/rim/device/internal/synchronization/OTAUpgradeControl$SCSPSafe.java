package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.SyncCollectionStatusProvider;

class OTAUpgradeControl$SCSPSafe {
   private OTAUpgradeControl$SCSPSafe() {
   }

   static boolean isWritableForOTASL(SyncCollectionStatusProvider scsp) {
      try {
         return scsp.isWritableForOTASL();
      } finally {
         ;
      }
   }

   static int getOTASLControlMask(SyncCollectionStatusProvider scsp) {
      try {
         return scsp.getOTASLControlMask();
      } finally {
         ;
      }
   }
}
