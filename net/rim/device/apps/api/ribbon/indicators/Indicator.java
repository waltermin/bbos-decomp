package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.ui.Graphics;

public interface Indicator {
   int LEFT;
   int RIGHT;
   int ICON_ONLY;
   int DONT_DRAW;
   int CONTENT_PROTECTION_INDICATOR_PRIORITY;
   int MESSAGING_INDICATOR_PRIORITY;
   int PHONE_INDICATOR_PRIORITY;
   int DIRECT_CONNECT_INDICATOR_PRIORITY;
   int IM_INDICATOR_PRIORITY;
   int CALENDAR_INDICATOR_PRIORITY;
   int BROWSER_INDICATOR_PRIORITY;
   int ALARM_INDICATOR_PRIORITY;
   int TASK_INDICATOR_PRIORITY;

   int draw(Graphics var1, int var2, int var3, int var4);

   int getWidth(Graphics var1);

   int getHeight(Graphics var1);

   int getPriority();

   String getTypeName();
}
