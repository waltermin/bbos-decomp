package net.rim.device.apps.internal.lbs;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;
import net.rim.device.apps.internal.lbs.maplet.ScreenRect;

public final class Transform {
   public MapRect _mapView = new MapRect();
   public MapRect _cropView = new MapRect();
   public ScreenRect _screenView = new ScreenRect();
   MapPoint _screenAnchor = new MapPoint();
   int _zoom;
   int _rotation;
   Maplet _maplet;
   int _mapletScale;
   int _mapletBlx;
   int _mapletBly;
   int _worldScaleX;
   int _worldBlx;
   int _worldBly;
   int[] _worldTransform = new int[9];
   int[] _mapletTransform = new int[9];
   private int[] _matrix = new int[9];
   private int[] _translate = new int[9];
   private int[] _rotate = new int[9];
   int _sin;
   int _cos;
   int _width;
   int _height;
   int _latitude;
   int _longitude;
   private int _yOffset = 0;

   public Transform() {
      Arrays.fill(this._worldTransform, 0);
      Arrays.fill(this._mapletTransform, 0);
   }

   public final void setScreenExtent(int width, int height) {
      this._screenView._right = width;
      this._screenView._bottom = height;
      this._screenAnchor._x = width / 2;
      this._screenAnchor._y = height / 2;
   }

   public final void setYOffset(int offset) {
      if (this._yOffset != offset) {
         this._yOffset = offset;
         this.updateCropView();
         this.calcMapletTransform();
         this.calcWorldTransform();
      }
   }

   public final int getYOffset() {
      return this._yOffset;
   }

   public final int[] getWorldTransform() {
      return this._worldTransform;
   }

   public final int getWorldBLX() {
      return this._worldBlx;
   }

   public final int getWorldBLY() {
      return this._worldBly;
   }

   private final void updateCropView() {
      this._cropView._left = this._mapView._left;
      this._cropView._right = this._mapView._right;
      int wHeight = this._yOffset << this._zoom;
      this._cropView._bottom = this._mapView._bottom + wHeight;
      this._cropView._top = this._mapView._top - wHeight;
   }

   public final void update(MapRect rect, int zoom, int rotation) {
      if (rect._bottom != this._mapView._bottom
         || rect._left != this._mapView._left
         || rect._right != this._mapView._right
         || rect._top != this._mapView._top
         || zoom != this._zoom
         || rotation != this._rotation) {
         this._mapView._bottom = rect._bottom;
         this._mapView._left = rect._left;
         this._mapView._right = rect._right;
         this._mapView._top = rect._top;
         this._zoom = zoom;
         this._rotation = rotation;
         this._cos = Utilities.cos(rotation);
         this._sin = Utilities.sin(rotation);
         int geoWidth = this._mapView._right - this._mapView._left;
         int geoHeight = this._mapView._top - this._mapView._bottom;
         this._longitude = rect._left + geoWidth / 2;
         this._latitude = rect._bottom + geoHeight / 2;
         this.updateCropView();
         this.calcMapletTransform();
         this.calcWorldTransform();
      }
   }

   public final void convertWorldToScreen(MapPoint point) {
      point._x = point._x - this._worldBlx >> this._zoom;
      point._y = point._y - this._worldBly >> this._zoom;
      int x = point._x * this._worldTransform[0] + point._y * this._worldTransform[1] + this._worldTransform[2] >> 16;
      int y = point._x * this._worldTransform[3] + point._y * this._worldTransform[4] + this._worldTransform[5] >> 16;
      point._x = x;
      point._y = y;
   }

   public final void convertMapletToScreen(XYPoint point) {
      int x = point.x * this._mapletTransform[0] + point.y * this._mapletTransform[1] + this._mapletTransform[2] >> 16;
      int y = point.x * this._mapletTransform[3] + point.y * this._mapletTransform[4] + this._mapletTransform[5] >> 16;
      point.x = x;
      point.y = y + this._yOffset;
   }

