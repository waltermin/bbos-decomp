package net.rim.device.apps.api.calendar.ota;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;

public class CICALEventLogger implements OTACalendarConstants {
   private CICALEventLogger() {
   }

   public static void register() {
      EventLogger.register(-256469206327664059L, "net.rim.calendar.ota", 2);
      EventLogger.register(3947765566650592598L, "net.rim.calendar.ota.data");
   }

   public static synchronized void logEvent(int eventId, int level) {
      EventLogger.logEvent(-256469206327664059L, eventId, level);
   }

   public static synchronized void logEvent(int eventId, int level, byte[] packet) {
      logEvent(eventId, level);
      if (packet != null && !PersistentContent.isEncryptionEnabled()) {
         EventLogger.logEvent(3947765566650592598L, packet, level == 2 ? level : 5);
      }
   }

   public static synchronized void logEvent(int eventId, int level, byte[] packet, long value) {
      logEvent(eventId, level, packet);
      EventLogger.logEvent(3947765566650592598L, value, level);
   }

   public static void logEvent(String message, int level) {
      if (!PersistentContent.isEncryptionEnabled()) {
         EventLogger.logEvent(-256469206327664059L, message.getBytes(), level);
      }
   }
}
