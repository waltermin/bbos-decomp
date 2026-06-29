package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.ApplicationFolder;
import net.rim.device.apps.api.ribbon.ApplicationHierarchy;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.apps.api.ribbon.ModuleApplication;

final class InternalApplicationHierarchy {
   private ApplicationHierarchy _hierarchy;
   private HierarchyData _hierarchyData;

   InternalApplicationHierarchy(ApplicationHierarchy hierarchy, HierarchyData hierarchyData) {
      this._hierarchy = hierarchy;
      this._hierarchyData = hierarchyData;
      ApplicationFolder[] folders = this.getFolders();
      String[] deletedThemeFolders = hierarchyData.getDeletedThemeFolders();

      for (int i = folders.length - 1; i >= 0; i--) {
         String folderName = folders[i].getName();
         if (Arrays.contains(deletedThemeFolders, folderName)) {
            Arrays.removeAt(folders, i);
         }
      }

      String[] folderNames = hierarchyData.getFolders();

      for (int i = 0; i < folderNames.length; i++) {
         this.addFolder(folderNames[i]);
      }
   }

   final String getDefaultFolderName() {
      return this._hierarchy.getDefaultFolderName();
   }

   final boolean getDisableHotKeys() {
      return this._hierarchyData.getDisableHotKeys();
   }

   final void setDisableHotKeys(boolean disableHotKeys) {
      this._hierarchyData.setDisableHotKeys(disableHotKeys);
   }

   final ApplicationProperties getApplicationProperties(String name) {
      ApplicationProperties properties = this._hierarchyData.getApplicationProperties(name);
      if (properties != null) {
         ApplicationProperties themeProperties = this._hierarchy.getApplicationProperties(name);
         if (properties != null) {
            String bundleName = properties.getBundleName();
            int resourceId = properties.getResourceId();
            if (bundleName != null && resourceId != 0) {
               properties.setDescriptionResource(bundleName, resourceId);
            }
         }
      }

      if (properties == null) {
         String namePart = name;

         while (properties == null) {
            properties = this._hierarchy.getApplicationProperties(namePart);
            if (properties != null) {
               if (!namePart.equals(name)) {
                  properties.setDescriptionResource(null, 0);
               }

               properties = new InternalApplicationProperties(properties);
               this._hierarchyData.setApplicationProperties(name, properties);
               properties.clearDirtyFlag();
               return properties;
            }

            int index = namePart.lastIndexOf(46);
            if (index == -1) {
               return properties;
            }

            namePart = namePart.substring(0, index);
         }
      }

      return properties;
   }

   final ApplicationProperties createDefaultProperties(String name) {
      ApplicationProperties properties = new InternalApplicationProperties();
      properties.setFolderName(this._hierarchy.getDefaultFolderName());
      this._hierarchyData.setApplicationProperties(name, properties);
      return properties;
   }

   final ApplicationFolder[] getFolders() {
      return this._hierarchy.getFolders();
   }

   final ApplicationFolder addFolder(String name) {
      ApplicationFolder[] folders = this.getFolders();
      String folderName = null;
      if (name != null && name.length() != 0) {
         for (int i = 0; i < folders.length; i++) {
            folderName = folders[i].getName();
            if (name.equals(folderName)) {
               return null;
            }
         }

         this._hierarchyData.addFolder(name);
         ApplicationFolder newFolder = new ApplicationFolder(name, "not used", 0);
         this._hierarchy.addFolder(newFolder);
         return newFolder;
      } else {
         return null;
      }
   }

   final void deleteFolder(ApplicationFolder folder) {
      Arrays.remove(this.getFolders(), folder);
      this._hierarchyData.removeFolder(folder.getName());
   }

   public final boolean getAllowMoveIcons() {
      return this._hierarchy.getAllowMoveIcons();
   }

   public final boolean getAllowHideIcons() {
      return this._hierarchy.getAllowHideIcons();
   }

   public final ModuleApplication[] getModules() {
      return this._hierarchy.getModules();
   }

   final void clearDefaultProperties() {
      this._hierarchyData.clearDefaultProperties();
   }

   final void commit() {
      PersistentObject.commit(this._hierarchyData);
   }
}
