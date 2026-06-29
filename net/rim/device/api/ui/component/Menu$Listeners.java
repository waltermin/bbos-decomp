package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;

class Menu$Listeners implements ScreenUiEngineAttachedListener {
   private final Menu this$0;

   Menu$Listeners(Menu _1) {
      this.this$0 = _1;
   }

   @Override
   public void onScreenUiEngineAttached(Screen screen, boolean attached) {
      if (!attached) {
         this.this$0.close();
         screen.removeScreenUiEngineAttachedListener(this);
      }
   }
}
