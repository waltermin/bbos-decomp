package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class BorderRounded extends Border {
   private boolean _filled;
   private boolean _dashed;
   private int _arcWidth;
   private int _arcHeight;
   private Border _outerBackground;
   public static final int FILLED = 1;
   public static final int DASHED = 2;
   public static final int TRANSPARENT = 4;

   public BorderRounded(int top, int right, int bottom, int left, int style) {
      super(top, right, bottom, left);
      this._filled = (style & 1) != 0;
      this._dashed = (style & 2) != 0;
      if ((style & 4) == 0 && Graphics.isColor()) {
         this._outerBackground = new BorderSimple(top, right, bottom, left);
      } else {
         this._outerBackground = new BorderTransparent(top, right, bottom, left);
      }

      this._arcWidth = 2 * Math.min(right, left);
      this._arcHeight = 2 * Math.min(top, bottom);
   }

   @Override
   public void paint(Graphics graphics, XYRect rect) {
      int prevColor = graphics.getColor();
      graphics.setColor(graphics.getBackgroundColor());
      this._outerBackground.paint(graphics, rect);
      graphics.setColor(prevColor);
      if (this._filled) {
         graphics.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, this._arcWidth, this._arcHeight);
      }

      if (this._dashed) {
         graphics.setStipple(-858993460);
      }

      graphics.drawRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, this._arcWidth, this._arcHeight);
      if (this._dashed) {
         graphics.setStipple(-1);
      }
   }
}
