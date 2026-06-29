package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class ToggleShuffleMenuItem extends MenuItem {
   private MediaBrowserField _field;

   public ToggleShuffleMenuItem(MediaBrowserField field) {
      super((field.isShuffled() ? "✓ " : "") + BrowserResources.getString(841), 590854, Integer.MAX_VALUE);
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.toggleShuffle();
   }
}
