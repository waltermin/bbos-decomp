package net.rim.device.apps.internal.standardcalculator;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.MathUtilities;

final class CalculatorUI extends Manager {
   private CalculatorScreen _calculatorScreen;
   private LabelField _display;
   private CalculatorUI$MemoryLabelField _memoryField;
   private char _currentOperator;
   private char _constantOperator;
   private boolean _haveRightOperand;
   private DeciFloat _leftTermFP;
   private DeciFloat _rightTermFP;
   private DeciFloat _constant;
   DeciFloat _displayFP;
   private DeciFloat _memory;
   private StringBuffer _input;
   private PersistentObject _persistentMemory;
   private PersistentObject _persistentResult;
   private int _topFieldHeight;
   private int _displayWidth = 150;
   private int _memoryHeight;
   private int _displayLeft;
   private boolean _phoneStyleKeyboard;
   private int _screenWidth;
   private boolean _isReducedKeyboard;
   private boolean _hasTrackBall;
   private boolean _altPressed;
   private int _screenHeight;
   private int _toneDuration;
   private int _toneVolume;
   private Bitmap _focusBMP;
   private Bitmap _highlightBMP;
   private Bitmap _regularBMP;
   private int _buttonWidth;
   private int _buttonHeight;
   private int _keyPadStartX;
   private int _keyPadStartY;
   private int _keyPadSizeX;
   private int _keyPadSizeY;
   private int _keyGridX;
   private int _keyGridY;
   private char[][] _keyMap;
   private boolean _focusedKeyDisplayed = false;
   private boolean _secondClearPress;
   private int _memoryWidth = 150;
   private int _memoryLeft;
   private int _memoryPosition;
   private int _topPosition = 22;
   private int _topOfScreenToTopOfBitmap = 0;
   private static final long PERSIST_RESULT_GUID = 2148662295891987665L;
   private static final long PERSIST_MEMORY_GUID = 4506447955275015669L;
   static final boolean ENABLE_TESTS = false;
   private static final int PRECISION = 8;
   private static final String INIT_STRING = "0.";
   static ResourceBundle _resources = ResourceBundle.getBundle(-6534156458416053964L, "net.rim.device.apps.internal.resource.Calculator");
   static ResourceBundle _commonResources = ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common");
   public static String ERROR_MSG = "E";
   private static final char BACKSPACE = '\b';
   private static final char ZERO = '0';
   private static final char ONE = '1';
   private static final char TWO = '2';
   private static final char THREE = '3';
   private static final char FOUR = '4';
   private static final char FIVE = '5';
   private static final char SIX = '6';
   private static final char SEVEN = '7';
   private static final char EIGHT = '8';
   private static final char NINE = '9';
   private static final char CLEAR_ENTRY = '\ue007';
   private static final char CLEAR = '\ue008';
   private static final char MENU = '\ue004';
   private static final char MEM_CLEAR = '\ue001';
   private static final char DIVIDE = '/';
   private static final char SQUARE_ROOT = '√';
   private static final char MEM_RECALL = '\ue000';
   private static final char MULTIPLY = '*';
   private static final char PERCENT = '%';
   private static final char MEM_SAVE = '\ue003';
   private static final char MINUS = '-';
   private static final char INVERSE = '\ue005';
   private static final char MEM_ADD = '\ue002';
   private static final char SIGN = '∓';
   private static final char DECIMAL = '\ue006';
   private static final char PLUS = '+';
   private static final char EQUALS = '=';
   private static final char MEM_SUBTRACT = '\ue009';
   private static final int TOP_KEY_BUTTON_WIDTH = 50;
   private static final int KEY_BUTTON_WIDTH = 25;
   private static final int TOP_KEY_WIDTH = 155;
   private static final int KEY_WIDTH = 155;
   private static final int BOTTOM_PADDING = 10;
   private static int _lastGoodX = -1;
   private static int _lastGoodY = -1;

