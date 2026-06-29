package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.ArticInterface$Line;
import net.rim.device.internal.ui.FormatParams;
import net.rim.tid.text.AttributedString;
import net.rim.vm.Array;

public class RichTextField extends TextField implements ActiveRegionSupport$ActiveRegionFieldIf {
   ActiveRegionSupport _arSupport;
   private boolean _legacyArraysUpdated = false;
   private boolean _legacyAttributesUsed = false;
   private int[] _offsets;
   private int _offset_no;
   private byte[] _attributes;
   private Font[] _fonts;
   private int[] _colorForeground;
   private int[] _colorBackground;
   private Object[] _cookies;
   private int _layoutWidth;
   public static final int TEXT_ALIGN_LEFT;
   public static final int USE_TEXT_WIDTH;
   public static final int TEXT_ALIGN_HCENTER;
   public static final int TEXT_ALIGN_RIGHT;
   private static final long LEGACY_FONT_INDEX_ATTRIB_MASK;
   private static final int LEGACY_FONT_INDEX_ATTRIB_SHIFT;

   public void setText(String text, int[] offsets, byte[] attributes, Font[] fonts) {
      this.setText(text, offsets, attributes, fonts, null);
   }

   public void setText(String text, int[] offsets, byte[] attributes, Font[] fonts, Object[] cookies) {
      if (text != null) {
         if (text.length() > 0 && offsets != null && attributes != null) {
            if (offsets.length != attributes.length + 1) {
               throw new IllegalArgumentException("attributes array must contain exactly one less entry than the offsets array");
            }

            if (fonts == null) {
               throw new IllegalArgumentException("fonts array cannot be null");
            }
         } else if (text.length() > 0 && offsets != null && attributes == null) {
            throw new IllegalArgumentException("attributes array cannot be null");
         }

         this.vetOffsets(text, offsets);
         this.vetAttributes(attributes, fonts);
      } else {
         text = "";
      }

      this.findDefaultDirectionality(text);
      AttributedString attribText = new AttributedString(text, this.getDefaultFontAttributes(), 0);
      this.setText(attribText, offsets, attributes, fonts, cookies);
   }

   protected void setText(AttributedString attribText, int[] offsets, byte[] attributes, Font[] fonts, Object[] cookies) {
      this._offsets = offsets != null ? Arrays.copy(offsets) : new int[]{0, attribText.length()};
      this._offset_no = this._offsets.length;
      this._attributes = attributes != null ? Arrays.copy(attributes) : new byte[]{0};
      if (fonts != null) {
         this._fonts = new Font[fonts.length];
         System.arraycopy(fonts, 0, this._fonts, 0, fonts.length);
      } else {
         this._fonts = new Font[1];
      }

      this.setCookies(cookies);
      this.convertOffsetsToAttribString(attribText, 0, this._offset_no - 1, this._offsets, this._attributes, this._fonts);
      this.setAttributedText(attribText);
   }

   protected int getRunEnd() {
      return this._arSupport.getRunEnd();
   }

   protected int getRunStart() {
      return this._arSupport.getRunStart();
   }

   void setFont(int index, Font font) {
      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      this._fonts[index] = font;
      this.convertOffsetsToAttribString(null, 0, this._offset_no - 1, this._offsets, this._attributes, this._fonts);
   }

   public int getRegion(int offset) {
      if (offset == super._text.length() && offset != 0) {
         offset--;
      }

      return this._arSupport.getRegion(offset);
   }

   public Object getRegionCookie() {
      return this._arSupport.getCookieWithFocus(this.getCaretPosition());
   }

   public Object getRegionCookie(int region) {
      return null != this._cookies && this._cookies.length != 0 && region >= 0 ? this._cookies[region] : null;
   }

   public String getRegionText() {
      return this._arSupport.getCurrentRegionText(this.getCaretPosition(), super._text);
   }

   public String getRegionText(int region) {
      return this._arSupport.getRegionText(region, super._text);
   }

   public Font getRegionFont() {
      return Ui.getFontFromAttributes(super._insertionAttrib, this.getFont());
   }

   public Font getRegionFont(int region) {
      return !this._arSupport.adjustCurrentRunForIndex(region) ? null : Ui.getFontFromAttributes(this._arSupport.getRunAttrib(), this.getFont());
   }

