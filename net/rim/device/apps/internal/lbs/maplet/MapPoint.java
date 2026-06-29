package net.rim.device.apps.internal.lbs.maplet;

public final class MapPoint {
   public int _x;
   public int _y;

   public final void copy(MapPoint dst) {
      dst._x = this._x;
      dst._y = this._y;
   }
}
