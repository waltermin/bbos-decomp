package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.browser.push.PushProcessor;
import net.rim.device.api.system.Application;
import net.rim.device.apps.api.options.OptionsProviderRegistration;

public class PushLoader extends Application {
   public static Application _dispatchApplication;

   public static void libMain(String[] args) {
      new PushLoader();
      _dispatchApplication.enterEventDispatcher();
   }

   public PushLoader() {
      _dispatchApplication = this;
      PushProcessor.getInstance().start();
   }

   public static void registerOptions() {
      PushOptionsInitializer app = new PushOptionsInitializer();
      OptionsProviderRegistration.registerOptionsProvider(app);
      PushOptions.getOptions().enableSynchronization();
   }
}
