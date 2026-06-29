package net.rim.device.internal.io.store;

import java.io.InputStream;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.WeakReference;

final class ContentStoreImpl implements LowMemoryListener {
   private IntHashtable _crcTable;
   private long _quotaUsed = 0;
   private long _picturesQuotaUsed = 0;
   private ContentStoreDatabase _database = ContentStoreDatabase.getInstance();
   private ContentStoreSyncCollection _syncCollection;
   private static ContentStoreImpl _instance;
   private static final long GUID = -7070747161240792919L;
   public static final int OPEN_CREATE = 1;
   public static final int OPEN_OVERWRITE = 2;
   public static final int OPEN_APPEND = 4;
   public static final int OP_FILE_CREATE = 0;
   public static final int OP_FILE_REMOVE = 1;
   public static final int OP_FILE_OPEN = 2;
   public static final int OP_FILE_CLOSE = 3;
   public static final int OP_FILE_READ = 4;
   public static final int OP_FILE_WRITE = 5;
   public static final int OP_FILE_APPEND = 6;
   public static final int OP_FILE_SEEK = 7;
   public static final int OP_FILE_GET_ATTR = 8;
   public static final int OP_FILE_SET_ATTR = 9;
   public static final int OP_FILE_RENAME = 10;
   public static final int OP_FOLDER = 128;
   public static final int OP_FOLDER_CREATE = 128;
   public static final int OP_FOLDER_REMOVE = 129;
   public static final int OP_FOLDER_OPEN = 130;
   public static final int OP_FOLDER_CLOSE = 131;
   public static final int OP_FOLDER_READ = 132;
   public static final int OP_FOLDER_RENAME = 133;
   public static final int OP_FOLDER_LINK = 134;
   public static final int OP_FOLDER_UNLINK = 135;

   public final FileImpl addFile(String pathAndFilename, int open) {
      String folderName = this.getPath(pathAndFilename);
      String name = this.getFilename(pathAndFilename);
      FolderImpl folder = this.getFolder(folderName);
      if (folder == null) {
         this.addFolder(folderName);
         folder = this.getFolder(folderName);
      }

      return folder.addFile(name, open);
   }

   final void addInternal(FolderImpl folder, String filename, String contentType, Object object, boolean isSystem, boolean encrypt) {
      synchronized (this._database) {
         if (folder != null && folder.getId() != 0) {
            FileImpl prev = folder.getFile(filename);
            if (prev != null && prev.isAlive()) {
               throw new Object(7);
            }

            FileImpl file = new FileImpl(0);
            file.setFolder(folder, false, isSystem);
            file.setName(filename, false);
            file.setContent(object, false);
            this._database.getFileTable().addInternal(file, isSystem);
            this._database.commit();
            FileSystem.addFileJournalEntry(file.getJSRPath(), 0);
            this._syncCollection.fileAdded(file);
         } else {
            throw new Object("Folder cannot be null or root.");
         }
      }
   }

   public final boolean addFolder(String pathAndFilename) {
      return this.addFolderInternal(pathAndFilename, 0, true);
   }

   public final SymbolicLinkImpl addSymbolicLink(String pathAndFilename, int open) {
      String folderName = this.getPath(pathAndFilename);
      String name = this.getFilename(pathAndFilename);
      FolderImpl folder = this.getFolder(folderName);
      if (folder == null) {
         this.addFolderInternal(folderName, 17, false);
         folder = this.getFolder(folderName);
      }

      if (!folder.isAlive()) {
         folder.resurrect();
      }

      return folder.addSymbolicLink(name, open);
   }

   public final long getPicturesQuotaUsed() {
      return this._picturesQuotaUsed;
   }

