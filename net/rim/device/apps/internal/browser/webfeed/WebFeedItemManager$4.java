package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.ui.MenuItem;

final class WebFeedItemManager$4 extends MenuItem {
   private final WebFeedItemManager this$0;

   WebFeedItemManager$4(WebFeedItemManager _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.delete(this.this$0._descriptionContent.getDisplayableContent());
   }
}
