package net.rim.device.apps.internal.browser.img;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.MediaUtilities;

public final class SetAsHomescreenBackgroundMenuItem extends MenuItem {
   private String _filename;

   public SetAsHomescreenBackgroundMenuItem(String filename) {
      super(BrowserResources.getString(733), 565264, Integer.MAX_VALUE);
      this._filename = filename;
   }

   @Override
   public final void run() {
      MediaUtilities.setAsHomeScreenBackground(this._filename);
   }
}
