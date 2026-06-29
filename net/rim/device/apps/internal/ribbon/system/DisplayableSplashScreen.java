package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.FullScreen;

final class DisplayableSplashScreen extends FullScreen {
   private boolean _isDisplayed;
   private UiApplication _app;
   private Runnable _runnable;
   private int _timeout;

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         synchronized (this) {
            this._isDisplayed = true;
            if (this._app != null) {
               if (this._app.invokeLater(this._runnable, this._timeout, false) == -1) {
                  this._runnable.run();
               }

               this._app = null;
               this._runnable = null;
               this._timeout = 0;
            }
         }
      } else {
         synchronized (this) {
            this._isDisplayed = false;
            this._app = null;
            this._runnable = null;
            this._timeout = 0;
         }
      }

      super.onUiEngineAttached(attached);
   }

   final synchronized void setTimer(UiApplication app, Runnable runnable, int timeout) {
      if (this._isDisplayed) {
         if (app.invokeLater(runnable, timeout, false) == -1) {
            runnable.run();
            return;
         }
      } else {
         this._app = app;
         this._runnable = runnable;
         this._timeout = timeout;
      }
   }
}
