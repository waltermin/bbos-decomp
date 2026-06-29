package net.rim.wica.runtime.ui.internal.component.table;

public class TableStyle {
   private FontStyleColor _superStyle;
   private FontStyleColor _headerStyle;
   private FontStyleColor _oddRowStyle;
   private FontStyleColor _evenRowStyle;
   public static final int HEADER_INDEX;
   public static final FontStyleColor FROZEN_STYLE = new FontStyleColor();
   public static final int GRID_COLOR;
   public static final int SELECTABLE_COLUMN_EXISTANCE_INDICATOR_COLOR;

   public void setSuperStyle(FontStyleColor superStyle) {
      this._superStyle = superStyle;
   }

   public void setEvenRowStyle(FontStyleColor evenRowStyle) {
      this._evenRowStyle = evenRowStyle;
   }

   public void setHeaderStyle(FontStyleColor headerStyle) {
      this._headerStyle = headerStyle;
   }

   public void setOddRowStyle(FontStyleColor oddRowStyle) {
      this._oddRowStyle = oddRowStyle;
   }

   public FontStyleColor getSuperStyle() {
      return this._superStyle;
   }

   public FontStyleColor getHeaderStyle() {
      return this._headerStyle;
   }

   public FontStyleColor getEvenRowStyle() {
      return this._evenRowStyle;
   }

   public FontStyleColor getOddRowStyle() {
      return this._oddRowStyle;
   }

   public FontStyleColor getRowStyle(int rowIndex) {
      if (rowIndex == -1) {
         return this._headerStyle;
      } else {
         return rowIndex % 2 == 0 ? this._evenRowStyle : this._oddRowStyle;
      }
   }

   static {
      FROZEN_STYLE.setBackgroundColor(8900346);
      FROZEN_STYLE.setForegroundColor(0);
   }
}
