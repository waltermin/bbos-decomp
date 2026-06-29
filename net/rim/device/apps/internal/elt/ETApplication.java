package net.rim.device.apps.internal.elt;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.location.LocationServicesOptionsProvider;

public final class ETApplication extends Application {
   private ETManager _etManager = null;
   static final long UID = 7659638648801846908L;
   static Class class$net$rim$device$apps$internal$elt$ETApplication;

   private ETApplication() {
      ETCollection.registerOnStartup();
      ITPolicyListener.registerOnStartup();
      this._etManager = new ETManager(this);
      LocationServicesOptionsProvider.register(new LocationServicesOptionsProviderImpl());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final ETManager getETManager() {
      ETApplication app = (ETApplication)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(7659638648801846908L);
      if (app == null) {
         int module = CodeModuleManager.getModuleHandleForClass(
            class$net$rim$device$apps$internal$elt$ETApplication == null
               ? (class$net$rim$device$apps$internal$elt$ETApplication = class$("net.rim.device.apps.internal.elt.ETApplication"))
               : class$net$rim$device$apps$internal$elt$ETApplication
         );
         ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(module);

         label31:
         try {
            ApplicationManager.getApplicationManager().runApplication(descriptors[0]);
         } catch (Throwable var5) {
            Logger.logError("getETManager", ((StringBuffer)(new Object("ApplicationManagerException: "))).append(e).toString());
            break label31;
         }

         app = (ETApplication)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(7659638648801846908L);
      }

      return app._etManager;
   }

   public static final void main(String[] args) {
      Logger.register();
      ApplicationRegistry.getApplicationRegistry().replace(7659638648801846908L, new ETApplication());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