   public final FileImpl get(String pathAndFilename) {
      if (pathAndFilename == null) {
         return null;
      }

      int slash = pathAndFilename.lastIndexOf(47) + 1;
      if (slash == 0) {
         throw new Object("path must contain a slash");
      }

      FolderImpl folder = this._database.getFolderTable().getFolder(pathAndFilename.substring(0, slash));
      if (folder == null) {
         return null;
      }

      String filename = pathAndFilename.substring(slash);
      return folder.getFile(filename);
   }

   public final FileImpl getByOriginalURL(String url) {
      synchronized (this._database) {
         return this._database.getFileTable().getByOriginalURL(url);
      }
   }

   final ContentStoreDatabase getDatabase() {
      return this._database;
   }

   public final String getFilenameFromCRC(int crc) {
      return (String)this._crcTable.get(crc);
   }

   public final FolderImpl getFolder(String path) {
      synchronized (this._database) {
         return this._database.getFolderTable().getFolder(path);
      }
   }

   final Object getMonitor() {
      return this._database;
   }

   public final ContentStoreSyncCollection getSyncCollection() {
      return this._syncCollection;
   }

   public final FileListImpl list(FileListCriteria criteria) {
      synchronized (this._database) {
         FileListImpl list = new FileListImpl(criteria);
         this._database.getFileTable().list(list);
         return list;
      }
   }

   public final FileListImpl list(String rootFolder, String contentType) {
      synchronized (this._database) {
         int attrOn = 32;
         int attrOff = 8;
         return this.list(rootFolder, false, contentType, attrOn, attrOff);
      }
   }

   public final FileListImpl list(String rootFolder, boolean searchSubFolders, String contentType, int attrOn, int attrOff) {
      synchronized (this._database) {
         FolderImpl folder = this._database.getFolderTable().getFolder(rootFolder);
         FileListCriteria criteria = new FileListCriteria();
         criteria.setRootFolder(folder, searchSubFolders);
         criteria.setContentType(contentType);
         criteria.setAttributes(attrOn, attrOff);
         return this.list(criteria);
      }
   }

   public final FileListImpl list(String[] rootFolders, boolean searchSubFolders, String contentType, int attrOn, int attrOff) {
      synchronized (this._database) {
         if (rootFolders != null && rootFolders.length > 0) {
            int len = rootFolders.length;
            FolderImpl[] folders = new FolderImpl[len];

            for (int i = 0; i < len; i++) {
               folders[i] = this._database.getFolderTable().getFolder(rootFolders[i]);
            }

            FileListCriteria criteria = new FileListCriteria();
            criteria.setRootFolders(folders, searchSubFolders);
            criteria.setContentType(contentType);
            criteria.setAttributes(attrOn, attrOff);
            return this.list(criteria);
         } else {
            return null;
         }
      }
   }

   final FileListImpl list(FolderImpl folder, boolean searchSubFolders, String contentType, int attrOn, int attrOff) {
      synchronized (this._database) {
         FileListCriteria criteria = new FileListCriteria();
         criteria.setRootFolder(folder, searchSubFolders);
         criteria.setContentType(contentType);
         criteria.setAttributes(attrOn, attrOff);
         return this.list(criteria);
      }
   }

   public final FileListImpl list(String rootFolder, boolean searchSubFolders, String contentType, int attrOn, int attrOff, int drmAttrOn, int drmAttrOff) {
      synchronized (this._database) {
         FolderImpl folder = this._database.getFolderTable().getFolder(rootFolder);
         FileListCriteria criteria = new FileListCriteria();
         criteria.setRootFolder(folder, searchSubFolders);
         criteria.setContentType(contentType);
         criteria.setAttributes(attrOn, attrOff);
         criteria.setAttributes(drmAttrOn, drmAttrOff);
         return this.list(criteria);
      }
   }

   public final boolean purge() {
      boolean ret = false;
      synchronized (this._database) {
         if (this._database.getFileTable().purge()) {
            ret = true;
         }

         if (this._database.getFolderTable().purge()) {
            ret = true;
         }

         return ret;
      }
   }

