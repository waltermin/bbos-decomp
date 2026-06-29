package net.rim.device.apps.api.ribbon;

import net.rim.device.api.util.Persistable;

public class ApplicationFolder implements Persistable {
   private String _name;
   private String _bundleName;
   private int _resourceId;
   private boolean _movingIconsAllowed;
   private boolean _hidingIconsAllowed;

   public ApplicationFolder(String name, String bundleName, int resourceId) {
      this._name = name;
      this._bundleName = bundleName;
      this._resourceId = resourceId;
      this._movingIconsAllowed = true;
      this._hidingIconsAllowed = true;
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

   public boolean getAllowMoveIcons() {
      return this._movingIconsAllowed;
   }

   public void setAllowMoveIcons(boolean allowed) {
      this._movingIconsAllowed = allowed;
   }

   public boolean getAllowHideIcons() {
      return this._hidingIconsAllowed;
   }

   public void setAllowHideIcons(boolean allowed) {
      this._hidingIconsAllowed = allowed;
   }
}
