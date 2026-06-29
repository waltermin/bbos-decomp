package net.rim.tid.im;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodContext;
import net.rim.tid.awt.im.spi.InputModeChangeListener;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.itie.LinguisticData;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.TextHitInfo;
import net.rim.tid.util.Utils;

public class MinimalInputMethod implements InputMethod {
   InputMethodContext _iContext;
   SLKeyLayout _lnkLayout;
   int _rollerCharacterIndex = -1;
   AttributedString _buffer = new AttributedString();
   TextFilter _filter;
   private Locale _iLocale;
   private SLControlObject _iControlObject;
   private boolean iKeyRepeateProcessed;
   private Locale[] _iAvailableLocale;
   private int lastKeyPressed = -1;
   private boolean iNotFromKeypad;
   private int[] _iControl = new int[]{
      10,
      27,
      8,
      127,
      9,
      131,
      132,
      129,
      130,
      32,
      133,
      134,
      137,
      128,
      -804650987,
      12,
      14,
      16,
      18,
      20,
      22,
      24,
      26,
      28,
      30,
      32,
      34,
      37,
      39,
      41,
      48,
      51,
      54,
      57,
      60,
      84,
      -804651003,
      49,
      52,
      55,
      58,
      61,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804913038,
      809644097,
      809775171,
      809906245,
      810037319,
      810168393,
      810299467,
      810430541,
      810561615
   };
   private int _priorInserPosition;
   private int _keyboardID;
   private boolean _keyUpProcessOnly;
   private byte[] _IMProp;
   public static final char KEYPAD_TYPE_DELIMITER = '|';

   protected String getKeyMapLibName() {
      return "net_rim_platform_im_resource";
   }

   public boolean setFilter(TextFilter filter) {
      this._filter = filter;
      return true;
   }

   public char getPeriodSymbol() {
      return '.';
   }

   protected boolean isEnteringRollerCharacter() {
      return this._rollerCharacterIndex != -1;
   }

