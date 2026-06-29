package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.synchronization.SyncObject;

final class GhostMessageSync$GhostMessageSyncObject implements SyncObject {
   EmailHierarchy _h;
   int _uid;
   int _info;

   GhostMessageSync$GhostMessageSyncObject(EmailHierarchy h, int uid, int info) {
      this._h = h;
      this._uid = uid;
      this._info = info;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }
}
