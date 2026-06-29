package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;

final class StandbyManagerImpl$EnterStandbyDialog extends Dialog {
   private final StandbyManagerImpl this$0;

   StandbyManagerImpl$EnterStandbyDialog(StandbyManagerImpl _1, boolean enter) {
      super(enter ? _1._rb.getString(10200) : _1._rb.getString(10201), new Object[0], new int[0], 1, null, 0);
      this.this$0 = _1;
      _1._isEnteringStandby = true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 273) {
         this.this$0._muteKeyPressed = true;
         this.close();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }
}
