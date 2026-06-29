package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.device.cldc.io.utility.URL;

final class ESWindow$SameOriginPolicy {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final boolean applies(Frame f1, Frame f2) {
      String urlstr1 = f1.getUrl();
      String urlstr2 = f2.getUrl();

      try {
         URL url1 = (URL)(new Object(urlstr1));
         URL url2 = (URL)(new Object(urlstr2));
         return url1.getScheme().equalsIgnoreCase(url2.getScheme()) && url1.getHost().equalsIgnoreCase(url2.getHost()) && url1.getPort() == url2.getPort();
      } catch (Throwable var7) {
         e.printStackTrace();
         return false;
      }
   }
}
