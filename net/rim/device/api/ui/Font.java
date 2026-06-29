package net.rim.device.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.TraceBack;

public class Font {
   private int _style;
   private int _height;
   private int _leading;
   private int _ascent;
   private int _descent;
   private String _name;
   private int _effects;
   private int _antialiasMode;
   private int _A;
   private int _B;
   private int _C;
   private int _D;
   private int _Tx;
   private int _Ty;
   private FontFamily _family;
   private int _effectsStrokeColor;
   private int _effectsFillColor;
   private int[] _transform = new int[6];
   TextMetrics _textMetrics = new TextMetrics();
   GlyphMetrics _glyphMetrics = new GlyphMetrics();
   public static final int PLAIN = 0;
   public static final int BOLD = 1;
   public static final int ITALIC = 2;
   public static final int EXTRA_BOLD = 64;
   public static final int UNDERLINED = 4;
   public static final int DOTTED_UNDERLINED = 8;
   public static final int BROKEN_LINE_UNDERLINED = 16;
   public static final int STRIKE_THROUGH = 32;
   private static final int FIRST_EFFECT = 256;
   public static final int PLAIN_OUTLINE_EFFECT = 256;
   public static final int GRAY_OUTLINE_EFFECT = 512;
   public static final int COLORED_OUTLINE_EFFECT = 768;
   public static final int ENGRAVED_EFFECT = 1024;
   public static final int EMBOSSED_EFFECT = 1280;
   public static final int DROP_SHADOW_LEFT_EFFECT = 1536;
   public static final int COLORED_DROP_SHADOW_LEFT_EFFECT = 1792;
   public static final int DROP_SHADOW_RIGHT_EFFECT = 2048;
   public static final int COLORED_DROP_SHADOW_RIGHT_EFFECT = 2304;
   public static final int GLOW_EFFECT = 2560;
   public static final int MONO_WIDTH_EFFECT = 65536;
   public static final int CALLIGRAPHIC_EFFECT = 131072;
   public static final int ANTIALIAS_NONE = 1;
   public static final int ANTIALIAS_STANDARD = 2;
   public static final int ANTIALIAS_SUBPIXEL = 3;
   public static final int ANTIALIAS_LOW_RES = 4;
   public static final long GUID_FONT_CHANGED = -4394903006263251010L;
   public static final int APPLICATION = 1;
   public static final int TRADITIONAL_CHINESE_HINT = 1024;
   public static final int SIMPLIFIED_CHINESE_HINT = 2048;
   public static final int JAPANESE_HINT = 3072;
   public static final int KOREAN_HINT = 4096;
   public static final int HAN_MASK = -7169;
   public static final int LATIN_SCRIPT = 1;
   public static final int GREEK_SCRIPT = 2;
   public static final int CYRILLIC_SCRIPT = 4;
   public static final int ARMENIAN_SCRIPT = 8;
   public static final int HEBREW_SCRIPT = 16;
   public static final int ARABIC_SCRIPT = 32;
   public static final int THAI_SCRIPT = 262144;
   public static final int HANGUL_SCRIPT = 8388608;
   public static final int HIRAGANA_SCRIPT = 134217728;
   public static final int KATAKANA_SCRIPT = 268435456;
   public static final int BOPOMOFO_SCRIPT = 536870912;
   public static final int CJK_SCRIPT = 1073741824;
   public static final int COPYRIGHT_NAME_CODE = 0;
   public static final int LICENSE_NAME_CODE = 13;
   private static Font _defaultFont;

   Font(FontFamily family, int style, int height) {
      this(family, style, height, 1, 0, 65536, 0, 0, 65536, 0, 0);
   }

   Font(FontFamily family, int style, int height, int antialiasingMode, int effects, int A, int B, int C, int D, int Tx, int Ty) {
      this(family, style, height, 1, effects, A, B, C, D, Tx, Ty, 0, 16777215);
   }

