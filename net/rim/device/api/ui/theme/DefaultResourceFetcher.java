package net.rim.device.api.ui.theme;

import java.util.Enumeration;
import net.rim.device.api.util.EmptyEnumeration;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

class DefaultResourceFetcher implements ResourceFetcher {
   private Resource _resources;
   private String _moduleName;
   private boolean _initialized;

   DefaultResourceFetcher() {
   }

   DefaultResourceFetcher(String moduleName) {
      this._initialized = true;
      this._resources = Resource$Internal.getResourceClass(moduleName);
      if (this._resources == null) {
         throw new IllegalArgumentException("Resources not found: " + moduleName);
      }

      this._moduleName = moduleName;
   }

   @Override
   public void setResourcesFromModule(String moduleName) {
      if (this._initialized) {
         throw new IllegalStateException("ResourceFetcher already initialized.");
      }

      this._resources = Resource$Internal.getResourceClass(moduleName);
      this._moduleName = moduleName;
   }

   @Override
   public Resource getResources() {
      return this._resources;
   }

   @Override
   public Enumeration listResources() {
      this.checkState();
      Enumeration e = this._resources.getResourceKeys();
      return e != null ? e : new EmptyEnumeration();
   }

   @Override
   public byte[] fetchResource(String name) {
      this.checkState();
      return this._resources.getResource(name);
   }

   @Override
   public String getBaseURL() {
      return "cod://" + this._moduleName + "/";
   }

   private void checkState() {
      if (this._resources == null) {
         throw new IllegalStateException("resources must be set before being used");
      }
   }
}
