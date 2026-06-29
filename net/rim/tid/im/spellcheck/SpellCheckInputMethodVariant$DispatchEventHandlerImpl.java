package net.rim.tid.im.spellcheck;

import net.rim.device.api.ui.component.TextField;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.im.layout.SLKeyLayout;

class SpellCheckInputMethodVariant$DispatchEventHandlerImpl implements SpellCheckInputMethodVariant$DispatchEventHandler {
   private final SpellCheckInputMethodVariant this$0;

   protected void moveCaretWithinMisspelledWord(int amount) {
      int caret = this.this$0._inputMethodContext.getCaretPosition();
      caret += amount;
      if (caret >= this.this$0._bounds.start) {
         if (caret <= this.this$0._bounds.end) {
            if (this.this$0._currentComponent != null && this.this$0._currentComponent instanceof TextField) {
               TextField field = (TextField)this.this$0._currentComponent;
               int labelLength = field.getLabelLength();
               if (caret >= labelLength && caret <= field.getTextLength() + labelLength) {
                  field.setCaretPosition(caret);
                  this.this$0.showInputBounds();
               }
            }
         }
      }
   }

   protected boolean checkIgnorableKeyCode(int keyCode) {
      switch (keyCode) {
         case 17:
         case 18:
         case 19:
         case 273:
            return true;
         default:
            return false;
      }
   }

   protected void handleFocusEvent(FocusEvent evt) {
   }

   protected void handleInputKeyEvent(KeyEvent evt) {
   }

   protected void handleNonInputKeyEvent(KeyEvent evt) {
   }

   protected void handleOtherEvent(Event evt) {
   }

   protected char eventToChar(KeyEvent event) {
      SLKeyLayout lnkLayout = this.this$0._controlObject.getKeyLayout();
      if (lnkLayout != null) {
         StringBuffer chs = lnkLayout.getKeyChars(event.getKeyCode(), event.getModifiers(), false);
         if (chs != null && chs.length() > 0) {
            return chs.charAt(0);
         }
      }

      return '\u0000';
   }

   @Override
   public void handleEvent(Event event) {
      this.this$0._cancelDelay = true;
      if (!(event instanceof KeyEvent)) {
         if (!(event instanceof FocusEvent)) {
            this.handleOtherEvent(event);
         } else {
            FocusEvent focusEvent = (FocusEvent)event;
            this.handleFocusEvent(focusEvent);
         }
      } else {
         KeyEvent keyEvent = (KeyEvent)event;
         int keyCode = keyEvent.getKeyCode();
         if (this.checkIgnorableKeyCode(keyCode)) {
            event.consume();
         } else if (keyCode == 4098) {
            event.consume();
            this.this$0.showMenu();
         } else if (keyEvent.isInputEvent()) {
            this.handleInputKeyEvent(keyEvent);
         } else {
            this.handleNonInputKeyEvent(keyEvent);
         }
      }
   }

   SpellCheckInputMethodVariant$DispatchEventHandlerImpl(SpellCheckInputMethodVariant _1) {
      this.this$0 = _1;
   }
}
