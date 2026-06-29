package net.rim.device.api.crypto.keystore;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.internal.synchronization.MinimalSyncCollection;
import net.rim.device.internal.synchronization.NoProtectedContentInCollection;

final class KeyStoreManagerSyncCollection implements SyncCollection, MinimalSyncCollection, NoProtectedContentInCollection {
   private static KeyStoreManagerSyncConverter _converter = KeyStoreManagerSyncConverter.getInstance();

   public KeyStoreManagerSyncCollection() {
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      if (helper.getBackupFlag()) {
         helper.setBackupFlag(false);
         return true;
      } else {
         return helper.getAccess();
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      helper.setBackupFlag(true);
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      return new SyncObject[]{helper};
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      return helper.getUID() == uid ? helper : null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      return 1;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "KeyStoreManager";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return _converter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }
}
