package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.apps.internal.docview.core.ArznRichTextInfo;
import net.rim.device.apps.internal.docview.core.ArznSheetData;
import net.rim.vm.Array;

public final class DocViewSheetData implements ArznSheetData {
   private final IntHashtable _cellValues;
   private final IntHashtable _cellFormattedValues;
   private DocViewSheetData$ArznColumnInfo[] _colLength = new DocViewSheetData$ArznColumnInfo[0];
   private StringBuffer _strBuffer = (StringBuffer)(new Object(192));
   private int _nRows;
   private int _nCols;
   private String _strSheetName;
   private boolean _sheetHasDefinedBgColor;
   private int _sheetBgColor;
   private IntIntHashtable _sheetRowColorHash;
   private IntIntHashtable _sheetColColorHash;
   private IntIntHashtable _hiddenRows;
   private IntIntHashtable _hiddenCols;
   private int _maxRowIndexWithInfo;
   private int _chunkIndexWhenMarkedComplete = -1;
   private int _crtChunkIndex = -1;
   private IntIntHashtable _chunkIndeces = (IntIntHashtable)(new Object());
   private int _lastChunkFirstRowIndexAdded = -1;
   private boolean _isCurrentCellComplete = true;
   private int _crtCellRow = -1;
   private int _crtCellColumn = -1;
   private boolean _isCrtCellValid;
   private boolean _crtParaDeletedByTrackChange;
   private int _textSize;
   private final DocViewParsingData _parsingDataCaller;
   private final boolean _notifyCaller;
   private final DocViewSheetData$SheetCacheInfo _cacheInfo = new DocViewSheetData$SheetCacheInfo(null);
   boolean _hasEmbeddedObjects;
   private static final int TEXTNOTIFY_SIZE;

   public final void setID(int chunkIndex) {
      if (chunkIndex < 0) {
         throw new Object("Chunk indeces should be positive numbers!");
      }

      this._crtChunkIndex = chunkIndex;
   }

   public final void resizeToRowCols(int rows, int cols) {
      if (rows > 0 && this._nRows > rows) {
         this._nRows = rows;
      }

      if (cols > 0 && this._nCols > cols) {
         this._nCols = cols;
      }
   }

