package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class ContextState implements DebugListItem {
   @Override
   public final String getLabel() {
      return BrowserResources.getString(369);
   }

   @Override
   public final Screen getScreen() {
      MainScreen screen = (MainScreen)(new Object(1196268651020288L));
      screen.setTitle((Field)(new Object(BrowserResources.getString(369))));
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      Page page = browser.getCurrentPage();
      if (page != null) {
         IBrowserContext context = page.getContext();
         if (context != null) {
            RichTextField field = (RichTextField)(new Object(context.toString()));
            screen.add(field);
         }
      }

      return screen;
   }
}