   Font(
      FontFamily family,
      int style,
      int height,
      int antialiasingMode,
      int effects,
      int A,
      int B,
      int C,
      int D,
      int Tx,
      int Ty,
      int effectsStrokeColor,
      int effectsFillColor
   ) {
      this._style = style;
      this._height = height;
      this._family = family;
      if (family == null) {
         this._name = FontRegistry.DEFAULT_FAMILY;
      } else {
         this._name = family.getName();
      }

      this._antialiasMode = antialiasingMode;
      this._effects = effects;
      this._effectsStrokeColor = effectsStrokeColor;
      this._effectsFillColor = effectsFillColor;
      this._A = A;
      this._B = B;
      this._C = C;
      this._D = D;
      this._Tx = Tx;
      this._Ty = Ty;
      this._transform[0] = A;
      this._transform[1] = B;
      this._transform[2] = C;
      this._transform[3] = D;
      this._transform[4] = Tx;
      this._transform[5] = Ty;
      this.setMetrics();
      if (this._height != this._ascent + this._descent + this._leading) {
         throw new IllegalStateException("Font: height != ascent + descent + leading");
      }
   }

   private native void setMetrics();

   public Font derive(int style) {
      return this.derive(style, this._height);
   }

   public Font derive(int style, int height) {
      return this.derive(style, height, 0, this._antialiasMode, this._effects);
   }

   public Font derive(int style, int height, int units) {
      return this.derive(style, height, units, this._antialiasMode, this._effects);
   }

   public Font derive(int style, int height, int units, int antialiasMode, int effects) {
      return this.derive(style, height, units, antialiasMode, effects, this._A, this._B, this._C, this._D, this._Tx, this._Ty);
   }

   public synchronized Font derive(int style, int height, int units, int antialiasMode, int effects, int[] transform) {
      return transform != null && transform.length == 6
         ? this.derive(style, height, units, antialiasMode, effects, transform[0], transform[1], transform[2], transform[3], transform[4], transform[5])
         : this;
   }

   public synchronized Font derive(int style, int height, int units, int antialiasMode, int effects, int A, int B, int C, int D, int Tx, int Ty) {
      Font font = null;
      if (effects >= 0 && 1 <= antialiasMode && 4 >= antialiasMode) {
         if (this._family != null) {
            font = this._family.getFont(style, height, units, antialiasMode, effects, A, B, C, D, Tx, Ty, this._effectsStrokeColor, this._effectsFillColor);
         }

         return font == null ? this : font;
      } else {
         return this;
      }
   }

   public synchronized Font derive(
      int style,
      int height,
      int units,
      int antialiasMode,
      int effects,
      int A,
      int B,
      int C,
      int D,
      int Tx,
      int Ty,
      int effectsStrokeColor,
      int effectsFillColor
   ) {
      Font font = null;
      if (effects >= 0 && 1 <= antialiasMode && 4 >= antialiasMode) {
         if (this._family != null) {
            font = this._family.getFont(style, height, units, antialiasMode, effects, A, B, C, D, Tx, Ty, effectsStrokeColor, effectsFillColor);
         }

         return font == null ? this : font;
      } else {
         return this;
      }
   }

   public final int getAdvance(String text) {
      if (text == null) {
         return 0;
      }

      int tLength = text.length();
      return tLength == 0 ? 0 : this.getAdvance(text, 0, tLength);
   }

   public native int getAdvance(char var1);

   public int getAdvance(String text, int offset, int length) {
      if (text != null) {
         int tLength = text.length();
         if (length == Integer.MAX_VALUE) {
            length = tLength - offset;
         }

         if (offset < 0 || length < 0 || offset + length > tLength) {
            throw new IllegalArgumentException();
         } else {
            return tLength == 0 ? 0 : this._getAdvance(text, offset, length);
         }
      } else if ((offset != 0 || length != 0) && length != Integer.MAX_VALUE) {
         throw new NullPointerException();
      } else {
         return 0;
      }
   }

   private native int _getAdvance(String var1, int var2, int var3);

   public int getAdvance(StringBuffer text, int offset, int length) {
      if (text != null) {
         int tLength = text.length();
         if (length == Integer.MAX_VALUE) {
            length = tLength - offset;
         }

         if (offset < 0 || length < 0 || offset + length > tLength) {
            throw new IllegalArgumentException();
         } else {
            return tLength == 0 ? 0 : this._getAdvance(text, offset, length);
         }
      } else if ((offset != 0 || length != 0) && length != Integer.MAX_VALUE) {
         throw new NullPointerException();
      } else {
         return 0;
      }
   }

   private native int _getAdvance(StringBuffer var1, int var2, int var3);

