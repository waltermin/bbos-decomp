package net.rim.tid.im.spellcheck;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.itie.IComponent;

class SpellCheckInputMethodVariant$LookupModeEventHandler extends SpellCheckInputMethodVariant$DispatchEventHandlerImpl {
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$LookupModeEventHandler(SpellCheckInputMethodVariant _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected void handleInputKeyEvent(KeyEvent keyEvent) {
      switch (keyEvent.getID()) {
         case 513:
         case 514:
         case 515:
            char ch = this.eventToChar(keyEvent);
            switch (ch) {
               case '\b':
                  if (this.handleLookupBackspace(keyEvent)) {
                     return;
                  }
                  break;
               case '\n':
                  if (this.handleLookupEnter(keyEvent)) {
                     return;
                  }
                  break;
               case '\u001b':
                  if (this.handleLookupEscape(keyEvent)) {
                     return;
                  }
            }
         default:
            this.this$0.hideWindows();
            this.this$0._state = 5;
            this.this$0._lookupMode = 3;
            this.this$0._misspelledWord.setLength(0);
            this.this$0._misspelledWord.append(this.this$0._variants.getOriginal());
            this.this$0._variants.setVariantIndex(-1);
            this.this$0.dispatchEvent(keyEvent);
            return;
         case 519:
            this.processKeyRoll(keyEvent);
      }
   }

   protected void processKeyRoll(KeyEvent keyEvent) {
      int amount = keyEvent.getKeyCode();
      this.moveCaretWithinMisspelledWord(amount);
      keyEvent.consume();
   }

   @Override
   protected void handleNonInputKeyEvent(KeyEvent keyEvent) {
      this.this$0._inputMethod.dispatchEvent(keyEvent);
   }

   @Override
   protected void handleFocusEvent(FocusEvent evt) {
      IComponent comp = InputContext.getInstance().getInputComponent();
      if (comp != this.this$0._currentComponent) {
         this.this$0.reset(1);
      }
   }

   protected boolean handleLookupBackspace(KeyEvent keyEvent) {
      return false;
   }

   private boolean handleLookupEscape(KeyEvent event) {
      switch (event.getID()) {
         case 512:
            return false;
         case 513:
         default:
            event.consume();
            if (!Application.getApplication().acceptsKeyUpEvents()) {
               if (!this.this$0._delaying) {
                  if (this.this$0._delayedSpellCheckRunnable == null) {
                     this.this$0._delayedSpellCheckRunnable = new SpellCheckInputMethodVariant$DelayedSpellCheck(this.this$0, this.this$0._inputMethodContext);
                  }

                  this.this$0._delaying = true;
                  this.this$0._cancelDelay = false;
                  Application.getApplication().invokeLater(this.this$0._delayedSpellCheckRunnable, UiSettings.getKeypadRepeatDelay(), false);
                  return true;
               }

               this.this$0.hideWindows();
               this.this$0.ignoreToken(false);
               this.this$0.runSpellCheck();
            }

            return true;
         case 514:
            event.consume();
            this.this$0.hideWindows();
            this.this$0.stopSpellCheck(1);
            UiEngine ui = Ui.getUiEngine();
            Screen screen = ui.getActiveScreen();
            if (screen != null) {
               screen.setGateInput(true);
            }

            return true;
         case 515:
            event.consume();
            if (Application.getApplication().acceptsKeyUpEvents()) {
               this.this$0.hideWindows();
               this.this$0.ignoreToken(false);
               this.this$0.runSpellCheck();
            }

            return true;
      }
   }

   private boolean handleLookupEnter(KeyEvent event) {
      switch (event.getID()) {
         case 512:
            return false;
         case 513:
         default:
            event.consume();
            this.this$0.hideWindows();
            this.this$0.changeTokenToWord(false);
            this.this$0.runSpellCheck();
            return true;
         case 514:
            event.consume();
            return true;
         case 515:
            event.consume();
            return true;
      }
   }
}
