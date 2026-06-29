package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;

final class FlipBitmapField extends CustomBitmapField {
   private int[] _tempx = new int[4];
   private int[] _tempy = new int[4];
   private final int _flip;

   FlipBitmapField(EncodedImage image, long style, int flip) {
      super(image.getBitmap(), style);
      this._flip = flip;
   }

   @Override
   protected final void paintBitmap(Graphics graphics, int x, int y, int width, int height, Bitmap bitmap, int left, int top) {
      this._tempx[0] = this._tempx[3] = x;
      this._tempx[1] = this._tempx[2] = x + this.getBitmapWidth();
      this._tempy[0] = this._tempy[1] = y;
      this._tempy[2] = this._tempy[3] = y + this.getBitmapHeight();
      boolean flipX = (this._flip & 2) != 0;
      boolean flipY = (this._flip & 1) != 0;
      int dux = flipX ? -65536 : 65536;
      int dvx = 0;
      int duy = 0;
      int dvy = flipY ? -65536 : 65536;
      graphics.drawTexturedPath(this._tempx, this._tempy, null, null, this._tempx[0], this._tempy[0], dux, dvx, duy, dvy, bitmap);
   }
}
