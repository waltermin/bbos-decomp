package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.content.IntArray;

final class Route$Path {
   int[] _x;
   int[] _y;
   int _numPoints;
   int _type;
   IntArray _data;

   public Route$Path(int[] x, int[] y, int numPoints, int type) {
      this._x = x;
      this._y = y;
      this._numPoints = numPoints;
      this._type = type;
   }
}
