package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class CachedSecurityInfo implements SecurityInfo {
   @Override
   public final Certificate getServerCertificate() {
      return null;
   }

   @Override
   public final String getProtocolVersion() {
      return null;
   }

   @Override
   public final String getProtocolName() {
      return BrowserResources.getString(612);
   }

   @Override
   public final String getCipherSuite() {
      return null;
   }
}