   public final void convertMapletToWorld(MapPoint point, Maplet maplet) {
      if (maplet != this._maplet) {
         this.setMaplet(maplet);
      }

      point._x = (point._x << this._mapletScale) + this._mapletBlx;
      point._y = (point._y << this._mapletScale) + this._mapletBly;
   }

   public final void convertWorldToMaplet(XYPoint point, Maplet maplet) {
      if (maplet != this._maplet) {
         this.setMaplet(maplet);
      }

      point.x = point.x - this._mapletBlx >> this._mapletScale;
      point.y = point.y - this._mapletBly >> this._mapletScale;
   }

   public final Maplet getMaplet() {
      return this._maplet;
   }

   public final void setMaplet(Maplet maplet) {
      if (maplet != this._maplet) {
         this._maplet = maplet;
         int level = Maplet.getMapletLevel(this._zoom);
         this._mapletScale = Maplet.getMapletZoomScale(level);
         this._mapletBlx = this._maplet.getMapletBLX();
         this._mapletBly = this._maplet.getMapletBLY();
         this.calcMapletTransform();
      }
   }

   public final void calcMapletTransform() {
      if (this._maplet != null) {
         int yscale = 65536 << this._mapletScale >> this._zoom;
         int xscale = yscale;
         int tx1 = this._mapletBlx - this._cropView._left >> this._zoom;
         int ty1 = this._cropView._top - this._mapletBly >> this._zoom;
         int sphericalCorrection = 65536;
         if (LBSOptions.SPHERICAL_CORRECTION) {
            sphericalCorrection = getSphericalCorrection((this._cropView._bottom + this._cropView._top) / 2, this._zoom);
            xscale = sphericalCorrection << this._mapletScale >> this._zoom;
            tx1 *= sphericalCorrection;
         } else {
            tx1 = Fixed32.toFP(tx1);
         }

         int[] matrix = this._matrix;
         matrix[0] = xscale;
         matrix[1] = 0;
         matrix[2] = tx1;
         matrix[3] = 0;
         matrix[4] = -1 * yscale;
         matrix[5] = ty1 * 65536;
         matrix[6] = 0;
         matrix[7] = 0;
         matrix[8] = 65536;
         if (this._rotation == 0) {
            System.arraycopy(matrix, 0, this._mapletTransform, 0, this._mapletTransform.length);
         } else {
            int geoWidth;
            if (LBSOptions.SPHERICAL_CORRECTION) {
               geoWidth = Fixed32.mul(this._cropView._right - this._cropView._left, Fixed32.div(sphericalCorrection, 65536));
            } else {
               geoWidth = this._cropView._right - this._cropView._left;
            }

            int tx = -65536 * (geoWidth >> this._zoom + 1);
            int ty = 65536 * (this._cropView._bottom - this._cropView._top >> this._zoom + 1);
            int[] translate = this._translate;
            translate[0] = 65536;
            translate[2] = tx;
            translate[4] = 65536;
            translate[5] = ty;
            translate[8] = 65536;
            VecMath.multiply3x3Affine(translate, 0, matrix, 0, matrix, 0);
            int sin = Utilities.sin(this._rotation);
            int cos = Utilities.cos(this._rotation);
            int[] rotate = this._rotate;
            rotate[0] = cos;
            rotate[1] = -sin;
            rotate[3] = sin;
            rotate[4] = cos;
            rotate[8] = 65536;
            VecMath.multiply3x3Affine(rotate, 0, matrix, 0, matrix, 0);
            translate[2] = -tx;
            translate[5] = -ty;
            VecMath.multiply3x3Affine(translate, 0, matrix, 0, matrix, 0);
            System.arraycopy(matrix, 0, this._mapletTransform, 0, this._mapletTransform.length);
         }
      }
   }

