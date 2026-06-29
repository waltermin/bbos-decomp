package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.StringUtilities;

public class CustomListField extends ListField implements ListFieldCallback {
   protected XYRect _focusRectangle = new XYRect(0, 0, 0, 0);
   protected byte _nBuffer = 3;
   protected int _startVisibleCol;
   protected int _endVisibleCol = -1;
   protected final DocViewSheetData _sheetData;
   protected short _nStyle;
   private byte _gridLook;
   private MultiElementRow _tableRow;
   private ColumnInfo[] _colVector;
   protected int _nTotalRows;
   protected int _nTotalCols;
   private int _nSelectedRow;
   private int _nSelectedCol;
   protected int[] _columnFitVector;
   protected int _hiddenRowsCount;
   private int _hiddenColsCount;
   private StringBuffer _stringBuffer = new StringBuffer();
   protected boolean _showHiddenRowsCols = true;
   private FontFactory _fontFactory;
   private int _maxVisibleRowIndex = -1;
   private int _maxVisibleColumnIndex = -1;
   private int _minVisibleRowIndex = -1;
   private int _minVisibleColumnIndex = -1;
   private boolean _inHighlightRegion;
   protected boolean _useOriginalFont;
   protected int _fontStyle;
   public static final short STYLE_SCROLLOVERHORZ = 1;
   public static final short STYLE_SCROLLOVERVERT = 16;
   public static final short STYLE_DISPLAYHEADER = 256;
   public static final short STYLE_OUTLINECELLS = 4096;
   private static final short SIZE_HEADERWIDTH = 32;
   public static final byte LOOK_3D = 1;
   public static final byte LOOK_BLUE = 2;

   public void initFocus() {
      this._startVisibleCol = 0;
      this._endVisibleCol = -1;
      this.calculateCurrentFocusRect();
   }

   protected void resetColumnFitVector() {
      int nCols = this._sheetData.getNumberOfCols();

      for (short i = 0; i < nCols; i++) {
         this._columnFitVector[i] = -1;
      }
   }

   protected int getColumnFitWidth(int columnIndex) {
      if (this._columnFitVector[columnIndex] != -1) {
         return this._columnFitVector[columnIndex];
      }

      int fitWidth = 0;
      int nRows = this._sheetData.getNumberOfRows();

      for (int i = 0; i < nRows; i++) {
         if (this._showHiddenRowsCols || !this._sheetData.isRowHidden(i)) {
            try {
               DocViewCellInfo cellInfo = this._sheetData.getCellValue(i, columnIndex);
               int[] regionsOffset = cellInfo.getRegionsOffsetIndeces();
               int[] regionsFontType = cellInfo.getRegionsFontType();
               int regionsOffsetLength = regionsOffset.length;
               int columnFitWidth = 0;
               int currentFontSize = this._tableRow.getCurrentFontSize();
               if (regionsOffsetLength == regionsFontType.length * 2) {
                  if (cellInfo._textDirection == 2) {
                     int totalSize = 0;

                     for (int j = 0;
                        j < regionsOffsetLength && (regionsOffset[j + 1] != 1 || this._sheetData.getSheetBuffer().charAt(regionsOffset[j]) != '\n');
                        j += 2
                     ) {
                        totalSize += regionsOffset[j + 1];
                     }

                     if (totalSize > 0) {
                        columnFitWidth = this._fontFactory
                           .getFont(this._useOriginalFont ? 0 : this._fontStyle, currentFontSize)
                           .getBounds(this._sheetData.getSheetBuffer(), regionsOffset[0], totalSize);
                     }
                  } else {
                     for (int j = 0;
                        j < regionsOffsetLength - 1 && (regionsOffset[j + 1] != 1 || this._sheetData.getSheetBuffer().charAt(regionsOffset[j]) != '\n');
                        j += 2
                     ) {
                        Font crtFont = this._fontFactory
                           .getFont(
                              !this._useOriginalFont && this._fontStyle != 0 ? this._fontStyle | regionsFontType[j >> 1] & 44 : regionsFontType[j >> 1],
                              currentFontSize
                           );
                        columnFitWidth += crtFont.getBounds(this._sheetData.getSheetBuffer(), regionsOffset[j], regionsOffset[j + 1]);
                     }
                  }
               }

               fitWidth = Math.max(fitWidth, columnFitWidth);
            } finally {
               continue;
            }
         }
      }

      this._columnFitVector[columnIndex] = fitWidth;
      return fitWidth;
   }

