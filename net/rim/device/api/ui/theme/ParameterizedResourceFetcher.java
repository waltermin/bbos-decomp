package net.rim.device.api.ui.theme;

import java.util.Enumeration;
import net.rim.device.resources.Resource;

class ParameterizedResourceFetcher extends DefaultResourceFetcher {
   private final Resource _themeResource;

   public ParameterizedResourceFetcher(String themeModuleName, Resource themeResource) {
      super(themeModuleName);
      this._themeResource = themeResource;
   }

   @Override
   public Resource getResources() {
      return this._themeResource;
   }

   @Override
   public Enumeration listResources() {
      Enumeration superResult = super.listResources();
      Enumeration e = this._themeResource.getResourceKeys();
      return e != null ? new MergedEnumeration(e, superResult) : superResult;
   }

   @Override
   public byte[] fetchResource(String name) {
      byte[] superResult = super.fetchResource(name);
      return superResult != null ? superResult : this._themeResource.getResource(name);
   }
}
