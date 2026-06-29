package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.Screen;
import net.rim.device.cldc.util.CalendarExtensions;

interface DebugListItem {
   CalendarExtensions GMT_CAL;
   SimpleDateFormat DATE_FORMAT;

   String getLabel();

   Screen getScreen();
}
