package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

final class DocViewSyncCollection implements SyncConverter, SyncCollection, SyncCollectionStatistics {
   private DocViewAttachmentPersist _data = DocViewAttachmentPersist.getInstance();
   private static DocViewSyncCollection _instance;

   static final DocViewSyncCollection getInstance() {
      if (_instance == null) {
         _instance = new DocViewSyncCollection();
      }

      return _instance;
   }

   private DocViewSyncCollection() {
      SyncManager.getInstance().enableSynchronization(this);
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      return version == this.getSyncVersion() ? this._data.restoreData(data, UID) : null;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (version == this.getSyncVersion() && object instanceof BackupProvider) {
         ((BackupProvider)object).serializeData(buffer, -1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final String getSyncName() {
      return "Attachment Data";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return this.getSyncName();
   }

   @Override
   public final int getSyncObjectCount() {
      return this._data.getSyncObjectsCount();
   }

   @Override
   public final int getSyncCollectionSize() {
      return this._data.getCurrentAttachmentDataSize();
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return this._data.getSyncObjects();
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this._data.clear();
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }
}
