package net.rim.device.api.system;

public interface SIMCardATListener extends SIMCardListener {
   int ALLOWED_KEYS_DIGITS_ONLY;
   int ALLOWED_KEYS_SMS_CHARACTER_SET;
   int ALLOWED_KEYS_YES_NO;
   int SET_UP_CALL_ACTION_HOLD_CALLS;
   int SET_UP_CALL_ACTION_DISCONNECT_CALLS;
   int SET_UP_CALL_ACTION_IF_NOT_BUSY;
   int CC_STATUS_NO_CHANGE;
   int CC_STATUS_CALL_CHANGED;
   int CC_STATUS_CALL_BARRED;
   int CC_STATUS_CALL_FAILED;
   int CC_STATUS_CALL_OK;
   int CC_STATUS_CALL_REPLACED_BY_SS;
   int CC_STATUS_CALL_REPLACED_BY_USSD;
   int CC_STATUS_SS_CHANGED;
   int CC_STATUS_SS_BARRED;
   int CC_STATUS_SS_REPLACED_BY_CALL;
   int CC_STATUS_SS_REPLACED_BY_USSD;
   int CC_STATUS_SS_FAILED;
   int CC_STATUS_SS_OK;
   int CC_STATUS_SS_UNKNOWN;
   int CC_STATUS_SM_CHANGED;
   int CC_STATUS_SM_BARRED;
   int CC_STATUS_SM_OK;
   int CC_STATUS_USSD_FAILED;
   int CC_STATUS_USSD_OK;

   void atDisplayText(byte[] var1, int var2, boolean var3, boolean var4, boolean var5);

   void atGetInkey(byte[] var1, int var2, int var3, boolean var4, int var5);

   void atGetInput(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, int var9);

   void atSelectItem(byte[] var1, Object[] var2, int[] var3, int var4, boolean var5);

   void atSetUpMenu(byte[] var1, Object[] var2, int[] var3, boolean var4);

   void atSetUpCall(byte[] var1, byte[] var2, boolean var3, int var4);

   void atPlayTone(byte[] var1);

   void atCallControl(int var1, int var2);

   void atLaunchBrowser(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6);

   void atSessionEnd();

   void atTimeout();

   void atSetUpIdleModeText(byte[] var1, int var2);

   void atDisplayAlphaID(byte[] var1);
}
