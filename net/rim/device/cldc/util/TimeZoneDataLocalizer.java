package net.rim.device.cldc.util;

import net.rim.device.api.i18n.Locale;

public interface TimeZoneDataLocalizer {
   int TIME_ZONE_SHORT_NAME = 0;
   int TIME_ZONE_LONG_NAME = 1;
   int TIME_ZONE_DEFAULT_NAME = 2;

   String getResourceString(int var1, int var2, Locale var3);
}
