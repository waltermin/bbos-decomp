package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.lbs.maplet.DEntry;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;

class ClipPath implements Comparator {
   DEntry _path;
   MapPoint _start = new MapPoint();
   int _startIndex;
   MapPoint _end = new MapPoint();
   int _endIndex;
   int _length;
   int _lengthStart;
   int _lengthEnd;
   int _side;

   boolean clip(int[] x, int[] y, int start, int end, MapRect rect, int scaleX, int scaleY) {
      int dxSum = 0;
      int dySum = 0;
      int dxStartSum = 0;
      int dyStartSum = 0;
      int dxEndSum = 0;
      int dyEndSum = 0;
      LongestPathLength[] pathLengthArray = new LongestPathLength[0];
      boolean hasInsidePoint = false;
      int i = start;
      int side = -1;

      while (i < end) {
         int nextSide = rect.hitTest(x[i], y[i]);
         if (nextSide == 0) {
            hasInsidePoint = true;
            break;
         }

         this.checkCutXPaths(side, nextSide, i, x, y, scaleX, scaleY, rect, pathLengthArray);
         side = nextSide;
         i++;
      }

      if (i == end && pathLengthArray.length == 0 && !hasInsidePoint) {
         return false;
      }

      if (!hasInsidePoint && pathLengthArray.length > 0) {
         return true;
      }

      this.checkPathEnds(start, end, i, side, x, y, scaleX, scaleY, rect, pathLengthArray);
      this.checkFromEndPoint(start, end, i, x, y, scaleX, scaleY, rect, pathLengthArray);
      if (pathLengthArray.length > 1) {
         new LongestPathLength();
         LongestPathLength var28 = pathLengthArray[0];

         for (int idx = 1; idx < pathLengthArray.length; idx++) {
            new LongestPathLength();
            LongestPathLength pathl = pathLengthArray[idx];
            if (var28._lengthInView < pathl._lengthInView) {
               var28 = pathl;
            }
         }

         this._length = var28._lengthInView;
         this._lengthStart = var28._lengthOutViewS;
         this._lengthEnd = var28._lengthOutViewE;
         this._startIndex = var28._startIdx;
         this._endIndex = var28._endIdx;
         this._start = var28._start;
         this._end = var28._end;
      }

      return this._length > 0;
   }

   void convertEndpointsToWorld(Maplet maplet) {
      LabelPathCallback._transform.convertMapletToWorld(this._start, maplet);
      LabelPathCallback._transform.convertMapletToWorld(this._end, maplet);
   }

   @Override
   public int compare(Object o1, Object o2) {
      return ((LabelPath)o1).compare((LabelPath)o2);
   }

   private void checkCutXPaths(
      int side, int nextSide, int startIdx, int[] x, int[] y, int scaleX, int scaleY, MapRect rect, LongestPathLength[] pathLengthArray
   ) {
      int dxSum = 0;
      int dySum = 0;
      int dxStartSum = 0;
      int dyStartSum = 0;
      int dxEndSum = 0;
      int dyEndSum = 0;
      int i = startIdx;
      if (side != -1 && side != nextSide && (side & nextSide) == 0) {
         this._start = new MapPoint();
         this._end = new MapPoint();
         if (rect.intersection(this._start, side, x[i - 1], y[i - 1], x[i], y[i]) && rect.intersection(this._end, nextSide, x[i - 1], y[i - 1], x[i], y[i])) {
            this._startIndex = i + 1;
            this._endIndex = i;
            int dx = Math.abs(this._start._x - this._end._x);
            int dy = Math.abs(this._start._y - this._end._y);
            if (dx > dy) {
               dxSum += dx;
            } else {
               dySum += dy;
            }

            int dxStart = Math.abs(this._start._x - x[i - 1]);
            int dyStart = Math.abs(this._start._y - y[i - 1]);
            if (dxStart > dyStart) {
               dxStartSum += dxStart;
            } else {
               dyStartSum += dyStart;
            }

            int dxEnd = Math.abs(this._end._x - x[i - 1]);
            int dyEnd = Math.abs(this._end._y - y[i - 1]);
            if (dxEnd > dyEnd) {
               dxEndSum += dxEnd;
            } else {
               dyEndSum += dyEnd;
            }

            if (dxSum > 0 || dySum > 0) {
               dxSum = dxSum * scaleX >> 16;
               dySum = dySum * scaleY >> 16;
               this._length = dxSum + dySum;
               if (dxStartSum > 0 || dyStartSum > 0) {
                  dxStartSum = dxStartSum * scaleX >> 16;
                  dyStartSum = dyStartSum * scaleY >> 16;
                  this._lengthStart = dxStartSum + dyStartSum;
               }

               if (dxEndSum > 0 || dyEndSum > 0) {
                  dxEndSum = dxEndSum * scaleX >> 16;
                  dyEndSum = dyEndSum * scaleY >> 16;
                  this._lengthEnd = dxEndSum + dyEndSum;
               }

               LongestPathLength pathLength = new LongestPathLength(
                  this._start, this._end, this._length, this._lengthStart, this._lengthEnd, this._startIndex, this._endIndex
               );
               Arrays.add(pathLengthArray, pathLength);
            }
         }
      }
   }

