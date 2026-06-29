package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.collection.ReadableList;

final class FolderList$EmptyList implements ReadableList {
   @Override
   public final int size() {
      return 0;
   }

   @Override
   public final Object getAt(int index) {
      throw new ArrayIndexOutOfBoundsException();
   }

   @Override
   public final int getIndex(Object element) {
      return -1;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      int i = count;

      while (--i >= 0) {
         elements[destIndex + i] = this.getAt(index + i);
      }

      return count;
   }
}
