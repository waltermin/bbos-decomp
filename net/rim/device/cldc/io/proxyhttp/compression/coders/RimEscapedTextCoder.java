package net.rim.device.cldc.io.proxyhttp.compression.coders;

import net.rim.device.api.system.ApplicationRegistry;

public final class RimEscapedTextCoder extends EscapedTextCoder {
   private static final String[] RimStrings = new String[]{
      "Browser-Channel", "Browser-Content", "Browser-Message", "cookie", "etag", "server", "set-cookie", "via", "warning"
   };
   private static final long ID = -688136161628172402L;

   public static final RimEscapedTextCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      RimEscapedTextCoder coder = (RimEscapedTextCoder)registry.getOrWaitFor(-688136161628172402L);
      if (coder == null) {
         coder = new RimEscapedTextCoder();
         registry.put(-688136161628172402L, coder);
      }

      return coder;
   }

   public RimEscapedTextCoder() {
      super(RimStrings, true);
   }
}
