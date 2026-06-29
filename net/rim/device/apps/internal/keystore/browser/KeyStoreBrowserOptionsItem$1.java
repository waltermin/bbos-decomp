package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.menu.MenuScreen;
import net.rim.device.apps.api.framework.verb.Verb;

class KeyStoreBrowserOptionsItem$1 implements Runnable {
   private final KeyStoreBrowserOptionsItem this$0;

   KeyStoreBrowserOptionsItem$1(KeyStoreBrowserOptionsItem _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Screen activeScreen = this.this$0._app.getActiveScreen();
      if (activeScreen instanceof MenuScreen) {
         this.this$0._app.popScreen(activeScreen);
         activeScreen = this.this$0._app.getActiveScreen();
      }

      if (activeScreen == KeyStoreBrowserOptionsItem.access$2400(this.this$0)) {
         Verb closeVerb = KeyStoreBrowserOptionsItem.access$2500(this.this$0);
         if (closeVerb != null) {
            synchronized (this.this$0._app.getAppEventLock()) {
               closeVerb.invoke(null);
               return;
            }
         }
      }
   }
}
