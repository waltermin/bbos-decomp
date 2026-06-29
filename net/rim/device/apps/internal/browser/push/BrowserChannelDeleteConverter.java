package net.rim.device.apps.internal.browser.push;

import java.io.DataInput;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

final class BrowserChannelDeleteConverter extends BaseConverter {
   private static final String PUSH_CHANNEL_ID_KEY = "X-Rim-Push-Channel-Id";

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput inputStreamObj, Object headersObj) throws SerializationException {
      if (headersObj instanceof HttpHeaders && inputStreamObj instanceof InputStream) {
         InputStream inputStream = (InputStream)inputStreamObj;
         HttpHeaders headers = (HttpHeaders)headersObj;
         EventLogger.logEvent(1907089860548946979L, 1347445604, 5);
         String id = headers.getPropertyValue("X-Rim-Push-Channel-Id");
         if (id == null) {
            EventLogger.logEvent(1907089860548946979L, 1347450217, 3);
            throw new SerializationException();
         } else {
            BrowserPushUtilities.readContent(inputStream);
            return new BrowserChannelDeleteModel(id);
         }
      } else {
         throw new SerializationException();
      }
   }
}
