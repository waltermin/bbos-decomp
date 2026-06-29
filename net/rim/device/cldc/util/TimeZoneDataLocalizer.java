package net.rim.device.cldc.util;

import net.rim.device.api.i18n.Locale;

public interface TimeZoneDataLocalizer {
   int TIME_ZONE_SHORT_NAME;
   int TIME_ZONE_LONG_NAME;
   int TIME_ZONE_DEFAULT_NAME;

   String getResourceString(int var1, int var2, Locale var3);
}
