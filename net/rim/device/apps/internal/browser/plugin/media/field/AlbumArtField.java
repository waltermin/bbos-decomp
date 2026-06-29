package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.browser.util.ImageConverter;

final class AlbumArtField extends Field {
   private EncodedImage _encodedImage;
   private boolean _scale;

   AlbumArtField(EncodedImage image, boolean scale) {
      super(36028797018963968L);
      this._encodedImage = image;
      this._scale = scale;
   }

   @Override
   protected final void layout(int width, int height) {
      if (this._scale) {
         int imageHeight = height - 3;
         this._encodedImage = ImageConverter.scaleImage(this._encodedImage, imageHeight, imageHeight, imageHeight, imageHeight);
      }

      this.setExtent(height, height);
   }

   @Override
   public final void paint(Graphics g) {
      int imageWidth = this._encodedImage.getScaledWidth();
      int imageHeight = this._encodedImage.getScaledHeight();
      int x = this.getWidth() - imageWidth >> 1;
      int y = this.getHeight() - imageHeight >> 1;

      try {
         g.drawImage(x, y, imageWidth, imageHeight, this._encodedImage, 0, 0, 0);
      } finally {
         return;
      }
   }
}
