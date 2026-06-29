package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.ProgressIndicator;
import net.rim.device.apps.api.ui.ProgressRunnable;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;

final class ApplicationList$ApplicationListLoader implements ProgressRunnable {
   ApplicationList _appList;
   ProgressIndicator _indicator;
   ApplicationList$ApplicationListItem[] _groupListItems;

   ApplicationList$ApplicationListLoader(ApplicationList appList) {
      this._appList = appList;
   }

   @Override
   public final void setProgressIndicator(ProgressIndicator progressIndicator) {
      this._indicator = progressIndicator;
   }

   @Override
   public final void stop() {
   }

   @Override
   public final void run() {
      synchronized (ApplicationList._loadLock) {
         if (ApplicationList._isProcessing) {
            return;
         }

         ApplicationList._isProcessing = true;
      }

      this.buildAppList();
      this._appList.setList(this._groupListItems);
      synchronized (ApplicationList._loadLock) {
         ApplicationList._isProcessing = false;
      }

      this._appList.listUpdated();
   }

   private final void buildAppList() {
      CodeModuleGroup[] allGroups = CodeModuleGroupManager.loadAll();
      if (allGroups == null) {
         this._groupListItems = new ApplicationList$ApplicationListItem[0];
         this._appList.getIndentTable().clear();
      } else {
         int totalGroups = allGroups.length;
         int curr = 0;
         if (this._indicator != null) {
            this._indicator.initialize(OptionsResources.getString(2032), null, 0, totalGroups, 0);
         }

         this._groupListItems = new ApplicationList$ApplicationListItem[0];
         this._appList.getIndentTable().clear();

         for (int i = 0; i < totalGroups; i++) {
            CodeModuleGroup group = allGroups[i];
            Enumeration modules = group.getModules();
            boolean rimApp = true;
            boolean usesDefaults = true;

            while (modules.hasMoreElements()) {
               String name = (String)modules.nextElement();
               int handle = CodeModuleManager.getModuleHandle(name);
               if (!this._appList.isShowAll()
                  && rimApp
                  && (
                     !ApplicationControl.isSignedWithRRI(handle)
                        || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(handle).startsWith("net_rim")
                  )) {
                  rimApp = false;
               }

               if (usesDefaults && ApplicationControl.differsFromUserDefaults(handle)) {
                  usesDefaults = false;
               }
            }

            if (this._appList.isShowAll() || !rimApp) {
               int flags = group.getFlags();
               if ((flags & 2) == 0 && (flags & 4) == 0) {
                  Arrays.add(this._groupListItems, new ApplicationList$ApplicationListItem(group, usesDefaults));
                  allGroups[i] = null;
               }
            }

            if (this._indicator != null) {
               if (++curr <= totalGroups) {
                  this._indicator.setProgress(curr);
               }
            }
         }

         Arrays.sort(this._groupListItems, new ApplicationListItemComparator());
         if (this._indicator != null) {
            this._indicator.setStatusString(OptionsResources.getString(2036), false);
         }

         for (int i = 0; i < this._groupListItems.length; i++) {
            i = this.addDependencies(allGroups, i, 1);
         }

         if (this._indicator != null) {
            this._indicator.dismiss();
         }
      }
   }

   private final int addDependencies(CodeModuleGroup[] allGroups, int index, int indentLevel) {
      int totalGroups = allGroups.length;
      String name = this._groupListItems[index]._group.getName();

      for (int j = 0; j < totalGroups; j++) {
         if (allGroups[j] != null) {
            CodeModuleGroup currGroup = allGroups[j];
            if (currGroup.containsDependency(name)) {
               int flags = currGroup.getFlags();
               if ((flags & 4) != 0 && (flags & 2) == 0) {
                  index++;
                  ApplicationList$ApplicationListItem dependency = new ApplicationList$ApplicationListItem(
                     currGroup, !ApplicationControl.differsFromUserDefaults(currGroup.getHandle())
                  );
                  Arrays.insertAt(this._groupListItems, dependency, index);
                  this._appList.getIndentTable().put(dependency, new Object(indentLevel));
                  CodeModuleGroup var11 = null;
                  index = this.addDependencies(allGroups, index, indentLevel + 1);
               }
            }
         }
      }

      return index;
   }
}
