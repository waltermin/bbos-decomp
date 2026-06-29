package net.rim.device.apps.internal.phone;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.ApplicationKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.ui.ReturnKeyIcons;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.phone.api.DTMFEcho;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.gprs.GSM230Filter;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.SpeedDialVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.tid.awt.Event;
import net.rim.tid.text.AttributedString;
import net.rim.vm.Array;

final class PhoneNumberInput extends BasicEditField implements HolsterListener, PersistentContentListener {
   private PhoneInputScreen _screen;
   private Application _app;
   private int _fieldState = 0;
   private int _fieldStateChangeLength = -1;
   private PhoneNumberInput$KeyEventInformation _keyEventInfo;
   private Font[] _inputFonts = new Font[5];
   private int _fontIndex = 0;
   private int[] _lineInfo = new int[1];
   private BasicEditField _source;
   private boolean _ignoreKeyUp = false;
   private boolean _ignoreKeyRepeat = false;
   private boolean _keypadAltMode = false;
   private boolean _resetAltMode = false;
   private int _delayingEchoId = -1;
   StringBuffer _bufferedString = new StringBuffer();
   private Runnable _delayEchoToneRunnable = new PhoneNumberInput$2(this);
   private static final int MAX_INPUT = 80;
   private static final int INPUT_SCREEN_WIDTH = Display.getWidth();
   private static final int IMAGE_PADDING = 4;
   private static final char REPLACEMENT = '�';
   private static boolean _isReducedKeyboard = PhoneUtilities.isQwertyReducedKeyboard();
   private static IconCollection _returnKeyIconCollection;
   static final int AMBIGUOUS_MODE = 0;
   static final int NUMBER_MODE = 1;
   static final int ADDRESS_MODE = 2;
   private static final int INVALID_STATE = -1;
   private static final int MIN_FONT_HEIGHT = Math.max(20, getReturnKeyImage(0).getHeight(25, 25));
   static FontFamily _phoneScreenFontFamily;
   private static final int HANDLED = -1;
   private static VerbRepository _rawPhoneNumberRepository = VerbRepository.getVerbRepository(-5389783330697330291L);
   private static ContextObjectWR _rawPhoneNumberInputContextWR = new ContextObjectWR();

   PhoneNumberInput(PhoneInputScreen screen, Application app, BasicEditField source) {
      super(null, null, 80, 0);
      this._keyEventInfo = new PhoneNumberInput$KeyEventInformation(this);
      this.setTag(Tag.create("phone-dial"));
      this._screen = screen;

      label31:
      try {
         int fontSize = 42;
         FontFamily fontFamily = FontFamily.forName("BBMillBank");
         this._inputFonts[0] = fontFamily.getFont(1, fontSize);

         for (int i = 1; i < this._inputFonts.length; i++) {
            this._inputFonts[i] = this.getAdjustedFont(i - 1);
         }
      } finally {
         break label31;
      }

      this._source = source;
      this._app = app;
      PersistentContent.addWeakListener(this);
   }

   static final Image getReturnKeyImage(int inputMode) {
      if (_returnKeyIconCollection == null) {
         _returnKeyIconCollection = ReturnKeyIcons.ICONS;
      }

      int index = 0;
      switch (inputMode) {
         case -1:
            break;
         case 0:
         default:
            index = 0;
            break;
         case 1:
            index = 1;
            break;
         case 2:
            index = 2;
      }

      return _returnKeyIconCollection.getImage(index);
   }

   private final Image getReturnKeyImage() {
      int mode = 0;
      if (this._keypadAltMode) {
         mode = 2;
      }

      return getReturnKeyImage(mode);
   }

   static final boolean validateCharacter(char ch) {
      return Keypad.getAltedChar(ch) == '*' || Keypad.getAltedChar(ch) == '#'
         ? true
         : CharacterUtilities.isLetter(ch) || CharacterUtilities.isDigit(ch) || CharacterUtilities.isSpaceChar(ch);
   }

   private static final int getDefaultMode() {
      if (PersistentContent.getTicket() == null && !Security.getInstance().isAddressBookExcludedFromContentProtection()) {
         return 1;
      } else {
         return Locale.getDefaultInputForSystem().getLanguage().equals("zh") ? 1 : 0;
      }
   }

