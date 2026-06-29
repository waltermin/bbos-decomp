package net.rim.device.internal.io.store;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

final class FileListImpl {
   private FileListCriteria _criteria;
   private FSDescriptor[] _list = new FSDescriptor[0];

   FileListImpl() {
   }

   public FileListImpl(FileListCriteria criteria) {
      this._criteria = criteria;
   }

   public final void addInternal(FSDescriptor file) {
      Arrays.add(this._list, file);
   }

   private final boolean contains(FSDescriptor file) {
      return Arrays.getIndex(this._list, file) != -1;
   }

   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      elements[destIndex] = this._list[0];
      return 1;
   }

   public final Object getAt(int index) {
      return this._list[index];
   }

   public final FileImpl getAtFileDescriptor(int index) {
      FSDescriptor descriptor = this._list[index];
      return !(descriptor instanceof FileImpl) ? null : (FileImpl)descriptor;
   }

   public final FSDescriptor getAtFSDescriptor(int index) {
      return this._list[index];
   }

   public final FileListCriteria getCriteria() {
      return this._criteria;
   }

   public final int getIndex(Object element) {
      for (int index = this._list.length - 1; index >= 0; index--) {
         if (this._list[index] == element) {
            return index;
         }
      }

      return -1;
   }

   private final void removeInternal(FSDescriptor file) {
      Arrays.remove(this._list, file);
   }

   public final void sort(Comparator comparator) {
      Arrays.sort(this._list, comparator);
   }

   public final int size() {
      return this._list.length;
   }
}
