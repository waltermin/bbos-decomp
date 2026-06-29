package net.rim.device.apps.internal.browser.wml;

import java.util.Stack;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.ByteVector;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.internal.browser.ui.BrowserTextField;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.apps.internal.browser.util.ShortVector;

final class TextSegment extends TaskContainer {
   private Stack _managerStack;
   private Vector _textVariables;
   private StringBuffer _text = (StringBuffer)(new Object());
   private IntVector _offsets = (IntVector)(new Object());
   private ByteVector _attributes = (ByteVector)(new Object());
   private ShortVector _varStarts = new ShortVector();
   private FontCache _fontCache;
   private Font[] _currentFonts;
   private int[] _currentColors;
   private int _excessHeight;
   private int[] _fontHeights;
   private int _fontHeight;
   private int _boldStyle;
   private int _italicStyle;
   private int _underlinedStyle;
   private int _boldStyleCnt;
   private int _italicStyleCnt;
   private int _underlinedStyleCnt;
   private int _paragraphStyle;
   private boolean _wrap = true;
   private boolean _firstAnchorVerb;
   private int _anchorCnt;
   private String _defaultFontFace;
   private boolean _autoMatch = true;
   private boolean _minimalMenuMode;
   private Vector _oldAnchorVerbs = (Vector)(new Object());
   private Vector _vars = (Vector)(new Object());
   private Vector _anchorVerbs = (Vector)(new Object());
   private WMLBrowserContent _browserContent;
   private Font _defaultFont;
   private int _minimumFontSize;
   private int _minimumFontStyle;
   private boolean _removeLeadingSpaces;
   private static final int DEFAULT_PT_SIZE_DELTA = 2;
   private static Stack _defaultManagerStack = (Stack)(new Object());

   TextSegment(Font defaultFont, int minimumFontSize, int minimumFontStyle, boolean minimalMenuMode) {
      this(defaultFont, _defaultManagerStack, null, null, true, minimumFontSize, minimumFontStyle, minimalMenuMode);
   }

   TextSegment(
      Font defaultFont,
      Stack managerStack,
      Vector textVariables,
      String renderingEncoding,
      boolean autoMatch,
      int minimumFontSize,
      int minimumFontStyle,
      boolean minimalMenuMode
   ) {
      super(341248);
      this._managerStack = managerStack;
      this._textVariables = textVariables;
      this._fontCache = FontCache.getInstance();
      this._autoMatch = autoMatch;
      this._minimalMenuMode = minimalMenuMode;
      this._currentFonts = new Object[0];
      if (Graphics.isColor()) {
         this._currentColors = new int[0];
      }

      this._defaultFont = defaultFont;
      int[] tmpFontHeights = defaultFont.getFontFamily().getHeights();
      int length = tmpFontHeights.length;

      for (int i = 0; i < length; i++) {
         tmpFontHeights[i] = Ui.convertSize(tmpFontHeights[i], 0, 2);
      }

      int numFontHeights = 1;
      int i = 1;
      int j = 0;

      while (i < tmpFontHeights.length) {
         if (tmpFontHeights[i] != tmpFontHeights[j]) {
            numFontHeights++;
            j = i;
         }

         i++;
      }

      this._fontHeights = new int[numFontHeights];
      this._fontHeights[0] = tmpFontHeights[0];
      i = 1;
      j = 0;

      while (i < tmpFontHeights.length) {
         if (tmpFontHeights[i] != this._fontHeights[j]) {
            this._fontHeights[++j] = tmpFontHeights[i];
         }

         i++;
      }

      this._fontHeight = defaultFont.getHeight(2);
      this._minimumFontSize = minimumFontSize;
      this._minimumFontStyle = minimumFontStyle;
      i = defaultFont.getStyle();
      this._boldStyle = (i | this._minimumFontStyle) & 65;
      this._italicStyle = i & 2;
      this._underlinedStyle = i & 4;
      this._offsets.addElement(0);
      this._defaultFontFace = this._fontCache.getFontFamily(this._defaultFont, renderingEncoding, null).getName();
   }

   @Override
   final void setBrowserContent(WMLBrowserContent browserContent) {
      super.setBrowserContent(browserContent);
      this._browserContent = browserContent;
   }

   final void startP() {
      this._removeLeadingSpaces = true;
      this.flush(false);
      this.setParagraphStyle(0);
      this.setWrappingStyle(true);
   }

   final void endP() {
      this.renderText("\n");
      this.flush(true);
   }

   final void appendNewLine() {
      this.renderText("\n");
      this._removeLeadingSpaces = true;
   }

   final void addVariable(WMLVariable data) {
      this._vars.addElement(data);
      this._varStarts.addElement((short)this._text.length());
      this.renderText("$");
   }

   final int getVarStartsLength() {
      return this._vars.size();
   }

   final short[] getVarStarts() {
      return this._varStarts.toArray();
   }

   final WMLVariable[] getVars() {
      WMLVariable[] vars = new WMLVariable[this._vars.size()];
      this._vars.copyInto(vars);
      return vars;
   }