   @Override
   public final boolean isSelectable() {
      return false;
   }

   @Override
   protected final boolean isClearMenuItemAllowed() {
      return false;
   }

   @Override
   protected final boolean isSymbolScreenAllowed() {
      return false;
   }

   @Override
   public final boolean isPasteable() {
      return true;
   }

   @Override
   public final boolean paste(Clipboard cb) {
      this.pasteText(cb.toString());
      return true;
   }

   @Override
   public final void select(boolean toggle) {
      super.select(false);
   }

   final void pasteText(String text) {
      if (text != null) {
         int len = text.length();

         for (int i = 0; i < len; i++) {
            char ch = text.charAt(i);
            switch (ch) {
               case '!':
                  this._bufferedString.append('\uf402');
                  break;
               case '#':
               case '*':
               case '+':
                  this._bufferedString.append(ch);
                  break;
               case ',':
                  this._bufferedString.append('\uf3fe');
                  break;
               default:
                  if (CharacterUtilities.isUpperCase(ch) || CharacterUtilities.isDigit(ch)) {
                     this._bufferedString.append(ch);
                  }
            }
         }

         if (this._bufferedString.length() != 0) {
            this._fieldState = 1;
            this._fieldStateChangeLength = 0;
            this._screen.onInputFieldNonEmpty(this._fieldState);
            this.updateField();
         }
      }
   }

   @Override
   public final void dispatchEvent(Event rEvent) {
      if (rEvent.getID() == 1004) {
         rEvent.setSource(this._source);
      }

      this._source.dispatchEvent(rEvent);
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int cursorPos = this.getCursorPosition();
      int diff = amount;
      int remaining = amount;
      if ((status & 131072) != 0 || !Trackball.isSupported()) {
         remaining = 0;
         int pos = this.calcNewPos(amount);
         if (pos == -1) {
            if (amount < 0) {
               if (PhoneUtilities.getAvailableLineIds().length > 1 && (RadioInfo.getNetworkService() & 256) == 0) {
                  this.setCursorPosition(this.getTextLength());
                  this.invalidate();
                  return amount;
               }

               return 0;
            }

            this._screen.onRollOffPhoneNumberInput();
            this.setCursorPosition(this.getTextLength());
            this.invalidate();
            return amount;
         }

         diff = pos - cursorPos;
      }

      if (this._fieldState == 2) {
         return 0;
      }

      if (this._fieldState == 0 && this.getTextLength() > 0) {
         this._fieldState = 1;
         this._fieldStateChangeLength = 0;
         this._screen.onInputFieldNonEmpty(this._fieldState);
      }

      cursorPos += diff;
      if (cursorPos > this.getTextLength()) {
         this._screen.onRollOffPhoneNumberInput();
      } else {
         if (cursorPos < 0) {
            return 0;
         }

         this.setCursorPosition(cursorPos);
         remaining = 0;
      }

      this.invalidate();
      return remaining;
   }