   final boolean removeExpired() {
      return this._database.getFileTable().removeExpired();
   }

   public final byte[] encrypt(InputStream input) {
      return ContentStoreEncryption.encrypt(input);
   }

   public final byte[] decrypt(byte[] drmbuffer) {
      return ContentStoreEncryption.decrypt(drmbuffer);
   }

   public final void updateQuotaUsed(FileImpl file, long bytes) {
      if (isInPicturesFolder(file)) {
         this._picturesQuotaUsed += bytes;
      }

      this._quotaUsed += bytes;
   }

   public final boolean isQuotaAvailable(FileImpl file, long bytes) {
      long totalSize = FileSystemOptions.getContentStoreTotalSize();
      long picturesReservedSize = FileSystemOptions.getPicturesReservedSize();
      if (isInPicturesFolder(file)) {
         if (this._picturesQuotaUsed + bytes <= picturesReservedSize) {
            return true;
         }

         if (this._quotaUsed + bytes <= totalSize) {
            return true;
         }
      } else {
         long quotaOutsidePictures = totalSize - picturesReservedSize;
         long quotaUsedForPicturesOutsideQuota = Math.max(0, this._picturesQuotaUsed - picturesReservedSize);
         if (this._quotaUsed - this._picturesQuotaUsed + quotaUsedForPicturesOutsideQuota + bytes <= quotaOutsidePictures) {
            return true;
         }
      }

      return false;
   }

   public final void resetQuotaUsed() {
      this._quotaUsed = 0;
      this._picturesQuotaUsed = 0;
   }

   public final long getQuotaUsed() {
      return this._quotaUsed;
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      AutoCloseOutputStream.cleanup();
      boolean ret = false;
      if (priority == 0) {
         if (this.removeExpired()) {
            ret = true;
         }

         if (this.purge()) {
            ret = true;
         }
      }

      return ret;
   }

   public static final ContentStoreImpl getInstance() {
      return _instance;
   }

   private final String getPath(String pathAndFilename) {
      int slash = pathAndFilename.lastIndexOf(47);
      if (slash == -1) {
         throw new Object("Path must be absolute.");
      } else {
         return pathAndFilename.substring(0, slash + 1);
      }
   }

   private final String getFilename(String pathAndFilename) {
      int slash = pathAndFilename.lastIndexOf(47);
      if (slash == -1) {
         throw new Object("Path must be absolute.");
      } else {
         String filename = pathAndFilename.substring(slash + 1);
         if (filename != null && filename.length() != 0) {
            return filename;
         } else {
            throw new Object("Bad path");
         }
      }
   }

   static final boolean isInPicturesFolder(FileImpl file) {
      return isInFolder(file, 9);
   }

   static final boolean isInSamplesFolder(Object file) {
      return isInFolder(file, 15);
   }

   private static final boolean isInFolder(Object file, int folderId) {
      if (file instanceof SymbolicLinkImpl) {
         return false;
      }

      FolderImpl folder;
      if (!(file instanceof FileImpl)) {
         if (!(file instanceof FolderImpl)) {
            return false;
         }

         folder = (FolderImpl)file;
      } else {
         folder = (FolderImpl)((FileImpl)file).getFolder();
      }

      while (folder != null) {
         if (folder.getUID() == folderId) {
            return true;
         }

         if (folder.getParentId() == 0) {
            return false;
         }

         folder = folder.getFolder();
      }

      return false;
   }

   private final int findListener(Object search, WeakReference[] listenerList) {
      for (int lv = listenerList.length - 1; lv >= 0; lv--) {
         Object listener = listenerList[lv].get();
         if (listener == search) {
            return lv;
         }
      }

      return -1;
   }

