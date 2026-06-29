package net.rim.device.apps.internal.lbs.protocol;

final class MapRequest$MapletValue {
   protected int _x;
   protected int _y;
   protected int _level;
   private final MapRequest this$0;

   public MapRequest$MapletValue(MapRequest this$0, int x, int y, int level) {
      this.this$0 = this$0;
      this._x = x;
      this._y = y;
      this._level = level;
   }

   public final boolean equals(int x, int y, int level) {
      return x == this._x && y == this._y && level == this._level;
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(this._x);
      buf.append(", ");
      buf.append(this._y);
      buf.append(", ");
      buf.append(this._level);
      return buf.toString();
   }
}
