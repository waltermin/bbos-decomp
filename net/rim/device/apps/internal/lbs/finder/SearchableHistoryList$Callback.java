package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.apps.internal.lbs.Location;

public interface SearchableHistoryList$Callback {
   int SELECT = 0;
   int EDIT = 1;

   void onLocation(Location var1, int var2);
}
