package net.rim.device.apps.internal.browser.util;

import net.rim.vm.Array;

public final class Table {
   private TableItem[][][] _cellObjects = new TableItem[0][0][];
   private int _columnCount;
   private int _currentRow = 0;
   private int _currentColumn = 0;

   public final int getColCount() {
      return this._columnCount;
   }

   public final int getCurrentCol() {
      return this._currentColumn;
   }

   public final int getRowCount() {
      return this._cellObjects.length;
   }

   public final TableItem getCell(int row, int column) {
      return this._cellObjects[row][column];
   }

   public final void finishRow() {
      if (this._currentColumn != 0) {
         this._currentRow++;

         while (true) {
            if (this._cellObjects.length <= this._currentRow) {
               if (this._currentRow > this._cellObjects.length) {
                  this._currentColumn = 0;
                  int oldSize = this._cellObjects.length;
                  Array.resize(this._cellObjects, this._currentRow);

                  for (int i = oldSize; i < this._cellObjects.length; i++) {
                     this._cellObjects[i] = new TableItem[this._columnCount];
                  }

                  return;
               } else {
                  this._currentColumn = 0;
                  break;
               }
            }

            this._currentColumn = -1;

            for (int i = 0; i < this._columnCount; i++) {
               if (this._cellObjects[this._currentRow][i] == null) {
                  this._currentColumn = i;
                  break;
               }
            }

            if (this._currentColumn != -1) {
               break;
            }

            this._currentRow++;
         }
      }
   }

   public final void addCell(TableItem newCell, int colspan, int rowspan) {
      int colStart = this._currentColumn;
      int rowStart = this._currentRow;
      int rowEnd = this._currentRow + rowspan;
      int colEnd = this._currentColumn + colspan;
      newCell.setTableItems(colStart, colEnd, rowStart, rowEnd);
      if (this._columnCount < colEnd) {
         this._columnCount = colEnd;

         for (int i = this._cellObjects.length - 1; i >= 0; i--) {
            Array.resize(this._cellObjects[i], this._columnCount);
         }
      }

      if (rowEnd > this._cellObjects.length) {
         int oldSize = this._cellObjects.length;
         Array.resize(this._cellObjects, rowEnd);

         for (int i = oldSize; i < rowEnd; i++) {
            this._cellObjects[i] = new TableItem[this._columnCount];
         }
      }

      for (int i = rowStart; i < rowEnd; i++) {
         for (int j = colStart; j < colEnd; j++) {
            this._cellObjects[i][j] = newCell;
         }
      }

      this._currentColumn += colspan;

      for (int i = this._currentColumn; i < this._columnCount; i++) {
         if (this._cellObjects[this._currentRow][i] == null) {
            this._currentColumn = i;
            return;
         }
      }
   }
}
