package net.rim.device.internal.system;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;

public final class LockEventLogger {
   private static final long GUID;
   private static final String NAME;
   public static final int LOCKACTION_APP;
   public static final int REAL_TIME_CLOCK_UPDATE;
   public static final int IDLE_TIMEOUT;
   public static final int LONGTERM_TIMEOUT;
   public static final int HOLSTER_STATE_CHANGED;
   public static final int RIBBON_NEXT_KEY_REPEAT;
   public static final int RIBBON_ALT_ENTER;
   public static final int SYNC_SECURITY_OPTIONS_PASSWORD_ENABLED;
   public static final int SYNC_SECURITY_OPTIONS_CONTENT_PROTECTION;
   public static final int SYNC_DEVICE_OPTIONS_ITADMIN_SET_ITPOLICY;
   public static final int SYNC_DEVICE_OPTIONS_SECURITY_SET_PASSWORD;
   public static final int BLUETOOH_SEND_CHALLENGE;
   public static final int ITADMIN_OTA_ITPOLICY;
   public static final int ITADMIN_LOCK_HANDHELD;
   public static final int ITADMIN_KILL_HANDHELD;
   public static final int ITADMIN_SET_PASSWORD;
   public static final int SMARTCARD_REMOVED;

   public static final void logLockEvent(int code) {
      logLockEvent(code, 0);
   }

   public static final void logLockEvent(int code, int level) {
      if (code == 1281977448) {
         Security security = Security.getInstance();
         if (DeviceInfo.isInHolster() || !security.isPasswordEnabled()) {
            return;
         }

         if (security.activateLongTermTimeOut()) {
            code = 1282176116;
         } else {
            code = 1281975412;
         }
      }

      if (!EventLogger.logEvent(-785375246662306450L, code, level)) {
         EventLogger.register(-785375246662306450L, "Device Lock Command", 2);
         EventLogger.logEvent(-785375246662306450L, code, level);
      }
   }

   private LockEventLogger() {
   }
}
