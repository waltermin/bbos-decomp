package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.component.Scrollbar;

final class ApplicationScreen$ScreenManager extends Manager {
   private FocusManager _focusManager;
   private ScreenTitleField _titleField = new ScreenTitleField();
   private Scrollbar _verticalScrollbar;
   private boolean _showInFullScreen;

   ApplicationScreen$ScreenManager() {
      super(3458764513820540928L);
      this._focusManager = new FocusManager();
      this._verticalScrollbar = new Scrollbar();
      this.add(this._titleField);
      this.add(this._focusManager);
      this.add(this._verticalScrollbar);
      this._verticalScrollbar.setClient(this._focusManager.getScrollingManager());
   }

   @Override
   protected final void sublayout(int width, int height) {
      int titleHeight = 0;
      this.setPositionChild(this._titleField, 0, 0);
      if (!this._showInFullScreen && this._titleField.getTitle() != null) {
         this.layoutChild(this._titleField, width, height);
         titleHeight = this._titleField.getHeight();
      } else {
         this.layoutChild(this._titleField, width, 0);
      }

      int availableHeight = height - titleHeight;
      this.layoutChild(this._verticalScrollbar, width, availableHeight);
      int remainingWidth = width - this._verticalScrollbar.getWidth();
      this.setPositionChild(this._verticalScrollbar, remainingWidth, titleHeight);
      this.layoutChild(this._focusManager, remainingWidth, availableHeight);
      this.setPositionChild(this._focusManager, 0, titleHeight);
      this.setVirtualExtent(
         Math.max(this._titleField.getWidth(), this._focusManager.getVirtualWidth()), this._focusManager.getVirtualHeight() + this._titleField.getHeight()
      );
      this.setExtent(width, height);
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int top = clip.y;
      int bottom = clip.y + clip.height;
      int numFields = this.getFieldCount();
      int i = this.getFieldAtLocation(clip.x, top);
      if (i != -1) {
         while (i < numFields) {
            Field field = this.getField(i);
            if (field.getTop() >= bottom) {
               return;
            }

            this.paintChild(graphics, field);
            i++;
         }
      }
   }
}
