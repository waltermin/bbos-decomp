package net.rim.device.apps.internal.browser.push;

import java.io.DataInput;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.internal.browser.stack.AccumulatorInputStream;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.internal.browser.util.Pipe;

final class BrowserContentConverter extends BaseConverter {
   private static final String CONTENT_LOCATION_KEY;

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput inputStreamObj, Object headersObj) {
      if (headersObj instanceof Object && inputStreamObj instanceof Object) {
         InputStream inputStream = (InputStream)inputStreamObj;
         HttpHeaders headers = (HttpHeaders)headersObj;
         EventLogger.logEvent(1907089860548946979L, 1347448931, 5);
         String url = headers.getPropertyValue("Content-Location");
         if (url == null) {
            EventLogger.logEvent(1907089860548946979L, 1347450229, 3);
            throw new Object();
         }

         CacheResult cacheResult = null;
         AccumulatorInputStream in = (AccumulatorInputStream)(new Object(null, inputStream, null, false));
         Pipe pipe = in.getPipe();
         if (pipe != null && pipe.getLength() > 0) {
            url = URI.getAbsoluteURL(url, null);
            cacheResult = new CacheResult(url, null, headers, 200);
            cacheResult.setData(pipe);
         }

         if (cacheResult == null) {
            throw new Object();
         } else {
            return new BrowserContentModel(cacheResult);
         }
      } else {
         throw new Object();
      }
   }
}
