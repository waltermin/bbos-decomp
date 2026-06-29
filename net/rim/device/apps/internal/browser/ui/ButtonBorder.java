package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.Border;

public final class ButtonBorder extends Border {
   private int _state;
   private int _backgroundColor;
   private Border _parentBorder;
   public static final int STATE_NORMAL;
   public static final int STATE_FOCUS;
   public static final int STATE_ACTIVE;

   public ButtonBorder(int state, int backgroundColor, Border parentBorder) {
      super(
         parentBorder != null ? parentBorder.getTop() + 3 : 5,
         parentBorder != null ? parentBorder.getRight() + 3 : 5,
         parentBorder != null ? parentBorder.getBottom() + 3 : 5,
         parentBorder != null ? parentBorder.getLeft() + 3 : 5
      );
      this._state = state;
      this._backgroundColor = backgroundColor;
      this._parentBorder = parentBorder;
   }

   @Override
   public final void paint(Graphics graphics, XYRect rect) {
      int oldColor = graphics.getColor();
      int x0 = rect.x;
      int y0 = rect.y;
      int x1 = rect.X2();
      int y1 = rect.Y2();
      int width = rect.width;
      int height = rect.height;
      if (this._parentBorder != null) {
         this._parentBorder.paint(graphics, rect);
         x0 += this._parentBorder.getLeft();
         y0 += this._parentBorder.getTop();
         x1 -= this._parentBorder.getRight();
         y1 -= this._parentBorder.getBottom();
         width -= this._parentBorder.getRight() + this._parentBorder.getLeft();
         height -= this._parentBorder.getBottom() + this._parentBorder.getTop();
      }

      graphics.setColor(this._backgroundColor);
      graphics.fillRect(x0, y0, width, height);
      if (this._parentBorder == null) {
         graphics.setColor(16777215);
         graphics.drawRect(x0, y0, width, height);
         graphics.drawRect(x0 + 1, y0 + 1, width - 2, height - 2);
      }

      graphics.setColor(0);
      if (this._state != 0) {
         if (this._parentBorder == null) {
            graphics.drawRect(x0, y0, width, height);
         }

         int offsetAdd0 = this._parentBorder == null ? 3 : 1;
         int offsetAdd1 = this._parentBorder == null ? 4 : 1;
         if (this._state == 2) {
            offsetAdd0++;
            offsetAdd1--;
         }

         int tempx0 = x0 + offsetAdd0;
         int tempy0 = y0 + offsetAdd0;
         graphics.drawRect(tempx0, tempy0, x1 - offsetAdd1 - tempx0, y1 - offsetAdd1 - tempy0);
      } else if (this._parentBorder == null) {
         graphics.drawLine(x0, y1 - 1, x1 - 1, y1 - 1);
         graphics.drawLine(x1 - 1, y0, x1 - 1, y1 - 1);
      }

      if (this._parentBorder == null) {
         graphics.setColor(4342338);
         if (this._state != 2) {
            graphics.drawLine(x1 - 2, y0 + 1, x1 - 2, y1 - 2);
            graphics.drawLine(x0 + 1, y1 - 2, x1 - 2, y1 - 2);
         } else {
            graphics.drawLine(x0 + 1, y0 + 1, x0 + 1, y1 - 2);
            graphics.drawLine(x0 + 1, y0 + 1, x1 - 2, y0 + 1);
         }

         graphics.setColor(8684676);
         if (this._state != 2) {
            graphics.drawLine(x1 - 3, y0 + 2, x1 - 3, y1 - 3);
            graphics.drawLine(x0 + 2, y1 - 3, x1 - 3, y1 - 3);
         } else {
            graphics.drawLine(x0 + 2, y0 + 2, x1 - 3, y0 + 2);
            graphics.drawLine(x0 + 2, y0 + 2, x0 + 2, y1 - 3);
         }
      }

      graphics.setColor(oldColor);
   }
}
