package net.rim.device.apps.internal.explorer.file.util;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;

public final class BitmapCache {
   private Bitmap _cacheBitmap;
   private Graphics _cacheGraphics;
   private EncodedImage[] _cacheImages;
   private int[] _lru;
   private int _thumbnailWidth;
   private int _thumbnailHeight;

   public BitmapCache(int size) {
      this._cacheImages = new Object[size];
      this._lru = new int[size];
      int i = 0;

      while (i < size) {
         this._lru[i] = i++;
      }
   }

   public final void setCacheParameters(int thumbnailWidth, int thumbnailHeight) {
      if (this._thumbnailWidth != thumbnailWidth && this._thumbnailWidth != thumbnailHeight) {
         this.clear();
         this._thumbnailWidth = thumbnailWidth;
         this._thumbnailHeight = thumbnailHeight;
         this._cacheBitmap = (Bitmap)(new Object(thumbnailWidth, this._lru.length * thumbnailHeight));
         this._cacheGraphics = (Graphics)(new Object(this._cacheBitmap));
      }
   }

   public final void paint(Graphics g, EncodedImage thumb, int x, int y, int width, int height) {
      if (this._cacheBitmap != null) {
         int index = -1;

         for (int i = 0; i < this._cacheImages.length; i++) {
            if (this._cacheImages[i] == thumb) {
               index = i;
               break;
            }
         }

         if (index == -1) {
            index = this._lru[0];
            this._cacheImages[index] = thumb;
            int targetY = index * this._thumbnailHeight;
            this._cacheGraphics.drawImage(0, targetY, width, height, thumb, 0, 0, 0);
         }

         System.arraycopy(this._lru, 1, this._lru, 0, this._lru.length - 1);
         this._lru[this._lru.length - 1] = index;
         g.drawBitmap(x, y, width, height, this._cacheBitmap, 0, index * this._thumbnailHeight);
      }
   }

   public final void clear() {
      for (int i = 0; i < this._cacheImages.length; i++) {
         this._cacheImages[i] = null;
      }

      Arrays.sort(this._lru, 0, this._lru.length);
   }
}
