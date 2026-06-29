package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class TogglePlaylistViewMenuItem extends MenuItem {
   private MediaBrowserField _field;

   public TogglePlaylistViewMenuItem(MediaBrowserField field) {
      super(BrowserResources.getString(field.isPlaylistShown() ? (field.isAudio() ? 847 : 861) : 846), 590856, Integer.MAX_VALUE);
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.togglePlaylist();
   }
}
