package net.rim.tid.im.spellcheck;

import net.rim.tid.awt.Event;

class SpellCheckInputMethodVariant$PassthruModeEventHandler implements SpellCheckInputMethodVariant$DispatchEventHandler {
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$PassthruModeEventHandler(SpellCheckInputMethodVariant _1) {
      this.this$0 = _1;
   }

   @Override
   public void handleEvent(Event event) {
      this.this$0._inputMethod.dispatchEvent(event);
   }
}
