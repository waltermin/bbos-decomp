package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.content.IntArray;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.MapRect;

public final class Route extends Location {
   int _distance;
   int _time;
   XYRect _bbox = (XYRect)(new Object());
   String _startAddress;
   String _endAddress;
   int _numPoints;
   int _numPaths;
   XYPoint[] _points;
   Route$Path[] _paths;
   int _lastFocusNumber;
   int[][][] _xFinal;
   int[][][] _yFinal;
   int _oldYOffset;
   MapRect _oldMapView = new MapRect();
   int _oldRotation;
   Location _location;
   String _routeName;
   CurrentDecisions _decisions;

   public Route() {
   }

   public Route(MapField mapField, int distance, int time, String routeName) {
      this._distance = distance;
      this._time = time;
      this._routeName = routeName;
      super._label = routeName;
      this._numPoints = 0;
      this._numPaths = 0;
      this._points = new Object[0];
      this._paths = new Route$Path[0];
      this._decisions = new CurrentDecisions(mapField, this);
   }

   public final void addDecision(Decision decision) {
      this._decisions.add(decision);
   }

   public final void addPoint(int x, int y) {
      XYPoint point = (XYPoint)(new Object(x, y));
      Arrays.add(this._points, point);
      this._numPoints++;
   }

   private final void addPoints(IntArray d) {
      int n = d.getPointCount();
      int[] x = new int[n];
      int[] y = new int[n];
      d.getPoints(x, y);

      for (int i = 0; i < n; i++) {
         this.addPoint(x[i], y[i]);
      }
   }

   public final void addPath(IntArray d, int type) {
      if (type == 2) {
         this.addPoints(d);
      } else {
         int n = d.getPointCount();
         int[] x = new int[n];
         int[] y = new int[n];
         d.getPoints(x, y);
         Route$Path path = new Route$Path(x, y, n, type);
         path._data = d;
         Arrays.add(this._paths, path);
         this._numPaths++;
      }
   }

