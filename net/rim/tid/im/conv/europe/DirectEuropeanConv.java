package net.rim.tid.im.conv.europe;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.spi.InputMethodContext;
import net.rim.tid.im.conv.ConversionEvent;
import net.rim.tid.im.conv.IConversion;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.text.AttributedString;
import net.rim.tid.util.Utils;

public class DirectEuropeanConv extends IConversion {
   private char[] _keyCharsCache = new char[2];
   private int _charCacheLen = -1;
   private int _charCacheIndex = -1;
   private int _rollerCharacterIndex = -1;
   private boolean _keyRepeateProcessed;
   private DirectEuropeanConv$Invoker _timeoutInvoker;
   private SLEuropeanWordsCorrector _corrector = new SLEuropeanWordsCorrector();
   private SLCurrentVariant _tempVariant = new SLCurrentVariant();
   private boolean _skipAutoCap;
   private char _lastCharUsed;
   private boolean _autocapNextKeys;
   private static final short MAX_NUMBER_OF_CORRECTED_CHARACTERS;
   public static final int SUBMIT_TIMEOUT;

   public DirectEuropeanConv() {
      this._timeoutInvoker = new DirectEuropeanConv$Invoker(this);
   }

   @Override
   public boolean setLocale(Locale src) {
      return true;
   }

   @Override
   public void dispatchConversionEvent(ConversionEvent event) {
      this._timeoutInvoker.cancel();
      int code = event.getKeyCode();
      StringBuffer chars = event.getKeyChars();
      if (!this.isControl(code) && chars.length() != 0 && chars.charAt(0) != 0) {
         this.processKeys(event);
         this.setSubmitInvocation();
      } else {
         this.processControl(event);
      }

      this._timeoutInvoker.start();
   }

   private void setSubmitInvocation() {
      if (!super.composedText.isEmpty()) {
         int timeout = UiSettings.getKeypadRepeatDelay();
         timeout = timeout < 500 ? 500 : timeout + 100;
         this._timeoutInvoker.init((byte)0, timeout);
      }
   }

   private void setKeyRepeatSubmitInvocation() {
      if (!super.composedText.isEmpty()) {
         int timeout = UiSettings.getKeypadRepeatDelay();
         if (!this.isRollCharsInput()) {
            timeout = timeout < 500 ? 500 : timeout + 100;
            timeout += UiSettings.getKeypadRepeatRate();
         } else {
            timeout = UiSettings.getKeypadRepeatRate() + 100;
         }

         this._timeoutInvoker.init((byte)2, timeout);
      }
   }

   private void setSymScreenInvocation() {
      this._timeoutInvoker.init((byte)1, UiSettings.getKeypadRepeatDelay() + 100);
   }

   protected void processKeyCodeUndefined(ConversionEvent event) {
      int evType = event.getInitialEvent().getID();
      switch (evType) {
         case 514:
            this.processKeyRepeat(event);
            return;
         case 515:
            int type = this._timeoutInvoker.getType();
            switch (type) {
               case 0:
               default:
                  this.setSubmitInvocation();
                  return;
               case 1:
                  super.inputMethod.actionPerformedCallback(113, null);
                  this.consumeEvent(event);
                  return;
               case 2:
                  this.setKeyRepeatSubmitInvocation();
               case -1:
                  return;
            }
         case 519:
            this.processRollEvent(event);
            return;
         default:
            if (!super.composedText.isEmpty()) {
               this.endComposition();
               this.consumeEvent(event);
            }
      }
   }

   private void processControl(ConversionEvent event) {
      int code = event.getKeyCode();
      SLVariants variant = super.composedText.getCurrentVariant(false);
      switch (code) {
         default:
            this._keyRepeateProcessed = false;
         case 0:
         case 129:
         case 130:
            this._skipAutoCap = false;
            if (!super.composedText.isEmpty()) {
               switch (code) {
                  case 0:
                  case 128:
                     break;
                  case 8:
                     super.composedText.getCurrentVariant(true).clear();
                     this.endComposition();
                     this.consumeEvent(event);
                     return;
                  case 10:
                     this.endComposition();
                     return;
                  case 129:
                  case 130:
                     this.consumeEvent(event);
                     if (!super.composedText.isLookupVisible()) {
                        this.endComposition();
                        return;
                     }

                     if (variant.getVariantsCount() > 1) {
                        int action = code == 130 ? 31 : 32;
                        super.inputMethod.setActionPerformedToLookup(this, action, event);
                        variant.setCaretPosition(0);
                        super.composedText.setLookVisible(true);
                     }

                     return;
                  default:
                     this.endComposition();
                     this.consumeEvent(event);
                     return;
               }
            }

            switch (code) {
               case 0:
                  this.processKeyCodeUndefined(event);
                  return;
               case 8:
                  this.processBackSpaceForCommited(event);
                  return;
               case 128:
                  this.endComposition();
                  this.consumeEvent(event);
                  if (this.isPhoneInputStyle()) {
                     this.setSymScreenInvocation();
                     return;
                  }

                  super.inputMethod.actionPerformedCallback(113, null);
                  return;
               default:
                  this._timeoutInvoker.init((byte)-1, -1);
                  this._rollerCharacterIndex = -1;
            }
      }
   }

