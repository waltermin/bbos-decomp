package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class BorderBitmap extends Border {
   private int _topCorners;
   private int _rightCorners;
   private int _bottomCorners;
   private int _leftCorners;
   private int _x;
   private int _y;
   private int _width;
   private int _height;
   private Bitmap _bitmap;
   private int _rop;
   private Bitmap _bitmapTop;
   private Bitmap _bitmapBottom;
   private Bitmap _bitmapLeft;
   private Bitmap _bitmapRight;
   private Background _background;

   public BorderBitmap(int top, int right, int bottom, int left, Bitmap bitmap) {
      this(top, right, bottom, left, bitmap, top, right, bottom, left);
   }

   public BorderBitmap(int top, int right, int bottom, int left, Bitmap bitmap, int topCorners, int rightCorners, int bottomCorners, int leftCorners) {
      super(top, right, bottom, left);
      this._topCorners = topCorners;
      this._rightCorners = rightCorners;
      this._bottomCorners = bottomCorners;
      this._leftCorners = leftCorners;
      if (topCorners >= top && rightCorners >= right && bottomCorners >= bottom && leftCorners >= left) {
         this._x = left;
         this._y = top;
         this._width = bitmap.getWidth() - left - right;
         this._height = bitmap.getHeight() - bottom - top;
         if (this._width >= 0 && this._height >= 0) {
            this._bitmap = bitmap;
            this._rop = this._bitmap.hasAlpha() ? -97 : -99;
            this.setTransparent(this._bitmap.hasAlpha());
            this.$initBitmaps();
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException("border: invalid corner");
      }
   }

   private void $initBitmaps() {
      int minTile = 16;
      int tileWidth = this._width != 0 ? this._width * ((minTile + this._width - 1) / this._width) : 0;
      int tileHeight = this._height != 0 ? this._height * ((minTile + this._height - 1) / this._height) : 0;
      this._bitmapTop = new Bitmap(tileWidth, this._y);
      this._bitmapBottom = new Bitmap(tileWidth, this._bitmap.getHeight() - this._height - this._y);
      this._bitmapLeft = new Bitmap(this._x, tileHeight);
      this._bitmapRight = new Bitmap(this._bitmap.getWidth() - this._width - this._x, tileHeight);
      this.copy(this._bitmap, this._x, 0, this._width, this._y, this._bitmapTop);
      this.copy(this._bitmap, this._x, this._y + this._height, this._width, this._bitmapBottom.getHeight(), this._bitmapBottom);
      this.copy(this._bitmap, 0, this._y, this._x, this._height, this._bitmapLeft);
      this.copy(this._bitmap, this._x + this._width, this._y, this._bitmapRight.getWidth(), this._height, this._bitmapRight);
      if (this._width == 1 && this._height == 1) {
         int[] pixelValue = new int[1];
         this._bitmap.getARGB(pixelValue, 0, 1, this._x, this._y, this._width, this._height);
         if (pixelValue[0] >>> 24 != 0) {
            this._background = Background.createSolidBackground(pixelValue[0]);
         }
      } else {
         int[] pixelValues = new int[this._width * this._height];
         this._bitmap.getARGB(pixelValues, 0, this._width, this._x, this._y, this._width, this._height);

         for (int lv = pixelValues.length - 1; lv >= 0; lv--) {
            if (pixelValues[lv] >>> 24 != 0) {
               Bitmap bitmapBackground = new Bitmap(tileWidth, tileHeight);
               this.copy(this._bitmap, this._x, this._y, this._width, this._height, bitmapBackground);
               this._background = Background.createBitmapBackground(bitmapBackground);
               return;
            }
         }
      }
   }

   private void copy(Bitmap src, int x, int y, int width, int height, Bitmap dest) {
      int[] argbData = new int[width * height];
      src.getARGB(argbData, 0, width, x, y, width, height);

      for (int tx = 0; tx < dest.getWidth(); tx += width) {
         for (int ty = 0; ty < dest.getHeight(); ty += height) {
            dest.setARGB(argbData, 0, width, tx, ty, width, height);
         }
      }
   }

   @Override
   public Background getBackground() {
      return this._background;
   }

   @Override
   public void paint(Graphics graphics, XYRect rect) {
      int borderTop = this.getTop();
      int borderRight = this.getRight();
      int borderBottom = this.getBottom();
      int borderLeft = this.getLeft();
      int yInsideBottom = rect.Y2() - borderBottom;
      int yInsideTop = rect.y + borderTop;
      int xInsideRight = rect.X2() - borderRight;
      int xInsideLeft = rect.x + borderLeft;
      int innerWidth = rect.width - borderLeft - borderRight;
      int innerHeight = rect.height - borderTop - borderBottom;
      if (borderTop != 0) {
         graphics.tileRop(this._rop, xInsideLeft, rect.y, innerWidth, borderTop, this._bitmapTop, 0, 0);
      }

      if (borderBottom != 0) {
         graphics.tileRop(this._rop, xInsideLeft, yInsideBottom, innerWidth, borderBottom, this._bitmapBottom, 0, 0);
      }

      if (borderLeft != 0) {
         graphics.tileRop(this._rop, rect.x, yInsideTop, borderLeft, innerHeight, this._bitmapLeft, 0, 0);
      }

      if (borderRight != 0) {
         graphics.tileRop(this._rop, xInsideRight, yInsideTop, borderRight, innerHeight, this._bitmapRight, 0, 0);
      }

      if (this._topCorners != 0) {
         if (this._leftCorners != 0) {
            graphics.drawBitmap(rect.x, rect.y, this._leftCorners, this._topCorners, this._bitmap, 0, 0);
         }

         if (this._rightCorners != 0) {
            graphics.drawBitmap(xInsideRight, rect.y, this._rightCorners, this._topCorners, this._bitmap, this._x + this._width, 0);
         }
      }

      if (this._bottomCorners != 0) {
         if (this._leftCorners != 0) {
            graphics.drawBitmap(rect.x, yInsideBottom, this._leftCorners, this._bottomCorners, this._bitmap, 0, this._y + this._height);
         }

         if (this._rightCorners != 0) {
            graphics.drawBitmap(
               xInsideRight, yInsideBottom, this._rightCorners, this._bottomCorners, this._bitmap, this._x + this._width, this._y + this._height
            );
         }
      }
   }
}
