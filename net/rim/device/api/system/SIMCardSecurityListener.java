package net.rim.device.api.system;

public interface SIMCardSecurityListener extends SIMCardListener {
   int RESPONSE_OK;
   int RESPONSE_INVALID_PARAMS;
   int RESPONSE_MEMORY_PROBLEM;
   int RESPONSE_FILE_NOT_FOUND;
   int RESPONSE_SERVICE_NOT_AVAILABLE;
   int RESPONSE_SIM_GENERAL_FAULT;
   int RESPONSE_ACCESS_DENIED;
   int RESPONSE_FILE_INVALIDATED;
   int RESPONSE_ALLOC_ERROR;
   int RESPONSE_SM_FAULT;
   int RESPONSE_CODE_BLOCKED;
   int RESPONSE_SIMAT_BUSY;
   int RESPONSE_SIM_APP_ERROR;
   int RESPONSE_SIM_DDL_ERROR;
   int RESPONSE_UNKNOWN;

   void requestSendPIN(int var1);

   void requestSendPUK(int var1);

   void pinValid();

   void responseEnablePIN(int var1, int var2, int var3);

   void responseDisablePIN(int var1, int var2, int var3);

   void responseChangePIN(int var1, int var2, int var3);

   void responseValidatePIN(int var1, int var2, int var3);

   void responseDeactivateMEP(boolean var1);

   void wtlsKeyWriteComplete(int var1);
}