   private void consumeEvent(ConversionEvent event) {
      event.consume();
      if (super.composedText.isLookupVisible()) {
         event.setID(3);
      } else {
         event.setID(1);
      }
   }

   private void processBackSpaceForCommited(ConversionEvent event) {
      InputMethodContext context = super.inputMethod.getInputMethodContext();
      int insPosition = context.getComposedTextStart();
      if (insPosition != 0) {
         AttributedString text = context.getAttributedText();
         if (text != null) {
            this._lastCharUsed = text.getText().charAt(insPosition - 1);
            if (CharacterUtilities.isUpperCase(this._lastCharUsed)) {
               this._skipAutoCap = true;
               this._lastCharUsed = Utils.toLowerCase(this._lastCharUsed);
            }
         }
      }
   }

   private void processKeyRepeat(ConversionEvent event) {
      this._timeoutInvoker.cancel();
      this._timeoutInvoker.init((byte)-1, -1);
      this.setKeyRepeatSubmitInvocation();
      if (!this.isRollCharsInput() && !this._keyRepeateProcessed) {
         KeyEvent evt = (KeyEvent)event.getInitialEvent();
         StringBuffer chars = super.inputMethod.getKeyLayout().getKeyChars(evt.getKeyCode(), evt.getModifiers(), false);
         event.setKeyChars(chars);
         char backup = chars.charAt(0);
         this.verify(chars, event);
         if (chars.length() != 0 && (!this.isControl(backup) || chars.charAt(0) != 0)) {
            backup = chars.charAt(0);
         }

         if (this.isControl(backup)) {
            event.setKeyCode(backup);
            if (event.getKeyCode() == 0) {
               event.consume();
            } else {
               this.processControl(event);
            }
         } else {
            this.consumeEvent(event);
            if (chars.length() != 0) {
               if (this.isPhoneInputStyle()) {
                  this.processPhoneFieldKeyRepeate(event, chars);
               } else {
                  SLVariants variant = super.composedText.getCurrentVariant(true);
                  if (variant.currentVariantLength() != 0) {
                     StringBuffer sb = variant.getOriginal();
                     char tmp = Utils.toUpperCase(sb.charAt(0));
                     variant.clear();
                     sb.append(tmp);
                  }
               }
            }
         }
      } else {
         this.consumeEvent(event);
      }
   }

   private void processRollEvent(ConversionEvent aEvent) {
      if (this.isPhoneInputStyle()) {
         if (!super.composedText.isEmpty()) {
            this.endComposition();
            this.consumeEvent(aEvent);
         }
      } else {
         KeyEvent evt = (KeyEvent)aEvent.getInitialEvent();
         int status = evt.getKeyChar();
         int amount = evt.getKeyCode();
         if ((status & 8) != 0 && (status & 2) == 0) {
            StringBuffer choices = super.inputMethod.getKeyLayout().getKeyChars(super.inputMethod.getLastKeyPressed(), 3, false);
            this.verify(choices, aEvent, false);
            if (choices != null && choices.length() > 0 && choices.charAt(0) != 0) {
               SLVariants variant = super.composedText.getCurrentVariant(false);
               if (variant == null) {
                  aEvent.consume();
                  return;
               }

               StringBuffer original = variant.getOriginal();
               if (this._rollerCharacterIndex == -1 && original.length() > 0) {
                  char lastKey = original.charAt(original.length() - 1);

                  for (int lv = choices.length() - 1; lv >= 0; lv--) {
                     if (choices.charAt(lv) == lastKey) {
                        this._rollerCharacterIndex = lv;
                        break;
                     }
                  }

                  if (amount > 0) {
                     this._rollerCharacterIndex++;
                  } else {
                     this._rollerCharacterIndex -= 2;
                  }
               } else {
                  this._rollerCharacterIndex += amount;
               }

               this._rollerCharacterIndex = this._rollerCharacterIndex % choices.length();
               if (this._rollerCharacterIndex < 0) {
                  this._rollerCharacterIndex = this._rollerCharacterIndex + choices.length();
               }

               int loopCounter = 0;
               int sign = MathUtilities.clamp(-1, amount, 1);
               if (super._filter != null) {
                  while (!super._filter.validate(choices.charAt(this._rollerCharacterIndex))) {
                     this._rollerCharacterIndex += sign;
                     if (this._rollerCharacterIndex < 0) {
                        this._rollerCharacterIndex = this._rollerCharacterIndex + choices.length();
                     }

                     this._rollerCharacterIndex = this._rollerCharacterIndex % choices.length();
                     if (loopCounter++ > choices.length()) {
                        evt.consume();
                        return;
                     }
                  }
               }

               variant.clear();
               variant.insertChar(choices.charAt(this._rollerCharacterIndex), -1);
               variant.setCaretPosition(0);
               this.consumeEvent(aEvent);
            }
         }
      }
   }

