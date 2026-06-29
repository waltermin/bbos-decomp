package net.rim.device.internal.io.store;

import java.util.Enumeration;
import net.rim.device.api.util.CharacterUtilities;

final class ListEnumeration implements Enumeration {
   private int _index;
   private FileListImpl _list;
   private String _filter;
   private boolean _returnFileInfo;

   public final FSDescriptor nextDescriptor() {
      FSDescriptor fd = (FSDescriptor)this._list.getAt(this._index);
      this._index++;
      return fd;
   }

   @Override
   public final Object nextElement() {
      FSDescriptor fd = (FSDescriptor)this._list.getAt(this._index);
      this._index++;
      if (!this._returnFileInfo) {
         return fd instanceof FolderImpl ? ((StringBuffer)(new Object())).append(fd.getName()).append("/").toString() : fd.getName();
      }

      int attrib = 0;
      long modTime = 0;
      long fileSize = -1;

      try {
         int fdAttrib = fd.getAttributes();
         if ((fdAttrib & 8) != 0) {
            attrib |= 8;
         }

         if ((fdAttrib & 1024) != 0) {
            attrib |= 1;
         }

         if ((fdAttrib & 2) == 0 && (fdAttrib & 1) != 0) {
            attrib |= 2;
         }

         if ((fdAttrib & 16) != 0) {
            attrib |= 4;
         }

         if (fd instanceof FolderImpl) {
            attrib |= 16;
         }

         if (fd instanceof FileImpl) {
            FileImpl file = (FileImpl)fd;
            fileSize = file.getLength();
            modTime = file.getTimeModify();
         }
      } finally {
         return new Object(fd.getName(), fileSize, modTime, attrib);
      }

      return new Object(fd.getName(), fileSize, modTime, attrib);
   }

   @Override
   public final boolean hasMoreElements() {
      for (int listLength = this._list.size(); this._index < listLength; this._index++) {
         FSDescriptor fd = (FSDescriptor)this._list.getAt(this._index);
         if (this.matchesFilter(fd.getName())) {
            return true;
         }
      }

      return false;
   }

   private final boolean matchesFilter(String name) {
      if (this._filter == null) {
         return true;
      }

      int filterLength = this._filter.length();
      int nameLength = name.length();
      int filterOffset = 0;
      int nameOffset = 0;

      while (nameOffset < nameLength && filterOffset < filterLength) {
         char filterCh = this._filter.charAt(filterOffset++);
         if (filterCh != '*') {
            if (CharacterUtilities.toLowerCase(name.charAt(nameOffset++)) != CharacterUtilities.toLowerCase(filterCh)) {
               return false;
            }
         } else {
            if (filterOffset >= filterLength) {
               return true;
            }

            char nextCh = this._filter.charAt(filterOffset++);
            if (nextCh == '*') {
               filterOffset--;
            } else {
               boolean found = false;

               while (true) {
                  if (nameOffset < nameLength) {
                     if (name.charAt(nameOffset++) != nextCh) {
                        continue;
                     }

                     found = true;
                  }

                  if (!found) {
                     return false;
                  }
                  break;
               }
            }
         }
      }

      if (nameOffset == nameLength) {
         for (int i = this._filter.length() - 1; i >= filterOffset; i--) {
            if (this._filter.charAt(i) != '*') {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ListEnumeration(FileListImpl list, String filter, boolean returnFileInfo) {
      this._list = list;
      this._filter = filter;
      this._returnFileInfo = returnFileInfo;
      if (this._filter != null && (this._filter.length() == 0 || this._filter.equals("*"))) {
         this._filter = null;
      }
   }
}
