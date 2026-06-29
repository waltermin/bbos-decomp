package net.rim.device.api.system;

public interface SIMCardSecurityListener extends SIMCardListener {
   int RESPONSE_OK = 0;
   int RESPONSE_INVALID_PARAMS = 1;
   int RESPONSE_MEMORY_PROBLEM = 2;
   int RESPONSE_FILE_NOT_FOUND = 3;
   int RESPONSE_SERVICE_NOT_AVAILABLE = 4;
   int RESPONSE_SIM_GENERAL_FAULT = 5;
   int RESPONSE_ACCESS_DENIED = 6;
   int RESPONSE_FILE_INVALIDATED = 7;
   int RESPONSE_ALLOC_ERROR = 8;
   int RESPONSE_SM_FAULT = 9;
   int RESPONSE_CODE_BLOCKED = 10;
   int RESPONSE_SIMAT_BUSY = 11;
   int RESPONSE_SIM_APP_ERROR = 12;
   int RESPONSE_SIM_DDL_ERROR = 13;
   int RESPONSE_UNKNOWN = 14;

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
