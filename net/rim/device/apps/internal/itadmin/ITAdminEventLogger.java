package net.rim.device.apps.internal.itadmin;

import net.rim.device.api.system.EventLogger;

class ITAdminEventLogger {
   public static final long EVENT_LOGGER_GUID = 8708180829147027502L;
   public static final long EVENT_LOGGER_DATA_GUID = 7700987943844774186L;
   public static final int EVENT_RECEIVE_PACKET = 1380144720;
   public static final int EVENT_UNKNOWN_COMMAND = 1431194446;
   public static final int EVENT_RESEND_PACKET = 1381191236;
   public static final int EVENT_SEND_PACKET = 1397050948;
   public static final int EVENT_SET_ITPOLICY = 1397772108;
   public static final int EVENT_SET_PASSWORD = 1347639108;
   public static final int EVENT_SET_OWNER_INFO = 1397708622;
   public static final int EVENT_LOCK_DEVICE = 1280262987;
   public static final int EVENT_SET_PEER_2_PEER = 1397764688;
   public static final int EVENT_WRITE_NV = 1465010516;
   public static final int EVENT_READ_NV = 1380270404;
   public static final int EVENT_ALREADY_PROCESSED = 1162494804;
   public static final int EVENT_SERVICE_DATA = 1381389122;
   public static final int EVENT_REREGISTRATION = 1381123399;
   public static final int EVENT_NO_ENCRYPTED_CHANNEL = 1313162056;
   public static final int EVENT_REQ_SERVICE_RECORDS = 1381061458;
   public static final int EVENT_OTA_FLUSH = 1179407176;
   public static final int EVENT_ITPOLICY_ACK_SENT = 1094929195;
   public static final int EVENT_ITPOLICY_ACK_SB_OVERRIDE = 1094929194;
   public static final int EVENT_SOURCE_INVALID = 1397313110;
   public static final int EVENT_AUTHENTICATION_FAILED = 1262834258;
   public static final int EVENT_MALFORMED_POLICY = 1111573584;
   public static final int EVENT_MULTIPLE_COMMANDS = 1297435732;
   public static final int EVENT_KILL_HANDHELD = 1263094860;
   public static final int EVENT_KEY_REGENERATION = 1264142158;
   public static final String EVENT_LOGGER_NAME = "net.rim.itadmin";

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
