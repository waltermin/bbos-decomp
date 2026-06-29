package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.ui.Font;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntVector;

public class TableData extends TableStyle {
   private ColumnData[] _columns;
   private Font _font;
   private int _availableWidth;
   private int _numOfRows;
   private int _numOfCols;
   private int _width;
   private boolean _isHeaderVisible = true;
   private boolean _isRowSelectorVisible = true;
   private boolean _isGridVisible = true;
   private double _remainder;
   private int _partVisibleColIndex = -1;
   private int _partVisibleColWidth;
   private IntVector _isGridIndicatingNotVisibleCols;
   private IntVector _frozenSelectableColumns;
   private IntVector _visibleColumns;
   private IntVector _columnIndexes;

   public TableData(ColumnData[] columns) {
      this._columns = columns == null ? new ColumnData[0] : columns;
      this._numOfCols = this._columns.length;
      this._numOfRows = this._numOfCols > 0 ? columns[0].getNumOfRows() : 0;
      this._frozenSelectableColumns = new IntVector();
      this._visibleColumns = new IntVector(this._numOfCols);
      this._columnIndexes = new IntVector(this._numOfCols);
      this._isGridIndicatingNotVisibleCols = new IntVector(this._numOfCols + 1);
   }

   public ColumnData getColumn(int colIndex) {
      return this._columns[colIndex];
   }

   public void setAvailableWidth(int width) {
      this._availableWidth = width;
   }

   public int getNumOfCols() {
      return this._numOfCols;
   }

   public int getNumOfRows() {
      return this._numOfRows;
   }

   public int getRowHeight() {
      return this._font == null ? 0 : this._font.getHeight();
   }

   public Font getFont() {
      return this._font;
   }

   public void setFont(Font font) {
      this._font = font;
   }

   public void layout() {
      this._width = 0;
      this._remainder = (double)0L;
      int maxTruncatedWidth = this._availableWidth - 1;
      int firstMaxTruncatedWidth = maxTruncatedWidth;
      this._columnIndexes.removeAllElements();
      this._frozenSelectableColumns.removeAllElements();

      for (int i = 0; i < this._numOfCols; i++) {
         if (this._columns[i].isFrozen()) {
            this._frozenSelectableColumns.addElement(i);
         } else {
            this._columnIndexes.addElement(i);
         }
      }

      int size = this._frozenSelectableColumns.size();

      for (int var7 = 0; var7 < size; var7++) {
         int colWidth = this.setColumnWidth(this._columns[this._frozenSelectableColumns.elementAt(var7)], maxTruncatedWidth, firstMaxTruncatedWidth);
         this._width += colWidth;
         maxTruncatedWidth -= colWidth;
      }

      size = this._columnIndexes.size();

      for (int var8 = 0; var8 < size; var8++) {
         this._width = this._width + this.setColumnWidth(this._columns[this._columnIndexes.elementAt(var8)], maxTruncatedWidth, firstMaxTruncatedWidth);
      }

      if (this._width > 0) {
         this._width++;
      }

      if (this._width <= this._availableWidth) {
         this._visibleColumns.removeAllElements();

         for (int var9 = 0; var9 < this._numOfCols; var9++) {
            if (this._columns[var9].isSelectable()) {
               this._visibleColumns.addElement(var9);
            }
         }
      } else {
         this._frozenSelectableColumns.removeAllElements();

         for (int var10 = 0; var10 < this._numOfCols; var10++) {
            ColumnData column = this._columns[var10];
            if (column.isSelectable() && column.isFrozen()) {
               this._frozenSelectableColumns.addElement(var10);
            }
         }
      }
   }

