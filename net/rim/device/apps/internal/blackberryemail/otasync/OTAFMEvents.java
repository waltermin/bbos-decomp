package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.vm.PersistentInteger;

final class OTAFMEvents {
   private static final long OTAFM_EVENT_GUID = -4961693082956770146L;
   private static final long OTAFM_DEBUG_LEVEL_GUID = -2925824056567109495L;
   public static final int EVENTS_ONLY = 0;
   public static final int DEBUG_DATA = 1;
   private static final int DEFAULT_DEBUG_LEVEL = 0;
   public static final int OTAFM_SYNC_STARTED = 1398361643;
   public static final int OTAFM_SYNC_ENDED = 1398361645;
   public static final int OTAFM_SERVICE_RECORD_ADDED = 1397899588;
   public static final int OTAFM_SERVICE_RECORD_UPDATED = 1397904720;
   public static final int OTAFM_SERVICE_RECORD_REMOVED = 1397903949;
   public static final int OTAFM_MESSAGE_LIST_SYNCED = 1297303339;
   public static final int OTAFM_FOLDER_LIST_SYNCED = 1179403307;
   public static final int OTAFM_FAILED_TRANSMISSION_CONFIG = 1414677293;
   public static final int OTAFM_FAILED_TRANSMISSION_OTHER = 1414680365;
   public static final int OTAFM_FAILED_TRANSMISSION_SERVICE = 1414681389;
   public static final int OTAFM_FLUSH_BUFFER = 1179407176;
   public static final int OTAFM_SEND_CONFIGURATION_QUERY = 1044596305;
   public static final int OTAFM_SEND_CONFIGURATION = 1044596295;
   public static final int OTAFM_SEND_MESSAGE_LIST = 1045254983;
   public static final int OTAFM_SEND_RESTORE_MESSAGE_LIST = 1045581139;
   public static final int OTAFM_SEND_MESSAGE_LIST_ABORT = 1045253153;
   public static final int OTAFM_SEND_DELETE_COMMAND = 1044661580;
   public static final int OTAFM_SEND_MOVE_COMMAND = 1045253974;
   public static final int OTAFM_SEND_PURGE_DELETED_MESSAGES = 1045447757;
   public static final int OTAFM_SEND_STATUS_COMMAND = 1045648449;
   public static final int OTAFM_SEND_FOLDER_LIST_REQUEST = 1044794450;
   public static final int OTAFM_SEND_PURGED_MESSAGE_LIST = 1045450060;
   public static final int OTAFM_RECEIVE_MOVE_COMMAND = 1011699542;
   public static final int OTAFM_RECEIVE_DELETE_COMMAND = 1011107148;
   public static final int OTAFM_RECEIVE_DELETE_IGNORED_COMMAND = 1011108909;
   public static final int OTAFM_RECEIVE_STATUS_COMMAND = 1012094017;
   public static final int OTAFM_RECEIVE_CONFIGURATION = 1011041863;
   public static final int OTAFM_RECEIVE_CONFIGURATION_ACK_SAME = 1011041835;
   public static final int OTAFM_RECEIVE_CONFIGURATION_ACK_DIFF = 1011041837;
   public static final int OTAFM_RECEIVE_CONFIGURATION_QUERY = 1011041873;
   public static final int OTAFM_RECEIVE_MESSAGE_LIST_REQUEST = 1011700561;
   public static final int OTAFM_RECEIVE_FOLDER_LIST = 1011240002;
   public static final int OTAFM_RECEIVE_FOLDER_LIST_COMPLETE = 1011240005;
   public static final int OTAFM_RECEIVE_CREATE_FOLDER = 1011041868;
   public static final int OTAFM_RECEIVE_DELETE_FOLDER = 1011107404;
   public static final int OTAFM_RECEIVE_MODIFY_FOLDER = 1011697228;
   public static final int OTAFM_RECEIVE_UNKNOWN_COMMAND = 1012223563;
   public static final int OTAFM_DELETE_FOLDER_DONE = 558255172;
   public static final int OTAFM_TRANSMISSION_ERROR = 1413829202;
   public static final int OTAFM_RETRY_PACKET = 1381257817;
   public static final int OTAFM_ITPOLICY_CHANGED = 1011438672;
   public static final int OTAFM_FROM = 1179799373;
   public static final int OTAFM_TO = 538989647;
   public static final int OTAFM_STATUS = 1398030676;
   public static final int OTAFM_DELETE_FOLDER = 759581764;
   public static final int OTAFM_TRANSMIT = 1414676814;
   public static final int OTAFM_MESSAGE_LIST_ACK_DONE = 1296843051;
   public static final int OTAFM_MESSAGE_LIST_ACK_IGNORED = 1296843053;
   public static final int OTAFM_SEND_MODIFY_FOLDER_ATTRIBUTES = 1397573185;
   public static final int OTAFM_CONFIG_QUERY_IN_STACK = 558057041;
   public static final int OTAFM_UNKNOWN_TRANSACTION = 1062490702;

