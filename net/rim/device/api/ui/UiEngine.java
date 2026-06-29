package net.rim.device.api.ui;

public interface UiEngine {
   int GLOBAL_MODAL = 1;
   int GLOBAL_QUEUE = 2;
   int GLOBAL_SHOW_LOWER = 4;
   int PUSH_SCREEN_METHOD = 0;
   int QUEUE_STATUS_METHOD = 1;
   long GUID_JVM_REFRESH_DISPLAY = -8341035019292897176L;

   void addUserInputEventListener(UserInputEventListener var1);

   void dismissStatus(Screen var1);

   Screen getActiveScreen();

   int getGlobalPriority(Screen var1);

   int getLayoutGeneration();

   int getScreenCount();

   int getStylusX();

   int getStylusY();

   boolean isPaintingSuspended();

   void pushGlobalScreen(Screen var1, int var2, int var3);

   void pushScreen(Screen var1);

   void pushModalScreen(Screen var1);

   void popScreen(Screen var1);

   void queueStatus(Screen var1, int var2, boolean var3);

   void repaint();

   void relayout();

   void removeUserInputEventListener(UserInputEventListener var1);

   void pushGlobalScreen(Screen var1, int var2, boolean var3);

   void setStylusPos(int var1, int var2);

   void suspendPainting(boolean var1);

   void updateDisplay();

   boolean isTopmostGlobal();

   int getTopmostGlobalPriority();
}
