package net.rim.wica.compatibility.versionranges;

public class IntVersionRange implements VersionRange {
   protected int _minRange;
   protected int _maxRange = -1;
   private static final int VERSION_RANGE_UNBOUNDED;

   protected boolean isInRange(int version) {
      return this._maxRange == -1 ? version >= this._minRange : version >= this._minRange && version <= this._maxRange;
   }

   @Override
   public boolean isInRange(String version) {
      int versionInt;
      try {
         versionInt = Integer.parseInt(version);
      } finally {
         ;
      }

      return this.isInRange(versionInt);
   }

   public IntVersionRange(int minRange) {
      this._minRange = minRange;
   }

   public IntVersionRange(int minRange, int maxRange) {
      this._minRange = minRange;
      this._maxRange = maxRange;
   }

   protected IntVersionRange() {
   }
}
