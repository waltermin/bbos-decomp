package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;

public final class ImageBitmap implements Image {
   private Bitmap _bitmap;

   public static final Image create(Bitmap bitmap) {
      if (bitmap == null) {
         throw new NullPointerException();
      } else {
         return new ImageBitmap(bitmap);
      }
   }

   private ImageBitmap(Bitmap bitmap) {
      this._bitmap = bitmap;
   }

   @Override
   public final int getHeight(int width, int height) {
      return Math.min(this._bitmap.getHeight(), height);
   }

   @Override
   public final int getWidth(int width, int height) {
      return Math.min(this._bitmap.getWidth(), width);
   }

   @Override
   public final void paint(Graphics graphics, int x, int y, int width, int height) {
      x += width - this._bitmap.getWidth() >> 1;
      y += height - this._bitmap.getHeight() >> 1;
      if (Graphics.isColor() && this._bitmap.getType() == 129 && this._bitmap.hasAlpha()) {
         graphics.rop(-96, x, y, width, height, this._bitmap, 0, 0);
      } else {
         graphics.drawBitmap(x, y, width, height, this._bitmap, 0, 0);
      }
   }
}
