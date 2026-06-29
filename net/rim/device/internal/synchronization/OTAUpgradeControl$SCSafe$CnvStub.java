package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

class OTAUpgradeControl$SCSafe$CnvStub implements SyncConverter {
   private OTAUpgradeControl$SCSafe$CnvStub() {
   }

   @Override
   public boolean convert(SyncObject so, DataBuffer buff, int version) {
      return false;
   }

   @Override
   public SyncObject convert(DataBuffer buff, int version, int uid) {
      return null;
   }

   OTAUpgradeControl$SCSafe$CnvStub(OTAUpgradeControl$1 x0) {
      this();
   }
}
