package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class Border3d extends Border {
   private int _colorTop0;
   private int _colorTop1;
   private int _colorRight0;
   private int _colorRight1;
   private int _colorBottom0;
   private int _colorBottom1;
   private int _colorLeft0;
   private int _colorLeft1;
   private int[] _temp1 = new int[5];
   private int[] _temp2 = new int[5];

   public Border3d(int top, int right, int bottom, int left) {
      this(top, right, bottom, left, 13882323, 11119017, 11119017, 13882323, 11119017, 13882323, 13882323, 11119017);
   }

   public Border3d(
      int top,
      int right,
      int bottom,
      int left,
      int colorTop0,
      int colorTop1,
      int colorRight0,
      int colorRight1,
      int colorBottom0,
      int colorBottom1,
      int colorLeft0,
      int colorLeft1
   ) {
      super(top, right, bottom, left);
      this._colorTop0 = colorTop0;
      this._colorTop1 = colorTop1;
      this._colorRight0 = colorRight0;
      this._colorRight1 = colorRight1;
      this._colorBottom0 = colorBottom0;
      this._colorBottom1 = colorBottom1;
      this._colorLeft0 = colorLeft0;
      this._colorLeft1 = colorLeft1;
   }

   @Override
   public void paint(Graphics graphics, XYRect rect) {
      int left = this.getLeft();
      int top = this.getTop();
      int right = this.getRight();
      int bottom = this.getBottom();
      int x2 = rect.x;
      int x0 = rect.x;
      int y1 = rect.y;
      int y0 = rect.y;
      int x3;
      int x1 = x3 = rect.X2();
      int y3;
      int y2 = y3 = rect.Y2();
      int x6;
      int x4 = x6 = rect.x + left;
      int y5;
      int y4 = y5 = rect.y + top;
      int x7;
      int x5 = x7 = x1 - right;
      int y7;
      int y6 = y7 = y2 - bottom;
      if (left == 0) {
         x4 = x0;
         x6 = x2;
      }

      if (right == 0) {
         x5 = x1;
         x7 = x3;
      }

      if (bottom == 0) {
         y6 = y2;
         y7 = y3;
      }

      if (top == 0) {
         y4 = y0;
         y5 = y1;
      }

      int color = graphics.getColor();
      if (left > 0) {
         this._temp1[0] = x0;
         this._temp1[1] = x4;
         this._temp1[2] = x6;
         this._temp1[3] = x2;
         this._temp1[4] = x0;
         this._temp2[0] = y0;
         this._temp2[1] = y4;
         this._temp2[2] = y6;
         this._temp2[3] = y2;
         this._temp2[4] = y0;
         graphics.setColor(this._colorLeft0);
         graphics.drawFilledPath(this._temp1, this._temp2, null, null);
         graphics.setColor(this._colorLeft1);
         graphics.drawLine(x4 - 1, y4, x6 - 1, y6);
      }

      if (right > 0) {
         this._temp1[0] = x1;
         this._temp1[1] = x3;
         this._temp1[2] = x7;
         this._temp1[3] = x5;
         this._temp1[4] = x1;
         this._temp2[0] = y1;
         this._temp2[1] = y3;
         this._temp2[2] = y7;
         this._temp2[3] = y5;
         this._temp2[4] = y1;
         graphics.setColor(this._colorRight0);
         graphics.drawFilledPath(this._temp1, this._temp2, null, null);
         graphics.setColor(this._colorRight1);
         graphics.drawLine(x5, y5, x7, y7);
      }

      if (top > 0) {
         this._temp1[0] = x0;
         this._temp1[1] = x1;
         this._temp1[2] = x5;
         this._temp1[3] = x4;
         this._temp1[4] = x0;
         this._temp2[0] = y0;
         this._temp2[1] = y1;
         this._temp2[2] = y5;
         this._temp2[3] = y4;
         this._temp2[4] = y0;
         graphics.setColor(this._colorTop0);
         graphics.drawFilledPath(this._temp1, this._temp2, null, null);
         graphics.setColor(this._colorTop1);
         graphics.drawLine(x4, y4 - 1, x5, y5 - 1);
      }

      if (bottom > 0) {
         this._temp1[0] = x2;
         this._temp1[1] = x3;
         this._temp1[2] = x7;
         this._temp1[3] = x6;
         this._temp1[4] = x2;
         this._temp2[0] = y2;
         this._temp2[1] = y3;
         this._temp2[2] = y7;
         this._temp2[3] = y6;
         this._temp2[4] = y2;
         graphics.setColor(this._colorBottom0);
         graphics.drawFilledPath(this._temp1, this._temp2, null, null);
         graphics.setColor(this._colorBottom1);
         graphics.drawLine(x6, y6, x7, y7);
      }

      graphics.setColor(color);
   }
}
