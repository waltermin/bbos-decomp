package net.rim.tid.im.spellcheck;

import net.rim.device.api.ui.component.Status;

class SpellCheckInputMethodVariant$1 implements Runnable {
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$1(SpellCheckInputMethodVariant _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Status.show("Input system error. The system is being restarted.");
   }
}
