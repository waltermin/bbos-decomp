package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.wica.runtime.ui.internal.FocusHolder;

final class WicletRepetitionManager$FocusField extends FocusHolder {
   private int _extentHeight;
   private Field _focusRelay;
   private final WicletRepetitionManager this$0;

   private WicletRepetitionManager$FocusField(WicletRepetitionManager this$0) {
      this.this$0 = this$0;
      this._extentHeight = 0;
   }

   public final void setExtentHeight(int height) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(0, this._extentHeight);
   }

   public final void setFocusRelay(Field focusRelay) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void setFocus() {
      boolean wasCollapsible = this.this$0._isCollapsible;
      this.this$0._isCollapsible = false;
      if (this._focusRelay != null) {
         super.setFocus();
         this._focusRelay.setFocus();
         this._focusRelay = null;
      } else {
         super.setFocus();
      }

      if (wasCollapsible) {
         this.this$0._isCollapsible = true;
         this.this$0._rowsContainer.doUpdateLayout();
      }
   }

   WicletRepetitionManager$FocusField(WicletRepetitionManager x0, WicletRepetitionManager$1 x1) {
      this(x0);
   }
}
