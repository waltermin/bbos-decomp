package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.browser.OTAStatusReportService;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.rms.RecordStoreManagerProxy;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.MIDletApplication;
import net.rim.vm.Array;

final class DeleteModuleGroupVerb extends Verb {
   private CodeModuleGroup _cmg;

   DeleteModuleGroupVerb(CodeModuleGroup cmg) {
      super(598352, CommonResource.getBundle(), 17);
      this._cmg = cmg;
   }

   @Override
   public final Object invoke(Object parameter) {
      CodeModuleGroup[] allGroups = CodeModuleGroupManager.loadAll();
      if (allGroups == null) {
         allGroups = new Object[0];
      }

      Vector groupsToDelete = (Vector)(new Object());
      int[] handles = getModuleHandles(this._cmg, allGroups);
      groupsToDelete.addElement(new GroupHolder(this._cmg, handles));
      getDependencies(groupsToDelete, this._cmg.getName(), allGroups);
      int numGroupsToDelete = groupsToDelete.size();

      for (int i = 0; i < numGroupsToDelete; i++) {
         GroupHolder gh = (GroupHolder)groupsToDelete.elementAt(i);
         CodeModuleGroup cmg = gh._cmg;
         int[] moduleHandles = gh._moduleHandles;
         if ((cmg.getFlags() & 1) != 0) {
            Object[] values = new Object[]{this._cmg.getFriendlyName(), cmg.getFriendlyName()};
            Dialog.alert(MessageFormat.format(OptionsResources.getString(1914), values));
            return null;
         }

         for (int j = moduleHandles.length - 1; j >= 0; j--) {
            if (ApplicationControl.isRequiredApp(moduleHandles[j])) {
               Dialog.alert(MessageFormat.format(OptionsResources.getString(1490), new Object[]{this._cmg.getFriendlyName()}));
               return null;
            }
         }
      }

      boolean rebootRequired = false;

      for (int i = 0; i < numGroupsToDelete; i++) {
         GroupHolder gh = (GroupHolder)groupsToDelete.elementAt(i);
         CodeModuleGroup cmg = gh._cmg;
         int[] moduleHandles = gh._moduleHandles;
         boolean isMidlet = moduleHandles.length == 1 && CodeModuleManager.isMidlet(moduleHandles[0]);
         String prompt = null;
         if (!isMidlet) {
            if (i == 0) {
               prompt = CommonResource.format(10025, cmg.getFriendlyName());
            }
         } else {
            String moduleName = CodeModuleManager.getModuleName(moduleHandles[0]);
            String confirm = MIDletApplication.getAppProperty(moduleName, "MIDlet-Delete-Confirm", false);
            ApplicationDescriptor[] ads = CodeModuleManager.getApplicationDescriptors(moduleHandles[0]);
            StringBuffer sb = (StringBuffer)(new Object(OptionsResources.getString(1923)));
            if (confirm != null) {
               sb.append(' ');
               sb.append(confirm);
            }

            for (int j = 0; j < ads.length; j++) {
               if (j == 0) {
                  sb.append('\n');
               }

               sb.append('\n');
               sb.append(ads[j].getName());
            }

            prompt = sb.toString();
         }

         if (prompt != null && Dialog.ask(2, prompt, -1) != 3) {
            return null;
         }

         String groupName = cmg.getName();

         label166:
         try {
            RecordStore.openRecordStore("Fake_RecordStore_Name", false);
         } finally {
            break label166;
         }

         if (isMidlet) {
            int attributeDividerIndex = groupName.indexOf(58);
            if (attributeDividerIndex != -1) {
               String midletName = groupName.substring(0, attributeDividerIndex);
               String midletVendor = groupName.substring(attributeDividerIndex + 1, groupName.length());
               ((RecordStoreManagerProxy)ApplicationRegistry.getApplicationRegistry()
                  .waitFor(6635119920104263588L))
                  .deleteRecordStores(midletName, midletVendor);
            }
         } else {
            ((RecordStoreManagerProxy)ApplicationRegistry.getApplicationRegistry().waitFor(6635119920104263588L)).deleteRecordStores(groupName, null);
         }

         cmg.delete();
         boolean rc = this.deleteModules(moduleHandles);
         if (rc) {
            rebootRequired = true;
         }
      }

      this.sendOTAReport(this._cmg.getName());
      if (rebootRequired) {
         String prompt = OptionsResources.getString(1486);
         if (Dialog.ask(3, prompt, -1) == 4) {
            InternalServices.initiateReset("DeleteGroup");
         }
      }

      return new Object();
   }

   private static final void getDependencies(Vector v, String groupName, CodeModuleGroup[] allGroups) {
      int numGroups = allGroups.length;

      for (int i = 0; i < numGroups; i++) {
         if (allGroups[i].containsDependency(groupName)) {
            int[] handles = getModuleHandles(allGroups[i], allGroups);
            GroupHolder gh = new GroupHolder(allGroups[i], handles);
            if (!v.contains(gh)) {
               v.addElement(gh);
               getDependencies(v, allGroups[i].getName(), allGroups);
            }
         }
      }
   }

   private static final int[] getModuleHandles(CodeModuleGroup cmg, CodeModuleGroup[] allGroups) {
      int[] handles = new int[0];
      int numGroups = allGroups.length;
      Enumeration e = cmg.getModules();
      int i = 0;

      label29:
      while (e.hasMoreElements()) {
         String moduleName = (String)e.nextElement();

         for (int j = 0; j < numGroups; j++) {
            if (!allGroups[j].equals(cmg) && allGroups[j].containsModule(moduleName)) {
               continue label29;
            }
         }

         int handle = CodeModuleManager.getModuleHandle(moduleName);
         if (handle != 0) {
            Array.resize(handles, i + 1);
            handles[i] = handle;
            i++;
         }
      }

      return handles;
   }

   private final boolean deleteModules(int[] handles) {
      boolean rebootRequired = false;

      for (int i = 0; i < handles.length; i++) {
         try {
            if (CodeStore.isEldestSiblingModule(handles[i])) {
               switch (CodeModuleManager.deleteModuleEx(handles[i], true)) {
                  case 6:
                     rebootRequired = true;
               }
            }
         } finally {
            continue;
         }
      }

      return rebootRequired;
   }

   private final void sendOTAReport(String moduleGroupName) {
      OTAStatusReportService ota = BrowserServices.getOTAStatusReportService();
      if (ota != null) {
         ota.appDeleted(moduleGroupName);
      }
   }
}
