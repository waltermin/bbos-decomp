package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.vm.Array;

public class ColumnData extends TableStyle {
   private String[] _cells;
   private String _header;
   private int _width;
   private int _numOfRows;
   private double _widthPercentage = (double)0L;
   private boolean _isFrozen;
   private boolean _isAutoSized = true;
   private boolean _isHidden;
   private static final int _ROWS_TO_AUTOSIZE;

   public ColumnData(String[] cells, String header) {
      this._cells = cells == null ? new Object[0] : cells;
      this._header = header;
      this._numOfRows = this._cells.length;
   }

   public void setFrozen(boolean isFrozen) {
      this._isFrozen = isFrozen;
   }

   public boolean isFrozen() {
      return this._isFrozen;
   }

   public void setHidden(boolean isHidden) {
      this._isHidden = isHidden;
   }

   public boolean isHidden() {
      return this._isHidden;
   }

   public int getAutoSizedWidth(Font font) {
      int maxWidth = this._header == null ? 0 : font.getBounds(this._header);
      int screenWidth = Graphics.getScreenWidth();

      for (int i = this._numOfRows > 20 ? 19 : this._numOfRows - 1; i >= 0; i--) {
         int width = font.getBounds(this._cells[i]);
         if (width > maxWidth) {
            maxWidth = width;
            if (maxWidth >= screenWidth) {
               return screenWidth;
            }
         }
      }

      if (this._numOfRows > 20) {
         int width = font.getBounds(this._cells[this._numOfRows - 1]);
         if (width > maxWidth) {
            maxWidth = width;
         }
      }

      return maxWidth == 0 ? 0 : maxWidth + 2;
   }

   public boolean isSelectable() {
      return !this._isHidden && this._width > 0;
   }

   public int getWidth() {
      return this._width;
   }

   public int getNumOfRows() {
      return this._numOfRows;
   }

   public String getHeader() {
      return this._header;
   }

   public void setHeader(String text) {
      this._header = text;
   }

   public String getCell(int rowIndex) {
      return rowIndex == -1 ? this._header : this._cells[rowIndex];
   }

   public void setCell(int rowIndex, String text) {
      this._cells[rowIndex] = text;
   }

   public boolean isAutoSized() {
      return this._isAutoSized;
   }

   public double getWidthPercentage() {
      return this._widthPercentage;
   }

   public void setWidthPercentage(double widthPercentage) {
      this._isAutoSized = false;
      if (widthPercentage < 0L) {
         this._widthPercentage = (double)0L;
      } else if (widthPercentage > 4607182418800017408L) {
         this._widthPercentage = (double)4607182418800017408L;
      } else {
         this._widthPercentage = widthPercentage;
      }
   }

   void setWidth(int width) {
      this._width = width;
   }

   void setNumOfRows(int numOfRows) {
      if (numOfRows > this._cells.length) {
         Array.resize(this._cells, numOfRows);
      }

      this._numOfRows = numOfRows;
   }
}
