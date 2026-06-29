package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class AbstractFilterScreen$KeyDownListener implements KeyListener {
   private final AbstractFilterScreen this$0;

   private AbstractFilterScreen$KeyDownListener(AbstractFilterScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      boolean hasFocus = this.this$0._containsEdit.isFocus();
      if (hasFocus && Keypad.key(keycode) == 10) {
         this.this$0.addContainsContentToContainsArea();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   AbstractFilterScreen$KeyDownListener(AbstractFilterScreen x0, AbstractFilterScreen$1 x1) {
      this(x0);
   }
}
