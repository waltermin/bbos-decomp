package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.browser.history.LongTermHistoryScreen;

class BookmarksScreen$1 extends MenuItem {
   private final BookmarksScreen this$0;

   BookmarksScreen$1(BookmarksScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         UiApplication.getUiApplication().pushScreen(new LongTermHistoryScreen(this.this$0));
      }
   }
}
