package net.rim.device.apps.api.browser;

import net.rim.device.api.system.ApplicationRegistry;

public final class BrowserServices {
   public static final String WLAN_ACTIVATION_BROWSERCONFIG_UID;
   public static final String WLAN_HOTSPOT_LOGIN_URL;

   private BrowserServices() {
   }

   public static final boolean isBrowserAvailable() {
      Browser browser = getBrowser();
      return browser != null ? browser.isBrowserAvailable() : false;
   }

   public static final boolean loadUrl(String url) {
      return loadUrl(url, null);
   }

   public static final boolean loadUrl(String url, String uid) {
      Browser browser = getBrowser();
      return browser != null ? browser.loadUrl(url, uid) : false;
   }

   public static final OTAStatusReportService getOTAStatusReportService() {
      Browser browser = getBrowser();
      return browser != null ? browser.getOTAStatusReportService() : null;
   }

   public static final boolean showBrowser(String uid) {
      Browser browser = getBrowser();
      return browser != null ? browser.showBrowser(uid) : false;
   }

   public static final String getBISEmailSetupServiceUID() {
      Browser browser = getBrowser();
      return browser != null ? browser.getBISEmailSetupServiceUID() : null;
   }

   private static final Browser getBrowser() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (Browser)ar.get(4307171400805038204L);
   }
}
