package net.rim.device.apps.internal.mms;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.vm.Array;

public final class MMSSync implements SyncCollection, SyncCollectionStatistics {
   private SyncConverter _converter;
   private static final String DATABASE_NAME = "MMS Messages";
   public static final int CURRENT_VERSION = 1;

   public MMSSync(SyncConverter converter) {
      this._converter = converter;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      MMSMessageModel message = (MMSMessageModel)object;
      MMSStorage.fileMessage(message, MMSStorage.getFolderForMessage(message));
      if (!message.isOpened()) {
         UnreadCountManager.incrementUnreadCount(5);
         UnreadCountManager.incrementUnreadCount(11);
      }

      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return false;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      MMSStorage.removeAll();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] objects = new SyncObject[0];
      this.addFolderContents(objects, 8244211460627721111L);
      this.addFolderContents(objects, 949632297110531729L);
      this.addFolderContents(objects, -7297051376619864492L);
      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      SyncObject syncObject = this.getSyncObject(uid, 8244211460627721111L);
      if (syncObject == null) {
         syncObject = this.getSyncObject(uid, 949632297110531729L);
      }

      if (syncObject == null) {
         syncObject = this.getSyncObject(uid, -7297051376619864492L);
      }

      return syncObject;
   }

   private final SyncObject getSyncObject(int uid, long folderID) {
      Folder folder = MMSStorage.getMMSFolder(folderID);
      ReadableList folderContents = (ReadableList)folder.getContainedItems();
      int numElements = folderContents.size();

      for (int i = 0; i < numElements; i++) {
         SyncObject element = (SyncObject)folderContents.getAt(i);
         if (element.getUID() == uid) {
            return element;
         }
      }

      return null;
   }

   private final int addFolderContents(SyncObject[] array, long folderId) {
      Folder folder = MMSStorage.getMMSFolder(folderId);
      ReadableList folderContents = (ReadableList)folder.getContainedItems();
      int length = folderContents.size();
      if (array != null) {
         int startIndex = array.length;
         Array.resize(array, startIndex + length);

         for (int i = 0; i < length; i++) {
            array[startIndex + i] = (SyncObject)folderContents.getAt(i);
         }
      }

      return length;
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
      int i = this.addFolderContents(null, 8244211460627721111L);
      i += this.addFolderContents(null, 949632297110531729L);
      return i + this.addFolderContents(null, -7297051376619864492L);
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "MMS Messages";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }
}
