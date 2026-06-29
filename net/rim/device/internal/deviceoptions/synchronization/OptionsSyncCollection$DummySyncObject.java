package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.SyncObject;

final class OptionsSyncCollection$DummySyncObject implements SyncObject {
   private int _uid;

   OptionsSyncCollection$DummySyncObject(int aUid) {
      this._uid = aUid;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }
}
