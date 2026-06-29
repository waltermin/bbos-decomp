package net.rim.blackberry.api.phone;

public interface PhoneListener {
   int CALL_ERROR_SUBSCRIBER_BUSY;
   int CALL_ERROR_CONGESTION;
   int CALL_ERROR_RADIO_PATH_UNAVAILABLE;
   int CALL_ERROR_NUMBER_UNOBTAINABLE;
   int CALL_ERROR_AUTHORIZATION_FAILURE;
   int CALL_ERROR_EMERGENCY_CALLS_ONLY;
   int CALL_ERROR_HOLD_ERROR;
   int CALL_ERROR_OUTGOING_CALLS_BARRED;
   int CALL_ERROR_GENERAL;
   int CALL_ERROR_MAINTENANCE_REQUIRED;
   int CALL_ERROR_SERVICE_NOT_AVAILABLE;
   int CALL_ERROR_DUE_TO_FADING;
   int CALL_ERROR_LOST_DUE_TO_FADING;
   int CALL_ERROR_TRY_AGAIN;
   int CALL_ERROR_FDN_MISMATCH;
   int CALL_ERROR_CONNECTION_DENIED_BY_NETWORK;
   int CALL_ERROR_NUMBER_NOT_IN_SERVICE;
   int CALL_ERROR_PLEASE_TRY_LATER;
   int CALL_ERROR_SERVICE_CONFLICT;
   int CALL_ERROR_SYSTEM_BUSY_TRY_LATER;
   int CALL_ERROR_USER_BUSY_IN_PRIVATE;
   int CALL_ERROR_USER_BUSY_IN_DATA;
   int CALL_ERROR_USER_NOT_AUTHORIZED;
   int CALL_ERROR_USER_NOT_AVAILABLE;
   int CALL_ERROR_USER_UNKNOWN;
   int CALL_ERROR_USER_NOT_REACHABLE;
   int CALL_ERROR_INCOMING_CALL_BARRED;
   int CALL_ERROR_CALL_REPLACED_BY_STK;
   int CALL_ERROR_STK_CALL_NOT_ALLOWED;

   void callInitiated(int var1);

   void callWaiting(int var1);

   void callIncoming(int var1);

   void callAnswered(int var1);

   void callConnected(int var1);

   void callConferenceCallEstablished(int var1);

   void conferenceCallDisconnected(int var1);

   void callDisconnected(int var1);

   void callDirectConnectConnected(int var1);

   void callDirectConnectDisconnected(int var1);

   void callEndedByUser(int var1);

   void callFailed(int var1, int var2);

   void callResumed(int var1);

   void callHeld(int var1);

   void callAdded(int var1);

   void callRemoved(int var1);
}
