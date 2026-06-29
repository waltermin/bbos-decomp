package net.rim.device.apps.api.framework.file;

import net.rim.device.api.util.Comparator;

class ExplorerRegistry$AliasEntryComparator implements Comparator {
   public boolean equals(Object o1, Object o2) {
      if (o1 instanceof AliasFileEntry && o2 instanceof AliasFileEntry) {
         AliasFileEntry entry1 = (AliasFileEntry)o1;
         AliasFileEntry entry2 = (AliasFileEntry)o2;
         return entry1.getName().equals(entry2.getName());
      } else {
         return false;
      }
   }

   @Override
   public int compare(Object o1, Object o2) {
      AliasFileEntry entry1 = null;
      if (!(o1 instanceof AliasFileEntry)) {
         return -1;
      }

      entry1 = (AliasFileEntry)o1;
      AliasFileEntry entry2 = null;
      if (!(o2 instanceof AliasFileEntry)) {
         return 1;
      }

      entry2 = (AliasFileEntry)o2;
      return entry1.getName().compareTo(entry2.getName());
   }

   private ExplorerRegistry$AliasEntryComparator() {
   }

   ExplorerRegistry$AliasEntryComparator(ExplorerRegistry$1 x0) {
      this();
   }
}