   final void anchorOn() {
      if (this._anchorCnt != 0) {
         this.anchorOff();
      }

      int textLength = this._text.length();
      if (textLength > 0) {
         char lastChar = this._text.charAt(textLength - 1);
         if (lastChar != ' ' && lastChar != '\t' && lastChar != '\n' && lastChar != 160) {
            this.renderText(" ");
         }
      }

      this._anchorCnt++;
      this._firstAnchorVerb = true;
      WMLAnchorVerb anchorVerb = new WMLAnchorVerb((short)this._text.length());
      this._anchorVerbs.addElement(anchorVerb);
      this.underlinedOn();
      this._underlinedStyle |= 8;
   }

   final void anchorOff() {
      WMLAnchorVerb anchorVerb = (WMLAnchorVerb)this._anchorVerbs.lastElement();
      anchorVerb.setEndOffset((short)this._text.length());
      anchorVerb.setTask(this.getTask());
      if (anchorVerb.getStartOffset() == anchorVerb.getEndOffset()) {
         this._anchorVerbs.removeElementAt(this._anchorVerbs.size() - 1);
      }

      for (int i = 0; i < this._oldAnchorVerbs.size(); i++) {
         ((WMLAnchorVerb)this._oldAnchorVerbs.elementAt(i)).setTask(this.getTask());
      }

      this._oldAnchorVerbs.removeAllElements();
      this._anchorCnt--;
      this._firstAnchorVerb = false;
      this._underlinedStyle &= -9;
      this.underlinedOff();
   }

   final void setAnchorTitle(WMLVariable title, boolean showTitleOnly) {
      ((WMLAnchorVerb)this._anchorVerbs.lastElement()).setTitle(title, showTitleOnly);
      if (this._oldAnchorVerbs != null) {
         for (int i = 0; i < this._oldAnchorVerbs.size(); i++) {
            ((WMLAnchorVerb)this._oldAnchorVerbs.elementAt(i)).setTitle(title, showTitleOnly);
         }
      }
   }

   final void setAnchorAccess(char ch) {
      ((WMLAnchorVerb)this._anchorVerbs.lastElement()).setAccessKey(ch);
      if (this._oldAnchorVerbs != null) {
         for (int i = 0; i < this._oldAnchorVerbs.size(); i++) {
            ((WMLAnchorVerb)this._oldAnchorVerbs.elementAt(i)).setAccessKey(ch);
         }
      }
   }

   final WMLAnchorVerb getCurrentAnchorVerb() {
      if (this._anchorCnt > 0) {
         return this._anchorVerbs.lastElement() instanceof WMLAnchorVerb ? (WMLAnchorVerb)this._anchorVerbs.lastElement() : null;
      } else {
         return null;
      }
   }

   final void setParagraphStyle(int paragraphStyle) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int getParagraphStyle() {
      return this._paragraphStyle;
   }

   final void setRemoveLeadingSpaces() {
      this._removeLeadingSpaces = true;
   }

   final void italicOn() {
      this._italicStyle = 2;
      this._italicStyleCnt++;
   }

   final void italicOff() {
      this._italicStyleCnt--;
      if (this._italicStyleCnt == 0) {
         this._italicStyle = 0;
      }
   }

   final void underlinedOn() {
      this._underlinedStyle = 4;
      this._underlinedStyleCnt++;
   }

   final void underlinedOff() {
      this._underlinedStyleCnt--;
      if (this._underlinedStyleCnt == 0) {
         this._underlinedStyle = 0;
      }
   }

   final void boldOn() {
      this._boldStyle = this._minimumFontStyle | 1;
      this._boldStyleCnt++;
   }

   final void boldOff() {
      this._boldStyleCnt--;
      if (this._boldStyleCnt == 0) {
         this._boldStyle = this._minimumFontStyle;
      }
   }

   final void increaseHeight() {
      if (this._excessHeight < 0) {
         this._excessHeight++;
      } else {
         boolean set = false;
         int desiredFontHeight = this._fontHeight + 2;

         for (int i = 0; i < this._fontHeights.length; i++) {
            if (desiredFontHeight <= this._fontHeights[i]) {
               this._fontHeight = this._fontHeights[i];
               set = true;
               break;
            }
         }

         if (!set) {
            this._excessHeight++;
         }
      }
   }

   final void decreaseHeight() {
      if (this._excessHeight > 0) {
         this._excessHeight--;
      } else {
         int desiredFontHeight = this._fontHeight - 2;
         boolean set = false;

         for (int i = 0; i < this._fontHeights.length; i++) {
            if (desiredFontHeight <= this._fontHeights[i] && this._fontHeights[i] >= this._minimumFontSize) {
               this._fontHeight = this._fontHeights[i];
               set = true;
               break;
            }
         }

         if (!set) {
            this._excessHeight--;
         }
      }
   }

