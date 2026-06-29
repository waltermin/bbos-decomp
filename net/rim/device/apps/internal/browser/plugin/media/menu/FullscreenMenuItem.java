package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class FullscreenMenuItem extends MenuItem {
   private MediaBrowserField _field;

   public FullscreenMenuItem(MediaBrowserField field, boolean fullscreen) {
      super((fullscreen ? "✓ " : "") + BrowserResources.getString(890), 590855, Integer.MAX_VALUE);
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.externalToggleFullscreen();
   }
}
