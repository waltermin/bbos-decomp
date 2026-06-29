package net.rim.device.apps.internal.docview.gui;

final class DocViewSheetData$SheetCacheInfo {
   private int _row = -1;
   private int _column = -1;
   private DocViewSheetData$ArznCellTextHandler _cellTextHandler;

   private DocViewSheetData$SheetCacheInfo() {
   }

   final void setCellHandler(int row, int column, DocViewSheetData$ArznCellTextHandler textHandler) {
      if (textHandler == null) {
         throw new Object("Null text handler object.");
      }

      this._row = row;
      this._column = column;
      this._cellTextHandler = textHandler;
   }

   final DocViewSheetData$ArznCellTextHandler getCellHandler(int row, int column) {
      return this._row == row && this._column == column ? this._cellTextHandler : null;
   }

   DocViewSheetData$SheetCacheInfo(DocViewSheetData$1 x0) {
      this();
   }
}
