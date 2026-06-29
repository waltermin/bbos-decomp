package net.rim.device.api.ui.component;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.MissingResourceException;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;

public class AutoTextEditField extends EditField {
   private boolean _keyRepeatProcessed;
   private boolean _processingKeyHeldWhileRolling;
   private int[] _bookmarks = new int[]{-1, -1, -1, 1866989824, 727916, 1287875073, 1918312329, 1394831384, 1930628195, 1064304896, 1936682051, 1879113763};
   private short _lastAutoTextAction = 0;
   private short _lastAutoPeriodAction = 0;
   private short _lastAutoCapAction = 0;
   private String _replacedString;
   private String _replacementString;
   private char _lastAutoCapChar;
   private int _pendingAutoCapBookmark = -1;
   private short _pendingAutoCapAction = 0;
   private StringBuffer _deletedText = new StringBuffer();
   private int _leadingBackspaceCount;
   private int _deleteCount;
   private boolean _shiftPressed;
   private String _autoQuoteOpen;
   private String _autoQuoteClosed;
   private int _cachedLocaleCode;
   private static final long AUTOREPLACE_MASK = 196608L;
   public static final long AUTOREPLACE = 65536L;
   public static final long AUTOREPLACE_OFF = 131072L;
   private static final long AUTOCAP_MASK = 786432L;
   public static final long AUTOCAP = 262144L;
   public static final long AUTOCAP_OFF = 524288L;
   private static final long AUTOPERIOD_MASK = 1048576L;
   private static final long AUTOPERIOD = 1048576L;
   public static final long AUTOPERIOD_OFF = 2097152L;
   private static final long AUTOQUOTE_MASK = 12582912L;
   private static final long AUTOQUOTE = 4194304L;
   public static final long AUTOQUOTE_OFF = 8388608L;
   private static final short BOOKMARK_AUTOTEXT = 0;
   private static final short BOOKMARK_AUTOPERIOD = 1;
   private static final short BOOKMARK_AUTOCAP = 2;
   private static final short ACTION_NONE = 0;
   private static final short ACTION_REPLACE = 1;
   private static final short ACTION_UNDO = 2;
   private static AutoText _autoTextEngine = AutoText.getAutoText();
   private static String CLAUSE_SEPARATORS = _autoTextEngine.getClauseSeparatorString();
   private static String AUTOCAP_DELIMITERS = " \t\"'(){}[]<>«»“”";
   private static String AUTOLIST_CONTENT = " \t(){}[]<>0123456789.,ivxIVX";
   private static String AUTOLIST_DELIMITERS = ")}]>.,";
   private static String AUTOLIST_SPACE = " \t";
   private static ResourceBundleFamily _inputFamily = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input");

   public AutoTextEditField() {
      this(null, null, 1000000, 4503599627370496L);
   }

   public AutoTextEditField(String label, String initialValue) {
      this(label, initialValue, 1000000, 4503599627370496L);
   }

