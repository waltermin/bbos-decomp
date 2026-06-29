package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.MenuItem;

class UrlEditField$1 extends MenuItem {
   private final UrlEditField this$0;

   UrlEditField$1(UrlEditField _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.setText(this.this$0._initialValue);
   }
}
