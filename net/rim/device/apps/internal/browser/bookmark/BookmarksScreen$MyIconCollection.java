package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.IconCollection;

class BookmarksScreen$MyIconCollection extends IconCollection {
   private int _width;
   private EncodedImage[] _images;

   public BookmarksScreen$MyIconCollection() {
      super(1, 1);
   }

   public void addImage(EncodedImage image) {
      if (this._images == null) {
         this._images = new Object[0];
      }

      Arrays.add(this._images, image);
   }

   @Override
   public int getHeight(int width, int height) {
      return 16;
   }

   void setWidth(int width) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public int getWidth(int width, int height) {
      return this._width > 0 ? this._width : 16;
   }

   @Override
   public int paint(Graphics graphics, int x, int y, int width, int height, int column, int row) {
      try {
         int scaleX = this._images[column].getScaleX32();
         int scaleY = this._images[column].getScaleY32();
         this._images[column].setScaleX32(Fixed32.div(Fixed32.toFP(16), Fixed32.toFP(width)));
         this._images[column].setScaleY32(Fixed32.div(Fixed32.toFP(16), Fixed32.toFP(height)));
         graphics.drawImage(x, y, width, height, this._images[column], 0, 0, 0);
         this._images[column].setScaleX32(scaleX);
         this._images[column].setScaleY32(scaleY);
      } finally {
         return width;
      }

      return width;
   }
}
