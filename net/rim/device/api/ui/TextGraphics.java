package net.rim.device.api.ui;

import net.rim.device.internal.ui.StringBufferGap;

public class TextGraphics {
   private int _style;
   private int _height;
   private String _name;
   private int _effects;
   private int _antialiasMode = 1;
   private int _A = 65536;
   private int _B;
   private int _C;
   private int _D = 65536;
   private int _Tx;
   private int _Ty;
   private int _effectsStrokeColor = 0;
   private int _effectsFillColor = 16777215;
   private int[] _transform = new int[6];
   private static FontMetrics _fontMetrics = new FontMetrics();

   public TextGraphics(String typefaceName, int height) {
      if (typefaceName != null && typefaceName.length() != 0 && height > 0) {
         this._name = typefaceName;
         this._height = height;
      } else {
         throw new IllegalArgumentException("typeface name can't be empty and height should be more than 0");
      }
   }

   public TextGraphics(Font font) {
      if (font == null) {
         throw new IllegalArgumentException("Font object should be non zero");
      }

      this.setFontSpec(font);
   }

   public int getAntialiasMode() {
      return this._antialiasMode;
   }

   public String getTypefaceName() {
      return this._name;
   }

   public int getEffects() {
      return this._effects;
   }

   public int getHeight() {
      return this._height;
   }

   public int getHeightWithLeading() {
      this.getFontMetrics(_fontMetrics);
      return _fontMetrics.iHeight + _fontMetrics.iLeadingAbove + _fontMetrics.iLeadingBelow;
   }

   public int getStyle() {
      return this._style;
   }

   public int[] getTransform() {
      this._transform[0] = this._A;
      this._transform[1] = this._B;
      this._transform[2] = this._C;
      this._transform[3] = this._D;
      this._transform[4] = this._Tx;
      this._transform[5] = this._Ty;
      return this._transform;
   }

   public int getEffectsStrokeColor() {
      return this._effectsStrokeColor;
   }

   public int getEffectsFillColor() {
      return this._effectsFillColor;
   }

   public void setStyle(int aStyle) {
      this._style = aStyle;
   }

   public void setHeight(int aHeight) {
      if (aHeight <= 0) {
         throw new IllegalArgumentException("Height should be more than zero");
      }

      this._height = aHeight;
   }

   public void setHeightWithLeading(int aHeight) {
      if (aHeight <= 1) {
         throw new IllegalArgumentException("Height should be more than " + aHeight);
      }

      this._height = this._convertToFontEngineSize(aHeight);
   }

   private native int _convertToFontEngineSize(int var1);

   public void setTransform(int[] transform) {
      if (transform != null && transform.length == 6) {
         this._A = this._transform[0];
         this._B = this._transform[1];
         this._C = this._transform[2];
         this._D = this._transform[3];
         this._Tx = this._transform[4];
         this._Ty = this._transform[5];
      } else {
         throw new IllegalArgumentException("transfrorm is empty or not defined right");
      }
   }

   public void setTransform(int a, int b, int c, int d, int tx, int ty) {
      this._A = a;
      this._B = b;
      this._C = c;
      this._D = d;
      this._Tx = tx;
      this._Ty = ty;
   }

   public void setEffects(int aEffects) {
      this._effects = aEffects;
   }

   public void setAntialiasingMode(int aMode) {
      if (aMode != 1 && aMode != 2 && aMode != 3 && aMode != 4) {
         throw new IllegalArgumentException(" antialiasing mode should be one of the values defined in the Font class");
      }

      this._antialiasMode = aMode;
   }

   public void setEffectsStrokeColor(int color) {
      this._effectsStrokeColor = color;
   }

   public void setEffectsFillColor(int color) {
      this._effectsFillColor = color;
   }

