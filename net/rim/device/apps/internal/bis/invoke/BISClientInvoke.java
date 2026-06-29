package net.rim.device.apps.internal.bis.invoke;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.vm.Process;

public class BISClientInvoke {
   public static final String WIZARD_INVOKE_ARGUMENT;
   public static final String RIBBON_INVOKE_ARGUMENT;
   public static final String BIS_CLIENT_MODULE_NAME;
   public static final String BIS_LAUNCH_MODULE_NAME;
   private static ApplicationDescriptor _wizardDescriptor = (ApplicationDescriptor)(new Object(
      BISClientInvoke._baseDescriptor, "net_rim_bis_launch", new String[]{"WIZARD"}
   ));
   private static ApplicationDescriptor _ribbonDescriptor = (ApplicationDescriptor)(new Object(
      BISClientInvoke._baseDescriptor, "net_rim_bis_launch", new String[]{"RIBBON"}
   ));
   private static ApplicationDescriptor _baseDescriptor = CodeModuleManager.getApplicationDescriptors(CodeModuleManager.getModuleHandle("net_rim_bis_launch"))[0];

   public static boolean canBeInvoked() {
      return BISClientConfigRecord.getBISClientConfigRecord() != null;
   }

   public static String getBISClientVersion() {
      int handle = CodeModuleManager.getModuleHandle("net_rim_bis_client");
      return handle > 0 ? CodeModuleManager.getModuleVersion(handle) : "0";
   }

   public static String getBISLaunchVersion() {
      int handle = CodeModuleManager.getModuleHandle("net_rim_bis_launch");
      return CodeModuleManager.getModuleVersion(handle);
   }

   public static void destroyLaunchProcess(boolean ribbonLaunch) {
      if (ribbonLaunch) {
         destroyProcess(_ribbonDescriptor);
      } else {
         destroyProcess(_wizardDescriptor);
      }
   }

   public static void destroyProcess(ApplicationDescriptor descriptor) {
      int pid = ApplicationManager.getApplicationManager().getProcessId(descriptor);
      Process process = Process.getProcess(pid);
      if (process != null) {
         process.destroy();
      }
   }

   public static ApplicationDescriptor getWizardInvokeDescriptor() {
      return _wizardDescriptor;
   }

   public static ApplicationDescriptor getRibbonInvokeDescriptor() {
      return _ribbonDescriptor;
   }
}
