package net.rim.device.internal.system;

import net.rim.device.api.system.RadioListener;

public interface EngineeringDataListener extends RadioListener {
   int SRVPGM_DATA_NV_READ_OK;
   int SRVPGM_DATA_NV_READ_FAIL;
   int SRVPGM_DATA_NV_WRITE_OK;
   int SRVPGM_DATA_NV_WRITE_FAIL;
   int SRVPGM_CORRECT_OTKSL;
   int SRVPGM_CORRECT_MSL;
   int SRVPGM_WRONG_LOCK_CODE;
   int SRVPGM_OTKSL_INACTIVE;
   int SRVPGM_EXCESS_LOCK_COUNT;
   int SRVPGM_A_KEY_VALIDATION_FAIL;
   int MASTER_RESET_FAILED;
   int MASTER_RESET_WRONG_PASSWORD;
   int MASTER_RESET_SUCCESS;

   void engResponseMasterReset(int var1);

   void engServiceProgramEvent(int var1);

   void engOTASPResponse(byte[] var1);

   void engDataInitialized();

   void engDataChanged();

   void engDataLogworthy(int var1);
}
