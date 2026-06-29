package net.rim.device.internal.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.XYRect;

public class TextFlowRegion {
   public Object _obj;
   private String _fontFamily;
   private int _fontStyle;
   private int _fontHeight;
   private Font _font;
   private short _flags;
   private int _offsetYTop;
   private int _offsetYBottom;
   private short _offsetXLeft;
   private short _offsetXRight;
   public int _foregroundColour = -1;
   public int _backgroundColour = -1;
   public short _width;
   public short _height;
   public short _margin;
   private int _minTextWidth;
   private int _preferredTextWidth;
   private int _calcWidthStartOffset;
   private int _calcWidthEndOffset;
   private short _offsetXLeftmost;
   private short _offsetXRightmost;
   public static final short REGION_ALIGN_MASK;
   public static final short REGION_ALIGN_DEFAULT;
   public static final short REGION_ALIGN_LEFT;
   public static final short REGION_ALIGN_RIGHT;
   public static final short REGION_ALIGN_CENTER;
   public static final short REGION_DIR_MASK;
   public static final short REGION_DIR_LTR;
   public static final short REGION_DIR_RTL;
   public static final short REGION_DIR_OVERRIDE_LTR;
   public static final short REGION_DIR_OVERRIDE_RTL;
   public static final short REGION_VALIGN_MASK;
   public static final short REGION_VALIGN_DEFAULT;
   public static final short REGION_VALIGN_BOTTOM;
   public static final short REGION_VALIGN_TOP;
   public static final short REGION_VALIGN_MIDDLE;
   public static final short REGION_BREAKING_BEFORE;
   public static final short REGION_BREAKING_BEFORE_WITH_NEWLINE;
   public static final short REGION_BREAKING_AFTER;
   public static final short REGION_BREAKING_AFTER_WITH_NEWLINE;
   public static final short REGION_BREAK_MASK;
   public static final short REGION_DISPLAY_NONE;
   public static final short REGION_VISIBILITY_HIDDEN;
   public static final short REGION_ADDITIONAL_FLAGS_MASK;
   public static final short REGION_FIND_MAX_WIDTH;

   public TextFlowRegion() {
      this._offsetYBottom = this._offsetYTop = 0;
      this._offsetXLeft = this._offsetXRight = 0;
   }

   public short getFlags() {
      return this._flags;
   }

   public void setAdditionalFlags(short displayFlags) {
      this._flags = (short)(this._flags & -3073);
      this._flags = (short)(this._flags | displayFlags & 3072);
   }

   public void setVAlignment(short alignment) {
      this._flags = (short)(this._flags & -12289);
      this._flags = (short)(this._flags | alignment & 12288);
   }

   public void setFindMaxWidthFlag(boolean set) {
      if (set) {
         this._flags = (short)(this._flags | 16384);
      } else {
         this._flags = (short)(this._flags & -16385);
      }
   }

   public void setAlignment(short alignment) {
      this._flags = (short)(this._flags & -8);
      this._flags = (short)(this._flags | alignment & 7);
   }

   public void setDirection(short direction) {
      this._flags = (short)(this._flags & -9);
      this._flags = (short)(this._flags | direction & 8);
   }

   public void setBreakingFlags(short breaking) {
      this._flags = (short)(this._flags & -961);
      this._flags = (short)(this._flags | breaking & 960);
   }

   public void inherit(TextFlowRegion parentRegion) {
      this._fontFamily = parentRegion._fontFamily;
      this._fontStyle = parentRegion._fontStyle;
      this._fontHeight = parentRegion._fontHeight;
      this._font = parentRegion._font;
      this._foregroundColour = parentRegion._foregroundColour;
      this._backgroundColour = parentRegion._backgroundColour;
      this._flags = (short)(parentRegion._flags & -961);
      this._margin = parentRegion._margin;
   }

   public void setFont(Font f) {
      this._font = f;
   }

   public Font getFont() {
      return this._font;
   }

   public void setFontHeight(int height) {
      if (this._fontHeight != height) {
         this._fontHeight = height;
         this._font = null;
      }
   }

   public void setFontFamily(String family) {
      if (!family.equals(this._fontFamily)) {
         this._fontFamily = family;
         this._font = null;
      }
   }

   public void setFontStyle(int style) {
      if (this._fontStyle != style) {
         this._fontStyle = style;
         this._font = null;
      }
   }

   public int getFontStyle() {
      return this._fontStyle;
   }

   public int getFontHeight() {
      return this._fontHeight;
   }

   public String getFontFamily() {
      return this._fontFamily;
   }

   public int getOffsetYTop() {
      return this._offsetYTop;
   }

   public int getOffsetYBottom() {
      return this._offsetYBottom;
   }

   public int getOffsetXLeft() {
      return this._offsetXLeft;
   }

   public int getOffsetXRight() {
      return this._offsetXRight;
   }

   public void getCoords(XYRect rect) {
      if ((this._flags & 16384) != 0) {
         rect.set(this._offsetXLeftmost, this._offsetYTop, this._offsetXRightmost - this._offsetXLeftmost, this._offsetYBottom - this._offsetYTop);
      } else {
         rect.set(this._offsetXLeft, this._offsetYTop, this._offsetXRight - this._offsetXLeft, this._offsetYBottom - this._offsetYTop);
      }
   }

   public void setPosition(int leftX, int topY, int rightX, int bottomY) {
      this._offsetXLeft = (short)leftX;
      this._offsetYTop = topY;
      this._offsetXRight = (short)rightX;
      this._offsetYBottom = bottomY;
   }

   public void calculateTextWidth(StringBuffer text, int textPosition, int endTextPosition) {
      if (this._calcWidthEndOffset != endTextPosition || this._calcWidthStartOffset != textPosition) {
         this.calculateTextWidth0(text, textPosition, endTextPosition);
      }
   }

   public void invalidateTextWidths() {
      this._minTextWidth = 0;
      this._preferredTextWidth = 0;
      this._calcWidthEndOffset = 0;
      this._calcWidthStartOffset = 0;
   }

   private native void calculateTextWidth0(StringBuffer var1, int var2, int var3);

   public int getMinTextWidth() {
      return this._minTextWidth;
   }

   public int getPreferredTextWidth() {
      return this._preferredTextWidth;
   }
}
