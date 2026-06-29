package net.rim.device.apps.internal.browser.push;

import java.io.DataInput;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.browser.channel.ChannelModel;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.AccumulatorInputStream;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.internal.browser.util.Pipe;

final class BrowserChannelConverter extends BaseConverter {
   private static final String PUSH_CHANNEL_ID_KEY = "X-Rim-Push-Channel-Id";
   private static final String CONTENT_LOCATION_KEY = "Content-Location";
   private static final String PUSH_DELETE_URL_KEY = "X-Rim-Push-Delete-Url";
   private static final String PUSH_READ_ICON_URL_KEY = "X-Rim-Push-Read-Icon-Url";
   private static final String PUSH_UNREAD_ICON_URL_KEY = "X-Rim-Push-Unread-Icon-Url";
   private static final String PUSH_TITLE_KEY = "X-Rim-Push-Title";
   private static final String PUSH_RIBBON_POSITION_KEY = "X-Rim-Push-Ribbon-Position";
   private static final String PUSH_DESCRIPTION_KEY = "X-Rim-Push-Description";
   private static final String PUSH_PRIORITY_KEY = "X-Rim-Push-Priority";

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object convert(DataInput inputStreamObj, Object headersObj) {
      if (headersObj instanceof Object && inputStreamObj instanceof Object) {
         InputStream inputStream = (InputStream)inputStreamObj;
         String preferredConfigUid = null;
         int preferredConfigType = 1;
         String preferredTransportCid = BrowserConfigRecord.IPPP_SERVICE_CID;
         if (inputStream instanceof Object) {
            PushInputStream pis = (PushInputStream)inputStream;
            if (pis.getConnectionType() == 3) {
               preferredConfigUid = BrowserConfigRecord.mapTransportUIDToConfigUID("IPPP", pis.getSource());
            }
         }

         HttpHeaders headers = (HttpHeaders)headersObj;
         if (headers.getPropertyValue("x-rim-fetch-bearer") != null) {
            preferredConfigUid = HeaderParser.getPreferredConfigUID(headers, null);
            preferredConfigType = HeaderParser.getPreferredConfigType(headers, 1);
            preferredTransportCid = HeaderParser.getPreferredTransportCID(headers, BrowserConfigRecord.IPPP_SERVICE_CID);
         }

         EventLogger.logEvent(1907089860548946979L, 1347445601, 5);
         String id = headers.getPropertyValue("X-Rim-Push-Channel-Id");
         if (id == null) {
            EventLogger.logEvent(1907089860548946979L, 1347450217, 3);
            throw new Object();
         }

         id = (String)(new Object(id.toCharArray()));
         String url = headers.getPropertyValue("Content-Location");
         if (url == null) {
            EventLogger.logEvent(1907089860548946979L, 1347450229, 3);
            throw new Object();
         }

         String deleteUrl = headers.getPropertyValue("X-Rim-Push-Delete-Url");
         String readIconUrl = headers.getPropertyValue("X-Rim-Push-Read-Icon-Url");
         String unreadIconUrl = headers.getPropertyValue("X-Rim-Push-Unread-Icon-Url");
         String title = headers.getPropertyValue("X-Rim-Push-Title");
         if (title == null) {
            title = url;
         }

         String description = headers.getPropertyValue("X-Rim-Push-Description");
         if (description == null) {
            description = "";
         }

         int ribbonPosition = 1;
         String ribbonPos = headers.getPropertyValue("X-Rim-Push-Ribbon-Position");
         if (ribbonPos != null) {
            label89:
            try {
               ribbonPosition = Integer.parseInt(ribbonPos);
            } catch (Throwable var23) {
               EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("PPex\n"))).append(nfe.toString()).toString().getBytes(), 3);
               break label89;
            }
         }

         CacheResult cacheResult = null;
         AccumulatorInputStream in = (AccumulatorInputStream)(new Object(null, inputStream, null, false));
         Pipe pipe = in.getPipe();
         if (pipe != null && pipe.getLength() > 0) {
            url = URI.getAbsoluteURL(url, null);
            cacheResult = new CacheResult(url, null, headers, 200);
            cacheResult.setData(pipe);
         }

         int priority = 0;
         String priorityStr = headers.getPropertyValue("X-Rim-Push-Priority");
         if (priorityStr != null) {
            if (StringUtilities.strEqualIgnoreCase(priorityStr, "high", 1701707776)) {
               priority = 3;
            } else if (StringUtilities.strEqualIgnoreCase(priorityStr, "medium", 1701707776)) {
               priority = 2;
            } else if (StringUtilities.strEqualIgnoreCase(priorityStr, "low", 1701707776)) {
               priority = 1;
            } else if (StringUtilities.strEqualIgnoreCase(priorityStr, "none", 1701707776)) {
               priority = 0;
            }
         }

         return new BrowserChannelModel(
            new ChannelModel(
               id,
               url,
               deleteUrl,
               readIconUrl,
               unreadIconUrl,
               title,
               description,
               ribbonPosition,
               priority,
               preferredConfigUid,
               preferredConfigType,
               preferredTransportCid
            ),
            cacheResult
         );
      } else {
         throw new Object();
      }
   }
}