   protected synchronized void processRollEvent(KeyEvent evt) {
      int status = evt.getKeyChar();
      int amount = evt.getKeyCode();
      if ((status & 8) != 0 && (status & 2) == 0) {
         StringBuffer choices = this._lnkLayout.getKeyChars(this.lastKeyPressed, 3, false);
         if (choices != null && choices.length() > 0 && choices.charAt(0) != 0) {
            int startIndex = this._iContext.getComposedTextStart();
            if (this._rollerCharacterIndex != -1) {
               this._rollerCharacterIndex += amount;
            } else {
               boolean hasUpperCase = true;
               if (startIndex > 0) {
                  AttributedString res = this._iContext.getAttributedText();
                  if (res == null || res.length() == 0) {
                     return;
                  }

                  char lastKey = res.getText().charAt(startIndex - 1);
                  char uc;
                  if (CharacterUtilities.isLowerCase(lastKey)) {
                     uc = CharacterUtilities.toUpperCase(lastKey);
                  } else {
                     uc = lastKey;
                     hasUpperCase = CharacterUtilities.isUpperCase(lastKey);
                  }

                  for (int lv = choices.length() - 1; lv >= 0; lv--) {
                     if (choices.charAt(lv) == uc) {
                        this._rollerCharacterIndex = lv;
                        break;
                     }
                  }
               }

               if (amount > 0) {
                  this._rollerCharacterIndex++;
               } else {
                  this._rollerCharacterIndex -= hasUpperCase ? 2 : 1;
               }
            }

            this._rollerCharacterIndex = this._rollerCharacterIndex % choices.length();
            if (this._rollerCharacterIndex < 0) {
               this._rollerCharacterIndex = this._rollerCharacterIndex + choices.length();
            }

            int loopCounter = 0;
            int sign = MathUtilities.clamp(-1, amount, 1);
            if (this._filter != null) {
               while (!this._filter.validate(choices.charAt(this._rollerCharacterIndex), null, startIndex - 1)) {
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

            if (startIndex > 0) {
               AttributedString res = this._iContext.getAttributedText();
               if (res == null || res.length() == 0) {
                  return;
               }

               if (this._iContext.getComposedTextStart() == this._priorInserPosition + 1) {
                  this._iContext.setComposedText(startIndex - 1, startIndex);
               }
            }

            this._buffer.delete(this._buffer.length());
            this._buffer.insert(choices.charAt(this._rollerCharacterIndex));
            this.sendComposedText(this._buffer);
            evt.consume();
            return;
         }
      }
   }

   protected void sendComposedText(StringBuffer text) {
      this._buffer.set(text);
      this._iContext.dispatchInputMethodEvent(1100, this._buffer, 0, text.length(), 0, TextHitInfo.leading(0), null);
   }

   public boolean setKeyLayoutLocale(Locale aLocale) {
      String libName = this.getKeyMapLibName();
      Locale keyboardLocale = Locale.getDefaultForKeyboard();
      String keyboardType = this.getKeyboardType(keyboardLocale.getCode());
      int keyboardID = keyboardLocale.isKeyboardIDSet() ? keyboardLocale.getKeyboardID() : this._keyboardID;
      SLKeyLayout lnkLayout = SLKeyLayout.getLayout(aLocale, false, keyboardID, keyboardType, aLocale, libName, false);
      if (lnkLayout == null) {
         String language = aLocale.getLanguage();
         Locale l = Locale.get(language);
         lnkLayout = SLKeyLayout.getLayout(aLocale, false, keyboardID, keyboardType, l, libName, false);
         if (lnkLayout == null) {
            if (language.equals("es")) {
               lnkLayout = SLKeyLayout.getLayout(aLocale, false, keyboardID, keyboardType, Locale.get("ca", ""), libName, false);
            } else if (language.equals("he") && !keyboardType.equals("qwerty")) {
               lnkLayout = SLKeyLayout.getLayout(aLocale, false, keyboardID, "qwerty", aLocale, libName, false);
               if (lnkLayout == null) {
                  lnkLayout = SLKeyLayout.getLayout(aLocale, false, keyboardID, "qwerty", l, libName, false);
               }
            }
         }

         if (lnkLayout == null) {
            lnkLayout = SLKeyLayout.getLayout(aLocale, false, keyboardID, keyboardType, l, libName, true);
         }
      }

      if (lnkLayout == null) {
         return false;
      }

      this._lnkLayout = lnkLayout;
      return true;
   }

   void setLayout(SLKeyLayout aLayout) {
      if (aLayout != null) {
         this._lnkLayout = aLayout;
      }
   }

   void sendComposedText(AttributedString text) {
      this._iContext.dispatchInputMethodEvent(1100, text, 0, text.length(), 0, TextHitInfo.leading(0), null);
   }

   public SLKeyLayout getKeyLayout() {
      return this._lnkLayout;
   }

   protected synchronized void processKeyRepeate(KeyEvent evt) {
      int code = evt.getKeyCode();
      StringBuffer keyChars = this._lnkLayout.getKeyChars(code, evt.getModifiers(), false);
      char keyChar = keyChars.charAt(0);
      evt.setKeyChar(keyChar);
      if (!this.isControl(keyChar)) {
         if (this.iNotFromKeypad) {
            int position = this._iContext.getComposedTextStart() - this._iContext.getLabelLength();
            if (position >= 1) {
               AttributedString res = this._iContext.getAttributedText();
               if (res != null && res.length() != 0) {
                  char ch = res.getText().charAt(position - 1);
                  evt.setKeyChar(ch);
                  this._buffer.delete(this._buffer.length());
                  this._buffer.insert(ch);
                  this.sendComposedText(this._buffer);
                  evt.consume();
               }
            }
         } else {
            if (!this.iKeyRepeateProcessed && !this.isEnteringRollerCharacter()) {
               if (CharacterUtilities.isUpperCase(keyChar) || CharacterUtilities.isLowerCase(keyChar)) {
                  this.iKeyRepeateProcessed = true;
                  int startPos = this._iContext.getComposedTextStart();
                  int position = startPos - this._iContext.getLabelLength();

                  for (int i = 0; i < keyChars.length(); i++) {
                     char upCasedChar = Utils.toUpperCase(keyChars.charAt(i));
                     if (this._filter != null && !this._filter.validate(upCasedChar, null, position - 1)) {
                        keyChars.delete(i, i + 1);
                        i--;
                     } else {
                        keyChars.setCharAt(i, upCasedChar);
                     }
                  }

                  if (keyChars.length() == 0) {
                     evt.consume();
                     return;
                  }

                  evt.setKeyChar(keyChars.charAt(0));
                  if (keyChars.length() > 0 && this._iContext.getComposedTextStart() == this._priorInserPosition + 1 && position > 0) {
                     this._iContext.setComposedText(startPos - 1, startPos);
                  }

                  this.sendComposedText(keyChars);
                  evt.consume();
                  return;
               }

               if (CharacterUtilities.isLetter(keyChar)) {
                  this.iKeyRepeateProcessed = true;
                  evt.consume();
                  return;
               }
            } else {
               evt.consume();
            }
         }
      }
   }

   protected boolean isControl(char ch) {
      return Arrays.binarySearch(this._iControl, ch & 65535) >= 0;
   }

   @Override
   public void reconvert() {
   }

   @Override
   public void dispatchEvent(Event event) {
      if (event instanceof KeyEvent && this._lnkLayout != null) {
         KeyEvent evt = (KeyEvent)event;
         int eventID = evt.getID();
         int keyCode = evt.getKeyCode();
         if (evt.isInputEvent()) {
            switch (eventID) {
               case 514:
                  this.processKeyRepeate(evt);
                  return;
               case 516:
                  return;
               case 519:
                  this.processRollEvent(evt);
                  return;
            }
         }

         if ((eventID == 513 || eventID == 514) && !this.isKeyUpProcessOnly(evt) || this.isKeyUpProcessOnly(evt) && eventID == 515) {
            int modif = evt.getModifiers();
            if (this.isKeyUpProcessOnly(evt) && (modif & 8) != 0 && (modif & 2) == 0) {
               modif &= -9;
               modif |= 1;
            }

            StringBuffer keyChars = this._lnkLayout.getKeyChars(keyCode, modif, false);
            this._priorInserPosition = this._iContext.getCaretPosition() == this._iContext.getAnchorPosition()
               ? Math.min(this._iContext.getCaretPosition(), this._iContext.getAnchorPosition())
               : this._iContext.getComposedTextStart();
            this.iKeyRepeateProcessed = false;
            this.lastKeyPressed = keyCode;
            this._rollerCharacterIndex = -1;
            if (modif != 32768) {
               evt.setKeyChar(keyChars.charAt(0));
               this.iNotFromKeypad = false;
            } else {
               this.iNotFromKeypad = true;
            }

            char ch = this.iNotFromKeypad ? evt.getKeyChar() : keyChars.charAt(0);
            if (this.isKeyUpProcessOnly(evt) && (!this.isControl(ch) || ch == 137 || ch == ' ' || ch == '\b' || ch == 127)) {
               evt.consume();
               this._buffer.delete(0, this._buffer.length());
               this._buffer.insert(0, ch);
               this.sendComposedText(this._buffer);
            }
         }

         if (this.isKeyUpProcessOnly(evt) && eventID == 513) {
            char ch = this._lnkLayout.getKeyChars(keyCode, evt.getModifiers(), false).charAt(0);
            evt.setKeyChar(ch);
            switch (ch) {
               case '\u0000':
               case '\b':
               case '\n':
               case '\u001b':
               case ' ':
               case '\u007f':
                  break;
               default:
                  event.consume();
            }
         }
      }
   }

   @Override
   public boolean isCompositionEnabled() {
      return true;
   }

   @Override
   public int setTextInputStyle(int style) {
      this._keyUpProcessOnly = (style & 8192) != 0;
      return -1;
   }

   @Override
   public void setCompositionEnabled(boolean enable) {
   }

   @Override
   public Locale getLocale() {
      return this._iLocale;
   }

   @Override
   public boolean setLocale(Locale aLocale, int state) {
      if (this._iLocale != null && this._iLocale.equals(aLocale) && this._iLocale.getKeyboardID() == aLocale.getKeyboardID()) {
         return true;
      }

      for (int i = 0; i < this._iAvailableLocale.length; i++) {
         if (this._iAvailableLocale[i].equals(aLocale)) {
            if (this.setKeyLayoutLocale(aLocale)) {
               this._iLocale = aLocale;
               Keypad.setKeypadLocale(aLocale);
               return true;
            }

            return false;
         }
      }

      return false;
   }

   @Override
   public boolean setLocale(Locale aLocale) {
      return this.setLocale(aLocale, 0);
   }

   @Override
   public void setInputMethodContext(InputMethodContext context) {
      this._iContext = context;
   }

   @Override
   public void notifyClientWindowChange(XYRect bounds) {
   }

   @Override
   public void activate() {
   }

   @Override
   public void deactivate(boolean isTemporary) {
   }

   @Override
   public void hideWindows() {
   }

   @Override
   public void removeNotify() {
   }

   @Override
   public void endComposition() {
   }

   @Override
   public void reset(int type) {
   }

   @Override
   public void dispose() {
   }

   @Override
   public Object getControlObject() {
      if (this._iControlObject == null) {
         this._iControlObject = new SLControlObject(this);
      }

      return this._iControlObject;
   }

   @Override
   public int loadLinguisticData(LinguisticData aData) {
      return 1;
   }

   @Override
   public int unloadLinguisticData(int id) {
      return 1;
   }

   @Override
   public void setIMProperties(byte propID, byte[] IMProperties) {
      switch (propID) {
         case 1:
            this._IMProp = IMProperties;
      }
   }

   @Override
   public byte[] getIMProperties(byte propID) {
      switch (propID) {
         case 1:
            return this._IMProp;
         default:
            return null;
      }
   }

   @Override
   public boolean isCorrectWord(StringBufferGap sbg, int startIndex, int length) {
      return true;
   }

   @Override
   public int actionPerformed(Object src, int action, Object parameter) {
      return 0;
   }

   @Override
   public int setListener(InputModeChangeListener listener) {
      return 3;
   }

   @Override
   public InputModeChangeListener getListener() {
      return null;
   }

   @Override
   public CustomWordsRepository getRepository(int type) {
      return null;
   }

   @Override
   public CustomDictionary getCustomDictionary(int type) {
      return null;
   }

   private boolean isKeyUpProcessOnly(KeyEvent event) {
      return this._keyUpProcessOnly && event.isInputEvent();
   }

   public MinimalInputMethod(Locale[] aAvailableLocale) {
      this._iAvailableLocale = aAvailableLocale;
      this._keyboardID = Keypad.getHardwareLayout();
      Arrays.sort(this._iControl, 0, this._iControl.length);
   }

   private String getKeyboardType(int aLocaleCode) {
      String ret = "qwerty";
      switch (aLocaleCode & -65536) {
         case 1684340736:
            ret = "qwertz";
         default:
            return ret;
         case 1718747136:
            return "azerty";
      }
   }
}
