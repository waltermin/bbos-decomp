package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.internal.proxy.Proxy;

final class MemoryCleanerApp extends UiApplication {
   private static ApplicationDescriptor _applicationDescriptor;

   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         MemoryCleanerProvider provider = new MemoryCleanerProvider();
         OptionsProviderRegistration.registerOptionsProvider(provider);
         MemoryCleanerManager manager = MemoryCleanerManager.getInstance();
         manager.registerWithSyncManager();
         _applicationDescriptor = (ApplicationDescriptor)(new Object(ApplicationDescriptor.currentApplicationDescriptor(), new Object[0]));
         Proxy.getInstance().addGlobalEventListener(new MemoryCleanerApp$MemoryCleanerEventListener());
      } else {
         MemoryCleanerApp app = new MemoryCleanerApp();
         app.invokeLater(new MemoryCleanerApp$StartMemoryCleaner());
         app.enterEventDispatcher();
      }
   }

   private static final ApplicationDescriptor getApplicationDescriptor() {
      if (_applicationDescriptor != null) {
         return _applicationDescriptor;
      }

      int handle = CodeModuleManager.getModuleHandle("net_rim_bb_mc_app");
      if (handle != -1) {
         ApplicationDescriptor[] appDescriptors = CodeModuleManager.getApplicationDescriptors(handle);

         for (int i = 0; i < appDescriptors.length; i++) {
            if (appDescriptors[i].getNameResourceBundle() != null) {
               return appDescriptors[i];
            }
         }
      }

      return null;
   }

   @Override
   protected final boolean acceptsForeground() {
      return false;
   }
}
