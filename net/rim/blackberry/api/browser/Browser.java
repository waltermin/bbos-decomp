package net.rim.blackberry.api.browser;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;

public final class Browser {
   public static String CID_BROWSERCONFIG = BrowserConfigRecord.SERVICE_CID;

   private Browser() {
   }

   public static final BrowserSession getDefaultSession() {
      return new DefaultBrowserSession();
   }

   public static final BrowserSession getSession(String uid) {
      return new ConnectionSpecificBrowserSession(uid);
   }

   public static final String getTransportUid(ServiceRecord browserConfigSr) {
      BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(browserConfigSr);
      return rec != null ? rec.getPropertyAsString(4) : null;
   }
}
