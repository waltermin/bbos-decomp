package net.rim.device.internal.diagnostics;

public interface StateTracker {
   int DEFAULT_INSTANCE;
   int INVALID_TYPE;
   int TEXT_TYPE;
   int ENUM_TYPE;
   int LONG_TYPE;
   int TIMER_TYPE;
   int LOCALIZED_TEXT_TYPE;
   int INVALID_ENUM_STATE;
   int NOT_APPLICABLE_ENUM_STATE;
   int TRYING_ENUM_STATE;
   int WORKING_ENUM_STATE;
   int ERROR_ENUM_STATE;

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
