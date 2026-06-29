package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.ScrollChangeListener;

class BrowserScreen$ScreenManager$ScrollHelper implements ScrollChangeListener {
   private final BrowserScreen$ScreenManager this$0;

   BrowserScreen$ScreenManager$ScrollHelper(BrowserScreen$ScreenManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void scrollChanged(Manager manager, int newHorizontalScroll, int newVerticalScroll) {
      this.this$0._horizontalScrollbar.scrollChanged(manager, newHorizontalScroll, newVerticalScroll);
      this.this$0._verticalScrollbar.scrollChanged(manager, newHorizontalScroll, newVerticalScroll);
   }
}
