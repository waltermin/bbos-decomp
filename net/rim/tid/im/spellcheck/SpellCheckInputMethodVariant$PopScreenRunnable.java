package net.rim.tid.im.spellcheck;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.PopupScreen;

class SpellCheckInputMethodVariant$PopScreenRunnable implements Runnable {
   PopupScreen _popup;

   SpellCheckInputMethodVariant$PopScreenRunnable(PopupScreen aPopup) {
      this._popup = aPopup;
   }

   @Override
   public void run() {
      Ui.getUiEngine().popScreen(this._popup);
   }
}
