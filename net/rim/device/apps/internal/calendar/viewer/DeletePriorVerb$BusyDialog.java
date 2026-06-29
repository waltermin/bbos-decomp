package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

final class DeletePriorVerb$BusyDialog extends Dialog {
   private UiApplication _app;
   private boolean _isVisible;
   private static ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");

   public DeletePriorVerb$BusyDialog(UiApplication app) {
      super(_rb.getString(99), null, null, 0, null);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_hourglass"));
      this._app = app;
      this.setEscapeEnabled(false);
   }

   @Override
   public final void show() {
      synchronized (Application.getEventLock()) {
         if (!this._isVisible) {
            this._app.pushScreen(this);
            this._isVisible = true;
         }
      }
   }

   public final void hide() {
      synchronized (Application.getEventLock()) {
         if (this._isVisible) {
            this._app.popScreen(this);
            this._isVisible = false;
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._app.requestBackground();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
