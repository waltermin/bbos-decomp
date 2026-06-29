package net.rim.device.apps.internal.applicationdelivery;

import net.rim.device.api.system.EventLogger;

final class ApplicationDeliveryEventLogger {
   public static final long EVENT_LOGGER_STRING_GUID = 8238338396594524829L;
   public static final long EVENT_LOGGER_NUMBER_GUID = -62755093155321176L;
   public static final int EVENT_RECEIVE_PACKET = 1382240363;
   public static final int EVENT_ERROR_PACKET_ENCODING = 1162898245;
   public static final int EVENT_ERROR_MODULE_RECEIVED = 1162695267;
   public static final int EVENT_ERROR_WRITING_MODULE = 1163359309;
   public static final int EVENT_ERROR_SAVING_MODULE = 1163097677;
   public static final int EVENT_ERROR_PARSING_PACKET = 1162895693;
   public static final int EVENT_ERROR_INVALID_VERSION = 1162440278;
   public static final int EVENT_ERROR_APPLICATION_TIMEOUT = 1161916500;
   public static final int EVENT_INFORMATION_MODULE_EXISTS = 1229800824;
   public static final int EVENT_INFORMATION_MODULE_INSTALLED = 1229801838;
   public static final int EVENT_INFORMATION_APPLICATION_INSTALLED = 1229015406;
   public static final int EVENT_INFORMATION_DUPLICATE_PACKET = 1229221968;
   public static final int EVENT_ERROR_SECURITY_VIOLATION = 1163092822;
   public static final int EVENT_ERROR_SAVING_MODULE_NOT_ENOUGH_MEMORY = 1163087213;
   public static final int EVENT_ERROR_SAVING_MODULE_GROUP_INFO = 1163085641;
   public static final int EVENT_ERROR_SENDING_ACK_NO_SERVICE = 1163084115;
   public static final int EVENT_ERROR_CREATING_MODULE = 1162048077;
   public static final int EVENT_ERROR_BEGINING_TRANSACTION = 1161979732;
   public static final int EVENT_ERROR_ENDING_TRANSACTION = 1162178132;
   public static final int EVENT_ERROR_FATAL = 1162245228;
   public static final int EVENT_ERROR_WRITING_APPLICATION = 1163359297;
   public static final String EVENT_LOGGER_NAME = "net.rim.appdelivery";

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
