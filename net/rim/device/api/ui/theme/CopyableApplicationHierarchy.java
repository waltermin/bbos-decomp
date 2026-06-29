package net.rim.device.api.ui.theme;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.ApplicationFolder;
import net.rim.device.apps.api.ribbon.ApplicationHierarchy;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.apps.api.ribbon.ModuleApplication;

class CopyableApplicationHierarchy extends ApplicationHierarchy {
   private String[] _applicationNames = new String[0];

   public CopyableApplicationHierarchy(String name) {
      super(name);
   }

   @Override
   public ApplicationProperties createApplicationProperties(String name) {
      Arrays.add(this._applicationNames, name);
      return super.createApplicationProperties(name);
   }

   public ApplicationHierarchy getCopy() {
      return this.copy();
   }

   private ApplicationHierarchy copy() {
      ApplicationHierarchy copy = new CopyableApplicationHierarchy$1(this, this.getName());
      copy.setAllowHideIcons(this.getAllowHideIcons());
      copy.setAllowMoveIcons(this.getAllowMoveIcons());
      copy.setDefaultFolderName(this.getDefaultFolderName());
      this.copyFolders(copy);
      this.copyModules(copy);
      this.copyApplications(copy);
      return copy;
   }

   private void copyFolders(ApplicationHierarchy copy) {
      ApplicationFolder[] folders = this.getFolders();

      for (int i = 0; i < folders.length; i++) {
         String name = folders[i].getName();
         String bundleName = folders[i].getResourceBundleName();
         int resourceId = folders[i].getResourceId();
         copy.addFolder(new ApplicationFolder(name, bundleName, resourceId));
      }
   }

   private void copyModules(ApplicationHierarchy copy) {
      ModuleApplication[] modules = this.getModules();

      for (int i = 0; i < modules.length; i++) {
         String name = modules[i].getName();
         String bundleName = modules[i].getResourceBundleName();
         int resourceId = modules[i].getResourceId();
         ModuleApplication module = new ModuleApplication(name, bundleName, resourceId);
         module.setLaunchString(modules[i].getLaunchString());
         copy.addModule(module);
      }
   }

   private void copyApplications(ApplicationHierarchy copy) {
      for (int i = 0; i < this._applicationNames.length; i++) {
         ApplicationProperties srcProperties = this.getApplicationProperties(this._applicationNames[i]);
         if (srcProperties != null) {
            ApplicationProperties copyProperties = copy.createApplicationProperties(this._applicationNames[i]);
            copyProperties.setCanHide(srcProperties.canHide());
            copyProperties.setDescriptionResource(srcProperties.getBundleName(), srcProperties.getResourceId());
            copyProperties.setDisabled(srcProperties.getDisabled());
            copyProperties.setFolderName(srcProperties.getFolderName());
            copyProperties.setPosition(srcProperties.getPosition());
            copyProperties.setVisible(srcProperties.getVisible());
         }
      }
   }
}
