package net.rim.device.cldc.io.proxyhttp.compression.coders;

import net.rim.device.api.system.ApplicationRegistry;

public final class TokenEscapedTextCoder extends EscapedTextCoder {
   private static final String[] TokenStrings = new String[]{
      "100-continue",
      "attachment",
      "basic ",
      "bytes",
      "chunked",
      "close",
      "compress",
      "deflate",
      "filename",
      "gzip",
      "identity",
      "ISO-8859-1",
      "Keep-Alive",
      "max-age=",
      "max-stale",
      "min-fresh=",
      "must-revalidate",
      "no-cache",
      "no-store",
      "no-transform",
      "none",
      "only-if-cached",
      "private",
      "proxy-revalidate",
      "public",
      "realm=",
      "s-maxage=",
      "US-ASCII",
      "UTF-8"
   };
   private static final long ID = 4236317391598801787L;

   public static final TokenEscapedTextCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      TokenEscapedTextCoder coder = (TokenEscapedTextCoder)registry.getOrWaitFor(4236317391598801787L);
      if (coder == null) {
         coder = new TokenEscapedTextCoder();
         registry.put(4236317391598801787L, coder);
      }

      return coder;
   }

   public TokenEscapedTextCoder() {
      super(TokenStrings, true);
   }
}
