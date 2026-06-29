package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.ApplicationManager;

public final class ApplicationLauncher {
   public static String FOLDER_URL_PREFIX = "folder://";
   private static final int FOLDER_URL_PREFIX_LENGTH = 9;

   private ApplicationLauncher() {
   }

   public static final boolean launch(String url) {
      if (url.startsWith(FOLDER_URL_PREFIX)) {
         String folderName = url.substring(9);
         return RibbonLauncherImpl.activateFolder(folderName);
      }

      try {
         ApplicationManager.getApplicationManager().launch(url);
         return true;
      } finally {
         ;
      }
   }
}
