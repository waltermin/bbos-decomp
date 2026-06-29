package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ribbon.launcher.OrganizeApplications;
import net.rim.device.apps.internal.ribbon.launcher.RibbonApiProxy;
import net.rim.device.apps.internal.ribbon.system.DateTimeFreshness;
import net.rim.device.apps.internal.ribbon.system.SystemActions;
import net.rim.device.apps.internal.ribbon.system.SystemManagementApp;
import net.rim.device.internal.system.ApplicationManagerInternal;

final class RibbonLauncherApp extends UiApplication {
   private RibbonLauncherImpl _ribbonBar;

   public static final void main(String[] args) {
      if (args.length != 0) {
         SystemActions.invoke(args);
      } else {
         if (!((ApplicationManagerInternal)ApplicationManager.getApplicationManager()).setConsoleProcess()) {
            throw new Object("Console already set");
         }

         RibbonLauncherApp app = new RibbonLauncherApp();
         SystemManagementApp.init();
         RibbonLauncherImpl ribbonBar = new RibbonLauncherImpl();
         app._ribbonBar = ribbonBar;
         new RibbonLauncherApp$RibbonLauncherProxy(ribbonBar);
         OrganizeApplications.register();
         new DateTimeFreshness();
         RibbonApiProxy.register();
         SystemActions.init();
         app.enableKeyUpEvents(true);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.replace(-863209076745204846L, Boolean.TRUE);
         app.enterEventDispatcher();
      }
   }

   private RibbonLauncherApp() {
   }

   @Override
   public final void activate() {
      this._ribbonBar.activate();
      super.activate();
   }

   @Override
   public final void deactivate() {
      this._ribbonBar.deactivate();
      super.deactivate();
   }
}