   public IntVector getVisibleColumns(int newSelectedCol, boolean isRightScroll) {
      this._partVisibleColIndex = -1;
      this._partVisibleColWidth = 0;
      int usedWidth = 1;
      if (newSelectedCol == -1 || !this._columns[newSelectedCol].isSelectable() || usedWidth >= this._availableWidth) {
         this._visibleColumns.removeAllElements();
         return this._visibleColumns;
      }

      if (this._width <= this._availableWidth) {
         this.resetGridIndicatingNotVisibleCols(this._visibleColumns.size());
         return this._visibleColumns;
      }

      this._visibleColumns.setSize(this._frozenSelectableColumns.size());
      this._frozenSelectableColumns.copyInto(this._visibleColumns.getArray());
      int selectedColIndex = Arrays.binarySearch(this._visibleColumns.getArray(), newSelectedCol, 0, this._visibleColumns.size());
      if (selectedColIndex < 0) {
         selectedColIndex = -selectedColIndex - 1;
         this._visibleColumns.insertElementAt(newSelectedCol, selectedColIndex);
      }

      usedWidth += this.getWidthOfColumns(this._visibleColumns, 0, this._visibleColumns.size());
      this._columnIndexes.removeAllElements();
      if (isRightScroll) {
         usedWidth = this.insertLeftColumns(this._visibleColumns, selectedColIndex, this._columnIndexes, usedWidth);
         this.insertRightColumns(this._visibleColumns, selectedColIndex, this._columnIndexes, usedWidth);
      } else {
         usedWidth = this.insertRightColumns(this._visibleColumns, selectedColIndex, this._columnIndexes, usedWidth);
         this.insertLeftColumns(this._visibleColumns, selectedColIndex, this._columnIndexes, usedWidth);
      }

      for (int i = this._columnIndexes.size() - 1; i >= 0; i--) {
         int columnIndexToInsert = this._columnIndexes.elementAt(i);
         this._visibleColumns
            .insertElementAt(
               columnIndexToInsert, -Arrays.binarySearch(this._visibleColumns.getArray(), columnIndexToInsert, 0, this._visibleColumns.size()) - 1
            );
      }

      this.calcGridIndicatingNotVisibleCols(this._visibleColumns, this._frozenSelectableColumns);
      return this._visibleColumns;
   }

   public boolean isGridIndicatingNotVisbleCols(int index) {
      return this._isGridIndicatingNotVisibleCols.elementAt(index) == 1;
   }

   public int getPartVisibleColIndex() {
      return this._partVisibleColIndex;
   }

   public int getPartVisibleColWidth() {
      return this._partVisibleColWidth;
   }

   public int findNextSelectableColumn(int colIndex, boolean searchToTheRightFirst) {
      int columnsLength = this._columns.length;
      if (searchToTheRightFirst) {
         for (int i = colIndex + 1; i < columnsLength; i++) {
            if (this._columns[i].isSelectable()) {
               return i;
            }
         }

         if (colIndex != -1) {
            if (this._columns[colIndex].isSelectable()) {
               return colIndex;
            }

            for (int var5 = colIndex; var5 >= 0; var5--) {
               if (this._columns[var5].isSelectable()) {
                  return var5;
               }
            }
         }
      } else {
         for (int i = colIndex - 1; i >= 0; i--) {
            if (this._columns[i].isSelectable()) {
               return i;
            }
         }

         if (this._isRowSelectorVisible) {
            return -1;
         }

         if (colIndex != -1 && this._columns[colIndex].isSelectable()) {
            return colIndex;
         }

         for (int var7 = colIndex + 1; var7 < columnsLength; var7++) {
            if (this._columns[var7].isSelectable()) {
               return var7;
            }
         }
      }

      return -1;
   }

   public boolean isHeaderVisible() {
      return this._isHeaderVisible;
   }

   public void setHeaderVisible(boolean headerVisible) {
      this._isHeaderVisible = headerVisible;
   }

   public boolean isGridVisible() {
      return this._isGridVisible;
   }

   public void setGridVisible(boolean isGridVisible) {
      this._isGridVisible = isGridVisible;
   }

   public boolean isRowSelectorVisible() {
      return this._isRowSelectorVisible;
   }

   public void setRowSelectorVisible(boolean rowSelectorVisible) {
      this._isRowSelectorVisible = rowSelectorVisible;
   }

   public int getWidth() {
      return this._width;
   }

   public int getWidthOfColumns(IntVector columnIndexes, int offset, int length) {
      int width = 0;

      for (int i = offset + length - 1; i >= offset; i--) {
         width += columnIndexes.elementAt(i) == this._partVisibleColIndex ? this._partVisibleColWidth : this._columns[columnIndexes.elementAt(i)].getWidth();
      }

      return width;
   }

   public void setNumOfRows(int numOfRows) {
      if (numOfRows < 0) {
         numOfRows = 0;
      }

      for (int i = this._numOfCols - 1; i >= 0; i--) {
         this._columns[i].setNumOfRows(numOfRows);
      }

      this._numOfRows = this._numOfCols > 0 ? numOfRows : 0;
   }

   private int insertRightColumns(IntVector frozenWithSelectedColumns, int selectedColIndex, IntVector columnsToInsert, int usedWidth) {
      int tempColIndex = 0;
      int lastIndex = frozenWithSelectedColumns.size() - 1;

      for (int i = selectedColIndex; usedWidth < this._availableWidth && i < lastIndex; i++) {
         tempColIndex = frozenWithSelectedColumns.elementAt(i + 1);

         for (int j = frozenWithSelectedColumns.elementAt(i) + 1; usedWidth < this._availableWidth && j < tempColIndex; j++) {
            usedWidth = this.insertColumn(columnsToInsert, usedWidth, j);
         }
      }

      if (usedWidth < this._availableWidth) {
         for (int j = frozenWithSelectedColumns.elementAt(lastIndex) + 1; usedWidth < this._availableWidth && j < this._numOfCols; j++) {
            usedWidth = this.insertColumn(columnsToInsert, usedWidth, j);
         }
      }

      return usedWidth;
   }

