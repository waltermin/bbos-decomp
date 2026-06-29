package net.rim.device.apps.internal.lbs.render;

import net.rim.device.apps.internal.lbs.maplet.MapPoint;

final class LongestPathLength {
   int _lengthInView;
   int _lengthOutViewS;
   int _lengthOutViewE;
   int _startIdx;
   int _endIdx;
   MapPoint _end;
   MapPoint _start;

   public LongestPathLength() {
   }

   public LongestPathLength(MapPoint start, MapPoint end, int length, int lengthS, int lengthE, int idxS, int idxE) {
      this._start = start;
      this._end = end;
      this._lengthInView = length;
      this._lengthOutViewS = lengthS;
      this._lengthOutViewE = lengthE;
      this._startIdx = idxS;
      this._endIdx = idxE;
   }
}
