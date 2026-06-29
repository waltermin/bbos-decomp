package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;

public final class InstalledModulesOptionsItem extends KeywordFilteredScreen {
   private boolean _showAll;
   private InstalledModulesOptionsItem$InstalledModulesList _moduleList;

   public InstalledModulesOptionsItem() {
      this(true);
   }

   public InstalledModulesOptionsItem(boolean showRIMModules) {
      super(OptionsResources.getString(showRIMModules ? 700 : 1965), null, null, null, false, null);
      this._moduleList = new InstalledModulesOptionsItem$InstalledModulesList(showRIMModules, this);
      this.setList(this._moduleList.getKeywords());
      this.setListCallback(this._moduleList);
      this.setDisplayUpperCaseCharsInSearchText(false);
      this.setAllowSpacesInSearchText(false);
      this._showAll = showRIMModules;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         return this.showModuleInfo();
      } else if (key == 27) {
         Verb exitVerb = ExitVerb.createCloseVerb(0, null);
         exitVerb.invoke(null);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.showModuleInfo();
               return true;
         }
      }

      return handled;
   }

   private final boolean showModuleInfo() {
      Object selectedElement = this.getSelectedElement();
      if (!(selectedElement instanceof InstalledModulesOptionsItem$InstalledModuleListItem)) {
         return false;
      }

      int handle = ((InstalledModulesOptionsItem$InstalledModuleListItem)selectedElement).getModuleHandle();
      Verb moduleInformationVerb = new InstalledModulesOptionsItem$ModuleInformationVerb(handle);
      moduleInformationVerb.invoke(null);
      return true;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      Verb defaultVerb = null;
      Object selectedElement = this.getSelectedElement();
      if (selectedElement instanceof InstalledModulesOptionsItem$InstalledModuleListItem) {
         int handle = ((InstalledModulesOptionsItem$InstalledModuleListItem)selectedElement).getModuleHandle();
         defaultVerb = new InstalledModulesOptionsItem$ModuleInformationVerb(handle);
         menu.add(defaultVerb);
         boolean isOTA = (CodeModuleManager.getModuleFlags(handle) & 2) != 0;
         if (!InternalServices.isDeviceSecure() || isOTA) {
            menu.add(new DeleteModuleVerb(handle, CodeModuleManager.getModuleName(handle)));
         }

         if (!ApplicationControl.isSignedWithRRI(handle)
            || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(handle).startsWith("net_rim")) {
            InstalledModulesOptionsItem$PermissionsVerb pv = new InstalledModulesOptionsItem$PermissionsVerb(handle);
            menu.add(pv);
            if (!this._showAll) {
               defaultVerb = pv;
            }
         }
      }

      menu.add(new InstalledModulesOptionsItem$PermissionsVerb(0));
      menu.setDefault(defaultVerb);
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if (verb instanceof DeleteModuleVerb && result != null) {
         this._moduleList.refresh();
      }
   }
}
