package net.rim.device.api.servicebook;

import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.synchronization.SyncObject;

final class ServiceBookSyncCollection$HRISyncObject implements SyncObject {
   private HostRoutingInfo _hri;
   private int _uid;
   private int _sruid;

   public final HostRoutingInfo getHRI() {
      return this._hri;
   }

   public final int getSRUID() {
      return this._sruid;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   ServiceBookSyncCollection$HRISyncObject(HostRoutingInfo hri, int uid, int sruid) {
      this._hri = hri;
      this._uid = uid;
      this._sruid = sruid;
   }
}
