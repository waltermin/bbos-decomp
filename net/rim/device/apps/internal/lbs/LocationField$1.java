package net.rim.device.apps.internal.lbs;

import net.rim.device.api.gps.GPSLocation;
import net.rim.device.api.gps.GPSRegistry;

final class LocationField$1 extends LocationField$LocationActionChoice {
   private final LocationField this$0;

   LocationField$1(LocationField this$0, int x0) {
      super(x0);
      this.this$0 = this$0;
   }

   @Override
   public final void onSelect() {
      GPSLocation location = GPSRegistry.getInstance().getLocation(0);
   }
}
