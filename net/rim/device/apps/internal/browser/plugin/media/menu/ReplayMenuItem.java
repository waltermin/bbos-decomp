package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class ReplayMenuItem extends MenuItem {
   private MediaBrowserField _field;

   public ReplayMenuItem(MediaBrowserField field) {
      super(BrowserResources.getString(850), 590852, Integer.MAX_VALUE);
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.replay();
   }
}
