package net.rim.device.internal.deviceagent;

import net.rim.device.api.system.EventLogger;

class DeviceAgentEventLogger {
   public static final long EVENT_LOGGER_GUID;
   public static final long EVENT_LOGGER_DATA_GUID;
   public static final int EVENT_ADD_SYNC_OBJECT;
   public static final int EVENT_REMOVE_SYNC_OBJECT;
   public static final int EVENT_UPDATE_SYNC_OBJECT;
   public static final int EVENT_ADD_SYNC_OBJECT_DATA;
   public static final String EVENT_LOGGER_NAME;

   private DeviceAgentEventLogger() {
   }

   static void register() {
      EventLogger.register(8267520505383620909L, "net.rim.deviceagent", 2);
   }

   static synchronized void logEvent(int eventId, int level) {
      EventLogger.logEvent(8267520505383620909L, eventId, level);
   }

   static synchronized void logEvent(int eventId, int level, byte[] packet) {
      logEvent(eventId, level);
      if (packet != null) {
         EventLogger.logEvent(8388771406474631800L, packet, level == 2 ? level : 5);
      }
   }

   static synchronized void logEvent(int eventId, int level, byte[] packet, long value) {
      logEvent(eventId, level, packet);
      EventLogger.logEvent(8388771406474631800L, value, level);
   }

   static void logEvent(String message, int level) {
      EventLogger.logEvent(8267520505383620909L, message.getBytes(), level);
   }
}
