package net.rim.device.api.crypto.certificate.status;

public interface ProviderUiContext {
   int NO_UI_AVAILABLE = -1;

   int promptUser(String var1, String var2, String[] var3, int[] var4);

   void setErrorMessage(String var1);
}
