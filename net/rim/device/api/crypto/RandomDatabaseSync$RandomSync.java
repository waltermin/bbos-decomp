package net.rim.device.api.crypto;

import net.rim.device.api.synchronization.SyncObject;

class RandomDatabaseSync$RandomSync implements SyncObject {
   private byte[] _data;

   public RandomDatabaseSync$RandomSync(byte[] data) {
      this._data = data;
   }

   public RandomDatabaseSync$RandomSync() {
      this._data = RandomSource.getBytes(1024);
   }

   public byte[] getData() {
      return this._data;
   }

   @Override
   public int getUID() {
      return 1;
   }
}
