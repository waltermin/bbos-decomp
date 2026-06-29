package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

class PagingController$PagingNavigation extends HorizontalFieldManager {
   ButtonField _previousButton;
   ButtonField _nextButton;
   LabelField _pageNumberLabelField;
   boolean _isPrevious;
   boolean _isNext;
   private final PagingController this$0;

   PagingController$PagingNavigation(PagingController this$0) {
      this.this$0 = this$0;
      this._previousButton = new PagingController$NavigagionButtonField("<<");
      this._previousButton.setChangeListener(new PagingController$1(this));
      this._nextButton = new PagingController$NavigagionButtonField(">>");
      this._nextButton.setChangeListener(new PagingController$2(this));
      this._pageNumberLabelField = new LabelField(this$0.getPageNumberLabelText(), 51539607552L);
      this.add(this._pageNumberLabelField);
   }

   void update() {
      boolean previous = !this.this$0.isFirstPage(this.this$0._currentPage);
      boolean next = !this.this$0.isLastPage(this.this$0._currentPage);
      if (previous && !this._isPrevious) {
         this.insert(this._previousButton, 0);
      }

      if (next && !this._isNext) {
         this.add(this._nextButton);
      }

      if (!previous && this._isPrevious) {
         this.delete(this._previousButton);
      }

      if (!next && this._isNext) {
         if (this._nextButton.isFocus()) {
            this._previousButton.setFocus();
         }

         this.delete(this._nextButton);
      }

      this._isPrevious = previous;
      this._isNext = next;
      this._pageNumberLabelField.setText(this.this$0.getPageNumberLabelText());
   }
}
