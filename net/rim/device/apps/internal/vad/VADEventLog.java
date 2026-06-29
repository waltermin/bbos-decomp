package net.rim.device.apps.internal.vad;

import net.rim.device.api.system.EventLogger;

public final class VADEventLog {
   public static final long GUID;
   private static String EVENT_LOGGER_TITLE = "net.rim.vad";
   private static final boolean LOG_TO_BUGDISP;
   public static final int ENGINE_INIT;
   public static final int ENGINE_START;
   public static final int ENGINE_STOP;
   public static final int ENGINE_EVENT;
   public static final int ENGINE_EXIT;
   public static final int ENGINE_DONE;
   public static final int ADDRESS_BOOK_REBUILD;
   public static final int ADDRESS_BOOK_TOO_LARGE;
   public static final int ADDRESS_BOOK_REBUILT;
   public static final int LANGUAGE_CHANGE;
   public static final int EVENT_QUEUED;

   static final void init() {
      EventLogger.register(19574030040793832L, EVENT_LOGGER_TITLE, 2);
   }

   static final void log(int id) {
      EventLogger.logEvent(19574030040793832L, id, 0);
      System.out
         .println(
            ((StringBuffer)(new Object()))
               .append(EVENT_LOGGER_TITLE)
               .append(": ")
               .append((char)(id >> 24 & 0xFF))
               .append((char)(id >> 16 & 0xFF))
               .append((char)(id >> 8 & 0xFF))
               .append((char)(id & 0xFF))
               .toString()
         );
   }

   static final void log(int id, int value) {
      EventLogger.logEvent(19574030040793832L, id, value, 16, 0);
      System.out
         .println(
            ((StringBuffer)(new Object()))
               .append(EVENT_LOGGER_TITLE)
               .append(": ")
               .append((char)(id >> 24 & 0xFF))
               .append((char)(id >> 16 & 0xFF))
               .append((char)(id >> 8 & 0xFF))
               .append((char)(id & 0xFF))
               .append(": ")
               .append(Integer.toHexString(value))
               .toString()
         );
   }

   static final void log(String msg) {
      EventLogger.logEvent(19574030040793832L, msg.getBytes());
      System.out.println(((StringBuffer)(new Object())).append(EVENT_LOGGER_TITLE).append(": ").append(msg).toString());
   }
}
