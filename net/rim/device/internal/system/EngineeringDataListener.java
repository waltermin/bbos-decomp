package net.rim.device.internal.system;

import net.rim.device.api.system.RadioListener;

public interface EngineeringDataListener extends RadioListener {
   int SRVPGM_DATA_NV_READ_OK = 1;
   int SRVPGM_DATA_NV_READ_FAIL = 2;
   int SRVPGM_DATA_NV_WRITE_OK = 3;
   int SRVPGM_DATA_NV_WRITE_FAIL = 4;
   int SRVPGM_CORRECT_OTKSL = 5;
   int SRVPGM_CORRECT_MSL = 6;
   int SRVPGM_WRONG_LOCK_CODE = 7;
   int SRVPGM_OTKSL_INACTIVE = 8;
   int SRVPGM_EXCESS_LOCK_COUNT = 9;
   int SRVPGM_A_KEY_VALIDATION_FAIL = 10;
   int MASTER_RESET_FAILED = 0;
   int MASTER_RESET_WRONG_PASSWORD = 1;
   int MASTER_RESET_SUCCESS = 2;

   void engResponseMasterReset(int var1);

   void engServiceProgramEvent(int var1);

   void engOTASPResponse(byte[] var1);

   void engDataInitialized();

   void engDataChanged();

   void engDataLogworthy(int var1);
}
