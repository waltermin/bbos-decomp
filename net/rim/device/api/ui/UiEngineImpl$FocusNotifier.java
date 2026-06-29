package net.rim.device.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.tid.itie.EventHandler;

class UiEngineImpl$FocusNotifier implements Runnable {
   private Screen _screenLost;
   private Screen _screenGained;
   private boolean _setFocusToIM;
   private Application _app;

   UiEngineImpl$FocusNotifier(Screen screenLost, Screen screenGained, Application app) {
      this._screenLost = screenLost;
      this._screenGained = screenGained;
      this._app = app;
      this._setFocusToIM = !(this._screenLost instanceof TextInputDialog) && !(this._screenGained instanceof TextInputDialog) && this._app != null;
   }

   public void setEnableFocusNotificationForIM(boolean enable) {
      this._setFocusToIM = enable;
   }

   @Override
   public void run() {
      if (this._screenLost != null) {
         this._screenLost.onFocusNotify(false);
         if (this._setFocusToIM) {
            EventHandler.getInstance()
               .focusLost(
                  this._screenLost instanceof TextInputDialog ? this._screenLost : this._screenLost.getLeafFieldWithFocus(),
                  (int)System.currentTimeMillis(),
                  this._app.getProcessId()
               );
         }
      }

      if (this._screenGained != null) {
         if (this._setFocusToIM) {
            EventHandler.getInstance().focusGained(this._screenGained.getLeafFieldWithFocus(), (int)System.currentTimeMillis(), this._app.getProcessId());
         }

         this._screenGained.onFocusNotify(true);
      }
   }
}
