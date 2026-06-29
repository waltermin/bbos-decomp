package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.system.EventLogger;

public class SyncEventLogger {
   public static final long EVENT_LOGGER_GUID;
   public static final long EVENT_LOGGER_DATA_GUID;
   public static final int EVENT_SET_LEGACY_POLICY;
   public static final int EVENT_SECURITY_REJECT;
   public static final int EVENT_TIMESTAMP_REJECT;
   public static final int EVENT_SET_SEVER_POLICY;
   public static final int EVENT_ALREADY_PROCESSED;
   public static final int EVENT_SET_P2P;
   public static final int EVENT_UNKNOWN;
   public static final String EVENT_LOGGER_NAME;

   private SyncEventLogger() {
   }

   public static void register() {
      EventLogger.register(-2063409751941108419L, "net.rim.deviceoptions.synchronization", 2);
   }

   public static synchronized void logEvent(int eventId, int level) {
      EventLogger.logEvent(-2063409751941108419L, eventId, level);
   }
}
