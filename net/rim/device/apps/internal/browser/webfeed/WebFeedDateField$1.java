package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class WebFeedDateField$1 extends MenuItem {
   private final WebFeedDateField this$0;

   WebFeedDateField$1(WebFeedDateField _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      WebFeedField field = (WebFeedField)this.this$0.getManager();
      field.markPriorOpened(this.this$0.getIndex());
      field.invalidate();
   }
}
