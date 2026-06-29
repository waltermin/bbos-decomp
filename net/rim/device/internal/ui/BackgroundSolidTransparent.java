package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

class BackgroundSolidTransparent extends Background {
   private int _color;
   private int _alpha;

   BackgroundSolidTransparent(int color) {
      this._alpha = color >>> 24;
      this._color = color & 16777215;
   }

   @Override
   public void draw(Graphics graphics, XYRect rect) {
      int originalAlpha = graphics.getGlobalAlpha();
      int original = graphics.getColor();
      graphics.setGlobalAlpha(this._alpha);
      graphics.setColor(this._color);
      graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
      graphics.setColor(original);
      graphics.setGlobalAlpha(originalAlpha);
   }

   @Override
   public boolean isTransparent() {
      return this._alpha < 255;
   }
}
