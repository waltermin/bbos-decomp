package net.rim.device.apps.internal.applicationdelivery;

import net.rim.device.api.system.EventLogger;

final class ApplicationDeliveryEventLogger {
   public static final long EVENT_LOGGER_STRING_GUID;
   public static final long EVENT_LOGGER_NUMBER_GUID;
   public static final int EVENT_RECEIVE_PACKET;
   public static final int EVENT_ERROR_PACKET_ENCODING;
   public static final int EVENT_ERROR_MODULE_RECEIVED;
   public static final int EVENT_ERROR_WRITING_MODULE;
   public static final int EVENT_ERROR_SAVING_MODULE;
   public static final int EVENT_ERROR_PARSING_PACKET;
   public static final int EVENT_ERROR_INVALID_VERSION;
   public static final int EVENT_ERROR_APPLICATION_TIMEOUT;
   public static final int EVENT_INFORMATION_MODULE_EXISTS;
   public static final int EVENT_INFORMATION_MODULE_INSTALLED;
   public static final int EVENT_INFORMATION_APPLICATION_INSTALLED;
   public static final int EVENT_INFORMATION_DUPLICATE_PACKET;
   public static final int EVENT_ERROR_SECURITY_VIOLATION;
   public static final int EVENT_ERROR_SAVING_MODULE_NOT_ENOUGH_MEMORY;
   public static final int EVENT_ERROR_SAVING_MODULE_GROUP_INFO;
   public static final int EVENT_ERROR_SENDING_ACK_NO_SERVICE;
   public static final int EVENT_ERROR_CREATING_MODULE;
   public static final int EVENT_ERROR_BEGINING_TRANSACTION;
   public static final int EVENT_ERROR_ENDING_TRANSACTION;
   public static final int EVENT_ERROR_FATAL;
   public static final int EVENT_ERROR_WRITING_APPLICATION;
   public static final String EVENT_LOGGER_NAME;

   private ApplicationDeliveryEventLogger() {
   }

   static final void register() {
      EventLogger.register(8238338396594524829L, "net.rim.appdelivery", 2);
      EventLogger.register(-62755093155321176L, "net.rim.appdelivery", 1);
   }

   static final synchronized void logEvent(int eventId, int level) {
      EventLogger.logEvent(8238338396594524829L, eventId, level);
   }

   static final synchronized void logNumberEvent(int number, int level) {
      EventLogger.logEvent(-62755093155321176L, number, level);
   }
}