   private void processPhoneFieldKeyRepeate(ConversionEvent event, StringBuffer chars) {
      this._keyRepeateProcessed = true;
      StringBuffer keyChars = event.getKeyChars();
      keyChars.setLength(0);
      if (chars.charAt(0) == '0') {
         keyChars.append('+');
         if (super.composedText.isEmpty()) {
            InputMethodContext context = super.inputMethod.getInputMethodContext();
            int caretPos = context.getComposedTextStart();
            if (caretPos != 0) {
               context.setComposedText(caretPos - 1, caretPos);
               if (context.getComposedTextStart() == context.getComposedTextEnd()) {
                  return;
               }
            }

            StringBuffer org = super.composedText.getCurrentVariant(true).getOriginal();
            org.setLength(0);
            org.append(keyChars.charAt(0));
         }

         this.endComposition();
         this.consumeEvent(event);
      } else {
         for (int i = 0; i < chars.length(); i++) {
            char tmp = chars.charAt(i);
            if (tmp == 'x') {
               keyChars.append(tmp);
            }
         }

         if (keyChars.length() > 0) {
            super.composedText.getCurrentVariant(true).insertChar(keyChars.charAt(0), -1);
         } else {
            keyChars.append('\u0000');
         }

         this.consumeEvent(event);
         this.endComposition();
      }
   }

   private void processKeys(ConversionEvent event) {
      this._rollerCharacterIndex = -1;
      this._keyRepeateProcessed = false;
      if (this.isRollCharsInput() || super.composedText.isLookupVisible()) {
         this.endComposition();
      }

      StringBuffer chars = event.getKeyChars();
      if (this._skipAutoCap) {
         this._skipAutoCap = false;

         for (int i = 0; i < chars.length(); i++) {
            if (chars.charAt(i) == this._lastCharUsed) {
               this._skipAutoCap = true;
               break;
            }
         }
      }

      this.verify(chars, event);
      if (chars.length() == 0) {
         chars.append('\u0000');
      } else {
         event.consume();
         event.setID(1);
         if (chars.length() != 1 || CharacterUtilities.isLetter(chars.charAt(0)) && !this.isPhoneInputStyle()) {
            if (this.isCharsRepeated(chars)) {
               if (this._autocapNextKeys) {
                  for (int i = 0; i < chars.length(); i++) {
                     chars.setCharAt(i, Utils.toUpperCase(chars.charAt(i)));
                  }

                  this._autocapNextKeys = false;
               }

               if (this._charCacheIndex != this._charCacheLen - 1) {
                  SLVariants variant = super.composedText.getCurrentVariant(true);
                  variant.getOriginal().setCharAt(0, this._keyCharsCache[++this._charCacheIndex]);
                  return;
               }
            }

            if (!super.composedText.isEmpty()) {
               super.composedText.setCommittedVariantsCount(1);
            }

            SLVariants variant = super.composedText.getFreeVariant();
            variant.getOriginal().setLength(1);
            variant.getOriginal().setCharAt(0, chars.charAt(0));
            this.checkCorrection();
            super.composedText.insertVariant(variant, super.composedText.getVariantsCount());
            super.composedText.setConvertedCharacterCount(0);
            this.setKeyCharsCache(chars);
            this._autocapNextKeys = (event.getModifiers() & 1) != 0;
            this._charCacheIndex = 0;
         } else {
            if (!super.composedText.isEmpty()) {
               this.endComposition();
            }

            super.composedText.getCurrentVariant(true).insertChar(chars.charAt(0), -1);
            super.composedText.commitAll();
         }
      }
   }

