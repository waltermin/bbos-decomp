package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.Border;

class FocusBorder extends Border {
   private Manager _mgr;

   public FocusBorder(Manager mgr, int top, int right, int bottom, int left) {
      super(top, right, bottom, left);
      this._mgr = mgr;
   }

   @Override
   public void paint(Graphics graphics, XYRect rect) {
      int divisor = 2;
      if (this._mgr.getFieldWithFocus() != null) {
         divisor = 1;
      }

      int top = this.getTop() / divisor;
      int right = this.getRight() / divisor;
      int left = this.getLeft() / divisor;
      int bottom = this.getBottom() / divisor;
      graphics.fillRect(rect.x, rect.y, rect.width, top);
      graphics.fillRect(rect.X2() - right, rect.y, right, rect.height);
      graphics.fillRect(rect.x, rect.Y2() - bottom, rect.width, bottom);
      graphics.fillRect(rect.x, rect.y, left, rect.height);
   }
}
