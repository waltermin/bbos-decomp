package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.vm.PersistentInteger;

final class OTAFMEvents {
   private static final long OTAFM_EVENT_GUID;
   private static final long OTAFM_DEBUG_LEVEL_GUID;
   public static final int EVENTS_ONLY;
   public static final int DEBUG_DATA;
   private static final int DEFAULT_DEBUG_LEVEL;
   public static final int OTAFM_SYNC_STARTED;
   public static final int OTAFM_SYNC_ENDED;
   public static final int OTAFM_SERVICE_RECORD_ADDED;
   public static final int OTAFM_SERVICE_RECORD_UPDATED;
   public static final int OTAFM_SERVICE_RECORD_REMOVED;
   public static final int OTAFM_MESSAGE_LIST_SYNCED;
   public static final int OTAFM_FOLDER_LIST_SYNCED;
   public static final int OTAFM_FAILED_TRANSMISSION_CONFIG;
   public static final int OTAFM_FAILED_TRANSMISSION_OTHER;
   public static final int OTAFM_FAILED_TRANSMISSION_SERVICE;
   public static final int OTAFM_FLUSH_BUFFER;
   public static final int OTAFM_SEND_CONFIGURATION_QUERY;
   public static final int OTAFM_SEND_CONFIGURATION;
   public static final int OTAFM_SEND_MESSAGE_LIST;
   public static final int OTAFM_SEND_RESTORE_MESSAGE_LIST;
   public static final int OTAFM_SEND_MESSAGE_LIST_ABORT;
   public static final int OTAFM_SEND_DELETE_COMMAND;
   public static final int OTAFM_SEND_MOVE_COMMAND;
   public static final int OTAFM_SEND_PURGE_DELETED_MESSAGES;
   public static final int OTAFM_SEND_STATUS_COMMAND;
   public static final int OTAFM_SEND_FOLDER_LIST_REQUEST;
   public static final int OTAFM_SEND_PURGED_MESSAGE_LIST;
   public static final int OTAFM_RECEIVE_MOVE_COMMAND;
   public static final int OTAFM_RECEIVE_DELETE_COMMAND;
   public static final int OTAFM_RECEIVE_DELETE_IGNORED_COMMAND;
   public static final int OTAFM_RECEIVE_STATUS_COMMAND;
   public static final int OTAFM_RECEIVE_CONFIGURATION;
   public static final int OTAFM_RECEIVE_CONFIGURATION_ACK_SAME;
   public static final int OTAFM_RECEIVE_CONFIGURATION_ACK_DIFF;
   public static final int OTAFM_RECEIVE_CONFIGURATION_QUERY;
   public static final int OTAFM_RECEIVE_MESSAGE_LIST_REQUEST;
   public static final int OTAFM_RECEIVE_FOLDER_LIST;
   public static final int OTAFM_RECEIVE_FOLDER_LIST_COMPLETE;
   public static final int OTAFM_RECEIVE_CREATE_FOLDER;
   public static final int OTAFM_RECEIVE_DELETE_FOLDER;
   public static final int OTAFM_RECEIVE_MODIFY_FOLDER;
   public static final int OTAFM_RECEIVE_UNKNOWN_COMMAND;
   public static final int OTAFM_DELETE_FOLDER_DONE;
   public static final int OTAFM_TRANSMISSION_ERROR;
   public static final int OTAFM_RETRY_PACKET;
   public static final int OTAFM_ITPOLICY_CHANGED;
   public static final int OTAFM_FROM;
   public static final int OTAFM_TO;
   public static final int OTAFM_STATUS;
   public static final int OTAFM_DELETE_FOLDER;
   public static final int OTAFM_TRANSMIT;
   public static final int OTAFM_MESSAGE_LIST_ACK_DONE;
   public static final int OTAFM_MESSAGE_LIST_ACK_IGNORED;
   public static final int OTAFM_SEND_MODIFY_FOLDER_ATTRIBUTES;
   public static final int OTAFM_CONFIG_QUERY_IN_STACK;
   public static final int OTAFM_UNKNOWN_TRANSACTION;

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
