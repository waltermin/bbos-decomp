package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.util.UserAgent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.WLAN;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.system.InternalServices;

public final class RTSPStackAdapter implements NetworkPageFetcher {
   public static final void registerOnStartup() {
      if (InternalServices.isSoftwareCapable(13)) {
         StackManager stackManager = StackManager.getInstance();
         stackManager.registerNetworkPageFetcher(new RTSPStackAdapter(), new String[]{"rtsp"});
      }
   }

   @Override
   public final void reinitialize() {
   }

   @Override
   public final InputConnection fetchPage(FetchRequest fetchRequest, BrowserConfigRecord browserConfig) {
      ModelResult modelResult = fetchRequest.getModelResult();
      CacheResult cacheResult = modelResult.getCacheResult();
      String urlToOpen = cacheResult.getURLWithoutFragment();
      String userAgent = null;
      HttpHeaders requestHeaders = modelResult.getRequestHeaders();
      if (requestHeaders != null) {
         userAgent = requestHeaders.getPropertyValue("User-Agent");
      }

      if (userAgent == null) {
         userAgent = UserAgent.getDefaultUserAgent();
      }

      if (WLAN.isWLANAllowed() && WLAN.isAssociated() != null) {
         urlToOpen = ((StringBuffer)(new Object())).append(urlToOpen).append(";interface=wifi").toString();
      } else {
         String transportCid = browserConfig.getPropertyAsString(3);
         if (transportCid.equals(WPTCPServiceRecord.SERVICE_CID)) {
            urlToOpen = ((StringBuffer)(new Object())).append(urlToOpen).append(";connectionuid=").append(browserConfig.getPropertyAsString(4)).toString();
         }
      }

      RTSPConnection conn = (RTSPConnection)(new Object(urlToOpen, userAgent));
      HttpHeaders resultHeaders = (HttpHeaders)(new Object());
      cacheResult.setResponseHeaders(resultHeaders);
      cacheResult.setResponseMessage("OK");
      cacheResult.setStatus(200);
      cacheResult.setLocalContent(true);
      return conn;
   }

   @Override
   public final SessionStats getSessionStats() {
      return null;
   }
}
