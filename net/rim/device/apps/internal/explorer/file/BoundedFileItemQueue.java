package net.rim.device.apps.internal.explorer.file;

import net.rim.vm.Array;

final class BoundedFileItemQueue {
   private FileItemField[] _items;
   private int _top;
   private int _bottom;

   BoundedFileItemQueue(int maxSize) {
      this._items = new FileItemField[maxSize];
   }

   public final synchronized boolean isEmpty() {
      return this._top == this._bottom;
   }

   public final synchronized int size() {
      return this._items.length;
   }

   public final synchronized void resize(int size) {
      this.flush();
      Array.resize(this._items, size);
   }

   public final synchronized FileItemField removeElement() {
      int bottom = this._bottom;
      if (this._top == bottom) {
         throw new Object();
      }

      FileItemField fileItem = this._items[bottom];
      this._items[bottom] = null;
      this._bottom = ++bottom % this._items.length;
      return fileItem;
   }

   public final synchronized void addElement(FileItemField fileItem) {
      int top = this._top;
      this._items[top] = fileItem;
      int var4;
      this._top = var4 = ++top % this._items.length;
      if (var4 == this._bottom) {
         this._bottom = ++var4 % this._items.length;
      }
   }

   public final synchronized void flush() {
      int next = this._top;

      for (int last = this._bottom; next != last; next = ++next % this._items.length) {
         this._items[next] = null;
      }

      this._top = this._bottom = 0;
   }

   public final synchronized boolean contains(FileItemField fileItem) {
      int next = this._bottom;

      for (int last = this._top; next != last; next = ++next % this._items.length) {
         if (this._items[next] == fileItem) {
            return true;
         }
      }

      return false;
   }
}
