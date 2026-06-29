package net.rim.device.apps.internal.lbs.maplet;

public final class ScreenRect extends MapRect {
   public int _yOffset = 0;

   @Override
   final boolean VerticalIntersection(MapPoint intersection, int edge, int x1, int y1, int x2, int y2) {
      int tmpBottom = super._bottom - (this._yOffset << 1);
      int dx = x2 - x1;
      if (dx == 0) {
         return false;
      }

      int dy = y2 - y1;
      intersection._x = edge;
      if (dy == 0) {
         intersection._y = y1;
      } else {
         intersection._y = y1 + (edge - x1) * dy / dx;
      }

      return intersection._y >= super._top && intersection._y <= tmpBottom;
   }

   @Override
   final boolean HorizontalIntersection(MapPoint intersection, int edge, int x1, int y1, int x2, int y2) {
      int tmpBottom = super._bottom - (this._yOffset << 1);
      edge = edge == super._bottom ? tmpBottom : edge;
      int dy = y2 - y1;
      if (dy == 0) {
         return false;
      }

      int dx = x2 - x1;
      intersection._y = edge;
      if (dx == 0) {
         intersection._x = x1;
      } else {
         intersection._x = x1 + (edge - y1) * dx / dy;
      }

      return intersection._x >= super._left && intersection._x <= super._right;
   }

   @Override
   public final int hitTest(int x, int y) {
      if (x < super._left) {
         if (y > super._bottom - this._yOffset) {
            return 10;
         } else {
            return y < super._top ? 9 : 8;
         }
      } else if (x > super._right) {
         if (y > super._bottom - this._yOffset) {
            return 6;
         } else {
            return y < super._top ? 5 : 4;
         }
      } else if (y > super._bottom - this._yOffset) {
         return 2;
      } else {
         return y < super._top ? 1 : 0;
      }
   }

   public final void copy(ScreenRect src) {
      super._left = src._left;
      super._right = src._right;
      super._top = src._top;
      super._bottom = src._bottom;
      this._yOffset = src._yOffset;
   }
}
