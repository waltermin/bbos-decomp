package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.internal.ui.Border;

final class WicletRepetitionManager$RowManager extends HorizontalFieldManager {
   private Manager _rowFocusManager;
   private Manager _content;
   private final WicletRepetitionManager this$0;

   WicletRepetitionManager$RowManager(WicletRepetitionManager this$0, int row) {
      super(1155173304420532224L);
      this.this$0 = this$0;
      this.setTag(WicletRepetitionManager.TAG);
      this._content = new WicletRepetitionManager$1(this);
      this._content.setBorder((Border)(new Object(3, 3, 3, 3)));
      ComponentHelper.buildLayout(this$0._context, this._content, this$0._model, row, 36028797018963968L);
      this._rowFocusManager = new WicletRepetitionManager$RowFocusManager(this$0);
      this.add(this._content);
      this.add(this._rowFocusManager);
   }

   public final void recycle(int row) {
      this._content.deleteAll();
      ComponentHelper.buildLayout(this.this$0._context, this._content, this.this$0._model, row, 36028797018963968L);
      this.updateLayout();
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._rowFocusManager == this.getFieldWithFocus()) {
         graphics.setDrawingStyle(8, false);
         int fg = ThemeAttributeSet.getColor(this, 3);
         int bg = ThemeAttributeSet.getColor(this, 2);
         graphics.setColor(bg);
         graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
         graphics.setColor(fg);
      }

      super.subpaint(graphics);
   }

   public final Field getFirstFocus() {
      return this._rowFocusManager.getField(0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      super.sublayout(width, height > this.this$0._maxRowHeight ? this.this$0._maxRowHeight : height);
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      if (this.this$0._isCollapsible) {
         this.updateLayout();
      }

      this.this$0._paging.setSelectedRelative(this.getIndex());
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      if (this.this$0._isCollapsible) {
         this.updateLayout();
      }
   }
}