   public AutoTextEditField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, validateStyle(style));
      this.checkLocale();
   }

   @Override
   protected boolean backspace() {
      if (this.isSelecting()) {
         return super.backspace();
      }

      if (this.isPreviousCharacterAtBookmark(0)) {
         if (this._lastAutoTextAction != 2 && this.undoAutoText()) {
            return true;
         }
      } else if (this.isPreviousCharacterAtBookmark(1)) {
         if (this._lastAutoPeriodAction != 2) {
            this._bookmarks[1] = this.getCaretPosition() - 1;
            this._lastAutoPeriodAction = 2;
         } else {
            this.resetBookmark(1);
         }
      } else if (this.isPreviousCharacterAtBookmark(2) && this._lastAutoCapAction != 2) {
         this._pendingAutoCapBookmark = this.getCaretPosition() - 1;
         this._pendingAutoCapAction = 2;
      }

      return super.backspace();
   }

   private synchronized boolean handleClauseSeparator(char character) {
      if (character == ' ' && this._shiftPressed) {
         return true;
      } else if (character == ' ' && this.getCaretPosition() > 0 && this.getAttributedText().charAt(this.getCaretPosition() - 1) == ' ') {
         this.handleAutoPeriod(character);
         return true;
      } else {
         return this.handleAutoText(character) && this.handleAutoQuote(character);
      }
   }

   @Override
   protected boolean insert(char key, int status) {
      if (this.isSelecting()) {
         this.selectionDelete();
      }

      boolean performInsert = true;
      if (_autoTextEngine.isClauseSeparator(key)) {
         performInsert = this.handleClauseSeparator(key);
      } else if (Character.isLowerCase(key) && !this._processingKeyHeldWhileRolling) {
         key = this.handleLowerCaseCharacter(key);
      }

      if (this._bookmarks[2] != -1 && this._bookmarks[2] == this.getCaretPosition() && this._lastAutoCapAction == 2) {
         this.resetBookmark(2);
      }

      if (performInsert) {
         return super.insert(key, status);
      }

      this.fieldChangeNotify(0);
      return false;
   }

   private boolean isAutoCapLocation() {
      if (this.getCursorPosition() == 0) {
         return true;
      }

      int prevNotIgnoredCharOffset = this.previousIndexOf(AUTOCAP_DELIMITERS, false);
      char prevNotIgnoredChar = 0;
      if (prevNotIgnoredCharOffset >= this.getLabelLength()) {
         prevNotIgnoredChar = this.getAttributedText().charAt(prevNotIgnoredCharOffset);
      }

      switch (prevNotIgnoredChar) {
         case '\n':
         case '\u2029':
            return true;
         default:
            if (prevNotIgnoredCharOffset < this.getCaretPosition() - 1 && _autoTextEngine.isSentenceTerminator(prevNotIgnoredChar)) {
               return true;
            } else {
               int endListIndex = this.previousIndexOf(AUTOLIST_SPACE, false);
               if (endListIndex > this.getLabelLength()) {
                  boolean isDelimiter = AUTOLIST_DELIMITERS.indexOf(this.getAttributedText().charAt(endListIndex)) >= 0;
                  if (isDelimiter) {
                     int index = this.previousIndexOf(AUTOLIST_CONTENT, false);
                     char startList = index >= 0 ? this.getAttributedText().charAt(index) : '\u0000';
                     switch (startList) {
                        case '\u0000':
                        case '\n':
                        case '\u2029':
                           return true;
                     }
                  }
               }

               return false;
            }
      }
   }

   private boolean isAutoCapsOn() {
      return !this.isStyle(524288);
   }

   private boolean isAutoPeriodOn() {
      return !this.isStyle(2097152);
   }

   private boolean isAutoQuoteOn() {
      return false;
   }

   private boolean isAutoTextOn() {
      return !this.isStyle(131072);
   }

   private boolean isPreviousCharacterAtBookmark(int bookmarkIndex) {
      return this._bookmarks[bookmarkIndex] != -1 && this.getCaretPosition() - 1 == this._bookmarks[bookmarkIndex];
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      this._shiftPressed = (status & 4) == 0 && (status & 2) != 0;
      if (this.isStyle(2147483648L) && key == '\n') {
         this.handleClauseSeparator(key);
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected int replace(
      int aStart,
      int aEnd,
      AttributedString$Iterator aIterator,
      long aIteratorMask,
      long aIteratorXMask,
      int aCommittedLen,
      int aPosInsideComposedText,
      boolean aMoveCursor,
      int aContext
   ) {
      if (aContext == 0 && aCommittedLen == 1 && aIterator.length() == 1 && this.getComposedTextEnd() == this.getComposedTextStart() && this._bookmarks != null
         )
       {
         this.keyChar(aIterator.text().charAt(aIterator.pos()), 0, 0);
         return 0;
      } else {
         return super.replace(aStart, aEnd, aIterator, aIteratorMask, aIteratorXMask, aCommittedLen, aPosInsideComposedText, aMoveCursor, aContext);
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      this._keyRepeatProcessed = false;
      return super.keyDown(keycode, time);
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      boolean result = false;
      char charkey = Keypad.map(keycode);
      if (this.isEnteringRollerCharacter() || !this.isEditable()) {
         result = super.keyRepeat(keycode, time);
      } else if (charkey == '\n' || charkey == '\b') {
         result = super.keyRepeat(keycode, time);
      } else if (Character.isLowerCase(charkey) || Character.isUpperCase(charkey)) {
         if (SymbolScreen.contains(this.getLastKeyPressed())) {
            this.insert((char)this.getLastKeyPressed(), 0);
            result = true;
         } else if (!this._keyRepeatProcessed) {
            if (Character.isLowerCase(charkey)) {
               charkey = Character.toUpperCase(charkey);
            }

            this.backspace();
            this.insert(charkey, 0);
         }

         result = true;
      }

      this._keyRepeatProcessed = true;
      return result;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected int moveFocus(int amount, int status, int time) {
      int ret = 0;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         if ((status & 8) != 0) {
            this._processingKeyHeldWhileRolling = true;
         }

         ret = super.moveFocus(amount, status, time);
         var7 = false;
      } finally {
         if (var7) {
            this._processingKeyHeldWhileRolling = false;
         }
      }

      this._processingKeyHeldWhileRolling = false;
      return ret;
   }

   @Override
   protected void setText(String text, int context) {
      this.resetBookmark(0);
      this.resetBookmark(1);
      this.resetBookmark(2);
      super.setText(text, context);
   }

   private void handleAutoPeriod(char character) {
      if (this.isAutoPeriodOn() && InputContext.getInstance().isAutoPeriodOn()) {
         int prevNonSpaceOffset = this.getPrevNonSpaceCharOffset();
         char prevNonSpaceChar = prevNonSpaceOffset == -1 ? '\u0000' : this.getAttributedText().charAt(prevNonSpaceOffset);
         int offset = prevNonSpaceOffset == -1 ? this.getLabelLength() : prevNonSpaceOffset + 1;
         if (this._bookmarks[1] == -1 || this._lastAutoPeriodAction != 2 || this._bookmarks[1] != offset) {
            if (!_autoTextEngine.isNoAutoPeriodCharacter(prevNonSpaceChar)) {
               FieldChangeListener oldListener = this.getChangeListener();
               this.setChangeListener(null);
               int periodOffset = offset;
               this.getAttributedText().delete(periodOffset, periodOffset + 1);
               this.getAttributedText().insert(periodOffset, this.getPeriodSymbol());
               this._bookmarks[1] = periodOffset;
               this._lastAutoPeriodAction = 1;
               this.setChangeListener(oldListener);
            }
         }
      }
   }

   private char getPeriodSymbol() {
      SLControlObject cObj = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      return cObj.getPeriodSymbol();
   }

   private boolean handleAutoQuote(char character) {
      this.checkLocale();
      if (!this.isAutoQuoteOn()) {
         return true;
      }

      if (character != '"') {
         return true;
      }

      boolean opening = false;
      char prevChar = this.getCaretPosition() > 0 ? this.getAttributedText().charAt(this.getCaretPosition() - 1) : '\u0000';
      switch (prevChar) {
         case '\u0000':
         case '\n':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case '\u200b':
         case '\u200c':
         case '\u200d':
         case '\u2028':
         case '\u2029':
         case '　':
            opening = true;
         default:
            String replacement = opening ? this._autoQuoteOpen : this._autoQuoteClosed;
            this.replace("", replacement);
            return false;
      }
   }

   private boolean handleAutoText(char character) {
      if (!this.isAutoTextOn()) {
         return true;
      }

      if (this.isPreviousCharacterAtBookmark(0) && this._lastAutoTextAction == 2) {
         return true;
      }

      int previousSeparatorOffset = this.previousIndexOf(CLAUSE_SEPARATORS, true) + 1;
      if (previousSeparatorOffset == 0) {
         previousSeparatorOffset = this.getLabelLength();
      }

      if (previousSeparatorOffset == this.getCaretPosition()) {
         return true;
      }

      boolean insertTriggerCharacter = true;
      String replaceString = this.getAttributedText().getText(previousSeparatorOffset, this.getCaretPosition());
      Object autoTextEntry = _autoTextEngine.checkWord(replaceString);
      if (autoTextEntry != null) {
         String replacementString = this.replaceMacros(_autoTextEngine.getReplacementStringPattern(autoTextEntry));
         if (replaceString.equals(replacementString) || replacementString.length() == 0) {
            return insertTriggerCharacter;
         }

         int replaceStringStart = previousSeparatorOffset;
         if (this._leadingBackspaceCount > 0) {
            replaceStringStart -= this._leadingBackspaceCount;
            if (replaceStringStart < this.getLabelLength()) {
               replaceStringStart = this.getLabelLength();
            }

            replaceString = this.getAttributedText().getText(replaceStringStart, this.getCaretPosition());
            this._leadingBackspaceCount = 0;
         }

         replacementString = this.adjustCase(replacementString, replaceString, _autoTextEngine.getReplacementCase(autoTextEntry));
         int newLength = this.getTextLength() - replaceString.length() + replacementString.length();
         if (newLength >= this.getMaxSize()) {
            this.displayFieldFullMessage();
            return false;
         }

         replacementString = this.replace(replaceString, replacementString);
         if (this._deleteCount > 0) {
            if (this._deletedText.length() == this._deleteCount) {
               this._deletedText.setLength(this._deleteCount - 1);
            }

            while (this._deleteCount > 1) {
               this.selectionDelete();
               this._deleteCount--;
            }

            this._deletedText.insert(0, character);
            this._deletedText.insert(0, replaceString);
            replaceString = this._deletedText.toString();
            this._deletedText.setLength(0);
            this._deleteCount = 0;
            insertTriggerCharacter = false;
         }

         this._replacedString = replaceString;
         this._replacementString = replacementString;
         this._bookmarks[0] = this.getCaretPosition() - 1;
         this._lastAutoTextAction = 1;
      }

      return insertTriggerCharacter;
   }

   private String checkEntry(String original, int macroIndex) {
      if (original.charAt(macroIndex - 1) == '\'' && original.charAt(macroIndex + 1) == 'b') {
         StringBuffer temp = new StringBuffer(original);
         temp.setCharAt(macroIndex + 1, 'B');
         return temp.toString();
      } else {
         return original;
      }
   }

   private String replaceMacros(String text) {
      int macroIndex = text.indexOf(37);
      if (macroIndex == -1) {
         return text;
      }

      if (macroIndex > 0 && text.length() > macroIndex + 1) {
         text = this.checkEntry(text, macroIndex);
      }

      StringBuffer newText = new StringBuffer(text.substring(0, macroIndex));
      this._leadingBackspaceCount = 0;
      this._deleteCount = 0;
      int textLength = text.length();

      for (int i = macroIndex; i < textLength; i++) {
         char c = text.charAt(i);
         if (c == '%' && i < textLength - 1) {
            switch (text.charAt(i + 1)) {
               case '%':
                  newText.append('%');
                  i++;
                  break;
               case 'B':
                  if (this.getCaretPosition() + this._deleteCount < this.getDisplayTextLength()) {
                     this._deletedText.append(this.getAttributedText().charAt(this.getCaretPosition() + this._deleteCount));
                  }

                  this._deleteCount++;
                  i++;
                  break;
               case 'D':
                  DateFormat.getInstance(40).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               case 'O':
                  newText.append(Owner.getOwnerInfo());
                  i++;
                  break;
               case 'P':
                  int pin = DeviceInfo.getDeviceId();
                  newText.append(Integer.toHexString(pin));
                  i++;
                  break;
               case 'T':
                  DateFormat.getInstance(5).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               case 'U':
                  if (DirectConnect.isSupported()) {
                     newText.append(DirectConnect.getUFMI());
                     i++;
                  } else {
                     newText.append(c);
                  }
                  break;
               case 'V':
                  String insertion = DeviceInfo.getDeviceName() + '/' + ApplicationDescriptor.currentApplicationDescriptor().getVersion();
                  newText.append(insertion);
                  i++;
                  break;
               case 'b':
                  int newTextLength = newText.length();
                  if (newTextLength > 0) {
                     newText.deleteCharAt(newTextLength - 1);
                  } else {
                     this._leadingBackspaceCount++;
                  }

                  i++;
                  break;
               case 'd':
                  DateFormat.getInstance(56).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               case 'o':
                  newText.append(Owner.getOwnerName());
                  i++;
                  break;
               case 'p':
                  try {
                     Phone phone = Phone.getInstance();
                     int line = phone.getAlternateLine(-1);
                     String phoneNumber = phone.getAlternateLineNumber(line);
                     if (phoneNumber != null) {
                        newText.append(phoneNumber);
                     }
                  } catch (Exception var11) {
                  }

                  i++;
                  break;
               case 't':
                  DateFormat.getInstance(7).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               default:
                  newText.append(c);
            }
         } else {
            newText.append(c);
         }
      }

      return newText.toString();
   }

   private static boolean isAllUpperCase(String s) {
      int len = s.length();
      if (len == 0 || Character.isLowerCase(s.charAt(0)) || Character.isDigit(s.charAt(0))) {
         return false;
      } else {
         return Character.isLowerCase(s.charAt(len - 1)) ? false : s.equals(s.toUpperCase());
      }
   }

   private String adjustCase(String replacement, String original, int caseType) {
      if (replacement.length() == 0) {
         return "";
      }

      String formattedReplacement = replacement;
      switch (caseType) {
         default:
            if (!this.isPreviousCharacterAtBookmark(2) && isAllUpperCase(original)) {
               formattedReplacement = replacement.toUpperCase();
            } else if (Character.isUpperCase(original.charAt(0))) {
               StringBuffer formatBuffer = new StringBuffer(replacement);
               formatBuffer.setCharAt(0, Character.toUpperCase(replacement.charAt(0)));
               formattedReplacement = formatBuffer.toString();
            }
         case 1:
            return formattedReplacement;
      }
   }

   private char handleLowerCaseCharacter(char character) {
      if (!this.isAutoCapsOn()) {
         return character;
      } else if (this._bookmarks[2] != -1
         && this._bookmarks[2] == this.getCaretPosition()
         && this._lastAutoCapAction == 2
         && character == this._lastAutoCapChar) {
         return character;
      } else if (this.isAutoCapLocation()) {
         this._lastAutoCapChar = character;
         this._pendingAutoCapBookmark = this.getCaretPosition();
         this._pendingAutoCapAction = 1;
         return Character.toUpperCase(character);
      } else {
         return character;
      }
   }

   private String replace(String replaceString, String replacementString) {
      FieldChangeListener oldListener = this.getChangeListener();
      this.setChangeListener(null);
      this.backspace(replaceString.length(), 0);
      int oldCursorPos = this.getCursorPosition();
      this.insert(replacementString, 0);
      int newCursorPos = this.getCursorPosition();
      this.setChangeListener(oldListener);
      return newCursorPos - oldCursorPos == replacementString.length()
         ? replacementString
         : this.getText(oldCursorPos + this.getLabelLength(), newCursorPos - oldCursorPos);
   }

   @Override
   protected void update(int delta) {
      super.update(delta);
      if (delta > 0) {
         this.updateBookmarks(this.getCaretPosition() - delta, this.getCaretPosition() - 1, false);
      } else {
         if (delta < 0) {
            this.updateBookmarks(this.getCaretPosition(), this.getCaretPosition() - delta - 1, true);
         }
      }
   }

   private void updateBookmark(int bookmarkIndex, int changeStartOffset, int changeEndOffset, boolean delete) {
      int bookmark = this._bookmarks[bookmarkIndex];
      if (bookmark != -1) {
         int amount = changeEndOffset - changeStartOffset + 1;
         if (delete) {
            if (bookmark >= changeStartOffset && bookmark <= changeEndOffset) {
               this.resetBookmark(bookmarkIndex);
               return;
            }

            if (bookmark > changeEndOffset) {
               this._bookmarks[bookmarkIndex] = this._bookmarks[bookmarkIndex] - amount;
               return;
            }
         } else if (bookmark >= changeEndOffset) {
            this._bookmarks[bookmarkIndex] = this._bookmarks[bookmarkIndex] + amount;
         }
      }
   }

   private void updateBookmarks(int changeStartOffset, int changeEndOffset, boolean delete) {
      boolean autoPeriodBookmarkDecremented = false;
      if (this._bookmarks[1] == changeStartOffset && this._lastAutoPeriodAction == 2) {
         this._bookmarks[1]--;
         autoPeriodBookmarkDecremented = true;
      }

      for (int i = 0; i < this._bookmarks.length; i++) {
         this.updateBookmark(i, changeStartOffset, changeEndOffset, delete);
      }

      if (autoPeriodBookmarkDecremented && this._bookmarks[1] != -1) {
         this._bookmarks[1]++;
      }

      if (this._pendingAutoCapBookmark != -1) {
         this._bookmarks[2] = this._pendingAutoCapBookmark;
         this._lastAutoCapAction = this._pendingAutoCapAction;
         this._pendingAutoCapBookmark = -1;
         this._pendingAutoCapAction = 0;
      }
   }

   private void resetBookmark(int bookmarkIndex) {
      this._bookmarks[bookmarkIndex] = -1;
      switch (bookmarkIndex) {
         case 0:
         default:
            this._lastAutoTextAction = 0;
            return;
         case 1:
            this._lastAutoPeriodAction = 0;
            return;
         case 2:
            this._lastAutoCapAction = 0;
         case -1:
      }
   }

   private boolean undoAutoText() {
      int startOffset = this.getCaretPosition() - this._replacementString.length();
      if (startOffset >= 0) {
         String textToValidate = this.getAttributedText().getText(startOffset, startOffset + this._replacementString.length());
         if (this._replacementString.equals(textToValidate)) {
            this.replace(this._replacementString, this._replacedString);
            this._bookmarks[0] = this.getCaretPosition() - 1;
            this._lastAutoTextAction = 2;
            this.fieldChangeNotify(0);
            return true;
         }

         this.resetBookmark(0);
      }

      return false;
   }

   private static boolean getBooleanResource(int id) {
      String value = _inputFamily.getString(id);
      return unfoldBooleanResource(value);
   }

   private static boolean unfoldBooleanResource(String value) {
      if (value != null && value.length() == 1) {
         char ch = value.charAt(0);
         if (ch == '0') {
            return false;
         }

         if (ch == '1') {
            return true;
         }
      }

      throw new IllegalArgumentException("AutoText setting in resources in incorrect");
   }

   void checkLocale() {
      int currentCode = Locale.getDefault().getCode();
      if (this._cachedLocaleCode != currentCode) {
         this._cachedLocaleCode = currentCode;
         ResourceBundleFamily family = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input");
         this._autoQuoteOpen = family.getString(23);
         this._autoQuoteClosed = family.getString(24);
      }
   }

   private static long validateStyle(long style) {
      if ((style & 196608) == 0) {
         boolean value = getBooleanResource(14);
         if (value) {
            style |= 65536;
         } else {
            style |= 131072;
         }
      }

      if ((style & 786432) == 0) {
         boolean value = getBooleanResource(13);
         if (value) {
            style |= 262144;
         } else {
            style |= 524288;
         }
      }

      if ((style & 1048576) == 0) {
         ResourceBundle bundle = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input")
            .getBundle(Locale.getDefaultInputForSystem());
         boolean value = false;
         if (bundle != null) {
            try {
               String textValue = bundle.getString(15);
               value = unfoldBooleanResource(textValue);
            } catch (MissingResourceException var5) {
            }
         }

         if (value) {
            style |= 1048576;
         } else {
            style |= 2097152;
         }
      }

      if ((style & 12582912) == 0) {
         style |= 8388608;
      }

      return style;
   }

   @Override
   public void selectionDelete() {
      super.selectionDelete();
   }

   @Override
   public int insert(String text, int context, boolean stripInvalid, boolean validateText) {
      return super.insert(text, context, stripInvalid, validateText);
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      if (event.getID() != 1103) {
         AttributedString insertText = event.getText();
         int inserted_len = insertText.length();
         int committed_count = event.getCommittedCharacterCount();
         if (inserted_len == 1 && committed_count == 0 && this.getComposedTextStart() == this.getComposedTextEnd()) {
            char key = insertText.getText().charAt(0);
            if (_autoTextEngine.isClauseSeparator(key)) {
               this.handleClauseSeparator(key);
            }
         }
      }

      return super.inputMethodTextChanged(event);
   }

   private int getPrevNonSpaceCharOffset() {
      int pos = this.getCaretPosition();
      AttributedString text = this.getAttributedText();
      int labelLen = this.getLabelLength();

      for (int i = pos - 1; i >= labelLen; i--) {
         if (text.charAt(i) != ' ') {
            return i;
         }
      }

      return -1;
   }

   private int previousIndexOf(String s, boolean match) {
      int pos = this.getCaretPosition();
      AttributedString text = this.getAttributedText();
      int labelLen = this.getLabelLength();

      for (int i = pos - 1; i >= labelLen; i--) {
         boolean actualMatch = s.indexOf(text.charAt(i)) != -1;
         if (match == actualMatch) {
            return i;
         }
      }

      return -1;
   }
}
