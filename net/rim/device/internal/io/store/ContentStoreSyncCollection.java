package net.rim.device.internal.io.store;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.vm.Array;

final class ContentStoreSyncCollection implements SyncCollection, CollectionEventSource, SyncCollectionStatistics {
   private IntIntHashtable _folderMap;
   private boolean _inTransaction;
   ContentStoreImpl _store = ContentStoreImpl.getInstance();
   ContentStoreDatabase _database = this._store.getDatabase();
   CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private static String SAMPLE_PICTURES = "/samples/pictures/";
   private static String USER_PICTURES = "/home/user/pictures/";
   private static String USER_RINGTONES = "/home/user/ringtones/";
   public static final int SYNC_VERSION;
   public static final int SYNC_VERSION_DESKTOP;
   public static final int SYNC_VERSION_MIN;
   public static final int SYNC_VERSION_MAX;

   final void enableSynchronization() {
      SyncManager.getInstance().enableSynchronization(this);
   }

   final void fileAdded(SyncObject object) {
      this._collectionListenerManager.fireElementAdded(this, object);
   }

   final void fileRemoved(SyncObject object) {
      this._collectionListenerManager.fireElementRemoved(this, object);
   }

   public final SyncCollectionSchema getSchema() {
      return null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (!(object instanceof FileImpl)) {
         if (!(object instanceof FolderImpl)) {
            return false;
         }

         FolderImpl folder = (FolderImpl)object;
         if (ContentStoreImpl.isInSamplesFolder(folder)) {
            return true;
         }

         FolderImpl parent = null;
         String name = folder.getName();
         if (name.length() != 0) {
            int parentId;
            if (this._folderMap != null) {
               parentId = this._folderMap.get(folder.getParentId());
            } else {
               parentId = folder.getParentId();
            }

            label186:
            try {
               parent = this._database.getFolderTable().getFolderForId(parentId);
               parent.addFolder(name);
            } catch (Throwable var17) {
               System.err.println(e);
               this.log(e);
               break label186;
            }
         }

         if (this._folderMap != null) {
            try {
               FolderImpl realFolder = parent != null ? parent.getFolder(name) : this._store.getFolder("/");
               if (realFolder == null) {
                  throw new Object();
               }

               int folderid = realFolder.getId();
               this._folderMap.put(folder.getId(), folderid);
               return true;
            } catch (Throwable var16) {
               this.log(e);
               return true;
            }
         } else {
            return true;
         }
      } else {
         FileImpl file = (FileImpl)object;
         if (ContentStoreImpl.isInSamplesFolder(file)) {
            return true;
         }

         int folderId = file.getFolderId();
         FolderImpl folder = this._database.getFolderTable().getFolderForId(folderId);
         if (folder == null) {
            FolderImpl temp = this._store.getDatabase().getFolderTable().getFolder(file.getFolderId());
            if (temp != null) {
               folderId = temp.getId();
               folder = this._database.getFolderTable().getFolderForId(folderId);
            }

            if (folder == null) {
               throw new Object();
            }
         }

         label194:
         try {
            boolean removeFile = (file.getAttributes() & 8192) != 0;
            file.setFolder(folder, false, true);
            this._store.getDatabase().getFileTable().addInternal(file, false);
            this._collectionListenerManager.fireElementAdded(this, file);
            if (removeFile) {
               file.remove();
            }
         } catch (Throwable var18) {
            this.log(e);
            break label194;
         }

         file.commit();
         return true;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof FileImpl && newObject instanceof FileImpl) {
         FileImpl oldFile = (FileImpl)oldObject;
         FileImpl newFile = (FileImpl)newObject;

         try {
            this._store.getDatabase().getFileTable().update(newFile);
            this._collectionListenerManager.fireElementUpdated(this, oldFile, newFile);
            return true;
         } finally {
            ;
         }
      } else if (oldObject instanceof FolderImpl && newObject instanceof FolderImpl) {
         FolderImpl oldFolder = (FolderImpl)oldObject;
         FolderImpl newFolder = (FolderImpl)newObject;
         this._store.getDatabase().getFolderTable().update(newFolder);
         if (this._folderMap != null) {
            this._folderMap.put(newFolder.getId(), oldFolder.getId());
         }

         this._collectionListenerManager.fireElementUpdated(this, oldFolder, newFolder);
         return true;
      } else {
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof FSDescriptor) {
         FSDescriptor descriptor = (FSDescriptor)object;

         try {
            if (!(descriptor instanceof FolderImpl)) {
               descriptor.remove();
            }

            return true;
         } catch (Throwable var5) {
            this.log(e);
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final boolean removeAllSyncObjects() {
      synchronized (this._store.getMonitor()) {
         this._database.clear();
         this._database.getFileTable().clearPicturesTable();
         this.commit();
         ContentStoreImpl.getInstance().resetQuotaUsed();
         if (!this._inTransaction) {
            FileSystem.rootChanged("store/");
         }

         return true;
      }
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      ContentStoreImpl store = ContentStoreImpl.getInstance();
      synchronized (store.getMonitor()) {
         SyncObject[] objects = new Object[this.getSyncObjectCount()];
         int dest = 0;
         FolderImpl[] folders = store.getDatabase().getFolderTable().getArrayInternal();

         for (int lv = folders.length - 1; lv >= 0; lv--) {
            FolderImpl element = folders[lv];
            if (element != null && element.isArchivable() && !element.hasNullAncestor()) {
               objects[dest++] = element;
            }
         }

         Comparator comparator = store.getDatabase().getFolderTable().getComparatorForSync();
         Arrays.sort(objects, 0, dest, comparator);
         FileImpl[] files = store.getDatabase().getFileTable().getArrayInternal();

         for (int lv = files.length - 1; lv >= 0; lv--) {
            FileImpl element = files[lv];
            if (element != null && element.isArchivable()) {
               FolderImpl folder = (FolderImpl)element.getFolder();
               if (folder != null && !folder.hasNullAncestor()) {
                  objects[dest++] = element;
               }
            }
         }

         Array.resize(objects, dest);
         return objects;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      ContentStoreImpl store = ContentStoreImpl.getInstance();
      synchronized (store.getMonitor()) {
         FileImpl[] files = store.getDatabase().getFileTable().getArrayInternal();

         for (int i = files.length - 1; i >= 0; i--) {
            FileImpl file = files[i];
            if (file != null && file.getUID() == uid) {
               return file;
            }
         }

         FolderImpl[] folders = store.getDatabase().getFolderTable().getArrayInternal();

         for (int i = folders.length - 1; i >= 0; i--) {
            FolderImpl folder = folders[i];
            if (folder != null && folder.getUID() == uid) {
               return folders[i];
            }
         }

         return null;
      }
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
   public final int getSyncObjectCount() {
      return ContentStoreImpl.getInstance().getDatabase().getSyncObjectCount();
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Content Store";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return new ContentStoreSyncCollection$ContentStoreSyncConverter(this);
   }

   @Override
   public final void beginTransaction() {
      this._inTransaction = true;
   }

   @Override
   public final void endTransaction() {
      if (this._inTransaction) {
         synchronized (this._store.getMonitor()) {
            this._inTransaction = false;
            this._folderMap = null;
            this.commit();
            FileSystem.rootChanged("store/");
         }
      }
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   private final void commit() {
      if (!this._inTransaction) {
         this._store.getDatabase().commit();
      }
   }

   private final void log(Throwable e) {
      System.err.println(e.toString());
      e.printStackTrace();
   }
}