   public int getAdvance(char[] text, int offset, int length) {
      if (text != null) {
         if (length == Integer.MAX_VALUE) {
            length = text.length - offset;
         }

         if (offset < 0 || length < 0 || offset + length > text.length) {
            throw new IllegalArgumentException();
         } else {
            return text.length == 0 ? 0 : this._getAdvance(text, offset, length);
         }
      } else if ((offset != 0 || length != 0) && length != Integer.MAX_VALUE) {
         throw new NullPointerException();
      } else {
         return 0;
      }
   }

   private native int _getAdvance(char[] var1, int var2, int var3);

   public int getAntialiasMode() {
      return this._antialiasMode;
   }

   public int getAscent() {
      return this._ascent;
   }

   public int getBaseline() {
      return this._ascent + this._leading;
   }

   public static Font getDefault() {
      return _defaultFont != null ? _defaultFont : FontRegistry.getDefaultFont();
   }

   public static int getDefaultHeight(int units) {
      return FontRegistry.getDefaultHeight(units);
   }

   public int getDescent() {
      return this._descent;
   }

   public int getEffects() {
      return this._effects;
   }

   public FontFamily getFontFamily() {
      return this._family;
   }

   public int getHeight() {
      return this._height;
   }

   public int getHeight(int units) {
      return Ui.convertSize(this._height, 0, units);
   }

   public int getLeading() {
      return this._leading;
   }

   public int getStyle() {
      return this._style;
   }

   public int[] getTransform() {
      return Arrays.copy(this._transform);
   }

   public int getEffectsStrokeColor() {
      return this._effectsStrokeColor;
   }

   public int getEffectsFillColor() {
      return this._effectsFillColor;
   }

   public boolean isBold() {
      return (this._style & 1) != 0;
   }

   public boolean isItalic() {
      return (this._style & 2) != 0;
   }

   public boolean isUnderlined() {
      return (this._style & 4) != 0;
   }

   public boolean isPlain() {
      return (this._style & 15) == 0;
   }

