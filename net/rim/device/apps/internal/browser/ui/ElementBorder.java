package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.TextFlowRegion;

public final class ElementBorder extends Border {
   private TextFlowRegion _region;
   private int _colorTop;
   private int _colorRight;
   private int _colorBottom;
   private int _colorLeft;

   public ElementBorder(TextFlowRegion region, int top, int right, int bottom, int left) {
      this(region, top, right, bottom, left, -1, -1, -1, -1);
   }

   public ElementBorder(TextFlowRegion region, int top, int right, int bottom, int left, int colorTop, int colorRight, int colorBottom, int colorLeft) {
      super(top, right, bottom, left);
      this._region = region;
      this._colorTop = colorTop;
      this._colorRight = colorRight;
      this._colorBottom = colorBottom;
      this._colorLeft = colorLeft;
      this._region = region;
   }

   @Override
   public final void paint(Graphics graphics, XYRect rect) {
      int color = graphics.getColor();
      int foregroundColor = this._region._foregroundColour;
      if (foregroundColor == -1) {
         foregroundColor = color;
      }

      if (this._colorTop != -1) {
         graphics.setColor(this._colorTop);
      } else {
         graphics.setColor(foregroundColor);
      }

      graphics.fillRect(rect.x, rect.y, rect.width, this.getTop());
      if (this._colorRight != -1) {
         graphics.setColor(this._colorRight);
      } else {
         graphics.setColor(foregroundColor);
      }

      graphics.fillRect(rect.X2() - this.getRight(), rect.y, this.getRight(), rect.height);
      if (this._colorBottom != -1) {
         graphics.setColor(this._colorBottom);
      } else {
         graphics.setColor(foregroundColor);
      }

      graphics.fillRect(rect.x, rect.Y2() - this.getBottom(), rect.width, this.getBottom());
      if (this._colorLeft != -1) {
         graphics.setColor(this._colorLeft);
      } else {
         graphics.setColor(foregroundColor);
      }

      graphics.fillRect(rect.x, rect.y, this.getLeft(), rect.height);
      graphics.setColor(color);
   }
}
