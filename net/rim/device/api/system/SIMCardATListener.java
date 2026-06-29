package net.rim.device.api.system;

public interface SIMCardATListener extends SIMCardListener {
   int ALLOWED_KEYS_DIGITS_ONLY = 0;
   int ALLOWED_KEYS_SMS_CHARACTER_SET = 1;
   int ALLOWED_KEYS_YES_NO = 2;
   int SET_UP_CALL_ACTION_HOLD_CALLS = 0;
   int SET_UP_CALL_ACTION_DISCONNECT_CALLS = 1;
   int SET_UP_CALL_ACTION_IF_NOT_BUSY = 2;
   int CC_STATUS_NO_CHANGE = 0;
   int CC_STATUS_CALL_CHANGED = 1;
   int CC_STATUS_CALL_BARRED = 2;
   int CC_STATUS_CALL_FAILED = 8;
   int CC_STATUS_CALL_OK = 10;
   int CC_STATUS_CALL_REPLACED_BY_SS = 3;
   int CC_STATUS_CALL_REPLACED_BY_USSD = 16;
   int CC_STATUS_SS_CHANGED = 4;
   int CC_STATUS_SS_BARRED = 5;
   int CC_STATUS_SS_REPLACED_BY_CALL = 6;
   int CC_STATUS_SS_REPLACED_BY_USSD = 17;
   int CC_STATUS_SS_FAILED = 7;
   int CC_STATUS_SS_OK = 9;
   int CC_STATUS_SS_UNKNOWN = 255;
   int CC_STATUS_SM_CHANGED = 11;
   int CC_STATUS_SM_BARRED = 12;
   int CC_STATUS_SM_OK = 13;
   int CC_STATUS_USSD_FAILED = 14;
   int CC_STATUS_USSD_OK = 15;

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
