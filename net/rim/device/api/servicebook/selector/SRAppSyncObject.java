package net.rim.device.api.servicebook.selector;

import net.rim.device.api.synchronization.SyncObject;

final class SRAppSyncObject implements SyncObject {
   private SRSelectorData _data;
   private String _uid;
   private int _guid;

   public SRAppSyncObject(SRSelectorData data, int guid) {
      this._data = data;
      this._guid = guid;
   }

   public final SRSelectorData getData() {
      return this._data;
   }

   public final String getUid() {
      return this._uid;
   }

   public final void setUid(String s) {
      this._uid = s;
   }

   @Override
   public final int getUID() {
      return this._guid;
   }
}