   public String getSelectedCellValue() {
      try {
         if ((this._nStyle & 256) != 0) {
            return this._nSelectedRow != 0 && this._nSelectedCol != 0 ? this.getStringCellValue(this.getSelectedRow() - 1, this.getSelectedColumn() - 1) : "";
         } else {
            return this.getStringCellValue(this.getSelectedRow(), this.getSelectedColumn());
         }
      } finally {
         ;
      }
   }

   protected final String getStringCellValue(int row, int column) {
      DocViewCellInfo cellInfo = this._sheetData.getCellValue(row, column);
      synchronized (this._stringBuffer) {
         StringBuffer sheetBuffer = this._sheetData.getSheetBuffer();
         int[] regionsOffset = cellInfo.getRegionsOffsetIndeces();
         int regionOffsetLength = regionsOffset.length;

         for (int i = 0; i < regionOffsetLength - 1; i += 2) {
            StringUtilities.append(this._stringBuffer, sheetBuffer, regionsOffset[i], regionsOffset[i + 1]);
         }

         String strReturn = this._stringBuffer.toString();
         this._stringBuffer.setLength(0);
         return strReturn;
      }
   }

   public short getTableStyle() {
      return this._nStyle;
   }

   public void customSetFont(Font font, boolean useOriginalFont, int fontStyle) {
      super.setFont(font);
      this._useOriginalFont = useOriginalFont;
      this._fontStyle = fontStyle;
      this.setRowHeight(font.getHeight() + 2);
      this._tableRow.setFont(font);
   }

