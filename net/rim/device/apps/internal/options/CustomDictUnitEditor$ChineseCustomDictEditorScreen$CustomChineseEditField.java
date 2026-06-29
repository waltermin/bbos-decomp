package net.rim.device.apps.internal.options;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.EditField;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.util.PinInfo;

class CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField extends EditField {
   private Vector _pinInfoFR;
   private final CustomDictUnitEditor$ChineseCustomDictEditorScreen this$1;

   public CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField(CustomDictUnitEditor$ChineseCustomDictEditorScreen _1, long style) {
      super(null, null, 1000000, style);
      this.this$1 = _1;
      this._pinInfoFR = (Vector)(new Object(6));
   }

   public Vector getPinInfoFR() {
      return this._pinInfoFR;
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      if (InputContext.getInstance().getActiveInputMethodID() == 16384 && event.getCommittedCharacterCount() > 0) {
         PinInfo curPin = (PinInfo)(new Object());
         this.this$1._controlObject.actionPerformed(137, curPin);

         for (int i = 0; i < curPin.length(); i++) {
            PinInfo curPins = (PinInfo)(new Object());
            curPins.setSingle(curPin, curPin.length() - 1 - i);
            if (this.getCaretPosition() >= this.getComposedTextStart() && this.getCaretPosition() <= this.getComposedTextEnd()) {
               this._pinInfoFR.insertElementAt(curPins, this.getComposedTextStart());
            } else {
               this._pinInfoFR.insertElementAt(curPins, this.getCaretPosition() - 1);
            }
         }

         return super.inputMethodTextChanged(event);
      } else {
         return super.inputMethodTextChanged(event);
      }
   }

   private boolean isAlphabetChar(char ch) {
      return ch >= 'a' && ch <= 'z';
   }

   private boolean isControl(char ch, int modifiers) {
      switch (ch) {
         case '\u0000':
         case '\t':
         case '\u001b':
         case '\u0081':
         case '\u0082':
         case '\u0083':
         case '\u0084':
         case '\u0085':
         case '\u0086':
         case '\u0089':
            return true;
         case '\b':
            if (InputContext.getInstance().getActiveInputMethodID() == 16384 && this.getComposedTextLength() == 0 && this._pinInfoFR.size() > 0) {
               if (this.getCaretPosition() == 0) {
                  this._pinInfoFR.removeElementAt(0);
                  return true;
               }

               this._pinInfoFR.removeElementAt(this.getCaretPosition() - 1);
            }

            return true;
         case '\n':
            if ((modifiers & 8) != 0 && (modifiers & 2) == 0) {
               return false;
            }

            return true;
         case ' ':
            if (this.getTextLength() > 0) {
               return true;
            }

            return false;
         default:
            return false;
      }
   }

   private boolean isMaxLengthReached() {
      String word = this.this$1.this$0._mainScreen.getText();
      int count = 0;

      for (int i = word.length() - 1; i >= 0; i--) {
         char ch = word.charAt(i);
         if (!this.isAlphabetChar(ch)) {
            count++;
         }
      }

      return count >= 6;
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      if (!(rEvent instanceof Object)) {
         super.dispatchEvent(rEvent);
      } else {
         KeyEvent event = (KeyEvent)rEvent;
         if (!event.isInputEvent()) {
            super.dispatchEvent(rEvent);
         } else {
            int keycode = event.getKeyCode();
            int modifiers = event.getModifiers();
            char key = Keypad.getLayout().getKeyChars(keycode, modifiers).charAt(0);
            if (this.isAlphabetChar(key) || this.isControl(key, modifiers)) {
               super.dispatchEvent(rEvent);
               if (keycode == 10 || rEvent.getID() == 516) {
                  if (this.getCommittedTextLength() == this.getTextLength()) {
                     event.init(event.getSource(), event.getID(), event.getWhen(), modifiers, keycode, event.getKeyChar());
                  } else if (event.isConsumed()) {
                     this.this$1._controlObject.actionPerformed(137, this.this$1._pinInfo);
                  }
               }

               if (this.isMaxLengthReached()) {
                  if (this.isAlphabetChar(key)) {
                     event.setKeyCode(8);
                     super.dispatchEvent(rEvent);
                     event.setKeyCode(keycode);
                     if (InputContext.getInstance().getActiveInputMethodID() != 16384) {
                        this.this$1._controlObject.actionPerformed(137, this.this$1._pinInfo);
                     }

                     this.this$1._controlObject.actionPerformed(154, this.this$1._pinInfo);
                     Application.getApplication()
                        .invokeLaterInternal(new CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField$1(this), 1, false);
                     event.consume();
                     return;
                  }

                  if (event.isConsumed() && InputContext.getInstance().getActiveInputMethodID() != 16384) {
                     this.this$1._controlObject.actionPerformed(137, this.this$1._pinInfo);
                  }
               }
            }
         }
      }
   }
}
