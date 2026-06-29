package net.rim.device.apps.internal.globalsearch;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

public final class GlobalSearchApplication extends UiApplication {
   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         RibbonLauncher.getInstance().unregisterAction("SearchMessages");
      } else if (args != null && args.length == 3 && args[0].equals("invokeapi")) {
         GlobalSearchApplication app = new GlobalSearchApplication();
         app.pushScreen(new GlobalSearchScreen(args[1], args[2]));
         app.enterEventDispatcher();
      } else {
         GlobalSearchApplication app = new GlobalSearchApplication();
         app.pushScreen(new GlobalSearchScreen());
         app.enterEventDispatcher();
      }
   }

   private GlobalSearchApplication() {
   }
}
