package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.GlyphMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.MediaIcon;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.im.layout.UILocaleKeyLayout;
import net.rim.tid.itie.EventHandler;
import net.rim.vm.Array;

class SymbolScreen$SymbolField extends Field {
   private Tag TAG;
   private Tag TAG_NEXT;
   private ThemeAttributeSet _tasNext;
   protected byte _maxKeysInRow;
   protected int _yStep;
   protected int _keyWidth;
   protected int _keyHeight;
   protected int _replHeight;
   protected int _maxIBearingY;
   private final int HORIZONTAL_SPACE;
   private Font _keyFont;
   private Font _symbolFont;
   private char[][] _keyCodes;
   private String[][] _layout;
   protected IntIntHashtable _map;
   private String _description;
   private byte _focusRow;
   private byte _focusColumn;
   private boolean _swapPages;
   private GlyphMetrics _glyphMetrics;
   protected int _currentPage;
   private MessageFormat _pageFormatter;
   private StringBuffer _pageBuffer;
   protected int _pagesStandard;
   protected int[] _pages;
   private final SymbolScreen this$0;
   protected static final int XTAB = 5;
   protected static final int YTAB = 4;
   protected static final int VSPACE = 1;
   protected static final int ARROW_SIGN = 8646;
   private static final int NUMBER_OF_ROWS = 3;
   private static final int NUMBER_OF_MAPPED_PAGES = 2;

   protected SymbolScreen$SymbolField(SymbolScreen _1) {
      super(18014398509481984L);
      this.this$0 = _1;
      this.TAG = Tag.create("symbolfield");
      this.TAG_NEXT = Tag.create("symbolfield-next");
      this.HORIZONTAL_SPACE = InternalServices.isReducedFormFactor() ? 5 : 1;
      this._map = new IntIntHashtable();
      this._glyphMetrics = new GlyphMetrics();
      this._currentPage = -1;
      this._pageFormatter = new MessageFormat(UiInternal.BUNDLE.getString(31));
      this._pageBuffer = new StringBuffer();
      this._pagesStandard = 2;
      this.setTag(this.TAG);
      this.$initLayoutKeyCodes();
      this.$initDisplayKeys();
      this.reset();
      this.nextPage(true, true);
   }

   @Override
   protected void applyFont() {
      try {
         FontFamily keyFamily = Graphics.isColor() ? FontFamily.forName("BBMillbank") : FontFamily.forName(FontFamily.FAMILY_SYSTEM);
         int size = 8;
         this._keyFont = keyFamily.getFont(0, size, 2);
      } catch (Exception var8) {
      }

      Font font = Font.getDefault();
      int hanMask = font.getStyle() & 7168;
      int fontSize = 14;
      int keyFontHeight = this._keyFont.getHeight();
      boolean fontSizeChanged = false;
      int maxKeyWidth = this.getMaxKeyWidth();
      int minKeyWidth = Math.min(maxKeyWidth == -1 ? keyFontHeight + 2 : maxKeyWidth, SymbolScreen.DISPLAY_WIDTH_ALLOWED / this._maxKeysInRow);
      this._symbolFont = font.derive((Graphics.isColor() ? 1 : 0) | hanMask, fontSize, 2);

      do {
         fontSizeChanged = false;
         if (this.getMaxSymbolWidth() + 2 >= minKeyWidth) {
            this._symbolFont = font.derive(1 | hanMask, --fontSize, 2);
            fontSizeChanged = true;
         }
      } while (fontSizeChanged && fontSize > 8);

      if (this.this$0._inputMethodID == 512) {
         this._symbolFont = font.derive(1 | hanMask, fontSize, 2, 4, 0);
      } else {
         this._symbolFont = font.derive((Graphics.isColor() ? 1 : 0) | hanMask, fontSize, 2);
      }

      super.applyFont();
   }

