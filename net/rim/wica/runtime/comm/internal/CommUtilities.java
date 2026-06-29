package net.rim.wica.runtime.comm.internal;

import java.io.InputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import net.rim.device.apps.api.utility.general.URI;

final class CommUtilities {
   public static final int MAX_REDIRECTS = 10;
   private static final String OPENWAVE_REDIRECT_PREFIX = "uplink:///goto?url=";

   private CommUtilities() {
   }

   static final String getRedirectUrl(HttpConnection httpConnection) {
      int statusCode = httpConnection.getResponseCode();
      if (statusCode >= 300 && statusCode < 400) {
         String location = httpConnection.getHeaderField("Location");
         if (location == null || location.length() == 0) {
            location = httpConnection.getHeaderField("Content-Location");
         }

         if (location != null && location.length() != 0) {
            if (location.startsWith("uplink:///goto?url=")) {
               location = location.substring(19);
               if (!location.startsWith("http://") && !location.startsWith("https://")) {
                  location = "http://" + location;
               }
            }

            return URI.getAbsoluteURL(location, httpConnection.getURL());
         }
      }

      return null;
   }

   static final void closeAndIgnoreException(Connection connection) {
      if (connection != null) {
         try {
            connection.close();
         } finally {
            return;
         }
      }
   }

   static final void closeAndIgnoreException(InputStream stream) {
      if (stream != null) {
         try {
            stream.close();
         } finally {
            return;
         }
      }
   }
}
