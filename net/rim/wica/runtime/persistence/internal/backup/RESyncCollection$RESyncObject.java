package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.SyncObject;

class RESyncCollection$RESyncObject implements SyncObject {
   private int _id;

   RESyncCollection$RESyncObject(int id) {
      this._id = id;
   }

   @Override
   public int getUID() {
      return this._id;
   }
}
