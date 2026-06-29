package net.rim.device.internal.diagnostics;

public interface StateTracker {
   int DEFAULT_INSTANCE = 0;
   int INVALID_TYPE = 0;
   int TEXT_TYPE = 1;
   int ENUM_TYPE = 2;
   int LONG_TYPE = 3;
   int TIMER_TYPE = 4;
   int LOCALIZED_TEXT_TYPE = 5;
   int INVALID_ENUM_STATE = 0;
   int NOT_APPLICABLE_ENUM_STATE = 1;
   int TRYING_ENUM_STATE = 2;
   int WORKING_ENUM_STATE = 3;
   int ERROR_ENUM_STATE = 4;

   void addListener(StateTrackerListener var1);

   void removeListener(StateTrackerListener var1);

   StateGroupManager getGroupManager();

   StateItemManager getItemManager();

   void updateTextItem(long var1, int var3, long var4, String var6);

   void updateLocalizedTextItem(long var1, int var3, long var4, String var6, int var7);

   void updateEnumItem(long var1, int var3, long var4, int var6);

   void updateLongItem(long var1, int var3, long var4, long var6);

   void updateTimerItem(long var1, int var3, long var4, boolean var6);
}
