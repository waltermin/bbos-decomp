package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ActiveRichTextField;

final class PushActiveRichTextField extends ActiveRichTextField {
   private PushDisplayDialog _dialog;

   public PushActiveRichTextField(String message, PushDisplayDialog dialog) {
      super(message);
      this._dialog = dialog;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 10) {
         this._dialog.loadPage(this.getCookieWithFocus());
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this._dialog.loadPage(this.getCookieWithFocus());
      return true;
   }
}