   protected boolean toggleHiddenStructure() {
      if (this.sheetHasAnyHiddenRowCol()) {
         this._showHiddenRowsCols = !this._showHiddenRowsCols;
         if (this._hiddenRowsCount > 0) {
            if (this._showHiddenRowsCols) {
               this._nTotalRows = this._nTotalRows + this._hiddenRowsCount;
               this.setSize(this._nTotalRows);
            } else {
               this._nTotalRows = this._nTotalRows - this._hiddenRowsCount;
               this.setSize(this._nTotalRows);
            }
         }

         if (this._hiddenColsCount > 0) {
            if (this._showHiddenRowsCols) {
               this._nTotalCols = this._nTotalCols + this._hiddenColsCount;
               return true;
            }

            this._nTotalCols = this._nTotalCols - this._hiddenColsCount;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean sheetHasAnyHiddenRowCol() {
      return this._hiddenRowsCount > 0 || this._hiddenColsCount > 0;
   }

   public void setTableStyle(short nNewStyle) {
      boolean bAddRow = (this._nStyle & 256) == 0 && (nNewStyle & 256) != 0;
      boolean bRemoveRow = (this._nStyle & 256) != 0 && (nNewStyle & 256) == 0;
      this._nStyle = nNewStyle;
      if (bAddRow) {
         this.insert(this.getSize());
         this._nTotalRows++;
         this._nTotalCols++;
         if (this.getManager() != null) {
            this.setFocusedCell(true);
            return;
         }
      } else if (bRemoveRow) {
         this.delete(this.getSize() - 1);
         this._nTotalRows--;
         this._nTotalCols--;
         if (this.getManager() != null) {
            this.setFocusedCell(true);
         }
      }
   }

   public void setFocusedCell(boolean bTop) {
      try {
         boolean bDisplayHeader = (this._nStyle & 256) != 0;
         if (bTop) {
            if (bDisplayHeader) {
               this.setFocusedCell(-1, -1);
            } else if (!this._showHiddenRowsCols) {
               this.setFocusedCell(this._minVisibleRowIndex, this._minVisibleColumnIndex);
            } else {
               this.setFocusedCell(0, 0);
            }
         } else {
            if (this._showHiddenRowsCols) {
               this.setFocusedCell(this._sheetData.getNumberOfRows() - 1, this._sheetData.getNumberOfCols() - 1);
               return;
            }

            this.setFocusedCell(this._maxVisibleRowIndex, this._maxVisibleColumnIndex);
         }
      } finally {
         return;
      }
   }

   public void setFocusedCell(int row, int col) throws Exception {
      if (row >= -1 && row < this._sheetData.getNumberOfRows() && col >= -1 && col < this._sheetData.getNumberOfCols()) {
         if (this._showHiddenRowsCols || !this._sheetData.isRowHidden(row) && !this._sheetData.isColumnHidden(col)) {
            int nActualRow = this.transformToRowIndex(row, false);
            int nActualCol = this.transformToColumnIndex(col, false);
            if ((this._nStyle & 256) != 0) {
               nActualRow++;
               nActualCol++;
            }

            int nOldSelectedRow = this._nSelectedRow;
            this._nSelectedRow = nActualRow;
            this._nSelectedCol = nActualCol;
            this.calculateCurrentFocusRect();
            if (nOldSelectedRow != this._nSelectedRow) {
               super.moveFocus(this._nSelectedRow - nOldSelectedRow, 0, 0);
            }

            this.focusAdd(false);
            this.invalidate();
         } else {
            throw new Exception();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected void focusCellChanged() {
   }

   public int getSelectedColumn() {
      if (!this._showHiddenRowsCols) {
         boolean showHeaders = (this._nStyle & 256) != 0;
         int var10000 = this.transformToColumnIndex(this._nSelectedCol - (showHeaders ? 1 : 0), true);
         return showHeaders ? var10000 + 1 : var10000 + 0;
      } else {
         return this._nSelectedCol;
      }
   }

   protected boolean isLastRetrievedRow(int realRowIndex) {
      return false;
   }

   protected String getColName(int nCol) {
      synchronized (this._stringBuffer) {
         if (nCol >= 0) {
            int iRange = 26;
            int i = 0;

            int iValue;
            for (iValue = nCol; iValue >= iRange; iValue -= iRange) {
               i++;
            }

            int iFullRanges = i / iRange;
            int iPartialRanges = i % iRange;
            int iFullLetterRanges = iFullRanges / iRange;
            int iPartialLetterRanges = iFullRanges % iRange;

            for (int var13 = 0; var13 < iFullLetterRanges; var13++) {
               this._stringBuffer.append('Z');
            }

            if (iPartialLetterRanges > 0) {
               this._stringBuffer.append((char)(65 + iPartialLetterRanges - 1));
            }

            if (iPartialRanges > 0) {
               this._stringBuffer.append((char)(65 + iPartialRanges - 1));
            }

            this._stringBuffer.append((char)(65 + iValue));
         }

         String result = this._stringBuffer.toString();
         this._stringBuffer.setLength(0);
         return result;
      }
   }

   protected String getRowName(int nRow) {
      return String.valueOf(nRow);
   }

   public int getSelectedRow() {
      if (!this._showHiddenRowsCols) {
         boolean showHeaders = (this._nStyle & 256) != 0;
         int var10000 = this.transformToRowIndex(this._nSelectedRow - (showHeaders ? 1 : 0), true);
         return showHeaders ? var10000 + 1 : var10000 + 0;
      } else {
         return this._nSelectedRow;
      }
   }

   public boolean isShowHiddenRowCol() {
      return this._showHiddenRowsCols;
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return DocViewGUIInternalConstants.SCREEN_WIDTH;
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._colVector[index]._columnInfo;
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index < this._nTotalRows) {
         if (this._sheetData.getNumberOfRows() != 0 && this._sheetData.getNumberOfCols() != 0) {
            boolean bAddRowColInfo = (this._nStyle & 256) != 0;
            boolean isColorDisplay = Graphics.isColor();
            int realRowIndex = this.transformToRowIndex(bAddRowColInfo ? index - 1 : index, true);
            int iIndex = 0;
            int columnStart = this._startVisibleCol;
            boolean sheetHasBgColor = false;
            int sheetBgColor = 0;
            boolean rowHasBgColor = false;
            int rowBgColor = 0;
            if (isColorDisplay) {
               sheetHasBgColor = this._sheetData.sheetHasDefinedBgColor();
               if (sheetHasBgColor) {
                  sheetBgColor = this._sheetData.getSheetBgColor();
               }

               if (bAddRowColInfo) {
                  if (index > 0) {
                     rowHasBgColor = this._sheetData.sheetRowHasDefinedBgColor(realRowIndex);
                     if (rowHasBgColor) {
                        rowBgColor = this._sheetData.getSheetRowBgColor(realRowIndex);
                     }
                  }
               } else {
                  rowHasBgColor = this._sheetData.sheetRowHasDefinedBgColor(realRowIndex);
                  if (rowHasBgColor) {
                     rowBgColor = this._sheetData.getSheetRowBgColor(realRowIndex);
                  }
               }
            }

            if (!bAddRowColInfo) {
               for (int iColIndex = columnStart; iColIndex < this._endVisibleCol; iColIndex++) {
                  if (this._showHiddenRowsCols || !this._sheetData.isColumnHidden(iColIndex)) {
                     ColumnInfo crtColInfo = this._colVector[iIndex];
                     crtColInfo._hasBgColor = false;
                     crtColInfo._embObjectType = -1;
                     if (isColorDisplay) {
                        this.setColumnInfo(crtColInfo, realRowIndex, iColIndex, rowHasBgColor, rowBgColor, sheetHasBgColor, sheetBgColor);
                     } else {
                        this.setColumnInfo(crtColInfo, realRowIndex, iColIndex);
                     }

                     iIndex++;
                  }
               }
            } else {
               if (columnStart == 0) {
                  ColumnInfo crtColInfo = this._colVector[0];
                  crtColInfo._hasBgColor = false;
                  crtColInfo._embObjectType = -1;
                  if (index == 0) {
                     crtColInfo._columnInfo = null;
                  } else {
                     crtColInfo._columnInfo = this.getRowName(realRowIndex + 1);
                  }

                  iIndex++;
                  columnStart++;
               }

               if (index == 0) {
                  for (int iColIndex = columnStart; iColIndex < this._endVisibleCol; iColIndex++) {
                     int crtColumnIndex = iColIndex - 1;
                     if (this._showHiddenRowsCols || !this._sheetData.isColumnHidden(crtColumnIndex)) {
                        ColumnInfo crtColInfo = this._colVector[iIndex++];
                        crtColInfo._hasBgColor = false;
                        crtColInfo._embObjectType = -1;
                        crtColInfo._columnInfo = this.getColName(crtColumnIndex);
                     }
                  }
               } else {
                  for (int iColIndex = columnStart; iColIndex < this._endVisibleCol; iColIndex++) {
                     int crtColumnIndex = iColIndex - 1;
                     if (this._showHiddenRowsCols || !this._sheetData.isColumnHidden(crtColumnIndex)) {
                        ColumnInfo crtColInfo = this._colVector[iIndex];
                        crtColInfo._hasBgColor = false;
                        crtColInfo._embObjectType = -1;
                        if (isColorDisplay) {
                           this.setColumnInfo(crtColInfo, realRowIndex, crtColumnIndex, rowHasBgColor, rowBgColor, sheetHasBgColor, sheetBgColor);
                        } else {
                           this.setColumnInfo(crtColInfo, realRowIndex, crtColumnIndex);
                        }

                        iIndex++;
                     }
                  }
               }
            }

            this._tableRow
               .initialize(
                  this._colVector,
                  iIndex,
                  this._nStyle,
                  this._gridLook,
                  this._nBuffer,
                  this._startVisibleCol == 0,
                  realRowIndex >= 0 ? this.isLastRetrievedRow(realRowIndex) : false,
                  index == this._nTotalRows - 1
               );
            this._tableRow.paint(graphics, y, this._useOriginalFont, this._fontStyle);
         }
      }
   }

   private void fillColumnWidths() {
      int iIndex = 0;
      int columnStart = this._startVisibleCol;
      if ((this._nStyle & 256) != 0) {
         if (columnStart == 0) {
            ColumnInfo colInfo = this._colVector[0];
            colInfo._columnWidth = 32;
            iIndex++;
            columnStart++;
         }

         for (int iColIndex = columnStart; iColIndex < this._endVisibleCol; iColIndex++) {
            int realColIndex = iColIndex - 1;
            if (this._showHiddenRowsCols || this._hiddenColsCount <= 0 || !this._sheetData.isColumnHidden(realColIndex)) {
               short colWidth = this._sheetData.getColumnWidth(realColIndex);
               ColumnInfo colInfo = this._colVector[iIndex++];
               colInfo._columnWidth = colWidth;
            }
         }
      } else {
         for (int iColIndex = columnStart; iColIndex < this._endVisibleCol; iColIndex++) {
            if (this._showHiddenRowsCols || this._hiddenColsCount <= 0 || !this._sheetData.isColumnHidden(iColIndex)) {
               short colWidth = this._sheetData.getColumnWidth(iColIndex);
               ColumnInfo colInfo = this._colVector[iIndex++];
               colInfo._columnWidth = colWidth;
            }
         }
      }
   }

   private boolean calculateCurrentFocusRect() {
      try {
         boolean bRedraw = this.calculateVisibleColumns();
         int sheetSelectedCol = this.getSelectedColumn();
         this._focusRectangle.x = this.getTotalAccumulatedColumnStart(sheetSelectedCol) - this.getTotalAccumulatedColumnStart(this._startVisibleCol);
         this._focusRectangle.width = this.getFocusColumnWidth(sheetSelectedCol);
         if (this._startVisibleCol == 0) {
            this._focusRectangle.x++;
         } else if (this._focusRectangle.x == 0) {
            this._focusRectangle.x++;
            this._focusRectangle.width--;
         }

         int nColHeight = this.getRowHeight();
         this._focusRectangle.y = nColHeight * this._nSelectedRow;
         this._focusRectangle.height = nColHeight;
         if ((this._nStyle & 4096) == 0 && ((this._nStyle & 256) == 0 || this._nSelectedCol != 0 && this._nSelectedRow != 0)) {
            this._focusRectangle.width++;
            if (this._nSelectedRow != this._nTotalRows - 1) {
               this._focusRectangle.height++;
            }
         }

         this.focusCellChanged();
         return bRedraw;
      } finally {
         this.focusCellChanged();
         return false;
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._sheetData.getNumberOfRows() != 0 && this._sheetData.getNumberOfCols() != 0) {
         if (!this._inHighlightRegion) {
            this._inHighlightRegion = true;
            boolean hasHeaders = (this._nStyle & 256) != 0;
            if (hasHeaders && this._nSelectedRow == 0) {
               graphics.invert(this._focusRectangle.x, this._focusRectangle.y + 1, this._focusRectangle.width + 1, this._focusRectangle.height - 2);
            } else if (!hasHeaders && this._nSelectedRow == 0) {
               graphics.invert(this._focusRectangle.x, this._focusRectangle.y + 1, this._focusRectangle.width, this._focusRectangle.height - 2);
            } else {
               graphics.invert(this._focusRectangle.x, this._focusRectangle.y, this._focusRectangle.width, this._focusRectangle.height - 1);
            }

            this._inHighlightRegion = false;
         }
      }
   }

   private void setColumnInfo(ColumnInfo colInfo, int row, int col, boolean rowHasBgColor, int rowBgColor, boolean sheetHasBgColor, int sheetBgColor) {
      if (rowHasBgColor) {
         colInfo._hasBgColor = true;
         colInfo._bgColor = rowBgColor;
      } else if (this._sheetData.sheetColumnHasDefinedBgColor(col)) {
         colInfo._hasBgColor = true;
         colInfo._bgColor = this._sheetData.getSheetColumnBgColor(col);
      } else if (sheetHasBgColor) {
         colInfo._hasBgColor = true;
         colInfo._bgColor = sheetBgColor;
      }

      this.setColumnInfo(colInfo, row, col);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void setColumnInfo(ColumnInfo colInfo, int row, int col) {
      boolean var8 = false /* VF: Semaphore variable */;

      label68:
      try {
         var8 = true;
         colInfo._columnInfo = this._sheetData.getCellValue(row, col);
         var8 = false;
      } finally {
         if (var8) {
            colInfo._columnInfo = null;
            break label68;
         }
      }

      if (this._sheetData._hasEmbeddedObjects) {
         if (colInfo._columnInfo != null) {
            if (!(colInfo._columnInfo instanceof DocViewCellInfo)) {
               return;
            }

            if (((DocViewCellInfo)colInfo._columnInfo).hasText(true)) {
               return;
            }
         }

         DocViewTextContentHandler textHandler = this._sheetData.getCellFormatterExtern(row, col, false);
         if (textHandler != null) {
            BreakObj[] breaks = textHandler.getBreakVector();
            if (breaks != null && breaks.length > 0) {
               for (int i = 0; i < breaks.length; i++) {
                  if (breaks[i] instanceof EmbeddedBreak) {
                     colInfo._embObjectType = ((EmbeddedBreak)breaks[i])._type;
                     return;
                  }
               }
            }
         }
      }
   }

   private int transformToRowIndex(int index, boolean fromSheetRow) {
      if (index >= 0 && index <= this._sheetData.getNumberOfRows() - 1) {
         int realIndex = index;
         if (!this._showHiddenRowsCols && this._hiddenRowsCount > 0) {
            if (fromSheetRow) {
               for (int i = 0; i <= realIndex; i++) {
                  if (this._sheetData.isRowHidden(i)) {
                     realIndex++;
                  }
               }
            } else {
               for (int i = realIndex; i >= 0; i--) {
                  if (this._sheetData.isRowHidden(i)) {
                     realIndex--;
                  }
               }
            }
         }

         return realIndex;
      } else {
         return -1;
      }
   }

   private int transformToColumnIndex(int index, boolean fromSheetCol) {
      if (index >= 0 && index <= this._sheetData.getNumberOfCols() - 1) {
         int realIndex = index;
         if (!this._showHiddenRowsCols && this._hiddenColsCount > 0) {
            if (fromSheetCol) {
               for (int i = 0; i <= realIndex; i++) {
                  if (this._sheetData.isColumnHidden(i)) {
                     realIndex++;
                  }
               }
            } else {
               for (int i = realIndex; i >= 0; i--) {
                  if (this._sheetData.isColumnHidden(i)) {
                     realIndex--;
                  }
               }
            }
         }

         return realIndex;
      } else {
         return -1;
      }
   }

   private int getMaxRowIndexVisible() {
      int maxRowIndex;
      for (maxRowIndex = this._sheetData.getNumberOfRows() - 1; maxRowIndex >= 0; maxRowIndex--) {
         if (!this._sheetData.isRowHidden(maxRowIndex)) {
            return maxRowIndex;
         }
      }

      return maxRowIndex;
   }

   private int getMaxColIndexVisible() {
      int maxColIndex;
      for (maxColIndex = this._sheetData.getNumberOfCols() - 1; maxColIndex >= 0; maxColIndex--) {
         if (!this._sheetData.isColumnHidden(maxColIndex)) {
            return maxColIndex;
         }
      }

      return maxColIndex;
   }

   private int getMinRowIndexVisible() {
      int minRowIndex;
      for (minRowIndex = 0; minRowIndex < this._sheetData.getNumberOfRows(); minRowIndex++) {
         if (!this._sheetData.isRowHidden(minRowIndex)) {
            return minRowIndex;
         }
      }

      return minRowIndex;
   }

   private int getMinColIndexVisible() {
      int minColIndex;
      for (minColIndex = 0; minColIndex < this._sheetData.getNumberOfCols(); minColIndex++) {
         if (!this._sheetData.isColumnHidden(minColIndex)) {
            return minColIndex;
         }
      }

      return minColIndex;
   }

   private int calculateSelectedCell(int amount, int status, int time) {
      if (this._nTotalRows != 0 && this._nTotalCols != 0) {
         boolean bHorzScroll = (status & 65537) != 0;
         boolean bVertScroll = (this._nStyle & 16) != 0;
         int nReturn = 0;
         if (bHorzScroll) {
            int focusCell = this._nSelectedRow * this._nTotalCols + this._nSelectedCol;
            focusCell += amount;
            if (focusCell < 0) {
               focusCell = 0;
            }

            if (focusCell > this._nTotalRows * this._nTotalCols - 1) {
               focusCell = this._nTotalRows * this._nTotalCols - 1;
            }

            if ((this._nStyle & 1) == 0 && focusCell / this._nTotalCols != this._nSelectedRow) {
               return 0;
            }

            int nOldSelectedRow = this._nSelectedRow;
            this._nSelectedRow = focusCell / this._nTotalCols;
            this._nSelectedCol = focusCell % this._nTotalCols;
            if (nOldSelectedRow != this._nSelectedRow) {
               super.moveFocus(this._nSelectedRow - nOldSelectedRow, 0, time);
            }
         } else {
            int newAmount = amount;
            if (amount > 0) {
               if (this._nSelectedRow == this._nTotalRows - 1) {
                  if (!bVertScroll) {
                     return 0;
                  }

                  if (this._nSelectedCol >= this._nTotalCols - 1) {
                     return 0;
                  }

                  this._nSelectedCol++;
                  this._nSelectedRow = 0;
                  super.moveFocus(-this._nTotalRows, status, time);
                  newAmount--;
               }

               this._nSelectedRow += newAmount;
               if (this._nSelectedRow > this._nTotalRows - 1) {
                  this._nSelectedRow = this._nTotalRows - 1;
               }
            } else if (amount < 0) {
               if (this._nSelectedRow == 0) {
                  if (!bVertScroll) {
                     return 0;
                  }

                  if (this._nSelectedCol <= 0) {
                     return 0;
                  }

                  this._nSelectedCol--;
                  this._nSelectedRow = this._nTotalRows - 1;
                  super.moveFocus(this._nTotalRows, status, time);
                  newAmount++;
               }

               this._nSelectedRow += newAmount;
               if (this._nSelectedRow < 0) {
                  this._nSelectedRow = 0;
               }
            }

            nReturn = super.moveFocus(newAmount, status, time);
         }

         return nReturn;
      } else {
         return 0;
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int nReturn = this.calculateSelectedCell(amount, status, time);
      if (this.calculateCurrentFocusRect()) {
         this.invalidate();
      }

      return nReturn;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      rect.set(this._focusRectangle);
   }

   private int getTotalAccumulatedColumnStart(int index) {
      if (index == 0) {
         return 1;
      }

      boolean bDisplayHeader = (this._nStyle & 256) != 0;
      if (!this._showHiddenRowsCols && this._hiddenColsCount > 0) {
         int columnIndex = bDisplayHeader ? index - 1 : index;
         if (this._sheetData.isColumnHidden(columnIndex)) {
            throw new IllegalArgumentException();
         }

         int columnStart = 0;
         if (bDisplayHeader) {
            columnStart += 32 + this._nBuffer;
         }

         for (int i = 0; i < columnIndex; i++) {
            if (!this._sheetData.isColumnHidden(i)) {
               columnStart += this._sheetData.getColumnWidth(i) + this._nBuffer;
            }
         }

         return columnStart;
      } else {
         return bDisplayHeader
            ? this._sheetData.getAccumulatedColumnStart(index - 1) + index * this._nBuffer + 32
            : this._sheetData.getAccumulatedColumnStart(index) + index * this._nBuffer;
      }
   }

   private int getFocusColumnWidth(int index) {
      if ((this._nStyle & 256) != 0) {
         return index > 0 ? this._sheetData.getColumnWidth(index - 1) + this._nBuffer - 1 : 33;
      }

      int var10000 = this._sheetData.getColumnWidth(index) + this._nBuffer;
      return index == 0 ? var10000 - 2 : var10000 - 1;
   }

   private boolean calculateVisibleColumns() {
      if (this._endVisibleCol != -1 && this.isSelectedColumnVisible()) {
         return false;
      }

      int oldStartVisibleCol = this._startVisibleCol;
      int oldEndVisibleCol = this._endVisibleCol;
      this._startVisibleCol = 0;
      this._endVisibleCol = -1;
      int sheetSelectedCol = this.getSelectedColumn();
      boolean displayHeaders = (this._nStyle & 256) != 0;
      int maxVisiblePixelValueNeeded = this.getTotalAccumulatedColumnStart(sheetSelectedCol)
         + (displayHeaders && sheetSelectedCol == 0 ? 32 : this._sheetData.getColumnWidth(displayHeaders ? sheetSelectedCol - 1 : sheetSelectedCol))
         + this._nBuffer;
      int crtColumnStart = -1;
      int i = 0;
      int colsCount = this._sheetData.getNumberOfCols() + (displayHeaders ? 1 : 0);

      for (i = 0; i < colsCount; i++) {
         try {
            crtColumnStart = this.getTotalAccumulatedColumnStart(i);
         } finally {
            continue;
         }

         if (crtColumnStart + DocViewGUIInternalConstants.SCREEN_WIDTH > maxVisiblePixelValueNeeded || i == sheetSelectedCol) {
            this._startVisibleCol = i;

            int j;
            for (j = i; j < colsCount; j++) {
               try {
                  if (this.getTotalAccumulatedColumnStart(j) > crtColumnStart + DocViewGUIInternalConstants.SCREEN_WIDTH) {
                     this._endVisibleCol = j;
                     break;
                  }
               } finally {
                  continue;
               }
            }

            if (j == colsCount) {
               this._endVisibleCol = colsCount;
            }
            break;
         }
      }

      if (i == colsCount) {
         this._startVisibleCol = colsCount - 1;
         this._endVisibleCol = colsCount;
      }

      if (oldStartVisibleCol == this._startVisibleCol && oldEndVisibleCol == this._endVisibleCol) {
         return false;
      }

      this.fillColumnWidths();
      return true;
   }

   private boolean isSelectedColumnVisible() {
      int sheetSelectedCol = this.getSelectedColumn();
      int startCol = this.getTotalAccumulatedColumnStart(sheetSelectedCol) - this.getTotalAccumulatedColumnStart(this._startVisibleCol);
      boolean displayHeader = (this._nStyle & 256) != 0;
      return startCol >= 0
         && startCol
               + (displayHeader && sheetSelectedCol == 0 ? 32 : this._sheetData.getColumnWidth(sheetSelectedCol - (displayHeader ? 1 : 0)))
               + this._nBuffer
            < DocViewGUIInternalConstants.SCREEN_WIDTH;
   }

   public CustomListField(DocViewSheetData sheetData, FontFactory fontFactory, short style, byte look, long fieldStyle) {
      super(sheetData.getNumberOfRows(), fieldStyle);
      this.setSearchable(false);
      this.setEmptyString("", 0);
      this.setCallback(this);
      this._fontFactory = fontFactory;
      this._sheetData = sheetData;
      this._nTotalRows = this._sheetData.getNumberOfRows();
      this._nTotalCols = this._sheetData.getNumberOfCols();
      this._sheetData.setColumnWidth((short)35);
      this._hiddenRowsCount = this._sheetData.getNumHiddenRows();
      this._hiddenColsCount = this._sheetData.getNumHiddenColumns();
      this._minVisibleRowIndex = this.getMinRowIndexVisible();
      this._minVisibleColumnIndex = this.getMinColIndexVisible();
      this._maxVisibleRowIndex = this.getMaxRowIndexVisible();
      this._maxVisibleColumnIndex = this.getMaxColIndexVisible();
      this._columnFitVector = new int[this._nTotalCols];
      this.resetColumnFitVector();
      this._tableRow = new MultiElementRow(sheetData.getSheetBuffer(), fontFactory);
      int size = this._nTotalCols + 1;
      this._colVector = new ColumnInfo[size];

      for (int i = 0; i < size; i++) {
         this._colVector[i] = new ColumnInfo();
      }

      this._gridLook = look;
      this.setTableStyle(style);
      this.toggleHiddenStructure();
   }
}
