package net.rim.device.internal.io.store;

import java.util.Enumeration;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileUtilities;

final class FolderImpl implements FSDescriptor, Persistable, SyncObject {
   private int _uid;
   private int _folderId;
   private int _parentId;
   private String _name;
   private String _bindName;
   private int _attributes;
   private static final int ARCHIVE_ATTR_ON;
   private static final int ARCHIVE_ATTR_OFF;
   private static final int ATTR_CONSTANT;

   public final FileImpl addFile(String name, int open) {
      FileImpl prev = this.getFile(name);
      if (prev != null && prev.isAlive()) {
         if ((open & 6) == 0) {
            throw new Object(7);
         }

         if (!(prev instanceof SymbolicLinkImpl)) {
            return prev;
         }

         prev.remove();
      }

      if (this.getId() == 0) {
         throw new Object("Folder cannot be root.");
      }

      if ((open & 1) == 0) {
         throw new Object(8);
      }

      boolean isSystem = false;
      if (StringUtilities.strEqualIgnoreCase(name, "BBThumbs.dat")) {
         isSystem = true;
      }

      synchronized (ContentStoreDatabase.getInstance()) {
         FileImpl file = new FileImpl(0);
         file.setFolder(this, false, isSystem);
         file.setName(name, false);
         ContentStoreDatabase.getInstance().getFileTable().addInternal(file, isSystem);
         ContentStoreImpl.getInstance().getSyncCollection().fileAdded(file);
         FileSystem.addFileJournalEntry(file.getJSRPath(), 0);
         return file;
      }
   }

   public final boolean addFolder(String name) {
      ContentStoreDatabase database = ContentStoreDatabase.getInstance();
      FolderTable folderTable = database.getFolderTable();
      synchronized (database) {
         if (folderTable.add(this, name, 0, true)) {
            FolderImpl obj = folderTable.getFolder(this, name);
            if (obj == null) {
               return false;
            }

            ContentStoreImpl.getInstance().getSyncCollection().fileAdded(obj);
            FileSystem.addFileJournalEntry(obj.getJSRPath(), 0);
            return true;
         } else {
            return false;
         }
      }
   }

