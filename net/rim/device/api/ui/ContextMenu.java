package net.rim.device.api.ui;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class ContextMenu {
   private MenuItem[] _items;
   private Field _target;
   private int _defaultItem = -1;
   private boolean _defaultIsSet;
   private static ContextMenu _menu = new ContextMenu();
   public static final int UNDEFINED;

   private ContextMenu() {
      this._items = new MenuItem[0];
   }

   public final void addItem(MenuItem item) {
      if (this._target == null) {
         throw new IllegalStateException();
      }

      int last = this._items.length;
      Array.resize(this._items, last + 1);
      this._items[last] = item;
   }

   public final int addSeparatorInternal() {
      int maxOrdinal = this._items.length > 0 ? this._items[this._items.length - 1].getOrdinal() : 0;
      this.addItem(MenuItem.separator(maxOrdinal + 1));
      return this._items.length - 1;
   }

   public final MenuItem getDefaultItem() {
      if (this._defaultItem == -1) {
         return this.getSize() > 0 ? this._items[this._items.length - 1] : null;
      } else {
         return this._items[this._defaultItem];
      }
   }

   public final int getDefault() {
      return this._defaultItem;
   }

   public final boolean setDefaultItem(MenuItem item) {
      int end = this._items.length;

      for (int lv = 0; lv < end; lv++) {
         MenuItem litem = this._items[lv];
         if (litem == item) {
            this._defaultItem = lv;
            this._defaultIsSet = true;
            return true;
         }
      }

      return false;
   }

   public final void setDefault(int position) {
      this._defaultItem = position;
      if (this._defaultItem == -1) {
         this._defaultIsSet = false;
      }
   }

   public final boolean isDefaultSet() {
      return this._defaultIsSet;
   }

   public static final ContextMenu getInstance() {
      return _menu;
   }

   public final int getSize() {
      return this._items.length;
   }

   public final Field getTarget() {
      return this._target;
   }

   public final MenuItem[] getItems() {
      MenuItem[] itemsCopy = new MenuItem[this._items.length];
      System.arraycopy(this._items, 0, itemsCopy, 0, this._items.length);
      return itemsCopy;
   }

   public final boolean isEmpty() {
      return this.getSize() == 0;
   }

   public final void setTarget(Field target) {
      this._target = target;
      this.clear();
   }

   public final void clear() {
      this._defaultItem = -1;
      this._defaultIsSet = false;
      Array.resize(this._items, 0);
   }

   public final void sort() {
      MenuItem _default = null;
      if (this._defaultItem != -1) {
         _default = this.getDefaultItem();
      }

      Arrays.sort(this._items, 0, this._items.length, MenuItem.ORDINAL_COMPARATOR);
      if (_default != null) {
         this.setDefaultItem(_default);
      }
   }
}