   private final int calcNewPos(int amount) {
      if (Math.abs(amount) > 1) {
         amount /= Math.abs(amount);
      }

      int curChar = this.getCursorPosition();
      if (amount < 0) {
         if (curChar < this._lineInfo[0]) {
            return -1;
         }

         if (this._lineInfo.length == 1 && curChar == this._lineInfo[0]) {
            return -1;
         }
      } else if (this._lineInfo.length == 1 || curChar >= this._lineInfo[this._lineInfo.length - 2]) {
         return -1;
      }

      int[] lines = new int[this._lineInfo.length + 1];
      lines[0] = 0;
      System.arraycopy(this._lineInfo, 0, lines, 1, this._lineInfo.length);
      int curLine = 0;

      while (lines[curLine] < curChar) {
         curLine++;
      }

      int newLine = curLine + amount;
      if (curChar == lines[curLine] && curLine != this._lineInfo.length) {
         return lines[newLine];
      }

      int startOfCurLine = lines[curLine - 1];
      int charCount = curChar - startOfCurLine;
      int startOfNewLine = 0;
      if (newLine > 0) {
         startOfNewLine = lines[newLine - 1];
      }

      int textLen = this.getTextLength();
      int minPos = startOfNewLine;
      int maxPos = lines[newLine];
      if (maxPos != textLen) {
         maxPos--;
      }

      Font f = this.getFont();
      String originalString = this.getAttributedText().getString();
      String text = originalString.substring(startOfCurLine, curChar);
      int dist = f.getAdvance(text);
      if (dist == 0) {
         return minPos;
      }

      if (curChar != textLen) {
         text = originalString.substring(curChar, curChar + 1);
         int extra = (int)(f.getAdvance(text) * 4598175219545276416L);
         if (amount == -1) {
            dist += extra * 3;
         } else {
            dist += extra;
         }
      }

      int newCharPos = startOfNewLine + charCount;
      if (newCharPos >= maxPos) {
         newCharPos = maxPos;
      }

      if (newCharPos + 1 > maxPos) {
         newCharPos = maxPos - 1;
      }

      String newText1 = originalString.substring(startOfNewLine, newCharPos);
      String newText2 = originalString.substring(startOfNewLine, newCharPos + 1);
      int newDist1 = f.getAdvance(newText1);
      int newDist2 = f.getAdvance(newText2);

      while (newDist2 < dist) {
         if (++newCharPos >= maxPos) {
            return maxPos;
         }

         newText1 = originalString.substring(startOfNewLine, newCharPos);
         newText2 = originalString.substring(startOfNewLine, newCharPos + 1);
         newDist1 = f.getAdvance(newText1);
         newDist2 = f.getAdvance(newText2);
      }

      while (newDist1 > dist) {
         if (--newCharPos <= 0) {
            return 0;
         }

         newText1 = originalString.substring(startOfNewLine, newCharPos);
         newText2 = originalString.substring(startOfNewLine, newCharPos + 1);
         newDist1 = f.getAdvance(newText1);
         newDist2 = f.getAdvance(newText2);
      }

      if (newCharPos < startOfNewLine) {
         newCharPos = startOfNewLine;
      }

      if (newCharPos < 0) {
         newCharPos = 0;
      }

      if (newCharPos > maxPos) {
         newCharPos = maxPos;
      }

      return newCharPos;
   }

   final boolean isValidInput() {
      return this._fieldState != 2 && this.getTextLength() > 0 && this._delayingEchoId == -1;
   }