   private OTAFMEvents() {
   }

   static final void register() {
      EventLogger.register(-4961693082956770146L, "net.rim.otafm", 2);
   }

   static final int getDebugLevel() {
      int id = PersistentInteger.getId(-2925824056567109495L, 0);
      return PersistentInteger.get(id);
   }

   static final void setDebugLevel(int newLevel) {
      int id = PersistentInteger.getId(-2925824056567109495L, 0);
      PersistentInteger.set(id, newLevel);
   }

   static final void logEvent(int eventId, int level) {
      EventLogger.logEvent(-4961693082956770146L, eventId, level);
   }

   private static final String serviceRecordName(ServiceRecord serviceRecord) {
      return ((StringBuffer)(new Object())).append(serviceRecord.getName()).append('/').append(serviceRecord.getUid()).toString();
   }

   static final void logEvent(int eventId, ServiceRecord serviceRecord, int level) {
      if (level <= EventLogger.getMinimumLevel()) {
         if (getDebugLevel() >= 1 || level == 2) {
            logEvent(eventId, serviceRecordName(serviceRecord), level);
            return;
         }

         logEvent(eventId, level);
      }
   }

   static final void logEvent(int eventId, ServiceRecord serviceRecord, int value, int level) {
      logEvent(eventId, serviceRecord, value, level, false);
   }

   static final void logEvent(int eventId, ServiceRecord serviceRecord, int value, int level, boolean force) {
      if (level <= EventLogger.getMinimumLevel()) {
         if (getDebugLevel() >= 1 || level == 2 || force) {
            logEvent(eventId, serviceRecord, String.valueOf(value), level, force);
            return;
         }

         logEvent(eventId, level);
      }
   }

   static final void logEvent(int eventId, ServiceRecord serviceRecord, String message, int level) {
      logEvent(eventId, serviceRecord, message, level, false);
   }

   static final void logEvent(int eventId, ServiceRecord serviceRecord, String message, int level, boolean force) {
      if (level <= EventLogger.getMinimumLevel()) {
         if (getDebugLevel() >= 1 || level == 2 || force) {
            logEvent(eventId, ((StringBuffer)(new Object())).append(serviceRecordName(serviceRecord)).append(", ").append(message).toString(), level, force);
            return;
         }

         logEvent(eventId, level);
      }
   }

   static final void logEvent(int eventId, int value, int level) {
      if (level <= EventLogger.getMinimumLevel()) {
         if (getDebugLevel() >= 1 || level == 2) {
            logEvent(eventId, String.valueOf(value), level);
            return;
         }

         logEvent(eventId, level);
      }
   }

   static final void logEvent(int eventId, String message, int level) {
      logEvent(eventId, message, level, false);
   }

   static final void logEvent(int eventId, String message, int level, boolean force) {
      if (level <= EventLogger.getMinimumLevel()) {
         if (getDebugLevel() >= 1 || level == 2 || force) {
            StringBuffer buffer = (StringBuffer)(new Object());
            buffer.append((char)(eventId >> 24 & 0xFF)).append((char)(eventId >> 16 & 0xFF)).append((char)(eventId >> 8 & 0xFF)).append((char)(eventId & 0xFF));
            buffer.append(" - ").append(message);
            EventLogger.logEvent(-4961693082956770146L, buffer.toString().getBytes(), level);
            return;
         }

         logEvent(eventId, level);
      }
   }
}
