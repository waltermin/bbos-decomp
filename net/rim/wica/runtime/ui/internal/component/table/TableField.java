package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.MathUtilities;
import net.rim.wica.runtime.ui.internal.FontAccentsUtil;

public class TableField extends Field {
   private TableData _table;
   private int _selectedCol;
   private int _selectedRow;
   private IntVector _visibleCols;
   private int _rowHeight;
   private boolean _isHeaderVisible;
   private boolean _isGridVisible;
   private boolean _isRowSelectorVisible;
   private int _tableX;
   private int _firstRowIndex;
   private int _firstColIndex;
   private int _lastRowIndex;
   private int _numOfRows;
   private int _lastColIndex;
   private boolean _isHeaderNotFullyVisible;
   private boolean _paintEnabled = true;
   private int _firstSelectableColIndex;
   private int _lastSelectableColIndex;
   private int _headerRowY_SC;
   private boolean _isLayoutWidthMin;
   private IntVector _gridX = (IntVector)(new Object());
   private IntVector _gridY = (IntVector)(new Object());
   public static final int ROW_SELECTOR_WIDTH = 5;
   private static final TableField$LeftRightArrows _ARROWS = new TableField$LeftRightArrows(null);
   private static final TableField$ViewCellDialog _VIEW_CELL_DIALOG = new TableField$ViewCellDialog();
   private static MenuItem _VIEW_CELL = new TableField$ViewCellMenuItem();
   private static MenuItem _FREEZE = new TableField$FreezeColumnMenuItem();
   private static MenuItem _UNFREEZE = new TableField$UnfreezeColumnMenuItem();

   public static void setMenuLabels(ResourceBundle bundle, int viewCellID, int freezeColID, int unfreezeColID) {
      _VIEW_CELL = new TableField$ViewCellMenuItem(bundle, viewCellID);
      _FREEZE = new TableField$FreezeColumnMenuItem(bundle, freezeColID);
      _UNFREEZE = new TableField$UnfreezeColumnMenuItem(bundle, unfreezeColID);
   }

   public TableField(TableData table) {
      super(18014398509481984L);
      this._table = table == null ? new TableData(null) : table;
      this._visibleCols = (IntVector)(new Object(this._table.getNumOfCols()));
      this.init();
      this._selectedCol = this._firstColIndex;
      this._selectedRow = this._firstRowIndex;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      rect.x = 0;
      rect.width = this.getContentWidth();
      rect.y = this.getY(this._selectedRow);
      rect.height = this._rowHeight + 1;
   }

   @Override
   public boolean isFocusable() {
      return this._lastColIndex == -1 || !this._isHeaderVisible && this._numOfRows <= 0 || this._visibleCols != null && this._visibleCols.size() <= 0
         ? false
         : super.isFocusable();
   }

   @Override
   public boolean isSelectionCopyable() {
      return this._selectedCol > -1;
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      cb.put(this.getSelectedText());
   }

   public void setSelectedRow(int row) {
      if (row != this._selectedRow) {
         this.focusRemove();
         this._selectedRow = MathUtilities.clamp(this._firstRowIndex, row, this._lastRowIndex);
         if (this._isHeaderVisible) {
            this.scrollToRowWithFocus();
         }

         this.focusAdd(true);
      }
   }

   public int getSelectedRow() {
      return this._selectedRow;
   }

   public void setSelectedCol(int col) {
      if (col != this._selectedCol) {
         this.focusRemove();
         this._selectedCol = MathUtilities.clamp(this._firstColIndex, col, this._lastColIndex);
         Screen screen = this.getScreen();
         if (screen != null && screen.isValidLayout() && this.isFocusable()) {
            this.scrollToColWithFocus();
         }

         this.focusAdd(true);
      }
   }

   public int getSelectedCol() {
      return this._selectedCol;
   }

   public TableData getTableData() {
      return this._table;
   }

