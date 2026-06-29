package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TextChangeListener;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringProvider;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.ArticInterface;
import net.rim.device.internal.ui.ArticInterface$LayoutRun;
import net.rim.device.internal.ui.ArticInterface$Line;
import net.rim.device.internal.ui.ArticInterface$LineInfo;
import net.rim.device.internal.ui.Cursor;
import net.rim.device.internal.ui.FormatParams;
import net.rim.device.internal.ui.Formatter;
import net.rim.device.internal.ui.Formatter$TextRenderer;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.InputMethodRequests;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.itie.EventHandler;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;
import net.rim.tid.text.AttributedString$Picture;
import net.rim.tid.text.AttributedTextIterator;
import net.rim.tid.text.BreakIterator;
import net.rim.tid.text.TextHitInfo;
import net.rim.vm.Memory;
import net.rim.vm.TraceBack;
import net.rim.vm.WeakReference;

public class TextField extends Field implements InputMethodRequests, FieldLabelProvider, Formatter$TextRenderer, AccessibleText {
   private byte _caretShape = 4;
   private boolean _selecting;
   private int _cursor;
   private boolean _cursorLeadingEdge;
   private boolean _cursorIsAtBidiBorder;
   private int _anchor;
   private ArticInterface$Line _lineList;
   private int _lineCount;
   private ArticInterface$Line _cursorLine;
   private int _cursorLineStart;
   private int _cursorLineTop;
   private boolean _isDefaultR2L;
   private int _preferredXCoord = -1;
   private boolean _inMoveFocus;
   AttributedString _text;
   private Object _imCookie;
   private int _width = -1;
   private int _maxNumChars = 1000000;
   private TextFilter _filter;
   private int _themeGeneration;
   private Font _font;
   private int _composedStart;
   private int _composedEnd;
   private long _composedTextAttributeMask;
   private int _convertedCharactersCount;
   private int _latestCommittedStart;
   private int _latestCommittedEnd;
   boolean _isLabelOwnLine;
   private int _labelLength;
   private int _lastLabelLineTop;
   private String _labelSet;
   private XYRect _tempRect = new XYRect();
   private TextHitInfo _tempDocPosInfo = new TextHitInfo();
   private ArticInterface$LineInfo _tempLineInfo = new ArticInterface$LineInfo();
   private AttributedString$Iterator _iterator;
   private long _lastFieldFull;
   private Runnable _fieldFullMsgInvoker;
   private boolean _pasteable = true;
   private boolean _cursorPositionSet;
   private AbstractStringWrapper _stringData = AbstractStringWrapper.createInstance("");
   private boolean _notifyingTextChanged = false;
   private int _focusOffset = -1;
   private int _focus_x;
   private int _focus_y;
   private int _focus_width;
   private int _focus_height;
   private boolean _isPreLayout = true;
   private boolean _inLayout = false;
   private int _formatThreadId = -1;
   private FormatParams _formatParams = new FormatParams();
   private int _lastFormatLength;
   protected long _insertionAttrib;
   protected long _insertionXAttrib;
   private boolean _composed_highlighted;
   private Object[] _textChangeListeners;
   private boolean _navigationClickHandled;
   private boolean _isAutoSelectModeOn;
   private TextField$ConvertedString _convertedString;
   private Screen _dependentScreen = null;
   private boolean _isUnicodeInputAllowed = true;
   private static Tag TAG_EDIT = Tag.create("edit");
   private static final boolean EDIT_DEBUG = false;
   public static final int DEFAULT_MAXCHARS = 1000000;
   public static final long IM_NO_PREDICTION = 128L;
   public static final long IM_PROVIDE_SUPPLEMENTARY_INPUT = 256L;
   public static final long IM_USE_ADDR_BOOK = 512L;
   public static final long CONSUME_INPUT = 1024L;
   public static final long NO_IM_SIDE_CONVERSION = 2048L;
   public static final int JUMP_FOCUS_AT_END = 4096;
   public static final long NO_KEY_DOWN_PROCESS = 8192L;
   public static final long NO_LEARNING = 16384L;
   public static final long NO_AUTOSPACE = 32768L;
   public static final long NO_LOOKUP = 536870912L;
   public static final long NO_COMPLEX_INPUT = 1073741824L;
   public static final long NO_NEWLINE = 2147483648L;
   private static MenuItem _clearFieldItem = new TextField$1(CommonResource.getBundle(), 2, 219392, Integer.MAX_VALUE);
   private static MenuItem _showSymbolsItem = new TextField$2(CommonResource.getBundle(), 10065, 50680656, Integer.MAX_VALUE);
   private static WeakReference _toggleInputItem = new WeakReference(null);
   protected static final long DEFAULT_FONT_ATTRIB_MASK = 536870912L;
   protected static final long FONT_ATTRIB_MASK = 675086335L;
   private static final int MAX_LINES_TO_FORMAT = 20;
   static final long COOKIES_ATTRIB_MASK = 65504L;
   static final int COOKIES_ATTRIB_SHIFT = 5;

   public boolean isUnicodeInputAllowed() {
      return this._isUnicodeInputAllowed;
   }

   public void setAllowUnicodeInput(boolean allow) {
      if (this._isUnicodeInputAllowed != allow) {
         this._isUnicodeInputAllowed = allow;
         if (this._labelSet != null) {
            this.setLabel(this._labelSet);
         }
      }
   }

   protected void findDefaultDirectionality(String text) {
      this._isDefaultR2L = false;
      boolean isSet = false;

      for (int i = 0; i < text.length(); i++) {
         int type = CharacterUtilities.getBidiType(text.charAt(i)) & 127;
         if (type != 0) {
            switch (type) {
               case 0:
               case 3:
                  this._isDefaultR2L = true;
                  break;
               case 1:
               case 2:
               case 4:
               default:
                  this._isDefaultR2L = false;
            }

            isSet = true;
            break;
         }
      }

      if (!isSet) {
         Locale l = this.isEditable() ? Locale.getDefaultInput() : Locale.getDefault();
         String lang = l.getLanguage();
         this._isDefaultR2L = lang.equals("ar") || lang.equals("he");
      }
   }

   TextFilter getFilterFromStyle(long style) {
      return null;
   }

   protected void updateInputStyle() {
      SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      controlObject.actionPerformed(38, null);
      int style = (int)this.getStyle();
      if (this._filter != null) {
         style = (int)(style | this._filter.getPreferredInputStyle());
      }

      controlObject.setTextInputStyle(style);
      controlObject.setFilter(this._filter);
   }

   public int getTextInputStyle() {
      int style = (int)this.getStyle();
      if (this._filter != null) {
         style = (int)(style | this._filter.getPreferredInputStyle());
      }

      return style;
   }

   final void fireInputMethodTextChanged(InputMethodEvent event) {
      Object[] listeners = this._textChangeListeners;
      if (listeners != null) {
         for (int index = 0; index < listeners.length; index++) {
            ((TextChangeListener)listeners[index]).inputMethodTextChanged(this, event);
         }
      }
   }

   public int getConvertedCharactersCount() {
      return this._convertedCharactersCount;
   }

   final void fireTextChangeEvent(int eventID) {
      Object[] listeners = this._textChangeListeners;
      if (listeners != null) {
         for (int index = 0; index < listeners.length; index++) {
            ((TextChangeListener)listeners[index]).textValueChanged(this, eventID);
         }
      }
   }

   protected synchronized void hitTest(int aIndex, XYRect aResult) {
      if (aIndex < 0 || aIndex > this._text.length()) {
         throw new IllegalArgumentException();
      }

      if (this._isPreLayout) {
         aResult.set(0, 0, 0, 0);
      } else {
         ArticInterface$LineInfo info = this.getLineInfoForDocPos(aIndex, true);

         try {
            ArticInterface.DocPosToCaret(aResult, this._text, info._line, info._start, info._top, aIndex, true);
         } catch (IllegalStateException e1) {
            aResult.set(0, 0, 0, 0);
            e1.printStackTrace();
            Formatter.reportArticException(
               this._text, 0, null, aIndex, true, 0, 0, 0, this._cursorLineStart, this._cursorLineTop, this._cursorLine, this._lineList
            );
         } catch (IllegalArgumentException e2) {
            aResult.set(0, 0, 0, 0);
            e2.printStackTrace();
            Formatter.reportArticException(
               this._text, 0, null, aIndex, true, 0, 0, 0, this._cursorLineStart, this._cursorLineTop, this._cursorLine, this._lineList
            );
         }

         aResult.height = aResult.y + aResult.height - info._top;
         aResult.y = info._top;
         aResult.width = 0;
      }
   }

   public final synchronized void removeTextChangeListener(TextChangeListener listener) {
      this._textChangeListeners = ListenerUtilities.removeListener(this._textChangeListeners, listener);
   }

   public final synchronized void addTextChangeListener(TextChangeListener listener) {
      this._textChangeListeners = ListenerUtilities.addListener(this._textChangeListeners, listener);
   }

   public void setAdjustAlignments(boolean adjustAlignments) {
   }

   protected void endLayoutUpdate() {
      if (!this._inLayout) {
         this.updateLayoutNowOrLater();
      }
   }

   protected void startLayoutUpdate() {
      if (!this._inLayout) {
         this._isPreLayout = true;
      }
   }