   private void verify(StringBuffer chars, ConversionEvent event) {
      InputMethodContext _icontext = super.inputMethod.getInputMethodContext();
      int position = -1;
      if (_icontext != null) {
         position = _icontext.getComposedTextStart();
      }

      super.verify(chars, true, event.getModifiers(), position);
   }

   private void verify(StringBuffer chars, ConversionEvent event, boolean convert) {
      InputMethodContext _icontext = super.inputMethod.getInputMethodContext();
      int position = -1;
      if (_icontext != null) {
         position = _icontext.getComposedTextStart();
      }

      super.verify(chars, convert, event.getModifiers(), position);
   }

   private boolean isCharsRepeated(StringBuffer chars) {
      if (chars.length() != this._charCacheLen) {
         return false;
      }

      for (int i = 0; i < this._charCacheLen; i++) {
         if (this._autocapNextKeys) {
            if (Utils.toUpperCase(chars.charAt(i)) != this._keyCharsCache[i]) {
               return false;
            }
         } else if (chars.charAt(i) != this._keyCharsCache[i]) {
            return false;
         }
      }

      return true;
   }

   private void setKeyCharsCache(StringBuffer chars) {
      if (this._keyCharsCache.length < chars.length()) {
         this._keyCharsCache = new char[chars.length()];
      }

      this._charCacheLen = chars.length();
      chars.getChars(0, this._charCacheLen, this._keyCharsCache, 0);
   }

   @Override
   public void endComposition() {
      this._timeoutInvoker.cancel();
      this._charCacheLen = -1;
      this._charCacheIndex = -1;
      this._rollerCharacterIndex = -1;
      this._autocapNextKeys = false;
      this.checkCorrection();
      super.composedText.commitAll();
      super.inputMethod.sendComposedText(super.composedText);
   }

   @Override
   public void reset() {
      this._timeoutInvoker.cancel();
      this._charCacheLen = -1;
      this._charCacheIndex = -1;
      this._rollerCharacterIndex = -1;
      this._autocapNextKeys = false;
      this.checkCorrection();
      super.composedText.commitAll();
      super.composedText.removeAllVariants();
   }

   protected boolean isRollCharsInput() {
      return this._rollerCharacterIndex != -1;
   }

   @Override
   public boolean isControl(int code) {
      return code == 32 ? false : super.isControl(code);
   }

   private void timeoutInvoke(byte type) {
      switch (type) {
         case -1:
            break;
         case 0:
         default:
            if (!super.composedText.isLookupVisible()) {
               super.inputMethod.endComposition();
               return;
            }
            break;
         case 1:
            super.inputMethod.actionPerformedCallback(113, null);
            return;
         case 2:
            super.inputMethod.endComposition();
      }
   }

   private boolean isPhoneInputStyle() {
      return (super._inputStyle & 100663296) != 0;
   }

   protected void checkCorrection() {
      if (this.isAutocapON()) {
         this._skipAutoCap = false;
         SLVariants variant = super.composedText.getCurrentVariant(false);
         if (variant != null) {
            variant.separateVariants();
            if (variant.getCurrentVariantIndex() != -1) {
               variant.getVariantAt(variant.getCurrentVariantIndex(), this._tempVariant);
               variant.setOriginal(this._tempVariant);
               variant.setVariantIndex(-1);
            }

            StringBuffer uncommitted = variant.getOriginal();
            InputMethodContext context = super.inputMethod.getInputMethodContext();
            int caretPos = context.getComposedTextStart();
            int start_pos = caretPos >= 50 ? caretPos - 50 : 0;
            AttributedString committed = null;
            if (start_pos != caretPos) {
               committed = context.getAttributedText();
               if (committed == null) {
                  return;
               }
            }

            if (uncommitted.length() > 0) {
               this._corrector.applyCorrection(uncommitted, committed, start_pos, caretPos, start_pos == 0, false, this.isAutotextON());
            }
         }
      }
   }

   protected boolean isAutocapON() {
      return (super._inputStyle & 262144) != 0 && !this._skipAutoCap && !this.isPhoneInputStyle();
   }
}
