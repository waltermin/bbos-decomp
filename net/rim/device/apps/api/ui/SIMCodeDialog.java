package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.ui.component.SimplePasswordDialog;

public final class SIMCodeDialog extends SimplePasswordDialog {
   private int _dialogType;
   public static final int PIN_ENTRY;
   public static final int PUK_ENTRY_SIMPLE;
   public static final int PUK_ENTRY_COMPLEX;
   public static final int PIN_MINIMUM_LENGTH;
   public static final int PIN_MAXIMUM_LENGTH;
   public static final int PUK_MINIMUM_LENGTH;
   public static final int PUK_MAXIMUM_LENGTH;

   public SIMCodeDialog(int dialogType) {
      super(null, dialogType == 1 ? 4 : 8, dialogType == 1 ? 8 : (dialogType == 2 ? 8 : 32), true, 0);
      this._dialogType = dialogType;
   }

   @Override
   public final boolean cancel() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      char altKey = Keypad.getAltedChar(key);
      if (altKey > 255) {
         altKey = CharacterUtilities.foldFullWidth(altKey);
      }

      if (altKey == '*' || altKey == '#') {
         key = altKey;
      }

      switch (key) {
         case '\u001b':
            return this.cancel();
         case '*':
            if (this._dialogType != 3) {
               return this.accept();
            }
         case '\n':
         case '#':
            return this.accept();
         default:
            return super.keyChar(key, status, time);
      }
   }
}
