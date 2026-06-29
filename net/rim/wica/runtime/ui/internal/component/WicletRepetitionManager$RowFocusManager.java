package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.wica.runtime.ui.internal.WicaScreen;

final class WicletRepetitionManager$RowFocusManager extends VerticalFieldManager {
   private boolean _isRowWithTwoFocusesLessThanScreenHeight;
   private WicletRepetitionManager$FocusField _focusFieldTop;
   private WicletRepetitionManager$FocusField _focusFieldMiddle;
   private WicletRepetitionManager$FocusField _focusFieldBottom;
   private final WicletRepetitionManager this$0;

   WicletRepetitionManager$RowFocusManager(WicletRepetitionManager this$0) {
      this.this$0 = this$0;
      this._focusFieldTop = new WicletRepetitionManager$FocusField(this$0, null);
      this._focusFieldTop.setFocusable(true);
      this._focusFieldMiddle = new WicletRepetitionManager$FocusField(this$0, null);
      this._focusFieldBottom = new WicletRepetitionManager$FocusField(this$0, null);
      this.add(this._focusFieldTop);
      this.add(this._focusFieldMiddle);
      this.add(this._focusFieldBottom);
   }

   @Override
   protected final int firstFocus(int direction) {
      return 0;
   }

   @Override
   protected final int nextFocus(int direction, boolean alt) {
      if (this._isRowWithTwoFocusesLessThanScreenHeight) {
         int nextFocusIndex = super.nextFocus(direction, alt);
         if (nextFocusIndex == -1) {
            return nextFocusIndex;
         } else {
            return ((WicaScreen)this.getScreen()).isFieldVisible(this.getField(nextFocusIndex)) ? -1 : nextFocusIndex;
         }
      } else {
         return super.nextFocus(direction, alt);
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      int contentHeight = this.getManager().getField(0).getHeight();
      this._isRowWithTwoFocusesLessThanScreenHeight = false;
      if (contentHeight > this.this$0._screenContentHeightMinusScrollHeight) {
         int halfOfContentHeight = contentHeight / 2;
         if (halfOfContentHeight > this.this$0._screenContentHeightMinusScrollHeight) {
            int thirdOfContentHeight = contentHeight / 3;
            this._focusFieldTop.setExtentHeight(thirdOfContentHeight);
            this._focusFieldMiddle.setFocusable(true);
            this._focusFieldMiddle.setExtentHeight(contentHeight - 2 * thirdOfContentHeight);
            this._focusFieldBottom.setFocusable(true);
            this._focusFieldBottom.setExtentHeight(thirdOfContentHeight);
         } else {
            if (contentHeight < this.this$0._screenContentHeight) {
               this._isRowWithTwoFocusesLessThanScreenHeight = true;
            }

            this._focusFieldTop.setExtentHeight(halfOfContentHeight);
            this._focusFieldMiddle.setFocusable(true);
            this._focusFieldMiddle.setExtentHeight(contentHeight - halfOfContentHeight);
            this._focusFieldBottom.setFocusable(false);
            this._focusFieldBottom.setExtentHeight(0);
         }
      } else {
         this._focusFieldTop.setExtentHeight(contentHeight);
         this._focusFieldMiddle.setFocusable(false);
         this._focusFieldMiddle.setExtentHeight(0);
         this._focusFieldBottom.setFocusable(false);
         this._focusFieldBottom.setExtentHeight(0);
      }

      super.sublayout(width, height);
   }
}
