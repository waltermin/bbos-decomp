package net.rim.device.apps.api.icons;

import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

public final class ApplicationIcon {
   public static final int FOCUS_OFF;
   public static final int FOCUS_ON;
   public static final int APP_UNKNOWN_INDEX;
   public static final int APP_CAMERA_INDEX;
   public static final int APP_VOICENOTESRECORDER_INDEX;
   public static final int APP_VIDEOCAMERA_INDEX;
   public static final String APP_CAMERA_STR;
   public static final String APP_VOICENOTESRECORDER_STR;
   public static final String APP_VIDEOCAMERA_STR;
   public static final int ICON_COLUMNS;
   public static final int ICON_ROWS;
   private static final IconCollection ICONS = IconCollection.get("net_rim_application", 3, 2);

   ApplicationIcon() {
   }

   public static final Image getApplicationIconImage(int iconIndex) {
      return ICONS.getImage(iconIndex);
   }

   public static final Image getApplicationIconImage(String appName, int row) {
      return ICONS.getImage(row << 16 | getApplicationIconIndex(appName));
   }

   public static final int getApplicationIconIndex(String appName) {
      int iconIndex = 0;
      if ("Camera".equals(appName)) {
         return 1;
      }

      if ("VoiceNotesRecorder".equals(appName)) {
         return 2;
      }

      if ("VideoCamera".equals(appName)) {
         iconIndex = 3;
      }

      return iconIndex;
   }
}
