package net.rim.wica.runtime.provisioning.internal;

import java.io.InputStream;
import net.rim.wica.packaging.PackageUtilities;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

final class DefaultProvisioningService$ExternalEntityResolver implements EntityResolver {
   private DefaultProvisioningService$WicletPackage _appPackage;
   private String _language;

   DefaultProvisioningService$ExternalEntityResolver(DefaultProvisioningService$WicletPackage appPackage, String language) {
      this._appPackage = appPackage;
      this._language = language;
   }

   @Override
   public final InputSource resolveEntity(String publicId, String systemId) {
      try {
         if (systemId != null) {
            String path = PackageUtilities.extractPathFromURI(systemId);
            if (path != null) {
               return (InputSource)(new Object((InputStream)(new Object(this._appPackage.getImmediateResource(path, this._language).getData()))));
            }
         }
      } finally {
         return null;
      }

      return null;
   }
}