   private int insertLeftColumns(IntVector frozenWithSelectedColumns, int selectedColIndex, IntVector columnsToInsert, int usedWidth) {
      int tempColIndex = 0;

      for (int i = selectedColIndex; usedWidth < this._availableWidth && i > 0; i--) {
         tempColIndex = frozenWithSelectedColumns.elementAt(i - 1);

         for (int j = frozenWithSelectedColumns.elementAt(i) - 1; usedWidth < this._availableWidth && j > tempColIndex; j--) {
            usedWidth = this.insertColumn(columnsToInsert, usedWidth, j);
         }
      }

      for (int j = frozenWithSelectedColumns.elementAt(0) - 1; usedWidth < this._availableWidth && j >= 0; j--) {
         usedWidth = this.insertColumn(columnsToInsert, usedWidth, j);
      }

      return usedWidth;
   }

   private int insertColumn(IntVector columnsToInsert, int usedWidth, int colIndex) {
      ColumnData colData = this._columns[colIndex];
      if (!colData.isHidden()) {
         int colWidth = colData.getWidth();
         columnsToInsert.addElement(colIndex);
         usedWidth += colWidth;
         if (usedWidth >= this._availableWidth) {
            if (usedWidth > this._availableWidth) {
               this._partVisibleColIndex = colIndex;
               this._partVisibleColWidth = colWidth + this._availableWidth - usedWidth;
               return usedWidth;
            }
         } else if (usedWidth + 1 == this._availableWidth) {
            usedWidth++;
         }
      }

      return usedWidth;
   }

   private int setColumnWidth(ColumnData col, int maxTruncatedWidth, int availableWidth) {
      if (col.isHidden()) {
         return 0;
      }

      int colWidth = 0;
      if (maxTruncatedWidth > 0) {
         if (col.isAutoSized()) {
            colWidth = col.getAutoSizedWidth(this._font);
         } else {
            double width = col.getWidthPercentage() * availableWidth;
            colWidth = (int)width;
            this._remainder += width % 4607182418800017408L;
            if (this._remainder > 4607182418800017408L) {
               this._remainder %= 4607182418800017408L;
               colWidth++;
            }
         }

         if (colWidth > 0) {
            if (colWidth > maxTruncatedWidth) {
               colWidth = maxTruncatedWidth;
            } else if (colWidth + 1 == maxTruncatedWidth) {
               colWidth++;
            }
         }
      }

      col.setWidth(colWidth);
      return colWidth;
   }

   private void calcGridIndicatingNotVisibleCols(IntVector visibleCols, IntVector frozenCols) {
      int size = visibleCols.size();
      this.resetGridIndicatingNotVisibleCols(size);

      for (int i = frozenCols.size() - 1; i >= 0; i--) {
         int frozenColIndex = frozenCols.elementAt(i);
         int index = Arrays.binarySearch(visibleCols.getArray(), frozenColIndex, 0, size);
         if (index - 1 >= 0) {
            int adjColIndex = visibleCols.elementAt(index - 1);
            if (adjColIndex + 1 < frozenColIndex && adjColIndex < this.getPrevSelectableCol(frozenColIndex)) {
               this._isGridIndicatingNotVisibleCols.setElementAt(1, index);
            }
         }

         if (index + 1 < size) {
            int adjColIndex = visibleCols.elementAt(index + 1);
            if (adjColIndex - 1 > frozenColIndex && adjColIndex > this.getNextSelectableCol(frozenColIndex)) {
               this._isGridIndicatingNotVisibleCols.setElementAt(1, index + 1);
            }
         }
      }
   }

   private void resetGridIndicatingNotVisibleCols(int size) {
      this._isGridIndicatingNotVisibleCols.setSize(size + 1);

      for (int i = size; i >= 0; i--) {
         this._isGridIndicatingNotVisibleCols.setElementAt(0, i);
      }
   }

   private int getNextSelectableCol(int colIndex) {
      int columnsLength = this._columns.length;

      for (int i = colIndex + 1; i < columnsLength; i++) {
         if (this._columns[i].isSelectable()) {
            return i;
         }
      }

      return colIndex;
   }

   private int getPrevSelectableCol(int colIndex) {
      for (int i = colIndex - 1; i >= 0; i--) {
         if (this._columns[i].isSelectable()) {
            return i;
         }
      }

      return colIndex;
   }
}
