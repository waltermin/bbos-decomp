package net.rim.device.apps.internal.bis.launch.invoke;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;

public final class BISClientInvoke {
   public static final String WIZARD_INVOKE_ARGUMENT;
   public static final String RIBBON_INVOKE_ARGUMENT;
   public static final String BIS_LAUNCH_MODULE_NAME;
   private static ApplicationDescriptor _wizardDescriptor;
   private static ApplicationDescriptor _ribbonDescriptor;

   static {
      ApplicationDescriptor baseDescriptor = CodeModuleManager.getApplicationDescriptors(CodeModuleManager.getModuleHandle("net_rim_bis_launch"))[0];
      _wizardDescriptor = (ApplicationDescriptor)(new Object(baseDescriptor, "net_rim_bis_launch", new String[]{"WIZARD"}));
      _ribbonDescriptor = (ApplicationDescriptor)(new Object(baseDescriptor, "net_rim_bis_launch", new String[]{"RIBBON"}));
   }
}
