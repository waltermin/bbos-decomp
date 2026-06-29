package net.rim.device.api.system;

public interface SIMCardAPDUListener extends SIMCardListener {
   void openSuccessful(byte var1, byte var2);

   void openError(byte var1, byte var2);

   void closeSuccessful(byte var1, byte var2);

   void closeError(byte var1, byte var2, byte var3);

   void exchangeAPDUSuccessful(byte var1, byte var2);

   void exchangeAPDUError(byte var1, byte var2, byte var3);

   void pinOpeartionSuccessful(byte var1, int var2, byte var3);

   void pinOperationUnSuccessful(byte var1, int var2, byte var3, byte var4);
}
