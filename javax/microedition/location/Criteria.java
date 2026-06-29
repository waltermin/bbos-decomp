package javax.microedition.location;

public class Criteria {
   private int _horizontalAccuracy;
   private int _preferredPowerConsumption;
   private int _preferredResponseTime;
   private int _verticalAccuracy;
   private boolean _isAddressInfoRequired;
   private boolean _isAllowedToCost = true;
   private boolean _isAltitudeRequired;
   private boolean _isSpeedAndCourseRequired;
   public static final int NO_REQUIREMENT = 0;
   public static final int POWER_USAGE_LOW = 1;
   public static final int POWER_USAGE_MEDIUM = 2;
   public static final int POWER_USAGE_HIGH = 3;

   public int getPreferredPowerConsumption() {
      return this._preferredPowerConsumption;
   }

   public boolean isAllowedToCost() {
      return this._isAllowedToCost;
   }

   public int getVerticalAccuracy() {
      return this._verticalAccuracy;
   }

   public int getHorizontalAccuracy() {
      return this._horizontalAccuracy;
   }

   public int getPreferredResponseTime() {
      return this._preferredResponseTime;
   }

   public boolean isSpeedAndCourseRequired() {
      return this._isSpeedAndCourseRequired;
   }

   public boolean isAltitudeRequired() {
      return this._isAltitudeRequired;
   }

   public boolean isAddressInfoRequired() {
      return this._isAddressInfoRequired;
   }

   public void setHorizontalAccuracy(int accuracy) {
      this._horizontalAccuracy = accuracy;
   }

   public void setVerticalAccuracy(int accuracy) {
      this._verticalAccuracy = accuracy;
   }

   public void setPreferredResponseTime(int time) {
      this._preferredResponseTime = time;
   }

   public void setPreferredPowerConsumption(int level) {
      this._preferredPowerConsumption = level;
   }

   public void setCostAllowed(boolean costAllowed) {
      this._isAllowedToCost = costAllowed;
   }

   public void setSpeedAndCourseRequired(boolean speedAndCourseRequired) {
      this._isSpeedAndCourseRequired = speedAndCourseRequired;
   }

   public void setAltitudeRequired(boolean altitudeRequired) {
      this._isAltitudeRequired = altitudeRequired;
   }

   public void setAddressInfoRequired(boolean addressInfoRequired) {
      this._isAddressInfoRequired = addressInfoRequired;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Criteria)) {
         return false;
      }

      Criteria criteria = (Criteria)obj;
      return criteria._horizontalAccuracy == this._horizontalAccuracy
         && criteria._isAddressInfoRequired == this._isAddressInfoRequired
         && criteria._isAllowedToCost == this._isAllowedToCost
         && criteria._isAltitudeRequired == this._isAltitudeRequired
         && criteria._isSpeedAndCourseRequired == this._isSpeedAndCourseRequired
         && criteria._preferredPowerConsumption == this._preferredPowerConsumption
         && criteria._preferredResponseTime == this._preferredResponseTime
         && criteria._verticalAccuracy == this._verticalAccuracy;
   }
}
