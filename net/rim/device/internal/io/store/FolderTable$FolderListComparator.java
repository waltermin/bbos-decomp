package net.rim.device.internal.io.store;

import net.rim.device.api.util.Comparator;

class FolderTable$FolderListComparator implements Comparator {
   FolderTable _table;

   FolderTable$FolderListComparator(FolderTable table) {
      this._table = table;
   }

   @Override
   public int compare(Object o1, Object o2) {
      FolderImpl f1 = (FolderImpl)o1;
      FolderImpl f2 = (FolderImpl)o2;
      String path1 = this._table.getPath(f1);
      String path2 = this._table.getPath(f2);
      return path1.compareTo(path2);
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }
}
