package net.rim.device.api.browser.util;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;

public final class UserAgent {
   private static final String DEFAULT_BROWSER_VERSION = "4.3.0";
   static Class class$net$rim$device$api$browser$util$UserAgent;

   private UserAgent() {
   }

   public static final String getDefaultUserAgent() {
      return "BlackBerry"
         + DeviceInfo.getDeviceName()
         + '/'
         + getBrowserVersion(false)
         + " Profile/"
         + System.getProperty("microedition.profiles")
         + " Configuration/"
         + System.getProperty("microedition.configuration")
         + " VendorID/"
         + Branding.getVendorId();
   }

   public static final String getBrowserVersion() {
      return getBrowserVersion(false);
   }

   public static final String getBrowserVersion(boolean fullVersion) {
      int handle = CodeModuleManager.getModuleHandleForClass(
         class$net$rim$device$api$browser$util$UserAgent == null
            ? (class$net$rim$device$api$browser$util$UserAgent = class$("net.rim.device.api.browser.util.UserAgent"))
            : class$net$rim$device$api$browser$util$UserAgent
      );
      if (handle <= 0) {
         return "4.3.0";
      }

      String version = CodeModuleManager.getModuleVersion(handle);
      int versionLength = version.length();
      int state = 0;
      int i = 0;
      int thirdPeriodIndex = -1;

      while (state != -1 && i < versionLength) {
         char c = version.charAt(i);
         switch (state) {
            case -1:
               break;
            case 0:
            default:
               if (c == '.') {
                  state = 1;
               } else if (c < '0' || c > '9') {
                  state = -1;
               }
               break;
            case 1:
               if (c == '.') {
                  state = 2;
               } else if (c < '0' || c > '9') {
                  state = -1;
               }
               break;
            case 2:
               if (c == '.') {
                  state = 3;
                  thirdPeriodIndex = i;
               } else if (c < '0' || c > '9') {
                  state = -1;
               }
               break;
            case 3:
               if (c < '0' || c > '9') {
                  state = -1;
               }
         }

         i++;
      }

      if (thirdPeriodIndex < 0 || i != versionLength || state != 3) {
         return "4.3.0";
      } else {
         return fullVersion ? version : version.substring(0, thirdPeriodIndex);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
