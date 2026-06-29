package net.rim.device.internal.system;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;

public final class LockEventLogger {
   private static final long GUID = -785375246662306450L;
   private static final String NAME = "Device Lock Command";
   public static final int LOCKACTION_APP = 1281454192;
   public static final int REAL_TIME_CLOCK_UPDATE = 1281977448;
   public static final int IDLE_TIMEOUT = 1281975412;
   public static final int LONGTERM_TIMEOUT = 1282176116;
   public static final int HOLSTER_STATE_CHANGED = 1281912684;
   public static final int RIBBON_NEXT_KEY_REPEAT = 1282567787;
   public static final int RIBBON_ALT_ENTER = 1282564460;
   public static final int SYNC_SECURITY_OPTIONS_PASSWORD_ENABLED = 1282634608;
   public static final int SYNC_SECURITY_OPTIONS_CONTENT_PROTECTION = 1282634595;
   public static final int SYNC_DEVICE_OPTIONS_ITADMIN_SET_ITPOLICY = 1282630761;
   public static final int SYNC_DEVICE_OPTIONS_SECURITY_SET_PASSWORD = 1282630768;
   public static final int BLUETOOH_SEND_CHALLENGE = 1281516392;
   public static final int ITADMIN_OTA_ITPOLICY = 1281979503;
   public static final int ITADMIN_LOCK_HANDHELD = 1281979500;
   public static final int ITADMIN_KILL_HANDHELD = 1281979499;
   public static final int ITADMIN_SET_PASSWORD = 1281979504;
   public static final int SMARTCARD_REMOVED = 1282630514;

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
