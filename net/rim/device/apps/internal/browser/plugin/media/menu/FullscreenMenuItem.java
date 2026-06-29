package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class FullscreenMenuItem extends MenuItem {
   private MediaBrowserField _field;

   public FullscreenMenuItem(MediaBrowserField field, boolean fullscreen) {
      super(((StringBuffer)(new Object())).append(fullscreen ? "✓ " : "").append(BrowserResources.getString(890)).toString(), 590855, Integer.MAX_VALUE);
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.externalToggleFullscreen();
   }
}
