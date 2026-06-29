package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.CodeModuleManager;

final class InstalledModulesOptionsItem$InstalledModuleListItem {
   private int _moduleHandle;
   private String _moduleName;

   public InstalledModulesOptionsItem$InstalledModuleListItem(int moduleHandle) {
      this._moduleHandle = moduleHandle;
      this._moduleName = CodeModuleManager.getModuleName(this._moduleHandle);
   }

   public final int getModuleHandle() {
      return this._moduleHandle;
   }

   public final String getModuleName() {
      return this._moduleName;
   }
}