   public final void setBBox(XYRect bbox) {
      this._bbox.set(bbox);
      super._latitude = this._bbox.y + this._bbox.height / 2;
      super._longitude = this._bbox.x + this._bbox.width / 2;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void paint(Graphics graphics, Transform transform, boolean drawCaption) {
      int oldColour = graphics.getColor();
      int oldAlpha = graphics.getGlobalAlpha();
      MapPoint ptScreen = new MapPoint();
      MapPoint ptScreenLast = new MapPoint();
      int strokeWidth = 14;
      int padding = strokeWidth << transform._zoom;
      graphics.setColor(8388736);
      graphics.setGlobalAlpha(128);
      graphics.setStrokeWidth(strokeWidth);
      graphics.setStrokeStyle(3);
      graphics.setDrawingStyle(1, true);
      graphics.setDrawingStyle(2, true);
      if (this._xFinal != null
         && this._yFinal != null
         && this._xFinal[0] != null
         && this._yFinal[0] != null
         && transform._mapView._bottom == this._oldMapView._bottom
         && transform._mapView._top == this._oldMapView._top
         && transform._mapView._left == this._oldMapView._left
         && transform._mapView._right == this._oldMapView._right
         && transform.getYOffset() == this._oldYOffset
         && transform._rotation == this._oldRotation) {
         for (int i = 0; i < this._numPaths; i++) {
            boolean var48 = false /* VF: Semaphore variable */;

            try {
               var48 = true;
               if (this._xFinal[i] != null) {
                  if (this._yFinal[i] != null) {
                     graphics.drawPathOutline((int[])this._xFinal[i], (int[])this._yFinal[i], null, null, false);
                     var48 = false;
                  } else {
                     var48 = false;
                  }
               } else {
                  var48 = false;
               }
            } finally {
               if (var48) {
                  System.out.println("Exc Line");
                  continue;
               }
            }
         }
      } else {
         this._oldMapView._bottom = transform._mapView._bottom;
         this._oldMapView._top = transform._mapView._top;
         this._oldMapView._left = transform._mapView._left;
         this._oldMapView._right = transform._mapView._right;
         this._oldYOffset = transform.getYOffset();
         this._oldRotation = transform._rotation;
         this._xFinal = new int[this._numPaths][][];
         this._yFinal = new int[this._numPaths][][];

         for (int i = 0; i < this._numPaths; i++) {
            Route$Path path = this._paths[i];
            int numPoints = path._numPoints;
            int start = -1;
            int end = numPoints - 1;
            XYPoint position = (XYPoint)(new Object());
            XYPoint positionLast = (XYPoint)(new Object());
            MapRect rect = new MapRect(transform._cropView);
            rect._bottom -= padding;
            rect._top += padding;
            rect._left -= padding;
            rect._right += padding;
            position = this.determineRelativePosition(position, rect, path._x[0], path._y[0]);
            positionLast.x = position.x;
            positionLast.y = position.y;
            if (position.x == 0 && position.y == 0) {
               start = 0;
               end = 1;
            } else {
               for (int j = 1; j < numPoints; j++) {
                  position = this.determineRelativePosition(position, rect, path._x[j], path._y[j]);
                  if (this.testRelativePosition(position, positionLast, rect, path._x[j], path._y[j], path._x[j - 1], path._y[j - 1])) {
                     end = Math.max(j - 1, 0) + 1;
                     int width = transform._mapView.width();
                     int height = transform._mapView.height();

                     while (start > 0 && Math.abs(path._x[start] - transform._longitude) < width && Math.abs(path._y[start] - transform._latitude) < height) {
                        start--;
                     }

                     start = Math.max(start, 0);
                     break;
                  }

                  positionLast.x = position.x;
                  positionLast.y = position.y;
               }
            }

            if (start == -1) {
               break;
            }

            position = this.determineRelativePosition(position, rect, path._x[end], path._y[end]);

            while (end < numPoints - 1 && position.x == 0 && position.y == 0) {
               position = this.determineRelativePosition(position, rect, path._x[++end], path._y[end]);
            }

            int width = transform._mapView.width();
            int height = transform._mapView.height();

            while (end < numPoints - 1 && Math.abs(path._x[end] - transform._longitude) < width && Math.abs(path._y[end] - transform._latitude) < height) {
               end++;
            }

            numPoints = end - start + 1;
            int[] xScreen = new int[numPoints];
            int[] yScreen = new int[numPoints];
            ptScreen._x = path._x[start];
            ptScreen._y = path._y[start];
            transform.convertWorldToScreen(ptScreen);
            xScreen[0] = ptScreen._x;
            yScreen[0] = ptScreen._y;
            ptScreenLast._x = ptScreen._x;
            ptScreenLast._y = ptScreen._y;
            int k = 1;

            for (int j = start + 1; j < end; j++) {
               ptScreen._x = path._x[j];
               ptScreen._y = path._y[j];
               transform.convertWorldToScreen(ptScreen);
               int dx = Math.abs(ptScreen._x - ptScreenLast._x);
               int dy = Math.abs(ptScreen._y - ptScreenLast._y);
               if (dx >= 5 || dy >= 5) {
                  xScreen[k] = ptScreen._x;
                  yScreen[k] = ptScreen._y;
                  ptScreenLast._x = ptScreen._x;
                  ptScreenLast._y = ptScreen._y;
                  k++;
               }
            }

            ptScreen._x = path._x[end];
            ptScreen._y = path._y[end];
            transform.convertWorldToScreen(ptScreen);
            xScreen[k] = ptScreen._x;
            yScreen[k] = ptScreen._y;
            if (++k != numPoints) {
               int[] xFinal = new int[k];
               int[] yFinal = new int[k];
               this._xFinal[i] = (int[][])xFinal;
               this._yFinal[i] = (int[][])yFinal;
               System.arraycopy(xScreen, 0, xFinal, 0, k);
               System.arraycopy(yScreen, 0, yFinal, 0, k);
               boolean var43 = false /* VF: Semaphore variable */;

               try {
                  var43 = true;
                  if (xFinal != null) {
                     if (yFinal != null) {
                        graphics.drawPathOutline(xFinal, yFinal, null, null, false);
                        var43 = false;
                     } else {
                        var43 = false;
                     }
                  } else {
                     var43 = false;
                  }
               } finally {
                  if (var43) {
                     System.out.println("Exc Line");
                     continue;
                  }
               }
            } else {
               this._xFinal[i] = (int[][])xScreen;
               this._yFinal[i] = (int[][])yScreen;
               boolean var38 = false /* VF: Semaphore variable */;

               try {
                  var38 = true;
                  if (xScreen != null) {
                     if (yScreen != null) {
                        graphics.drawPathOutline(xScreen, yScreen, null, null, false);
                        var38 = false;
                     } else {
                        var38 = false;
                     }
                  } else {
                     var38 = false;
                  }
               } finally {
                  if (var38) {
                     System.out.println("Exc Line");
                     continue;
                  }
               }
            }
         }
      }

      int var54 = 0;
      graphics.setColor(8388736);
      graphics.setDrawingStyle(2, true);
      int d = 10;
      int r = 5;

      while (var54 < this._numPoints) {
         XYPoint point = this._points[var54];
         if (point.x >= transform._cropView._left
            && point.x <= transform._cropView._right
            && point.y >= transform._cropView._bottom
            && point.y <= transform._cropView._top) {
            ptScreen._x = point.x;
            ptScreen._y = point.y;
            transform.convertWorldToScreen(ptScreen);
            boolean var33 = false /* VF: Semaphore variable */;

            label616:
            try {
               var33 = true;
               graphics.fillArc(ptScreen._x - r, ptScreen._y - r, d, d, 0, 360);
               var33 = false;
            } finally {
               if (var33) {
                  System.out.println("Exc Line");
                  break label616;
               }
            }
         }

         var54++;
      }

      this._decisions.paint(graphics, transform, drawCaption);
      graphics.setColor(oldColour);
      graphics.setGlobalAlpha(oldAlpha);
      graphics.setStrokeWidth(1);
   }

   private final XYPoint determineRelativePosition(XYPoint point, MapRect rect, int x, int y) {
      if (x < rect._left) {
         point.x = -1;
      } else if (x > rect._right) {
         point.x = 1;
      } else {
         point.x = 0;
      }

      if (y < rect._bottom) {
         point.y = -1;
         return point;
      } else if (y > rect._top) {
         point.y = 1;
         return point;
      } else {
         point.y = 0;
         return point;
      }
   }

   private final boolean testRelativePosition(XYPoint position, XYPoint positionLast, MapRect rect, int x1, int y1, int x2, int y2) {
      if (position.x == 0 && position.y == 0) {
         return true;
      }

      if (position.x == 0 && position.y != positionLast.y
         || position.y == 0 && position.x != positionLast.x
         || position.x != positionLast.x && position.y != positionLast.y) {
         int side = rect.hitTest(x1, y1);
         MapPoint intersection = new MapPoint();
         if (rect.intersection(intersection, side, x1, y1, x2, y2)) {
            return true;
         }

         side = rect.hitTest(x2, y2);
         if (rect.intersection(intersection, side, x1, y1, x2, y2)) {
            return true;
         }
      }

      return false;
   }

   public final void createDecisionPointBreadcrumbs() {
      if (this._numPaths == 0) {
         for (int i = 0; i < this._decisions._count; i++) {
            this.addPoint(this._decisions._decisions[i]._longitude, this._decisions._decisions[i]._latitude);
         }
      }
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Route: distance: ")))
         .append(this._distance)
         .append(", time: ")
         .append(this._time)
         .append(", startAddress: ")
         .append(this._startAddress)
         .append(", endAddress: ")
         .append(this._endAddress)
         .toString();
   }
}
