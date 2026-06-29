package net.rim.device.apps.api.ribbon;

import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public class ApplicationProperties implements Persistable {
   private int _position = 200;
   private boolean _visible = true;
   private boolean _canHide = true;
   private String _folderName = "";
   private boolean _disabled;
   private boolean _dirty;
   private String _bundleName;
   private int _resourceId;
   private String _customImageName = null;
   private String _customFocusImageName = null;
   private String _alias = null;
   public static final int DEFAULT_POSITION;
   public static final int UNDEFINED_POSITION;

   protected ApplicationProperties() {
   }

   public ApplicationProperties(ApplicationProperties properties) {
      this();
      if (properties != null) {
         this._position = properties._position;
         this._visible = properties._visible;
         this._canHide = properties._canHide;
         this._folderName = properties._folderName;
         this._disabled = properties._disabled;
         this._bundleName = properties._bundleName;
         this._resourceId = properties._resourceId;
         this._customImageName = properties._customImageName;
         this._customFocusImageName = properties._customFocusImageName;
         this._alias = properties._alias;
      }
   }

   public void setPosition(int position) {
      if (this._position != position) {
         this._dirty = true;
      }

      this._position = position;
   }

   public int getPosition() {
      return this._position;
   }

   public void setVisible(boolean visible) {
      if (this._visible != visible) {
         this._dirty = true;
      }

      this._visible = visible;
   }

   public boolean getVisible() {
      return this._visible;
   }

   public void setCanHide(boolean canHide) {
      if (this._canHide != canHide) {
         this._dirty = true;
      }

      this._canHide = canHide;
   }

   public boolean canHide() {
      return this._canHide;
   }

   public void setFolderName(String folderName) {
      if (!StringUtilities.strEqual(this._folderName, folderName)) {
         this._dirty = true;
      }

      this._folderName = folderName;
   }

   public String getFolderName() {
      return this._folderName;
   }

   public void setDisabled(boolean disabled) {
      if (this._disabled != disabled) {
         this._dirty = true;
      }

      this._disabled = disabled;
   }

   public boolean getDisabled() {
      return this._disabled;
   }

   public void setDescriptionResource(String bundleName, int resourceId) {
      this._bundleName = bundleName;
      this._resourceId = resourceId;
   }

   public String getBundleName() {
      return this._bundleName;
   }

   public int getResourceId() {
      return this._resourceId;
   }

   public void clearDirtyFlag() {
      this._dirty = false;
   }

   public boolean getDirtyFlag() {
      return this._dirty;
   }

   public void setCustomImageName(String name) {
      if (!StringUtilities.strEqual(this._customImageName, name)) {
         this._dirty = true;
      }

      this._customImageName = name;
   }

   public String getCustomImageName() {
      return this._customImageName;
   }

   public void setCustomFocusImageName(String name) {
      if (!StringUtilities.strEqual(this._customFocusImageName, name)) {
         this._dirty = true;
      }

      this._customFocusImageName = name;
   }

   public String getCustomFocusImageName() {
      return this._customFocusImageName;
   }

   public void setAlias(String alias) {
      if (!StringUtilities.strEqual(this._alias, alias)) {
         this._dirty = true;
      }

      this._alias = alias;
   }

   public String getAlias() {
      return this._alias;
   }
}
