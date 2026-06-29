package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.ResourceFetcher;

class NamedBackgroundBitmap extends BackgroundBitmap {
   private ResourceFetcher _resource;
   private String _name;

   NamedBackgroundBitmap(ResourceFetcher resource, String name) {
      super(null);
      this._resource = resource;
      this._name = name;
      byte[] imageData = this._resource.fetchResource(this._name);
      if (imageData == null) {
         throw new IllegalArgumentException("Can't find background " + this._name);
      }
   }

   @Override
   public void draw(Graphics graphics, XYRect rect) {
      this.initializeBitmap();
      super.draw(graphics, rect);
   }

   @Override
   public boolean freeStaleObject(int priority) {
      if (this.getBitmap() != null) {
         this.setBitmap(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isTransparent() {
      this.initializeBitmap();
      return super.isTransparent();
   }

   private void initializeBitmap() {
      if (this.getBitmap() == null) {
         byte[] pngImage = this._resource.fetchResource(this._name);
         Bitmap bitmap = Bitmap.createBitmapFromPNG(pngImage, 0, -1);
         this.setBitmap(bitmap);
      }
   }
}
