package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.system.Bitmap;

public interface ApplicationLauncherField {
   void resetFocusToTop();

   void clearFolderStack();

   boolean isRootFolder();

   boolean goToFolder(String var1);

   boolean popFolder();

   void setActiveFolder(InternalApplicationFolder var1);

   ApplicationEntry getSelectedApplication();

   ApplicationEntry getMovingApplication();

   void setFocus(ApplicationEntry var1);

   InternalApplicationFolder getActiveFolder();

   boolean hidingIconsAllowed();

   boolean hasHiddenApplications();

   void setShowHiddenApps(boolean var1);

   void loadApplications();

   ApplicationEntry getApplicationByHotKey(char var1);

   void deleteAllApplications();

   void beginMoveApplication();

   boolean moveApplicationInProgress();

   void completeMoveApplication(boolean var1);

   void updateMovingApplicationPosition();

   void setBackgroundBitmap(Bitmap var1);

   ApplicationEntry getApplicationAt(int var1);

   void setHotKeysDisabled(boolean var1);

   void refreshApplication(ApplicationEntry var1);
}
