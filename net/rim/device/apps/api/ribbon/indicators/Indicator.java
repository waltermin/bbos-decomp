package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.ui.Graphics;

public interface Indicator {
   int LEFT = 1;
   int RIGHT = 2;
   int ICON_ONLY = 4;
   int DONT_DRAW = 0;
   int CONTENT_PROTECTION_INDICATOR_PRIORITY = 1;
   int MESSAGING_INDICATOR_PRIORITY = 2;
   int PHONE_INDICATOR_PRIORITY = 4;
   int DIRECT_CONNECT_INDICATOR_PRIORITY = 5;
   int IM_INDICATOR_PRIORITY = 6;
   int CALENDAR_INDICATOR_PRIORITY = 7;
   int BROWSER_INDICATOR_PRIORITY = 8;
   int ALARM_INDICATOR_PRIORITY = 9;
   int TASK_INDICATOR_PRIORITY = 10;

   int draw(Graphics var1, int var2, int var3, int var4);

   int getWidth(Graphics var1);

   int getHeight(Graphics var1);

   int getPriority();

   String getTypeName();
}