   public void refresh() {
      if (this._numOfRows == this._table.getNumOfRows() + (this._table.isHeaderVisible() ? 1 : 0) && this._rowHeight == this._table.getRowHeight() + 1) {
         this.setVisibilityProperties();
         this._table.layout();
         int newTableWidth = this._table.getWidth() + this._tableX;
         int contentWidth = this.getContentWidth();
         if ((!this._isLayoutWidthMin || newTableWidth >= contentWidth) && (this._isLayoutWidthMin || newTableWidth == contentWidth)) {
            this.setFirstAndLastSelectableColIndexes();
            this.invalidate();
         } else {
            this.updateLayout();
         }
      } else {
         this.init();
         this._selectedRow = MathUtilities.clamp(this._firstRowIndex, this._selectedRow, this._lastRowIndex);
         this.updateLayout();
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (this._lastColIndex != -1 && (this._isHeaderVisible || this._numOfRows > 0)) {
         this._table.setAvailableWidth(width - this._tableX);
         this._rowHeight = this._table.getRowHeight() + 1;
         this.tableLayoutUpdate();
         if (this._visibleCols.size() > 0) {
            int tableWidth = this._table.getWidth() + this._tableX;
            this._isLayoutWidthMin = width <= tableWidth;
            this.setExtent(this._isLayoutWidthMin ? width : tableWidth, this._rowHeight * this._numOfRows + 1);
            return;
         }
      }

      this._isLayoutWidthMin = false;
      this.setExtent(0, 0);
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this._paintEnabled) {
         XYRect clip = graphics.getClippingRect();
         if (clip.height == 0 || clip.width == 0) {
            return;
         }

         int clipY1 = clip.y;
         int clipY2 = clip.y + clip.height - 1;
         int topRow = this.getRowIndex(clipY1);
         int bottomRow = this.getRowIndex(clipY2);
         int y = 0;
         int x = 0;
         int i = 0;
         int visibleColsLength = this._visibleCols.size();
         int contentWidth = this.getContentWidth();
         int tableWidth = contentWidth - this._tableX;
         FontStyleColor superTableStyle = this._table.getSuperStyle();
         int numOfRowsToPaint = bottomRow - topRow + 1;
         if (superTableStyle != null) {
            graphics.pushContext(this._tableX, clip.y, tableWidth, clip.height, 0, 0);
            this.setStyle(graphics, superTableStyle);
         }

         this.calcGridX(tableWidth);
         this._gridY.setSize(numOfRowsToPaint + 1);
         if (topRow == -1 || this._isHeaderVisible && this._isHeaderNotFullyVisible && clipY1 + this._headerRowY_SC < this._rowHeight) {
            y = this.getY(topRow);
            this.drawRow(graphics, -1, y, tableWidth, this._rowHeight + 1);
            this._gridY.setElementAt(y, bottomRow - topRow);
            topRow++;
         }

         for (int var27 = bottomRow; var27 >= topRow; var27--) {
            y = this.getY(var27);
            this.drawRow(graphics, var27, y, tableWidth, this._rowHeight + 1);
            this._gridY.setElementAt(y, bottomRow - var27);
         }

         if (superTableStyle != null) {
            graphics.popContext();
         }

         x = this._gridX.lastElement();
         this._gridY.setElementAt(this.getContentHeight() - 1, numOfRowsToPaint);
         int originalColor = graphics.getColor();
         if (this._isGridVisible) {
            graphics.setColor(11119017);

            for (int var28 = numOfRowsToPaint; var28 >= 0; var28--) {
               y = this._gridY.elementAt(var28);
               graphics.drawLine(this._tableX, y, x, y);
            }

            for (int var29 = visibleColsLength; var29 >= 0; var29--) {
               if (!this._table.isGridIndicatingNotVisbleCols(var29)) {
                  x = this._gridX.elementAt(var29);
                  graphics.drawLine(x, clipY1, x, clipY2);
               }
            }
         }

         graphics.setColor(16711680);

         for (int var30 = visibleColsLength; var30 >= 0; var30--) {
            if (this._table.isGridIndicatingNotVisbleCols(var30)) {
               x = this._gridX.elementAt(var30);
               graphics.drawLine(x, clipY1, x, clipY2);
            }
         }

         graphics.setColor(originalColor);
         boolean isRight = this.isRigthArrow();
         boolean isLeft = this.isLeftArrow();
         if (isRight || isLeft) {
            int arrowHeight = _ARROWS._left.getHeight();
            int arrowWidth = _ARROWS._left.getWidth();
            int arrowsTop = this.getHorizontalArrowsTop(arrowHeight);
            if (clip.y <= arrowsTop + arrowHeight - 1 && clip.Y2() > arrowsTop) {
               if (isLeft && clip.x <= this._tableX + arrowWidth - 1 && clip.X2() > this._tableX) {
                  graphics.drawBitmap(this._tableX, arrowsTop, arrowWidth, arrowHeight, _ARROWS._left, 0, 0);
               }

               if (isRight && clip.x <= contentWidth - 1 && clip.X2() > contentWidth - arrowWidth) {
                  graphics.drawBitmap(contentWidth - arrowWidth, arrowsTop, arrowWidth, arrowHeight, _ARROWS._right, 0, 0);
               }
            }
         }
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      int x = this.getX(this._selectedCol);
      int width = this.getColWidth(this._selectedCol);
      int y = this.getY(this._selectedRow);
      int height = this._rowHeight + 1;
      if (this._isRowSelectorVisible) {
         if (on) {
            int x1 = 4;
            int halfHeight = height / 2;
            int y1 = y + halfHeight - (height % 2 == 0 ? 1 : 0);
            int y0;
            int y2;
            if (x1 >= halfHeight) {
               x1 = halfHeight;
               y0 = y;
               y2 = y0 + height - 1;
            } else {
               y0 = y1 - x1;
               y2 = y1 + x1;
            }

            graphics.drawLine(0, y0, 0, y2);
            graphics.drawLine(0, y2, x1, y1);
            graphics.drawLine(x1, y1, 0, y0);
         }

         graphics.invert(x, y, width, height);
         if (!on) {
            graphics.clear(0, y, 5, height);
            return;
         }
      } else {
         graphics.invert(x, y, width, height);
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      Font tableFont = this._table.getFont();
      if (tableFont == null) {
         this._table.setFont(this.getFont());
      } else {
         this.setFont(tableFont);
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      super.moveFocus(amount, status, time);
      return this.isAltPressed(status) ? this.moveFocusHorizontally(amount) : this.moveFocusVertically(amount);
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      super.moveFocus(x, y, status, time);
      this.setSelectedRow(this.getRowIndex(MathUtilities.clamp(0, y, this.getContentHeight() - 1)));
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      if (direction > 0) {
         this._selectedRow = this._firstRowIndex;
         if (this.isRigthArrow() || this.isLeftArrow()) {
            this.invalidate();
         }
      } else if (direction < 0) {
         this._selectedRow = this._lastRowIndex;
      }

      if (this._isHeaderVisible) {
         this.scrollToRowWithFocus();
      }
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      if (this.isFocus() && this._isHeaderVisible) {
         this.scrollToRowWithFocus();
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      if (this._selectedCol > -1) {
         contextMenu.addItem(_VIEW_CELL);
         if (this._table.getColumn(this._selectedCol).isFrozen()) {
            contextMenu.addItem(_UNFREEZE);
         } else {
            contextMenu.addItem(_FREEZE);
         }
      }

      super.makeContextMenu(contextMenu);
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      this.focusRemove();
      if ((dx == 0 || this.moveFocusHorizontally(dx) == 0) && (dy == 0 || this.moveFocusVertically(dy) == 0)) {
         this.focusChangeNotify(2);
         this.focusAdd(true);
         return true;
      } else {
         return false;
      }
   }

   protected Manager getScrollingManager() {
      Screen screen = this.getScreen();
      return !(screen instanceof Object) ? null : ((MainScreen)screen).getMainManager();
   }

   private int moveFocusHorizontally(int amount) {
      int newSelectedCol = MathUtilities.clamp(this._firstColIndex, this._selectedCol + amount, this._lastColIndex);
      if (newSelectedCol == this._selectedCol) {
         return 0;
      }

      if (newSelectedCol != -1 && !this._table.getColumn(newSelectedCol).isSelectable()) {
         newSelectedCol = this._table.findNextSelectableColumn(newSelectedCol, this._selectedCol < newSelectedCol);
      }

      int partVisibleColIndex = this._table.getPartVisibleColIndex();
      int index;
      if (Math.abs(amount) > 1
         || (index = Arrays.binarySearch(this._visibleCols.getArray(), newSelectedCol, 0, this._visibleCols.size())) < 0
         || this._visibleCols.elementAt(index) == partVisibleColIndex) {
         IntVector newVisibleCols;
         if (newSelectedCol == -1) {
            newVisibleCols = this._table.getVisibleColumns(this._table.findNextSelectableColumn(-1, true), true);
         } else {
            newVisibleCols = this._table.getVisibleColumns(newSelectedCol, this._selectedCol < newSelectedCol);
         }

         if (!isEqual(this._visibleCols, newVisibleCols) || partVisibleColIndex != this._table.getPartVisibleColIndex()) {
            this._visibleCols.setSize(newVisibleCols.size());
            newVisibleCols.copyInto(this._visibleCols.getArray());
            this.invalidate();
         }
      }

      this._selectedCol = newSelectedCol;
      return 0;
   }

   private int moveFocusVertically(int scrolls) {
      int newSelectedRow = this._selectedRow + scrolls;
      int scrollsNotConsumed = 0;
      if (newSelectedRow < this._firstRowIndex) {
         scrollsNotConsumed = newSelectedRow - this._firstRowIndex;
         newSelectedRow = this._firstRowIndex;
      } else if (newSelectedRow > this._lastRowIndex) {
         scrollsNotConsumed = newSelectedRow - this._lastRowIndex;
         newSelectedRow = this._lastRowIndex;
      }

      boolean isRight = this.isRigthArrow();
      boolean isLeft = this.isLeftArrow();
      if (isRight || isLeft) {
         Manager m = this.getScrollingManager();
         if (m != null) {
            int arrowHeight = _ARROWS._left.getHeight();
            int arrowWidth = _ARROWS._left.getWidth();
            int arrowsTop = this.getHorizontalArrowsTop(arrowHeight);
            if (this._selectedRow < newSelectedRow) {
               if (this.getY(newSelectedRow) + this._rowHeight - arrowHeight > arrowsTop) {
                  this.invlidateHorizontalArrows(isLeft, isRight, arrowHeight, arrowWidth, arrowsTop, arrowsTop + arrowHeight - 1);
               }
            } else if (scrollsNotConsumed < 0) {
               this.invalidate();
            } else {
               int newArrowsTop = this.getY(newSelectedRow) + m.getVisibleHeight() - arrowHeight;
               if (newArrowsTop < arrowsTop) {
                  this.invlidateHorizontalArrows(isLeft, isRight, arrowHeight, arrowWidth, newArrowsTop, newArrowsTop + arrowHeight - 1);
               }
            }
         }
      }

      this._selectedRow = newSelectedRow;
      if (this._isHeaderVisible) {
         this.scrollToRowWithFocus();
      }

      return scrollsNotConsumed;
   }

   private void invlidateHorizontalArrows(boolean isLeft, boolean isRight, int arrowHeight, int arrowWidth, int arrowsTop, int arrowsBottom) {
      if (isLeft) {
         this.invalidate(this._tableX, arrowsTop, arrowHeight, arrowsBottom);
      }

      if (isRight) {
         this.invalidate(this.getContentWidth() - arrowWidth, arrowsTop, arrowHeight, arrowsBottom);
      }
   }

   private void scrollToRowWithFocus() {
      Screen screen = this.getScreen();
      if (screen != null && screen.isValidLayout()) {
         Manager m = this.getScrollingManager();
         if (m != null && m.isStyle(281474976710656L)) {
            this._headerRowY_SC = getTopRelativeToScreenTop(this) - getTopRelativeToScreenTop(m);
            int viewHeight = m.getContentHeight();
            if (this._selectedRow == this._firstRowIndex) {
               this._isHeaderNotFullyVisible = false;
               if (!this.isRowFullyVisible(this._headerRowY_SC, 0, viewHeight)) {
                  this.invalidate();
                  return;
               }
            } else {
               int selectedRowY_SC = this.getY(this._selectedRow) + this._headerRowY_SC;
               int scrollDelta = this.getScrollDeltaToMakeRowVisible(selectedRowY_SC, viewHeight);
               this._isHeaderNotFullyVisible = !this.isRowFullyVisible(this._headerRowY_SC, scrollDelta, viewHeight);
               if (this._isHeaderNotFullyVisible) {
                  int vs = m.getVerticalScroll();
                  int newVS = vs;
                  if (scrollDelta > 0) {
                     int maxHeightOfRowsThatFitInScreen = m.getContentHeight() / this._rowHeight * this._rowHeight;
                     newVS = vs + selectedRowY_SC - maxHeightOfRowsThatFitInScreen + this._rowHeight;
                  } else if (scrollDelta < 0) {
                     newVS = vs + selectedRowY_SC - this._rowHeight;
                  } else {
                     int topVisibleRowIndex = this.getTopVisibleRowIndex(this._headerRowY_SC, viewHeight);
                     int topVisibleRowY_SC = this.getY(topVisibleRowIndex) + this._headerRowY_SC;
                     if (this.isRowFullyVisible(topVisibleRowY_SC, 0, viewHeight)) {
                        if (topVisibleRowIndex == this._selectedRow) {
                           newVS = vs - this._rowHeight;
                        }
                     } else {
                        scrollDelta = this.isRowFullyVisible(selectedRowY_SC, topVisibleRowY_SC, viewHeight) ? 0 : this._rowHeight;
                        newVS = vs + topVisibleRowY_SC + scrollDelta;
                     }
                  }

                  if (newVS != vs) {
                     this._paintEnabled = false;
                     m.setVerticalScroll(newVS);
                     this._paintEnabled = true;
                     this._headerRowY_SC += vs - newVS;
                     this.invalidate();
                  }
               }
            }
         }
      }
   }

   private void scrollToColWithFocus() {
      int colIndex = this._selectedCol;
      if (colIndex == -1) {
         colIndex = this._firstSelectableColIndex;
      } else if (!this._table.getColumn(colIndex).isSelectable()) {
         this._selectedCol = colIndex = this._table.findNextSelectableColumn(colIndex, true);
      }

      IntVector newVisibleCols = this._table.getVisibleColumns(colIndex, true);
      if (!isEqual(this._visibleCols, newVisibleCols)) {
         this._visibleCols.setSize(newVisibleCols.size());
         newVisibleCols.copyInto(this._visibleCols.getArray());
      }
   }

   private void drawRow(Graphics graphics, int rowIndex, int y, int width, int height) {
      FontStyleColor rowStyle = this._table.getRowStyle(rowIndex);
      int visibleColsLength = this._visibleCols.size();
      if (rowStyle != null) {
         graphics.pushContext(this._tableX, y, width, height, 0, 0);
         this.setStyle(graphics, rowStyle);
      }

      for (int j = 0; j < visibleColsLength; j++) {
         ColumnData colData = this._table.getColumn(this._visibleCols.elementAt(j));
         int x = this._gridX.elementAt(j);
         int colWidth = this._gridX.elementAt(j + 1) - x - 1;
         FontStyleColor superStyle = colData.getSuperStyle();
         FontStyleColor colStyle = colData.getRowStyle(rowIndex);
         boolean isCellStyleSet = superStyle != null || colStyle != null || colData.isFrozen();
         if (isCellStyleSet) {
            graphics.pushContext(x, y, colWidth + 2, this._rowHeight + 1, 0, 0);
            if (superStyle != null) {
               this.setStyle(graphics, superStyle);
            }

            if (colStyle != null) {
               this.setStyle(graphics, colStyle);
            }

            if (colData.isFrozen()) {
               this.setStyle(graphics, TableStyle.FROZEN_STYLE);
            }
         }

         FontAccentsUtil.drawText(graphics, colData.getCell(rowIndex), x + 1, y + 1, colWidth);
         if (isCellStyleSet) {
            graphics.popContext();
         }
      }

      if (rowStyle != null) {
         graphics.popContext();
      }
   }

   private void calcGridX(int tableWidth) {
      int visibleColsLength = this._visibleCols.size();
      int partVisibleColIndex = this._table.getPartVisibleColIndex();
      int index = -1;
      int x = this._tableX;
      this._gridX.setSize(visibleColsLength + 1);
      if (partVisibleColIndex != -1) {
         index = Arrays.binarySearch(this._visibleCols.getArray(), partVisibleColIndex, 0, this._visibleCols.size());

         int i;
         for (i = 0; i < index; i++) {
            this._gridX.setElementAt(x, i);
            x += this._table.getColumn(this._visibleCols.elementAt(i)).getWidth();
         }

         this._gridX.setElementAt(x, i);
         x += this._table.getPartVisibleColWidth();
      }

      for (int i = index + 1; i < visibleColsLength; i++) {
         this._gridX.setElementAt(x, i);
         x += this._table.getColumn(this._visibleCols.elementAt(i)).getWidth();
      }

      this._gridX.setElementAt(this._tableX + tableWidth - 1, visibleColsLength);
   }

   private int getScrollDeltaToMakeRowVisible(int rowY_SC, int viewHeight) {
      if (rowY_SC < 0) {
         return rowY_SC;
      }

      int rowY2_SC = rowY_SC + this._rowHeight;
      int screenY2 = viewHeight - 1;
      return rowY2_SC > screenY2 ? rowY2_SC - screenY2 : 0;
   }

   private boolean isRowFullyVisible(int rowY_SC, int scrollDelta, int viewHeight) {
      int rowY_SC_afterScroll = rowY_SC - scrollDelta;
      return rowY_SC_afterScroll >= 0 && rowY_SC_afterScroll + this._rowHeight < viewHeight;
   }

   private int getTopVisibleRowIndex(int tableY_SC, int viewHeight) {
      if (tableY_SC >= viewHeight) {
         throw new Object("Table is not visible");
      } else if (tableY_SC > 0) {
         return this._firstRowIndex;
      } else {
         int tableY2_SC = tableY_SC + this.getContentHeight() - 1;
         if (tableY2_SC < 0) {
            throw new Object("Table is not visible");
         } else if (tableY2_SC == 0) {
            return this._lastRowIndex;
         } else {
            return this._isHeaderVisible
               ? this._numOfRows - (tableY2_SC / this._rowHeight + (tableY2_SC % this._rowHeight > 0 ? 1 : 0)) - 1
               : this._numOfRows - (tableY2_SC / this._rowHeight + (tableY2_SC % this._rowHeight > 0 ? 1 : 0)) - 0;
         }
      }
   }

   private void setStyle(Graphics graphics, FontStyleColor style) {
      if (style.isBackgroundColor()) {
         graphics.setBackgroundImage(null, 0, 0);
         graphics.setBackgroundColor(style.getBackgroundColor());
         graphics.clear();
      }

      if (style.isForegroundColor()) {
         graphics.setColor(style.getForegroundColor());
      }

      if (style.isFontStyle()) {
         graphics.setFont(this._table.getFont().derive(style.getFontStyle()));
      }
   }

   private int getRowIndex(int y) {
      int contentY2 = this.getContentHeight() - 1;
      if (y < 0 || y > contentY2) {
         throw new Object("y must be between 0 and height - 1");
      }

      if (y == contentY2) {
         return this._lastRowIndex;
      }

      int rowIndex = y / this._rowHeight;
      if (this._isHeaderVisible) {
         rowIndex--;
      }

      return rowIndex;
   }

   private int getY(int rowIndex) {
      if (rowIndex >= this._firstRowIndex && rowIndex <= this._lastRowIndex) {
         return (this._isHeaderVisible ? rowIndex + 1 : rowIndex) * this._rowHeight;
      } else {
         throw new Object("Row index must be between -1 (if header is visible) or 0 and (number of rows - 1)");
      }
   }

   private int getX(int colIndex) {
      if (colIndex == -1) {
         if (this._isRowSelectorVisible) {
            return 0;
         } else {
            throw new Object("colIndex can not be -1 when row selector is not visible");
         }
      } else {
         int visibleColsIndex = Arrays.binarySearch(this._visibleCols.getArray(), colIndex, 0, this._visibleCols.size());
         if (visibleColsIndex < 0) {
            throw new Object("colIndex corresponds to non visible column");
         } else {
            return this._table.getWidthOfColumns(this._visibleCols, 0, visibleColsIndex) + this._tableX;
         }
      }
   }

   private int getColWidth(int colIndex) {
      return colIndex == -1 ? this.getContentWidth() : this._table.getColumn(colIndex).getWidth() + 1;
   }

   private boolean isAltPressed(int status) {
      return (status & 1) != 0;
   }

   private void viewCell() {
      _VIEW_CELL_DIALOG.setText(this.getSelectedText());
      _VIEW_CELL_DIALOG.show();
   }

   private String getSelectedText() {
      return this._table.getColumn(this._selectedCol).getCell(this._selectedRow);
   }

   private void setColFrozenState(boolean isFrozen) {
      this._table.getColumn(this._selectedCol).setFrozen(isFrozen);
      this.fieldChangeNotify(0);
      this.tableLayoutUpdate();
      this.invalidate();
   }

   private void init() {
      this.setVisibilityProperties();
      this._numOfRows = this._table.getNumOfRows();
      this._lastColIndex = this._table.getNumOfCols() - 1;
      if (this._isHeaderVisible) {
         this._numOfRows++;
         this._firstRowIndex = -1;
         this._lastRowIndex = this._numOfRows - 2;
      } else {
         this._firstRowIndex = 0;
         this._lastRowIndex = this._numOfRows - 1;
      }

      if (this._isRowSelectorVisible) {
         this._firstColIndex = -1;
      } else {
         this._firstColIndex = 0;
      }
   }

   private void tableLayoutUpdate() {
      this._table.layout();
      this.setFirstAndLastSelectableColIndexes();
   }

   private void setVisibilityProperties() {
      this._isHeaderVisible = this._table.isHeaderVisible();
      this._isGridVisible = this._table.isGridVisible();
      this._isRowSelectorVisible = this._table.isRowSelectorVisible();
      this._tableX = this._isRowSelectorVisible ? 5 : 0;
   }

   private void setFirstAndLastSelectableColIndexes() {
      this._firstSelectableColIndex = this._table.findNextSelectableColumn(-1, true);
      this._lastSelectableColIndex = this._table.findNextSelectableColumn(this._lastColIndex, true);
      this.scrollToColWithFocus();
   }

   private boolean isLeftArrow() {
      return this._visibleCols.elementAt(0) != this._firstSelectableColIndex || this._firstSelectableColIndex == this._table.getPartVisibleColIndex();
   }

   private boolean isRigthArrow() {
      return this._visibleCols.lastElement() != this._lastSelectableColIndex || this._lastSelectableColIndex == this._table.getPartVisibleColIndex();
   }

   private int getHorizontalArrowsTop(int arrowHeight) {
      Manager m = this.getScrollingManager();
      return m == null
         ? 0
         : MathUtilities.clamp(
            0, m.getVisibleHeight() - getTopRelativeToScreenTop(this) + getTopRelativeToScreenTop(m) - arrowHeight, this.getContentHeight() - arrowHeight
         );
   }

   private static boolean isEqual(IntVector v1, IntVector v2) {
      int size;
      if (v1 != null && v2 != null && (size = v1.size()) == v2.size()) {
         for (int i = size - 1; i >= 0; i--) {
            if (v1.elementAt(i) != v2.elementAt(i)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static int getTopRelativeToScreenTop(Field field) {
      int top = field.getContentTop();

      for (Manager manager = field.getManager(); manager != null; manager = manager.getManager()) {
         top += manager.getContentTop() - manager.getVerticalScroll();
      }

      return top;
   }
}
