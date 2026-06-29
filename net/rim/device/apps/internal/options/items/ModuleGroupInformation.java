package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

public final class ModuleGroupInformation extends ModuleInformation {
   private CodeModuleGroup _moduleGroup;
   private int[] _handles;

   public ModuleGroupInformation(CodeModuleGroup cmg) {
      this._moduleGroup = cmg;
   }

   @Override
   public final void open() {
      this.createMainScreen(this._moduleGroup.getFriendlyName());
      UiApplication.getUiApplication().pushModalScreen(super._mainScreen);
   }

   @Override
   protected final void createModuleInformationItems() {
      String value = null;
      value = this._moduleGroup.getDescription();
      this.addProperty(705, value);
      value = this._moduleGroup.getVersion();
      this.addProperty(703, value);
      value = this._moduleGroup.getVendor();
      this.addProperty(704, value);
      value = this._moduleGroup.getCopyright();
      if (value != null && value.length() > 0) {
         this.addProperty(1466, value);
      }

      Enumeration moduleNames = this._moduleGroup.getModules();
      this.addLabelField(1469);
      this._handles = new int[0];

      while (moduleNames.hasMoreElements()) {
         String name = (String)moduleNames.nextElement();
         int handle = CodeModuleManager.getModuleHandle(name);
         if (handle != 0 && CodeStore.isEldestSiblingModule(handle)) {
            Array.resize(this._handles, this._handles.length + 1);
            this._handles[this._handles.length - 1] = handle;
            this.addIndentedField(name);
         }
      }

      boolean rimApp = true;

      for (int i = this._handles.length - 1; i >= 0; i--) {
         if (!ApplicationControl.isSignedWithRRI(this._handles[i])
            || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(this._handles[i]).startsWith("net_rim")) {
            rimApp = false;
            break;
         }
      }

      if (!rimApp && this._handles.length > 0 && Arrays.getIndex(this._handles, 0) == -1) {
         super._mainScreen.addMenuItem(new ModuleGroupInformation$ApplicationControlMenuItem(this._handles, this._moduleGroup.getFriendlyName()));
      }
   }
}