   CalculatorUI(CalculatorScreen calculatorScreen, long style) {
      super(style);
      this._calculatorScreen = calculatorScreen;
      this._display = (LabelField)(new Object("0.", 1152921504606846981L));
      this._screenWidth = Display.getWidth();
      this._screenHeight = Display.getHeight();
      this._memoryField = new CalculatorUI$MemoryLabelField(1152921504606846982L);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void initialize() {
      int fontSize = -1;
      int memoryFontSize = -1;
      this._phoneStyleKeyboard = true;
      this._isReducedKeyboard = false;
      this._hasTrackBall = Trackball.isSupported();
      switch (Keypad.getHardwareLayout()) {
         case 0:
         case 1295594807:
            this._phoneStyleKeyboard = false;
            break;
         case 1364341300:
         case 1364346180:
            this._isReducedKeyboard = true;
      }

      if (this._isReducedKeyboard) {
         this._keyMap = new char[][]{
            {'Q', 'E', 'T', 'U', 'O', '\u0000', '\n', '퀆', 'Q', 'W'},
            {'A', 'D', 'G', 'J', 'L', '\u0000', '\n', '퀆', 'A', 'S'},
            {'Z', 'C', 'B', 'M', '\b', '\u0000', '\u0006', '퀊', 'Ɛ', '\u0000'},
            {'ā', '\u0014', ' ', 'ą', '\n', '\u0000', '\t', '퀊', 'ĭ', '\u0000'}
         };
      } else {
         this._keyMap = new char[][]{
            {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', '\u0005', '퀆', 'Z', 'C', 'B', 'M', '\b', '\u0000', '\u0006', '퀊'},
            {'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', '\b', '\u0005', '퀆', 'Q', 'E', 'T', 'U', 'O', '\u0000', '\n', '퀆'},
            {'\u0000', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '\u0000', '\n', '\u000b', '퀄', 'ऀ', '⸴', '⸳', '⸰', '㈱', '4', '\f', '퀄'},
            {
                  '\u0000',
                  '\u0000',
                  '0',
                  '\u0000',
                  '\u0000',
                  '\u0000',
                  '\u0000',
                  '\u0000',
                  '\u0000',
                  '\u0000',
                  '\n',
                  '퀆',
                  '\u0000',
                  'Z',
                  'X',
                  'C',
                  'V',
                  'B',
                  'N',
                  'M'
            }
         };
      }

      this._keyPadSizeY = this._keyMap.length;
      this._keyPadSizeX = this._keyMap[0].length;
      byte var9;
      byte var10;
      if (this._screenWidth == 240 && this._screenHeight == 260) {
         this._displayWidth = 210;
         this._memoryWidth = 160;
         this._memoryLeft = this._screenWidth - this._memoryWidth >> 1;
         var9 = 26;
         var10 = 14;
         this._memoryHeight = 18;
         this._topFieldHeight = 31;
         this._topPosition = 14;
         this._buttonWidth = 45;
         this._buttonHeight = 36;
         this._keyPadStartX = 8;
         this._keyPadStartY = 109;
         this._toneDuration = 230;
         this._toneVolume = 88;
      } else if (this._screenWidth == 320 && this._screenHeight == 240) {
         this._displayWidth = 274;
         this._memoryWidth = 212;
         this._memoryLeft = 121;
         var9 = 26;
         var10 = 15;
         this._memoryHeight = 27;
         this._memoryPosition = 210;
         this._topFieldHeight = 31;
         this._topPosition = 24;
         this._buttonWidth = 31;
         this._buttonHeight = 43;
         this._keyPadStartX = 5;
         this._keyPadStartY = 68;
         this._toneDuration = 230;
         this._toneVolume = 88;
      } else {
         this._displayWidth = 222;
         this._memoryWidth = 147;
         this._memoryLeft = 100;
         var9 = 24;
         var10 = 15;
         this._memoryHeight = 22;
         this._memoryPosition = 137;
         this._topFieldHeight = 28;
         this._topPosition = 9;
         this._buttonWidth = 23;
         this._buttonHeight = 28;
         this._keyPadStartX = 5;
         this._keyPadStartY = 46;
         this._toneDuration = 100;
         this._toneVolume = 50;
      }

      this._displayLeft = this._screenWidth - this._displayWidth >> 1;
      FontFamily memoryFont = null;
      FontFamily displayFont = null;
      boolean var7 = false /* VF: Semaphore variable */;

      label64:
      try {
         var7 = true;
         memoryFont = FontFamily.forName("StandardCalculatorMemory");
         displayFont = FontFamily.forName("StandardCalculatorDisplay");
         var7 = false;
      } finally {
         if (var7) {
            System.err.println("StandardCalculatorFont is not found");
            break label64;
         }
      }

      if (displayFont != null) {
         this._display.setFont(displayFont.getFont(1, var9));
      }

      if (memoryFont != null) {
         this._memoryField.setFont(memoryFont.getFont(0, var10));
      }

      this._input = (StringBuffer)(new Object());
      this._memory = new DeciFloat();
      this._memory.invalid();
      this.resetState();
      this.add(this._memoryField);
      this.add(this._display);
      this.add((Field)(new Object(18014398509481984L)));
      this.getPersistentObjects();
   }

   @Override
   public final void applyTheme() {
      super.applyTheme();
      Theme curTheme = ThemeManager.getActiveTheme();
      this._regularBMP = curTheme.getBitmap("background_calculator");

      label34:
      try {
         this._highlightBMP = curTheme.getBitmap("highlight_calculator");
      } finally {
         break label34;
      }

      try {
         this._focusBMP = curTheme.getBitmap("focus_calculator");
      } finally {
         return;
      }
   }

   private final void beep() {
      try {
         javax.microedition.media.Manager.playTone(72, this._toneDuration, this._toneVolume);
      } finally {
         return;
      }
   }

   private final void getPersistentObjects() {
      this._persistentResult = RIMPersistentStore.getPersistentObject(2148662295891987665L);
      synchronized (this._persistentResult) {
         Object obj = this._persistentResult.getContents();
         if (obj instanceof DeciFloat) {
            this._rightTermFP.copy((DeciFloat)obj);
            this.showResult(this._rightTermFP);
         }
      }

      this._persistentMemory = RIMPersistentStore.getPersistentObject(4506447955275015669L);
      synchronized (this._persistentMemory) {
         Object obj = this._persistentMemory.getContents();
         if (obj instanceof DeciFloat) {
            this._memory.copy((DeciFloat)obj);
         }
      }

      this.show();
      this.showMemory();
   }

   private final void setPersistentObjects() {
      this.evalRight();
      this._persistentResult.setContents(this._displayFP, 51);
      this._persistentResult.commit();
      if (!this._memory.isInvalid()) {
         this._persistentMemory.setContents(this._memory, 51, false);
      } else {
         this._persistentMemory.setContents(null, 51);
      }

      this._persistentMemory.commit();
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._screenWidth == 240 && this._screenHeight == 260) {
         this.setPositionChild(this._memoryField, this._memoryLeft, this._topPosition);
         this.layoutChild(this._memoryField, this._memoryWidth, this._memoryHeight);
         this.setPositionChild(this._display, this._displayLeft, this._topFieldHeight + this._topPosition);
         this.layoutChild(this._display, this._displayWidth, this._topFieldHeight);
      } else {
         this.setPositionChild(this._display, this._displayLeft, this._topPosition);
         this.layoutChild(this._display, this._displayWidth, this._topFieldHeight);
         this.setPositionChild(this._memoryField, this._memoryLeft, this._memoryPosition);
         this.layoutChild(this._memoryField, this._memoryWidth, this._memoryHeight);
      }

      this._topOfScreenToTopOfBitmap = this._calculatorScreen.getHeight() - height >> 1;
      this.setExtent(width, height);
   }

   final void runTests() {
   }

   protected final String getText() {
      return this._display.getText().trim();
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      this.displayFocusRectangle(false);
      return this.setKeyCoords(key) ? this.localkeyDown(key) : super.keyDown(keycode, time);
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      this.displayFocusRectangle(false);
      this.setKeyCoords(key);
      this.drawKeyPress(false);
      this.toggleAltKeyState(false);
      return false;
   }

   private final boolean localkeyDown(char key) {
      this.drawKeyPress(true);
      return this.processKey(key);
   }

   @Override
   protected final void onObscured() {
      this.displayFocusRectangle(false);
      super.onObscured();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.displayFocusRectangle(false);
      }

      super.onVisibilityChange(visible);
   }

