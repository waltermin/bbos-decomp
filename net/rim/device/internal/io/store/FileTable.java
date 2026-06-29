package net.rim.device.internal.io.store;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.vm.Array;

final class FileTable implements Persistable {
   private int _empty;
   private int _dead;
   private FileImpl[] _rows = new FileImpl[0];
   private Object[] _indexes = new Object[1];
   private static Vector _picturesAllFiles;
   private static PersistentObject _picturesPersist = RIMPersistentStore.getPersistentObject(3969401279175688951L);
   private static final long PICTURES_ROOT_PERSISTENT_NAME = 3969401279175688951L;
   private static final int INDEX_ORIGINAL_URL = 0;
   private static final int INDEX_COUNT = 1;
   private static String _preloadedCodPrefix;

   final void optionsChanged() {
      _picturesPersist.setReservedMemorySize((int)FileSystemOptions.getPicturesReservedSize());
   }

   FileTable() {
      this._indexes[0] = new Object();
   }

   final void init(Hashtable files) {
      Enumeration elem = files.keys();

      while (elem.hasMoreElements()) {
         try {
            FileImpl file = (FileImpl)elem.nextElement();
            String folderName = (String)files.get(file);
            FolderImpl folder = ContentStoreImpl.getInstance().getFolder(folderName);
            int attributes = file.getAttributes();
            file.setFolder(folder, false, true);
            file.setAttributes(attributes, 0);
            this.addInternal(file, true);
         } finally {
            continue;
         }
      }
   }

   final void addInternal(FileImpl file, boolean isSystem) {
      if (!isSystem) {
         this.getFolderTable().assertOperation(file.getFolder(), 0);
      }

      FolderImpl folder = ContentStoreDatabase.getInstance().getFolderTable().getFolder(file.getFolder(), file.getName());
      if (folder != null && folder.isAlive()) {
         throw new Object(7);
      }

      FileImpl old = this.get(file.getFolder(), file.getBindName());
      if (file != old) {
         if (old != null) {
            old.remove();
         }

         if (ContentStoreImpl.isInPicturesFolder(file)) {
            _picturesAllFiles.addElement(file);
            _picturesPersist.commit();
         }

         int location = 0;
         if (this._empty != 0) {
            int end = this._rows.length;
            location = 0;

            while (location < end && this._rows[location] != null) {
               location++;
            }

            this._empty--;
         } else {
            Array.resize(this._rows, this._rows.length + 1);
            location = this._rows.length - 1;
         }

         this._rows[location] = file;
         this.commit();
      }
   }

   final void clearPicturesTable() {
      _picturesAllFiles.removeAllElements();
      _picturesPersist.commit();
   }

   final void commitPicturesTable() {
      _picturesPersist.commit();
   }

   private final void commit() {
      ContentStoreDatabase.getInstance().commit(this, false);
   }

   public final FileImpl get(int uid) {
      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FileImpl file = this._rows[lv];
         if (file != null && file.getUID() == uid) {
            return file;
         }
      }

