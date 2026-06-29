package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

public final class QuickContactPromptDialog extends Dialog {
   private QuickContactPromptDialog(String message) {
      super(3, message, 4, null, 0);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
   }

   public static final boolean prompt(String msg) {
      return new QuickContactPromptDialog(msg).prompt();
   }

   public final boolean prompt() {
      return this.doModal() == 4;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return true;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21) {
         this.keyChar('\u001b', 0, time);
      }

      return super.keyDown(keycode, time);
   }
}
