package net.rim.device.apps.api.browser;

public interface Browser {
   long APP_REGISTRY_KEY;

   boolean isBrowserAvailable();

   boolean loadUrl(String var1, String var2);

   OTAStatusReportService getOTAStatusReportService();

   boolean showBrowser(String var1);

   String getBISEmailSetupServiceUID();
}
