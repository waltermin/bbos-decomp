package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.ui.container.MainScreen;

class CacheState$MyMainScreen extends MainScreen {
   private final CacheState this$0;

   CacheState$MyMainScreen(CacheState _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean navigationClick(int status, int time) {
      this.this$0.handleSelection();
      return true;
   }
}
