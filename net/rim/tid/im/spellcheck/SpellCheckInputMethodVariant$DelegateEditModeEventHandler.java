package net.rim.tid.im.spellcheck;

import net.rim.device.api.system.Application;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.itie.IComponent;
import net.rim.tid.text.TextHitInfo;

class SpellCheckInputMethodVariant$DelegateEditModeEventHandler extends SpellCheckInputMethodVariant$DispatchEventHandlerImpl {
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$DelegateEditModeEventHandler(SpellCheckInputMethodVariant _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected void handleFocusEvent(FocusEvent evt) {
      IComponent comp = InputContext.getInstance().getInputComponent();
      if (comp != this.this$0._currentComponent) {
         this.this$0.reset(1);
      }
   }

   @Override
   protected void handleInputKeyEvent(KeyEvent evt) {
      int caret = this.this$0._inputMethodContext.getCaretPosition();
      if (caret > this.this$0._bounds.end) {
         caret = this.this$0._bounds.end;
      }

      if (this.this$0._state == 5) {
         this.this$0.sendCommittedComposed(true);
         this.this$0._inputMethodContext.dispatchInputMethodEvent(1102, null, 0, 0, 0, (TextHitInfo)(new Object(caret, true)), null);
         KeyEvent ke = (KeyEvent)(new Object(this.this$0._currentComponent, 516, 0, 0, 0, '\u0000', -1));
         ke.init(this.this$0._currentComponent, 516, 0, 0, 0, '\u0000', true);
         this.this$0._inputMethod.dispatchEvent(ke);
         this.this$0._state = 6;
      }

      this.this$0._inputMethod.dispatchEvent(evt);
      if (this.this$0._inputMethodContext.getComposedTextStart() == this.this$0._inputMethodContext.getComposedTextEnd()) {
         this.this$0._inputMethod.endComposition();
         if (this.this$0._delayedRunSpellCheckRunnable == null) {
            this.this$0._delayedRunSpellCheckRunnable = new SpellCheckInputMethodVariant$DelayedRunSpellCheck(this.this$0, this.this$0._inputMethodContext);
         }

         Application.getApplication().invokeLater(this.this$0._delayedRunSpellCheckRunnable, 20, false);
      }
   }

   @Override
   protected void handleNonInputKeyEvent(KeyEvent evt) {
      this.this$0._inputMethod.dispatchEvent(evt);
   }

   @Override
   protected void handleOtherEvent(Event evt) {
      this.this$0._inputMethod.dispatchEvent(evt);
   }
}