   private ContentStoreImpl() {
      this._crcTable = (IntHashtable)(new Object(40));
      this._crcTable.put(-47428882, "DesertDunes.jpg");
      this._crcTable.put(946818243, "MountainLake.jpg");
      this._crcTable.put(1609918764, "Waterfall.jpg");
      this._crcTable.put(1719905142, "WhiteLily.jpg");
      this._crcTable.put(848085548, "BeachBoats.jpg");
      this._crcTable.put(1014251894, "DesertArch.jpg");
      this._crcTable.put(-1276612881, "WaterDrop.jpg");
      this._crcTable.put(17220433, "BeachWalk.jpg");
      this._crcTable.put(1994532721, "DuskClouds.jpg");
      this._crcTable.put(-80297432, "SunStreaks.jpg");
      this._crcTable.put(-1115501270, "dandelion.jpg");
      this._crcTable.put(153169139, "leaf.jpg");
      this._crcTable.put(795829061, "dunes.jpg");
      this._crcTable.put(-297969014, "building.jpg");
      this._crcTable.put(1617188492, "dusk.jpg");
      this._crcTable.put(-1590026919, "flower.jpg");
      this._crcTable.put(458887867, "arch.jpg");
      this._crcTable.put(-1246508696, "orbital.jpg");
      this._crcTable.put(-1078925901, "autumn.jpg");
      this._crcTable.put(-1556066747, "dolphins.jpg");
      this._crcTable.put(-2031090077, "DesertDunes.jpg");
      this._crcTable.put(-1697343667, "MountainLake.jpg");
      this._crcTable.put(-62657558, "Waterfall.jpg");
      this._crcTable.put(451424706, "WhiteLily.jpg");
      this._crcTable.put(1550375204, "BeachBoats.jpg");
      this._crcTable.put(-407831459, "DesertArch.jpg");
      this._crcTable.put(1323000750, "WaterDrop.jpg");
      this._crcTable.put(1306099356, "BeachWalk.jpg");
      this._crcTable.put(1530291244, "DuskClouds.jpg");
      this._crcTable.put(1532706027, "SunStreaks.jpg");
      this._crcTable.put(863054143, "dandelion.jpg");
      this._crcTable.put(-1684284025, "leaf.jpg");
      this._crcTable.put(1453385657, "dunes.jpg");
      this._crcTable.put(1868841387, "building.jpg");
      this._crcTable.put(-1988092782, "dusk.jpg");
      this._crcTable.put(206618153, "flower.jpg");
      this._crcTable.put(-489464545, "arch.jpg");
      this._crcTable.put(-1168658472, "orbital.jpg");
      this._crcTable.put(1826745540, "autumn.jpg");
      this._crcTable.put(1941767043, "dolphins.jpg");
      FileTable fileTable = this._database.getFileTable();
      int len = fileTable.getArrayInternal().length;

      for (int i = 0; i < len; i++) {
         FileImpl file = fileTable.getArrayInternal()[i];
         if (file != null && !(file instanceof SymbolicLinkImpl)) {
            try {
               this.updateQuotaUsed(file, file.getLength());
            } finally {
               continue;
            }
         }
      }
   }

   private final boolean addFolderInternal(String pathAndFilename, int attributes, boolean verify) {
      synchronized (this._database) {
         if (this._database.getFolderTable().add(pathAndFilename, attributes, verify)) {
            FileSystem.addFileJournalEntry(FileUtilities.encodeString(((StringBuffer)(new Object("/store"))).append(pathAndFilename).toString()), 0);
            this._syncCollection.fileAdded(this._database.getFolderTable().getFolder(pathAndFilename));
            return true;
         } else {
            return false;
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (ContentStoreImpl)ar.getOrWaitFor(-7070747161240792919L);
      if (_instance == null) {
         _instance = new ContentStoreImpl();
         ar.put(-7070747161240792919L, _instance);
         _instance._syncCollection = new ContentStoreSyncCollection();
         _instance._syncCollection.enableSynchronization();
         LowMemoryManager.addLowMemoryListener(_instance);
      }
   }
}
