package net.rim.wica.runtime.logging;

import net.rim.device.api.system.EventLogger;

public final class Logger {
   public static final int ALWAYS_LOG;
   public static final int ERROR;
   public static final int WARNING;
   public static final int INFORMATION;
   public static final int DEBUG;
   public static final int EVENT_ID_UNUSED;
   private static Logger _logger = new Logger();

   private Logger() {
      EventLogger.register(-5077035485006973644L, "net.rim.mds.runtime", 2);
   }

   public static final void log(String message) {
      log(null, message, 0, -1);
   }

   public static final void log(String message, int type) {
      log(null, message, type, -1);
   }

   public static final void log(String source, String message) {
      log(source, message, 0, -1);
   }

   public static final void log(String message, int type, int eventId) {
      log(null, message, type, eventId);
   }

   public static final void log(String source, String message, int type) {
      log(source, message, type, -1);
   }

   public static final void log(String source, String message, int type, int eventId) {
      _logger.logInternal(source, message, type, eventId);
   }

   private final void logInternal(String source, String message, int type, int eventId) {
      StringBuffer msg = (StringBuffer)(new Object(message));
      if (source != null) {
         msg.append(", ");
         msg.append(source);
      }

      if (eventId != -1) {
         msg.append(", ");
         msg.append(eventId);
      }

      EventLogger.logEvent(-5077035485006973644L, msg.toString().getBytes(), type);
   }
}
