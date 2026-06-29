package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.internal.io.file.FileUtilities;

final class ImageFactory$ImageComponent implements SimpleRibbonComponent, RibbonComponentInitializer {
   RibbonComponent$RibbonComponentChangeListener _listener;
   protected int _height;
   protected int _width;
   protected Bitmap _bitmap;
   private boolean _isWallpaper;

   final void setBitmap(Bitmap bitmap) {
      this._bitmap = bitmap;
      if (this._listener != null) {
         this._listener.ribbonComponentChanged(this);
      }
   }

   final void load(String src) {
      if (src != null) {
         if (src.startsWith("wallpaper")) {
            ImageFactory imageFactory = ImageFactory.getInstance();
            this._bitmap = imageFactory._backgroundBitmap;
            this._isWallpaper = true;
            imageFactory.addBackgroundListener(this);
         }

         if (src.startsWith("store://")) {
            try {
               EncodedImage image = FileUtilities.getEncodedImage("file:///store" + src.substring(7));
               if (image != null) {
                  this._bitmap = image.getBitmap();
                  return;
               }
            } finally {
               return;
            }
         } else {
            if (src.equals("branding://bitmap1")) {
               byte[] data = Branding.getData(0);
               if (data != null) {
                  this._bitmap = Bitmap.createBitmapFromPNG(data, 0, data.length);
                  return;
               }

               this._bitmap = Bitmap.getBitmapResource("default-branding.png");
            }
         }
      }
   }

   @Override
   public final int getComponentHeight() {
      return this._height;
   }

   @Override
   public final int getComponentWidth() {
      return this._width;
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      String src = (String)params.get("src");
      this.load(src);
      if (this._bitmap == null) {
         src = (String)params.get("altsrc");
         this.load(src);
      }
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      if (this._bitmap == null) {
         return 0;
      }

      int bmWidth = this._bitmap.getWidth();
      int bmHeight = this._bitmap.getHeight();
      if (!this._isWallpaper || width < Display.getWidth()) {
         x += (this._width - bmWidth) / 2;
      }

      if (!this._isWallpaper || height < Display.getHeight()) {
         y += (this._height - bmHeight) / 2;
      }

      g.drawBitmap(x, y, bmWidth, bmHeight, this._bitmap, 0, 0);
      return 0;
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this._height = height;
      this._width = width;
   }

   @Override
   public final void uninitialize() {
   }

   @Override
   public final void applyTheme() {
   }
}
