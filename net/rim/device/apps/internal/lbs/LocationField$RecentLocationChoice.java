package net.rim.device.apps.internal.lbs;

final class LocationField$RecentLocationChoice extends LocationField$LocationChoice {
   protected Location _location;

   LocationField$RecentLocationChoice(Location location) {
      this._location = location;
   }

   @Override
   public final String toString() {
      String label = this._location._label;
      return label != null
         ? label
         : ((StringBuffer)(new Object("(")))
            .append(Integer.toString(this._location._latitude))
            .append(",")
            .append(Integer.toString(this._location._longitude))
            .append(")")
            .toString();
   }
}
