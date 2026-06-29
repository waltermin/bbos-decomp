package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class SkipTrackMenuItem extends MenuItem {
   private boolean _forward;
   private MediaBrowserField _field;

   public SkipTrackMenuItem(boolean forward, MediaBrowserField field) {
      super(forward ? BrowserResources.getString(839) : BrowserResources.getString(840), forward ? 590851 : 590850, Integer.MAX_VALUE);
      this._forward = forward;
      this._field = field;
   }

   @Override
   public final void run() {
      this._field.skipTrack(this._forward);
   }
}
