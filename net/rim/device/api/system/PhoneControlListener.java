package net.rim.device.api.system;

public interface PhoneControlListener extends RadioListener {
   int SS_ACTION_REGISTRATION = 1;
   int SS_ACTION_ACTIVATION = 2;
   int SS_ACTION_ERASURE = 3;
   int SS_ACTION_DEACTIVATION = 4;
   int SS_ACTION_INTERROGATION = 5;
   int SS_ACTION_PASSWORD_REGISTRATION = 6;
   int SS_ACTION_USS_OPERATION = 7;
   int SS_ACTION_QUERY = 8;
   int SS_INTERROGATION_STATUS_ACTIVE = 2;
   int SS_INTERROGATION_STATUS_REGISTERED = 4;
   int SS_INTERROGATION_STATUS_PROVISIONED = 1;
   int SS_INTERROGATION_STATUS_QUIESCENT = 8;
   int SS_ERROR_CALL_BARRED = 0;
   int SS_ERROR_STATUS = 1;
   int SS_ERROR_SUBS_VIOLATION = 2;
   int SS_ERROR_INCOMPATIBILITY = 3;
   int SS_ERROR_ABSENT_SUBSCRIBER = 4;
   int SS_ERROR_SYSTEM_FAILURE = 5;
   int SS_ERROR_PASSWORD_FAILURE = 6;
   int SS_ERROR_UNKNOWN = 7;
   int SS_ERROR_UNKNOWN_SS_SERVICE = 8;
   int SS_ERROR_UNSUPPORTED_SS_SERVICE = 9;
   int SS_ERROR_CHV_MISMATCH = 10;
   int SS_ERROR_GENERAL_ERROR_RETURNED = 11;
   int SS_ERROR_BARRING_DENIED = 12;
   int SS_ERROR_FORWARDING_DENIED = 13;
   int SS_ERROR_FDN_MISMATCH = 14;
   int SS_ERROR_UNKNOWN_SUBSCRIBER = 15;
   int SS_ERROR_ILLEGAL_SUBSCRIBER = 16;
   int SS_ERROR_BRERSERV_NOT_PROV = 17;
   int SS_ERROR_TELESERV_NOT_PROV = 18;
   int SS_ERROR_ILLEGAL_EQUIPMENT = 19;
   int SS_ERROR_ILLEGAL_OPERATION = 20;
   int SS_ERROR_NOT_AVAILABLE = 21;
   int SS_ERROR_FACILITY_NOT_SUPPORTED = 22;
   int SS_ERROR_DATA_MISSING = 23;
   int SS_ERROR_UNEXPECTED_DATA_VALUE = 24;
   int SS_ERROR_PWD_REGISTRATION_FAILURE = 25;
   int SS_ERROR_NEGATIVE_PWD_CHECK = 26;
   int SS_ERROR_NUMOF_PWD_ATTEMPT_VIOL = 27;
   int SS_ERROR_UNKNOWN_ALPHABET = 28;
   int SS_ERROR_USSD_BUSY = 29;
   int SS_ERROR_WRONG_PASSWORD = 1573;
   int SS_ERROR_PWD_INCORRECT_LENGTH = 30;
   int SS_ERROR_STK_NOT_ALLOWED = 31;
   int SS_STATE_NOT_PROVISIONED = 0;
   int SS_STATE_PROVISIONED = 1;
   int SS_STATE_ACTIVE = 2;
   int FDN_RESPONSE_SUCCESS = 1665;
   int FDN_RESPONSE_SIM_NOT_READY = 1666;
   int FDN_RESPONSE_SIM_ERROR = 1667;
   int SS_PASSWORD_REQUEST_CURRENT_PASSWORD = 1;
   int SS_PASSWORD_REQUEST_NEW_PASSWORD = 2;
   int SS_PASSWORD_REQUEST_REPEAT_NEW_PASSWORD = 3;

   void ssRequestSucceeded(int var1, int var2, int var3, int var4, boolean var5, boolean var6);

   void ssRequestFailed(int var1, int var2, boolean var3);

   void ssRequestRejected(boolean var1);

   void ssRequestReleased(boolean var1);

   void ssRequestInvalidPassword();

   void ssPasswordRequested(int var1);

   void ssUpdated(int var1, int var2);

   void ssNotification(int var1);

   void ssUssDisplay(byte[] var1, int var2, boolean var3);

   void featureReady();

   void responseEnableFDN(int var1);

   void voiceLineChanged(int var1);

   void voicemailCountUpdated(int var1, int var2);

   void alternateLinesUpdated();
}
