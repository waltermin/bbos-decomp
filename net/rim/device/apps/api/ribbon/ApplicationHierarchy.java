package net.rim.device.apps.api.ribbon;

import java.util.Hashtable;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public class ApplicationHierarchy implements Persistable {
   private String _name;
   private String _defaultFolderName;
   private boolean _movingIconsAllowed;
   private boolean _hidingIconsAllowed;
   private ApplicationFolder[] _folders;
   private ModuleApplication[] _modules;
   private Hashtable _properties;

   protected ApplicationHierarchy(String name) {
      this._name = name;
      this._defaultFolderName = "";
      this._movingIconsAllowed = true;
      this._hidingIconsAllowed = true;
      this._folders = new ApplicationFolder[0];
      this._properties = new Hashtable();
      this._modules = new ModuleApplication[0];
      ApplicationFolder rootFolder = new ApplicationFolder("", null, 0);
      this.addFolder(rootFolder);
   }

   public String getName() {
      return this._name;
   }

   public String getDefaultFolderName() {
      return this._defaultFolderName;
   }

   public void setDefaultFolderName(String name) {
      this._defaultFolderName = name;
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

   public final void addFolder(ApplicationFolder folder) {
      synchronized (this._folders) {
         Arrays.add(this._folders, folder);
      }
   }

   public ApplicationFolder getRootFolder() {
      return this._folders[0];
   }

   public ApplicationFolder[] getFolders() {
      return this._folders;
   }

   public void addModule(ModuleApplication module) {
      Arrays.add(this._modules, module);
   }

   public ModuleApplication[] getModules() {
      return this._modules;
   }

   public ApplicationProperties createApplicationProperties(String name) {
      ApplicationProperties properties = new ApplicationProperties();
      properties.setFolderName(this.getDefaultFolderName());
      this._properties.put(name, properties);
      return properties;
   }

   public ApplicationProperties getApplicationProperties(String name) {
      return (ApplicationProperties)this._properties.get(name);
   }
}