   private void checkPathEnds(
      int start, int end, int startIdx, int side, int[] x, int[] y, int scaleX, int scaleY, MapRect rect, LongestPathLength[] pathLengthArray
   ) {
      int dxSum = 0;
      int dySum = 0;
      int dxStartSum = 0;
      int dyStartSum = 0;
      int dxEndSum = 0;
      int dyEndSum = 0;
      int i = startIdx;
      this._start = new MapPoint();
      this._end = new MapPoint();
      this._endIndex = end;
      if (i == start) {
         this._start._x = x[i];
         this._start._y = y[i];
         this._startIndex = i + 1;
         this._length = 0;
      } else {
         rect.intersection(this._start, side, x[i - 1], y[i - 1], x[i], y[i]);
         this._startIndex = i;
         int dx = Math.abs(this._start._x - x[i]);
         int dy = Math.abs(this._start._y - y[i]);
         if (dx > dy) {
            dxSum += dx;
         } else {
            dySum += dy;
         }

         int dxStart = Math.abs(this._start._x - x[i - 1]);
         int dyStart = Math.abs(this._start._y - y[i - 1]);
         if (dxStart > dyStart) {
            dxStartSum += dxStart;
         } else {
            dyStartSum += dyStart;
         }
      }

      i++;

      for (; i < end; i++) {
         side = rect.hitTest(x[i], y[i]);
         if (side != 0) {
            break;
         }

         int dx = Math.abs(x[i] - x[i - 1]);
         int dy = Math.abs(y[i] - y[i - 1]);
         if (dx > dy) {
            dxSum += dx;
         } else {
            dySum += dy;
         }
      }

      if (i == end) {
         this._end._x = x[i - 1];
         this._end._y = y[i - 1];
         this._endIndex = i - 2;
      } else {
         rect.intersection(this._end, side, x[i], y[i], x[i - 1], y[i - 1]);
         this._endIndex = i - 1;
         int dx = Math.abs(this._end._x - x[i - 1]);
         int dy = Math.abs(this._end._y - y[i - 1]);
         if (dx > dy) {
            dxSum += dx;
         } else {
            dySum += dy;
         }

         int dxEnd = Math.abs(this._end._x - x[i]);
         int dyEnd = Math.abs(this._end._y - y[i]);
         if (dxEnd > dyEnd) {
            dxEndSum += dxEnd;
         } else {
            dyEndSum += dyEnd;
         }
      }

      dxSum = dxSum * scaleX >> 16;
      dySum = dySum * scaleY >> 16;
      this._length = dxSum + dySum;
      dxStartSum = dxStartSum * scaleX >> 16;
      dyStartSum = dyStartSum * scaleY >> 16;
      this._lengthStart = dxStartSum + dyStartSum;
      dxEndSum = dxEndSum * scaleX >> 16;
      dyEndSum = dyEndSum * scaleY >> 16;
      this._lengthEnd = dxEndSum + dyEndSum;
      LongestPathLength pathLength = new LongestPathLength(
         this._start, this._end, this._length, this._lengthStart, this._lengthEnd, this._startIndex, this._endIndex
      );
      Arrays.add(pathLengthArray, pathLength);
   }

