package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;

public class RIMProcessLauncherMain {
   public static void libMain(String[] args) {
      if (args.length > 0) {
         RIMProcessLauncher$Data data = (RIMProcessLauncher$Data)ApplicationRegistry.getApplicationRegistry().get(-7293675776592666707L);
         int flags;
         Runnable runnable;
         RIMProcessLauncher$ApplicationCallback callback;
         synchronized (data.getDescriptor()) {
            flags = data.getFlags();
            runnable = data.getRunnable();
            callback = data.getApplicationCallback();
            data.setRunnable(null);
            data.getDescriptor().notify();
         }

         if ((flags & 1) != 0) {
            Application app;
            if ((flags & 3) == 3) {
               app = new RIMProcessLauncherMain$UiApplicationImpl(runnable);
            } else {
               app = new RIMProcessLauncherMain$ApplicationImpl(runnable);
               if (callback != null) {
                  callback.applicationStarted(app);
               }
            }

            app.enterEventDispatcher();
            return;
         }

         runnable.run();
      }
   }

   private RIMProcessLauncherMain() {
   }

   public static void initialize() {
      Object obj = new RIMProcessLauncherMain();
      int handle = CodeModuleManager.getModuleHandleForClass(obj.getClass());
      if (handle > -1) {
         ApplicationDescriptor[] appDescriptors = CodeModuleManager.getApplicationDescriptors(handle);
         if (appDescriptors != null && appDescriptors.length > 0) {
            RIMProcessLauncher$Data data = (RIMProcessLauncher$Data)(new Object());
            data.setDescriptor(appDescriptors[0]);
            ApplicationRegistry.getApplicationRegistry().put(-7293675776592666707L, data);
         }
      }
   }
}
