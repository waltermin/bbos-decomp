package net.rim.tid.im.spellcheck;

import net.rim.device.api.ui.component.TextField;
import net.rim.tid.awt.event.KeyEvent;

class SpellCheckInputMethodVariant$SureTypeLookupModeEventHandler extends SpellCheckInputMethodVariant$LookupModeEventHandler {
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$SureTypeLookupModeEventHandler(SpellCheckInputMethodVariant _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected void processKeyRoll(KeyEvent keyEvent) {
      int amount = keyEvent.getKeyCode();
      if (amount >= 0) {
         this.moveCaretWithinMisspelledWord(amount);
         keyEvent.consume();
      } else {
         int caret = this.this$0._inputMethodContext.getCaretPosition();
         caret += amount;
         if (caret >= this.this$0._bounds.start) {
            if (caret <= this.this$0._bounds.end) {
               this.this$0.hideWindows();
               this.this$0._state = 5;
               this.this$0._lookupMode = 3;
               this.this$0._misspelledWord.setLength(0);
               this.this$0._misspelledWord.append(this.this$0._variants.getOriginal());
               this.this$0._variants.setVariantIndex(-1);
               this.this$0.sendCommittedComposed(true);
               if (this.this$0._currentComponent != null && this.this$0._currentComponent instanceof Object) {
                  TextField field = (TextField)this.this$0._currentComponent;
                  int labelLength = field.getLabelLength();
                  if (caret >= labelLength && caret <= field.getTextLength() + labelLength) {
                     field.setCaretPosition(caret);
                  }
               }

               KeyEvent ke = (KeyEvent)(new Object(this.this$0._currentComponent, 516, 0, 0, 0, '\u0000', -1));
               ke.init(this.this$0._currentComponent, 516, 0, 0, 0, '\u0000', true);
               this.this$0._inputMethod.dispatchEvent(ke);
               this.this$0._state = 6;
            }
         }
      }
   }
}
