package net.rim.wica.runtime.ui.internal.component.table;

public class FontStyleColor {
   private int _foregroundColor = 0;
   private int _backgroundColor = 0;
   private int _fontStyle = 0;
   private boolean _isForegroundColor;
   private boolean _isBackgroundColor;
   private boolean _isFontStyle;

   public boolean isBackgroundColor() {
      return this._isBackgroundColor;
   }

   public boolean isFontStyle() {
      return this._isFontStyle;
   }

   public boolean isForegroundColor() {
      return this._isForegroundColor;
   }

   public int getBackgroundColor() {
      return this._backgroundColor;
   }

   public void setBackgroundColor(int backgroundColor) {
      this._isBackgroundColor = true;
      this._backgroundColor = backgroundColor;
   }

   public int getFontStyle() {
      return this._fontStyle;
   }

   public void setFontStyle(int fontStyle) {
      this._isFontStyle = true;
      this._fontStyle = fontStyle;
   }

   public int getForegroundColor() {
      return this._foregroundColor;
   }

   public void setForegroundColor(int foregroundColor) {
      this._isForegroundColor = true;
      this._foregroundColor = foregroundColor;
   }
}