   private void checkFromEndPoint(int start, int end, int i, int[] x, int[] y, int scaleX, int scaleY, MapRect rect, LongestPathLength[] pathLengthArray) {
      int dxSum = 0;
      int dySum = 0;
      int dxStartSum = 0;
      int dyStartSum = 0;
      int dxEndSum = 0;
      int dyEndSum = 0;
      int startSide = 0;
      i++;

      while (i < end) {
         startSide = rect.hitTest(x[i], y[i]);
         if (startSide != 0) {
            break;
         }

         i++;
      }

      if (startSide != 0) {
         int idx = end - 1;
         int endSide = rect.hitTest(x[idx], y[idx]);
         if (endSide != startSide && (endSide & startSide) == 0) {
            this._start = new MapPoint();
            this._end = new MapPoint();
            if (endSide != 0) {
               for (this._endIndex = idx; idx > i; idx--) {
                  int nextPointSide = rect.hitTest(x[idx - 1], y[idx - 1]);
                  if (endSide == nextPointSide) {
                     this._endIndex = idx - 1;
                  }

                  if (nextPointSide != 0
                     && endSide != nextPointSide
                     && (endSide & nextPointSide) == 0
                     && rect.intersection(this._end, endSide, x[this._endIndex - 1], y[this._endIndex - 1], x[this._endIndex], y[this._endIndex])
                     && rect.intersection(this._start, startSide, x[idx - 1], y[idx - 1], x[idx], y[idx])) {
                     this._startIndex = idx;
                     this._endIndex--;
                     int dx = Math.abs(this._start._x - this._end._x);
                     int dy = Math.abs(this._start._y - this._end._y);
                     if (dx > dy) {
                        dxSum += dx;
                     } else {
                        dySum += dy;
                     }

                     int dxStart = Math.abs(this._start._x - x[idx]);
                     int dyStart = Math.abs(this._start._y - y[idx]);
                     if (dxStart > dyStart) {
                        dxStartSum += dxStart;
                     } else {
                        dyStartSum += dyStart;
                     }

                     int dxEnd = Math.abs(this._end._x - x[idx - 1]);
                     int dyEnd = Math.abs(this._end._y - y[idx - 1]);
                     if (dxEnd > dyEnd) {
                        dxEndSum += dxEnd;
                     } else {
                        dyEndSum += dyEnd;
                     }
                     break;
                  }
               }
            } else {
               this._endIndex = idx;
               this._end._x = x[idx];
               this._end._y = y[idx];
               idx--;
               int hitSide = -1;

               while (idx > i) {
                  hitSide = rect.hitTest(x[idx], y[idx]);
                  if (hitSide != 0) {
                     break;
                  }

                  int dx = Math.abs(x[idx] - x[idx + 1]);
                  int dy = Math.abs(y[idx] - y[idx + 1]);
                  if (dx > dy) {
                     dxSum += dx;
                  } else {
                     dySum += dy;
                  }

                  idx--;
               }

               if (hitSide == 0) {
                  hitSide = rect.hitTest(x[idx], y[idx]);
               }

               rect.intersection(this._start, hitSide, x[idx], y[idx], x[idx + 1], y[idx + 1]);
               this._startIndex = idx + 1;
               int dx = Math.abs(this._start._x - x[idx + 1]);
               int dy = Math.abs(this._start._y - y[idx + 1]);
               if (dx > dy) {
                  dxSum += dx;
               } else {
                  dySum += dy;
               }

               int dxEnd = Math.abs(this._start._x - x[idx]);
               int dyEnd = Math.abs(this._start._y - y[idx]);
               if (dxEnd > dyEnd) {
                  dxEndSum += dxEnd;
               } else {
                  dyEndSum += dyEnd;
               }
            }

            dxSum = dxSum * scaleX >> 16;
            dySum = dySum * scaleY >> 16;
            this._length = dxSum + dySum;
            dxStartSum = dxStartSum * scaleX >> 16;
            dyStartSum = dyStartSum * scaleY >> 16;
            this._lengthStart = dxStartSum + dyStartSum;
            dxEndSum = dxEndSum * scaleX >> 16;
            dyEndSum = dyEndSum * scaleY >> 16;
            this._lengthEnd = dxEndSum + dyEndSum;
            LongestPathLength pathLength = new LongestPathLength(
               this._start, this._end, this._length, this._lengthStart, this._lengthEnd, this._startIndex, this._endIndex
            );
            Arrays.add(pathLengthArray, pathLength);
         }
      }
   }
}
