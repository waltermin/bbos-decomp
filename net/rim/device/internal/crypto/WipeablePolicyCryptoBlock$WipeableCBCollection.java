package net.rim.device.internal.crypto;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

public final class WipeablePolicyCryptoBlock$WipeableCBCollection implements SyncCollection, SyncConverter {
   SyncObject _keySyncObject = new WipeablePolicyCryptoBlock$WipeableCBSyncObject();
   private static final int CURRENT_VERSION;

   WipeablePolicyCryptoBlock$WipeableCBCollection() {
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return new SyncObject[]{this._keySyncObject};
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      this._keySyncObject = object;
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final int getSyncObjectCount() {
      return 1;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Encrypted Wipeable Policy Keys";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return this.getSyncName();
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
      WipeablePolicyCryptoBlock.getWLANKey();
   }

   @Override
   public final void endTransaction() {
      this._keySyncObject = null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      return version != 1 ? false : WipeablePolicyCryptoBlock$WipeableCBSyncObject.convert(object, buffer);
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      return version != 1 ? null : WipeablePolicyCryptoBlock$WipeableCBSyncObject.convert(data, UID);
   }
}
