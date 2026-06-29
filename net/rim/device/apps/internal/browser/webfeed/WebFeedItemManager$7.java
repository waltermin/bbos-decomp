package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.ui.MenuItem;

final class WebFeedItemManager$7 extends MenuItem {
   private final WebFeedItemManager this$0;

   WebFeedItemManager$7(WebFeedItemManager _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0.getFieldCount() == 1) {
         this.this$0.showDescriptionContent();
      } else {
         if (this.this$0._descriptionContent != null) {
            this.this$0.delete(this.this$0._descriptionContent.getDisplayableContent());
         }
      }
   }
}
