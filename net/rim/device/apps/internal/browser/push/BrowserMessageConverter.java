package net.rim.device.apps.internal.browser.push;

import java.io.DataInput;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.store.BrowserFolders;

final class BrowserMessageConverter extends BaseConverter {
   private static final String CONTENT_LOCATION_KEY;
   private static final String PUSH_TITLE_KEY;

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput inputStreamObj, Object headersObj) {
      if (headersObj instanceof Object && inputStreamObj instanceof Object) {
         InputStream inputStream = (InputStream)inputStreamObj;
         HttpHeaders headers = (HttpHeaders)headersObj;
         EventLogger.logEvent(1907089860548946979L, 1347448941, 5);
         String preferredConfigUid = null;
         int preferredConfigType = 1;
         String preferredTransportCid = BrowserConfigRecord.IPPP_SERVICE_CID;
         if (inputStream instanceof Object) {
            PushInputStream in = (PushInputStream)inputStream;
            if (in.getConnectionType() == 3) {
               preferredConfigUid = BrowserConfigRecord.mapTransportUIDToConfigUID("IPPP", in.getSource());
            }
         }

         if (headers.getPropertyValue("x-rim-fetch-bearer") != null) {
            preferredConfigUid = HeaderParser.getPreferredConfigUID(headers, null);
            preferredConfigType = HeaderParser.getPreferredConfigType(headers, 1);
            preferredTransportCid = HeaderParser.getPreferredTransportCID(headers, BrowserConfigRecord.IPPP_SERVICE_CID);
         }

         String url = headers.getPropertyValue("Content-Location");
         if (url == null) {
            EventLogger.logEvent(1907089860548946979L, 1347450229, 3);
            throw new Object();
         }

         String title = headers.getPropertyValue("X-Rim-Push-Title");
         if (title == null) {
            title = url;
         }

         byte[] content = BrowserPushUtilities.readContent(inputStream);
         CacheResult cacheResult = null;
         if (content != null && content.length > 0) {
            cacheResult = new CacheResult(url, content, headers, 200);
         }

         Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
         if (browserFolder != null) {
            ModelResult modelResult = new ModelResult(url, 8193, null);
            modelResult.setCacheResult(cacheResult);
            modelResult.setConfigUID(preferredConfigUid);
            modelResult.setTransportCID(preferredTransportCid);
            modelResult.setConfigType(preferredConfigType);
            BrowserPageModel pageModel = new BrowserPageModel(0, title, modelResult, browserFolder.getLUID());
            return new BrowserMessageModel(browserFolder, pageModel, url);
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }
}
