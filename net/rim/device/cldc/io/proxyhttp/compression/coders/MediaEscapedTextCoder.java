package net.rim.device.cldc.io.proxyhttp.compression.coders;

import net.rim.device.api.system.ApplicationRegistry;

public final class MediaEscapedTextCoder extends EscapedTextCoder {
   private static final String[] MediaStrings = new String[]{
      "application/*",
      "application/java-vm",
      "application/octet-stream",
      "application/vnd.rim.",
      "application/vnd.rim.cod",
      "application/vnd.rim.htmlc",
      "application/vnd.wap.wmlc",
      "application/vnd.wap.wmlscriptc",
      "application/x-www-form-urlencoded",
      "image/*",
      "image/gif",
      "image/jpeg",
      "image/png",
      "image/vnd.wap.wbmp",
      "multipart/*",
      "multipart/alternative",
      "multipart/byteranges",
      "multipart/form-data",
      "multipart/mixed",
      "text/*",
      "text/html",
      "text/plain",
      "text/vnd.sun.j2me.app-descriptor",
      "text/vnd.wap.wml",
      "text/vnd.wap.wmlscript",
      "text/x-vcalendar",
      "text/x-vcard",
      "text/xml"
   };
   private static final long ID;

   public static final MediaEscapedTextCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      MediaEscapedTextCoder coder = (MediaEscapedTextCoder)registry.getOrWaitFor(-5963165340771518656L);
      if (coder == null) {
         coder = new MediaEscapedTextCoder();
         registry.put(-5963165340771518656L, coder);
      }

      return coder;
   }

   public MediaEscapedTextCoder() {
      super(MediaStrings, true);
   }
}