   public void setPreLayoutInternal(boolean b) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      this._isPreLayout = b;
   }

   protected synchronized int replace(
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
      this._selecting = this._isAutoSelectModeOn = false;
      int old_length = this.getLabelLength() + this.getDecodedTextLength();
      if (aStart < 0) {
         aStart = 0;
      } else if (aStart > old_length) {
         aStart = old_length;
      }

      if (aEnd < aStart) {
         int temp = aEnd;
         aEnd = aStart;
         aStart = temp;
      } else if (aEnd > old_length) {
         aEnd = old_length;
      }

      int inserted_len = aIterator.length();
      int removed_len = aEnd - aStart;
      boolean fieldFull = false;
      int new_length = old_length + inserted_len - removed_len;
      if (new_length - this._labelLength > this._maxNumChars) {
         if (inserted_len != aCommittedLen) {
            if (this.isComposedTextExist()) {
               this._text.setAttribToZero(this._composedStart, this._composedEnd, 786432);
               this._text.setAttribToZero(this._composedStart, this._composedEnd, 67108864);
               this._latestCommittedStart = this._composedStart;
               this._latestCommittedEnd = this._composedEnd;
               this._composedStart = this._composedEnd;
               this.setCursorPosition(this._composedStart - this._labelLength);
            }

            this.update(this._cursor, 0, 0, 0, true, true);
            this.displayFieldFullMessage();
            return 1;
         }

         inserted_len = this._maxNumChars + this._labelLength - old_length + removed_len;
         aCommittedLen = inserted_len;
         int start = aIterator.pos();
         aIterator.set(start, start + inserted_len);
         fieldFull = true;
      }

      String textBefore = this._text.getText(aStart, aEnd);
      this._text.replace(aStart, aEnd, aIterator, aIteratorMask, aIteratorXMask);
      int old_composed_start = this._composedStart;
      this._composedStart += aCommittedLen;
      this._composedEnd = old_composed_start + inserted_len;
      this.update(aStart, removed_len, inserted_len, aCommittedLen + aPosInsideComposedText, aMoveCursor, true);
      if (Ui.isTTSEnabled()) {
         String textAfter = this._text.getText(aStart, aEnd + inserted_len - removed_len);
         this.accessibleEventOccurred(2, textBefore, textAfter, this);
      }

      FieldChangeListener listener = this.getChangeListener();
      if (this._isPreLayout) {
         this.setChangeListener(null);
      }

      this.fieldChangeNotify(aContext);
      if (this._isPreLayout) {
         this.setChangeListener(listener);
      }

      if (aCommittedLen != 0) {
         this._latestCommittedStart = old_composed_start;
         this._latestCommittedEnd = old_composed_start + aCommittedLen;
      }

      if (fieldFull) {
         this.displayFieldFullMessage();
         return 1;
      } else {
         return 0;
      }
   }

   public int replace(int aStart, int aEnd, AttributedString$Iterator aIterator, long aIteratorMask, long aIteratorXMask) {
      return this.replace(aStart, aEnd, aIterator, aIteratorMask, aIteratorXMask, aIterator.length(), 0, true, 0);
   }

   public void insert(int aPos, AttributedString$Iterator aIterator, long aIteratorAttrMask, long aIteratorXAttribMask) {
      if (!this.isEditable()) {
         throw new IllegalStateException("Attempt to modify a READ_ONLY field.");
      }

      this.replace(aPos, aPos, aIterator, aIteratorAttrMask, aIteratorXAttribMask, aIterator.length(), 0, true, 0);
   }

   protected void handleCursorPositionChanged() {
      this.setInsertionAttributesToSelection();
   }

   protected void invalidateFocusRect() {
      this._focusOffset = -1;
   }

   public void setAttrib(int aStartPos, int aEndPos, long aAttrib, long aAttribMask, long aXAttrib, long aXAttribMask) {
      int pos = aStartPos;
      int count = aEndPos - aStartPos;
      if (count < 0) {
         pos = aEndPos;
         count = -count;
      }

      this._text.seek(pos);
      this._text.setAttrib(count, aAttrib, aAttribMask, aXAttrib, aXAttribMask);
      this.update(pos, count, count, 0, false, false);
      if (this._cursor >= aStartPos && this._cursor <= aEndPos) {
         this.setInsertionAttributesToSelection();
      }
   }

   public int getComposedTextLength() {
      return this._composedEnd - this._composedStart;
   }

   public void deleteAttrib(long aAttribMask) {
      this.setAttrib(this._cursor, this._anchor, 0, aAttribMask, 0, 0);
   }

   public void setAttrib(long aAttrib, long aAttribMask) {
      this.setAttrib(this._cursor, this._anchor, aAttrib, aAttribMask, 0, 0);
   }

   public void setFontHeight(int height) {
      this.setAttrib(this._cursor, this._anchor, height << 0, 63, 0, 0);
   }

   protected synchronized int backspace(int count, int context) {
      if (this._cursor - count < this._labelLength) {
         count = this._cursor - this._labelLength;
      }

      if ((context & -2147483648) == 0 && !this.isEditable()) {
         return 0;
      }

      if (count == 0) {
         return 0;
      }

      if (this._cursor < count) {
         count -= this._cursor;
      }

      String textBefore = this._text.getText(this._cursor - count, this._cursor);
      this._text.delete(this._cursor - count, this._cursor);
      this.update(this._cursor - count, count, 0, 0, true, true);
      if (Ui.isTTSEnabled()) {
         this.accessibleEventOccurred(2, textBefore, null, this);
      }

      this.fieldChangeNotify(context);
      return count;
   }

   protected synchronized boolean backspace() {
      boolean backspaceProcessed = false;
      if (this.isSelecting()) {
         backspaceProcessed = this.deleteSelectedText() != 0;
      } else if (this._cursor != this._labelLength) {
         String textBefore = this._text.getText(this._cursor - 1, this._cursor);
         this._text.delete(this._cursor - 1, this._cursor);
         this.update(this._cursor - 1, 1, 0, 0, true, true);
         if (Ui.isTTSEnabled()) {
            this.accessibleEventOccurred(2, textBefore, null, this);
         }

         this.fieldChangeNotify(0);
         backspaceProcessed = true;
      }

      if (this._latestCommittedEnd != this._latestCommittedStart && this._latestCommittedEnd > this._cursor) {
         this.resetLatestCommittedText();
      }

      return backspaceProcessed;
   }

   public Screen dependentScreen() {
      return this._dependentScreen;
   }

   public int backspace(int count) {
      return this.backspace(count, Integer.MIN_VALUE);
   }

   public char charAt(int offset) {
      if (offset < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this._text.charAt(offset + this._labelLength);
      }
   }

   public void clear(int context) {
      this.setText("", context);
   }

   public void wipe() {
      if (Memory.getSecureOldObjects()) {
         this._text.getText().wipe();
      }
   }

   protected char convert(char key, int status) {
      if (this._filter != null) {
         key = this._filter.convert(key, this.getTextAbstractString(), this.getCursorPosition(), status);
      }

      return key;
   }

   public boolean isInMoveFocus() {
      return this._inMoveFocus;
   }

   public int getMaxSize() {
      return this._maxNumChars;
   }

   public synchronized String getText() {
      return this._text.getText(this._labelLength, this.getDisplayTextLength());
   }

   public synchronized String getText(int offset, int length) {
      return this._text.getText(offset, offset + length);
   }

   public synchronized void getText(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      this._text.getText().getText(srcBegin + this._labelLength, srcEnd + this._labelLength, dst, dstBegin);
   }

   public AbstractString getTextAbstractString() {
      return this._text.getText().getAbstractString();
   }

   public int getTextLength() {
      return this.getDisplayTextLength() - this._labelLength;
   }

   public int getDecodedTextLength() {
      return this.getDecodedTextLength(this.getLabelLength(), this._text.length());
   }

   public int getDecodedTextLength(int start, int end) {
      return end - start;
   }

   public int insert(String text) {
      return this.insert(text, Integer.MIN_VALUE, true, true);
   }

   public void insert(AttributedString$Picture picture) {
      this.insert(null, picture, Integer.MIN_VALUE, false, false);
   }

   protected int insert(String text, int context) {
      return this.insert(text, null, context, true, true);
   }

   public int insert(String text, int context, boolean stripInvalid, boolean validateText) {
      return this.insert(text, null, context, stripInvalid, validateText);
   }

   public synchronized int insert(String text, AttributedString$Picture picture, int context, boolean stripInvalid, boolean validateText) {
      if (text == null && picture == null) {
         text = "";
      }

      this.resetComposedText();
      if (stripInvalid && this.isStyle(2147483648L)) {
         text = this.removeNewlines(text);
      }

      if (validateText && !stripInvalid && !this.validate(text)) {
         return 0;
      }

      if (validateText && stripInvalid) {
         text = this.convert(text, this._cursor);
      }

      int textLength = text == null ? 1 : text.length();
      int count = Math.min(textLength, this._maxNumChars - this.getDecodedTextLength());
      boolean fieldFull = false;
      if (text != null && textLength > count) {
         if (count <= 0) {
            this.displayFieldFullMessage();
            return 0;
         }

         fieldFull = true;
         text = text.substring(0, count);
         textLength = count;
      }

      if (this._latestCommittedEnd - this._latestCommittedStart > 0 && this._latestCommittedEnd > this._cursor) {
         this.resetLatestCommittedText();
      }

      if (text != null) {
         this._iterator.set(this._cursor, this._cursor);
         this._text.setInsertAttrib(this._iterator.runAttrib());
         this._text.setInsertXAttrib(this._iterator.runXAttrib());
         this._text.insert(this._cursor, text);
      } else if (picture != null) {
         this._text.insert(this._cursor, picture);
      }

      this.update(this._cursor, 0, textLength, textLength, true, true);
      if (Ui.isTTSEnabled() && text != null) {
         this.accessibleEventOccurred(2, null, text, this);
      }

      this.fieldChangeNotify(context);
      if (fieldFull) {
         this.displayFieldFullMessage();
      }

      return textLength;
   }

   int getLayoutWidth(int width) {
      return width;
   }

   void handleLinesAfterFormat(FormatParams params) {
      this._lineCount = params._lineCount;
   }

   protected int getMaxLinesToFormat() {
      return Math.max(20, Display.getHeight() / this.getFont().getHeight() + 1);
   }

   public void inMoveFocus(boolean enable) {
      this._inMoveFocus = enable;
   }

   protected void setCursorPositionSet(boolean set) {
      this._cursorPositionSet = set;
   }

   public boolean isFieldFull() {
      return this.getDecodedTextLength() >= this._maxNumChars;
   }

   protected void update(int delta) {
   }

   protected void notifyTextChanged(FormatParams aParams, boolean aIsInsertionOrDeletion) {
   }

   public synchronized void setMaxSize(int maxSize) {
      if (maxSize < 0) {
         throw new IllegalArgumentException();
      }

      if (maxSize < this.getDecodedTextLength()) {
         int pos = this.getCursorPosition();
         this.setText(this.getText().substring(0, maxSize));
         if (pos > maxSize) {
            pos = maxSize;
         }

         this.setCursorPosition(pos, this.isCursorPositionSet() ? 0 : Integer.MIN_VALUE);
      }

      this._maxNumChars = maxSize;
   }

   public void setCursorPosition(int offset) {
      this.setCursorPosition(offset, this.isEditable() ? 0 : Integer.MIN_VALUE);
   }

   public int getCursorPosition() {
      return this._cursor - this._labelLength;
   }

   public void setFilter(TextFilter filter) {
      String oldText = this.getText();
      this._filter = filter;
      SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      if (controlObject != null) {
         controlObject.setFilter(this._filter);
      }

      this.setText("");
      this.insert(oldText);
   }

   public TextFilter getFilter() {
      return this._filter;
   }

   public void setText(String text) {
      this.setText(text, Integer.MIN_VALUE);
   }

   protected void setText(String text, int context) {
      this.setTextInternal(text, context, true);
   }

   public void setAttributedText(AttributedString text) {
      if (this.isStyle(2147483648L)) {
         this.removeNewlines(text);
      }

      String strText = text.getText(0, text.length());
      if (!this.validate(strText)) {
         throw new IllegalArgumentException("invalid text");
      }

      this.select(false);
      this.resetComposedText();
      if (text.length() > this._maxNumChars) {
         text.delete(this._maxNumChars, text.length());
      }

      AttributedString$Iterator iter = text.getIterator();
      int oldLen = this._text.length();
      int newLen = iter.length();
      this.replace(this._labelLength, oldLen, iter, -1, -1, newLen, 0, this.isEditable() || this._cursor > this._labelLength, Integer.MIN_VALUE);
      this.wipe();
   }

   protected boolean validate(char character) {
      if ((character > 31 || character == '\n') && (127 > character || character > 159)) {
         return this._filter != null ? this._filter.validate(character, this.getTextAbstractString(), this.getCursorPosition()) : true;
      } else {
         return false;
      }
   }

   protected boolean validate(String text) {
      if (this._filter != null) {
         this._stringData.reset(text);
         return this._filter.validate(this._stringData);
      } else {
         return true;
      }
   }

   protected int getLineTop(int aIndex) {
      int y = 0;
      if (aIndex >= 0 && aIndex <= this._lineCount) {
         int i = 0;

         for (ArticInterface$Line currentLine = this._lineList; currentLine != null && i < aIndex; i++) {
            y += currentLine._boundsBottom - currentLine._boundsTop;
            currentLine = currentLine._next;
         }

         return y;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected int getLineTop(ArticInterface$Line aLine) {
      int y = 0;

      for (ArticInterface$Line currentLine = this._lineList; currentLine != aLine; currentLine = currentLine._next) {
         y += currentLine._boundsBottom - currentLine._boundsTop;
      }

      return y;
   }

   protected int getDisplayLinePosition(int aIndex) {
      return this.getLineTop(aIndex);
   }

   public ArticInterface$LineInfo getLineInfoForYPos(int aY) {
      this._tempLineInfo._start = this._cursorLineStart;
      this._tempLineInfo._top = this._cursorLineTop;
      this._tempLineInfo._line = this._cursorLine;
      Formatter.getLineInfoForYPos(aY, this._tempLineInfo);
      return this._tempLineInfo;
   }

   public ArticInterface$LineInfo getLineInfoForDocPos(int aDocPos, boolean aLeadingEdge) {
      this._tempLineInfo._line = this._cursorLine;
      this._tempLineInfo._start = this._cursorLineStart;
      this._tempLineInfo._top = this._cursorLineTop;
      Formatter.getLineInfoForDocPos(aDocPos, aLeadingEdge, this._lineList, this._tempLineInfo, true);
      return this._tempLineInfo;
   }

   public void setSelection(int aNewCursor, boolean aNewCursorLeadingEdge, int aNewAnchor) {
      this.setSelection(aNewCursor, aNewCursorLeadingEdge, aNewAnchor, true);
   }

   protected synchronized int scrollHorizontally(int amount) {
      int labelLength = this.getLabelLength();
      if ((this._cursorLine._flags & 16) != 0) {
         amount = -amount;
      }

      if ((this._cursor != labelLength || amount >= 0) && (this._cursor != this.getDisplayTextLength() || amount <= 0)) {
         this._tempDocPosInfo.set(this._cursor, this._cursorLeadingEdge);
         int usedAmount = ArticInterface.AdjustDocPos(this._text.getText(), this._tempDocPosInfo, amount);
         int new_cursor = this._tempDocPosInfo._index;
         int result = amount - usedAmount;
         if (new_cursor < labelLength) {
            result += new_cursor - labelLength;
            new_cursor = labelLength;
         } else if (new_cursor < this.getDisplayTextLength()) {
            int focus_x = 0;
            this.getLineInfoForDocPos(new_cursor, this._cursorLeadingEdge);
            ArticInterface$Line cursorLine = this._tempLineInfo._line;
            int cursorLineStart = this._tempLineInfo._start;
            int rightMargin = this.getRightMargin();
            if (amount < 0) {
               while (true) {
                  focus_x = this.getCaretX(new_cursor, this._cursorLeadingEdge);
                  if (new_cursor == cursorLineStart || focus_x <= rightMargin) {
                     break;
                  }

                  new_cursor--;
               }
            } else {
               focus_x = this.getCaretX(new_cursor, this._cursorLeadingEdge);
               if (focus_x > rightMargin) {
                  if (cursorLine._next == null) {
                     while (true) {
                        focus_x = this.getCaretX(new_cursor, this._cursorLeadingEdge);
                        if (new_cursor == cursorLineStart) {
                           return 1;
                        }

                        if (focus_x <= rightMargin) {
                           return 1;
                        }

                        new_cursor--;
                     }
                  }

                  new_cursor = cursorLineStart + this.getLineLength(cursorLine);
                  int var11 = false;
               }
            }
         }

         boolean newLeadingEdge = true;
         if (this.posIsAtBidiBorder(new_cursor, false)) {
            newLeadingEdge = amount < 0;
         }

         if (this._selecting) {
            this.setSelection(new_cursor, newLeadingEdge, this._anchor, true);
         } else {
            this.setSelection(new_cursor, newLeadingEdge, new_cursor, true);
         }

         return result;
      } else {
         return this._selecting ? 0 : amount;
      }
   }

   protected synchronized int scrollVertically(int amount) {
      if (this.isComposedTextExist()) {
         return 0;
      }

      int labelLength = this.getLabelLength();
      ArticInterface$LineInfo cursorLineInfo = this.getCursorLine();
      int cursorLineTop = cursorLineInfo._top;
      ArticInterface$Line endLine = cursorLineInfo._line;
      int endLineTop = cursorLineTop;
      int endLineStart = cursorLineInfo._start;
      boolean isOffTheBottom = false;
      int amountRemain1 = amount;
      if (amount >= 0) {
         for (int index = 0; index < amount && endLine._next != null; index++) {
            endLineTop += endLine._boundsBottom - endLine._boundsTop;
            endLine = endLine._next;
            amountRemain1--;
         }

         isOffTheBottom = amountRemain1 > 0;
      } else {
         for (int index = amount; index < 0 && endLineTop > this._lastLabelLineTop && endLineStart > labelLength; index++) {
            endLine = endLine._prev;
            endLineTop -= endLine._boundsBottom - endLine._boundsTop;
            amountRemain1++;
            endLineStart -= endLine._textLength + endLine._skippedCharacters;
         }
      }

      while (endLineTop < this._lastLabelLineTop) {
         if (endLine == null) {
            throw new IllegalStateException();
         }

         endLineTop += endLine._boundsBottom - endLine._boundsTop;
         endLine = endLine._next;
      }

      int amountRemain = 0;
      if (amount < 0 && (amountRemain1 < 0 || this._cursor == labelLength) && endLine != null) {
         this.setPreferredXToLineStartOrEnd(endLine, true);
         if (!this._selecting) {
            amountRemain = amountRemain1;
         }
      }

      if (isOffTheBottom) {
         this.setPreferredXToLineStartOrEnd(endLine, false);
         if (!this._selecting && ((this.getStyle() & 4096) == 0 || this._cursor == this.getDisplayTextLength())) {
            amountRemain = amountRemain1;
         }
      }

      if (this._preferredXCoord == -1) {
         this._preferredXCoord = this.getCaretX(this._cursor, this._cursorLeadingEdge);
      }

      this.getDocPos(this._preferredXCoord, endLineTop, this._tempDocPosInfo);
      int pos = Math.max(this._tempDocPosInfo._index, labelLength);
      if (this._selecting) {
         this.setSelection(pos, this._tempDocPosInfo._leadingEdge, this._anchor, false);
      } else {
         this.setSelection(pos, this._tempDocPosInfo._leadingEdge, pos, false);
      }

      return amountRemain;
   }

   protected void setFormatFlags(int flags) {
      this._formatParams.setFormatFlags(flags);
   }

   protected void setDefaultInsertionAttributes() {
      this.setFontInsertionAttributes(this.getDefaultFontAttributes());
   }

   protected long getDefaultFontAttributes() {
      long attribs = Ui.getAttributesFromFont(this.getFont()) | 536870912 | Ui.DEFAULT_COLOR_ATTRIBS;
      if (this._isDefaultR2L) {
         attribs |= 16777216;
      }

      return attribs;
   }

   synchronized void getCaretRect(int aDocPos, boolean aLeadingEdge, XYRect aCaret) {
      if (this._lineCount == 0) {
         throw new IllegalStateException();
      }

      if (this._isPreLayout) {
         aCaret.set(0, 0, 0, 0);
      } else {
         try {
            ArticInterface.DocPosToCaret(aCaret, this._text, this._cursorLine, this._cursorLineStart, this._cursorLineTop, aDocPos, aLeadingEdge);
         } catch (IllegalStateException e1) {
            aCaret.set(0, 0, 0, 0);
            e1.printStackTrace();
            Formatter.reportArticException(
               this._text, 0, null, aDocPos, aLeadingEdge, 0, 0, 0, this._cursorLineStart, this._cursorLineTop, this._cursorLine, this._lineList
            );
         } catch (IllegalArgumentException e2) {
            aCaret.set(0, 0, 0, 0);
            e2.printStackTrace();
            Formatter.reportArticException(
               this._text, 0, null, aDocPos, aLeadingEdge, 0, 0, 0, this._cursorLineStart, this._cursorLineTop, this._cursorLine, this._lineList
            );
         }
      }
   }

   void getDocPos(int aX, int aY, TextHitInfo aDocPosInfo) {
      if (this._lineCount == 0) {
         throw new IllegalStateException();
      }

      if (this._isPreLayout) {
         aDocPosInfo.set(0, true);
      } else {
         try {
            ArticInterface.PointToDocPos(aDocPosInfo, null, this._text, this._cursorLine, this._cursorLineStart, this._cursorLineTop, aX, aY);
         } catch (IllegalStateException e1) {
            e1.printStackTrace();
            Formatter.reportArticException(
               this._text, 0, null, -1, false, aX, aY, 0, this._cursorLineStart, this._cursorLineTop, this._cursorLine, this._lineList
            );
         } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            Formatter.reportArticException(
               this._text, 0, null, -1, false, aX, aY, 0, this._cursorLineStart, this._cursorLineTop, this._cursorLine, this._lineList
            );
         }
      }
   }

   protected void highlightSelectedArea(Graphics graphics, boolean on, int selectionStart, int selectionEnd) {
      XYRect extent = this.getContentRect();
      XYRect clip = graphics.getClippingRect();
      if (extent.width >= clip.x && 0 <= clip.X2() && extent.height >= clip.y && 0 <= clip.Y2()) {
         if (selectionStart > selectionEnd) {
            int temp = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = temp;
         }

         ArticInterface$LineInfo info = this.getLineInfoForYPos(clip.y + clip.height);
         int endLineEnd = info._start + info._line._textLength + info._line._skippedCharacters;
         int clippedSelectionEnd = Math.min(selectionEnd - 1, endLineEnd);
         this.hitTest(clippedSelectionEnd, this._tempRect);
         int endLineTop = this._tempRect.y;
         info = this.getLineInfoForYPos(clip.y);
         int lineStart = info._start;
         selectionStart = Math.max(selectionStart, lineStart);
         if (selectionStart != lineStart) {
            info = this.getLineInfoForDocPos(selectionStart, true);
         }

         ArticInterface$Line line = info._line;
         int y = info._top;
         int offset = info._start;

         while (y <= endLineTop && line != null) {
            int offset1 = offset <= selectionStart ? selectionStart : offset;
            int offset2 = offset + line._textLength > selectionEnd ? selectionEnd : offset + line._textLength;
            Formatter.getTextBounds(offset1, offset2, this._tempRect, line, offset, y);
            this.drawHighlightRegion(graphics, 2, on, this._tempRect.x, this._tempRect.y, this._tempRect.width, this._tempRect.height);
            y += this._tempRect.height;
            offset += line._textLength + line._skippedCharacters;
            line = line._next;
         }
      }
   }

   public void autoSelectFullText() {
      UiApplication.getUiApplication().invokeLater(new TextField$4(this));
   }

   public void setPasteable(boolean pasteable) {
      this._pasteable = pasteable;
   }

   protected boolean insert(char key, int status) {
      key = this.convert(key, status);
      if (!this.validate(key)) {
         return false;
      }

      if (this.isSelecting()) {
         this.selectionDelete();
      }

      if (this.isFieldFull()) {
         this.displayFieldFullMessage();
         return false;
      }

      if (this.validate(key)) {
         this._text.insert(this._cursor, key);
         if (this._latestCommittedEnd - this._latestCommittedStart > 0 && this._latestCommittedEnd > this._cursor) {
            this.resetLatestCommittedText();
         }

         this.update(this._cursor, 0, 1, 1, true, true);
         if (Ui.isTTSEnabled()) {
            this.accessibleEventOccurred(2, null, String.valueOf(key), this);
         }

         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isClearMenuItemAllowed() {
      return this.getTextLength() != 0;
   }

   protected boolean isSymbolScreenAllowed() {
      return this.isEditable() && !SymbolScreen.getSymbolScreen().isEmpty(this);
   }

   protected ArticInterface$LineInfo getCursorLine() {
      this.getLineInfoForDocPos(this._cursor, this._cursorLeadingEdge);
      return this._tempLineInfo;
   }

   protected void displayFieldFullMessage() {
      if (this._lastFieldFull + 2000 < System.currentTimeMillis()) {
         this._lastFieldFull = System.currentTimeMillis();
         Screen screen = this.getScreen();
         if (screen != null && this._fieldFullMsgInvoker == null) {
            this._fieldFullMsgInvoker = new TextField$5(this, screen);
            Application.getApplication().invokeLater(this._fieldFullMsgInvoker);
         }
      }
   }

   protected int getDisplayLineCount() {
      return this._lineCount;
   }

   protected StringBufferGap getDisplayText() {
      return this._text.getText();
   }

   protected int getDisplayTextLength() {
      return this._text.length();
   }

   public void setCaretPosition(int offset) {
      this.setCursorPosition(offset - this._labelLength, 0);
   }

   protected void setCursorPosition(int offset, int context) {
      offset += this._labelLength;
      if (offset >= this._labelLength && this.getDisplayTextLength() >= offset) {
         this._text.getText().seek(offset);
         this.setCursorPosition(offset, true, context);
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected boolean isCursorPositionSet() {
      return this._cursorPositionSet;
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public void setLabel(String newLabel) {
      this.resetComposedText();
      int oldLabelLength = this._labelLength;
      newLabel = this.handleLabelOwnLine(newLabel);
      AttributedString attribStr = new AttributedString(newLabel, this.getDefaultFontAttributes(), 0);
      AttributedString$Iterator iter = attribStr.getIterator();
      this.replace(0, oldLabelLength, iter, -1, -1, iter.length(), 0, true, Integer.MIN_VALUE);
      if (!this._isPreLayout) {
         this.layoutLabel();
      }

      this._text.getText().setLabelLength(this._labelLength);
   }

   @Override
   public String getLabel() {
      return this._labelSet;
   }

   @Override
   public AttributedTextIterator getCommittedText(int beginIndex, int endIndex, String[] attributes) {
      return null;
   }

   @Override
   public AttributedTextIterator getText(int start, int end, boolean makeUncommitted) {
      return null;
   }

   @Override
   public int drawText(Graphics graphics, int offset, int length, int x, int y, DrawTextParam drawTextParam) {
      graphics.drawText(this._text.getText(), offset, length, x, y, drawTextParam, null);
      return 0;
   }

   @Override
   public int getSelectionStart() {
      return this._anchor;
   }

   @Override
   public int getSelectionEnd() {
      return this._cursor;
   }

   @Override
   public int getSelectionOffset() {
      return Math.min(this._anchor, this._cursor);
   }

   @Override
   public AttributedTextIterator getSelectedText() {
      return null;
   }

   @Override
   public AttributedTextIterator cancelLatestCommittedText() {
      return null;
   }

   @Override
   public int getCommittedTextLength() {
      return this._text.length() - (this._composedEnd - this._composedStart) - this._labelLength;
   }

   @Override
   public int getInsertPositionOffset() {
      return this.getComposedTextStart();
   }

   @Override
   public TextHitInfo getLocationOffset(int x, int y) {
      return null;
   }

   @Override
   public int getLabelLength() {
      return this._labelLength;
   }

   @Override
   public AttributedString getAttributedText() {
      return this._text;
   }

   @Override
   public int getLatestCommittedTextEnd() {
      return this._latestCommittedEnd;
   }

   @Override
   public int getLatestCommittedTextStart() {
      return this._latestCommittedStart;
   }

   @Override
   public int getAnchorPosition() {
      return this._anchor;
   }

   @Override
   public int getCaretPosition() {
      return this._cursor;
   }

   @Override
   public int getComposedTextEnd() {
      return this._composedEnd == this._composedStart ? this._cursor : this._composedEnd;
   }

   @Override
   public int getComposedTextStart() {
      return this._composedEnd == this._composedStart ? this._cursor : this._composedStart;
   }

   @Override
   public void setComposedText(int start, int end) {
      if (start >= 0 && start <= end && end <= this._text.length()) {
         this._composedStart = start;
         this._composedEnd = end;
         if (this._cursor < start || this._cursor > end + 1) {
            this.setCaretPosition(this._composedStart);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void getTextLocation(TextHitInfo offset, XYRect aResult) {
      if (aResult != null) {
         if (this._composedStart < this._composedEnd) {
            int index = offset._index;
            if (index < 0) {
               throw new IllegalArgumentException();
            }

            index += this._composedStart;
            if (index > this._composedEnd) {
               throw new IllegalArgumentException();
            }

            this.hitTest(index, aResult);
         } else if (this.isSelecting()) {
            int start = this.getAnchorPosition();
            int end = this.getCaretPosition();
            if (start > end) {
               start = end;
               end = this.getAnchorPosition();
            }

            this.hitTest(start, aResult);
            XYRect endRct = new XYRect();
            this.hitTest(end, endRct);
            if (endRct.y > aResult.y) {
               aResult.y = endRct.y;
               aResult.x = 0;
            }
         } else {
            this.hitTest(this.getCursorPosition(), aResult);
         }

         int top = this.getContentTop();
         int left = this.getContentLeft();

         for (Manager m = this.getManager(); m != null; m = m.getManager()) {
            top -= m.getVerticalScroll();
            top += m.getContentTop();
            left -= m.getHorizontalScroll();
            left += m.getContentLeft();
         }

         aResult.x += left;
         aResult.y += top;
      }
   }

   @Override
   public Object getIMCookieCache() {
      Object result = this._imCookie;
      this._imCookie = null;
      return result;
   }

   @Override
   public String getAfterIndex(int part, int index) {
      int start = 0;
      int end = 0;
      String simpleText = this._text.getString();
      switch (part) {
         case -1:
            int current = this.getLineInfoForDocPos(index, false)._start + this.getLineInfoForDocPos(index, false)._line._textLength + 1;
            if (++current < this._text.length()) {
               ArticInterface$Line line = this.getLineInfoForDocPos(current, false)._line;
               start = line == null ? 0 : this.getLineInfoForDocPos(current, false)._start;
               end = line == null ? 0 : start + line._textLength;
            }
            break;
         case 0:
         default:
            if (index < simpleText.length() - 2) {
               start = ++index;
               end = ++index;
            }
            break;
         case 1:
            BreakIterator boundary = BreakIterator.getInstance(1, Locale.getDefault());
            boundary.setText(simpleText);
            int startResult = boundary.following(index - 1);
            if (startResult != Integer.MAX_VALUE) {
               int last = boundary.following(startResult);
               if (last != Integer.MAX_VALUE) {
                  int following = boundary.following(last);
                  end = following == Integer.MAX_VALUE ? simpleText.length() : following;
                  start = startResult;
               }
            }
      }

      return this._text.getText(start, end);
   }

   @Override
   public String getAtIndex(int part, int index) {
      int start = 0;
      int end = 0;
      String simpleText = this._text.getString();
      switch (part) {
         case -1:
            if (this.getDisplayLineCount() != 0) {
               ArticInterface$Line line = this.getLineInfoForDocPos(index, false)._line;
               start = this.getLineInfoForDocPos(index, false)._start;
               end = line == null ? 0 : start + line._textLength;
            }
            break;
         case 0:
         default:
            if (index > 0) {
               start = index - 1;
            }

            end = index;
            break;
         case 1:
            BreakIterator boundary = BreakIterator.getInstance(1, Locale.getDefault());
            boundary.setText(simpleText);
            int startResult = boundary.preceding(index);
            start = startResult == Integer.MAX_VALUE ? 0 : startResult;
            int endResult = boundary.following(index);
            end = endResult == Integer.MAX_VALUE ? simpleText.length() : endResult;
      }

      return this._text.getText(start, end);
   }

   @Override
   public String getBeforeIndex(int part, int index) {
      int start = 0;
      int end = 0;
      String simpleText = this._text.toString();
      switch (part) {
         case -1:
            if (this.getDisplayLineCount() > 1) {
               int current = this.getLineInfoForDocPos(index, false)._start;
               if (--current >= 0) {
                  ArticInterface$Line line = this.getLineInfoForDocPos(current, false)._line;
                  start = line == null ? 0 : this.getLineInfoForDocPos(current, false)._start;
                  end = line == null ? 0 : start + line._textLength;
               }
            }
            break;
         case 0:
         default:
            if (index != 0) {
               start = index - 1;
               end = index;
            }
            break;
         case 1:
            BreakIterator boundary = BreakIterator.getInstance(1, Locale.getDefault());
            boundary.setText(simpleText);
            int startResult = boundary.preceding(index + 1);
            if (startResult != Integer.MAX_VALUE) {
               int proceeding = boundary.preceding(startResult - 1);
               start = proceeding == Integer.MAX_VALUE ? 0 : proceeding;
               end = startResult;
            }
      }

      return this._text.getText(start, end);
   }

   @Override
   protected boolean navigationUnclick(int status, int time) {
      if (this._navigationClickHandled) {
         this._navigationClickHandled = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getPreferredHeight() {
      int height = this.getFont().getHeight();
      if (this._isLabelOwnLine) {
         height *= 2;
      }

      return height;
   }

   private synchronized void setAutoSelectFullText() {
      if (this.isEditable() && this.getTextLength() > 0) {
         this._isAutoSelectModeOn = true;
         this.setCursorPosition(0, 0);
         this.select(true);
         this.setCursorPosition(this.getTextLength(), 0);
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int result = 0;
      int cursorBeforeMove = this._cursor;
      if (this._selecting) {
         if ((status & 1) == 0 && (!Trackball.isSupported() || (status & 65536) == 0)) {
            if (0 == (status & 2)) {
               this.scrollVertically(amount);
            } else if (Trackball.isSupported() && (status & 65536) == 0) {
               this.scrollVertically(amount);
            } else {
               this.scrollHorizontally(amount);
            }
         } else {
            this.scrollHorizontally(amount);
         }

         if (this._isAutoSelectModeOn && 0 == (status & 2)) {
            this.select(false);
         }
      } else if (0 != (status & 2)) {
         if (this.isSelectable()) {
            this.select(true);
         }
      } else if ((status & 1) == 0 && (!Trackball.isSupported() || (status & 65536) == 0)) {
         result = this.scrollVertically(amount);
      } else {
         result = this.scrollHorizontally(amount);
      }

      this._text.getText().seek(this._cursor);
      if (amount != result && Ui.isTTSEnabled()) {
         AccessibleEventDispatcher.dispatchAccessibleEvent(3, new Integer(cursorBeforeMove), new Integer(this._cursor), this);
      }

      return result;
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      x = MathUtilities.clamp(0, x, this.getRightMargin() - 1);
      y = MathUtilities.clamp(0, y, this.getBottomMargin() - 1);
      ArticInterface$LineInfo info = this.getLineInfoForYPos(y);
      int lineTop = info._top;
      int amount = 0;
      ArticInterface$Line line = this._cursorLine;
      int top = this._cursorLineTop;
      if (lineTop < top) {
         while (true) {
            if (lineTop >= top || line._prev == null) {
               if (top != y && line._next != null && (status & 512) != 0) {
                  amount++;
               }
               break;
            }

            line = line._prev;
            top -= line._boundsBottom - line._boundsTop;
            amount--;
         }
      } else {
         while (line != null && lineTop >= top + line._boundsBottom - line._boundsTop) {
            top += line._boundsBottom - line._boundsTop;
            line = line._next;
            amount++;
         }

         if (top != y && line != null && line._next != null && (status & 512) != 0) {
            amount++;
         }
      }

      this.moveFocus(amount, status, time);
      this._text.getText().seek(this._cursor);
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      if (!this.isCursorPositionSet()) {
         if (direction > 0) {
            this._inMoveFocus = true;
            this.setCursorPosition(0, this.isCursorPositionSet() ? 0 : Integer.MIN_VALUE);
            this._inMoveFocus = false;
            this.setPreferredXToLineStartOrEnd(this._cursorLine, true);
            return;
         }

         if (direction < 0) {
            this._inMoveFocus = true;
            synchronized (this) {
               this.setCursorPosition(this.getTextLength(), this.isCursorPositionSet() ? 0 : Integer.MIN_VALUE);
            }

            this._inMoveFocus = false;
            this.setPreferredXToLineStartOrEnd(this._cursorLine, false);
            return;
         }
      } else {
         this.setCursorPositionSet(false);
      }
   }

   @Override
   public final boolean isSelecting() {
      return this._selecting;
   }

   @Override
   public boolean isSelectable() {
      return this.getTextLength() != 0;
   }

   @Override
   public void select(boolean enable) {
      if (!this.isFocusable() && enable) {
         throw new IllegalStateException("Can't select a non focusable TextField.");
      }

      if (this._selecting != enable) {
         this.focusRemove();
         this._selecting = enable;
         if (enable) {
            this.setSelection(this._cursor, this._cursorLeadingEdge, this._cursor, false);
            this.focusAdd(true);
         } else {
            this.setSelection(this._cursor, this._cursorLeadingEdge, this._cursor, false);
            this._isAutoSelectModeOn = false;
         }

         this.invalidateFocusRect();
      }
   }

   @Override
   public boolean isSelectionCopyable() {
      return this._selecting;
   }

   @Override
   public boolean isSelectionDeleteable() {
      return this._cursor != this._anchor && this.isEditable();
   }

   @Override
   public boolean isPasteable() {
      return this._pasteable && this.isEditable();
   }

   @Override
   public int getPreferredWidth() {
      return this.isEditable() ? 536870911 : 10;
   }

   @Override
   public synchronized void getFocusRect(XYRect rect) {
      if (this._focusOffset != this._cursor) {
         this._focusOffset = this._cursor;
         this.getCaretRect(this._cursor, this._cursorLeadingEdge, this._tempRect);
         this._focus_y = this._tempRect.y;
         this._focus_height = this._tempRect.height;
         if (this._tempRect.width < 0) {
            this._focus_x = this._tempRect.x + this._tempRect.width;
            this._focus_width = -this._tempRect.width;
            if (this._focus_x < 0) {
               this._focus_width = -this._focus_x >= this._focus_width ? 1 : this._focus_width + this._focus_x;
               this._focus_x = 0;
            }
         } else {
            this._focus_x = this._tempRect.x;
            this._focus_width = this._tempRect.width;
            int rightMargin = this.getRightMargin();
            if (this._focus_x + this._focus_width > rightMargin) {
               if (this._focus_x >= rightMargin) {
                  this._focus_x = rightMargin - 1;
               }

               this._focus_width = rightMargin - this._focus_x;
            }
         }
      }

      rect.set(this._focus_x, this._focus_y, this._focus_width, this._focus_height);
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      this.getFocusRect(rect);
      if (this.getManager().getWidth() < this.getWidth()) {
         ArticInterface$Line cursorLine = this.getCursorLine()._line;
         if ((this._cursorLine._flags & 16) != 0) {
            int fakex = Math.min(this.getRightMargin(), rect.x + rect.width + 15);
            rect.width = rect.width + (fakex - rect.x - rect.width);
            int fakex2 = Math.max(0, rect.x - 15);
            rect.width = rect.width + (rect.x - fakex2);
            rect.x = fakex2;
            return;
         }

         int fakex = Math.max(0, rect.x - 15);
         rect.width = rect.width + (rect.x - fakex);
         rect.x = fakex;
         int linewidth = cursorLine._boundsRight - cursorLine._boundsLeft + (cursorLine._next == null ? this.getLastSpaceWidth() : 0);
         int fakex2 = Math.min(this.getRightMargin(), Math.min(linewidth, rect.x + rect.width + 15));
         rect.width = fakex2 - rect.x;
      }
   }

   @Override
   public Cursor getFocusCursor() {
      return Cursor.getPredefinedCursor(2);
   }

   private synchronized int getLastSpaceWidth() {
      char ch = ' ';
      int len = this._text.length();
      if (len == 0) {
         Font font = this.getFont();
         return font.getBounds(ch);
      } else {
         this._iterator.set(len - 1, len);
         Font font = Ui.getFontFromAttributes(this._iterator.runAttrib(), this.getFont());
         this.getCaretRect(len, false, this._tempRect);
         ArticInterface$Line line = this.getLineInfoForDocPos(len, false)._line;
         int var10000 = font.getBounds(ch);
         return this._tempRect.x <= line._boundsLeft ? var10000 + (line._boundsLeft - this._tempRect.x) : var10000 + (this._tempRect.x - line._boundsRight);
      }
   }

   private static long validateStyle(long style) {
      if ((style & 13510798882111488L) == 0) {
         style |= 4503599627370496L;
      }

      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style;
   }

   private void handleIMReset() {
      int text_len = this._text.length();
      if (this._cursor < 0 || this._cursor > text_len) {
         this._cursor = text_len;
      }

      if (this._composedEnd > this._composedStart && this._composedStart >= this._labelLength && this._composedEnd <= text_len) {
         this._text.setAttribToZero(this._composedStart, this._composedEnd, 786432);
         this._text.setAttribToZero(this._composedStart, this._composedEnd, 67108864);
         this._composedStart = this._composedEnd;
         this.setCaretPosition(this._composedStart);
      } else {
         if (text_len > 0) {
            this._text.setAttribToZero(0, text_len, 786432);
            this._text.setAttribToZero(0, text_len, 67108864);
         }

         if (this._cursor >= this._labelLength && this._cursor <= text_len) {
            this._composedStart = this._composedEnd = this._cursor;
         } else {
            this.setCaretPosition(text_len);
         }
      }

      this._latestCommittedStart = this._latestCommittedEnd = -1;
      this.update(this._cursor, 0, 0, 0, true, true);
   }

   @Override
   public boolean paste(Clipboard cb) {
      if (!this.isPasteable()) {
         return false;
      }

      if (this._composedStart != this._composedEnd) {
         return false;
      }

      Object pasted = cb.get();
      AttributedString attrString;
      if (!(pasted instanceof AttributedString)) {
         String iter = cb.toString();
         StringBuffer buffer = new StringBuffer(iter.length());

         for (int i = 0; i < iter.length(); i++) {
            char character = iter.charAt(i);
            if (this._filter != null) {
               character = this._filter.convert(character, 32768);
            }

            if (this.validate(character)) {
               buffer.append(character);
            }
         }

         attrString = new AttributedString(this.convert(buffer.toString(), this._cursor));
      } else {
         attrString = (AttributedString)pasted;
         String original = attrString.toString();
         String converted = this.convert(original, this._cursor);
         if (original != converted) {
            attrString = new AttributedString(converted);
         }
      }

      AttributedString$Iterator iter = attrString.getIterator();
      this.replace(this._anchor, this._cursor, iter, 0, 0, iter.length(), 0, true, 0);
      return true;
   }

   private void setPreferredXToLineStartOrEnd(ArticInterface$Line line, boolean isLineStart) {
      if ((line._flags & 16) != 0) {
         this._preferredXCoord = isLineStart ? this.getRightMargin() : 0;
      } else {
         this._preferredXCoord = isLineStart ? 0 : this.getRightMargin();
      }
   }

   private void setFontInsertionAttributes(long attribs) {
      this._insertionAttrib = attribs;
      this._text.setInsertAttrib(this._insertionAttrib);
   }

   private String convert(String original, int insertPos) {
      if (this._filter == null) {
         return original;
      }

      if (this._convertedString == null) {
         this._convertedString = new TextField$ConvertedString(this, null);
      }

      this._convertedString.init(original.length(), insertPos);
      boolean conversionNeeded = false;

      for (int i = 0; i < original.length(); i++) {
         char ch = original.charAt(i);
         char newCh = this._filter.convert(ch, this._convertedString, insertPos + i, 32768);
         if (this.validate(newCh)) {
            this._convertedString.appendFilteredChar(newCh);
            conversionNeeded = conversionNeeded || newCh != ch;
         } else {
            conversionNeeded = true;
         }
      }

      return conversionNeeded ? this._convertedString.getConvertedInsertionString() : original;
   }

   @Override
   protected void layout(int width, int height) {
      if (width >= 0 && height >= 0) {
         Font font = this.getFont();
         int adjustedWidth = width == Display.getWidth() ? width - 1 : width;
         if (this._font != font || ThemeManager.getGeneration() != this._themeGeneration || this._width != adjustedWidth) {
            this._themeGeneration = ThemeManager.getGeneration();
            this._isPreLayout = true;
            this._width = adjustedWidth;
         }

         if (this._isPreLayout) {
            this._inLayout = true;
            this.setFontInternal(font);
            this._isPreLayout = false;
            this.update(0, this._lastFormatLength, this._text.length(), this._text.length(), false, true);
            this._inLayout = false;
            this.layoutLabel();
         }

         int sumOfHeights = this.getLineTop(this._lineCount);
         adjustedWidth = this.getLayoutWidth(width);
         this.setExtent(adjustedWidth, sumOfHeights);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   protected synchronized void paint(Graphics aGraphics) {
      XYRect clip = aGraphics.getClippingRect();
      DrawTextParam drawTextParam = Ui.getTmpDrawTextParam();
      drawTextParam.iAlignment = 8;
      drawTextParam.iDrawNonPrintableCharacters = false;
      if (this.getWidth() > 0) {
         drawTextParam.iMaxAdvance = this.getRightMargin();
      }

      boolean drawFocus = aGraphics.isDrawingStyleSet(8);
      boolean drawSelect = aGraphics.isDrawingStyleSet(16);
      if (this.isStyle(137438953472L) && !drawFocus && !drawSelect) {
         int oldColor = aGraphics.getColor();
         if (this.isEditable()) {
            aGraphics.setColor(ThemeAttributeSet.getColor(this, 0));
         } else {
            aGraphics.setColor(13882323);
         }

         aGraphics.fillRect(clip.x, clip.y, clip.width, clip.height);
         aGraphics.setColor(oldColor);
      }

      ArticInterface$LineInfo info = this.getLineInfoForYPos(clip.y);
      Formatter.paint(aGraphics, drawTextParam, info, this._iterator, this, this);
      Ui.returnTmpDrawTextParam(drawTextParam);
   }

   private void resetLatestCommittedText() {
      if (this._latestCommittedEnd - this._latestCommittedStart != 0) {
         int dif = this._cursor - this._latestCommittedEnd;
         if (dif != 0) {
            int MAX_DEPTH = 5;
            if (dif > 0 && dif <= MAX_DEPTH) {
               StringBufferGap tmp = this._text.getText();
               int start = this._latestCommittedEnd;
               int end = start + dif;

               for (int i = end - 1; i >= start && " \t\n".indexOf(tmp.charAt(i)) != -1; i--) {
                  if (i == start) {
                     return;
                  }
               }
            }

            this._latestCommittedEnd = -1;
            this._latestCommittedStart = -1;
         }
      }
   }

   private int deleteSelectedText() {
      if (!this.isEditable()) {
         throw new IllegalStateException("Attempt to modify a READ_ONLY field.");
      }

      int start = this.getAnchorPosition();
      int end = this.getCaretPosition();
      if (start > end) {
         int temp = start;
         start = end;
         end = temp;
      }

      int length = end - start;
      this.select(false);
      this.setCursorPosition(end - this._labelLength, this.isCursorPositionSet() ? 0 : Integer.MIN_VALUE);
      return this.backspace(length, 0);
   }

   private void setTextInternal(String text, int context, boolean validate) {
      if (text == null) {
         text = "";
      }

      if (this.isStyle(2147483648L)) {
         text = this.removeNewlines(text);
      }

      if (validate && !this.validate(text)) {
         throw new IllegalArgumentException("invalid text");
      }

      this.select(false);
      this.resetComposedText();
      if (text.length() > this._maxNumChars) {
         text = text.substring(0, this._maxNumChars);
      }

      this.findDefaultDirectionality(text);
      AttributedString attribStr = new AttributedString(text, this.getDefaultFontAttributes(), 0);
      AttributedString$Iterator iter = attribStr.getIterator();
      synchronized (this) {
         int oldLen = this._text.length();
         int newLen = iter.length();
         this.replace(this._labelLength, oldLen, iter, -1, -1, newLen, 0, this.isEditable() || this._cursor > this._labelLength, context);
      }

      this.wipe();
   }

   @Override
   public void actionPerformed(int action, Object parameter) {
      if ((action & 0xFF) > 0) {
         switch (action & 0xFF) {
            case 112:
               if (parameter != null && parameter instanceof StringBuffer) {
                  this.fillInternalDebugInfo((StringBuffer)parameter);
               }
               break;
            case 113:
               if (this.isSymbolScreenAllowed()) {
                  SymbolScreen.show(this);
                  this._dependentScreen = SymbolScreen.getSymbolScreen();
                  return;
               }
               break;
            case 141:
               if (this.isSymbolScreenAllowed() && SymbolScreen.getSymbolScreen().isUiEngineAttached()) {
                  SymbolScreen.getSymbolScreen().close();
                  this._dependentScreen = null;
                  return;
               }
         }
      }
   }

   private void setCursorPosition(int aDocPos, boolean aLeadingEdge, int aContext) {
      boolean noUpdate = this.getManager() == null || !this.getManager().isValidLayout();
      if (!noUpdate && !this._inMoveFocus) {
         this.focusRemove();
      }

      this._cursorPositionSet = (aContext & -2147483648) == 0;
      if (noUpdate) {
         this._cursor = this._anchor = aDocPos;
         this._cursorLeadingEdge = aLeadingEdge;
      } else {
         this.setSelection(aDocPos, aLeadingEdge, this.isSelecting() ? this._anchor : aDocPos, true);
         if (!this._inMoveFocus) {
            this.focusAdd(true);
         }
      }
   }

   @Override
   public String toString() {
      return this.getText();
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      if (super.keyControl(key, status, time)) {
         return true;
      }

      if (!this.isEditable()) {
         return this.isSelecting() && key == '\n';
      }

      switch (key) {
         case '\b':
            if (0 != (status & 1)) {
               this.selectionDelete();
               return true;
            }

            this.backspace();
            return true;
         case '\u001b':
            return false;
         case '\u007f':
            this.selectionDelete();
            return true;
         case '\u0080':
            if (this.isSymbolScreenAllowed()) {
               SymbolScreen.show(this);
               this._dependentScreen = SymbolScreen.getSymbolScreen();
               return true;
            } else {
               if (!this.insert(key, status) && !this.isFieldFull()) {
                  return false;
               }

               return true;
            }
         default:
            char newkey = this.convert(key, status);
            return this.validate(newkey) ? this.insert(newkey, status) || this.isFieldFull() : false;
      }
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      int start = Math.min(this._cursor, this._anchor);
      int end = Math.max(this._cursor, this._anchor);
      int length = end - start;
      if (length > 0) {
         cb.put(this.getText(start, length));
      } else {
         cb.put(null);
      }
   }

   @Override
   public synchronized void selectionDelete() {
      if (this.isSelecting()) {
         this.deleteSelectedText();
      } else if (this._cursor != this.getDisplayTextLength()) {
         String textBefore = this._text.getText(this._cursor, this._cursor + 1);
         this._text.delete(this._cursor, this._cursor + 1);
         this.update(this._cursor, 1, 0, 0, true, true);
         if (Ui.isTTSEnabled()) {
            this.accessibleEventOccurred(2, textBefore, null, this);
         }

         this.fieldChangeNotify(0);
      }
   }

   private boolean posIsAtBidiBorder(int pos, boolean checkPrevLine) {
      ArticInterface$LineInfo info = this.getLineInfoForDocPos(pos, true);
      ArticInterface$Line line = info._line;
      int lineStart = info._start;
      boolean prevBidiIsR2L = false;
      if (lineStart == pos) {
         if (!checkPrevLine || (line._flags & 1) != 0) {
            return false;
         }

         ArticInterface$LayoutRun prevRun = line._prev._layoutRun[line._prev._layoutRun.length - 1];
         prevBidiIsR2L = (prevRun._flags & 2) != 0;
      }

      int runs = line._layoutRun == null ? 0 : line._layoutRun.length;

      for (int j = 0; j < runs; j++) {
         ArticInterface$LayoutRun run = line._layoutRun[j];
         int runStart = lineStart + run._textStart;
         if (runStart > pos) {
            return false;
         }

         if (runStart == pos) {
            boolean nextBidiIsR2L = (run._flags & 2) != 0;
            if (prevBidiIsR2L != nextBidiIsR2L) {
               return true;
            }

            return false;
         }

         if (runStart + run._textLength == pos) {
            prevBidiIsR2L = (run._flags & 2) != 0;
         }
      }

      return false;
   }

   private synchronized void update(int aStart, int aLength, int aNewLength, int aCursorOffset, boolean aMoveCursor, boolean aIsInsertionOrDeletion) {
      if (!this._notifyingTextChanged) {
         this.fireTextChangeEvent(0);
         this._formatParams.init(aStart, aLength, aNewLength, aCursorOffset, aMoveCursor, this._lineList);
         if (!this._isPreLayout) {
            this._notifyingTextChanged = true;
            this.notifyTextChanged(this._formatParams, aIsInsertionOrDeletion);
            this._notifyingTextChanged = false;
            this.invalidateFocusRect();
         }

         this.adjustCursorAfterTextChange(this._formatParams);
         if (!this._isPreLayout) {
            if (aIsInsertionOrDeletion && !this._inLayout) {
               this.update(this._formatParams.getDelta());
            }

            if (!this._formatParams._isFormatComplete && this._formatThreadId != -1) {
               Application.getApplication().cancelInvokeLater(this._formatThreadId);
            }

            this._formatParams.computeParamsAfterTextChanged(!this._inLayout, this.getMaxLinesToFormat());
            this._formatParams.initCursorLine(this._cursorLine, this._cursorLineStart, this._cursorLineTop);
            this._lineList = Formatter.incrementalFormat(
               this._formatParams, this, this._width, this._text, this._cursor, this._cursorLeadingEdge, this._anchor, !this._inLayout
            );
            this._lastFormatLength = this._lastFormatLength + (this._formatParams._newLength - this._formatParams._oldLength);
            this.handleLinesAfterFormat(this._formatParams);
            if (this._cursor >= this._formatParams._changedTextStart) {
               this.updateCursorAfterFormat(this._formatParams);
            }

            this.invalidate(
               this._formatParams._invalidRect.x,
               this._formatParams._invalidRect.y,
               this._formatParams._invalidRect.width,
               this._formatParams._invalidRect.height
            );
            if (!this._inLayout) {
               if (this._formatParams._invalidRect.height == Integer.MAX_VALUE) {
                  this.updateLayout();
               }

               this.focusAdd(false);
            }

            this.spawnDelayedFormatting();
         }
      }
   }

   private void spawnDelayedFormatting() {
      if (!this._formatParams._isFormatComplete) {
         this._formatThreadId = Application.getApplication().invokeLater(new TextField$3(this), 100, false);
      }
   }

   private String removeNewlines(String str) {
      if (str == null) {
         return null;
      }

      int l = str.length();
      StringBuffer sb = new StringBuffer(l);

      for (int i = 0; i < l; i++) {
         char c;
         if ((c = str.charAt(i)) != '\n') {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   private void removeNewlines(AttributedString str) {
      int l = str.length();

      for (int i = l - 1; i >= 0; i--) {
         if (str.charAt(i) == '\n') {
            str.delete(i, i + 1);
         }
      }
   }

   private boolean isComposedTextExist() {
      return this._composedStart != this._composedEnd;
   }

   private void resetComposedText() {
      if (this.isComposedTextExist()) {
         InputContext imcontext = this.getInputContext();
         if (imcontext.getInputComponent() == this) {
            imcontext.endComposition();
         }
      }
   }

   private void initCursor() {
      this._cursor = this.isEditable() ? this._text.length() : this._labelLength;
      this._anchor = this._cursor;
      this._cursorLine = this._lineList;
      this._cursorLineStart = 0;
      this._cursorLineTop = 0;
      this._cursorLineTop = this._cursorLine._boundsTop;
      this._cursorLeadingEdge = true;
      this._cursorIsAtBidiBorder = false;
   }

   private int getCaretX(int docPos, boolean leadingEdge) {
      this.getCaretRect(docPos, leadingEdge, this._tempRect);
      return this._tempRect.x;
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this.setLabel(this._labelSet);
   }

   @Override
   protected void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
   }

   private String handleLabelOwnLine(String labelSet) {
      this._labelSet = labelSet;
      if (labelSet == null) {
         labelSet = "";
      }

      this._isLabelOwnLine = ThemeManager.getActiveTheme().isLabelOnOwnLine();
      String label;
      if (this._isLabelOwnLine && labelSet.length() > 0 && labelSet.charAt(labelSet.length() - 1) != '\n') {
         label = labelSet + '\n';
      } else {
         label = labelSet.length() > 0 && labelSet.charAt(labelSet.length() - 1) != '\n' ? labelSet + '\u200b' : labelSet;
      }

      if (!this._isUnicodeInputAllowed && label.length() > 0 && label.charAt(label.length() - 1) != 8234 && this.isDirectionalityR2L(label)) {
         label = label + '\u202a';
      }

      this._labelLength = label.length();
      return label;
   }

   private final int getLineLength(ArticInterface$Line aLine) {
      return aLine._textLength + aLine._skippedCharacters;
   }

   private void layoutLabel() {
      int offset = 0;
      this._lastLabelLineTop = 0;

      ArticInterface$Line currentLine;
      for (currentLine = this._lineList; currentLine != null; currentLine = currentLine._next) {
         offset += this.getLineLength(currentLine);
         if (offset >= this._labelLength) {
            break;
         }

         this._lastLabelLineTop = this._lastLabelLineTop + (currentLine._boundsBottom - currentLine._boundsTop);
      }

      if (this._labelLength > 0 && currentLine != null && this._text.charAt(this._labelLength - 1) == '\n') {
         this._lastLabelLineTop = this._lastLabelLineTop + (currentLine._boundsBottom - currentLine._boundsTop);
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this.isEditable()) {
         if (!contextMenu.isEmpty()) {
            contextMenu.addSeparatorInternal();
         }

         if (this.isSymbolScreenAllowed()) {
            contextMenu.addItem(_showSymbolsItem);
         }

         if (!this.isSelecting() && this.isClearMenuItemAllowed()) {
            if (!contextMenu.isEmpty()) {
               contextMenu.addSeparatorInternal();
            }

            contextMenu.addItem(_clearFieldItem);
         }

         if (this.getInputContext().getActiveInputMethodID() == 4096) {
            Object toggleI = _toggleInputItem.get();
            if (toggleI == null) {
               toggleI = new TextField$TogglingMenuItem();
               _toggleInputItem.set(toggleI);
            }

            SLControlObject co = (SLControlObject)this.getInputContext().getInputMethodControlObject();
            int currentMode = co.getInputMode();
            if (currentMode != -1) {
               TextFilter filter = this.getFilter();
               int textFilterStyle = 0;
               if (filter != null) {
                  textFilterStyle = (int)filter.getPreferredInputStyle();
               }

               int id = 10091;
               int inputMode = 0;
               switch (currentMode) {
                  case -1:
                     break;
                  case 0:
                  default:
                     id = 10090;
                     inputMode = 2;
                     break;
                  case 1:
                     if (((this.getFieldStyle() | textFilterStyle) & 1073741824) != 0) {
                        id = 10090;
                        inputMode = 2;
                     }
                     break;
                  case 2:
                     if (((this.getFieldStyle() | textFilterStyle) & 1073741824) != 0) {
                        id = 10092;
                        inputMode = 1;
                     }
               }

               ((MenuItem)toggleI).setText(CommonResource.getBundle().getString(id));
               ((TextField$TogglingMenuItem)toggleI).setInputMode(inputMode);
               contextMenu.addItem((MenuItem)toggleI);
            }
         }
      }
   }

   private void adjustCursorAfterTextChange(FormatParams params) {
      boolean cursorChanged = false;
      if (!params._moveCursor) {
         if (this._cursor > this._text.length()) {
            this._cursor = this._anchor = this._text.length();
            cursorChanged = true;
         }
      } else {
         if (params._changedTextStart < 0 || params._oldLength < 0 || params._newLength < 0) {
            throw new IllegalArgumentException();
         }

         int end = params._changedTextStart + params._oldLength;
         int length_change = params._newLength - params._oldLength;
         params._isBackspace = params._newLength == 0;
         if (this._cursor >= params._changedTextStart) {
            if (this._cursor > end) {
               this._cursor += length_change;
            } else {
               int oldCursor = this._cursor;
               this._cursor = params._changedTextStart + params._cursorOffset;
               params._isBackspace = params._isBackspace && this._cursor != oldCursor;
            }
         }

         if (this._anchor >= params._changedTextStart) {
            if (this._anchor > end) {
               this._anchor += length_change;
            } else {
               this._anchor = params._changedTextStart + params._cursorOffset;
            }
         }

         cursorChanged = true;
      }

      if (cursorChanged && !this._isPreLayout) {
         this.handleCursorPositionChanged();
      }
   }

   private synchronized void setSelection(int aNewCursor, boolean aNewCursorLeadingEdge, int aNewAnchor, boolean setPrefferredX) {
      if (this._lineCount == 0) {
         throw new IllegalStateException();
      }

      if (this._width == -1) {
         this._cursor = aNewCursor;
         this._cursorLeadingEdge = aNewCursorLeadingEdge;
         this._anchor = aNewAnchor;
      } else {
         if (!this._formatParams._isFormatComplete) {
            int maxCharsPerPage = Math.max(20, Display.getHeight() / this.getFont().getHeight() + 1) * (this.getWidth() / this.getFont().getAdvance(' '));
            int formatToPos = Math.max(aNewCursor, aNewAnchor) + maxCharsPerPage;
            int nextPosToFormat = this._formatParams.getNextStartPosToFormat();
            if (formatToPos > nextPosToFormat && this._formatThreadId != -1) {
               Application.getApplication().cancelInvokeLater(this._formatThreadId);
            }

            while (!this._formatParams._isFormatComplete && formatToPos > nextPosToFormat) {
               Formatter.incrementalFormat(
                  this._formatParams, this, this._width, this._text, this._cursor, this._cursorLeadingEdge, this._anchor, !this._inLayout
               );
               nextPosToFormat = this._formatParams.getNextStartPosToFormat();
               this.handleLinesAfterFormat(this._formatParams);
               if (!this._inLayout && this._formatParams._invalidRect.height == Integer.MAX_VALUE) {
                  this.updateLayout();
               }
            }

            this.spawnDelayedFormatting();
         }

         this._formatParams.initCursorLine(this._cursorLine, this._cursorLineStart, this._cursorLineTop);
         XYRect invalid = this._formatParams._invalidRect;
         invalid.set(this._focus_x, this._focus_y, this._focus_width, this._focus_height);
         this._lineList = Formatter.setSelection(
            this._lineList, this._anchor, this._cursor, aNewAnchor, aNewCursor, aNewCursorLeadingEdge, this._width, this._text, this._formatParams
         );
         this._cursorLineStart = this._formatParams._cursorLineInfo._start;
         this._cursorLineTop = this._formatParams._cursorLineInfo._top;
         this._cursorLine = this._formatParams._cursorLineInfo._line;
         this._cursor = aNewCursor;
         this._cursorLeadingEdge = aNewCursorLeadingEdge;
         this._anchor = aNewAnchor;
         this._cursorIsAtBidiBorder = this.posIsAtBidiBorder(this._cursor, true);
         if (setPrefferredX) {
            this._preferredXCoord = this.getCaretX(this._cursor, this._cursorLeadingEdge);
         }

         this.invalidate(invalid.x, invalid.y, invalid.width, invalid.height);
         this.handleCursorPositionChanged();
      }
   }

   @Override
   public void setFont(Font font) {
      if (this.getFontIfSet() != font) {
         super.setFont(font);
      }

      if (this._font != font) {
         this.setFontInternal(font);
      }
   }

   private void setFontInternal(Font font) {
      long SET_FONT_ATTRIB_MASK = 134283263;
      long defaultFontAttribs = this.getDefaultFontAttributes();
      if ((defaultFontAttribs & 786432) != 0) {
         SET_FONT_ATTRIB_MASK |= 786432;
      }

      this.invalidateFocusRect();
      this.startLayoutUpdate();
      AttributedString$Iterator iterator = this._text.getIterator();
      int start = 0;

      do {
         int end = start + iterator.runLength();
         if ((iterator.runAttrib() & 536870912) != 0) {
            this.setAttrib(start, end, defaultFontAttribs, SET_FONT_ATTRIB_MASK, 0, 0);
         }

         start = end;
      } while (iterator.next());

      this.endLayoutUpdate();
      this._font = font;
      this.setInsertionAttributesToSelection();
      long hanStyle = 0;
      switch (this.getFont().getStyle() & 7168) {
         case 1024:
            hanStyle = 1;
            break;
         case 2048:
            hanStyle = 2;
            break;
         case 3072:
            hanStyle = 3;
            break;
         case 4096:
            hanStyle = 4;
      }

      this._text.setGlobalAttrib(hanStyle, 7);
   }

   private synchronized void incrementalFormat() {
      Formatter.incrementalFormat(this._formatParams, this, this._width, this._text, this._cursor, this._cursorLeadingEdge, this._anchor, !this._inLayout);
      this.handleLinesAfterFormat(this._formatParams);
      if (!this._inLayout && this._formatParams._invalidRect.height == Integer.MAX_VALUE) {
         this.updateLayout();
      }

      this.spawnDelayedFormatting();
   }

   public TextField(String label, String initialValue, int maxNumChars, long style) {
      super(validateStyle(style));
      this.setTag(TAG_EDIT);
      this._filter = this.getFilterFromStyle(style);
      if (this._filter != null && this._filter.getMaxLength() < maxNumChars) {
         maxNumChars = this._filter.getMaxLength();
      }

      if (maxNumChars < 1) {
         throw new IllegalArgumentException("invalid initialValue");
      }

      this._maxNumChars = Math.min(1000000, maxNumChars);
      if (initialValue != null && initialValue.length() > maxNumChars) {
         throw new IllegalArgumentException("invalid initialValue");
      }

      this._text = new AttributedString();
      this.setLabel(label);
      this.setTextInternal(initialValue, Integer.MIN_VALUE, !(this instanceof BasicEditField));
      this._iterator = this._text.getIterator();
      this._lineList = new ArticInterface$Line();
      this._lineList._flags = 3;
      this._lineCount = 1;
      this.initCursor();
      this.setDefaultInsertionAttributes();
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (this.isStyle(2147483648L) && key == '\n') {
         return false;
      }

      if (key == 27 && this._isAutoSelectModeOn) {
         this._selecting = this._isAutoSelectModeOn = false;
      }

      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (!this.isEditable()) {
         if (this.isSelecting() && key == '\n') {
            return true;
         } else {
            return key == 27 ? false : this.isStyle(1024);
         }
      } else {
         switch (key) {
            case '\b':
               if (0 != (status & 1)) {
                  this.selectionDelete();
                  return true;
               } else {
                  if (this._cursor == this._labelLength) {
                     this.selectionDelete();
                     return true;
                  }

                  this.backspace();
                  return true;
               }
            case '\u001b':
               return false;
            case '\u007f':
               if (this._cursor == this.getDisplayTextLength()) {
                  this.backspace();
                  return true;
               }

               this.selectionDelete();
               return true;
            default:
               return this.insert(key, status) || this.isFieldFull();
         }
      }
   }

   @Override
   public int caretPositionChanged(InputMethodEvent event) {
      switch (event.getID()) {
         case 1100:
         case 1103:
            break;
         case 1101:
         default:
            if (this.isComposedTextExist()) {
               this.setCaretPosition(this._composedStart + event.getCaret()._index);
               this.update(this._cursor, 0, 0, 0, true, false);
               this.fieldChangeNotify(0);
               return 0;
            }
            break;
         case 1102:
            if (!this.isComposedTextExist()) {
               this.setCaretPosition(event.getCaret()._index);
               this.update(this._cursor, 0, 0, 0, true, false);
               this.fieldChangeNotify(0);
               return 0;
            }
            break;
         case 1104:
            this._caretShape = (byte)event.getModifiers();
            this.invalidate(this._focus_x, this._focus_y, this._focus_width, this._focus_height);
      }

      return 0;
   }

   @Override
   public void setIMCookieCache(Object cookie) {
      this._imCookie = cookie;
   }

   private void updateCursorAfterFormat(FormatParams param) {
      this._cursorLine = param._cursorLineInfo._line;
      this._cursorLineStart = param._cursorLineInfo._start;
      this._cursorLineTop = param._cursorLineInfo._top;
      boolean cursorIsAtBidiBorder = this.posIsAtBidiBorder(this._cursor, true);
      if (this._cursorLineStart == this._cursor) {
         this._cursorLeadingEdge = true;
      } else if (cursorIsAtBidiBorder && !this._cursorIsAtBidiBorder && this._formatParams._moveCursor) {
         this._cursorLeadingEdge = this._formatParams._isBackspace;
      } else if (!cursorIsAtBidiBorder) {
         this._cursorLeadingEdge = true;
      }

      this.getLineInfoForDocPos(this._cursor, this._cursorLeadingEdge);
      this._cursorLine = this._tempLineInfo._line;
      this._cursorLineStart = this._tempLineInfo._start;
      this._cursorLineTop = this._tempLineInfo._top;
      this._cursorIsAtBidiBorder = cursorIsAtBidiBorder;
   }

   private void setInsertionAttributesToSelection() {
      if (this.getTextLength() == 0) {
         this.setDefaultInsertionAttributes();
      } else {
         this._insertionAttrib = this.getInsertionAttributesOfSelection();
         this._text.setInsertAttrib(this._insertionAttrib);
      }
   }

   private long getInsertionAttributesOfSelection() {
      int pos = this._cursor;
      if (this._cursor > this._labelLength
         && this._cursor >= this._anchor
         && (this._composedStart == this._composedEnd || this._cursor > this._composedStart)
         && (this._cursor > this._cursorLineStart || (this._cursorLine._flags & 1) == 0)) {
         pos--;
      }

      this._iterator.set(pos, pos);
      long insertionAttrib = this._iterator.runAttrib();
      if (this._composedStart != this._composedEnd) {
         insertionAttrib = insertionAttrib & (this._composedTextAttributeMask ^ -1) | this._insertionAttrib & this._composedTextAttributeMask;
      }

      long insertionXAttrib = this._iterator.runXAttrib();
      if ((insertionXAttrib & 65504) != 0) {
         insertionAttrib &= -786433;
      }

      return insertionAttrib;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized int inputMethodTextChanged(InputMethodEvent event) {
      if (!this.isEditable() && !this.isComposedTextExist()) {
         return 1;
      }

      if (event.getID() == 1103) {
         this.handleIMReset();
         return 0;
      }

      this._caretShape = event.getCaretShape();
      AttributedString eventText = event.getText();
      this._composedTextAttributeMask = event.getTextMask();
      int inserted_len = eventText.length();
      int committed_count = event.getCommittedCharacterCount();
      this._composed_highlighted = false;
      if (inserted_len > committed_count) {
         AttributedString$Iterator iter = eventText.getIterator();

         do {
            if ((iter.runAttrib() & 67108864) != 0) {
               this._composed_highlighted = true;
               break;
            }
         } while (iter.next());
      }

      if (committed_count > 0 && event.isOverrideCommittedTextAttributes()) {
         eventText.setAttrib(0, committed_count, this._insertionAttrib, this._composedTextAttributeMask);
      }

      this._convertedCharactersCount = Math.min(event.getConvertedCharacterCount(), inserted_len - committed_count);
      if (inserted_len == 0 && committed_count == 0 && !this.isComposedTextExist()) {
         return 0;
      }

      if (this._composedStart == this._composedEnd) {
         this._composedStart = this._composedEnd = Math.min(this._cursor, this._anchor);
      }

      if (committed_count == 1 && inserted_len == 1 && this._composedEnd - this._composedStart < 2) {
         char toInsert = eventText.charAt(0);
         if (this._composedEnd - this._composedStart <= 0) {
            if (toInsert != ' ') {
               this._latestCommittedStart = this._cursor;
               this._latestCommittedEnd = this._cursor + 1;
            }

            this.keyChar(toInsert, SLKeyLayout.convertModifiersToStatus(event.getModifiers()), 0);
            return 0;
         }

         eventText.replace(0, 1, this.convert(toInsert, SLKeyLayout.convertModifiersToStatus(event.getModifiers())));
      }

      int end;
      int start;
      if (this._composedEnd > this._composedStart) {
         start = this._composedStart;
         end = this._composedEnd;
      } else {
         start = this._anchor;
         end = this._cursor;
      }

      int posInComposedText = committed_count + event.getCaret()._index <= inserted_len ? event.getCaret()._index : 0;
      boolean var11 = false /* VF: Semaphore variable */;

      int var8;
      try {
         var11 = true;
         var8 = this.replace(start, end, eventText.getIterator(), this._composedTextAttributeMask, 0, committed_count, posInComposedText, true, 0);
         var11 = false;
      } finally {
         if (var11) {
            this.fireInputMethodTextChanged(event);
         }
      }

      this.fireInputMethodTextChanged(event);
      return var8;
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
      this._themeGeneration = -1;
   }

   @Override
   public InputMethodRequests getInputMethodRequests() {
      return this;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return this;
   }

   @Override
   public int getAccessibleRole() {
      return 2;
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      super.dispatchEvent(rEvent);
      if (rEvent.getID() == 1004) {
         if (this.isInputMethodEnabled()) {
            this.updateInputStyle();
            return;
         }
      } else if (!rEvent.isConsumed() && rEvent.isComponentDispatchEnabled() && rEvent instanceof KeyEvent) {
         KeyEvent event = (KeyEvent)rEvent;
         int status = SLKeyLayout.convertModifiersToStatus(event.getModifiers());
         switch (event.getID()) {
            case 512:
               break;
            case 513:
            case 514:
            default:
               if (this.keyDown(event.getKeyCode() << 16 | status, (int)event.getWhen()) || this.keyChar(event.getKeyChar(), status, (int)event.getWhen())) {
                  event.consume();
                  return;
               }
               break;
            case 515:
               if (this.keyUp(event.getKeyCode() << 16 | status, (int)event.getWhen())) {
                  event.consume();
               }
         }
      }
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      this.getDocPos(x, y, this._tempDocPosInfo);
      int pos = Math.max(this._tempDocPosInfo._index, this.getLabelLength());
      if (this._selecting) {
         this.setSelection(pos, this._tempDocPosInfo._leadingEdge, this._anchor, false);
      } else {
         this.setSelection(pos, this._tempDocPosInfo._leadingEdge, pos, false);
      }

      this.getFocusRect(this._tempRect);
      this.invalidate(this._focus_x, this._focus_y, this._focus_width, this._focus_height);
      return true;
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      EventHandler handler = EventHandler.getInstance();
      switch (event) {
         case 516:
         case 6914:
            boolean input = this.isInputMethodEnabled() && this.isEditable();
            return (handler.processKeyEvent(516, 0, '\u0000', status, time, input) & 65536) == 65536;
         default:
            return handler.processNavigationEvent(event, dx, dy, status, time);
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      EventHandler handler = EventHandler.getInstance();
      boolean input = this.isInputMethodEnabled() && this.isEditable();
      return handler.processKeyEvent(event, keycode, key, keycode, time, input);
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      boolean outline = false;
      int width = this._focus_width;
      if (this._composedStart != this._composedEnd) {
         switch (this._caretShape) {
            case -1:
            case 1:
               break;
            case 0:
            default:
               return;
            case 2:
               outline = true;
               break;
            case 3:
               width = 2;
               break;
            case 4:
               if (this._composed_highlighted) {
                  return;
               }
         }
      }

      int x = this._focus_x;
      int y = this._focus_y;
      int height = this._focus_height;
      if (this._selecting) {
         y += height + 1 >> 1;
         height >>= 1;
      }

      if (outline) {
         int color = graphics.getColor();
         graphics.setColor(ThemeAttributeSet.getColor(this, 2));
         graphics.drawRect(x, y, width, height);
         graphics.setColor(ThemeAttributeSet.getColor(this, 1));
         this.paint(graphics);
         graphics.setColor(color);
      } else {
         this.drawHighlightRegion(graphics, 1, on, x, y, width, height);
      }
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         this._navigationClickHandled = true;
         return true;
      } else {
         return false;
      }
   }

   public TextField(String label, String initialValue) {
      this(label, initialValue, 1000000, 0);
   }

   private boolean isDirectionalityR2L(String text) {
      for (int i = 0; i < text.length(); i++) {
         int type = CharacterUtilities.getBidiType(text.charAt(i)) & 127;
         if (type != 0) {
            switch (type) {
               case 0:
               case 3:
                  return true;
               case 1:
               case 2:
               case 4:
               default:
                  return false;
            }
         }
      }

      return false;
   }

   private void fillInternalDebugInfo(StringBuffer result) {
      Object screen = this.getScreen();
      if (screen != null) {
         result.append("Scr=");
         result.append(screen);
         result.append('\n');
      }

      result.append("hFcs=");
      result.append(this.isFocus());
      result.append('\n');
      result.append("lLn=");
      result.append(this._labelLength);
      result.append('\n');
      result.append("tLn=");
      result.append(this._text.length());
      result.append('\n');
      result.append("cStrt=");
      result.append(this._composedStart);
      result.append('\n');
      result.append("cEnd=");
      result.append(this._composedEnd);
      result.append('\n');
      result.append("lcStrt=");
      result.append(this._latestCommittedStart);
      result.append('\n');
      result.append("lcEnd=");
      result.append(this._latestCommittedEnd);
      result.append('\n');
      result.append("cOffst=");
      result.append(this._cursor);
      result.append('\n');
      result.append("isSlct=");
      result.append(this.isSelecting());
      result.append('\n');
   }

   public TextField(long style) {
      this(null, null, 1000000, style);
   }

   public TextField() {
      this(null, null, 1000000, 0);
   }

   private int getRightMargin() {
      Manager m = this.getManager();
      int hscroll = m == null ? 0 : m.getHorizontalScroll();
      return hscroll + this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
   }

   private int getBottomMargin() {
      Manager m = this.getManager();
      int hscroll = m == null ? 0 : m.getVerticalScroll();
      return hscroll + this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
   }
}
