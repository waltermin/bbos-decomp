package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;

public class BackingStore {
   private int _width;
   private int _height;
   private boolean _transparency;
   private Bitmap _backBuffer;
   private Bitmap _frontBuffer;
   private XYRect _dirty = new XYRect(0, 0, 0, 0);
   private XYRect _totalDirty = new XYRect(0, 0, 0, 0);

   public BackingStore(int width, int height, boolean transparency) {
      this._width = width;
      this._height = height;
      this._transparency = transparency;
   }

   public void cleanBackingStore() {
      if (this._backBuffer != null) {
         Graphics cleanGraphics = new Graphics(this._backBuffer);
         cleanGraphics.setGlobalAlpha(0);
         cleanGraphics.setBackgroundColor(0);
         cleanGraphics.clear();
      }

      if (this._frontBuffer != null) {
         Graphics cleanGraphics = new Graphics(this._frontBuffer);
         cleanGraphics.setGlobalAlpha(0);
         cleanGraphics.setBackgroundColor(0);
         cleanGraphics.clear();
      }
   }

   private static Bitmap createBitmap(int width, int height, boolean transparency) {
      Bitmap bitmap = null;

      try {
         bitmap = new Bitmap(width, height);
         if (transparency) {
            bitmap.createAlpha(2);
         }

         Graphics initGraphics = new Graphics(bitmap);
         initGraphics.setGlobalAlpha(0);
         initGraphics.setBackgroundColor(0);
         initGraphics.clear();
         return bitmap;
      } catch (IllegalArgumentException e) {
         System.out.println("BackingStore ERROR: " + e);
         return null;
      }
   }

   public synchronized Graphics getGraphics(XYRect dirty) {
      if (this._backBuffer == null) {
         this._backBuffer = createBitmap(this._width, this._height, this._transparency);
         if (this._backBuffer == null) {
            return null;
         }
      }

      XYRect rect = Ui.getTmpXYRect();
      rect.set(0, 0, this._width, this._height);
      if (!rect.contains(dirty)) {
         System.out.println("BackingStore warning: dirty rect out of bounds");
      }

      rect.intersect(dirty);
      this._dirty.unionNoEmpty(rect);
      Ui.returnTmpXYRect(rect);
      Graphics graphics = new Graphics(this._backBuffer);
      graphics.pushContext(dirty, 0, 0);
      if (this._transparency) {
         graphics.setGlobalAlpha(0);
         graphics.setBackgroundColor(0);
      } else {
         graphics.setBackgroundColor(16777215);
      }

      graphics.clear();
      graphics.popContext();
      graphics.pushContext(dirty, 0, 0);
      return graphics;
   }

   public synchronized void getTotalDirty(XYRect totalDirty) {
      totalDirty.set(this._totalDirty);
      this._totalDirty.set(0, 0, 0, 0);
   }

   public synchronized void paint(Graphics graphics, int x, int y) {
      if (this._frontBuffer != null) {
         int width = this._frontBuffer.getWidth();
         int height = this._frontBuffer.getHeight();
         if (width != 0 && height != 0) {
            graphics.drawBitmap(x, y, width, height, this._frontBuffer, 0, 0);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized void updateBuffers() {
      if (this._backBuffer != null && !this._dirty.isEmpty()) {
         if (this._frontBuffer == null) {
            this._frontBuffer = createBitmap(this._width, this._height, this._transparency);
            if (this._frontBuffer == null) {
               return;
            }
         }

         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            synchronized (this._frontBuffer) {
               Graphics graphics = new Graphics(this._frontBuffer);
               graphics.setGlobalAlpha(0);
               graphics.clear(this._dirty.x, this._dirty.y, this._dirty.width, this._dirty.height);
               graphics.rop(-97, this._dirty.x, this._dirty.y, this._dirty.width, this._dirty.height, this._backBuffer, this._dirty.x, this._dirty.y);
            }

            this._totalDirty.unionNoEmpty(this._dirty);
            var7 = false;
         } finally {
            if (var7) {
               this._dirty.set(0, 0, 0, 0);
            }
         }

         this._dirty.set(0, 0, 0, 0);
      }
   }
}
