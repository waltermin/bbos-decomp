package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.MenuItem;

final class AboutOptionsItem$CopyToClipBoardMenuItem extends MenuItem {
   private final AboutOptionsItem this$0;

   public AboutOptionsItem$CopyToClipBoardMenuItem(AboutOptionsItem _1) {
      super(ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common"), 1800, 196672, 0);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      AboutOptionsItem$AboutScreen abscreen = AboutOptionsItem._screens[this.this$0._currentScreenIndex];
      Clipboard.getClipboard().put(abscreen.getScreenCopyableText());
   }
}
