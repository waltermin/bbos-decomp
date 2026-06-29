package javax.microedition.location;

class LocationProvider$ProximityInfo {
   private Coordinates coordinates;
   private float radius;
   private ProximityListener listener;

   LocationProvider$ProximityInfo(Coordinates coords, float r, ProximityListener l) {
      this.coordinates = coords;
      this.radius = r;
      this.listener = l;
   }

   public void checkProximity(Location location) {
      if (this.coordinates.distance(location.getQualifiedCoordinates()) < this.radius) {
         this.listener.proximityEvent(this.coordinates, location);
      }
   }
}
