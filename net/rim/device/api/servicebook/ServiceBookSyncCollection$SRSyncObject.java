package net.rim.device.api.servicebook;

import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.synchronization.SyncObject;

final class ServiceBookSyncCollection$SRSyncObject implements SyncObject {
   private ServiceRecord _sr;
   private int _uid;
   private int[] _assocHRI;

   public final HostRoutingTable getAttachedHRT() {
      return this._sr.getAttachedHrt();
   }

   public final int[] getAssocHRI() {
      return this._assocHRI;
   }

   public final ServiceRecord getServiceRecord() {
      return this._sr;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   ServiceBookSyncCollection$SRSyncObject(ServiceRecord sr, int uid) {
      this(sr, uid, null);
   }

   ServiceBookSyncCollection$SRSyncObject(ServiceRecord sr, int uid, int[] assocHRI) {
      this._sr = sr;
      this._uid = uid;
      this._assocHRI = assocHRI;
   }
}