   @Override
   public final boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return false;
   }

   final void echoInitialKey(char key) {
      this._fieldState = getDefaultMode();
      this._keyEventInfo.setInfo(key, this._fieldState);
      if (this._keyEventInfo.getResultState() != -1) {
         if (this._fieldState != 1) {
            this._source.processKeyEvent(515, key, Keypad.keycode(CharacterUtilities.toUpperCase(key), 0), 0);
         }

         char tempKey = this._keyEventInfo.getEchoKey();
         if (!CharacterUtilities.isISOControl(tempKey)) {
            this._screen.onInputFieldNonEmpty(this._keyEventInfo.getResultState());
         }

         this.echo(tempKey, Keypad.keycode(key, 0));
      }
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      this._keyEventInfo.setInfo(Keypad.map(keycode), this._fieldState);
      if (this._keyEventInfo.getResultState() != -1 && (Keypad.map(keycode) != 137 || event != 513)) {
         if (this._delayingEchoId == -1 && this.getTextLength() < 80 && this._fieldState != 1) {
            super.processKeyEvent(event, key, keycode & -2, time);
         }

         return Keypad.map(keycode);
      } else {
         return key | 65536;
      }
   }

   final boolean repostProcessKeyEvent(int event, char key, int keycode, int time) {
      this._keyEventInfo.setInfo(Keypad.map(keycode), this._fieldState);
      switch (event) {
         case 513:
            return this.keyDown(keycode, time);
         case 514:
            return this.keyRepeat(keycode, time);
         case 515:
            return this.keyUp(keycode, time);
         case 520:
            return this.keyStatus(keycode, time);
         default:
            return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return true;
   }

   @Override
   protected final boolean keyControl(char key, int status, int time) {
      return false;
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      this._ignoreKeyRepeat = false;
      if (this._ignoreKeyUp) {
         this._ignoreKeyUp = false;
         return true;
      }

      switch (Keypad.key(keycode)) {
         case 10:
         case 17:
            if (this._fieldState != 2) {
               this.pasteText(PhoneUtilities.getLastNumberDialed());
            }

            return true;
         case 4098:
            return false;
         default:
            char key = Keypad.map(keycode);
            switch (key) {
               case '\u0000':
               case '\u001b':
                  return true;
               case '\b':
               case '\u007f':
                  if (this._bufferedString.length() == 0) {
                     return true;
                  }

                  if (this._bufferedString.length() != 1 && (this._fieldState != 2 || this.getTextLength() != 1)) {
                     int cursorPos = this.getCursorPosition();
                     if (this._fieldState == 1) {
                        if (cursorPos > 0) {
                           cursorPos--;
                        }

                        this._bufferedString.deleteCharAt(cursorPos);
                     } else {
                        this._bufferedString.deleteCharAt(this._bufferedString.length() - 1);
                     }

                     if (this._bufferedString.length() == this._fieldStateChangeLength) {
                        this._fieldState = getDefaultMode();
                        this._fieldStateChangeLength = -1;
                     }

                     this.updateField();
                     if (this._fieldState == 1 && cursorPos < this._bufferedString.length()) {
                        this.setCursorPosition(cursorPos);
                     }

                     this._screen.onInputFieldNonEmpty(this._fieldState);
                     return true;
                  }

                  this.clear(true);
                  return true;
               case '$':
               case '£':
               case '¤':
               case '¥':
               case '€':
                  return true;
               case '\u0080':
                  if (!_isReducedKeyboard) {
                     return true;
                  }
            }

            if (this.getTextLength() >= 80) {
               this.displayFieldFullMessage();
               return true;
            } else {
               this.echo(this._keyEventInfo.getEchoKey(), keycode);
               this._screen.onInputFieldNonEmpty(this._fieldState);
               if (this._resetAltMode) {
                  this._keypadAltMode = false;
                  this._resetAltMode = false;
                  this.invalidate();
               }

               if (key == '#' || Keypad.getAltedChar(key) == '#') {
                  String number = this.getText();
                  if (GSM230Filter.getCode(number) != -1) {
                     Application.getApplication().invokeLater(new PhoneNumberInput$1(this, number));
                     this.clear(true);
                  }
               }

               return true;
            }
      }
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      if (this._ignoreKeyRepeat) {
         return this.getTextLength() > 0;
      } else {
         this._ignoreKeyRepeat = true;
         char key = Keypad.map(keycode);
         if (_isReducedKeyboard && key == ' ') {
            this._ignoreKeyUp = true;
            this.echo('+', 0);
            this._fieldState = 1;
            return true;
         } else {
            return this.getTextLength() > 0;
         }
      }
   }

   final boolean keyClickedAndHeld(int keycode) {
      return this.getTextLength() > 0;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (!this.isValidInput() || key != 10 && key != 17) {
         if (key == 18 || key == 27) {
            this._keypadAltMode = false;
            if (this.getTextLength() > 0) {
               this.clear(true);
               return true;
            }
         }

         if (_isReducedKeyboard) {
            char testKey = Keypad.map(keycode);
            if (testKey == '#' || Keypad.getAltedChar(testKey) == '#') {
               return true;
            }
         }

         return false;
      } else {
         String number = this.getText();
         PhoneUtilities.setLastNumberDialed(number);
         PhoneLogger.log("startcall raw num");
         this._ignoreKeyUp = true;
         new DialVerb(number, null).invoke(null);
         return true;
      }
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      boolean altMode = (Keypad.status(keycode) & 1) != 0;
      if (this._keypadAltMode && !altMode) {
         this._resetAltMode = true;
         return true;
      }

      if (this._keypadAltMode != altMode) {
         this._keypadAltMode = altMode;
         this.invalidate();
      }

      return true;
   }

   @Override
   protected final void paint(Graphics graphics) {
      int length = this.getTextLength();
      if (length != 0) {
         String text = this.getText();
         Image enterImage = this.getReturnKeyImage();
         int availableWidth = this.getAvailableWidth(this._fontIndex);
         int fontHeight = this._inputFonts[this._fontIndex].getHeight();
         int imageHeight = enterImage.getHeight(fontHeight + 5, fontHeight + 5);
         int lineHeight = this._lineInfo.length == 1 ? this._inputFonts[0].getHeight() : fontHeight;
         lineHeight = Math.max(lineHeight, imageHeight);
         int x = 0;
         int y = 0;
         int pos = 0;
         int line = 0;

         while (pos < length) {
            int indexOfPause = text.indexOf(62462, pos);
            int indexOfWait = text.indexOf(62466, pos);
            int special = Math.min(indexOfPause, indexOfWait);
            if (special == -1) {
               special = Math.max(indexOfPause, indexOfWait);
            }

            if (special != -1 && special < this._lineInfo[line]) {
               x += graphics.drawText(text, pos, special - pos, x, y, 0, availableWidth);
               x += this.drawSpecialCharacter(graphics, x, y, special);
               pos = special + 1;
            } else {
               x += graphics.drawText(text, pos, this._lineInfo[line] - pos, x, y, 0, availableWidth);
               pos = this._lineInfo[line];
               if (length > this._lineInfo[line]) {
                  x = 0;
                  y += lineHeight;
                  line++;
               }
            }
         }

         int cursorPos = this.getCursorPosition();
         if (cursorPos >= length) {
            graphics.fillRect(x + 1, y, 3, this._inputFonts[this._fontIndex].getHeight());
         } else {
            int cursorX = 0;
            int cursorY = 0;
            int cursorWidth = 0;
            boolean specialCursor = text.charAt(cursorPos) == '\uf3fe' || text.charAt(cursorPos) == '\uf402';
            int offset = 0;
            int cursorLine = 0;

            while (cursorLine < this._lineInfo.length && cursorPos >= this._lineInfo[cursorLine]) {
               cursorLine++;
            }

            cursorY = lineHeight * cursorLine;
            if (cursorLine != 0) {
               offset = this._lineInfo[cursorLine - 1];
            }

            cursorX = this.getAdvance(this._inputFonts[this._fontIndex], offset, cursorPos - offset) + 1;
            cursorWidth = this.getAdvance(this._inputFonts[this._fontIndex], cursorPos, 1);
            if (specialCursor) {
               graphics.invert(cursorX, cursorY, cursorWidth, fontHeight);
            } else {
               this.drawHighlightRegion(graphics, 2, true, cursorX, cursorY, cursorWidth, fontHeight);
            }
         }

         if (this._lineInfo.length == 1) {
            y = this._inputFonts[this._fontIndex].getHeight() - imageHeight >> 1;
         }

         if (this.getTextLength() > 0 && this._fieldState != 2 && this.isFocus()) {
            enterImage.paint(graphics, x + 4, y, enterImage.getWidth(fontHeight + 5, fontHeight + 5), imageHeight);
         }
      }
   }

   private final int drawSpecialCharacter(Graphics graphics, int x, int y, int offset) {
      char specialChar = this.getText().charAt(offset);
      x++;
      int width;
      if (specialChar == '\uf3fe') {
         width = PhoneResources.drawIcon(graphics, x, y, 8);
      } else {
         width = PhoneResources.drawIcon(graphics, x, y, 9);
      }

      return width;
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (this.getTextLength() == 0) {
         int width = graphics.getFont().getAdvance(' ');
         if (on) {
            graphics.fillRect(2, 0, width, this.getHeight());
            return;
         }

         graphics.clear(2, 0, width, this.getHeight());
      }
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      this.setCursorPosition(this.getTextLength());
      this.invalidate();
   }

   final void clear(boolean notifyScreen) {
      if (this._delayingEchoId != -1) {
         this._app.cancelInvokeLater(this._delayingEchoId);
         this._delayingEchoId = -1;
      }

      super.clear(0);
      this._bufferedString.setLength(0);
      this._bufferedString.setLength(this._bufferedString.capacity());
      this._bufferedString.delete(0, this._bufferedString.length());
      this.getAttributedText().getText().wipe();
      this.setFont(null);
      this._fontIndex = 0;
      this.setPadding(0, 0, 0, 0);
      Array.resize(this._lineInfo, 1);
      this._lineInfo[0] = 0;
      this._fieldState = getDefaultMode();
      this._source.clear(0);
      this._ignoreKeyRepeat = false;
      this._ignoreKeyUp = false;
      if (notifyScreen) {
         this._screen.onInputFieldCleared();
      }
   }

   private final void updateText() {
      switch (this._fieldState) {
         case 0:
         case 1:
         default:
            this.setText(this._bufferedString.toString());
            return;
         case 2:
            this.setText(this._source.getText());
         case -1:
      }
   }

   @Override
   protected final void layout(int width, int height) {
      if (this.getTextLength() == 0) {
         height = this.getFont().getHeight();
      } else if (this._lineInfo.length == 1) {
         height = this._inputFonts[0].getHeight() - this.getPaddingTop() - this.getPaddingBottom();
      } else {
         int fontHeight = this._inputFonts[this._fontIndex].getHeight();
         int imageHeight = this.getReturnKeyImage().getHeight(fontHeight + 5, fontHeight + 5);
         height = this._lineInfo.length * Math.max(fontHeight, imageHeight);
      }

      this.setExtent(Display.getWidth(), height);
   }

   private final void echoTone(char tone) {
      if (!PhoneUtilities.isDiscreetProfileOn() && !PhoneUtilities.isQuietProfileOn()) {
         DTMFEcho.echoTone(tone);
      }
   }

   private final synchronized boolean delayEcho(char key, int pos) {
      if (this._delayingEchoId != -1) {
         this._app.cancelInvokeLater(this._delayingEchoId);
      }

      if (this._delayingEchoId != -1 && this._bufferedString.charAt(pos) != key) {
         this._delayingEchoId = -1;
         if (this.isValidInput()) {
            this.echoTone(this._bufferedString.charAt(pos));
            this.setCursorPosition(pos + 1);
         }
      }

      StringBuffer sb = Keypad.getLayout().getComplementaryChars(CharacterUtilities.toLowerCase(key), 0);
      if (sb != null) {
         if (sb.length() == 1) {
            this._delayingEchoId = -1;
            return false;
         }

         if (sb.length() > 1) {
            if (this._delayingEchoId != -1) {
               this._delayingEchoId = -1;
               key = CharacterUtilities.toUpperCase(sb.charAt(sb.length() - 1));
               this._bufferedString.setCharAt(pos, key);
               this.echoTone(key);
               this.updateField();
               this.setCursorPosition(pos + 1);
               return true;
            }

            this._delayingEchoId = this._app.invokeLater(this._delayEchoToneRunnable, 750, false);
            return false;
         }
      }

      return false;
   }

   private final void echo(char key, int keycode) {
      if (this._keyEventInfo.getResultState() != -1) {
         if (this._keyEventInfo.getResultState() != this._fieldState) {
            this._fieldState = this._keyEventInfo.getResultState();
            this._fieldStateChangeLength = this.getTextLength();
         }

         int cursorPos = this.getCursorPosition();
         if (this._delayingEchoId != -1 || _isReducedKeyboard && this._keypadAltMode && !this._resetAltMode && this._keyEventInfo.getResultState() != 2) {
            if (this.delayEcho(key, cursorPos)) {
               return;
            }

            cursorPos = this.getCursorPosition();
         }

         if (this._fieldState == 1) {
            this._bufferedString.insert(cursorPos, key);
         } else {
            this._bufferedString.append(key);
         }

         this.updateField();
         if (this.isValidInput()) {
            this.echoTone(key);
         }

         if (this._keypadAltMode && !this._resetAltMode && this._delayingEchoId != -1) {
            this.setCursorPosition(cursorPos);
         } else {
            if (this.getTextLength() > cursorPos + 1 && this._fieldState == 1) {
               this.setCursorPosition(cursorPos + 1);
            }
         }
      }
   }

   final void updateField() {
      this.updateText();
      this.updateFontSize();
      this.updateLineLengths();
      this.invalidate();
   }

   private final void updateFontSize() {
      String text = this.getText();
      int length = this.getTextLength();
      boolean updateFont = this.getFont() != this._inputFonts[this._fontIndex];
      if (text != null && length > 7) {
         while (true) {
            int width = this.getAdvance(this._inputFonts[this._fontIndex], 0, length);
            int widthAvailable = this.getAvailableWidth(this._fontIndex);
            if (width <= widthAvailable) {
               if (width < widthAvailable && this._fontIndex != 0) {
                  width = this.getAdvance(this._inputFonts[this._fontIndex - 1], 0, length);
                  widthAvailable = this.getAvailableWidth(this._fontIndex - 1);
                  if (width < widthAvailable) {
                     this._fontIndex--;
                     updateFont = true;
                  }
               }
               break;
            }

            if (this._inputFonts[this._fontIndex].getHeight() <= MIN_FONT_HEIGHT) {
               break;
            }

            this._fontIndex++;
            updateFont = true;
            if (this._fontIndex == this._inputFonts.length) {
               Array.resize(this._inputFonts, this._fontIndex + 1);
               this._inputFonts[this._fontIndex] = this.getAdjustedFont(this._fontIndex - 1);
            }
         }
      } else if (this._fontIndex != 0) {
         this._fontIndex = 0;
         updateFont = true;
      }

      if (updateFont) {
         this.setFont(this._inputFonts[this._fontIndex]);
         int padding = this._inputFonts[0].getHeight() - this._inputFonts[this._fontIndex].getHeight() >> 1;
         this.setPadding(padding, 0, padding, 0);
         this.updateLayout();
      }
   }

   private final void updateLineLengths() {
      int length = this.getTextLength();
      if (this._inputFonts[this._fontIndex].getHeight() > MIN_FONT_HEIGHT) {
         Array.resize(this._lineInfo, 1);
         this._lineInfo[0] = length;
      } else {
         int widthAvailable = this.getAvailableWidth(this._fontIndex);
         int totalWidth = 0;
         int line = 0;

         for (int i = 0; i < length; i++) {
            int width = this.getAdvance(this._inputFonts[this._fontIndex], i, 1);
            totalWidth += width;
            if (totalWidth > widthAvailable) {
               this._lineInfo[line] = i;
               if (++line == this._lineInfo.length) {
                  Array.resize(this._lineInfo, line + 1);
                  this.setPadding(0, 0, 0, 0);
                  this.updateLayout();
               }

               totalWidth = width;
            }
         }

         this._lineInfo[line] = length;
         if (this._lineInfo.length > line + 1) {
            Array.resize(this._lineInfo, line + 1);
            if (this._lineInfo.length == 1) {
               int padding = this._inputFonts[0].getHeight() - this._inputFonts[this._fontIndex].getHeight() >> 1;
               this.setPadding(padding, 0, padding, 0);
               this.updateLayout();
            }
         }
      }
   }

   private final int getAvailableWidth(int fontIndex) {
      int fontImageHeight = this._inputFonts[fontIndex].getHeight() + 5;
      return INPUT_SCREEN_WIDTH - this.getReturnKeyImage().getWidth(fontImageHeight, fontImageHeight) - 4;
   }

   private final Font getAdjustedFont(int index) {
      Font currentFont = this._inputFonts[index];
      int fontHeight = currentFont.getHeight() * 90 / 100 - 1;
      return currentFont.derive(currentFont.getStyle(), fontHeight);
   }

   private final int getAdvance(Font font, int offset, int length) {
      int endIndex = offset + length;
      int totalWidth = 0;
      AttributedString text = super.getAttributedText();

      for (int i = offset; i < endIndex; i++) {
         char testChar = text.getText().charAt(i);
         if (testChar == '\uf3fe') {
            totalWidth += PhoneResources.getIconWidth(font, 8);
         } else if (testChar == '\uf402') {
            totalWidth += PhoneResources.getIconWidth(font, 9);
         } else {
            totalWidth += font.getAdvance(testChar);
         }
      }

      return totalWidth;
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (!visible) {
         if (Backlight.isEnabled()) {
            this.clear(true);
         }
      } else if (this.getTextLength() == 0) {
         this._fieldState = getDefaultMode();
         this._screen.onInputFieldCleared();
      }

      super.onVisibilityChange(visible);
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      if (this.isPasteable() && Clipboard.getClipboard().get() != null) {
         MenuItem pasteItem = MenuItem.getPrefab(3);
         contextMenu.addItem(pasteItem);
         Clipboard clip = Clipboard.getClipboard();
         if (clip.isNotYetPasted() && clip.isTimeForPasteAsDefaultNotPassed()) {
            contextMenu.setDefaultItem(pasteItem);
         }
      }
   }

   final void addToMenu(SystemEnabledMenu menu, int instance, boolean systemLocked, boolean outgoingCallAllowed) {
      if (instance == 0 && !systemLocked && outgoingCallAllowed) {
         menu.add(new SpeedDialVerb(null, 6094, 1397760, '\u0000'));
      }

      if (this.isValidInput()) {
         Verb[] verbs = new Verb[0];
         verbs = _rawPhoneNumberRepository.getVerbs(null);
         ContextObject context = _rawPhoneNumberInputContextWR.getContextObject();
         context.put(253, this.getText());
         Object model = PhoneUtilities.createNumberModel(this.getText());
         context.put(247, model);
         PhoneUtilities.setPrivateFlag(context, 75);
         if (!systemLocked && model != null && instance == 0) {
            if (outgoingCallAllowed) {
               menu.add(new SpeedDialVerb(model, 6072, 1332244, '\u0000'));
            }

            context.setFlag(34);
            context.putIntegerData(1);
            context.put(254, model);
            model = FactoryUtil.createInstance(3797587162219887872L, context);
            if (model instanceof ConversionProvider) {
               ConversionProvider converter = (ConversionProvider)model;
               String[] names = new String[2];
               converter.convert(new ContextObject(10), names);
               if (names[1] == null || names[1].length() == 0) {
                  Verb addToAddressBookVerb = AddressBookServices.getAddToAddressBookVerb();
                  context.put(254, model);
                  menu.add(new WrapperVerb(addToAddressBookVerb, context, 16867328));
               }
            }
         }

         Verb defaultVerb = null;
         Verb[] trimmedVerbs = new Verb[0];
         int index = 0;
         Recognizer recognizer = DialVerb.getRecognizer();

         for (int i = 0; i < verbs.length; i++) {
            Verb verb = verbs[i];
            if ((!systemLocked || recognizer.recognize(verb))
               && !(verb instanceof ApplicationKeyInvocableVerb)
               && (!(verb instanceof ConditionalVerb) || ((ConditionalVerb)verb).canInvoke(context))) {
               if (!(verb instanceof Copyable)) {
                  Array.resize(trimmedVerbs, trimmedVerbs.length + 1);
                  trimmedVerbs[index++] = verb;
               } else {
                  Verb verbCopy = (Verb)((Copyable)verb).copy();
                  if (verbCopy instanceof SetParameter) {
                     ((SetParameter)verbCopy).setParameter(context);
                  }

                  if (DialVerb.getRecognizer().recognize(verbCopy)) {
                     defaultVerb = verbCopy;
                  }

                  Array.resize(trimmedVerbs, trimmedVerbs.length + 1);
                  trimmedVerbs[index++] = verbCopy;
               }
            }
         }

         menu.add(trimmedVerbs);
         menu.setDefault(defaultVerb);
         menu.add(new PhoneNumberInput$AddDelayVerb(this, 1));
         menu.add(new PhoneNumberInput$AddDelayVerb(this, 0));
      }
   }

   @Override
   public final void inHolster() {
      this.clear(false);
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 2) {
         this.clear(false);
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      if (PersistentContent.isEncryptionEnabled()) {
         this.wipe();
      }
   }

   static final void access$500(PhoneNumberInput x0) {
      x0.invalidate();
   }
}