   final void renderText(String text) {
      if (text != null) {
         if (this._removeLeadingSpaces) {
            int start = 0;
            int len = text.length();

            while (start < len && text.charAt(start) == ' ') {
               start++;
            }

            if (start > 0) {
               text = text.substring(start);
            }

            if (text.length() > 0) {
               this._removeLeadingSpaces = false;
            }
         }

         this._text.append(text);
         int textEnd = this._text.length();
         if (this._anchorCnt != 0) {
            ((WMLAnchorVerb)this._anchorVerbs.elementAt(this._anchorVerbs.size() - 1)).setEndOffset(textEnd);
            if (!this._firstAnchorVerb) {
               this._offsets.setElementAt(textEnd, this._offsets.size() - 1);
               return;
            }

            this._firstAnchorVerb = false;
         }

         this._offsets.addElement(textEnd);
         int color = (this._underlinedStyle & 8) != 0 ? 4856319 : -1;
         this._attributes
            .addElement(
               (byte)this._fontCache
                  .getFontIndex(
                     this._defaultFont,
                     this._boldStyle | this._italicStyle | this._underlinedStyle,
                     this._fontHeight,
                     2,
                     color,
                     this._currentFonts,
                     this._currentColors,
                     null,
                     this._defaultFontFace
                  )
            );
      }
   }

   final void flush(boolean isEndOfParagraph) {
      long style = this.getStyleForParagraph();
      int length = this._text.length();
      Field textField = this.createTextField(isEndOfParagraph);
      if (textField != null) {
         if (this._wrap || length > 255) {
            ((Manager)this._managerStack.peek()).add(textField);
            return;
         }

         HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1154117773257867264L | style));
         hfm.add(textField);
         ((Manager)this._managerStack.peek()).add(hfm);
      }
   }

   final Field createTextField(boolean isEndOfParagraph) {
      if (this._text.length() == 0) {
         return null;
      }

      int textLength = this._text.length();
      int start = 0;

      while (start < textLength && this._text.charAt(start) == ' ') {
         start++;
      }

      int end = textLength - 1;

      while (end > start && this._text.charAt(end) == ' ') {
         end--;
      }

      int trimmedTextLength = end - start + 1;
      Field textField = null;
      long style = this.getStyleForParagraph();
      if (this._vars.size() == 0 && this._anchorVerbs.size() == 0) {
         if (!isEndOfParagraph || trimmedTextLength != 1 || this._text.toString().charAt(start) != '\n') {
            textField = new BrowserTextField(
               this._browserContent,
               this._defaultFont,
               this._text.toString(),
               this._offsets.toArray(),
               this._attributes.toArray(),
               this._currentFonts,
               this._currentColors,
               null,
               null,
               this._autoMatch,
               style,
               this._minimalMenuMode
            );
         }
      } else {
         WMLVariable[] varr = new WMLVariable[this._vars.size()];
         this._vars.copyInto(varr);
         WMLAnchorVerb[] anc = new WMLAnchorVerb[this._anchorVerbs.size()];
         this._anchorVerbs.copyInto(anc);
         textField = new WMLTextField(
            this._browserContent,
            this._defaultFont,
            this._text.toString(),
            this._offsets.toArray(),
            this._attributes.toArray(),
            this._currentFonts,
            this._currentColors,
            null,
            varr,
            this._varStarts.toArray(),
            anc,
            this._defaultFontFace,
            this._autoMatch,
            style,
            this._minimalMenuMode
         );
      }

      int paragraphStyle = this._paragraphStyle;
      this.clear();
      this._paragraphStyle = paragraphStyle;
      if (textField instanceof WMLTextField && this._textVariables != null) {
         this._textVariables.addElement(textField);
      }

      return textField;
   }

   final String getCurrentText() {
      return this._text.toString();
   }

   private final long getStyleForParagraph() {
      long style = this._paragraphStyle;
      if ((style & 0) != 0) {
         style |= 4294967296L;
      }

      if ((style & 524288) != 0) {
         style |= 8589934592L;
      }

      if ((style & 262144) != 0) {
         style |= 12884901888L;
      }

      return style;
   }

   final void setWrappingStyle(boolean wrap) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setAutoMatch(boolean on) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void clear() {
      if (this._anchorCnt != 0) {
         WMLAnchorVerb anchor = (WMLAnchorVerb)this._anchorVerbs.lastElement();
         anchor.setEndOffset((short)this._text.length());
         WMLAnchorVerb newAnchor = (WMLAnchorVerb)anchor.clone();
         newAnchor.setStartOffset(0);
         this._anchorVerbs.removeAllElements();
         this._anchorVerbs.addElement(newAnchor);
         this._oldAnchorVerbs.addElement(anchor);
         this._firstAnchorVerb = true;
      } else {
         this._anchorVerbs.removeAllElements();
      }

      this._varStarts.reset();
      this._vars.removeAllElements();
      this._text.setLength(0);
      this._offsets.removeAllElements();
      this._offsets.addElement(0);
      this._attributes.removeAllElements();
      this._currentFonts = new Object[0];
      if (Graphics.isColor()) {
         this._currentColors = new int[0];
      }

      this._paragraphStyle = 0;
   }

   static {
      _defaultManagerStack.push(new Object());
   }
}
