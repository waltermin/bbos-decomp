package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.ui.MenuItem;

final class WebFeedItemManager$8 extends MenuItem {
   private final WebFeedItemManager this$0;

   WebFeedItemManager$8(WebFeedItemManager _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0
         ._parent
         .getBrowserContent()
         .getRenderingApplication()
         .eventOccurred((Event)(new Object(this, this.this$0._item.getLink(), null, null, false, 0)));
   }
}
