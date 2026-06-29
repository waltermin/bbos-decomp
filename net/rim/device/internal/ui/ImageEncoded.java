package net.rim.device.internal.ui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;

public final class ImageEncoded implements Image {
   private EncodedImage _image;
   private boolean _stamp;

   public static final Image create(EncodedImage image) {
      return new ImageEncoded(image);
   }

   private ImageEncoded(EncodedImage image) {
      if (image == null) {
         throw new NullPointerException();
      }

      this._image = image;
      this._stamp = Graphics.isColor() && this._image.getFrameMonochrome(0) && this._image.getFrameTransparency(0);
   }

   @Override
   public final int getHeight(int width, int height) {
      if (width >= this._image.getWidth() && height >= this._image.getHeight()) {
         return this._image.getHeight();
      }

      int scale = (this._image.getWidth() + width - 1) / width;
      scale = Math.max(scale, (this._image.getHeight() + height - 1) / height);
      return this._image.getHeight() / scale;
   }

   @Override
   public final int getWidth(int width, int height) {
      if (width >= this._image.getWidth() && height >= this._image.getHeight()) {
         return this._image.getWidth();
      }

      int scale = (this._image.getWidth() + width - 1) / width;
      scale = Math.max(scale, (this._image.getHeight() + height - 1) / height);
      return this._image.getWidth() / scale;
   }

   @Override
   public final void paint(Graphics graphics, int x, int y, int width, int height) {
      int scale = 1;
      if (width < this._image.getWidth() || height < this._image.getHeight()) {
         scale = (this._image.getWidth() + width - 1) / width;
         scale = Math.max(scale, (this._image.getHeight() + height - 1) / height);
      }

      synchronized (this._image) {
         int oldScale = this._image.getScale();
         this._image.setScale(scale);
         x += width - this._image.getScaledWidth() >> 1;
         y += height - this._image.getScaledHeight() >> 1;
         if (this._stamp) {
            graphics.ropImage(-96, x, y, width, height, this._image, 0, 0, 0);
         } else {
            graphics.drawImage(x, y, width, height, this._image, 0, 0, 0);
         }

         this._image.setScale(oldScale);
      }
   }
}
