package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.PopupScreen;

class CertificateHarvesterCompletionDialog$ScreenPopper implements Runnable {
   PopupScreen _screen;

   public CertificateHarvesterCompletionDialog$ScreenPopper(PopupScreen screen) {
      this._screen = screen;
   }

   @Override
   public void run() {
      Ui.getUiEngine().popScreen(this._screen);
   }
}
