package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;
import net.rim.wica.runtime.resources.RuntimeResources;

class PagingController {
   private Field _nullField;
   private PagedListModifier _view;
   private PagingController$PagingNavigation _pagingNavigation;
   private int _size;
   private int _itemsPerPage;
   private int _selected;
   int _currentPage;
   int _oldCount;
   PagingController$WicletPopupDialog _indexDialog;
   MenuItem _goFirst = new PagingController$3(this, RuntimeResources.getString(23), 0, 0);
   MenuItem _goLast = new PagingController$4(this, RuntimeResources.getString(24), 0, 0);
   MenuItem _goIndex = new PagingController$5(this, RuntimeResources.getString(25), 0, 0);

   PagingController(PagedListModifier view, int size, int itemsPerPage, int selected) {
      this._view = view;
      this.init(size, itemsPerPage, selected);
      this.reconstruct(-1);
   }

   int getSelected() {
      return this._selected;
   }

   void setSelected(int index) {
      this._selected = index;
      if (this._selected > -1) {
         this._currentPage = this.calcCurrentPage();
      }
   }

   void setSelectedAndSize(int index, int newSize) {
      int oldCount = this.getDisplayItemsCount();
      boolean wasPagingUsed = this.isPagingUsed();
      this.init(newSize, this._itemsPerPage, index);
      this.reconstructOrUpdate(oldCount, wasPagingUsed, -1);
   }

   void setSelectedRelative(int index) {
      this._selected = index < 0 ? -1 : this._currentPage * this._itemsPerPage + index;
   }

   boolean isPagingUsed() {
      return this._size > this._itemsPerPage;
   }

   private void init(int size, int itemsPerPage, int selected) {
      this._size = size;
      this._itemsPerPage = itemsPerPage;
      this._selected = selected;
      this._currentPage = this.calcCurrentPage();
   }

   public void reconstructOrUpdate(int oldCount, boolean wasPagingUsed, int selectIndex) {
      if (oldCount == this.getDisplayItemsCount() && wasPagingUsed == this.isPagingUsed()) {
         this.update(selectIndex);
      } else {
         this.reconstruct(selectIndex);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void reconstruct(int selectIndex) {
      this._view.beginReconstruction();
      int fieldCount = this._view.getFieldCount();
      int fromIndex = this.getDisplayFromIndex();
      int count = this.getDisplayItemsCount();
      if (fieldCount > 0) {
         if (this._nullField == null) {
            this._nullField = (Field)(new Object());
         }

         this._view.add(this._nullField);
         this._view.deleteRange(0, fieldCount);
      }

      if (this.isPagingUsed()) {
         if (this._pagingNavigation == null) {
            this._pagingNavigation = new PagingController$PagingNavigation(this);
         }

         this._pagingNavigation.update();
         this._view.add(this._pagingNavigation);
      }

      this._view.add(fromIndex, count);
      if (fieldCount > 0) {
         boolean var7 = false /* VF: Semaphore variable */;

         label41:
         try {
            var7 = true;
            this._view.deleteRange(0, 1);
            var7 = false;
         } finally {
            if (var7) {
               this._view.unFocus();
               this._view.deleteRange(0, 1);
               break label41;
            }
         }
      }

      this.updateSelection(fromIndex, count, selectIndex);
      this._view.endReconstruction();
   }

   private void update(int selectIndex) {
      int fromIndex = this.getDisplayFromIndex();
      int count = this.getDisplayItemsCount();
      this._view.update(fromIndex, count);
      if (this.isPagingUsed() && this._pagingNavigation != null) {
         this._pagingNavigation.update();
      }

      this.updateSelection(fromIndex, count, selectIndex);
      if (this._selected != -1 && this._view instanceof WicletSingleSelectListField) {
         ChoiceControl controller = (ChoiceControl)((ChoiceField)this._view).getModel();
         controller.eventOccurred(1);
      } else {
         if (this._selected != -1 && this._view instanceof WicletRepetitionManager) {
            UIControl controller = (UIControl)((WicletRepetitionManager)this._view).getModel();
            controller.eventOccurred(1);
         }
      }
   }

   private void updateSelection(int fromIndex, int count, int selectIndex) {
      if (selectIndex > -1) {
         this._view.setSelected(selectIndex);
      } else if (this._selected >= fromIndex && this._selected < fromIndex + count) {
         this._view.setSelected(this._selected - fromIndex);
      } else {
         this._view.setSelected(-1);
      }
   }

   private int calcCurrentPage() {
      return this._selected > 0 && this._itemsPerPage > 0
         ? this._selected / this._itemsPerPage
         : Math.min(this._currentPage, Math.max(0, this.getNumberOfPages() - 1));
   }

   private boolean isLastPage(int index) {
      return (index + 1) * this._itemsPerPage >= this._size;
   }

   private boolean isFirstPage(int index) {
      return index == 0;
   }

   private int getDisplayFromIndex() {
      return this._currentPage * this._itemsPerPage;
   }

   public int getDisplayItemsCount() {
      return this.isLastPage(this._currentPage) ? this._size - this.getDisplayFromIndex() : this._itemsPerPage;
   }

   public int getNumberOfPages() {
      return this._size % this._itemsPerPage > 0 ? this._size / this._itemsPerPage + 1 : this._size / this._itemsPerPage + 0;
   }

   private String getPageNumberLabelText() {
      return ((StringBuffer)(new Object())).append(this._currentPage + 1).append("/").append(this.getNumberOfPages()).toString();
   }

   protected void makeContextMenu(Menu menu, int context) {
      if (this.isPagingUsed() && context == 0) {
         menu.add(this._goIndex);
         if (this._currentPage == 0) {
            menu.add(this._goLast);
            return;
         }

         if (this._currentPage == this.getNumberOfPages() - 1) {
            menu.add(this._goFirst);
            return;
         }

         menu.add(this._goFirst);
         menu.add(this._goLast);
      }
   }

   private boolean jumpToPage() {
      int numPages = this.getNumberOfPages();
      int inputPage = -1;
      this._oldCount = this.getDisplayItemsCount();
      String inputString = this._indexDialog.getString();
      if (inputString.length() > 0 && inputString.length() <= Integer.toString(numPages).length()) {
         inputPage = Integer.parseInt(inputString);
      }

      if (inputPage > 0 && inputPage <= numPages) {
         this._currentPage = inputPage - 1;
         if (this._view instanceof WicletSingleSelectListField) {
            this.reconstructOrUpdate(this._oldCount, true, 0);
            return true;
         } else {
            this.reconstructOrUpdate(this._oldCount, true, -1);
            return true;
         }
      } else {
         Dialog.alert(RuntimeResources.getString(90, String.valueOf(numPages)));
         return false;
      }
   }
}
