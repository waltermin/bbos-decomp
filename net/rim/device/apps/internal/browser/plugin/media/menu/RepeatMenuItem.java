package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class RepeatMenuItem extends MenuItem {
   private MediaBrowserField _field;

   public RepeatMenuItem(MediaBrowserField field, boolean repeat) {
      super((repeat ? "✓ " : "") + BrowserResources.getString(842), 590853, Integer.MAX_VALUE);
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.toggleRepeat();
   }
}
