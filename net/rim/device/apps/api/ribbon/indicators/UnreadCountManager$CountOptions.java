package net.rim.device.apps.api.ribbon.indicators;

public interface UnreadCountManager$CountOptions {
   long MESSAGE_COUNT_OPTIONS = 7056244251443120672L;
   int DISPLAY_MESSAGE_COUNT_NONE = 0;
   int DISPLAY_MESSAGE_COUNT_UNREAD = 1;
   short DISPLAY_MESSAGE_COUNT_DEFUALT_VALUE_INDEX = 1;

   short getDisplayMessageCount();

   void setDisplayMessageCount(short var1);

   void setDisplayNewMessageIndicator(boolean var1);

   boolean getDisplayNewMessageIndicator();

   boolean getDisplayNewMessageIndicatorDefaultValue();
}