   public void setFontSpec(Font font) {
      if (font == null) {
         throw new IllegalArgumentException(" Font object should be non null");
      }

      this._style = font.getStyle();
      this._height = font.getHeight() - font.getLeading();
      this._name = font.getFontFamily().getName();
      this._antialiasMode = font.getAntialiasMode();
      this._effects = font.getEffects();
      this._effectsStrokeColor = font.getEffectsStrokeColor();
      this._effectsFillColor = font.getEffectsFillColor();
      int[] trans = font.getTransform();
      this._A = trans[0];
      this._B = trans[1];
      this._C = trans[2];
      this._D = trans[3];
      this._Tx = trans[4];
      this._Ty = trans[5];
   }

   public void setTypefaceName(String typefaceName) {
      if (typefaceName != null && typefaceName.length() != 0) {
         this._name = typefaceName;
      } else {
         throw new IllegalArgumentException("font name should not be empty");
      }
   }

   public int measureText(String text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0 && text.length() >= offset + length && text.length() != 0 && param != null) {
         return this._measureText(text, offset, length, param, metrics);
      } else {
         throw new IllegalArgumentException("text should not be empty and the offset and length should be properly defined");
      }
   }

   private native int _measureText(String var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   public int measureText(StringBufferGap text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0 && text.length() >= offset + length && text.length() != 0 && param != null) {
         return this._measureText(text, offset, length, param, metrics);
      } else {
         throw new IllegalArgumentException("text should not be empty and the offset and length should be properly defined");
      }
   }

   private native int _measureText(StringBufferGap var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   public int measureText(StringBuffer text, int offset, int length, DrawTextParam param, TextMetrics metrics) {
      if (text != null && offset >= 0 && length >= 0 && text.length() >= offset + length && text.length() != 0 && param != null) {
         return this._measureText(text, offset, length, param, metrics);
      } else {
         throw new IllegalArgumentException("text should not be empty and the offset and length should be properly defined");
      }
   }

   private native int _measureText(StringBuffer var1, int var2, int var3, DrawTextParam var4, TextMetrics var5);

   public void getFontMetrics(FontMetrics aFontMetrics) {
      if (aFontMetrics == null) {
         throw new IllegalArgumentException("argument can't be empty");
      }

      this._getFontMetrics(aFontMetrics);
   }

   private native void _getFontMetrics(FontMetrics var1);

   public int drawText(Graphics graphics, String aText, int offset, int length, int x, int y, DrawTextParam aParam, TextMetrics aMetrics) {
      if (graphics != null && aText != null && aText.length() != 0 && aParam != null && offset >= 0 && length >= 0 && aText.length() >= offset + length) {
         return this._drawText(graphics, aText, offset, length, x, y, aParam, aMetrics);
      } else {
         throw new IllegalArgumentException("text should not be empty and the offset, length and draw parameters should be properly defined");
      }
   }

   private native int _drawText(Graphics var1, String var2, int var3, int var4, int var5, int var6, DrawTextParam var7, TextMetrics var8);

   public int drawText(Graphics graphics, StringBuffer aText, int offset, int length, int x, int y, DrawTextParam aParam, TextMetrics aMetrics) {
      if (graphics != null && aText != null && aText.length() != 0 && aParam != null && offset >= 0 && length >= 0 && aText.length() >= offset + length) {
         return this._drawText(graphics, aText, offset, length, x, y, aParam, aMetrics);
      } else {
         throw new IllegalArgumentException("text should not be empty and the offset and length should be properly defined");
      }
   }

   private native int _drawText(Graphics var1, StringBuffer var2, int var3, int var4, int var5, int var6, DrawTextParam var7, TextMetrics var8);

   public int drawText(Graphics graphics, StringBufferGap aText, int offset, int length, int x, int y, DrawTextParam aParam, TextMetrics aMetrics) {
      if (graphics != null && aText != null && aText.length() != 0 && aParam != null && offset >= 0 && length >= 0 && aText.length() >= offset + length) {
         return this._drawText(graphics, aText, offset, length, x, y, aParam, aMetrics);
      } else {
         throw new IllegalArgumentException("text should not be empty and the offset and length should be properly defined");
      }
   }

   private native int _drawText(Graphics var1, StringBufferGap var2, int var3, int var4, int var5, int var6, DrawTextParam var7, TextMetrics var8);
}
