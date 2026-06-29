package net.rim.device.internal.ui.security.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class LockIconField extends Field {
   private int _x;
   private int _y;

   public LockIconField(int x, int y) {
      super(36028797018963968L);
      this._x = x;
      this._y = y;
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(this._x + 5, this._y + 5);
   }

   @Override
   protected void paint(Graphics graphics) {
      drawLock(graphics, this._x, this._y);
   }

   public static void drawLock(Graphics graphics, int x, int y) {
      int colour = graphics.getColor();
      graphics.setColor(16711680);
      graphics.drawRect(x, y + 2, 5, 3);
      graphics.drawPoint(x + 1, y + 1);
      graphics.drawPoint(x + 2, y);
      graphics.drawPoint(x + 3, y + 1);
      graphics.drawPoint(x + 1, y + 3);
      graphics.drawPoint(x + 3, y + 3);
      graphics.setColor(colour);
   }
}