   protected final boolean navigationClick(int status) {
      if (!this._hasTrackBall) {
         return false;
      } else {
         char key = this._keyMap[this._keyGridY][this._keyGridX];
         if (key == 257) {
            this.toggleAltKeyState(!this._altPressed);
            return true;
         } else {
            this.displayFocusRectangle(false);
            this.localkeyDown(key);
            return true;
         }
      }
   }

   protected final boolean navigationUnclick() {
      if (!this._hasTrackBall) {
         return false;
      }

      char key = this._keyMap[this._keyGridY][this._keyGridX];
      if (key != 257) {
         this.drawKeyPress(false);
         this.toggleAltKeyState(false);
      }

      if (!this._altPressed) {
         this.displayFocusRectangle(true);
      }

      return true;
   }

   protected final boolean moveFocusRectangle(int x, int y) {
      if (!this._hasTrackBall) {
         return false;
      }

      int newY = MathUtilities.clamp(0, this._keyGridY + y, this._keyPadSizeY - 1);
      int newX = MathUtilities.clamp(0, this._keyGridX + x, this._keyPadSizeX - 1);
      if (_lastGoodY == newY && _lastGoodY != this._keyGridY) {
         newX = _lastGoodX;
         _lastGoodX = -1;
         _lastGoodY = -1;
      }

      while (this._keyMap[newY][newX] == 0) {
         _lastGoodX = this._keyGridX;
         _lastGoodY = this._keyGridY;
         if (x == 0) {
            x = 1;
         }

         newX += x;
         if (newX >= this._keyPadSizeX) {
            x = -1;
            newX = _lastGoodX;
         }

         if (newX < 0) {
            return true;
         }
      }

      this.displayFocusRectangle(false);
      this._keyGridY = newY;
      this._keyGridX = newX;
      this.displayFocusRectangle(!this._focusedKeyDisplayed);
      return true;
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      if (event == 520 && key != 6913) {
         this.toggleAltKeyState((Keypad.status(keycode) & 1) != 0);
      }

      return super.processKeyEvent(event, key, keycode, time);
   }