   protected Font[] getFonts() {
      return this.getFonts(true);
   }

   protected Font[] getFonts(boolean convertDefaultFonts) {
      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      if (this._fonts == null) {
         return null;
      }

      Font[] fonts = new Font[this._fonts.length];
      System.arraycopy(this._fonts, 0, fonts, 0, this._fonts.length);
      if (convertDefaultFonts) {
         for (int i = 0; i < fonts.length; i++) {
            if (fonts[i] == null) {
               fonts[i] = this.getFont();
            }
         }
      }

      return fonts;
   }

   protected int[] getOffsets() {
      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      return Arrays.copy(this._offsets);
   }

   protected byte[] getAttributes() {
      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      return Arrays.copy(this._attributes);
   }

   public void setAttribute(int index, int attribute) {
      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      if (attribute >= 0 && attribute < this._fonts.length) {
         if (this._attributes[index] != (byte)attribute) {
            this._attributes[index] = (byte)attribute;
            long attrib = this._fonts[attribute] == null ? this.getDefaultFontAttributes() : Ui.getAttributesFromFont(this._fonts[attribute]);
            long xAttrib = (long)attribute << 16;
            this.setAttrib(this._offsets[index], this._offsets[index + 1], attrib, 675086335, xAttrib, 16711680);
            this.updateLayout();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void setAttributes(int[] colorForeground, int[] colorBackground) {
      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      if (colorForeground != null && colorForeground.length != this._fonts.length) {
         throw new IllegalArgumentException();
      }

      if (colorBackground != null && colorBackground.length != this._fonts.length) {
         throw new IllegalArgumentException();
      }

      this._colorForeground = Arrays.copy(colorForeground);
      this._colorBackground = Arrays.copy(colorBackground);
      this.startLayoutUpdate();

      for (int i = 0; i < this._offset_no - 1; i++) {
         long attrib = 0;
         int id = this._attributes[i];
         long foreground = colorForeground != null && colorForeground[id] != -1 ? Ui.convertColorTo16bit(colorForeground[id]) : Ui.DEFAULT_16BIT_COLOR;
         long background = colorBackground != null && colorBackground[id] != -1 ? Ui.convertColorTo16bit(colorBackground[id]) : Ui.DEFAULT_16BIT_COLOR;
         attrib |= foreground << 32 | background << 48;
         super._text.setAttrib(this._offsets[i], this._offsets[i + 1], attrib, -4294967296L, 0, 0);
      }

      this.endLayoutUpdate();
   }

   protected int[] getForegroundColors() {
      return Arrays.copy(this._colorForeground);
   }

   protected int[] getBackgroundColors() {
      return Arrays.copy(this._colorBackground);
   }

   protected void appendFonts(Font[] fonts) {
      if (fonts != null && fonts.length != 0) {
         if (!this._legacyArraysUpdated) {
            this.initLegacyArrays();
         }

         int newFontCount = fonts.length;
         if (this._fonts == null) {
            this._fonts = new Font[newFontCount];
            System.arraycopy(fonts, 0, this._fonts, 0, newFontCount);
         } else {
            int oldFontCount = this._fonts.length;
            Array.resize(this._fonts, oldFontCount + newFontCount);
            System.arraycopy(fonts, 0, this._fonts, oldFontCount, newFontCount);
         }
      }
   }

   protected int append(String text, int[] offsets, int[] lengths, byte[] attr) {
      if (text == null) {
         return 0;
      }

      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      this.vetArguments(text, offsets, lengths, attr);
      int textLength = super._text.length();
      int[] newOffsets;
      if (lengths == null) {
         newOffsets = this.insertText(text, offsets);
      } else {
         newOffsets = this.insertText(text, offsets, lengths);
      }

      int deltaLength = super._text.length() - textLength;
      if (deltaLength > 0) {
         this.addOffsetAndAttributes(newOffsets, attr);
         int startOffset = this._offset_no - newOffsets.length - 1;
         this.convertOffsetsToAttribString(null, startOffset, this._offset_no - 1, this._offsets, this._attributes, this._fonts);
      }

      this.fireTextChangeEvent(0);
      return deltaLength;
   }

   protected void setFont(int[] index, Font[] fonts) {
      if (index == null) {
         throw new IllegalArgumentException("Index array is null");
      }

      if (fonts == null) {
         throw new IllegalArgumentException("Font array is null");
      }

      int indexCount = index.length;
      if (indexCount != fonts.length) {
         throw new IllegalArgumentException("Font and index arrays of unequal length");
      }

      if (!this._legacyArraysUpdated) {
         this.initLegacyArrays();
      }

      for (int i = 0; i < indexCount; i++) {
         this._fonts[index[i]] = fonts[i];
      }

      this.convertOffsetsToAttribString(null, 0, this._offset_no - 1, this._offsets, this._attributes, this._fonts);
   }

   @Override
   public boolean regionsHaveSameCookie(int regionId1, int regionId2) {
      return false;
   }

   @Override
   public Object getCookie(int id) {
      return null;
   }

   @Override
   public boolean isCookieValid(int id) {
      return true;
   }

   private void setCookies(Object[] cookies) {
      if (cookies != null) {
         this._cookies = new Object[cookies.length];
         System.arraycopy(cookies, 0, this._cookies, 0, cookies.length);
      } else {
         this._cookies = null;
      }
   }

   private void vetOffsets(String text, int[] offsets) {
      if (offsets != null) {
         if (offsets[0] != 0) {
            throw new IllegalArgumentException("offsets[0] != 0");
         }

         if (offsets[offsets.length - 1] != text.length()) {
            throw new IllegalArgumentException("offsets[n-1] != text.length()");
         }

         for (int lv = offsets.length - 2; lv >= 0; lv--) {
            if (offsets[lv] >= offsets[lv + 1]) {
               throw new IllegalArgumentException("offsets must be monotonically increasing");
            }
         }
      }
   }

   private void vetAttributes(byte[] attributes, Font[] fonts) {
      if (attributes != null) {
         int maxAttribute = fonts.length;

         for (int lv = attributes.length - 1; lv >= 0; lv--) {
            int attribute = attributes[lv];
            if (attribute < 0 || maxAttribute <= attribute) {
               throw new IllegalArgumentException("attribute out of range");
            }
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (this.isSelecting() && key == '\n') {
         return true;
      }

      switch (key) {
         case ' ':
            if ((status & 1) == 0) {
               int direction = (status & 2) == 0 ? 512 : 256;
               this.getScreen().scroll(direction);
               return true;
            }
         default:
            return false;
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      return super._text.length() == 0 ? amount : super.moveFocus(amount, status, time);
   }

   @Override
   public void setText(String text) {
      super.setText(text);
      this.setCookies(null);
   }

   private static long validateStyle(long style) {
      style &= -13510798882111489L;
      style |= 9007199254740992L;
      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style;
   }

   @Override
   protected long getDefaultFontAttributes() {
      long attrib = super.getDefaultFontAttributes();
      if (this.isStyle(524288)) {
         return attrib | 4194304;
      }

      if (this.isStyle(262144)) {
         attrib |= 12582912;
      }

      return attrib;
   }

   private void initLegacyArrays() {
      int INC = 10;
      int arraySize = INC;
      int[] offsets = new int[arraySize];
      byte[] attributes = new byte[arraySize];
      Font[] fonts = new Font[arraySize];
      int offsetCount = 0;
      offsets[0] = 0;
      int maxFontIndex = 0;

      while (this._arSupport.adjustCurrentRunForIndex(offsetCount)) {
         if (offsetCount + 1 == arraySize) {
            arraySize += INC;
            Array.resize(offsets, arraySize);
            Array.resize(attributes, arraySize);
            Array.resize(fonts, arraySize);
         }

         offsets[offsetCount + 1] = this._arSupport.getRunEnd();
         if (this._legacyAttributesUsed) {
            byte fontIndex = (byte)((this._arSupport.getRunXAttrib() & 16711680) >> 16);
            attributes[offsetCount] = fontIndex;
         } else {
            Font font = Ui.getFontFromAttributes(this._arSupport.getRunAttrib(), this.getFont());
            attributes[offsetCount] = this.findFontIndex(fonts, font);
         }

         maxFontIndex = Math.max(maxFontIndex, attributes[offsetCount]);
         offsetCount++;
      }

      this._offsets = Arrays.copy(offsets, 0, ++offsetCount);
      this._attributes = Arrays.copy(attributes, 0, offsetCount - 1);
      if (this._fonts == null || !this._legacyAttributesUsed) {
         this._fonts = new Font[maxFontIndex + 1];
         System.arraycopy(fonts, 0, this._fonts, 0, maxFontIndex + 1);
      }

      this._offset_no = offsetCount;
      this._legacyArraysUpdated = true;
   }

   private byte findFontIndex(Font[] fonts, Font font) {
      byte index;
      for (index = 0; fonts[index] != null; index++) {
         if (fonts[index] == font) {
            return index;
         }
      }

      fonts[index] = font;
      return index;
   }

   public RichTextField() {
      this("", 0);
   }

   public RichTextField(long style) {
      this("", style);
   }

   private synchronized int[] insertText(String text, int[] offsets, int[] lengths) {
      int offsetsArrayLength = offsets.length;
      int newLength = super._text.length();
      int[] newOffsets = new int[offsetsArrayLength];

      for (int i = 0; i < offsetsArrayLength; i++) {
         super._text.insert(newLength, text.substring(offsets[i], offsets[i] + lengths[i]));
         newLength += lengths[i];
         newOffsets[i] = newLength;
      }

      return newOffsets;
   }

   private synchronized int[] insertText(String text, int[] offsets) {
      int offsetsArrayLength = offsets.length - 1;
      int newLength = super._text.length();
      int[] newOffsets = new int[offsetsArrayLength];

      for (int i = 0; i < offsetsArrayLength; i++) {
         super._text.insert(newLength, text.substring(offsets[i], offsets[i + 1]));
         int tempLen = offsets[i + 1] - offsets[i];
         newLength += tempLen;
         newOffsets[i] = newLength;
      }

      return newOffsets;
   }

   private void addOffsetAndAttributes(int[] offsets, byte[] attributes) {
      int offsetsLength = this._offset_no;
      int attributesLength = this._attributes.length;
      if (offsetsLength == 2 && this._offsets[0] == this._offsets[1]) {
         offsetsLength--;
         attributesLength--;
         this._offset_no--;
      }

      Array.resize(this._offsets, offsetsLength + offsets.length);
      this._offset_no += offsets.length;
      System.arraycopy(offsets, 0, this._offsets, offsetsLength, offsets.length);
      Array.resize(this._attributes, attributesLength + attributes.length);
      System.arraycopy(attributes, 0, this._attributes, attributesLength, attributes.length);
   }

   private void vetArguments(String text, int[] offsets, int[] lengths, byte[] attr) {
      if (offsets != null && attr != null) {
         int offsetsArrayLength = offsets.length;
         int lengthsArrayLength = 0;
         int attrArrayLength = attr.length;
         if (attrArrayLength < 1) {
            throw new IllegalArgumentException("The number of attributes must be greater than zero.");
         }

         if (lengths != null) {
            lengthsArrayLength = lengths.length;
            if (offsetsArrayLength != attrArrayLength) {
               throw new IllegalArgumentException("If lengths are provided, offsets array's length must the same as attributes array's length.");
            }
         } else if (offsetsArrayLength != attrArrayLength + 1) {
            throw new IllegalArgumentException("If lengths aren't provided, offsets array's length must be one greater than attributes array's length.");
         }

         if (offsets[0] < 0) {
            throw new IllegalArgumentException("The first offset cannot be less than 0.");
         }

         if (lengths != null && lengthsArrayLength != attrArrayLength) {
            throw new IllegalArgumentException("Lengths array's length must be identical to attributes array's length.");
         }

         this.vetAttributes(attr, this._fonts);

         for (int i = 0; i < lengthsArrayLength; i++) {
            if (lengths[i] < 1) {
               throw new IllegalArgumentException("Lengths may not be less than 1.");
            }
         }

         for (int i = 0; i < offsetsArrayLength - 1; i++) {
            if (offsets[i] >= offsets[i + 1]) {
               throw new IllegalArgumentException("Offsets should be monotonically increasing.");
            }
         }

         int temp = 0;
         if (lengths != null) {
            for (int i = 0; i < lengthsArrayLength; i++) {
               temp = offsets[i] + lengths[i];
               if (i < lengthsArrayLength - 1 && temp > offsets[i + 1]) {
                  throw new IllegalArgumentException("Substring " + i + " overlaps the next substring.");
               }
            }
         } else {
            temp = offsets[offsetsArrayLength - 1];
         }

         int textLength = text.length();
         if (temp > textLength) {
            throw new IllegalArgumentException("Substring oversteps end of text.");
         }
      } else {
         throw new IllegalArgumentException("Offsets array and attributes array may not be null.");
      }
   }

   public RichTextField(String text) {
      this(text, 0);
   }

   public RichTextField(String text, int[] offsets, byte[] attributes, Font[] fonts, Object[] cookies, long style) {
      super(validateStyle(style));
      this.setText(text, offsets, attributes, fonts, cookies);
      this._arSupport = new ActiveRegionSupport(super._text.getIterator(), this);
   }

   @Override
   public int getPreferredHeight() {
      return 0;
   }

   @Override
   int getLayoutWidth(int width) {
      return this.isStyle(67108864) ? this._layoutWidth : width;
   }

   @Override
   void handleLinesAfterFormat(FormatParams params) {
      super.handleLinesAfterFormat(params);
      this._layoutWidth = params._layoutWidth;
      if (this.isStyle(67108864) && (this.isStyle(262144) || this.isStyle(524288))) {
         int offset = Integer.MAX_VALUE;

         for (ArticInterface$Line line = params._lineList; line != null; line = line._next) {
            offset = Math.min(offset, line._originX);
         }

         for (ArticInterface$Line line = params._lineList; line != null; line = line._next) {
            line._boundsLeft += offset;
            line._boundsRight += offset;
            line._originX -= offset;
         }

         this._layoutWidth -= offset;
      }
   }

   @Override
   protected int getMaxLinesToFormat() {
      return this.isStyle(67108864) && (this.isStyle(262144) || this.isStyle(524288)) ? Integer.MAX_VALUE : super.getMaxLinesToFormat();
   }

   private synchronized void convertOffsetsToAttribString(AttributedString str, int start, int end, int[] offsets, byte[] attributes, Font[] fonts) {
      boolean changeField = str == null;
      if (changeField) {
         this.startLayoutUpdate();
      }

      for (int i = start; i < end; i++) {
         byte index = attributes[i];
         long attrib = fonts[index] == null ? this.getDefaultFontAttributes() : Ui.getAttributesFromFont(fonts[index]);
         long xAttrib = (long)(i + 1) << 5;
         long xAttribMask = 16711680;
         if (xAttrib > 65504) {
            System.err.println("Warning: too much cookies! Cookie " + i + " ignored");
         } else {
            xAttribMask |= 65504;
         }

         xAttrib |= (long)index << 16;
         if (changeField) {
            this.setAttrib(offsets[i], offsets[i + 1], attrib, 675086335, xAttrib, xAttribMask);
         } else {
            str.setAttrib(offsets[i], offsets[i + 1], attrib, 675086335, xAttrib, xAttribMask);
         }
      }

      if (changeField) {
         this.endLayoutUpdate();
         if (this._arSupport != null) {
            this._arSupport.init();
         }
      }

      this._legacyAttributesUsed = true;
   }

   @Override
   protected void notifyTextChanged(FormatParams aParams, boolean aIsInsertionOrDeletion) {
      super.notifyTextChanged(aParams, aIsInsertionOrDeletion);
      this._arSupport.init();
      if (aIsInsertionOrDeletion) {
         this._legacyArraysUpdated = false;
      }
   }

   public RichTextField(String text, int[] offsets, byte[] attributes, Font[] fonts, long style) {
      this(text, offsets, attributes, fonts, null, style);
   }

   public RichTextField(String text, long style) {
      super("", text, 1000000, validateStyle(style));
      this._arSupport = new ActiveRegionSupport(super._text.getIterator(), this);
   }

   @Override
   public int getCursorPosition() {
      return super.getCursorPosition();
   }

   @Override
   public String getText() {
      return super.getText();
   }

   @Override
   protected void layout(int width, int height) {
      super.layout(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      super.paint(graphics);
   }

   @Override
   public void setCursorPosition(int offset) {
      super.setCursorPosition(offset);
   }

   @Override
   public String getText(int offset, int length) {
      return super.getText(offset, length);
   }

   @Override
   public int getTextLength() {
      return super.getTextLength();
   }

   @Override
   public boolean isSelectable() {
      return super.isSelectable();
   }

   @Override
   public char charAt(int offset) {
      return super.charAt(offset);
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
   }
}
