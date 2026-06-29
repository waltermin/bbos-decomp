package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.math.Fixed32;

public class MapRect {
   public int _bottom;
   public int _left;
   public int _top;
   public int _right;
   protected static final int LEFT_OF_WINDOW = 8;
   protected static final int RIGHT_OF_WINDOW = 4;
   protected static final int BELOW_WINDOW = 2;
   protected static final int ABOVE_WINDOW = 1;
   protected static final int LEFT_BELOW = 10;
   protected static final int LEFT_ABOVE = 9;
   protected static final int RIGHT_BELOW = 6;
   protected static final int RIGHT_ABOVE = 5;

   public MapRect() {
   }

   public MapRect(MapRect other) {
      this.copy(other);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof MapRect)) {
         return super.equals(obj);
      }

      MapRect rect = (MapRect)obj;
      return this._bottom == rect._bottom && this._left == rect._left && this._top == rect._top && this._right == rect._right;
   }

   public void copy(MapRect src) {
      this._left = src._left;
      this._right = src._right;
      this._top = src._top;
      this._bottom = src._bottom;
   }

   public int width() {
      return this._right - this._left;
   }

   public int height() {
      return this._top - this._bottom;
   }

   public boolean intersects(short[] header) {
      return header[1] <= this._top && header[3] >= this._bottom && header[0] <= this._right && header[2] >= this._left;
   }

   boolean VerticalIntersection(MapPoint intersection, int edge, int x1, int y1, int x2, int y2) {
      int dx = x2 - x1;
      if (dx == 0) {
         return false;
      }

      int dy = y2 - y1;
      intersection._x = edge;
      if (dy == 0) {
         intersection._y = y1;
      } else {
         intersection._y = y1 + Fixed32.mul(edge - x1, Fixed32.div(dy, dx));
      }

      return intersection._y >= this._bottom && intersection._y <= this._top;
   }

   boolean HorizontalIntersection(MapPoint intersection, int edge, int x1, int y1, int x2, int y2) {
      int dy = y2 - y1;
      if (dy == 0) {
         return false;
      }

      int dx = x2 - x1;
      intersection._y = edge;
      if (dx == 0) {
         intersection._x = x1;
      } else {
         intersection._x = x1 + Fixed32.mul(edge - y1, Fixed32.div(dx, dy));
      }

      return intersection._x >= this._left && intersection._x <= this._right;
   }

   public boolean intersection(MapPoint intersection, int side, int x1, int y1, int x2, int y2) {
      switch (side) {
         case 0:
         case 3:
         case 7:
            return false;
         case 1:
            return this.HorizontalIntersection(intersection, this._top, x1, y1, x2, y2);
         case 2:
            return this.HorizontalIntersection(intersection, this._bottom, x1, y1, x2, y2);
         case 4:
            return this.VerticalIntersection(intersection, this._right, x1, y1, x2, y2);
         case 5:
            if (this.VerticalIntersection(intersection, this._right, x1, y1, x2, y2)) {
               return true;
            }

            return this.HorizontalIntersection(intersection, this._top, x1, y1, x2, y2);
         case 6:
            if (this.VerticalIntersection(intersection, this._right, x1, y1, x2, y2)) {
               return true;
            }

            return this.HorizontalIntersection(intersection, this._bottom, x1, y1, x2, y2);
         case 8:
         default:
            return this.VerticalIntersection(intersection, this._left, x1, y1, x2, y2);
         case 9:
            if (this.VerticalIntersection(intersection, this._left, x1, y1, x2, y2)) {
               return true;
            }

            return this.HorizontalIntersection(intersection, this._top, x1, y1, x2, y2);
         case 10:
            return this.VerticalIntersection(intersection, this._left, x1, y1, x2, y2)
               ? true
               : this.HorizontalIntersection(intersection, this._bottom, x1, y1, x2, y2);
      }
   }

   public int hitTest(int x, int y) {
      if (x < this._left) {
         if (y < this._bottom) {
            return 10;
         } else {
            return y > this._top ? 9 : 8;
         }
      } else if (x > this._right) {
         if (y < this._bottom) {
            return 6;
         } else {
            return y > this._top ? 5 : 4;
         }
      } else if (y < this._bottom) {
         return 2;
      } else {
         return y > this._top ? 1 : 0;
      }
   }

   public static MapRect getMapletRect(MapRect rect, int level) {
      MapRect result = new MapRect();
      int mapletSize = Maplet.getMapletSize(level);
      result._left = rect._left;
      int mod = Math.abs(result._left % mapletSize);
      int dif = 0;
      if (mod != 0) {
         dif = mapletSize - mod;
         result._left = result._left - (result._left < 0 ? dif : mod);
      }

      result._bottom = rect._bottom;
      mod = Math.abs(result._bottom % mapletSize);
      if (mod != 0) {
         dif = mapletSize - mod;
         result._bottom = result._bottom - (result._bottom < 0 ? dif : mod);
      }

      result._right = rect._right;
      mod = Math.abs(result._right % mapletSize);
      if (mod != 0) {
         dif = mapletSize - mod;
         result._right = result._right + (result._right < 0 ? mod : dif);
      }

      result._top = rect._top;
      mod = Math.abs(result._top % mapletSize);
      if (mod != 0) {
         dif = mapletSize - mod;
         result._top = result._top + (result._top < 0 ? mod : dif);
      }

      if (level > 11) {
         result._bottom -= 4000000;
         result._top += 1000000;
         result._left -= 3000000;
         result._right += 2000000;
      }

      return result;
   }
}
