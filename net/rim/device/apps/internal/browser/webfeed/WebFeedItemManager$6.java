package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class WebFeedItemManager$6 extends MenuItem {
   private final WebFeedItemManager this$0;

   WebFeedItemManager$6(WebFeedItemManager _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      WebFeedStatusStore.getInstance().setItemStatus(this.this$0._item, false);
      this.this$0._item.repaintItem();
   }
}
