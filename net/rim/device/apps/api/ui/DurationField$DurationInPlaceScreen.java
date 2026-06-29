package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.InPlaceScreen;

final class DurationField$DurationInPlaceScreen extends InPlaceScreen {
   private XYRect _rect = (XYRect)(new Object());

   public DurationField$DurationInPlaceScreen(DurationField original, DurationField fake, long style) {
      super(original, fake, style | 1);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int space = 2;
      XYRect extent = this._rect;
      DurationField original = (DurationField)this.getOriginalField();
      original.getInPlaceRect(extent);
      original.transformToScreen(extent);
      int borderAndPaddingLeft = this.getPaddingLeft() + this.getBorderLeft();
      int borderAndPaddingTop = this.getPaddingTop() + this.getBorderTop();
      int borderAndPaddingWidth = borderAndPaddingLeft + this.getPaddingRight() + this.getBorderRight();
      int borderAndPaddingHeight = borderAndPaddingTop + this.getPaddingBottom() + this.getBorderBottom();
      DurationField fake = (DurationField)this.getField();
      int innerWidth = Math.max(extent.width, fake.getPreferredWidth());
      this.setPositionDelegate(space, space);
      this.layoutDelegate(innerWidth + borderAndPaddingWidth, extent.height + borderAndPaddingHeight);
      extent.height = fake.getHeight() + borderAndPaddingWidth + 2 * space;
      extent.width = fake.getWidth() + borderAndPaddingHeight + 2 * space;
      extent.x -= space + borderAndPaddingLeft;
      extent.y -= space + borderAndPaddingTop;
      extent.x = extent.x < 0 ? 0 : extent.x;
      extent.y = extent.y < 0 ? 0 : extent.y;
      int screenWidth = Display.getWidth();
      extent.width = extent.width > screenWidth ? screenWidth : extent.width;
      int screenHeight = Display.getHeight();
      extent.height = extent.height > screenHeight ? screenHeight : extent.height;
      int rightOverextend = extent.X2() - screenWidth;
      if (rightOverextend > 0) {
         extent.x -= rightOverextend;
      }

      int bottomOverextend = extent.Y2() - screenHeight;
      if (bottomOverextend > 0) {
         extent.y -= bottomOverextend;
      }

      extent.width -= borderAndPaddingWidth;
      extent.height -= borderAndPaddingHeight;
      this.setPosition(extent.x, extent.y);
      this.setExtent(extent.width, extent.height);
   }
}
