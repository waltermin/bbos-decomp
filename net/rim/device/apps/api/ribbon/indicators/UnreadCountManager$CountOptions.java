package net.rim.device.apps.api.ribbon.indicators;

public interface UnreadCountManager$CountOptions {
   long MESSAGE_COUNT_OPTIONS;
   int DISPLAY_MESSAGE_COUNT_NONE;
   int DISPLAY_MESSAGE_COUNT_UNREAD;
   short DISPLAY_MESSAGE_COUNT_DEFUALT_VALUE_INDEX;

   short getDisplayMessageCount();

   void setDisplayMessageCount(short var1);

   void setDisplayNewMessageIndicator(boolean var1);

   boolean getDisplayNewMessageIndicator();

   boolean getDisplayNewMessageIndicatorDefaultValue();
}
