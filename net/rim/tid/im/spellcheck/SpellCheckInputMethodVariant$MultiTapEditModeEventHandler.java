package net.rim.tid.im.spellcheck;

import net.rim.device.api.system.Application;
import net.rim.tid.awt.event.KeyEvent;

class SpellCheckInputMethodVariant$MultiTapEditModeEventHandler extends SpellCheckInputMethodVariant$EditModeEventHandler {
   private final SpellCheckInputMethodVariant this$0;
   private static final long MULTITAP_SPELLCHECK_DELAY;

   SpellCheckInputMethodVariant$MultiTapEditModeEventHandler(SpellCheckInputMethodVariant _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected void handleInputKeyEvent(KeyEvent keyEvent) {
      if (this.this$0._delayedSpellCheckKeyedInputRunnableID != -1) {
         synchronized (this.this$0._inputMethodContext) {
            if (this.this$0._delayedSpellCheckKeyedInputRunnableID != -1) {
               Application.getApplication().cancelInvokeLater(this.this$0._delayedSpellCheckKeyedInputRunnableID);
            }
         }
      }

      super.handleInputKeyEvent(keyEvent);
   }

   @Override
   protected long getSpellCheckKeyedInputDelay() {
      return 1000;
   }
}