   final DocViewTextContentHandler getCurrentCellFormatterExtern(boolean createIfNull, boolean trackChangesOn) {
      return (!createIfNull || !this._isCrtCellValid) && !this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, trackChangesOn)
         ? null
         : this.getCellFormatter(this._crtCellRow, this._crtCellColumn, trackChangesOn);
   }

   public final short getColumnWidth(int nCol) {
      return this._colLength[nCol]._nColWidth;
   }

   public final int getAccumulatedColumnStart(int nCol) {
      return this._colLength[nCol]._nAccColumnWidth;
   }

   public final void setSheetName(String name) {
      this._strSheetName = name;
   }

   public final int getNumberOfCols() {
      return this._nCols;
   }

   public final int getNumberOfRows() {
      return this._nRows;
   }

   public final synchronized void setColumnWidth(short nColumnWidth) {
      short nAccWidth = 0;
      Array.resize(this._colLength, 0);

      for (int i = 0; i < this._nCols; i++) {
         Arrays.add(this._colLength, new DocViewSheetData$ArznColumnInfo(nColumnWidth, nAccWidth));
         nAccWidth += nColumnWidth;
      }
   }

   public final synchronized void setColumnWidth(int nColumn, short nColumnWidth) {
      this._colLength[nColumn]._nColWidth = nColumnWidth;
      int nAccWidth = this._colLength[nColumn]._nAccColumnWidth + nColumnWidth;

      for (int i = nColumn + 1; i < this._nCols; i++) {
         this._colLength[i]._nAccColumnWidth = nAccWidth;
         nAccWidth += this._colLength[i]._nColWidth;
      }
   }

   public final synchronized boolean setColumnWidth(short[] columnLengthVector) {
      int iLength = columnLengthVector.length;
      if (iLength != this._nCols) {
         return false;
      }

      Array.resize(this._colLength, 0);
      int nAccWidth = 0;

      for (int i = 0; i < iLength; i++) {
         short nCrtColumnWidth = columnLengthVector[i];
         Arrays.add(this._colLength, new DocViewSheetData$ArznColumnInfo(nCrtColumnWidth, nAccWidth));
         nAccWidth += nCrtColumnWidth;
      }

      return true;
   }

   public final int getMaxRowIndexWithInfo() {
      return this._maxRowIndexWithInfo;
   }

   public final void setSpreadsheetComplete(int chunkIndex) {
      if (chunkIndex != -1) {
         this._chunkIndexWhenMarkedComplete = chunkIndex;
         this.setSpreadsheetComplete();
      }
   }

   public final void endParsing(boolean parseSuccess) {
      this._textSize = 0;
      if (this._crtChunkIndex != -1) {
         if (!parseSuccess) {
            this._lastChunkFirstRowIndexAdded = -1;
         } else {
            if (this._chunkIndexWhenMarkedComplete == -1) {
               this._lastChunkFirstRowIndexAdded = -1;
            }

            int startRowIndex = this._lastChunkFirstRowIndexAdded >= 0 ? this._lastChunkFirstRowIndexAdded - 1 : this._nRows - 1;

            while (startRowIndex > 0 && this._cellValues.get(startRowIndex) == null) {
               startRowIndex--;
            }

            this._chunkIndeces.put(this._crtChunkIndex, startRowIndex);
            boolean isComplete = this.checkSpreadsheetComplete();
            if (this._crtChunkIndex != this._chunkIndexWhenMarkedComplete || !isComplete) {
               this._maxRowIndexWithInfo = this.calculateMaxRowIndexWithInfo();
            }
         }

         this._crtChunkIndex = -1;
      }
   }

   public final boolean isSpreadsheetComplete() {
      return this._maxRowIndexWithInfo == -1;
   }

   public final StringBuffer getSheetBuffer() {
      return this._strBuffer;
   }

   public final int getSheetColumnBgColor(int column) {
      return this._sheetColColorHash != null ? this._sheetColColorHash.get(column) : -1;
   }

   public final boolean sheetColumnHasDefinedBgColor(int column) {
      return this._sheetColColorHash != null ? this._sheetColColorHash.containsKey(column) : false;
   }

   public final boolean sheetHasDefinedBgColor() {
      return this._sheetHasDefinedBgColor;
   }

   public final int getNumHiddenRows() {
      try {
         return this._hiddenRows.size();
      } finally {
         ;
      }
   }

   public final int getNumHiddenColumns() {
      try {
         return this._hiddenCols.size();
      } finally {
         ;
      }
   }

   public final boolean isRowHidden(int row) {
      try {
         return this._hiddenRows.containsKey(row);
      } finally {
         ;
      }
   }

   public final boolean isColumnHidden(int column) {
      try {
         return this._hiddenCols.containsKey(column);
      } finally {
         ;
      }
   }

   public final int getSheetBgColor() {
      return this._sheetBgColor;
   }

   public final boolean sheetRowHasDefinedBgColor(int row) {
      return this._sheetRowColorHash == null || this._maxRowIndexWithInfo != -1 && row > this._maxRowIndexWithInfo
         ? false
         : this._sheetRowColorHash.containsKey(row);
   }

   public final int getSheetRowBgColor(int row) {
      return this._sheetRowColorHash == null || this._maxRowIndexWithInfo != -1 && row > this._maxRowIndexWithInfo ? -1 : this._sheetRowColorHash.get(row);
   }

   public final DocViewCellInfo getCellValue(int nRow, int nCol) {
      return this._maxRowIndexWithInfo != -1 && nRow > this._maxRowIndexWithInfo ? null : this.getCell(nRow, nCol);
   }

   public final DocViewTextContentHandler getCellFormatterExtern(int row, int column, boolean trackChangesOn) {
      return (this._maxRowIndexWithInfo == -1 || row <= this._maxRowIndexWithInfo) && this.hasCellFormatter(row, column, trackChangesOn)
         ? this.getCellFormatter(row, column, trackChangesOn)
         : null;
   }

   @Override
   public final void afterParagraphParsing(boolean incompleteParagraph, boolean lastCommandInCell) {
      if (this._isCrtCellValid) {
         if (!lastCommandInCell && !incompleteParagraph && !this._crtParaDeletedByTrackChange) {
            IntHashtable rowData = this.getRowData(this._crtCellRow);
            DocViewCellInfo cellInfo = (DocViewCellInfo)rowData.get(this._crtCellColumn);
            int startIndex = this._strBuffer.length();
            this._strBuffer.append('\n');
            if (cellInfo == null) {
               cellInfo = new DocViewCellInfo(startIndex, 1, 0, -1, -1, 0, this._isCurrentCellComplete, (byte)0, true);
               rowData.put(this._crtCellColumn, cellInfo);
            } else if (cellInfo.hasText(true)) {
               cellInfo.addParaSeparator(startIndex);
            } else {
               cellInfo.initialize(startIndex, 1, 0, -1, -1, 0, this._isCurrentCellComplete, (byte)0, true);
            }
         }

         if (this._cellFormattedValues != null) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).afterParagraphParsing(incompleteParagraph, false, lastCommandInCell);
            if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
               this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).afterParagraphParsing(incompleteParagraph, false, lastCommandInCell);
            }
         }
      }
   }

   @Override
   public final void addParagraphInfo(byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing) {
      if (this._cellFormattedValues != null && this._isCrtCellValid) {
         this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing, false);
         if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true)
               .addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing, false);
         }
      }
   }

   @Override
   public final void beforeParagraphParsing(boolean deletedByTrackChange) {
      if (this._isCrtCellValid) {
         this._crtParaDeletedByTrackChange = deletedByTrackChange;
         if (this._cellFormattedValues != null) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).beforeParagraphParsing(deletedByTrackChange);
            if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
               this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).beforeParagraphParsing(deletedByTrackChange);
            }
         }
      }
   }

   @Override
   public final void addTrackChange(String authorName, String dateTime) {
      if (this._cellFormattedValues != null && this._isCrtCellValid) {
         DocViewTextContentHandler tc = this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false);
         DocViewTextContentHandler tcWithTrackChanges = this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true);
         tc.addTrackChange(authorName, dateTime, false);
         tcWithTrackChanges.addTrackChange(authorName, dateTime, false);
      }
   }

   @Override
   public final void markLastHyperlinkElementEndOffset() {
      if (this._cellFormattedValues != null && this._isCrtCellValid) {
         this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).markLastHyperlinkElementEndOffset(false);
         if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).markLastHyperlinkElementEndOffset(false);
         }
      }
   }

   @Override
   public final void addBookmarkInfo(int bookmarkID) {
      if (this._cellFormattedValues != null && this._isCrtCellValid) {
         this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).addBookmarkInfo(bookmarkID);
         if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).addBookmarkInfo(bookmarkID);
         }
      }
   }

   @Override
   public final void addExternalHyperlink(StringBuffer strTarget) {
      if (this._cellFormattedValues != null && this._isCrtCellValid) {
         this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).addHyperlink(strTarget, false);
         if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).addHyperlink(strTarget, false);
         }
      }
   }

   @Override
   public final void addInternalHyperlink(int bookmarkID) {
      if (this._cellFormattedValues != null && this._isCrtCellValid) {
         this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).addHyperlink(bookmarkID, false);
         if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
            this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).addHyperlink(bookmarkID, false);
         }
      }
   }

   @Override
   public final void setCellBgColor(int row, int column, int bgColor) {
      if (this.isRowValid(row) && this.isColumnValid(column)) {
         IntHashtable rowData = this.getRowData(row);
         DocViewCellInfo cellInfo = (DocViewCellInfo)rowData.get(column);
         if (cellInfo == null) {
            cellInfo = new DocViewCellInfo(AttachmentViewerFactory.convertFromParsedBgColor(bgColor), this._isCurrentCellComplete);
            rowData.put(column, cellInfo);
            return;
         }

         cellInfo.setCellBgColor(AttachmentViewerFactory.convertFromParsedBgColor(bgColor));
      }
   }

   @Override
   public final void addTextContent(StringBuffer value, ArznRichTextInfo formatInfo, boolean deletedByTrackChange) {
      if (this._isCrtCellValid) {
         int iLength = value.length();
         if (this._lastChunkFirstRowIndexAdded == -1) {
            this._lastChunkFirstRowIndexAdded = this._crtCellRow;
         }

         int guiFontSize = 0;
         if (this._parsingDataCaller != null) {
            guiFontSize = this._parsingDataCaller.getTextFontSize(formatInfo, false);
         }

         if (iLength > 0) {
            if (!deletedByTrackChange) {
               IntHashtable rowData = this.getRowData(this._crtCellRow);
               DocViewCellInfo crtCellInfo = (DocViewCellInfo)rowData.get(this._crtCellColumn);
               int guiFontStyle = AttachmentViewerFactory.convertFromParsedFontStyle(formatInfo != null ? formatInfo._regionFont : -1);
               int guiForeColor = AttachmentViewerFactory.convertFromParsedForeColor(formatInfo != null ? formatInfo._textForeColor : -1);
               int guiBgColor = AttachmentViewerFactory.convertFromParsedBgColor(formatInfo != null ? formatInfo._textBgColor : -1);
               int startIndex = this._strBuffer.length();
               synchronized (this._strBuffer) {
                  synchronized (value) {
                     for (int i = 0; i < iLength; i++) {
                        this._strBuffer.append(value.charAt(i));
                     }
                  }
               }

               if (crtCellInfo == null) {
                  rowData.put(
                     this._crtCellColumn,
                     new DocViewCellInfo(
                        startIndex,
                        iLength,
                        guiFontStyle,
                        guiForeColor,
                        guiBgColor,
                        guiFontSize,
                        this._isCurrentCellComplete,
                        AttachmentViewerFactory.getLineDirection(this._strBuffer, startIndex, iLength),
                        false
                     )
                  );
               } else if (!crtCellInfo.hasText(true)) {
                  crtCellInfo.initialize(
                     startIndex,
                     iLength,
                     guiFontStyle,
                     guiForeColor,
                     guiBgColor,
                     guiFontSize,
                     this._isCurrentCellComplete,
                     AttachmentViewerFactory.getLineDirection(this._strBuffer, startIndex, iLength),
                     false
                  );
               } else {
                  if (!crtCellInfo.hasText(false)) {
                     crtCellInfo.setTextDirection(AttachmentViewerFactory.getLineDirection(this._strBuffer, startIndex, iLength));
                  }

                  crtCellInfo.addTextRegion(startIndex, iLength, guiFontStyle, guiForeColor, guiBgColor, guiFontSize, this._isCurrentCellComplete);
               }
            }

            if (this._cellFormattedValues != null) {
               this.getCellFormatter(this._crtCellRow, this._crtCellColumn, false).addText(true, formatInfo, value, guiFontSize, deletedByTrackChange);
               if (this.hasCellFormatter(this._crtCellRow, this._crtCellColumn, true)) {
                  this.getCellFormatter(this._crtCellRow, this._crtCellColumn, true).addText(true, formatInfo, value, guiFontSize, deletedByTrackChange);
               }
            }

            if (this._notifyCaller && this._cellFormattedValues != null && this._parsingDataCaller != null) {
               this._textSize += iLength;
               if (this._textSize >= 562) {
                  this._textSize = 0;
                  this._parsingDataCaller.parsedSpreadsheetChunk();
               }
            }
         }
      }
   }

   @Override
   public final void afterCellParsing() {
      DocViewSheetData$ArznCellTextHandler textHandler = this.getCellHandler(this._crtCellRow, this._crtCellColumn);
      if (textHandler != null) {
         textHandler.endCellParsing(this._crtChunkIndex);
      }

      this._isCurrentCellComplete = true;
      this._crtCellRow = this._crtCellColumn = -1;
      this._isCrtCellValid = false;
   }

   @Override
   public final void beforeCellParsing(int row, int column, boolean isCompleteCell) {
      this._isCrtCellValid = this.isRowValid(row) && this.isColumnValid(column);
      this._isCurrentCellComplete = isCompleteCell;
      this._crtCellRow = row;
      this._crtCellColumn = column;
      DocViewSheetData$ArznCellTextHandler textHandler = this.getCellHandler(row, column);
      if (textHandler != null) {
         textHandler.setComplete(isCompleteCell, this._crtChunkIndex);
      }
   }

   @Override
   public final void setColumnHidden(int colIndex) {
      if (this.isColumnValid(colIndex)) {
         if (this._hiddenCols == null) {
            this._hiddenCols = (IntIntHashtable)(new Object(1));
         }

         this._hiddenCols.put(colIndex, 1);
      }
   }

   @Override
   public final void setRowHidden(int rowIndex) {
      if (this.isRowValid(rowIndex)) {
         if (this._hiddenRows == null) {
            this._hiddenRows = (IntIntHashtable)(new Object(1));
         }

         this._hiddenRows.put(rowIndex, 1);
      }
   }

   @Override
   public final void setTableColBgColor(int col, int color) {
      if (this.isColumnValid(col)) {
         if (this._sheetColColorHash == null) {
            this._sheetColColorHash = (IntIntHashtable)(new Object(1));
         }

         this._sheetColColorHash.put(col, AttachmentViewerFactory.convertFromParsedBgColor(color));
      }
   }

   @Override
   public final void setTableRowBgColor(int row, int color) {
      if (this.isRowValid(row)) {
         if (this._sheetRowColorHash == null) {
            this._sheetRowColorHash = (IntIntHashtable)(new Object(1));
         }

         this._sheetRowColorHash.put(row, AttachmentViewerFactory.convertFromParsedBgColor(color));
      }
   }

   @Override
   public final void setTableBgColor(int color) {
      this._sheetHasDefinedBgColor = true;
      this._sheetBgColor = AttachmentViewerFactory.convertFromParsedBgColor(color);
   }

   @Override
   public final String toString() {
      return this._strSheetName;
   }

   private final void setSpreadsheetComplete() {
      this._maxRowIndexWithInfo = -1;
   }

   private final int[] getSortedKeys() {
      int size = this._chunkIndeces.size();
      if (size <= 0) {
         return null;
      }

      int[] keys = new int[size];
      int index = 0;
      IntEnumeration e = this._chunkIndeces.keys();

      while (e.hasMoreElements()) {
         keys[index++] = e.nextElement();
      }

      Arrays.sort(keys, 0, size);
      return keys;
   }

   private final int calculateMaxRowIndexWithInfo() {
      int retValue = 0;
      int[] keys = this.getSortedKeys();
      if (keys != null) {
         retValue = this._chunkIndeces.get(keys[0]);
         int i = 1;

         for (i = 1; i < keys.length && keys[i] == keys[i - 1] + 1; i++) {
            retValue = this._chunkIndeces.get(keys[i]);
         }

         if (i == keys.length && Arrays.binarySearch(keys, this._chunkIndexWhenMarkedComplete) >= 0) {
            retValue = -1;
         }

         int[] var4 = null;
      }

      return retValue;
   }

   private final DocViewCellInfo getCell(int nRow, int nCol) {
      return (DocViewCellInfo)((IntHashtable)this._cellValues.get(nRow)).get(nCol);
   }

   private final IntHashtable getRowData(int row) {
      IntHashtable rowData = (IntHashtable)this._cellValues.get(row);
      if (rowData == null) {
         if (this._nCols > 0) {
            rowData = (IntHashtable)(new Object(this._nCols >> 1));
         } else {
            rowData = (IntHashtable)(new Object());
         }

         this._cellValues.put(row, rowData);
      }

      return rowData;
   }

   private final DocViewSheetData$ArznCellTextHandler getCellHandler(int row, int column) {
      if (this._cellFormattedValues != null) {
         synchronized (this._cellFormattedValues) {
            DocViewSheetData$ArznCellTextHandler textHandler = this._cacheInfo.getCellHandler(row, column);
            if (textHandler == null) {
               IntHashtable rowData = (IntHashtable)this._cellFormattedValues.get(row);
               if (rowData != null) {
                  textHandler = (DocViewSheetData$ArznCellTextHandler)rowData.get(column);
                  if (textHandler != null) {
                     this._cacheInfo.setCellHandler(row, column, textHandler);
                     return textHandler;
                  }
               }

               return null;
            } else {
               return textHandler;
            }
         }
      } else {
         return null;
      }
   }

   private final DocViewTextContentHandler getCellFormatter(int row, int column, boolean trackChangesOn) {
      if (this._cellFormattedValues == null) {
         return null;
      }

      synchronized (this._cellFormattedValues) {
         DocViewSheetData$ArznCellTextHandler formatter = this._cacheInfo.getCellHandler(row, column);
         if (formatter == null) {
            IntHashtable rowData = (IntHashtable)this._cellFormattedValues.get(row);
            if (rowData == null) {
               if (this._nCols > 0) {
                  rowData = (IntHashtable)(new Object(this._nCols >> 1));
               } else {
                  rowData = (IntHashtable)(new Object());
               }

               this._cellFormattedValues.put(row, rowData);
            } else {
               formatter = (DocViewSheetData$ArznCellTextHandler)rowData.get(column);
            }

            if (formatter == null) {
               formatter = new DocViewSheetData$ArznCellTextHandler(this._isCurrentCellComplete, this._crtChunkIndex);
               rowData.put(column, formatter);
            }

            this._cacheInfo.setCellHandler(row, column, formatter);
         }

         DocViewTextContentHandler tcHandler = trackChangesOn ? formatter._trackChangesHandler : formatter._tcHandler;
         if (tcHandler == null) {
            if (trackChangesOn) {
               if (formatter._tcHandler != null) {
                  formatter._trackChangesHandler = new DocViewTextContentHandler(formatter._tcHandler, true);
               } else {
                  formatter._trackChangesHandler = new DocViewTextContentHandler(48, true);
               }

               tcHandler = formatter._trackChangesHandler;
            } else {
               formatter._tcHandler = new DocViewTextContentHandler(48, false);
               tcHandler = formatter._tcHandler;
            }
         }

         return tcHandler;
      }
   }

   private final boolean hasCellFormatter(int row, int column, boolean trackChangesOn) {
      if (this._cellFormattedValues != null) {
         synchronized (this._cellFormattedValues) {
            DocViewSheetData$ArznCellTextHandler textHandler = this._cacheInfo.getCellHandler(row, column);
            if (textHandler == null) {
               IntHashtable rowData = (IntHashtable)this._cellFormattedValues.get(row);
               if (rowData != null) {
                  textHandler = (DocViewSheetData$ArznCellTextHandler)rowData.get(column);
                  if (textHandler != null) {
                     this._cacheInfo.setCellHandler(row, column, textHandler);
                  }
               }
            }

            if (textHandler != null) {
               return trackChangesOn ? textHandler._trackChangesHandler != null : textHandler._tcHandler != null;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private final boolean checkSpreadsheetComplete() {
      int size = this._chunkIndeces.size();
      if (size > 1) {
         int[] keys = this.getSortedKeys();
         if (keys != null) {
            for (int i = 0; i < size - 1; i++) {
               if (keys[i] + 1 != keys[i + 1]) {
                  keys = null;
                  return false;
               }
            }

            keys = null;
            this.setSpreadsheetComplete();
            return true;
         }
      }

      return false;
   }

   private final boolean isRowValid(int row) {
      return row >= 0 && row < this._nRows;
   }

   private final boolean isColumnValid(int column) {
      return column >= 0 && column < this._nCols;
   }

   public DocViewSheetData(
      int nRows, int nColumns, String strSheetDataName, boolean createTextContentHandler, DocViewParsingData parsingCaller, boolean notifyCaller
   ) {
      this._nRows = nRows;
      this._nCols = nColumns;
      this._strSheetName = strSheetDataName;
      this._parsingDataCaller = parsingCaller;
      this._notifyCaller = notifyCaller;
      if (nRows > 0) {
         this._cellValues = (IntHashtable)(new Object(nRows >> 1));
      } else {
         this._cellValues = (IntHashtable)(new Object());
      }

      if (createTextContentHandler) {
         if (nRows > 0) {
            this._cellFormattedValues = (IntHashtable)(new Object(nRows >> 1));
         } else {
            this._cellFormattedValues = (IntHashtable)(new Object());
         }
      } else {
         this._cellFormattedValues = null;
      }
   }
}
