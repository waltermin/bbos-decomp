package net.rim.device.internal.synchronization.ota.api;

public interface SyncAgentConnectionEventId {
   byte ADD = 1;
   byte DELETE = 2;
   byte DELETEALL = 3;
   byte UPDATE = 4;
   byte REPLACE = 5;
   byte GET = 6;
   byte GETALL = 7;
   byte STATUS = 8;
   byte CLOSE = 9;
   byte RECORD = 10;
   byte START_SERVER_SESSION_SYNC_TRANSACTION = 49;
   byte END_SERVER_SESSION_SYNC_TRANSACTION = 50;
   byte START_DEVICE_SESSION_SYNC_TRANSACTION = 51;
   byte END_DEVICE_SESSION_SYNC_TRANSACTION = 52;
   byte START_SLOWSYNC_TRANSACTION = 53;
   byte END_SLOWSYNC_TRANSACTION = 54;
   byte INITIALIZED = 55;
   byte RESET = 56;
   byte RESET_STATE = 57;
   byte SUSPEND = 58;
   byte RESUME = 59;
   byte GETALLRECORDSHASHES = 60;
   byte FILL_IN_RECORD_CHANGE = 61;
   byte FILL_IN_CHANGE_LIST = 62;
   byte GET_NUMBER_OF_RECORDS = 63;
   byte ENCRYPT_CONTENT = 64;
   byte RETRY_SUSPEND = 65;
   byte RETRY_RESUME = 66;
   byte RETRY_DISABLE = 67;
}
