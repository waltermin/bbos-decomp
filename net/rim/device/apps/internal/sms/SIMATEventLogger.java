package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.EventLogger;

final class SIMATEventLogger implements SIMATEventViewerEvents {
   private SIMATEventLogger() {
   }

   static final void register() {
      EventLogger.register(-1591633741170059587L, "net.rim.simat", 2);
   }

   public static final synchronized void logDebug(int eventCode) {
      String data = getString(eventCode);
      EventLogger.logEvent(-1591633741170059587L, data.getBytes(), 5);
   }

   public static final synchronized void logDebug(String message) {
      EventLogger.logEvent(-1591633741170059587L, message.getBytes(), 5);
   }

   public static final synchronized void log(int eventCode, int level) {
      String data = getString(eventCode);
      EventLogger.logEvent(-1591633741170059587L, data.getBytes(), level);
   }

   public static final synchronized void log(int eventCode, Exception e) {
      String data = getString(eventCode) + ": " + e.toString();
      EventLogger.logEvent(-1591633741170059587L, data.getBytes(), 2);
   }

   private static final String getString(int eventCode) {
      StringBuffer sb = new StringBuffer();
      switch (eventCode) {
         case -1:
            sb.append("UnknownSIMATEventCode:");
            sb.append(eventCode);
            break;
         case 0:
         default:
            sb.append("atDisplayAlphaId");
            break;
         case 1:
            sb.append("atDisplayText");
            break;
         case 2:
            sb.append("atDisplayTextAck");
            break;
         case 3:
            sb.append("atEventActive");
            break;
         case 4:
            sb.append("atGetInkey");
            break;
         case 5:
            sb.append("atGetInput");
            break;
         case 6:
            sb.append("atLaunchBrowser");
            break;
         case 7:
            sb.append("atLaunchBrowserAck");
            break;
         case 8:
            sb.append("atPlayTone");
            break;
         case 9:
            sb.append("atSelectItem");
            break;
         case 10:
            sb.append("atSessionEnd");
            break;
         case 11:
            sb.append("atSetupIdelModeText");
            break;
         case 12:
            sb.append("atSetupMenu");
            break;
         case 13:
            sb.append("atTimeout");
            break;
         case 14:
            sb.append("SimATPopupScreen");
            break;
         case 15:
            sb.append("SimATScreenGetInput");
            break;
         case 16:
            sb.append("SimToolkitImage");
            break;
         case 17:
            sb.append("SimToolkitRuntime");
            break;
         case 18:
            sb.append("SimToolkitStartup");
            break;
         case 19:
            sb.append("BrowserNotAvail");
      }

      return sb.toString();
   }
}
