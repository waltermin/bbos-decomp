package net.rim.device.api.system;

public interface SIMCardEFListener extends SIMCardListener {
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
   int RESPONSE_SIM_APP_ERROR = 11;

   void responseEFInfo(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void responseEFRead(int var1, int var2, int var3, int var4, int var5);

   void responseEFWrite(int var1, int var2, int var3, int var4);
}
