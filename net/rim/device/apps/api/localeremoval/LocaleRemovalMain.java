package net.rim.device.apps.api.localeremoval;

class LocaleRemovalMain {
   public static void libMain(String[] args) {
      if (handleRemovalState() && LocaleRemovalUtility.getMultiLanguageBuildType(false) != 0 && !LocaleRemovalLowMemoryListener.hasRemovalLowMemoryActivated()) {
         LocaleRemovalLowMemoryListener.loadListener();
      }
   }

   private static boolean handleRemovalState() {
      int currentRemovalStatus = LocaleRemovalUtility.getRemovalStatus();
      boolean startLowMemoryManagement = true;
      switch (currentRemovalStatus) {
         case 1:
            LocaleRemovalUtility.setRemovalStatus(2);
            return false;
         case 2:
            startLowMemoryManagement = false;
         case 0:
            return startLowMemoryManagement;
         case 3:
         default:
            LocaleRemovalUtility.startLocaleRemovalProcess(null, null, null, true);
            return false;
      }
   }
}