      return null;
   }

   public final FileImpl get(FolderImpl folder, String filename) {
      int folderId = folder.getId();
      filename = IOUtilities.getBindName(filename);
      FileImpl.validateName(filename);

      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FileImpl file = this._rows[lv];
         if (file != null && file.getFolderId() == folderId && file.getBindName().equals(filename) && file.isAlive()) {
            return file;
         }
      }

      return null;
   }

   public final FileImpl getByOriginalURL(String url) {
      return (FileImpl)((Hashtable)this._indexes[0]).get(url);
   }

   final FileImpl[] getArrayInternal() {
      return this._rows;
   }

   private final FolderTable getFolderTable() {
      return ContentStoreDatabase.getInstance().getFolderTable();
   }

   public final int getSyncObjectCount() {
      int count = 0;

      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FileImpl file = this._rows[lv];
         if (file != null && file.isArchivable()) {
            count++;
         }
      }

      return count;
   }

   public final void list(FileListImpl list) {
      FolderTable folderTable = ContentStoreDatabase.getInstance().getFolderTable();
      FileListCriteria criteria = list.getCriteria();
      boolean searchSubFolders = criteria.isSubfoldersIncluded();
      FolderImpl[] rootFolderArray = criteria.getRootFolders();
      Hashtable rootFolders = null;
      if (rootFolderArray == null) {
         rootFolders = (Hashtable)(new Object(1));
         rootFolders.put(ContentStoreDatabase.getInstance().getFolderTable().getFolder("/"), rootFolders);
      } else {
         rootFolders = (Hashtable)(new Object(rootFolderArray.length));

         for (int index = rootFolderArray.length - 1; index >= 0; index--) {
            if (rootFolderArray[index] != null) {
               rootFolders.put(rootFolderArray[index], rootFolders);
            }
         }

         if (searchSubFolders) {
            if (rootFolders.containsKey(folderTable.getFolder("/"))) {
               rootFolders.clear();
               rootFolders.put(folderTable.getFolderForId(0), rootFolders);
            } else {
               for (int index = rootFolderArray.length - 1; index >= 0; index--) {
                  if (rootFolderArray[index] != null) {
                     for (FolderImpl folder = rootFolderArray[index].getFolder(); folder != folder.getFolder(); folder = folder.getFolder()) {
                        if (rootFolders.containsKey(folder)) {
                           rootFolders.remove(rootFolderArray[index]);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      String contentType = criteria.getContentType();
      int attrOff = criteria.getAttributesOff();
      int attrOn = criteria.getAttributesOn();
      int drmAttrOff = criteria.getDrmAttributesOff();
      int drmAttrOn = criteria.getDrmAttributesOn();
      IntHashtable folderList = (IntHashtable)(new Object());
      if (rootFolderArray != null) {
         if (searchSubFolders) {
            for (int index = rootFolderArray.length - 1; index >= 0; index--) {
               if (rootFolderArray[index] != null) {
                  folderTable.getDescendantIds(folderList, rootFolderArray[index]);
               }
            }
         } else {
            for (int index = rootFolderArray.length - 1; index >= 0; index--) {
               if (rootFolderArray[index] != null) {
                  FolderImpl folder = rootFolderArray[index];
                  folderList.put(folder.getId(), folder);
               }
            }
         }
      }

      boolean showHidden = (attrOff & 8) == 0;
      if ((attrOff & 536870912) == 0) {
         if (searchSubFolders) {
            Enumeration enumeration = folderList.elements();

            while (enumeration.hasMoreElements()) {
               FolderImpl folder = (FolderImpl)enumeration.nextElement();
               if (!rootFolders.containsKey(folder)) {
                  if ((folder.getAttributes() & 8) != 0) {
                     if (showHidden) {
                        list.addInternal(folder);
                     }
                  } else {
                     list.addInternal(folder);
                  }
               }
            }
         } else if (rootFolderArray != null) {
            for (int rootIndex = rootFolderArray.length - 1; rootIndex >= 0; rootIndex--) {
               if (rootFolderArray[rootIndex] != null) {
                  Enumeration enumeration = folderTable.getChildren(rootFolderArray[rootIndex]);

                  while (enumeration.hasMoreElements()) {
                     FolderImpl folder = (FolderImpl)enumeration.nextElement();
                     if ((folder.getAttributes() & 8) != 0) {
                        if (showHidden) {
                           list.addInternal(folder);
                        }
                     } else {
                        list.addInternal(folder);
                     }
                  }
               }
            }
         }
      }

      int end = this._rows.length;

      for (int lv = 0; lv < end; lv++) {
         FileImpl file = this._rows[lv];
         if (file != null) {
            try {
               int attr = file.getAttributes();
               int drmAttr = file.getDrmAttributes();
               if (folderList.get(file.getFolderId()) != null
                  && (attr & attrOff) == 0
                  && (drmAttr & drmAttrOff) == 0
                  && (attr & attrOn) == attrOn
                  && (drmAttr & drmAttrOn) == drmAttrOn) {
                  list.addInternal(file);
               }
            } finally {
               continue;
            }
         }
      }

      list.sort(new FileTable$FileListComparator());
   }

   final void notifyTable(int action, FileImpl file) {
      switch (action) {
         case 1:
            if (ContentStoreImpl.isInPicturesFolder(file)) {
               _picturesAllFiles.removeElement(file);
               _picturesPersist.commit();
            }

            this._dead++;
            this.purge();
      }
   }

   public final boolean purge() {
      if (this._dead == 0) {
         return false;
      }

      int deleted = 0;
      int end = this._rows.length;

      for (int lv = 0; lv < end; lv++) {
         FileImpl file = this._rows[lv];
         if (file != null && !file.isAlive()) {
            deleted++;
            this._rows[lv] = null;
            if (file instanceof SymbolicLinkImpl) {
               SymbolicLinkImpl link = (SymbolicLinkImpl)file;
               if (link.isCodURL()) {
                  String codLinkName = link.getLink();
                  if (codLinkName != null) {
                     this.onCodLinkDeleted(codLinkName, lv);
                  }
               }
            }

            LowMemoryManager.markAsRecoverable(file);
         }
      }

      this._empty += deleted;
      this._dead = 0;
      this.commit();
      return deleted != 0;
   }

   private final void onCodLinkDeleted(String codLinkName, int lv) {
      if (codLinkName.startsWith(_preloadedCodPrefix)) {
         int endIndex = codLinkName.indexOf(47, _preloadedCodPrefix.length());
         if (endIndex > 0) {
            String codPrefix = codLinkName.substring(0, endIndex + 1);
            if (!this.haveCodLink(codPrefix, lv)) {
               int handle = CodeModuleManager.getModuleHandle(codPrefix.substring(6, endIndex));
               if (handle != 0) {
                  CodeModuleManager.deleteModuleEx(handle, true);
               }
            }
         }
      }
   }

   private final boolean haveCodLink(String codLinkPrefix, int lv) {
      int below = lv;

      while (--below >= 0) {
         FileImpl file = this._rows[below];
         if (file instanceof SymbolicLinkImpl && this.isLinkedTo((SymbolicLinkImpl)file, codLinkPrefix)) {
            return true;
         }

         if (file != null) {
            break;
         }
      }

      int max = this._rows.length - 1;
      int above = lv;

      while (++above <= max) {
         FileImpl file = this._rows[above];
         if (file instanceof SymbolicLinkImpl && this.isLinkedTo((SymbolicLinkImpl)file, codLinkPrefix)) {
            return true;
         }

         if (file != null) {
            break;
         }
      }

      while (below > 0) {
         FileImpl file = this._rows[--below];
         if (file instanceof SymbolicLinkImpl && this.isLinkedTo((SymbolicLinkImpl)file, codLinkPrefix)) {
            return true;
         }
      }

      while (above < max) {
         FileImpl file = this._rows[++above];
         if (file instanceof SymbolicLinkImpl && this.isLinkedTo((SymbolicLinkImpl)file, codLinkPrefix)) {
            return true;
         }
      }

      return false;
   }

   private final boolean isLinkedTo(SymbolicLinkImpl link, String codPrefix) {
      if (link.isCodURL()) {
         String codLink = link.getLink();
         return codLink != null && codLink.startsWith(codPrefix);
      } else {
         return false;
      }
   }

   public final void sanitize() {
      int end = this._rows.length;

      for (int lv = 0; lv < end; lv++) {
         FileImpl file = this._rows[lv];
         if (file instanceof SymbolicLinkImpl) {
            SymbolicLinkImpl symLink = (SymbolicLinkImpl)file;

            try {
               if (symLink.isAlive() && symLink.isCodURL() && symLink.getLength() <= 0) {
                  String link = symLink.getLink();
                  int endIndex = link.indexOf(47, _preloadedCodPrefix.length());
                  if (endIndex > 0 && link.startsWith(_preloadedCodPrefix)) {
                     String moduleName = link.substring(0, endIndex + 1);
                     if (CodeModuleManager.getModuleHandle(moduleName) == 0) {
                        String msg = ((StringBuffer)(new Object("removing empty link: "))).append(link).toString();
                        EventLogger.logEvent(-7509200465648525729L, msg.toString().getBytes(), 0);
                        symLink.remove();
                     }
                  }
               }
            } finally {
               continue;
            }
         }
      }
   }

   public final void removeAllFilesIn(FolderImpl folder) {
      int folderId = folder.getId();
      int end = this._rows.length;

      for (int lv = 0; lv < end; lv++) {
         FileImpl file = this._rows[lv];
         if (file != null && file.getFolderId() == folderId && file.isAlive()) {
            try {
               file.remove();
            } finally {
               continue;
            }
         }
      }
   }

   final synchronized boolean removeExpired() {
      boolean anythingRemoved = false;
      long now = System.currentTimeMillis();
      int attr = 268435520;

      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FileImpl file = this._rows[lv];
         if (file != null) {
            try {
               long expiry = file.getTimeExpiry();
               long fileAttr = file.getAttributes();
               if (expiry <= now && (fileAttr & attr) == attr) {
                  file.remove();
                  anythingRemoved = true;
               }
            } finally {
               continue;
            }
         }
      }

      return anythingRemoved;
   }

   public final void update(FileImpl updated) {
      FileImpl file = this.get(updated.getUID());
      if (file != null) {
         file.setContent(updated.openInputStream(), false);
         file.setName(updated.getName(), false);
         file.setLastModified(file.getTimeModify());
         file.setTimeCreate(file.getTimeCreate());
         file.setDrmAttributes(updated.getDrmAttributes());
         file.setFolder(updated.getFolder(), false, false);
      }
   }

   static {
      synchronized (_picturesPersist) {
         _picturesAllFiles = (Vector)_picturesPersist.getContents();
         if (_picturesAllFiles == null) {
            _picturesAllFiles = (Vector)(new Object());
            _picturesPersist.setContents(_picturesAllFiles, 51, false);
            _picturesPersist.setReservedMemorySize((int)FileSystemOptions.getPicturesReservedSize());
            _picturesPersist.commit();
         }
      }

      _preloadedCodPrefix = "cod://net_rim_bb_media";
   }
}
