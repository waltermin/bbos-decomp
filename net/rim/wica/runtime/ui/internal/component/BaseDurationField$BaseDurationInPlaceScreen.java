package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.InPlaceScreen;

final class BaseDurationField$BaseDurationInPlaceScreen extends InPlaceScreen {
   private XYRect _rect = (XYRect)(new Object());

   public BaseDurationField$BaseDurationInPlaceScreen(BaseDurationField original, BaseDurationField fake, long style) {
      super(original, fake, style | 1);
   }

   @Override
   protected final void sublayout(int width, int height) {
      BaseDurationField original = (BaseDurationField)this.getOriginalField();
      BaseDurationField fake = (BaseDurationField)this.getField();
      XYRect extent = this._rect;
      int borderAndPaddingLeft = this.getPaddingLeft() + this.getBorderLeft();
      int borderAndPaddingTop = this.getPaddingTop() + this.getBorderTop();
      int borderAndPaddingWidth = borderAndPaddingLeft + this.getPaddingRight() + this.getBorderRight();
      int borderAndPaddingHeight = borderAndPaddingTop + this.getPaddingBottom() + this.getBorderBottom();
      original.getInPlaceRect(extent);
      original.transformToScreen(extent);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      extent.height = fake.getHeight() + borderAndPaddingHeight;
      extent.width = fake.getWidth() + borderAndPaddingWidth;
      extent.x -= borderAndPaddingLeft;
      extent.y -= borderAndPaddingTop;
      extent.x = extent.x < 0 ? 0 : extent.x;
      extent.y = extent.y < 0 ? 0 : extent.y;
      int screenWidth = Graphics.getScreenWidth();
      extent.width = extent.width > screenWidth ? screenWidth : extent.width;
      int screenHeight = Graphics.getScreenHeight();
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
