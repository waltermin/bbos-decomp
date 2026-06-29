package net.rim.device.apps.internal.browser.html;

import net.rim.vm.Array;

final class Table {
   int _availableWidth = -1;
   int _specifiedTableWidth = Integer.MIN_VALUE;
   int _currentRowAlign = -1;
   int _currentRowValign = -1;
   int _currentRowDir = -1;
   int _dir = -1;
   String _currentRowURI;
   boolean _currentRowBgRepeatX = true;
   boolean _currentRowBgRepeatY = true;
   int _currentRowBgPosX;
   int _currentRowBgPosY;
   String _backgroundURI;
   boolean _fixed;
   int _cellPadding;
   int _cellSpacing = 2;
   int _cellBorderMask;
   int _borderTopWidth;
   int _borderRightWidth;
   int _borderBottomWidth;
   int _borderLeftWidth;
   boolean _firstTd = true;
   int _currentColumn = 0;
   int _columnCount = 0;
   int[] _specifiedColumnWidths = new int[0];
   Object _parentCell;

   final void addColumn(int columnWidth) {
      this._columnCount++;
      Array.resize(this._specifiedColumnWidths, this._columnCount);
      if (this._currentColumn < this._columnCount) {
         this._specifiedColumnWidths[this._currentColumn] = columnWidth;
         this._currentColumn++;
      }
   }

   final int getNextSpecifiedWidth(int colspan) {
      int end = Math.min(this._currentColumn + colspan, this._columnCount);
      int sum = Integer.MIN_VALUE;

      for (int i = this._currentColumn; i < end; i++) {
         if (this._specifiedColumnWidths[i] != -1) {
            if (sum == Integer.MIN_VALUE) {
               sum = 0;
            }

            sum += this._specifiedColumnWidths[i];
         }
      }

      return sum;
   }

   final void finishColumns() {
      if (this._firstTd) {
         this.finishRow();
         this._firstTd = false;
      }
   }

   final void finishRow() {
      this._currentColumn = 0;
   }

   final void addCell(int colspan, int specifiedWidth) {
      if (this._fixed && this._currentColumn >= this._columnCount) {
         this._columnCount = this._currentColumn + 1;
         Array.resize(this._specifiedColumnWidths, this._columnCount);
         if (this._specifiedColumnWidths[this._currentColumn] == 0) {
            this._specifiedColumnWidths[this._currentColumn] = specifiedWidth;
         }
      }

      this._currentColumn += colspan;
   }
}
