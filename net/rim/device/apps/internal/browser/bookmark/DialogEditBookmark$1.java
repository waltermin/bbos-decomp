package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

class DialogEditBookmark$1 extends MenuItem {
   private final DialogEditBookmark this$0;

   DialogEditBookmark$1(DialogEditBookmark _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (DialogAddBookmark.validateBookmark(this.this$0._editTitle.getText(), this.this$0._editURL.getText())) {
         UiApplication.getUiApplication().popScreen(this.this$0);
      }
   }
}