   private void $initLayoutKeyCodes() {
      this._keyCodes = new char[3][];
      this._keyCodes[0] = this.getLayoutCodes(0);
      this._keyCodes[1] = this.getLayoutCodes(1);
      this._keyCodes[2] = this.getLayoutCodes(2);
      this.this$0._keysTotal = this._keyCodes[0].length + this._keyCodes[1].length + this._keyCodes[2].length;
      if (this.this$0._keysTotal > 1) {
         SymbolScreen.access$310(this.this$0);
      }
   }

   private int keyOrdinalNumber(char key) {
      int len0 = this._keyCodes[0].length;
      int i = 0;

      while (i < len0 && this._keyCodes[0][i] != key) {
         i++;
      }

      if (i < len0) {
         return i;
      }

      int len1 = this._keyCodes[1].length;
      i = 0;

      while (i < len1 && this._keyCodes[1][i] != key) {
         i++;
      }

      if (i < len1) {
         return i + len0;
      }

      len0 += len1;
      len1 = this._keyCodes[2].length;
      i = 0;

      while (i < len1 && this._keyCodes[2][i] != key) {
         i++;
      }

      return i < len1 ? i + len0 : -1;
   }

   private void $initDisplayKeys() {
      SLKeyLayout layout = UILocaleKeyLayout.getUIKeyLayout();
      this._layout = new String[this._keyCodes.length][];

      for (int i = 0; i < this._keyCodes.length; i++) {
         this._layout[i] = new String[this._keyCodes[i].length];

         for (int j = 0; j < this._keyCodes[i].length; j++) {
            this._layout[i][j] = this.getDisplayKey(layout, this._keyCodes[i][j]);
         }
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      Theme theme = ThemeManager.getActiveTheme();
      this._tasNext = theme.getAttributeSet(this.TAG_NEXT);
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      char focusChar = this.getFocusChar();
      char symbol = (char)this._map.get(Character.toUpperCase(focusChar));
      return new AccessibleContextFactory(String.valueOf(symbol), String.valueOf(focusChar), 22, 4, this, new TextField());
   }

   @Override
   public int getAccessibleChildCount() {
      int count = 0;

      for (int i = 0; i < this._keyCodes.length; i++) {
         count += this._keyCodes[i].length;
      }

      return count;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      char childAtIndex = 0;
      int i = 0;
      if (index >= this._keyCodes[i].length) {
         i = 0;

         while (i < this._keyCodes.length - 1 && (this._keyCodes[i].length >= index || index >= this._keyCodes[i + 1].length)) {
            i++;
         }

         try {
            index -= this._keyCodes[i].length;
            childAtIndex = this._keyCodes[i + 1][index];
         } catch (Exception e) {
            System.out.println("Array Index Out Of Bound Exception");
         }
      } else {
         childAtIndex = this._keyCodes[i][index];
      }

      char symbol = (char)this._map.get(Character.toUpperCase(childAtIndex));
      return new AccessibleContextFactory(String.valueOf(symbol), String.valueOf(childAtIndex), 22, 1, this, new TextField());
   }

   @Override
   public int getAccessibleRole() {
      return 22;
   }

   private char getSymbol(char keyCode, int page) {
      SLKeyLayout keyLayout = Keypad.getLayout();
      if (keyLayout != null && page >= 0) {
         switch (page) {
            case -1:
               if (2 <= page && page < this._pagesStandard && this.this$0._additionalSymbolData instanceof String) {
                  page -= 2;
                  if (keyCode == 127) {
                     return '⇆';
                  }

                  int position = this.keyOrdinalNumber(keyCode);
                  if (position >= 0) {
                     position += page * this.this$0._keysTotal;
                  }

                  if (position >= 0 && position < ((String)this.this$0._additionalSymbolData).length()) {
                     return ((String)this.this$0._additionalSymbolData).charAt(position);
                  }

                  return '\u0000';
               }

               return '\u0000';
            case 0:
            default:
               return keyLayout.getKeyChars(keyCode, 9, false).charAt(0);
            case 1:
               return keyLayout.getKeyChars(keyCode, 11, false).charAt(0);
         }
      } else {
         return '\u0000';
      }
   }

   private String getDisplayKey(SLKeyLayout layout, char keyCode) {
      return layout.getKeyChars(keyCode, 0).charAt(0) == 128 ? "SYM" : layout.getKeyChars(keyCode, 2).toString();
   }

   public char getFocusChar() {
      return 0 <= this._focusColumn && this._focusColumn < this._keyCodes[this._focusRow].length ? this._keyCodes[this._focusRow][this._focusColumn] : '\u0000';
   }

   private void setFocusChar(char key) {
      char[][] keyCodes = this._keyCodes;
      key = Character.toUpperCase(key);

      for (int i = 0; i < keyCodes.length; i++) {
         for (int j = 0; j < keyCodes[i].length; j++) {
            if (key == keyCodes[i][j]) {
               this._focusRow = (byte)i;
               this._focusColumn = (byte)j;
               return;
            }
         }
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      int x = this.getWidth() - ((this._keyWidth + this.HORIZONTAL_SPACE) * this._layout[this._focusRow].length - this.HORIZONTAL_SPACE) >> 1;
      x += this._focusColumn * (this._keyWidth + this.HORIZONTAL_SPACE);
      int y = this._focusRow * this._yStep + this._keyFont.getHeight() + 1 - this._maxIBearingY;
      rect.set(x - (this.HORIZONTAL_SPACE >> 1), y, this._keyWidth + this.HORIZONTAL_SPACE, this._replHeight + this._maxIBearingY);
   }

   private char[] getLayoutCodes(int row) {
      int modifier;
      switch (row) {
         case 0:
         default:
            modifier = 0;
            break;
         case 1:
            modifier = 1;
            break;
         case 2:
            modifier = 2;
      }

      SLKeyLayout layout = Keypad.getLayout();
      StringBuffer buffer = layout.getKeyChars(156, modifier, false);
      char[] result = new char[buffer.length()];
      buffer.getChars(0, result.length, result, 0);
      return result;
   }

   protected int getMaxSymbolHeight() {
      return this._symbolFont.getHeight();
   }

   protected int getMaxKeyWidth() {
      int keyWidth = -1;

      for (int i = 0; i < this._keyCodes.length; i++) {
         for (int j = 0; j < this._keyCodes[i].length; j++) {
            String key = this._layout[i][j];
            keyWidth = Math.max(keyWidth, this._keyFont.getBounds(key) + 3);
         }
      }

      return keyWidth;
   }

   protected int getMaxSymbolWidth() {
      int width = 0;
      SLKeyLayout layout = Keypad.getLayout();

      for (int i = 0; i < this._keyCodes.length; i++) {
         for (int j = 0; j < this._keyCodes[i].length; j++) {
            StringBuffer symbol = layout.getKeyChars(this._keyCodes[i][j], 9, false);
            width = Math.max(width, this._symbolFont.getBounds(symbol));
         }
      }

      return width;
   }

   protected int getNumPages() {
      return this._pagesStandard;
   }

   protected int getNumPagesAvailable() {
      return this._pages.length;
   }

   protected int[] getPages() {
      int[] pages = new int[0];
      this._pagesStandard = 2;
      if (this.this$0._additionalSymbolData instanceof String) {
         this._pagesStandard = 2 + (((String)this.this$0._additionalSymbolData).length() + this.this$0._keysTotal - 1) / this.this$0._keysTotal;
      }

      Array.resize(pages, Math.max(1, this._pagesStandard));
      int nonEmptyPages = 0;

      for (int page = 0; page < this._pagesStandard; page++) {
         if (!this.isPageEmpty(this.this$0._targetEditField, page)) {
            pages[nonEmptyPages] = page;
            nonEmptyPages++;
         }
      }

      Array.resize(pages, Math.max(1, nonEmptyPages));
      return pages;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      int focusIndex = 0;

      for (int i = 0; i < this._focusRow; i++) {
         focusIndex += this._keyCodes[i].length;
      }

      focusIndex += this._focusColumn;
      return index == focusIndex;
   }

   protected void initCurrentPageMap(int page) {
      this.updatePageNumber();
      if (Keypad.getLayout() != null && this._layout != null) {
         this._map.clear();
         int rows = this._layout.length;

         for (int i = 0; i < rows; i++) {
            int len = this._layout[i].length;
            if (this._maxKeysInRow < len) {
               this._maxKeysInRow = (byte)len;
            }

            for (int j = 0; j < len; j++) {
               char ch = this.getSymbol(this._keyCodes[i][j], page);
               if (ch != 0) {
                  this._map.put(this._keyCodes[i][j], ch);
               }
            }
         }

         this.updateDescription();
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      if (super.invokeAction(action)) {
         return true;
      }

      switch (action) {
         case 1:
            int symbol = this._map.get(Character.toUpperCase(this.getFocusChar()));
            return this.processSymbol(symbol);
         default:
            return false;
      }
   }

   protected boolean isCycling() {
      return InternalServices.isReducedFormFactor();
   }

   public boolean isCurrentPageEmpty(BasicEditField field) {
      IntEnumeration enumeration = this._map.elements();

      while (enumeration.hasMoreElements()) {
         int symbol = enumeration.nextElement();
         if (symbol != -1 && (field == null || field.validate((char)symbol))) {
            return false;
         }
      }

      return true;
   }

   public boolean isEmpty(TextField field) {
      for (int page = 0; page < this._pagesStandard; page++) {
         if (!this.isPageEmpty(field, page)) {
            return false;
         }
      }

      return true;
   }

   protected boolean isPageEmpty(TextField edit, int page) {
      for (int i = this._layout.length - 1; i >= 0; i--) {
         for (int j = this._layout[i].length - 1; j >= 0; j--) {
            char replacement = this.getSymbol(this._keyCodes[i][j], page);
            if (replacement != 0 && (edit == null || edit.validate(replacement))) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      return event == 514
         ? EventHandler.getInstance().processKeyEvent(event, keycode, key, keycode, time, true)
         : super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   protected boolean keyControl(char character, int status, int time) {
      boolean handled = false;
      if (character == 129 || character == 130) {
         int modifier = character == 129 ? -1 : 1;
         byte goingToRow = (byte)((this._focusRow + modifier + this._layout.length) % this._layout.length);
         byte goingToCol = this._focusColumn >= this._layout[goingToRow].length ? (byte)(this._layout[goingToRow].length - 1) : this._focusColumn;
         int amount = modifier == -1
            ? -(this._focusColumn + (this._layout[goingToRow].length - goingToCol))
            : this._layout[this._focusRow].length - this._focusColumn + goingToCol;
         if (amount != 0) {
            return this.this$0.dispatchTrackwheelEvent(519, amount, status, time);
         }

         handled = true;
      }

      if (!handled) {
         handled = super.keyControl(character, status, time);
      }

      return handled;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean handled = super.keyDown(keycode, time);
      if (!handled) {
         boolean noAlt = (keycode & 257) == 0;
         keycode = Keypad.key(keycode);
         char key = Keypad.map(keycode, 0);
         if (this.this$0._inputMethodID == 512 && (!noAlt || this.this$0._pageTimer != -1)) {
            if (key > 'ﻠ') {
               key -= 'ﻠ';
            }

            key = Keypad.getAltedChar(key);
            if (key > 'ﻠ') {
               key -= 'ﻠ';
            }

            if ('0' <= key && key <= '9') {
               if (this.this$0._pageTimer != -1) {
                  Application.getApplication().cancelInvokeLater(this.this$0._pageTimer);
                  this.this$0._pageTimer = -1;
                  this.gotoPage(this.this$0._pendingPageNumber * 10 + key - 48 - 1, false);
                  return true;
               }

               this.this$0._pendingPageNumber = key - '0';
               this.this$0._pageTimer = Application.getApplication().invokeLater(new SymbolScreen$SymbolField$1(this), 500, false);
               return true;
            }
         }

         switch (key) {
            case '\b':
            case ' ':
               this.this$0.close();
               return true;
            case '\n': {
               int symbol = this._map.get(Character.toUpperCase(this.getFocusChar()));
               return this.processSymbol(symbol);
            }
            case '\u0080':
               if (!this.nextPage(noAlt, true)) {
                  this.this$0.close();
                  return true;
               }

               this.invalidate();
               return true;
            default: {
               int symbol = this._map.get(keycode);
               handled = this.processSymbol(symbol);
               if (!this._swapPages) {
                  this.setFocusChar(key);
               }
            }
         }
      }

      this.invalidate();
      return handled;
   }

   @Override
   protected void layout(int width, int height) {
      switch (Display.getWidth() << 16 | Display.getHeight()) {
         case 15728800:
         case 15728880:
            this._keyWidth = 20;
            this._keyHeight = 13;
            this._replHeight = 20;
            break;
         case 21234016:
            this._keyWidth = 35;
            this._keyHeight = 23;
            this._replHeight = 34;
            break;
         default:
            int keyFontHeight = this._keyFont.getHeight();
            int maxKeyWidth = this.getMaxKeyWidth();
            this._keyWidth = Math.max(maxKeyWidth == -1 ? keyFontHeight + 2 : maxKeyWidth, this.getMaxSymbolWidth() + 2);
            this._keyWidth = Math.min(this._keyWidth, SymbolScreen.DISPLAY_WIDTH_ALLOWED / this._maxKeysInRow);
            this._keyHeight = Math.max(this._keyFont.getHeight(), 10);
            this._replHeight = this.getMaxSymbolHeight();
      }

      this._yStep = this._keyHeight + this._replHeight + 4;
      width = this._maxKeysInRow * (this._keyWidth + this.HORIZONTAL_SPACE) + 10;
      height = 3 * this._yStep + 2 * this._keyFont.getHeight();
      this.setExtent(width, height);
   }

   private boolean mapContains(int c) {
      return this._map.contains(c);
   }

   protected boolean nextPage(boolean forward, boolean wrap) {
      if (forward) {
         this._currentPage++;
      } else {
         this._currentPage--;
      }

      if (this._currentPage >= this._pages.length) {
         if (!wrap || this._pages.length <= 1) {
            this._currentPage = this._pages.length - 1;
            return false;
         }

         this._currentPage = 0;
      } else if (this._currentPage < 0) {
         if (!wrap) {
            return false;
         }

         this._currentPage = this._pages.length - 1;
      }

      this.initCurrentPageMap(this._pages[this._currentPage]);
      this.invalidate();
      return true;
   }

   protected void gotoPage(int page) {
      this.gotoPage(page, true);
   }

   protected void gotoPage(int page, boolean goAnyway) {
      if (page < 0 || page >= this._pages.length) {
         if (!goAnyway) {
            return;
         }

         page = 0;
      }

      this._currentPage = page;
      this.initCurrentPageMap(this._pages[this._currentPage]);
      this.invalidate();
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      int ret = 0;
      if (Trackball.isSupported() || this.this$0._inputMethodID == 512) {
         if ((status & 65536) == 0 && this.this$0._inputMethodID != 512 || (status & 1) != 0 && this.this$0._inputMethodID == 512) {
            this._focusRow = (byte)(this._focusRow + amount);
            int currentPageBefore = this._currentPage;
            if (this._focusRow > this._layout.length - 1 && amount > 0) {
               if (this._currentPage < this._pages.length - 1) {
                  this._focusRow = 0;
                  this.nextPage(true, true);
                  if (this._currentPage == currentPageBefore) {
                     this._focusRow = (byte)(this._layout.length - 1);
                  }

                  this.invalidate();
               } else if (this.this$0._inputMethodID != 512) {
                  this._focusRow = (byte)(this._layout.length - 1);
               } else {
                  this._focusRow = 0;
                  this.gotoPage(0);
               }
            } else if (this._focusRow < 0 && amount < 0) {
               if (this._currentPage > 0) {
                  this._focusRow = (byte)(this._layout.length - 1);
                  this.nextPage(false, true);
                  if (this._currentPage == currentPageBefore) {
                     this._focusRow = 0;
                  }

                  this.invalidate();
               } else {
                  this._focusRow = 0;
                  if (this.this$0._inputMethodID == 512) {
                     ret = -1;
                  }
               }
            }

            while (this._focusColumn > this._layout[this._focusRow].length - 1) {
               this._focusColumn--;
            }
         } else if (this.this$0._inputMethodID != 512) {
            this._focusColumn = (byte)(this._focusColumn + amount);
            if (amount > 0 && this._focusColumn >= this._layout[this._focusRow].length) {
               this._focusColumn = (byte)(this._layout[this._focusRow].length - 1);
            } else if (amount < 0 && this._focusColumn <= 0) {
               this._focusColumn = 0;
            }
         }
      }

      if (!Trackball.isSupported() && status == 0) {
         this._focusColumn = (byte)(this._focusColumn + amount);
         if (this._focusColumn >= this._layout[this._focusRow].length) {
            this._focusColumn = (byte)(this._focusColumn - this._layout[this._focusRow].length);
            if (this._focusRow == this._layout.length - 1) {
               this._focusRow = 0;
               this.nextPage(true, true);
               this.invalidate();
            } else {
               this._focusRow++;
            }

            this._focusColumn = (byte)MathUtilities.clamp(0, this._focusColumn, this._layout[this._focusRow].length - 1);
         } else if (this._focusColumn < 0) {
            if (this._focusRow == 0) {
               this._focusRow = (byte)(this._layout.length - 1);
               if (this.this$0._inputMethodID == 512) {
                  return amount;
               }

               this.nextPage(false, true);
               this.invalidate();
            } else {
               this._focusRow--;
            }

            this._focusColumn = (byte)(this._focusColumn + this._layout[this._focusRow].length);
            this._focusColumn = (byte)MathUtilities.clamp(0, this._focusColumn, this._layout[this._focusRow].length - 1);
         }
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
      }

      return ret;
   }

   @Override
   protected void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      Field target = this.this$0.getTarget();
      BasicEditField edit = null;
      if (target instanceof BasicEditField) {
         edit = (BasicEditField)target;
      }

      graphics.setFont(this._keyFont);
      if (this.this$0._inputMethodID != 512) {
         graphics.drawText(this._pageBuffer, 0, Integer.MAX_VALUE, 0, 0, 5, this.getWidth() - 10);
         graphics.drawText(this._description, 0, Integer.MAX_VALUE, 0, this.getHeight(), 108, this.getWidth() - 10);
      }

      int keyFontHeight = this._keyFont.getHeight();
      int y = keyFontHeight + 1;
      int rows = this._layout.length;

      for (int i = 0; i < rows; y += this._yStep) {
         if (clip.Y2() >= y && y + this._yStep >= clip.y) {
            int len = this._layout[i].length;
            int x = this.getWidth() - ((this._keyWidth + this.HORIZONTAL_SPACE) * len - this.HORIZONTAL_SPACE) >> 1;

            for (int j = 0; j < len; j++) {
               char original = this._keyCodes[i][j];
               int keyWidth = this._keyWidth;
               if (original != 0) {
                  String key = this._layout[i][j];
                  if (key != null && key.length() >= 2) {
                     keyWidth = Math.max(keyWidth, this._keyFont.getBounds(key) + 4);
                  }

                  graphics.setFont(this._symbolFont);
                  int replacement = this._map.get(original);
                  if (replacement != -1
                     && (replacement == 8646 || this._pages[this._currentPage] >= this._pagesStandard || edit == null || edit.validate((char)replacement))) {
                     this.paintSymbol(graphics, (char)replacement, x - this.HORIZONTAL_SPACE, y, keyWidth + 2 * this.HORIZONTAL_SPACE, this._replHeight);
                  }

                  if (replacement == 8646 && this._tasNext != null) {
                     graphics.pushContext(0, 0, 1073741823, 1073741823, 0, 0);
                     this.setThemeAttributesSpecial(this._tasNext, graphics);
                     graphics.setFont(this._keyFont);
                     int fg = graphics.getColor();
                     graphics.setColor(graphics.getBackgroundColor());
                     graphics.fillRect(x, y + this._replHeight + 1, keyWidth, this._keyHeight);
                     graphics.setColor(fg);
                     graphics.drawRect(x, y + this._replHeight + 1, keyWidth, this._keyHeight);
                     graphics.drawText(key, x, y + this._replHeight + 2, 4, keyWidth - 1);
                     this.setThemeAttributesSpecial(null, graphics);
                     graphics.popContext();
                  } else {
                     graphics.setFont(this._keyFont);
                     graphics.drawRect(x, y + this._replHeight + 1, keyWidth, this._keyHeight);
                     graphics.drawText(key, x, y + this._replHeight + 2, 4, keyWidth - 1);
                  }
               }

               x += keyWidth + this.HORIZONTAL_SPACE;
            }
         }

         i++;
      }
   }

   protected void paintSymbol(Graphics graphics, char symbol, int x, int y, int width, int height) {
      if (symbol != 8646) {
         graphics.drawText(symbol, x, y, 4, width);
         this._symbolFont.getGlyphMetrics(symbol, this._glyphMetrics);
         this._maxIBearingY = Math.max(this._maxIBearingY, -(this._symbolFont.getAscent() + this._symbolFont.getLeading() + this._glyphMetrics.iBearingY));
      } else {
         if (this.this$0._inputMethodID == 512) {
            int icon = 0;
            if (icon != -1) {
               MediaIcon.COLLECTION.paint(graphics, x, y - 2, width, height + 6, icon);
               return;
            }
         } else {
            int icon = this._currentPage < this._pages.length - 1 ? 0 : (this.isCycling() ? 4 : -1);
            if (icon != -1) {
               MediaIcon.COLLECTION.paint(graphics, x, y, width, height, icon);
            }
         }
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      int symbol = this._map.get(Character.toUpperCase(this.getFocusChar()));
      return this.processSymbol(symbol);
   }

   private boolean processSymbol(int symbol) {
      this._swapPages = false;
      if (symbol != -1) {
         if (symbol == 8646) {
            this._swapPages = true;
            if (!this.nextPage(true, true)) {
               this.this$0.close();
               return true;
            } else {
               this.invalidate();
               return true;
            }
         } else {
            if (this._pages[this._currentPage] >= this._pagesStandard
               || this.this$0._targetEditField != null && this.this$0._targetEditField.validate((char)symbol)) {
               this.send((char)symbol);
               this.this$0.close();
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected void reset() {
      this._pagesStandard = 2;
      this._pages = this.getPages();
      this._currentPage = -1;
      this.nextPage(true, true);
   }

   void send(char ch) {
      if (ch != ' ' && (this.this$0._targetEditField == null || this.this$0._targetEditField.validate(ch))) {
         int time = 0;
         Screen screen = this.this$0.getTarget().getScreen();
         screen.processKeyEvent(513, ch, 32768, time);
      }
   }

   protected void setDescription(String description) {
      this._description = description;
      this.invalidate();
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   protected void updateDescription() {
      this.setDescription(null);
   }

   protected void updatePageNumber() {
      if (this.this$0._inputMethodID == 512) {
         if (this.this$0._header != null) {
            this.this$0._header.setPageNumber(this._currentPage + 1);
            return;
         }
      } else {
         Object[] params = new Object[]{new Integer(this._currentPage + 1), new Integer(this._pages.length)};
         this._pageBuffer.setLength(0);
         this._pageFormatter.format(params, this._pageBuffer, null);
      }
   }
}