   private final void toggleAltKeyState(boolean newState) {
      if (newState != this._altPressed && this._isReducedKeyboard) {
         this._altPressed = newState;
         this.displayFocusRectangle(false);
         int x = this._keyGridX;
         int y = this._keyGridY;
         this.setKeyCoords(257);
         this.drawKeyPress(this._altPressed);
         this._keyGridX = x;
         this._keyGridY = y;
      }
   }

   private final void drawKeyPress(boolean down) {
      Bitmap source = down ? this._highlightBMP : this._regularBMP;
      if (this._keyGridX > -1 && source != null) {
         int keyPadX = this._keyGridX * this._buttonWidth;
         int keyPadY = this._keyGridY * this._buttonHeight;
         this._calculatorScreen
            .getGraphics()
            .drawBitmap(
               this._keyPadStartX + keyPadX,
               this._topOfScreenToTopOfBitmap + this._keyPadStartY + keyPadY,
               this._buttonWidth,
               this._buttonHeight,
               source,
               (down ? 0 : this._keyPadStartX) + keyPadX,
               (down ? 0 : this._keyPadStartY) + keyPadY
            );
      }
   }

   private final void displayFocusRectangle(boolean state) {
      if (state != this._focusedKeyDisplayed) {
         Bitmap source = state ? this._focusBMP : this._regularBMP;
         this._focusedKeyDisplayed = state;
         if (this._keyGridX > -1) {
            int keyPadX = this._keyGridX * this._buttonWidth;
            int keyPadY = this._keyGridY * this._buttonHeight;
            if (this._focusBMP != null) {
               this._calculatorScreen
                  .getGraphics()
                  .drawBitmap(
                     this._keyPadStartX + keyPadX,
                     this._topOfScreenToTopOfBitmap + this._keyPadStartY + keyPadY,
                     this._buttonWidth,
                     this._buttonHeight,
                     source,
                     (state ? 0 : this._keyPadStartX) + keyPadX,
                     (state ? 0 : this._keyPadStartY) + keyPadY
                  );
               return;
            }

            this._calculatorScreen
               .getGraphics()
               .invert(this._keyPadStartX + keyPadX, this._topOfScreenToTopOfBitmap + this._keyPadStartY + keyPadY, this._buttonWidth, this._buttonHeight);
         }
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this._calculatorScreen.onMenu(0);
      return true;
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   private final boolean setKeyCoords(int c) {
      for (int y = 0; y < this._keyPadSizeY; y++) {
         for (int x = 0; x < this._keyPadSizeX; x++) {
            if (this._keyMap[y][x] == c) {
               this._keyGridY = y;
               this._keyGridX = x;
               return true;
            }
         }
      }

      return false;
   }

   private final char mapKey(char c) {
      if (c != 261) {
         c = CharacterUtilities.toUpperCase(c, 1701707776);
      }

      char altKey = Keypad.map(c, 1);
      if (altKey > 255) {
         altKey = CharacterUtilities.foldFullWidth(altKey);
      }

      if (!this._isReducedKeyboard) {
         if (altKey != 0) {
            switch (altKey) {
               case '"':
               case '%':
               case '&':
               case '\'':
               case ':':
               case ';':
               case '<':
                  break;
               case '#':
               case '$':
               case '(':
               case ')':
               case '*':
               case '+':
               case '-':
               case '.':
               case '/':
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case '=':
               default:
                  c = altKey;
                  break;
               case ',':
                  c = '.';
            }
         }

         switch (c) {
            case '\b':
               return '\b';
            case '\n':
               return '=';
            case '\u001b':
               this.setPersistentObjects();
               System.exit(0);
               return '\u0000';
            case '#':
               return '∓';
            case '(':
               return '\ue007';
            case ')':
               return '\ue008';
            case ',':
            case '.':
               return '\ue006';
            case 'B':
               return '%';
            case 'H':
               return '\ue001';
            case 'J':
               return '\ue000';
            case 'K':
               return '\ue003';
            case 'L':
               return '\ue002';
            case 'O':
            case 'U':
               return '\ue009';
            case 'P':
               return '\ue005';
            case 'V':
               return '√';
            case '\u007f':
               if (!this._phoneStyleKeyboard) {
                  return '\b';
               }
               break;
            case 'ą':
               c = '0';
         }
      } else {
         if (c != 'L') {
            this._secondClearPress = false;
         }

         if (altKey != 0) {
            switch (altKey) {
               case '/':
                  break;
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               default:
                  c = altKey;
            }
         }

         switch (c) {
            case '\b':
               return (char)(this._altPressed ? '\ue001' : '\b');
            case '\n':
               return '=';
            case '\u0014':
               return (char)(this._altPressed ? '∓' : '*');
            case '\u001b':
               this.setPersistentObjects();
               System.exit(0);
               return '\u0000';
            case 'A':
               return (char)(this._altPressed ? '\ue005' : '/');
            case 'L':
               if (this._altPressed) {
                  return '\ue000';
               }

               if (this._secondClearPress) {
                  this._secondClearPress = false;
                  return '\ue008';
               }

               this._secondClearPress = true;
               return '\ue007';
            case 'O':
               return (char)(this._altPressed ? '\ue003' : '\ue006');
            case 'Q':
               return (char)(this._altPressed ? '\ue009' : '-');
            case 'Z':
               return (char)(this._altPressed ? '√' : '%');
            case 'ą':
               return (char)(this._altPressed ? '\ue002' : '+');
         }
      }

      return c;
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      this.setPersistentObjects();
   }

   private final boolean processKey(char c) {
      c = this.mapKey(c);
      if (this._displayFP != null && this._displayFP.isInvalid()) {
         switch (c) {
            case '\ue003':
            case '\ue005':
            case '\ue006':
            default:
               this.beep();
               return true;
            case '\ue004':
            case '\ue007':
            case '\ue008':
         }
      }

      switch (c) {
         case '\b':
            this.backspaceNumber();
            return true;
         case '%':
         case '∓':
         case '√':
         case '\ue005':
            this.doUnary(c);
            return true;
         case '*':
         case '+':
         case '-':
         case '/':
            this.binaryOperator(c);
            return true;
         case '=':
            this.doEquals();
            return true;
         case '\ue000':
            if (this._memory.isInvalid()) {
               this.beep();
               return true;
            }

            this._leftTermFP.copy(this._rightTermFP);
            this._rightTermFP.copy(this._memory);
            this._haveRightOperand = true;
            this.showResult(this._rightTermFP);
            return true;
         case '\ue001':
            this._memory.invalid();
            this.showMemory();
            return true;
         case '\ue002':
            this.evalRight();
            if (this._memory.isInvalid()) {
               this._memory.copy(this._displayFP);
            } else {
               this._memory.add(this._displayFP);
            }

            this.showMemory();
            return true;
         case '\ue003':
            this.evalRight();
            this._memory.copy(this._displayFP);
            this.showMemory();
            return true;
         case '\ue004':
            this._calculatorScreen.onMenu(0);
            return true;
         case '\ue006':
            this.appendNumber('.');
            return true;
         case '\ue007':
            this.clearThisEntry();
            return true;
         case '\ue008':
            this.clearAll();
            return true;
         case '\ue009':
            this.evalRight();
            if (this._memory.isInvalid()) {
               this._memory.copy(this._displayFP);
               this._memory.negate();
            } else {
               this._memory.sub(this._displayFP);
            }

            this.showMemory();
            return true;
         default:
            if (Character.isDigit(c)) {
               this.appendNumber(c);
               return true;
            } else {
               return false;
            }
      }
   }

   private final boolean checkPrecision() {
      int count = 0;

      for (int i = 0; i < this._input.length(); i++) {
         if (Character.isDigit(this._input.charAt(i))) {
            if (++count > 8) {
               return true;
            }
         }
      }

      return false;
   }

   private final void show() {
      if (this._displayFP != null) {
         if (this._displayFP.isInvalid()) {
            this._display.setText(ERROR_MSG);
         } else {
            this._display.setText(this._displayFP.toString());
         }
      } else if (this.checkPrecision()) {
         this.beep();
      } else if (this._input.length() == 0) {
         this._display.setText("0.");
      } else {
         this._display.setText(this._input, 0, this._input.length());
      }
   }

   private final void showResult(DeciFloat result) {
      this._displayFP = result;
      this.show();
   }

   private final void showMemory() {
      String str = "";
      if (!this._memory.isInvalid()) {
         str = this._memory.toString();
      }

      this._memoryField.setText(str);
   }

   private final void clearAll() {
      this.resetState();
      this.show();
   }

   private final void clearThisEntry() {
      this.initNumber();
      this.show();
   }

   private final void resetState() {
      this._displayFP = null;
      this._currentOperator = 0;
      this._constantOperator = 0;
      this._haveRightOperand = false;
      this._leftTermFP = new DeciFloat();
      this._rightTermFP = new DeciFloat();
      this._constant = new DeciFloat();
      this._input.setLength(0);
   }

   private final void initNumber() {
      this._displayFP = null;
      this._haveRightOperand = false;
      this._input.setLength(0);
   }

   private final void appendNumber(char c) {
      if (this._displayFP != null) {
         this.initNumber();
      }

      if (c == '.') {
         int i = this._input.length();

         while (--i >= 0) {
            if (this._input.charAt(i) == c) {
               this.beep();
               return;
            }
         }
      } else if (this._input.length() == 1 && this._input.charAt(0) == '0') {
         this._input.setLength(0);
      }

      this._input.append(c);
      if (this.checkPrecision()) {
         this.beep();
         this.backspaceNumber();
      } else {
         this._haveRightOperand = true;
         this._constantOperator = 0;
         this.show();
      }
   }

   protected final void setText(String str) {
      this.initNumber();
      this._input.append(str);
      if (this.checkPrecision()) {
         this.beep();
         this._input.setLength(0);
      } else {
         this._haveRightOperand = true;
         this._constantOperator = 0;
         this.show();
      }
   }

   private final void backspaceNumber() {
      if (this._displayFP != null) {
         this.clearThisEntry();
      } else {
         int length = this._input.length();
         switch (length) {
            case -1:
               this._input.setLength(length - 1);
               this.show();
               return;
            case 0:
            default:
               this.beep();
               return;
            case 1:
               this.clearThisEntry();
         }
      }
   }

   private final void evalRight() {
      if (this._displayFP == null && this._input.length() > 0) {
         this._rightTermFP = new DeciFloat(this._input, 0, this._input.length());
         this.initNumber();
      }

      this._displayFP = this._rightTermFP;
      this._haveRightOperand = true;
   }

   private final void doUnary(char operator) {
      this.evalRight();
      label15:
      switch (operator) {
         case '%':
            this._rightTermFP.div(new DeciFloat(100));
            switch (this._currentOperator) {
               case ')':
               case ',':
               case '.':
                  break label15;
               case '*':
               case '+':
               case '-':
               case '/':
               default:
                  this._rightTermFP.mul(this._leftTermFP);
                  break label15;
            }
         case '∓':
            this._rightTermFP.negate();
            break;
         case '√':
            if (!this._display.getText().equals(ERROR_MSG)) {
               this._rightTermFP.sqrt();
            }
            break;
         case '\ue005':
            DeciFloat result = new DeciFloat(1);
            result.div(this._rightTermFP);
            this._rightTermFP = result;
      }

      this.showResult(this._rightTermFP);
   }

   final void doConversion(int id) {
      int mulBy = 0;
      int divBy = 0;
      this.evalRight();
      switch (id) {
         case 301:
            mulBy = 39370079;
            break;
         case 302:
            mulBy = 328083990;
            break;
         case 303:
            mulBy = 109361330;
            break;
         case 304:
            mulBy = 62137119;
            break;
         case 305:
            mulBy = 220462290;
            break;
         case 306:
            this._rightTermFP.mul(new DeciFloat(18, -1));
            this._rightTermFP.add(new DeciFloat(32));
            break;
         case 401:
            divBy = 39370079;
            break;
         case 402:
            divBy = 328083990;
            break;
         case 403:
            divBy = 109361330;
            break;
         case 404:
            divBy = 62137119;
            break;
         case 405:
            divBy = 220462290;
            break;
         case 406:
            this._rightTermFP.sub(new DeciFloat(32));
            this._rightTermFP.div(new DeciFloat(18, -1));
            break;
         case 407:
            divBy = 21996935;
            break;
         case 408:
            mulBy = 26417205;
            break;
         case 409:
            mulBy = 21996935;
            break;
         case 506:
            divBy = 26417205;
            break;
         case 508:
         case 509:
            this._rightTermFP.mul(new DeciFloat(26417205, -8));
            DeciFloat result = new DeciFloat(62137119, -6);
            result.div(this._rightTermFP);
            this._rightTermFP = result;
            break;
         default:
            return;
      }

      if (mulBy != 0) {
         this._rightTermFP.mul(new DeciFloat(mulBy, -8));
      }

      if (divBy != 0) {
         this._rightTermFP.div(new DeciFloat(divBy, -8));
      }

      this.showResult(this._rightTermFP);
   }

   private final void binaryOperator(char operator) {
      this.calculateBinary();
      this._currentOperator = operator;
      this._constantOperator = 0;
      this._haveRightOperand = false;
      this.showResult(this._leftTermFP);
   }

   private final void calculateBinary() {
      if (this._haveRightOperand) {
         this.evalRight();
         switch (this._currentOperator) {
            case '\u0000':
               this._leftTermFP.copy(this._rightTermFP);
               return;
            case '*':
               this._leftTermFP.mul(this._rightTermFP);
               break;
            case '+':
               this._leftTermFP.add(this._rightTermFP);
               break;
            case '-':
               this._leftTermFP.sub(this._rightTermFP);
               break;
            case '/':
               this._leftTermFP.div(this._rightTermFP);
         }

         this._rightTermFP.copy(this._leftTermFP);
         this._haveRightOperand = false;
      }
   }

   private final void doEquals() {
      this.evalRight();
      if (this._currentOperator == 0) {
         if (this._constantOperator == 0) {
            this.showResult(this._rightTermFP);
            return;
         }

         this._currentOperator = this._constantOperator;
         this._leftTermFP.copy(this._rightTermFP);
         this._rightTermFP.copy(this._constant);
      } else {
         this._constantOperator = this._currentOperator;
         this._constant.copy(this._rightTermFP);
      }

      this.calculateBinary();
      this._currentOperator = 0;
      this._haveRightOperand = true;
      this.showResult(this._leftTermFP);
   }
}
