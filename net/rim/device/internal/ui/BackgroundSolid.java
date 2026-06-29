package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

class BackgroundSolid extends Background {
   private int _color;

   BackgroundSolid(int color) {
      this._color = color;
   }

   @Override
   public void draw(Graphics graphics, XYRect rect) {
      int original = graphics.getColor();
      graphics.setColor(this._color);
      graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
      graphics.setColor(original);
   }

   @Override
   public boolean isTransparent() {
      return false;
   }
}
