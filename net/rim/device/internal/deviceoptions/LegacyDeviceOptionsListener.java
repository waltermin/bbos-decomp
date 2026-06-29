package net.rim.device.internal.deviceoptions;

import net.rim.device.api.util.DataBuffer;

public interface LegacyDeviceOptionsListener {
   int NOTIFICATIONS_OFFSET = 8;
   int NOTIFICATIONS_SIZE = 11;
   int ALARM_OFFSET = 104;
   int ALARM_SIZE = 13;
   int NOTIFICATION_REPEAT_OFFSET = 285;
   int NOTIFICATION_REPEAT_SIZE = 1;
   int ALARM_WEEKEND_OFFSET = 287;
   int ALARM_WEEKEND_SIZE = 1;

   void setLegacyDeviceOptions(DataBuffer var1);
}
