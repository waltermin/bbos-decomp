package net.rim.wica.runtime.ui.internal.component;

import net.rim.wica.runtime.ui.internal.FocusManager;

class ScreenContext$2 implements Runnable {
   private final ScreenContext this$0;

   ScreenContext$2(ScreenContext this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      FocusManager manager = null;
      if (this.this$0.getLayout().getScreen() != null) {
         manager = (FocusManager)this.this$0.getLayout().getManager().getManager();
         manager.notifyUpdateStarted();
      }

      this.this$0.setSuspendLayout(true);
      this.this$0._layout.update(0);
      this.this$0.setSuspendLayout(false);
      this.this$0.setTitle(this.this$0.getTitle());
      if (this.this$0.getLayout().getScreen() != null) {
         manager.notifyUpdateCompleted();
      }

      if (this.this$0._menu.isDisplayed()) {
         this.this$0.closeMenu();
         int instance = 0;
         instance = this.this$0._menu.getInstance();
         this.this$0._uiService.getRuntime().requestMenuShow(instance);
      }
   }
}
