package net.rim.blackberry.api.phone;

public interface PhoneListener {
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
