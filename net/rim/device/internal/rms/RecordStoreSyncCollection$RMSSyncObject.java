package net.rim.device.internal.rms;

import java.util.Hashtable;
import net.rim.device.api.synchronization.SyncObject;

class RecordStoreSyncCollection$RMSSyncObject implements SyncObject {
   int _uid;
   Hashtable _data;
   String _midletSuiteName;
   boolean _dirty;

   public void setDirty(boolean dirty) {
      this._dirty = dirty;
   }

   public Hashtable getData() {
      return this._data;
   }

   public String getMidletSuiteName() {
      return this._midletSuiteName;
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   public boolean isDirty() {
      return this._dirty;
   }

   public RecordStoreSyncCollection$RMSSyncObject(String midletSuiteName, Hashtable data) {
      this._uid = RecordStoreSyncCollection.getUID(midletSuiteName);
      this._midletSuiteName = midletSuiteName;
      this._data = data;
      this._dirty = false;
   }
}
