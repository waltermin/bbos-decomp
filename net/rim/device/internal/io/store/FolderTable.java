package net.rim.device.internal.io.store;

import java.util.Enumeration;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntStack;
import net.rim.device.api.util.MultiMap;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.util.StringUtilitiesInternal;
import net.rim.vm.Array;

final class FolderTable implements Persistable {
   private FolderImpl[] _rows;
   private int _dead;
   private int _empty;
   private Object[] _indexes = new Object[1];
   public static final int ROOT;
   private static final int INDEX_CHILD;
   private static final int INDEX_COUNT;

   FolderTable() {
      this._rows = new FolderImpl[1];
      this._rows[0] = new FolderImpl(0, 0, 2065, "", 0);
      this._indexes[0] = new Object();
   }

   public final boolean add(FolderImpl parent, String name, int attributes, boolean verify) {
      return this.add(parent, name, attributes, verify, 0);
   }

   public final boolean add(FolderImpl parent, String name, int attributes, boolean verify, int uid) {
      int parentId = parent.getId();
      if (parentId >= 0 && parentId < this._rows.length && this._rows[parentId] != null) {
         FolderImpl folder = this.getFolder(parent, name);
         if (folder != null && (folder.getAttributes() & 4096) == 0 && folder.isAlive()) {
            return false;
         }

         if (ContentStoreDatabase.getInstance().getFileTable().get(parent, name) != null) {
            throw new Object(((StringBuffer)(new Object("Already exists: "))).append(name).toString());
         }

         if (verify) {
            this.assertOperation(parent, 128);
         }

         if (folder != null && !folder.isAlive()) {
            folder.resurrect();
         } else {
            int id;
            if (folder == null) {
               id = this._rows.length;
               Array.resize(this._rows, id + 1);
            } else {
               id = folder.getId();
            }

            if (attributes == 0 && (this._rows[parentId].getAttributes() & 4096) == 0) {
               attributes = this._rows[parentId].getAttributes();
               attributes &= -28;
               attributes |= 3;
            }

            folder = new FolderImpl(id, parentId, attributes, name, uid);
            this._rows[id] = folder;
         }

         ((MultiMap)this._indexes[0]).add(parent, folder);
         this.commit();
         return true;
      } else {
         throw new Object("Invalid parent");
      }
   }

   public final boolean add(String pathAndFilename, int attributes, boolean verify) {
      return this.add(pathAndFilename, attributes, verify, 0);
   }

   public final boolean add(String pathAndFilename, int attributes, boolean verify, int uid) {
      if (pathAndFilename.charAt(pathAndFilename.length() - 1) != '/') {
         throw new Object("folders must end in '/'");
      }

      int pathend = pathAndFilename.length() - 2;
      pathend = pathAndFilename.lastIndexOf(47, pathend);
      if (pathend < 0) {
         throw new Object("requires path");
      }

      if (uid != 0 && this.getFolder(uid) != null) {
         throw new Object("Duplicate UID");
      }

      String parentPath = pathAndFilename.substring(0, pathend + 1);
      String name = pathAndFilename.substring(pathend + 1, pathAndFilename.length() - 1);
      FolderImpl parent = this.getFolder(parentPath);
      if (parent == null) {
         this.add(parentPath, attributes, verify);
         parent = this.getFolder(parentPath);
      }

      return this.add(parent, name, attributes, verify, uid);
   }

   public final void assertOperation(FolderImpl folder, int operation) {
      try {
         switch (operation) {
            case 0:
               int attributes = folder.getAttributes();
               if ((attributes & 4098) == 0 || (attributes & 16) != 0) {
                  throw new Object(12);
               }
               break;
            case 1:
               int attributesx = folder.getAttributes();
               if ((attributesx & 4098) == 0 || (attributesx & 16) != 0) {
                  throw new Object(12);
               }
               break;
            case 128:
               int attributesxx = folder.getAttributes();
               if ((attributesxx & 4098) == 0 || (attributesxx & 16) != 0) {
                  throw new Object(12);
               }
         }
      } finally {
         throw new Object(((StringBuffer)(new Object("Invalid folder id: "))).append(folder.getId()).toString());
      }
   }

   private final void commit() {
      ContentStoreDatabase.getInstance().commit(this, false);
   }

   final FolderImpl[] getArrayInternal() {
      return this._rows;
   }

   final Enumeration getChildren(FolderImpl folder) {
      return ((MultiMap)this._indexes[0]).elements(folder);
   }

   public final Comparator getComparatorForList() {
      return new FolderTable$FolderListComparator(this);
   }

   public final Comparator getComparatorForSync() {
      return new FolderTable$FolderSyncComparator(this);
   }

