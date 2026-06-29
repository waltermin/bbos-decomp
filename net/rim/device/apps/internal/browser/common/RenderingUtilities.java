package net.rim.device.apps.internal.browser.common;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.stack.HeaderParser;

public final class RenderingUtilities {
   public static final void setTranscodeHeader(HttpHeaders headers, boolean transcode) {
      if (transcode) {
         headers.setProperty("x-rim-transcode-content", "*/*");
      } else {
         headers.setProperty("x-rim-transcode-content", "none");
      }
   }

   public static final void setReferrer(HttpHeaders headers, String referrer) {
      if (referrer != null) {
         headers.setProperty(HeaderParser.REFERER, referrer);
      }
   }
}
