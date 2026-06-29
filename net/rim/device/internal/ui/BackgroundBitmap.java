package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

class BackgroundBitmap extends Background {
   private Bitmap _bitmap;
   private int _rop;
   private int _left;
   private int _top;

   BackgroundBitmap(Bitmap bitmap) {
      this.setBitmap(bitmap);
   }

   protected Bitmap getBitmap() {
      return this._bitmap;
   }

   @Override
   public void draw(Graphics graphics, XYRect rect) {
      if (this._bitmap != null && rect.width != 0 && rect.height != 0) {
         int xOrigin;
         switch (this.getPositionY()) {
            case 1:
            default:
               xOrigin = 0;
               break;
            case 2:
               xOrigin = rect.x - this._bitmap.getWidth();
               break;
            case 3:
               xOrigin = rect.x - this._bitmap.getWidth() >> 1;
         }

         int yOrigin;
         switch (this.getPositionY()) {
            case 1:
            default:
               yOrigin = 0;
               break;
            case 2:
               yOrigin = rect.y - this._bitmap.getHeight();
               break;
            case 3:
               yOrigin = rect.y - this._bitmap.getHeight() >> 1;
         }

         if (this.getRepeat() == 1) {
            graphics.drawBitmap(rect.x, rect.y, rect.width, rect.height, this._bitmap, this._left + xOrigin, this._top + yOrigin);
            return;
         }

         graphics.tileRop(this._rop, rect.x, rect.y, rect.width, rect.height, this._bitmap, this._left + xOrigin, this._top + yOrigin);
      }
   }

   protected void setBitmap(Bitmap bitmap) {
      this._bitmap = bitmap;
      if (this._bitmap != null && this._bitmap.hasAlpha()) {
         this._rop = -97;
      } else {
         this._rop = -99;
      }
   }

   public void setOrigin(int left, int top) {
      this._left = left;
      this._top = top;
   }

   @Override
   public boolean isTransparent() {
      return this._bitmap != null && this._bitmap.hasAlpha();
   }
}
