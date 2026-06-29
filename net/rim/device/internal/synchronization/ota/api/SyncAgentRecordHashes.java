package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.internal.synchronization.ota.util.ReusableObject;

public final class SyncAgentRecordHashes implements ReusableObject {
   private int _uid;
   private int _keyFieldsHash;
   private int _allFieldsHash;
   public static final long POOL_GUID;

   public final void setUid(int aUid) {
      this._uid = aUid;
   }

   public final void setKeyFieldsHash(int aKeyFieldsHash) {
      this._keyFieldsHash = aKeyFieldsHash;
   }

   public final void setAllFieldsHash(int allFieldsHash) {
      this._allFieldsHash = allFieldsHash;
   }

   public final int getUid() {
      return this._uid;
   }

   public final int getKeyFieldsHash() {
      return this._keyFieldsHash;
   }

   public final int getAllFieldsHash() {
      return this._allFieldsHash;
   }

   @Override
   public final void reset() {
      this._uid = 0;
      this._keyFieldsHash = 0;
      this._allFieldsHash = 0;
   }
}