   public final int getSize() {
      return this._rows.length - this._empty - this._dead;
   }

   final void getDescendantIds(IntHashtable descendants, FolderImpl folder) {
      IntStack stack = (IntStack)(new Object());
      descendants.put(folder.getId(), folder);
      stack.push(folder.getId());
      int count = this._rows.length;

      while (!stack.isEmpty()) {
         if (count-- < 0) {
            throw new Object();
         }

         int id = stack.pop();
         Enumeration enumeration = this.getChildren(this.getFolderForId(id));

         while (enumeration.hasMoreElements()) {
            FolderImpl child = (FolderImpl)enumeration.nextElement();
            int elem = child.getId();
            descendants.put(elem, child);
            stack.push(elem);
         }
      }
   }

   public final FolderImpl getFolder(int uid) {
      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FolderImpl folder = this._rows[lv];
         if (folder != null && folder.getUID() == uid) {
            return folder;
         }
      }

      return null;
   }

   public final FolderImpl getFolder(String path) {
      FolderImpl folder = this._rows[0];
      synchronized (this._rows) {
         int pos1 = 1;
         int pos2 = 0;

         while (true) {
            pos2 = path.indexOf(47, pos1);
            if (pos2 == -1) {
               if (pos1 != path.length()) {
                  throw new Object("Path must start and end with a '/'.");
               }
               break;
            }

            if (pos2 == pos1) {
               throw new Object("Bad pathname.");
            }

            folder = this.getFolder(folder, path.substring(pos1, pos2));
            if (folder == null) {
               break;
            }

            pos1 = pos2 + 1;
         }

         return folder;
      }
   }

   final FolderImpl getFolderForId(int id) {
      try {
         return this._rows[id];
      } finally {
         ;
      }
   }

   public final FolderImpl getFolder(FolderImpl parent, String name) {
      int parentId = parent.getId();

      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FolderImpl folder = this._rows[lv];
         if (folder != null && folder.getParentId() == parentId && folder.getName().equals(name)) {
            return folder;
         }
      }

      return null;
   }

   public final int getLevel(FolderImpl folder) {
      int level;
      for (level = 0; folder.getId() > 0; level++) {
         folder = folder.getFolder();
      }

      return level;
   }

   public final String getPath(FolderImpl folder) {
      synchronized (this._rows) {
         StringBuffer buffer = StringUtilitiesInternal.getScratchBuffer();
         String result;
         synchronized (buffer) {
            this.getPathHelper(folder, buffer);
            result = buffer.toString();
            buffer.setLength(0);
         }

         return result;
      }
   }

   private final void getPathHelper(FolderImpl folder, StringBuffer buffer) {
      if (folder.getId() != 0) {
         this.getPathHelper(folder.getFolder(), buffer);
      }

      buffer.append(folder.getName());
      buffer.append('/');
   }

   public final int getSyncObjectCount() {
      int count = 0;

      for (int lv = this._rows.length - 1; lv >= 0; lv--) {
         FolderImpl folder = this._rows[lv];
         if (folder != null && folder.isArchivable()) {
            count++;
         }
      }

      return count;
   }

   final boolean hasNullAncestor(FolderImpl folder) {
      while (folder.getId() > 0) {
         folder = folder.getFolder();
         if (folder == null) {
            return true;
         }
      }

      return false;
   }

   final void notifyTable(int action, FolderImpl folder) {
      switch (action) {
         case 129:
            if (((MultiMap)this._indexes[0]).containsKey(folder)) {
               throw new Object();
            } else {
               ((MultiMap)this._indexes[0]).removeValue(folder.getFolder(), folder);
               this._dead++;
               this.purge();
            }
      }
   }

   public final boolean purge() {
      if (this._dead == 0) {
         return false;
      }

      int deleted = 0;

      label51:
      try {
         int end = this._rows.length;

         for (int lv = 0; lv < end; lv++) {
            FolderImpl folder = this._rows[lv];
            if (folder != null && !folder.isAlive()) {
               deleted++;
               this._rows[lv] = null;
               LowMemoryManager.markAsRecoverable(folder);
            }
         }
      } finally {
         break label51;
      }

      this._empty += deleted;
      this._dead = 0;
      this.commit();
      return deleted != 0;
   }

   public final void setName(FolderImpl folder, String name) {
      folder.setName(name);
   }

   public final void update(FolderImpl updated) {
      FolderImpl folder = this.getFolder(updated.getUID());
      if (folder != null) {
         this.setName(folder, updated.getName());
         folder.setAttributes(updated.getAttributes(), 0);
      }
   }
}
