package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.component.Menu;

class ScreenContext$1 implements Runnable {
   private final ScreenContext this$0;

   ScreenContext$1(ScreenContext this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      if (!this.this$0._menu.isDisplayed()) {
         synchronized (this.this$0._menu) {
            Menu.setTargetScreen(this.this$0.getLayout().getScreen());
            this.this$0._menu.show();
            ContextMenu.getInstance().setTarget(null);
            Menu.setTargetScreen(null);
         }
      }
   }
}
