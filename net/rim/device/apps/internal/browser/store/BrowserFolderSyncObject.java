package net.rim.device.apps.internal.browser.store;

import net.rim.device.api.synchronization.SyncObject;

final class BrowserFolderSyncObject implements SyncObject {
   private int _uid;
   private int _parentFolderUID;
   private String _friendlyName;

   public final void setParams(int uid, int parentFolderUID, String friendlyName) {
      this._uid = uid;
      this._parentFolderUID = parentFolderUID;
      this._friendlyName = friendlyName;
   }

   public final String getFriendlyName() {
      return this._friendlyName;
   }

   public final int getParentFolderUID() {
      return this._parentFolderUID;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public BrowserFolderSyncObject() {
   }

   public BrowserFolderSyncObject(int uid, int parentFolderUID, String friendlyName) {
      this._uid = uid;
      this._parentFolderUID = parentFolderUID;
      this._friendlyName = friendlyName;
   }
}
