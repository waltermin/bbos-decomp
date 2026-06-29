package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Transform;
import net.rim.device.apps.internal.lbs.render.RenderThread;

final class OffscreenBuffer {
   Graphics _graphics;
   Bitmap _bitmap;
   MapRect _rect;
   int _zoom = -1;
   int _width;
   int _height;
   Transform _transform;
   MapPoint _point1 = new MapPoint();
   MapPoint _point2 = new MapPoint();
   int[] _x;
   int[] _y;

   OffscreenBuffer(int width, int height, Transform transform) {
      this._width = width;
      this._height = height;
      this._transform = transform;
      this._bitmap = (Bitmap)(new Object(width, height));
      this._graphics = (Graphics)(new Object(this._bitmap));
      this._x = new int[]{0, width, width, 0};
      this._y = new int[]{0, 0, height, height};
   }

   final void render(RenderThread thread, MapRect rect, MapRect lblRect, int zoom, int rotation, int paddingTown, int paddingShield) {
      this._rect = new MapRect(rect);
      this._zoom = zoom;
      thread.render(this._graphics, this._rect, lblRect, this._zoom, rotation, paddingTown, paddingShield);
   }

   final void blt(Graphics graphics, MapRect rect, int zoom, int fieldHeight) {
      if (this._rect != null) {
         int width = rect.width() >> zoom;
         if (LBSOptions.SPHERICAL_CORRECTION) {
            int sphericalCorrection = Transform.getSphericalCorrection((rect._bottom + rect._top) / 2, zoom);
            width = Fixed32.mul(width, Fixed32.div(sphericalCorrection, 65536));
         }

         int height = rect.height() >> zoom;
         if (this._zoom != -1 && this._zoom != zoom) {
            int scale = 1 << 16 + zoom - this._zoom;
            int mywidth = width << this._zoom >> zoom;
            int myheight = height << this._zoom >> zoom;
            if (this._zoom > zoom) {
               int x = (mywidth - width) / 2;
               int y = (myheight - height) / 2 + this._transform.getYOffset();
               this._x = new int[]{0, width, width, 0};
               this._y = new int[]{0, 0, height, height};
               graphics.drawTexturedPath(this._x, this._y, null, null, -x, -y, scale, 0, 0, scale, this._bitmap);
            } else {
               int x = (width - mywidth) / 2;
               int y = (height - myheight) / 2 - this._transform.getYOffset();
               int x2 = x + mywidth;
               int y2 = y + myheight;
               this._x = new int[]{x, x2, x2, x};
               this._y = new int[]{y, y, y2, y2};
               graphics.drawTexturedPath(this._x, this._y, null, null, x, y, scale, 0, 0, scale, this._bitmap);
            }
         } else {
            this._point1._x = (this._rect._left + this._rect._right) / 2;
            this._point1._y = (this._rect._top + this._rect._bottom) / 2;
            this._transform.convertWorldToScreen(this._point1);
            graphics.drawBitmap(this._point1._x - width / 2, this._point1._y - height / 2, width, height, this._bitmap, 0, 0);
         }
      }
   }
}
