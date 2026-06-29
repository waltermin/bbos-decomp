package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

final class DocViewSheetDisplayField$ArznScreenField extends CustomListField implements CookieProvider {
   private byte _crtColumnSizeIdx;
   private int _maxRowIndexWithData;
   private final DocViewSheetDisplayField this$0;

   final boolean findString(boolean bSearchNext, boolean bSameString, boolean bCaseSensitive) {
      boolean bDisplayHeaders = (super._nStyle & 256) != 0;
      int nCols = this.getColCount();
      int nRows = this.getRowCount();
      int nSelectedRow = this.getSelectedRow();
      int nSelectedCol = this.getSelectedColumn();
      if (bDisplayHeaders) {
         nSelectedRow--;
         nSelectedCol--;
      }

      int nStartRow = nSelectedRow;
      int nStartColumn = nSelectedCol;
      if (bSameString && bSearchNext) {
         if (nStartColumn < nCols - 1) {
            nStartColumn++;
         } else {
            if (nStartRow >= nRows - 1) {
               return false;
            }

            nStartRow++;
            nStartColumn = 0;
         }
      }

      if (bSearchNext) {
         for (int i = nStartRow; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
               if ((i > nStartRow || j >= nStartColumn) && this.findStringInCell(this.this$0._strFindString, i, j, bCaseSensitive)) {
                  this.setFocusedCell(i, j);
                  return true;
               }
            }
         }
      }

