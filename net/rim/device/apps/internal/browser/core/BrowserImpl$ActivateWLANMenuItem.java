package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class BrowserImpl$ActivateWLANMenuItem extends MenuItem {
   public BrowserImpl$ActivateWLANMenuItem() {
      super(BrowserResources.getString(760), 1180240, 10000);
   }

   @Override
   public final void run() {
      RIMGlobalMessagePoster.postGlobalEvent(3212036545190435442L);
   }
}