   public synchronized int measureText(String text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length();
         return offset + length <= tLength && tLength != 0 ? this._measureText(text, offset, length, param, metrics) : 0;
      } else {
         return 0;
      }
   }

   private native int _measureText(String var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   public synchronized int measureText(StringBufferGap text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length();
         return offset + length <= tLength && tLength != 0 ? this._measureText(text, offset, length, param, metrics) : 0;
      } else {
         return 0;
      }
   }

   private native int _measureText(StringBufferGap var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   public synchronized int measureText(StringBuffer text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length();
         return offset + length <= tLength && tLength != 0 ? this._measureText(text, offset, length, param, metrics) : 0;
      } else {
         return 0;
      }
   }

   public synchronized int measureText(char[] text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length;
         return offset + length <= tLength && tLength != 0 ? this._measureText(text, offset, length, param, metrics) : 0;
      } else {
         return 0;
      }
   }

   private native int _measureText(StringBuffer var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   private native int _measureText(char[] var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   public int getBounds(String text) {
      if (text == null) {
         return 0;
      }

      int tLength = text.length();
      return tLength == 0 ? 0 : this.getBounds(text, 0, tLength);
   }

   public int getBounds(StringBuffer text) {
      if (text == null) {
         return 0;
      }

      int tLength = text.length();
      return tLength == 0 ? 0 : this.getBounds(text, 0, tLength);
   }

   public synchronized int getBounds(char aChar) {
      return this.getGlyphMetrics(aChar, this._glyphMetrics) == 0
         ? Math.max(
            this._glyphMetrics.iBearingX + this._glyphMetrics.iBitmapWidth,
            this._glyphMetrics.iAdvance >= this._glyphMetrics.iBitmapWidth ? this._glyphMetrics.iAdvance : this._glyphMetrics.iBitmapWidth
         )
         : this.getAdvance(aChar);
   }

   public synchronized int getBounds(StringBuffer text, int offset, int length) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length();
         if (offset + length <= tLength && tLength != 0) {
            this._measureText(text, offset, length, null, this._textMetrics);
            return Math.max(
               this._textMetrics.iBoundsBrX - this._textMetrics.iBoundsTlX,
               this._textMetrics.iAdvanceX >= this._textMetrics.iBoundsBrX ? this._textMetrics.iAdvanceX : this._textMetrics.iBoundsBrX
            );
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public synchronized int getBounds(StringBufferGap text, int offset, int length) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length();
         if (offset + length <= tLength && tLength != 0) {
            this._measureText(text, offset, length, null, this._textMetrics);
            return Math.max(
               this._textMetrics.iBoundsBrX - this._textMetrics.iBoundsTlX,
               this._textMetrics.iAdvanceX >= this._textMetrics.iBoundsBrX ? this._textMetrics.iAdvanceX : this._textMetrics.iBoundsBrX
            );
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public synchronized int getBounds(String text, int offset, int length) {
      if (text != null && offset >= 0 && length >= 0) {
         int tLength = text.length();
         if (offset + length <= tLength && tLength != 0) {
            this._measureText(text, offset, length, null, this._textMetrics);
            return Math.max(
               this._textMetrics.iBoundsBrX - this._textMetrics.iBoundsTlX,
               this._textMetrics.iAdvanceX >= this._textMetrics.iBoundsBrX ? this._textMetrics.iAdvanceX : this._textMetrics.iBoundsBrX
            );
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public synchronized int getBounds(char[] text, int offset, int length) {
      if (text == null) {
         return 0;
      } else if (offset >= 0 && length >= 0 && offset + length <= text.length && text.length != 0) {
         this._measureText(text, offset, length, null, this._textMetrics);
         return Math.max(
            this._textMetrics.iBoundsBrX - this._textMetrics.iBoundsTlX,
            this._textMetrics.iAdvanceX >= this._textMetrics.iBoundsBrX ? this._textMetrics.iAdvanceX : this._textMetrics.iBoundsBrX
         );
      } else {
         return 0;
      }
   }

   public void getMetrics(FontMetrics aFontMetrics) {
      this.getMetrics(aFontMetrics, 0, 0);
   }

   public void getMetrics(FontMetrics aFontMetrics, int aScripts) {
      this.getMetrics(aFontMetrics, aScripts, 0);
   }

   public void getMetricsForLocale(FontMetrics aFontMetrics, int aLocale) {
      this.getMetrics(aFontMetrics, 0, aLocale);
   }

   public native void getMetrics(FontMetrics var1, int var2, int var3);

   public native int getGlyphMetrics(char var1, GlyphMetrics var2);

   public static void setDefaultFont(Font defaultFont) {
      _defaultFont = defaultFont;

      try {
         RIMGlobalMessagePoster.postGlobalEvent(Application.getApplication().getProcessId(), -4394903006263251010L, 1, 0, null, null);
      } catch (IllegalStateException var2) {
      }
   }

   public static void setDefaultFontForSystem(Font defaultFont) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (defaultFont == null) {
         throw new IllegalArgumentException("System font must not be null.");
      }

      FontRegistry.setDefaultFont(defaultFont);
   }

   public static void setDefaultFontForSystem(String family, int style, int size, int units) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (family != null && size != 0) {
         FontRegistry.setDefaultFont(family, style, size, units);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public String toString() {
      ResourceBundle bundle = UiInternal.BUNDLE;
      StringBuffer s = new StringBuffer(128);
      s.append(this.getFontFamily().getName() + ' ' + this.getHeight() + " px ");
      if ((this.getStyle() & 1) != 0) {
         s.append(bundle.getString(19));
      }

      if ((this.getStyle() & 2) != 0) {
         s.append(bundle.getString(20));
      }

      if ((this.getStyle() & 8) != 0) {
         s.append(bundle.getString(18));
      }

      if ((this.getStyle() & 4) != 0) {
         s.append(bundle.getString(21));
      }

      return s.toString();
   }

   public String getCopyright() {
      return this.getName(0);
   }

   public String getLicense() {
      return this.getName(13);
   }

   public String getName(int aNameCode) {
      char[] s = new char[0];
      this.getName(s, aNameCode);
      return new String(s);
   }

   private native void getName(char[] var1, int var2);

   public boolean hasAttributes(
      int aStyle,
      int aHeight,
      int aAntialiasMode,
      int aEffects,
      int aA,
      int aB,
      int aC,
      int aD,
      int aTx,
      int aTy,
      int aEffectsStrokeColor,
      int aEffectsFillColor
   ) {
      return aStyle == this._style
         && aHeight == this._height
         && aAntialiasMode == this._antialiasMode
         && aEffects == this._effects
         && aA == this._A
         && aB == this._B
         && aC == this._C
         && aD == this._D
         && aTx == this._Tx
         && aTy == this._Ty
         && aEffectsStrokeColor == this._effectsStrokeColor
         && aEffectsFillColor == this._effectsFillColor;
   }
}
