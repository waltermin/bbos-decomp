package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

final class ApplicationOptionsScreen extends KeywordFilteredScreen {
   private ApplicationList _appList;
   private boolean _showAll;
   private static final int PROPERTIES = 4;

   ApplicationOptionsScreen() {
      this(null, true);
   }

   ApplicationOptionsScreen(String title) {
      this(title, true);
   }

   ApplicationOptionsScreen(String title, boolean showRIMApps) {
      super(title, null, null, null, false, null);
      this.setHelp("third_party_program_control");
      this._showAll = showRIMApps;
      this._appList = new ApplicationList(showRIMApps, this);
      this.setList(this._appList.getKeywords());
      this.setListCallback(this._appList);
      this.setDisplayUpperCaseCharsInSearchText(false);
      this.setAllowSpacesInSearchText(false);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      boolean retVal = true;
      switch (key) {
         case '\n':
            this.showProperties();
            break;
         default:
            retVal = super.keyChar(key, status, time);
      }

      return retVal;
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.showProperties();
               return true;
         }
      }

      return handled;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      CodeModuleGroup selectedCMG = (CodeModuleGroup)this.getSelectedElement();
      Verb displayModulesVerb = new ApplicationOptionsScreen$DisplayModulesVerb(this);
      menu.add(displayModulesVerb);
      if (selectedCMG == null) {
         menu.setDefault(displayModulesVerb);
      } else {
         Verb defaultVerb = new ApplicationOptionsScreen$PropertiesVerb(this);
         menu.add(defaultVerb);
         if ((selectedCMG.getFlags() & 1) == 0) {
            menu.add(new DeleteModuleGroupVerb(selectedCMG));
         }

         Enumeration enumeration = selectedCMG.getModules();
         boolean rimApp = true;
         int[] handles = new int[0];

         while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            int handle = CodeModuleManager.getModuleHandle(name);
            if (handle != 0
               && (
                  !ApplicationControl.isSignedWithRRI(handle)
                     || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(handle).startsWith("net_rim")
               )) {
               rimApp = false;
               Array.resize(handles, handles.length + 1);
               handles[handles.length - 1] = handle;
            }
         }

         if (!rimApp && handles.length > 0) {
            ApplicationOptionsScreen$PermissionsVerb pv = new ApplicationOptionsScreen$PermissionsVerb(handles, selectedCMG.getFriendlyName());
            menu.add(pv);
            if (!this._showAll) {
               defaultVerb = pv;
            }
         }

         menu.setDefault(defaultVerb);
      }

      Verb defaultPermissionsVerb = new ApplicationOptionsScreen$PermissionsVerb();
      menu.add(defaultPermissionsVerb);
   }

   private final void showProperties() {
      CodeModuleGroup moduleGroup = (CodeModuleGroup)this.getSelectedElement();
      if (moduleGroup != null) {
         ModuleGroupInformation propPage = new ModuleGroupInformation(moduleGroup);
         propPage.open();
      }
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if ((verb instanceof DeleteModuleVerb || verb instanceof DeleteModuleGroupVerb) && result != null) {
         this._appList.refresh();
      }
   }

   @Override
   protected final Object getSelectedElement() {
      Object element = super.getSelectedElement();
      return element == null ? null : ((ApplicationList$ApplicationListItem)element)._group;
   }
}
