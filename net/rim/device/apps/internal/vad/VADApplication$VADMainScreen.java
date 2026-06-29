package net.rim.device.apps.internal.vad;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.container.MainScreen;

final class VADApplication$VADMainScreen extends MainScreen {
   private Field _status;
   private final VADApplication this$0;

   VADApplication$VADMainScreen(VADApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void setStatus(Field status) {
      this._status = status;
      super.setStatus(status);
   }

   public final Field getStatus() {
      return this._status;
   }

   @Override
   public final boolean onMenu(int instance) {
      return false;
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      if (this.this$0._uiState != 29 && this.this$0._uiState != 33) {
         switch (Keypad.key(keycode)) {
            case 19:
            case 21:
               return 65536;
            default:
               return super.processKeyEvent(event, key, keycode, time);
         }
      } else {
         return 65536;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            return this.navigationClick(0, 0);
         case '\u001b':
         case '\u0086':
            this.this$0._manager.sendEvent(28);
            this.this$0.requestBackground();
            return true;
         default:
            if (this.this$0._uiState == 12) {
               this.this$0._manager.sendEvent(11);
               return true;
            } else if (this.this$0._listField == null) {
               return super.keyChar(key, status, time);
            } else {
               if (key < '1' || key > '9') {
                  key = Keypad.getAltedChar(key);
                  if (key < '1' || key > '9') {
                     return false;
                  }
               }

               int index = key - '0' - 1;
               if (index >= this.this$0._listField.getSize()) {
                  return false;
               } else {
                  this.this$0._listField.overrideSelectedIndex(index);
                  return this.navigationClick(0, 0);
               }
            }
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (this.this$0._uiState == 12) {
         this.this$0._manager.sendEvent(11);
         return true;
      } else if (this.this$0._listField != null) {
         int index = this.this$0._listField.getSelectedIndex();
         this.this$0._manager.sendEvent(1 + index);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1279348295:
            this.this$0._manager.nextLanguage();
            return true;
         case 1464422469:
            this.this$0._manager.reset(null);
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }
}
