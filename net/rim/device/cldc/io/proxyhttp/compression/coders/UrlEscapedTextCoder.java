package net.rim.device.cldc.io.proxyhttp.compression.coders;

import net.rim.device.api.system.ApplicationRegistry;

public final class UrlEscapedTextCoder extends EscapedTextCoder {
   private static final String[] UrlStrings = new String[]{
      ".asp",
      ".com:80",
      ".net:80",
      ".org:80",
      ".wml",
      "BlackBerry/",
      "CONNECT",
      "DELETE",
      "GET",
      "HEAD",
      "HTTP/1.0",
      "HTTP/1.1",
      "http://",
      "http://mobile.",
      "http://wap.",
      "http://www.",
      "OPTIONS",
      "POST",
      "PUT",
      "TRACE"
   };
   private static final long ID;

   public static final UrlEscapedTextCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      UrlEscapedTextCoder coder = (UrlEscapedTextCoder)registry.getOrWaitFor(4482128995303993346L);
      if (coder == null) {
         coder = new UrlEscapedTextCoder();
         registry.put(4482128995303993346L, coder);
      }

      return coder;
   }

   public UrlEscapedTextCoder() {
      super(UrlStrings, true);
   }
}
