package net.rim.device.api.system;

public interface PhoneCallListener extends RadioListener {
   int CALL_ERROR_SUBSCRIBER_BUSY = 1;
   int CALL_ERROR_CONGESTION = 2;
   int CALL_ERROR_RADIO_PATH_UNAVAILABLE = 3;
   int CALL_ERROR_NUMBER_UNOBTAINABLE = 4;
   int CALL_ERROR_AUTHORIZATION_FAILURE = 5;
   int CALL_ERROR_EMERGENCY_CALLS_ONLY = 6;
   int CALL_ERROR_HOLD_ERROR = 7;
   int CALL_ERROR_OUTGOING_CALLS_BARRED = 8;
   int CALL_ERROR_GENERAL = 9;
   int CALL_ERROR_MAINTENANCE_REQUIRED = 10;
   int CALL_ERROR_SERVICE_NOT_AVAILABLE = 11;
   int CALL_ERROR_DUE_TO_FADING = 12;
   int CALL_ERROR_LOST_DUE_TO_FADING = 13;
   int CALL_ERROR_TRY_AGAIN = 14;
   int CALL_ERROR_FDN_MISMATCH = 15;
   int CALL_ERROR_CONNECTION_DENIED_BY_NETWORK = 16;
   int CALL_ERROR_NUMBER_NOT_IN_SERVICE = 17;
   int CALL_ERROR_PLEASE_TRY_LATER = 18;
   int CALL_ERROR_SERVICE_CONFLICT = 19;
   int CALL_ERROR_SYSTEM_BUSY_TRY_LATER = 20;
   int CALL_ERROR_USER_BUSY_IN_PRIVATE = 21;
   int CALL_ERROR_USER_BUSY_IN_DATA = 22;
   int CALL_ERROR_USER_NOT_AUTHORIZED = 23;
   int CALL_ERROR_USER_NOT_AVAILABLE = 24;
   int CALL_ERROR_USER_UNKNOWN = 25;
   int CALL_ERROR_USER_NOT_REACHABLE = 26;
   int CALL_ERROR_INCOMING_CALL_BARRED = 27;
   int CALL_ERROR_CALL_REPLACED_BY_STK = 28;
   int CALL_ERROR_STK_CALL_NOT_ALLOWED = 29;
   int CALL_ERROR_NO_USER_RESPONDING = 30;
   int CALL_ERROR_ALERTING_NO_ANSWER = 31;
   int CALL_ERROR_CLIR_REJECTED = 32;
   int CALL_ERROR_CLIR_NOT_SUBSCRIBED = 33;
   int CALL_ERROR_CLIR_NOT_IMPLEMENTED = 34;
   int CALL_ERROR_IMSI_NOT_IN_VLR_CAUSE_4 = 35;
   int CALL_ERROR_IMEI_NOT_ACCEPTED_CAUSE_5 = 36;
   int OTA_STATUS_ACTIVATION_STARTED = 0;
   int OTA_STATUS_ACTIVATION_SUCCESS = 2;
   int OTA_STATUS_ACTIVATION_FAILED = 3;
   int OTA_STATUS_MAX_UNLOCK = 4;
   int OTA_STATUS_COMMIT_SUCCESS = 1;
   int OTA_STATUS_SPC_UNLOCK_SUCCESS = 5;
   int OTA_STATUS_SPC_UNLOCK_FAILED = 6;
   int OTA_STATUS_SPC_UPDATE_SUCCESS = 7;
   int OTA_STATUS_SPC_UPDATE_FAILED = 8;
   int OTA_STATUS_NAM_DOWNLOAD_SUCCESS = 9;
   int OTA_STATUS_NAM_DOWNLOAD_FAILED = 10;
   int OTA_STATUS_MDN_DOWNLOAD_SUCCESS = 11;
   int OTA_STATUS_MDN_DOWNLOAD_FAILED = 12;
   int OTA_STATUS_PRL_DOWNLOAD_SUCCESS = 13;
   int OTA_STATUS_PRL_DOWNLOAD_FAILED = 14;
   int OTA_STATUS_COMMIT_FAILED = 15;
   int CALL_TRANSFER_SUCCESSFUL = 0;
   int CALL_TRANSFER_TIMED_OUT = 1;
   int CALL_TRANSFER_ERROR = 2;
   int CALL_TRANSFER_STATE_NOT_IN_PROGRESS = 1;
   int CALL_TRANSFER_STATE_CALLING = 2;
   int CALL_TRANSFER_STATE_CONNECTED = 3;
   int CALL_TRANSFER_STATE_JOINED = 4;
   int CALL_TRANSFER_STATE_COMPLETE = 5;

   void callIncoming(int var1);

   void callDisplayUpdated(int var1);

   void callWaiting(int var1);

   void callInitiated(int var1);

   void callConnected(int var1);

   void callFailed(int var1, int var2);

   void callDelivered(int var1);

   void callManipulateFailed(int var1, int var2);

   void callDisconnected(int var1);

   void callHeld(int var1);

   void callResumed(int var1);

   void callAdded(int var1);

   void callRemoved(int var1);

   void callTransferred(int var1, int var2);

   void callTransferStateUpdated(int var1, int var2);

   void callVoicePrivacyUpdated(int var1, boolean var2);

   void callOTAStatusUpdated(int var1, int var2);

   void dtmfData(int var1);
}
