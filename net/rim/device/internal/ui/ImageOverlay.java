package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;

public final class ImageOverlay implements Image {
   private Image _image;
   private Image _overlay;

   public static final Image create(Image image, Image overlay) {
      if (image != null && overlay != null) {
         return new ImageOverlay(image, overlay);
      } else {
         throw new NullPointerException();
      }
   }

   private ImageOverlay(Image image, Image overlay) {
      this._image = image;
      this._overlay = overlay;
   }

   @Override
   public final int getHeight(int width, int height) {
      return this._image.getHeight(width, height);
   }

   @Override
   public final int getWidth(int width, int height) {
      return this._image.getWidth(width, height);
   }

   @Override
   public final void paint(Graphics graphics, int x, int y, int width, int height) {
      this._image.paint(graphics, x, y, width, height);
      this._overlay.paint(graphics, x, y, width, height);
   }
}
