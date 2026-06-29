package net.rim.device.internal.io.store;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;

final class ContentStoreDatabase implements Persistable {
   private FolderTable _folderTable;
   private FileTable _fileTable;
   public static final int UID_APPLICATIONS_FOLDER;
   public static final int UID_DEV_FOLDER;
   public static final int UID_SYSTEM_FOLDER;
   public static final int UID_SYSTEM_SAMPLES_FOLDER;
   public static final int UID_APPDATA_FOLDER;
   public static final int UID_APPDATA_RIM_FOLDER;
   public static final int UID_HOME_FOLDER;
   public static final int UID_HOME_USER_FOLDER;
   public static final int UID_HOME_USER_PICTURES_FOLDER;
   public static final int UID_HOME_USER_RINGTONES_FOLDER;
   public static final int UID_HOME_USER_VOICENOTES_FOLDER;
   public static final int UID_HOME_USER_SETTINGS_FOLDER;
   public static final int UID_TMP_FOLDER;
   public static final int UID_SYSTEM_FONT_FOLDER;
   public static final int UID_SAMPLES_FOLDER;
   public static final int UID_SAMPLES_PICTURES_FOLDER;
   public static final int MAX_INJECTED_FOLDER_UID;
   static final int[] UID_PROTECTED_DATA = new int[]{15, -805044209, 1684630830, 1768369674};
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(7626883756540088672L);
   private static final long PERSISTENT_NAME;
   private static ContentStoreDatabase _database;

   public ContentStoreDatabase() {
   }

   public final void assertOperation(FolderImpl folder, FileImpl file, int operation) {
      if (folder == null) {
         folder = file.getFolder();
      }

      switch (operation) {
         case 0:
            int attributes = folder.getAttributes();
            if ((attributes & 2) == 0 || (attributes & 16) != 0) {
               throw new Object(12);
            }
            break;
         case 1:
            int attributesx = folder.getAttributes();
            if ((attributesx & 2) == 0 || (attributesx & 16) != 0) {
               throw new Object(12);
            }
            break;
         case 128:
            int attributesxx = folder.getAttributes();
            if ((attributesxx & 2) == 0 || (attributesxx & 16) != 0) {
               throw new Object(12);
            }
      }
   }

   final void clear() {
      Hashtable protectedFiles = (Hashtable)(new Object(25));
      Hashtable protectedFolders = (Hashtable)(new Object(5));
      this.getProtectedFilesAndFolders(protectedFiles, protectedFolders);
      this._fileTable = new FileTable();
      this._folderTable = new FolderTable();
      this.populateDefaultAndProtectedFolders(protectedFolders);
      this._fileTable.init(protectedFiles);
      _persist.commit();
   }

   private final void getProtectedFilesAndFolders(Hashtable files, Hashtable folders) {
      FolderTable folderTable = this.getFolderTable();
      if (folderTable != null) {
         IntHashtable folderList = (IntHashtable)(new Object());

         for (int i = 0; i < UID_PROTECTED_DATA.length; i++) {
            FolderImpl folder = folderTable.getFolder(UID_PROTECTED_DATA[i]);
            if (folder != null && files != null) {
               folderTable.getDescendantIds(folderList, folder);
               FileListImpl list = ContentStoreImpl.getInstance().list(folder, true, null, 32, 536870912);

               for (int index = 0; index < list.size(); index++) {
                  FileImpl file = (FileImpl)list.getAt(index);

                  try {
                     files.put(file, file.getFolder().getPath());
                  } finally {
                     continue;
                  }
               }

               folderList.remove(UID_PROTECTED_DATA[i]);
            }
         }

         IntEnumeration enumeration = folderList.keys();

         while (enumeration.hasMoreElements()) {
            FolderImpl folder = folderTable.getFolderForId(enumeration.nextElement());

            try {
               folders.put(folder.getPath(), folder);
            } finally {
               continue;
            }
         }
      }
   }

   final void commit() {
      this.getFileTable().commitPicturesTable();
      _persist.commit();
   }

   final void commit(Object object, boolean force) {
      if (object instanceof FileImpl && ContentStoreImpl.isInPicturesFolder((FileImpl)object)) {
         this.getFileTable().commitPicturesTable();
      }

      if (force) {
         PersistentObject.forceCommit(object);
      } else {
         PersistentObject.commit(object);
      }
   }

   final FileTable getFileTable() {
      return this._fileTable;
   }

   final FolderTable getFolderTable() {
      return this._folderTable;
   }

   public static final ContentStoreDatabase getInstance() {
      return _database;
   }

   public final int getSyncObjectCount() {
      return this.getFolderTable().getSyncObjectCount() + this.getFileTable().getSyncObjectCount();
   }

   private final void populateDefaultAndProtectedFolders(Hashtable protectedFolders) {
      FolderTable folderTable = this.getFolderTable();

      try {
         int attrSystem = 2065;
         int attrUser = 3;
         int attrSystemHidden = attrSystem | 8;
         int attrUserHidden = attrUser | 8;
         folderTable.add("/applications/", attrSystemHidden, false, 1);
         folderTable.add("/dev/", attrSystemHidden, false, 2);
         folderTable.add("/system/", attrSystemHidden, false, 3);
         folderTable.add("/system/samples/", attrSystemHidden, false, 4);
         folderTable.add("/system/fonts/", attrSystemHidden, false, 14);
         folderTable.add("/appdata/", attrSystemHidden, false, 5);
         folderTable.add("/appdata/rim/", attrUserHidden, false, 6);
         folderTable.add("/home/", attrSystem, false, 7);
         folderTable.add("/home/user/", attrUser, false, 8);
         folderTable.add("/home/user/pictures/", attrUser, false, 9);
         folderTable.add("/home/user/ringtones/", attrUser, false, 10);
         folderTable.add("/home/user/voicenotes/", attrUser, false, 11);
         folderTable.add("/home/user/settings/", attrUserHidden, false, 12);
         folderTable.add("/tmp/", attrUserHidden | 4, false, 13);
         folderTable.add("/samples/", attrSystem, false, 15);
         folderTable.add("/samples/pictures/", attrSystem, false, 16);
         if (protectedFolders != null) {
            Enumeration paths = protectedFolders.keys();

            while (paths.hasMoreElements()) {
               String path = (String)paths.nextElement();
               FolderImpl folder = (FolderImpl)protectedFolders.get(path);
               folderTable.add(path, folder.getAttributes(), false);
            }
         }
      } finally {
         return;
      }
   }

   static {
      synchronized (_persist) {
         _database = (ContentStoreDatabase)_persist.getContents();
         if (_database == null) {
            _database = new ContentStoreDatabase();
            _database.clear();
            _persist.setContents(_database, 51, false);
            _persist.commit();
         }
      }
   }
}
