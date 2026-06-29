package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

public final class SyncAgentRecord extends DataBuffer {
   private byte _databaseVersion;
   private int _uid;
   private byte _tableId;
   private byte _index;
   private byte _lastIndex;
   private boolean _forSlowSync;
   private boolean _checkForDuplicateAdds;
   private int[] _fieldsdAttributes;

   public SyncAgentRecord() {
      this(0);
   }

   public SyncAgentRecord(int uid) {
      this(uid, null);
   }

   public SyncAgentRecord(int uid, byte[] fields) {
      this.setUid(uid);
      this.setFields(fields);
   }

   public final void checkForDuplicateAdds(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean checkForDuplicateAdds() {
      return this._checkForDuplicateAdds;
   }

   public final int[] getFieldsAttributes() {
      return this._fieldsdAttributes;
   }

   public final void setFieldsAttributes(int[] fieldsdAttributes) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean isForSlowSync() {
      return this._forSlowSync;
   }

   public final void setForSlowSync(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getDatabaseVersion() {
      return this._databaseVersion;
   }

   public final void setDatabaseVersion(int databaseVersion) {
      this._databaseVersion = (byte)databaseVersion;
   }

   public final void setIndex(int anIndex) {
      this._index = (byte)anIndex;
   }

   public final void setLastIndex(int aLastIndex) {
      this._lastIndex = (byte)aLastIndex;
   }

   public final int getIndex() {
      return this._index;
   }

   public final int getLastIndex() {
      return this._lastIndex;
   }

   public final boolean isFragmented() {
      return this._lastIndex != 0;
   }

   public final void setUid(int uid) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getUid() {
      return this._uid;
   }

   public final void setTableId(int aTableId) {
      this._tableId = (byte)aTableId;
   }

   public final int getTableId() {
      return this._tableId;
   }

   public final void setFields(byte[] fields) {
      if (fields != null) {
         this.setData(fields, 0, fields.length, true);
      } else {
         super.reset();
      }
   }

   public final byte[] getFields() {
      return this.getArray();
   }

   @Override
   public final void reset() {
      super.reset();
      this._databaseVersion = 0;
      this._uid = 0;
      this._tableId = 0;
      this._index = 0;
      this._lastIndex = 0;
      this._forSlowSync = false;
      this._checkForDuplicateAdds = false;
      if (this._fieldsdAttributes != null) {
         Array.resize(this._fieldsdAttributes, 0);
      }
   }
}
