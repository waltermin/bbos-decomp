package net.rim.device.api.lbs.gps;

import javax.microedition.location.Location;

public interface LocationCallback {
   void newLocation(Location var1);

   void locationError();
}
