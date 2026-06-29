package net.rim.device.internal.io.store;

import net.rim.device.api.util.Comparator;

class FolderTable$FolderSyncComparator implements Comparator {
   FolderTable _table;

   FolderTable$FolderSyncComparator(FolderTable table) {
      this._table = table;
   }

   @Override
   public int compare(Object o1, Object o2) {
      FolderImpl f1 = (FolderImpl)o1;
      FolderImpl f2 = (FolderImpl)o2;
      return this._table.getLevel(f1) - this._table.getLevel(f2);
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }
}
