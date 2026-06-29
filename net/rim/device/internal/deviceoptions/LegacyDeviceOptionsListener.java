package net.rim.device.internal.deviceoptions;

import net.rim.device.api.util.DataBuffer;

public interface LegacyDeviceOptionsListener {
   int NOTIFICATIONS_OFFSET;
   int NOTIFICATIONS_SIZE;
   int ALARM_OFFSET;
   int ALARM_SIZE;
   int NOTIFICATION_REPEAT_OFFSET;
   int NOTIFICATION_REPEAT_SIZE;
   int ALARM_WEEKEND_OFFSET;
   int ALARM_WEEKEND_SIZE;

   void setLegacyDeviceOptions(DataBuffer var1);
}