   public final SymbolicLinkImpl addSymbolicLink(String name, int open) {
      FileImpl prev = this.getFile(name);
      if (prev != null && prev.isAlive()) {
         if ((open & 2) == 0) {
            throw new Object(7);
         }

         if (prev instanceof SymbolicLinkImpl) {
            return (SymbolicLinkImpl)prev;
         }

         prev.remove();
      }

      if (this.getId() == 0) {
         throw new Object("Folder cannot be root.");
      }

      if ((open & 1) == 0) {
         throw new Object("File does not exist.");
      }

      synchronized (ContentStoreDatabase.getInstance()) {
         SymbolicLinkImpl link = new SymbolicLinkImpl(0);
         link.setFolder(this, false, true);
         link.setName(name, false);
         ContentStoreDatabase.getInstance().getFileTable().addInternal(link, true);
         ContentStoreImpl.getInstance().getSyncCollection().fileAdded(link);
         FileSystem.addFileJournalEntry(link.getJSRPath(), 0);
         return link;
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   final void setName(String name, boolean notify) {
      int error = FileImpl.validateName(name);
      if (error == 0 || (this._parentId == 0 || this._parentId == this._uid) && name != null && name.length() == 0) {
         String oldPath = null;
         if (notify) {
            oldPath = this.getJSRPath();
         }

         this._name = name;
         this._bindName = IOUtilities.getBindName(name);
         if (notify) {
            this.commit();
            FileSystem.addFileJournalEntry(this.getJSRPath(), oldPath, 3);
         }
      } else {
         throw new Object();
      }
   }

   public final boolean isArchivable() {
      return (this._attributes & 32) == 32 && (this._attributes & 2052) == 0;
   }

   public final boolean isAncestorOf(FolderImpl folder) {
      FolderImpl root = folderTable().getFolder("/");
      if (this == root) {
         return true;
      }

      while (folder != root) {
         if (folder == this) {
            return true;
         }

         folder = folder.getFolder();
      }

      return false;
   }

   public final FSDescriptor get(String name) {
      FSDescriptor descriptor = this.getFile(name);
      if (descriptor == null) {
         descriptor = this.getFolder(name);
      }

      return descriptor;
   }

   public final FileImpl getFile(String name) {
      return fileTable().get(this, name);
   }

   public final boolean hasNullAncestor() {
      return folderTable().hasNullAncestor(this);
   }

   public final FolderImpl getFolder(String name) {
      return folderTable().getFolder(this, name);
   }

   public final int getId() {
      return this._folderId;
   }

   final void commit() {
      ContentStoreDatabase.getInstance().commit(this, false);
   }

   public final String getPath() {
      return folderTable().getPath(this);
   }

   public final int getParentId() {
      return this._parentId;
   }

   @Override
   public final String getJSRPath() {
      try {
         return FileUtilities.encodeString(((StringBuffer)(new Object("/store"))).append(this.getPath()).toString());
      } finally {
         ;
      }
   }

   @Override
   public final String getName() {
      return this._name;
   }

   @Override
   public final FolderImpl getFolder() {
      return folderTable().getFolderForId(this._parentId);
   }

   @Override
   public final boolean isAlive() {
      return (this._attributes & 32) != 0;
   }

   @Override
   public final String getBindName() {
      return this._bindName;
   }

   @Override
   public final String getAttribute(String tag) {
      return null;
   }

   @Override
   public final void remove() {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         if (!this.isAlive()) {
            throw new Object();
         }

         if (this._uid >= 0 && this._uid <= 16) {
            throw new Object(12);
         }

         fileTable().removeAllFilesIn(this);
         Enumeration children = folderTable().getChildren(this);

         while (children.hasMoreElements()) {
            FolderImpl folder = (FolderImpl)children.nextElement();
            folder.remove();
         }

         this._attributes &= -33;
         this.commit();
         this.notifyTable(129);
         ContentStoreImpl.getInstance().getSyncCollection().fileRemoved(this);
         FileSystem.addFileJournalEntry(this.getJSRPath(), 1);
      }
   }

   @Override
   public final void resurrect() {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         if (this.isAlive()) {
            throw new Object();
         }

         FolderImpl parent = this.getFolder();
         if (!parent.isAlive()) {
            parent.resurrect();
         }

         this._attributes |= 32;
         this.commit();
         this.notifyTable(128);
      }
   }

   @Override
   public final void setAttributes(int attributesOn, int attributesOff) {
      int newAttributes = this._attributes;
      newAttributes |= attributesOn & 134217727;
      newAttributes &= ~attributesOff | -134217728;
      if (newAttributes != this._attributes) {
         this._attributes = newAttributes;
         FileSystem.addFileJournalEntry(this.getJSRPath(), 2);
      }
   }

   @Override
   public final void setName(String name) {
      this.setName(name, true);
   }

   @Override
   public final int getAttributes() {
      return this._attributes;
   }

   private static final FolderTable folderTable() {
      return ContentStoreDatabase.getInstance().getFolderTable();
   }

   private final void notifyTable(int action) {
      FolderTable table = ContentStoreImpl.getInstance().getDatabase().getFolderTable();
      table.notifyTable(action, this);
   }

   FolderImpl() {
      this._parentId = 0;
   }

   @Override
   public final int hashCode() {
      return this._uid;
   }

   private static final FileTable fileTable() {
      return ContentStoreDatabase.getInstance().getFileTable();
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof FolderImpl)) {
         return false;
      }

      FolderImpl folder = (FolderImpl)obj;
      return this._uid == folder._uid;
   }

   FolderImpl(int folder, int parent, int attributes, String name, int uid) {
      if (uid != 0) {
         this._uid = uid;
      } else {
         this._uid = UIDGenerator.getUID();
      }

      this._folderId = folder;
      this._parentId = parent;
      this._attributes = attributes | 32 | 536870912;
      this.setName(name, false);
   }
}
