package net.rim.tid.im.spellcheck;

import net.rim.device.api.system.Application;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.itie.IComponent;
import net.rim.tid.text.TextHitInfo;

class SpellCheckInputMethodVariant$EditModeEventHandler extends SpellCheckInputMethodVariant$DispatchEventHandlerImpl {
   private final SpellCheckInputMethodVariant this$0;
   private static final long SPELLCHECK_DELAY = 20L;

   SpellCheckInputMethodVariant$EditModeEventHandler(SpellCheckInputMethodVariant _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected void handleNonInputKeyEvent(KeyEvent evt) {
      this.this$0._inputMethod.dispatchEvent(evt);
   }

   @Override
   protected void handleInputKeyEvent(KeyEvent keyEvent) {
      int eventID = keyEvent.getID();
      switch (eventID) {
         case 512:
         case 517:
         case 518:
            break;
         case 513:
         case 514:
            if (this.eventToChar(keyEvent) == '\n') {
               this.this$0.scanFromCurrentPosition(false);
               keyEvent.consume();
            } else if (this.eventToChar(keyEvent) == 27) {
               keyEvent.consume();
               if (eventID == 514) {
                  this.this$0.stopSpellCheck(1);
               } else if (!Application.getApplication().acceptsKeyUpEvents()) {
                  if (!this.this$0._delaying) {
                     if (this.this$0._delayedSpellCheckRunnable == null) {
                        this.this$0._delayedSpellCheckRunnable = new SpellCheckInputMethodVariant$DelayedSpellCheck(
                           this.this$0, this.this$0._inputMethodContext
                        );
                     }

                     this.this$0._delaying = true;
                     this.this$0._cancelDelay = false;
                     Application.getApplication().invokeLater(this.this$0._delayedSpellCheckRunnable, UiSettings.getKeypadRepeatDelay(), false);
                  } else {
                     this.this$0.scanFromCurrentPosition(true);
                  }
               }
            } else {
               int caret = this.this$0._inputMethodContext.getCaretPosition();
               if (caret > this.this$0._bounds.end) {
                  caret = this.this$0._bounds.end;
               }

               if (this.this$0._state == 5) {
                  this.this$0.sendCommittedComposed(true);
                  this.this$0._inputMethodContext.dispatchInputMethodEvent(1102, null, 0, 0, 0, (TextHitInfo)(new Object(caret, true)), null);
                  this.this$0._state = 6;
               }

               if (this.this$0._delayedSpellCheckKeyedInputRunnable == null) {
                  this.this$0._delayedSpellCheckKeyedInputRunnable = new SpellCheckInputMethodVariant$DelayedSpellCheckKeyedInput(
                     this.this$0, this.this$0._inputMethodContext
                  );
               }

               Application.getApplication().invokeLater(this.this$0._delayedSpellCheckKeyedInputRunnable, this.getSpellCheckKeyedInputDelay(), false);
            }
            break;
         case 515:
            if (this.eventToChar(keyEvent) == 27) {
               this.this$0.scanFromCurrentPosition(true);
               keyEvent.consume();
            }
            break;
         case 516:
         default:
            this.this$0.learnCorrection();
            this.this$0.scanFromCurrentPosition(false);
            keyEvent.consume();
            break;
         case 519:
            int amount = keyEvent.getKeyCode();
            this.moveCaretWithinMisspelledWord(amount);
            keyEvent.consume();
      }

      if (!keyEvent.isConsumed()) {
         this.this$0._inputMethod.dispatchEvent(keyEvent);
      }
   }

   @Override
   protected void handleFocusEvent(FocusEvent evt) {
      IComponent comp = InputContext.getInstance().getInputComponent();
      if (comp != this.this$0._currentComponent) {
         this.this$0.reset(1);
      }

      if (!evt.isConsumed()) {
         this.this$0._inputMethod.dispatchEvent(evt);
      }
   }

   @Override
   protected void handleOtherEvent(Event evt) {
      this.this$0._inputMethod.dispatchEvent(evt);
   }

   protected long getSpellCheckKeyedInputDelay() {
      return 20;
   }
}
