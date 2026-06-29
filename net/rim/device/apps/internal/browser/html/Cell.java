package net.rim.device.apps.internal.browser.html;

import net.rim.device.apps.internal.browser.util.TableItem;

final class Cell implements TableItem {
   int _specifiedWidth;
   int _calculatedWidth;
   Object _cellData;
   Table[] _subTables;
   int _colStart;
   int _rowStart;
   int _colEnd;
   int _rowEnd;

   Cell(Object data, int specifiedWidth, int calculatedWidth) {
      this._cellData = data;
      this._specifiedWidth = specifiedWidth;
      this._calculatedWidth = calculatedWidth;
   }

   @Override
   public final void setTableItems(int colStart, int colEnd, int rowStart, int rowEnd) {
      this._rowStart = rowStart;
      this._rowEnd = rowEnd;
      this._colStart = colStart;
      this._colEnd = colEnd;
   }
}
