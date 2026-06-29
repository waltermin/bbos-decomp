package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Application;

final class TextFlowManager$ViewModeRunnable implements Runnable {
   private TextFlowManager _manager;
   private int _invokeLaterId = -1;
   private static TextFlowManager$ViewModeRunnable _instance = new TextFlowManager$ViewModeRunnable();
   private static final int VIEW_MODE_TOGGLE_DELAY;

   final boolean cancel() {
      if (this._invokeLaterId != -1) {
         Application.getApplication().cancelInvokeLater(this._invokeLaterId);
         this._invokeLaterId = -1;
         return true;
      } else {
         return false;
      }
   }

   final void kick(TextFlowManager manager) {
      Application app = Application.getApplication();
      if (this._invokeLaterId != -1) {
         app.cancelInvokeLater(this._invokeLaterId);
      }

      this._manager = manager;
      this._invokeLaterId = app.invokeLater(this, 500, false);
   }

   @Override
   public final void run() {
      this._invokeLaterId = -1;
      if (this._manager != null) {
         this._manager.toggleViewMode();
      }
   }

   private TextFlowManager$ViewModeRunnable() {
   }

   static final TextFlowManager$ViewModeRunnable getInstance() {
      return _instance;
   }
}
