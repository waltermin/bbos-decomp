package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.apps.internal.lbs.Location;

public interface SearchableHistoryList$Callback {
   int SELECT;
   int EDIT;

   void onLocation(Location var1, int var2);
}
