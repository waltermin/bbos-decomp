package net.rim.wica.compatibility.versionranges;

public class WicletVersionRange extends IntVersionRange {
   protected WicletVersionRange() {
   }

   public WicletVersionRange(String minRange, String maxRange) {
      super._minRange = this.convertToInt(minRange);
      super._maxRange = this.convertToInt(maxRange);
   }

   public WicletVersionRange(String minRange) {
      super._minRange = this.convertToInt(minRange);
   }

   @Override
   public boolean isInRange(String version) {
      int versionInt;
      try {
         versionInt = this.convertToInt(version);
      } finally {
         ;
      }

      return super.isInRange(versionInt);
   }

   private int convertToInt(String minRange) {
      minRange = minRange.replace('.', '0');
      return Integer.parseInt(minRange);
   }
}