   public final void calcWorldTransform() {
      this._worldBlx = (this._cropView._left + this._cropView._right) / 2;
      this._worldBly = (this._cropView._bottom + this._cropView._top) / 2;
      int yscale = 65536;
      int xscale = yscale;
      int tx1 = this._worldBlx - this._cropView._left >> this._zoom;
      int ty1 = this._cropView._top - this._worldBly >> this._zoom;
      int sphericalCorrection = 65536;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         sphericalCorrection = getSphericalCorrection((this._cropView._bottom + this._cropView._top) / 2, this._zoom);
         xscale = sphericalCorrection >> this._zoom;
         xscale = sphericalCorrection;
         tx1 *= sphericalCorrection;
      } else {
         tx1 = Fixed32.toFP(tx1);
      }

      int[] matrix = this._matrix;
      matrix[0] = xscale;
      matrix[1] = 0;
      matrix[2] = tx1;
      matrix[3] = 0;
      matrix[4] = -1 * yscale;
      matrix[5] = 65536 * ty1;
      matrix[6] = 0;
      matrix[7] = 0;
      matrix[8] = 65536;
      if (this._rotation == 0) {
         System.arraycopy(matrix, 0, this._worldTransform, 0, this._worldTransform.length);
      } else {
         int geoWidth;
         if (LBSOptions.SPHERICAL_CORRECTION) {
            geoWidth = Fixed32.mul(this._cropView._right - this._cropView._left, Fixed32.div(sphericalCorrection, 65536));
         } else {
            geoWidth = this._cropView._right - this._cropView._left;
         }

         int tx = -65536 * (geoWidth >> this._zoom + 1);
         int ty = 65536 * (this._cropView._bottom - this._cropView._top >> this._zoom + 1);
         int[] translate = this._translate;
         translate[0] = 65536;
         translate[2] = tx;
         translate[4] = 65536;
         translate[5] = ty;
         translate[8] = 65536;
         VecMath.multiply3x3Affine(translate, 0, matrix, 0, matrix, 0);
         int sin = Utilities.sin(this._rotation);
         int cos = Utilities.cos(this._rotation);
         int[] rotate = this._rotate;
         rotate[0] = cos;
         rotate[1] = -sin;
         rotate[3] = sin;
         rotate[4] = cos;
         rotate[8] = 65536;
         VecMath.multiply3x3Affine(rotate, 0, matrix, 0, matrix, 0);
         translate[2] = -tx;
         translate[5] = -ty;
         VecMath.multiply3x3Affine(translate, 0, matrix, 0, matrix, 0);
         System.arraycopy(matrix, 0, this._worldTransform, 0, this._worldTransform.length);
      }
   }

   public static final int getSphericalCorrection(int latitude, int zoom) {
      int degrees;
      if (zoom > 9) {
         degrees = 41;
      } else {
         degrees = latitude / 100000;
         if (degrees < 0) {
            degrees = -degrees;
         }
      }

      return Utilities.cos(degrees);
   }

   public final void clone(Transform dst) {
      if (dst != null) {
         dst._mapView.copy(this._mapView);
         dst._cropView.copy(this._cropView);
         dst._screenView.copy(this._screenView);
         this._screenAnchor.copy(dst._screenAnchor);
         dst._zoom = this._zoom;
         dst._rotation = this._rotation;
         dst._maplet = this._maplet;
         dst._mapletScale = this._mapletScale;
         dst._mapletBlx = this._mapletBlx;
         dst._mapletBly = this._mapletBly;
         dst._worldScaleX = this._worldScaleX;
         dst._worldBlx = this._worldBlx;
         dst._worldBly = this._worldBly;
         dst._worldTransform = Arrays.copy(this._worldTransform);
         dst._mapletTransform = Arrays.copy(this._mapletTransform);
         dst._matrix = Arrays.copy(this._matrix);
         dst._translate = Arrays.copy(this._translate);
         dst._rotate = Arrays.copy(this._rotate);
         dst._sin = this._sin;
         dst._cos = this._cos;
         dst._width = this._width;
         dst._height = this._height;
         dst._latitude = this._latitude;
         dst._longitude = this._longitude;
         dst._yOffset = this._yOffset;
      }
   }
}
