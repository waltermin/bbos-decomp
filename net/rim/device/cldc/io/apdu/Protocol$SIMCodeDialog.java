package net.rim.device.cldc.io.apdu;

import net.rim.device.internal.ui.component.SimplePasswordDialog;

final class Protocol$SIMCodeDialog extends SimplePasswordDialog {
   public Protocol$SIMCodeDialog() {
      this(null);
   }

   public Protocol$SIMCodeDialog(String prompt) {
      super(prompt, 4, 8, true, 134217728);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
         case '#':
         case '*':
            return this.accept();
         case '\u001b':
            return this.cancel();
         default:
            return super.keyChar(key, status, time);
      }
   }
}
