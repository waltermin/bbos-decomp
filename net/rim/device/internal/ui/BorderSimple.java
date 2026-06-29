package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class BorderSimple extends Border {
   private boolean _colorSet;
   private int _colorTop;
   private int _colorRight;
   private int _colorBottom;
   private int _colorLeft;
   public static final int STYLE_NONE = 0;
   public static final int STYLE_HIDDEN = 0;
   public static final int STYLE_DOTTED = 0;
   public static final int STYLE_DASHED = 0;
   public static final int STYLE_SOLID = 0;
   public static final int STYLE_DOUBLE = 0;
   public static final int STYLE_GROOVE = 0;
   public static final int STYLE_RIDGE = 0;
   public static final int STYLE_INSET = 0;
   public static final int STYLE_OUTSET = 0;

   public BorderSimple(int top, int right, int bottom, int left) {
      super(top, right, bottom, left);
   }

   public BorderSimple(int top, int right, int bottom, int left, int colorTop, int colorRight, int colorBottom, int colorLeft) {
      super(top, right, bottom, left);
      this._colorSet = true;
      this._colorTop = colorTop;
      this._colorRight = colorRight;
      this._colorBottom = colorBottom;
      this._colorLeft = colorLeft;
   }

   @Override
   public void paint(Graphics graphics, XYRect rect) {
      int color = graphics.getColor();
      if (this._colorSet) {
         graphics.setColor(this._colorTop);
      }

      graphics.fillRect(rect.x, rect.y, rect.width, this.getTop());
      if (this._colorSet) {
         graphics.setColor(this._colorRight);
      }

      graphics.fillRect(rect.X2() - this.getRight(), rect.y, this.getRight(), rect.height);
      if (this._colorSet) {
         graphics.setColor(this._colorBottom);
      }

      graphics.fillRect(rect.x, rect.Y2() - this.getBottom(), rect.width, this.getBottom());
      if (this._colorSet) {
         graphics.setColor(this._colorLeft);
      }

      graphics.fillRect(rect.x, rect.y, this.getLeft(), rect.height);
      graphics.setColor(color);
   }
}
