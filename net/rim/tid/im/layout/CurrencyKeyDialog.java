package net.rim.tid.im.layout;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TextInputDialog;

public final class CurrencyKeyDialog extends Dialog implements TextInputDialog {
   private static CurrencyKeyDialog _instance;

   public CurrencyKeyDialog(String message, Object[] choices, int defaultChoice) {
      super(message, choices, null, defaultChoice, null, 1);
      this.setEscapeEnabled(true);
   }

   public static final void closeDialog() {
      if (_instance != null) {
         _instance.close();
         _instance = null;
      }
   }

   public static final int ask(String message, Object[] choices, int defaultChoice) {
      _instance = new CurrencyKeyDialog(message, choices, defaultChoice);
      return _instance.doModal();
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      return Keypad.key(keycode) == 36 ? 65536 : super.processKeyEvent(event, key, keycode, time);
   }
}
