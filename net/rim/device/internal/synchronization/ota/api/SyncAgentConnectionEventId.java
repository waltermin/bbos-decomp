package net.rim.device.internal.synchronization.ota.api;

public interface SyncAgentConnectionEventId {
   byte ADD;
   byte DELETE;
   byte DELETEALL;
   byte UPDATE;
   byte REPLACE;
   byte GET;
   byte GETALL;
   byte STATUS;
   byte CLOSE;
   byte RECORD;
   byte START_SERVER_SESSION_SYNC_TRANSACTION;
   byte END_SERVER_SESSION_SYNC_TRANSACTION;
   byte START_DEVICE_SESSION_SYNC_TRANSACTION;
   byte END_DEVICE_SESSION_SYNC_TRANSACTION;
   byte START_SLOWSYNC_TRANSACTION;
   byte END_SLOWSYNC_TRANSACTION;
   byte INITIALIZED;
   byte RESET;
   byte RESET_STATE;
   byte SUSPEND;
   byte RESUME;
   byte GETALLRECORDSHASHES;
   byte FILL_IN_RECORD_CHANGE;
   byte FILL_IN_CHANGE_LIST;
   byte GET_NUMBER_OF_RECORDS;
   byte ENCRYPT_CONTENT;
   byte RETRY_SUSPEND;
   byte RETRY_RESUME;
   byte RETRY_DISABLE;
}