      return false;
   }

   final boolean setNewFont(String fontName, int fontSize, boolean fontNameChanged, boolean fontSizeChanged, boolean useOriginalFont, int fontStyle) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   final void notifyMoreDataIn(boolean isActive) {
      if (this._maxRowIndexWithData != -1) {
         this._maxRowIndexWithData = super._sheetData.getMaxRowIndexWithInfo();
         if (isActive) {
            synchronized (this.this$0._application.getAppEventLock()) {
               this.reFitSheet();
               this.initFocus();
               this.invalidate();
               return;
            }
         }

         this.reFitSheet();
      }
   }

   final void setSheetColumnWidth(byte colWidth, boolean state) {
      if (!state || colWidth != this._crtColumnSizeIdx) {
         switch (colWidth) {
            case -1:
               break;
            case 0:
            default:
               super._sheetData.setColumnWidth((short)20);
               break;
            case 1:
               super._sheetData.setColumnWidth((short)35);
               break;
            case 2:
               super._sheetData.setColumnWidth((short)60);
               break;
            case 3:
               int cols = this.getColCount();

               for (int i = 0; i < cols; i++) {
                  this.setFitColumn(i);
               }
         }

         if (state) {
            this._crtColumnSizeIdx = colWidth;
         }
      }
   }

   final DocViewCellInfo getSelectedCellInfo() {
      int row = this.getSelectedRow();
      int col = this.getSelectedColumn();
      if ((super._nStyle & 256) != 0) {
         row--;
         col--;
      }

      try {
         return super._sheetData.getCellValue(row, col);
      } finally {
         ;
      }
   }

   final int getSelectedRowBgColor() {
      int row = this.getSelectedRow();
      if ((super._nStyle & 256) != 0) {
         row--;
      }

      return super._sheetData.sheetRowHasDefinedBgColor(row) ? super._sheetData.getSheetRowBgColor(row) : -1;
   }

   final boolean selectedCellHasData(boolean onlyText) {
      int row = this.getSelectedRow();
      int col = this.getSelectedColumn();
      if ((super._nStyle & 256) != 0) {
         row--;
         col--;
      }

      label53:
      try {
         if (super._sheetData.getCellValue(row, col).hasText(true)) {
            return true;
         }
      } finally {
         break label53;
      }

      if (!onlyText) {
         DocViewTextContentHandler cellTextHandler = null;
         if (this.this$0._parsingData.getTrackChangesOnStatus()) {
            cellTextHandler = super._sheetData.getCellFormatterExtern(row, col, true);
         }

         if (cellTextHandler == null) {
            cellTextHandler = super._sheetData.getCellFormatterExtern(row, col, false);
         }

         return cellTextHandler != null && !AttachmentViewerFactory.isTextDocumentEmpty(cellTextHandler);
      } else {
         return false;
      }
   }

   final int calculateSelectedCellValueWithIndex(boolean shrinkToVisibleLength) {
      boolean bDisplayHeaders = (super._nStyle & 256) != 0;
      int retValue = 0;
      this.this$0._selectedCellDisplayValue.setLength(0);
      if (bDisplayHeaders) {
         if (this.getSelectedRow() != 0 && this.getSelectedColumn() != 0) {
            this.this$0._selectedCellDisplayValue.append(this.getColName(this.getSelectedColumn() - 1));
            this.this$0._selectedCellDisplayValue.append(this.getRowName(this.getSelectedRow()) + ": ");

            try {
               retValue = this.this$0._selectedCellDisplayValue.length();
               this.appendDisplayCellValueToBuffer(this.getSelectedRow() - 1, this.getSelectedColumn() - 1, shrinkToVisibleLength);
               return retValue;
            } finally {
               ;
            }
         } else {
            this.this$0._selectedCellDisplayValue.append(super._sheetData.toString());
            return retValue;
         }
      } else if (super._nTotalRows != 0 && super._nTotalCols != 0) {
         this.this$0._selectedCellDisplayValue.append(this.getColName(this.getSelectedColumn()));
         this.this$0._selectedCellDisplayValue.append(this.getRowName(this.getSelectedRow() + 1) + ": ");

         try {
            retValue = this.this$0._selectedCellDisplayValue.length();
            this.appendDisplayCellValueToBuffer(this.getSelectedRow(), this.getSelectedColumn(), shrinkToVisibleLength);
            return retValue;
         } finally {
            ;
         }
      } else {
         this.this$0._selectedCellDisplayValue.append(super._sheetData.toString());
         return retValue;
      }
   }

   final void toggleRowColsVisibility() {
      if (this.toggleHiddenStructure()) {
         if (super._hiddenRowsCount > 0) {
            this.reFitSheet();
         }

         this.this$0.setElementDescription("");
         super._startVisibleCol = 0;
         super._endVisibleCol = -1;
         this.setFocusedCell(true);
      }
   }

   final void displayTitleString() {
      this.this$0._cellDescription._prefixSize = this.calculateSelectedCellValueWithIndex(true);
      this.this$0._cellDescription._textDescription = this.this$0._selectedCellDisplayValue;
      this.this$0.setElementDescription(this.this$0._cellDescription);
   }

   @Override
   public final Object getCookieWithFocus() {
      Object cookie = null;
      if (this.selectedCellHasData(true)) {
         String strText = this.getSelectedCellValue();
         StringPatternEnumerator patternEnum = new StringPatternEnumerator(strText, this.this$0._patterns);
         if (patternEnum.hasMoreMatches()) {
            if (this.this$0._match == null) {
               this.this$0._match = new StringPattern$Match();
            }

            patternEnum.nextMatch(this.this$0._match);
            if (this.this$0._invokeContext == null) {
               this.this$0._invokeContext = new ContextObject();
            }

            this.this$0._invokeContext.put(253, strText.substring(this.this$0._match.beginIndex, this.this$0._match.endIndex));
            cookie = FactoryUtil.createInstance(this.this$0._match.id, this.this$0._invokeContext);
         }
      }

      return cookie;
   }

   private final int getFontStyle() {
      return super._fontStyle;
   }

   private final void reFitSheet() {
      IntVector columnIndividualFitVector = new IntVector();
      DocViewOptions options = DocViewOptions.getOptions();
      if (options.getSheetColumnWidth() != 3) {
         int cols = this.getColCount();

         for (int i = 0; i < cols; i++) {
            if (super._columnFitVector[i] != -1) {
               short colSize = super._sheetData.getColumnWidth(i);
               if (colSize == super._columnFitVector[i] || colSize != 20 && colSize != 35 && colSize != 60) {
                  columnIndividualFitVector.addElement(i);
               }
            }
         }
      }

      this.resetColumnFitVector();
      if (options.getSheetColumnWidth() == 3) {
         this._crtColumnSizeIdx = -1;
         this.setSheetColumnWidth((byte)3, true);
      } else {
         int size = columnIndividualFitVector.size();

         for (int i = 0; i < size; i++) {
            this.setFitColumn(columnIndividualFitVector.elementAt(i));
         }
      }

      IntVector var6 = null;
   }

   @Override
   protected final boolean keyControl(char key, int status, int time) {
      switch (key) {
         case '\u0082':
            return super.keyControl(key, status, time);
         case '\u0083':
         default:
            this.moveFocus(-1, status | 1, time);
            this.focusAdd(false);
            this.invalidate();
            return true;
         case '\u0084':
            this.moveFocus(1, status | 1, time);
            this.focusAdd(false);
            this.invalidate();
            return true;
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (InternalServices.isReducedFormFactor() && keycode >> 16 == 81) {
         try {
            this.toggleColumnSize(this.getSelectedColumn());
            return true;
         } finally {
            return super.keyDown(keycode, time);
         }
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (!InternalServices.isReducedFormFactor()) {
         switch (UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status)) {
            case 'H':
            case 'h':
               this.toggleRowColsVisibility();
               return true;
            case 'W':
            case 'w':
               try {
                  this.toggleColumnSize(this.getSelectedColumn());
                  return true;
               } finally {
                  ;
               }
         }
      }

      return super.keyChar(key, status, time);
   }

   private final boolean displayCustomMenu() {
      if ((super._nStyle & 256) != 0
         && this.getSelectedRow() == 0
         && this.getRowCount() > 0
         && this.getColCount() > 0
         && (
            super._showHiddenRowsCols
               || super._sheetData.getNumHiddenRows() != this.getRowCount() && super._sheetData.getNumHiddenColumns() != this.getColCount()
         )) {
         int colSel = this.getSelectedColumn();
         int selection = 0;
         int resourceID = 82;
         if (colSel != 0) {
            selection = 3;
            resourceID = 25;
            short crtColumnSize = super._sheetData.getColumnWidth(colSel - 1);
            switch (crtColumnSize) {
               case 20:
                  selection = 0;
                  break;
               case 35:
                  selection = 1;
                  break;
               case 60:
                  selection = 2;
            }
         }

         CustomMenuField fake = new CustomMenuField(DocViewDisplayField._resources.getStringArray(resourceID), selection);
         fake.setFont(this.getFont());
         int offset = 0;

         label88:
         try {
            offset = this.getManager().getManager().getContentTop();
         } finally {
            break label88;
         }

         InPlaceContextMenu contextMenu = new InPlaceContextMenu(
            super._focusRectangle.x + 3, super._focusRectangle.y + offset + (this.getFont().getHeight() >> 1), fake
         );
         if (contextMenu.doModal()) {
            int index = fake.getSelectedIndex();
            boolean refresh = true;
            if (colSel != 0) {
               if (index != selection) {
                  switch (index) {
                     case -1:
                        break;
                     case 0:
                     default:
                        super._sheetData.setColumnWidth(colSel - 1, (short)20);
                        break;
                     case 1:
                        super._sheetData.setColumnWidth(colSel - 1, (short)35);
                        break;
                     case 2:
                        super._sheetData.setColumnWidth(colSel - 1, (short)60);
                        break;
                     case 3:
                        this.setFitColumn(colSel - 1);
                  }
               } else {
                  refresh = false;
               }
            } else {
               this.setSheetColumnWidth((byte)index, false);
            }

            if (refresh) {
               this.initFocus();
               this.invalidate();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      Verb[] verbs = new Verb[0];
      Verb defaultVerb = CookieProviderUtilities.getFocusVerbs(this, null, verbs);
      int count = verbs.length;

      for (int idx = 0; idx < count; idx++) {
         int priority = verbs[idx] == defaultVerb ? 10 : Integer.MAX_VALUE;
         VerbMenuItem menuItem = new VerbMenuItem(verbs[idx], priority);
         contextMenu.addItem(menuItem);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      try {
         if (this.displayCustomMenu()) {
            return true;
         }
      } finally {
         return super.trackwheelClick(status, time);
      }

      return super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      try {
         if (this.displayCustomMenu()) {
            return true;
         }
      } finally {
         return super.navigationClick(status, time);
      }

      return super.navigationClick(status, time);
   }

   private final void toggleColumnSize(int columnIndex) {
      int colIndex = columnIndex;
      if ((super._nStyle & 256) != 0) {
         colIndex--;
      }

      short crtColumnSize = super._sheetData.getColumnWidth(colIndex);
      switch (crtColumnSize) {
         case 20:
            super._sheetData.setColumnWidth(colIndex, (short)35);
            break;
         case 35:
            super._sheetData.setColumnWidth(colIndex, (short)60);
            break;
         case 60:
            if (!this.setFitColumn(colIndex)) {
               super._sheetData.setColumnWidth(colIndex, (short)20);
            }
            break;
         default:
            super._sheetData.setColumnWidth(colIndex, (short)20);
      }

      this.initFocus();
      this.invalidate();
   }

   private final boolean setFitColumn(int colIndex) {
      int columnWidth = this.getColumnFitWidth(colIndex);
      if (columnWidth != 0 && columnWidth != super._sheetData.getColumnWidth(colIndex)) {
         super._sheetData.setColumnWidth(colIndex, (short)Math.min(DocViewGUIInternalConstants.SCREEN_WIDTH - super._nBuffer, columnWidth));
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if ((dx != 0 || dy != 0) && (status & 1) != 0) {
         this.screenSizeMove(dx != 0, dx > 0 || dy > 0, time);
         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   private final void screenSizeMove(boolean horizontal, boolean positive, int time) {
      int selectedColumn = this.getSelectedColumn();
      int selectedRow = this.getSelectedRow();
      if (horizontal) {
         if (positive) {
            if (selectedColumn < super._nTotalCols - 1) {
               this.moveFocus(super._nTotalCols - 1 - selectedColumn, 1, time);
               this.focusAdd(false);
               this.invalidate();
               return;
            }
         } else if (selectedColumn > 0) {
            this.moveFocus(-selectedColumn, 1, time);
            this.focusAdd(false);
            this.invalidate();
            return;
         }
      } else if (positive) {
         int visibleLines = this.getVisibleLinesPageDown(0);
         if (visibleLines > 0 && selectedRow < super._nTotalRows - 1) {
            this.moveFocus(Math.min(visibleLines, super._nTotalRows - 1 - selectedRow), 0, time);
            this.focusAdd(false);
            this.invalidate();
            return;
         }
      } else {
         int visibleLines = this.getVisibleLinesPageDown(0);
         if (visibleLines > 0 && selectedRow > 0) {
            this.moveFocus(Math.max(-visibleLines, -selectedRow), 0, time);
            this.focusAdd(false);
            this.invalidate();
         }
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if (amount != 0) {
         int returnVal = super.moveFocus(amount, status, time);
         if (returnVal != amount && (status & 65537) == 0 && !this.this$0.isMoreRequestSent() && !super._sheetData.isSpreadsheetComplete()) {
            boolean bDisplayHeaders = (super._nStyle & 256) != 0;
            short crtRow = (short)(this.getSelectedRow() - (bDisplayHeaders ? 1 : 0));
            if (crtRow >= this._maxRowIndexWithData && AttachmentViewerFactory.isAutoMoreEnabled()) {
               this.this$0.executeMore(null, true, false);
            }
         }

         return returnVal;
      } else {
         return super.moveFocus(amount, status, time);
      }
   }

   private final boolean getUseOriginalFont() {
      return super._useOriginalFont;
   }

   @Override
   protected final boolean isLastRetrievedRow(int realRowIndex) {
      return !super._sheetData.isSpreadsheetComplete() && realRowIndex == this._maxRowIndexWithData;
   }

   private final int getRowCount() {
      return super._sheetData.getNumberOfRows();
   }

   private final void appendDisplayCellValueToBuffer(int row, int column, boolean shrinkToVisibleLength) {
      DocViewCellInfo cellInfo = super._sheetData.getCellValue(row, column);
      int minLength = shrinkToVisibleLength ? Math.min(cellInfo.getCellContentLength(), 64) : cellInfo.getCellContentLength();
      if (minLength > 0) {
         StringBuffer sheetBuffer = super._sheetData.getSheetBuffer();
         int[] regionsOffset = cellInfo.getRegionsOffsetIndeces();
         int regionOffsetLength = regionsOffset.length;

         for (int i = 0; i < regionOffsetLength - 1 && minLength > 0; i += 2) {
            StringUtilities.append(this.this$0._selectedCellDisplayValue, sheetBuffer, regionsOffset[i], Math.min(minLength, regionsOffset[i + 1]));
            minLength -= regionsOffset[i + 1];
         }
      }
   }

   @Override
   protected final void focusCellChanged() {
      if (this.this$0._fullDocState) {
         this.displayTitleString();
      }

      super.focusCellChanged();
   }

   private final int getColCount() {
      return super._sheetData.getNumberOfCols();
   }

   DocViewSheetDisplayField$ArznScreenField(DocViewSheetDisplayField _1, DocViewSheetData sheetData, short style, byte gridLook, long fieldStyle) {
      super(sheetData, (FontFactory)_1._fontFactory, style, gridLook, fieldStyle);
      this.this$0 = _1;
      this._crtColumnSizeIdx = -1;
      this._maxRowIndexWithData = -1;
      DocViewOptions options = DocViewOptions.getOptions();
      this.setNewFont(options.getSheetFontName(), options.getSheetFontSize(), true, true, options.getUseOriginalSheetFont(), options.getSheetFontStyle());
      this._maxRowIndexWithData = sheetData.getMaxRowIndexWithInfo();
   }

   private final boolean findStringInCell(String strSearchString, int nRow, int nCol, boolean bCaseSensitive) {
      if (nRow < this.getRowCount() && nCol < this.getColCount()) {
         if (!super._showHiddenRowsCols && (super._sheetData.isRowHidden(nRow) || super._sheetData.isColumnHidden(nCol))) {
            return false;
         }

         String strCellValue = "";

         try {
            strCellValue = this.getStringCellValue(nRow, nCol);
         } finally {
            ;
         }

         String strToSearch = strSearchString;
         if (!bCaseSensitive) {
            strCellValue = strCellValue.toLowerCase();
            strToSearch = strToSearch.toLowerCase();
         }

         if (strCellValue.length() > 0 && strCellValue.indexOf(strToSearch) != -1) {
            return true;
         }
      }

      return false;
   }
}
