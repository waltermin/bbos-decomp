package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;

class ScreenClosingRunnable implements Runnable {
   Application _app;
   Screen _screenToClose;

   public ScreenClosingRunnable(Application app, Screen screenToClose) {
      this._app = app;
      this._screenToClose = screenToClose;
   }

   @Override
   public void run() {
      if (this._app.isEventThread()) {
         if (this._screenToClose.isDisplayed()) {
            this._screenToClose.close();
            return;
         }
      } else {
         this._app.invokeLater(this);
      }
   }
}
