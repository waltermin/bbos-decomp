package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

final class SLCModel$CacheThread extends Thread {
   private String _uri;
   private String _preferredConfigUID;
   private String _preferredTransportCID;
   private int _preferredConfigType;

   public SLCModel$CacheThread(String uri, String preferredConfigUID, int preferredConfigType, String preferredTransportCID) {
      this._uri = uri;
      if (preferredTransportCID == null) {
         this._preferredTransportCID = "WAP";
      } else {
         this._preferredTransportCID = preferredTransportCID;
      }

      this._preferredConfigUID = preferredConfigUID;
      this._preferredConfigType = preferredConfigType;
   }

   @Override
   public final void run() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      HttpHeaders requestHeaders = browser.getStandardRequestHeaders();
      ModelResult modelResult = new ModelResult(this._uri, 1, requestHeaders);
      BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(
         this._preferredConfigUID, this._preferredConfigType, this._preferredTransportCID
      );
      FetchRequest fetchRequest = new FetchRequest(modelResult, browserConfigRecord, 8);
      RawDataCache rawDataCache = browser.getRawDataCache();
      rawDataCache.get(fetchRequest);
   }
}
