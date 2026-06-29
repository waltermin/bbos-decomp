package net.rim.device.api.system;

public interface PhoneControlListener extends RadioListener {
   int SS_ACTION_REGISTRATION;
   int SS_ACTION_ACTIVATION;
   int SS_ACTION_ERASURE;
   int SS_ACTION_DEACTIVATION;
   int SS_ACTION_INTERROGATION;
   int SS_ACTION_PASSWORD_REGISTRATION;
   int SS_ACTION_USS_OPERATION;
   int SS_ACTION_QUERY;
   int SS_INTERROGATION_STATUS_ACTIVE;
   int SS_INTERROGATION_STATUS_REGISTERED;
   int SS_INTERROGATION_STATUS_PROVISIONED;
   int SS_INTERROGATION_STATUS_QUIESCENT;
   int SS_ERROR_CALL_BARRED;
   int SS_ERROR_STATUS;
   int SS_ERROR_SUBS_VIOLATION;
   int SS_ERROR_INCOMPATIBILITY;
   int SS_ERROR_ABSENT_SUBSCRIBER;
   int SS_ERROR_SYSTEM_FAILURE;
   int SS_ERROR_PASSWORD_FAILURE;
   int SS_ERROR_UNKNOWN;
   int SS_ERROR_UNKNOWN_SS_SERVICE;
   int SS_ERROR_UNSUPPORTED_SS_SERVICE;
   int SS_ERROR_CHV_MISMATCH;
   int SS_ERROR_GENERAL_ERROR_RETURNED;
   int SS_ERROR_BARRING_DENIED;
   int SS_ERROR_FORWARDING_DENIED;
   int SS_ERROR_FDN_MISMATCH;
   int SS_ERROR_UNKNOWN_SUBSCRIBER;
   int SS_ERROR_ILLEGAL_SUBSCRIBER;
   int SS_ERROR_BRERSERV_NOT_PROV;
   int SS_ERROR_TELESERV_NOT_PROV;
   int SS_ERROR_ILLEGAL_EQUIPMENT;
   int SS_ERROR_ILLEGAL_OPERATION;
   int SS_ERROR_NOT_AVAILABLE;
   int SS_ERROR_FACILITY_NOT_SUPPORTED;
   int SS_ERROR_DATA_MISSING;
   int SS_ERROR_UNEXPECTED_DATA_VALUE;
   int SS_ERROR_PWD_REGISTRATION_FAILURE;
   int SS_ERROR_NEGATIVE_PWD_CHECK;
   int SS_ERROR_NUMOF_PWD_ATTEMPT_VIOL;
   int SS_ERROR_UNKNOWN_ALPHABET;
   int SS_ERROR_USSD_BUSY;
   int SS_ERROR_WRONG_PASSWORD;
   int SS_ERROR_PWD_INCORRECT_LENGTH;
   int SS_ERROR_STK_NOT_ALLOWED;
   int SS_STATE_NOT_PROVISIONED;
   int SS_STATE_PROVISIONED;
   int SS_STATE_ACTIVE;
   int FDN_RESPONSE_SUCCESS;
   int FDN_RESPONSE_SIM_NOT_READY;
   int FDN_RESPONSE_SIM_ERROR;
   int SS_PASSWORD_REQUEST_CURRENT_PASSWORD;
   int SS_PASSWORD_REQUEST_NEW_PASSWORD;
   int SS_PASSWORD_REQUEST_REPEAT_NEW_PASSWORD;

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
