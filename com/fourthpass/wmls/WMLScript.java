package com.fourthpass.wmls;

import net.rim.device.apps.api.utility.general.URI;

public final class WMLScript {
   private URL _url;
   private ConstantPool _cp;
   private PragmaPool _pp;
   private FunctionPool _fp;

   public WMLScript(WMLInputStream stream, URL url, String charset, URI referer) {
      this._url = url;
      int version = stream.readByte();
      verifyVersion(version);
      stream.readMBInt();
      this._cp = new ConstantPool(stream, charset);
      this._pp = new PragmaPool(stream);
      this._fp = new FunctionPool(stream);
      if (referer != null) {
         String accessDomain = this._pp.getAccessDomain(this._cp);
         if (accessDomain != null) {
            String authority = referer.getAuthority();
            if (authority != null && !authority.endsWith(accessDomain)) {
               throw new RuntimeException("Domain does not match");
            }
         }

         String accessPath = this._pp.getAccessPath(this._cp);
         if (accessPath != null) {
            accessPath = new URI(accessPath, referer.getAbsoluteURL()).getPath();
            if (!referer.getPath().startsWith(accessPath)) {
               throw new RuntimeException("Path does not match");
            }
         }
      }
   }

   public final URL getURL() {
      return this._url;
   }

   final ConstantPool getConstantPool() {
      return this._cp;
   }

   final FunctionPool getFunctionPool() {
      return this._fp;
   }

   private static final void verifyVersion(int version) throws Exception {
      int major = ((version & 0xFF) >> 4) + 1;
      int minor = version & 15;
      if (major != 1 && minor != 1) {
         throw new Exception("Unsupported Script Version, only 1.1 is supported");
      }
   }
}
