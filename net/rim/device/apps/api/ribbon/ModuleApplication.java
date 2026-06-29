package net.rim.device.apps.api.ribbon;

import net.rim.device.api.util.Persistable;

public class ModuleApplication implements Persistable {
   private String _name;
   private String _bundleName;
   private int _resourceId;
   private String _launchString;

   public ModuleApplication(String name, String bundleName, int resourceId) {
      this._name = name;
      this._bundleName = bundleName;
      this._resourceId = resourceId;
   }

   public String getName() {
      return this._name;
   }

   public String getResourceBundleName() {
      return this._bundleName;
   }

   public int getResourceId() {
      return this._resourceId;
   }

   public void setLaunchString(String url) {
      this._launchString = url;
   }

   public String getLaunchString() {
      return this._launchString;
   }
}
