package net.rim.device.apps.internal.vad;

import net.rim.device.api.system.EventLogger;

public final class VADEventLog {
   public static final long GUID = 19574030040793832L;
   private static String EVENT_LOGGER_TITLE = "net.rim.vad";
   private static final boolean LOG_TO_BUGDISP = true;
   public static final int ENGINE_INIT = 1229867348;
   public static final int ENGINE_START = 1398035028;
   public static final int ENGINE_STOP = 1398034256;
   public static final int ENGINE_EVENT = 1163284052;
   public static final int ENGINE_EXIT = 1163413844;
   public static final int ENGINE_DONE = 1146048069;
   public static final int ADDRESS_BOOK_REBUILD = 1094992978;
   public static final int ADDRESS_BOOK_TOO_LARGE = 1094863175;
   public static final int ADDRESS_BOOK_REBUILT = 1094863956;
   public static final int LANGUAGE_CHANGE = 1279348295;
   public static final int EVENT_QUEUED = 1163284821;

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
