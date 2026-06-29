package net.rim.device.apps.internal.itadmin;

import net.rim.device.api.system.EventLogger;

class ITAdminEventLogger {
   public static final long EVENT_LOGGER_GUID;
   public static final long EVENT_LOGGER_DATA_GUID;
   public static final int EVENT_RECEIVE_PACKET;
   public static final int EVENT_UNKNOWN_COMMAND;
   public static final int EVENT_RESEND_PACKET;
   public static final int EVENT_SEND_PACKET;
   public static final int EVENT_SET_ITPOLICY;
   public static final int EVENT_SET_PASSWORD;
   public static final int EVENT_SET_OWNER_INFO;
   public static final int EVENT_LOCK_DEVICE;
   public static final int EVENT_SET_PEER_2_PEER;
   public static final int EVENT_WRITE_NV;
   public static final int EVENT_READ_NV;
   public static final int EVENT_ALREADY_PROCESSED;
   public static final int EVENT_SERVICE_DATA;
   public static final int EVENT_REREGISTRATION;
   public static final int EVENT_NO_ENCRYPTED_CHANNEL;
   public static final int EVENT_REQ_SERVICE_RECORDS;
   public static final int EVENT_OTA_FLUSH;
   public static final int EVENT_ITPOLICY_ACK_SENT;
   public static final int EVENT_ITPOLICY_ACK_SB_OVERRIDE;
   public static final int EVENT_SOURCE_INVALID;
   public static final int EVENT_AUTHENTICATION_FAILED;
   public static final int EVENT_MALFORMED_POLICY;
   public static final int EVENT_MULTIPLE_COMMANDS;
   public static final int EVENT_KILL_HANDHELD;
   public static final int EVENT_KEY_REGENERATION;
   public static final String EVENT_LOGGER_NAME;

   private ITAdminEventLogger() {
   }

   static void register() {
      EventLogger.register(8708180829147027502L, "net.rim.itadmin", 2);
   }

   static synchronized void logEvent(int eventId, int level) {
      EventLogger.logEvent(8708180829147027502L, eventId, level);
   }

   static synchronized void logEvent(int eventId, int level, byte[] packet) {
      logEvent(eventId, level);
      if (packet != null) {
         EventLogger.logEvent(7700987943844774186L, packet, level == 2 ? level : 5);
      }
   }

   static synchronized void logEvent(int eventId, int level, byte[] packet, long value) {
      logEvent(eventId, level, packet);
      EventLogger.logEvent(7700987943844774186L, value, level);
   }
}
