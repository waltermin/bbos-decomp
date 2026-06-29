package net.rim.device.api.ui.theme;

import java.io.InputStream;
import net.rim.device.resources.Resource;

class ThemeModuleDescriptor {
   private final InputStream _description;
   private final String _moduleName;
   private final Resource _resource;
   private final String _manifestVersion;
   private final String _createdBy;
   private final String _osVersion;

   public ThemeModuleDescriptor(InputStream description, String moduleName, Resource resource, String manifestVersion, String createdBy, String osVersion) {
      if (moduleName == null) {
         throw new IllegalArgumentException("theme module name cannot be null");
      }

      this._description = description;
      this._moduleName = moduleName;
      this._resource = resource;
      this._manifestVersion = manifestVersion;
      this._createdBy = createdBy;
      this._osVersion = osVersion;
   }

   public InputStream getDescription() {
      return this._description;
   }

   public String getModuleName() {
      return this._moduleName;
   }

   public Resource getResource() {
      return this._resource;
   }

   public String getManifestVersion() {
      return this._manifestVersion;
   }

   public String getCreatedBy() {
      return this._createdBy;
   }

   public String getOsVersion() {
      return this._osVersion;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ThemeModuleDescriptor)) {
         return false;
      }

      ThemeModuleDescriptor o = (ThemeModuleDescriptor)obj;
      if (this._moduleName == null) {
         if (o._moduleName != null) {
            return false;
         }
      } else if (!this._moduleName.equals(o._moduleName)) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      int result = 17;
      return 37 * result + this._moduleName.hashCode();
   }

   @Override
   public String toString() {
      return this._moduleName + '(' + this.getManifestVersion() + ',' + this.getCreatedBy() + ',' + this.getOsVersion() + ')';
   }
}
