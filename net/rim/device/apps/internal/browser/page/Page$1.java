package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.bookmark.DialogAddRSSBookmark;

class Page$1 extends MenuItem {
   private final Page this$0;

   Page$1(Page _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      DialogAddRSSBookmark dialog = new DialogAddRSSBookmark(this.this$0._browserContent, this.this$0._rssLinks);
      dialog.show();
   }
}
