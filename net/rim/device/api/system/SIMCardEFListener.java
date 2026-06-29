package net.rim.device.api.system;

public interface SIMCardEFListener extends SIMCardListener {
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
   int RESPONSE_SIM_APP_ERROR;

   void responseEFInfo(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void responseEFRead(int var1, int var2, int var3, int var4, int var5);

   void responseEFWrite(int var1, int var2, int var3, int var4);
}
