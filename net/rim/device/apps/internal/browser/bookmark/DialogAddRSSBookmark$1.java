package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

class DialogAddRSSBookmark$1 extends MenuItem {
   private final DialogAddRSSBookmark this$0;

   DialogAddRSSBookmark$1(DialogAddRSSBookmark _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      DialogAddRSSBookmark.access$000(this.this$0);
      UiApplication.getUiApplication().popScreen(this.this$0);
   }
}
