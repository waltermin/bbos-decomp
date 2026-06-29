package net.rim.device.api.ui.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.InPlaceScreen;

class DateInPlaceScreen extends InPlaceScreen {
   private XYRect _rect = new XYRect();

   public DateInPlaceScreen(DateField original, DateField fake, long style) {
      super(original, fake, style | 1);
   }

   @Override
   protected void sublayout(int width, int height) {
      DateField originalField = (DateField)this.getOriginalField();
      XYRect extent = this._rect;
      originalField.getInPlaceRect(extent);
      originalField.transformToScreen(extent);
      int borderAndPaddingLeft = this.getPaddingLeft() + this.getBorderLeft();
      int borderAndPaddingTop = this.getPaddingTop() + this.getBorderTop();
      int borderAndPaddingWidth = borderAndPaddingLeft + this.getPaddingRight() + this.getBorderRight();
      int borderAndPaddingHeight = borderAndPaddingTop + this.getPaddingBottom() + this.getBorderBottom();
      DateField fake = (DateField)this.getField();
      int prefWidth = fake.getPreferredWidth();
      DateField delegateField = (DateField)this.getDelegate().getField(0);
      if (delegateField.getMinimumWidth() == 0) {
         delegateField.setMinimumWidth(prefWidth);
      }

      int innerWidth = Math.min(width, prefWidth) + borderAndPaddingWidth;
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(innerWidth, height);
      extent.height = fake.getHeight() + borderAndPaddingHeight;
      extent.width = fake.getWidth() + borderAndPaddingWidth;
      extent.x -= borderAndPaddingLeft;
      extent.y -= borderAndPaddingTop;
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
