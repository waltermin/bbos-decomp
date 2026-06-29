package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.system.EventLogger;

public class SyncEventLogger {
   public static final long EVENT_LOGGER_GUID = -2063409751941108419L;
   public static final long EVENT_LOGGER_DATA_GUID = 4934071117162963402L;
   public static final int EVENT_SET_LEGACY_POLICY = 1280331596;
   public static final int EVENT_SECURITY_REJECT = 1397051986;
   public static final int EVENT_TIMESTAMP_REJECT = 1413829202;
   public static final int EVENT_SET_SEVER_POLICY = 1397772108;
   public static final int EVENT_ALREADY_PROCESSED = 1414742349;
   public static final int EVENT_SET_P2P = 1397764688;
   public static final int EVENT_UNKNOWN = 1431194446;
   public static final String EVENT_LOGGER_NAME = "net.rim.deviceoptions.synchronization";

   private SyncEventLogger() {
   }

   public static void register() {
      EventLogger.register(-2063409751941108419L, "net.rim.deviceoptions.synchronization", 2);
   }

   public static synchronized void logEvent(int eventId, int level) {
      EventLogger.logEvent(-2063409751941108419L, eventId, level);
   }
}
