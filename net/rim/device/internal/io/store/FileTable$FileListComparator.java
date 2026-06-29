package net.rim.device.internal.io.store;

import net.rim.device.api.util.Comparator;

class FileTable$FileListComparator implements Comparator {
   private Comparator _folderCompare = ContentStoreDatabase.getInstance().getFolderTable().getComparatorForList();

   @Override
   public int compare(Object o1, Object o2) {
      try {
         FSDescriptor f1 = (FSDescriptor)o1;
         FSDescriptor f2 = (FSDescriptor)o2;
         FolderImpl folder1 = f1.getFolder();
         FolderImpl folder2 = f2.getFolder();
         int folderCmp = this._folderCompare.compare(folder1, folder2);
         return folderCmp != 0 ? folderCmp : f1.getBindName().compareTo(f2.getBindName());
      } finally {
         ;
      }
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }
}
