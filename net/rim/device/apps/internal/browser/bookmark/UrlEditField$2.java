package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class UrlEditField$2 extends MenuItem {
   private final UrlEditField this$0;

   UrlEditField$2(UrlEditField _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Clipboard.getClipboard().put(this.this$0.getText());
      Dialog.alert(BrowserResources.getString(519));
   }
}
