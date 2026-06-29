package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class MediaBrowserField$PlaylistThread$1 implements Runnable {
   private final MediaBrowserField$PlaylistThread this$1;

   MediaBrowserField$PlaylistThread$1(MediaBrowserField$PlaylistThread _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      ((TextOverlayFieldManager)this.this$1.this$0._screenManager).setOverlayText(852, 16777215, 16711680);
      Dialog.alert(BrowserResources.getString(845));
   }
}
