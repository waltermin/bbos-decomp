package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.Matrix;

public class Table extends VerticalIndentFieldManager {
   protected Matrix _cells = new Matrix();
   private int[] _xOffsets;
   private int[] _yOffsets;
   private int _currentRow;
   private int _currentCol;
   private int _currentFocusRow;
   private int _currentFocusCol;
   private int _borders;
   private int _drawGeneration;
   public static final int CELL_PADDING;
   public static final int CELL_BORDER_WIDTH;
   public static final int CELL_MAX_INNER_WIDTH;
   public static final byte BORDER_TOP;
   public static final byte BORDER_BOTTOM;
   public static final byte BORDER_LEFT;
   public static final byte BORDER_RIGHT;
   public static final byte RULES_ROWS;
   public static final byte RULES_COLS;
   public static final byte RULES_GROUP;
   private static Table$ColSpanComparator _colSpanComp = new Table$ColSpanComparator();
   private static Table$RowSpanComparator _rowSpanComp = new Table$RowSpanComparator();

   public Table(byte borders) {
      this._borders = borders;
      this._currentFocusRow = 0;
      this._currentFocusCol = 0;
   }

   public void addRow() {
      if (this._currentRow != 0 || this._currentCol != 0) {
         this._currentRow++;
         this._currentCol = 0;
      }
   }

   public TableCell addCell(int rowSpan, int colSpan, int alignment) {
      while (
         this._currentCol < this._cells.sizeX()
            && this._currentRow < this._cells.sizeY()
            && this._cells.elementAt(this._currentCol, this._currentRow) != null
            && ((TableCell)this._cells.elementAt(this._currentCol, this._currentRow)).getRowSpan() > 1
      ) {
         this._currentCol++;
      }

      int endRow = this._currentRow + rowSpan - 1;
      int endCol = this._currentCol + colSpan - 1;
      TableCell newCell = new TableCell(this, this._currentRow, this._currentCol, rowSpan, colSpan, alignment);

      for (int x = this._currentCol; x <= endCol; x++) {
         for (int y = this._currentRow; y <= endRow; y++) {
            this._cells.setElementAt(newCell, x, y);
         }
      }

      this._currentCol = endCol + 1;
      this.add(newCell);
      return newCell;
   }

   @Override
   protected void sublayout(int width, int height) {
      if (!this.areLargeTablesOn()) {
         super.sublayout(width, height);
      } else {
         int numRows = this.getNumRows();
         int numCols = this.getNumCols();
         int numFields = this.getFieldCount();
         int[] widths = new int[numCols];
         int[] heights = new int[numRows];
         TableCell[] cells = new TableCell[numFields];

         for (int i = 0; i < numFields; i++) {
            cells[i] = (TableCell)this.getField(i);
         }

         Arrays.sort(cells, _colSpanComp);

         for (int var26 = 0; var26 < numFields; var26++) {
            TableCell cell = cells[var26];
            int startCol = cell.getStartCol();
            int colSpan = cell.getColSpan();
            int minCellWidth = cell.getPreferredCellWidth(128) + 1;
            int currentCellWidth = 0;
            int numNonZeroWidths = 0;

            for (int x = startCol; x < startCol + colSpan; x++) {
               if (widths[x] != 0) {
                  numNonZeroWidths++;
                  currentCellWidth += widths[x];
               }
            }

            if (currentCellWidth < minCellWidth) {
               int excess = minCellWidth - currentCellWidth;
               if (numNonZeroWidths > 0) {
                  int excessPerNonZeroColumn = excess / numNonZeroWidths;

                  for (int var30 = startCol; var30 < startCol + colSpan; var30++) {
                     if (widths[var30] != 0) {
                        widths[var30] += excessPerNonZeroColumn;
                     }
                  }
               } else {
                  int excessPerColumn = excess / colSpan;

                  for (int var29 = startCol; var29 < startCol + colSpan; var29++) {
                     widths[var29] += excessPerColumn;
                  }
               }
            }
         }

         this._xOffsets = new int[numCols + 1];
         this._xOffsets[0] = 0;
         int currentXOffset = 0;

         for (int x = 0; x < numCols; x++) {
            currentXOffset += 1 + widths[x] + 1 + 1;
            this._xOffsets[x + 1] = currentXOffset;
         }

         Arrays.sort(cells, _rowSpanComp);

         for (int var27 = 0; var27 < numFields; var27++) {
            TableCell cell = cells[var27];
            int startCol = cell.getStartCol();
            int colSpan = cell.getColSpan();
            int actualCellWidth = this._xOffsets[startCol + colSpan] - this._xOffsets[startCol] - 1;
            int minCellHeight = cell.getPreferredCellHeight(actualCellWidth);
            int rowSpan = cell.getRowSpan();
            int startRow = cell.getStartRow();
            int currentCellHeight = 0;
            int numNonZeroHeights = 0;

            for (int y = startRow; y < startRow + rowSpan; y++) {
               if (heights[y] != 0) {
                  numNonZeroHeights++;
                  currentCellHeight += heights[y];
               }
            }

            if (currentCellHeight < minCellHeight) {
               int excess = minCellHeight - currentCellHeight;
               if (numNonZeroHeights > 0) {
                  int excessPerNonZeroRow = excess / numNonZeroHeights;

                  for (int var33 = startRow; var33 < startRow + rowSpan; var33++) {
                     if (heights[var33] != 0) {
                        heights[var33] += excessPerNonZeroRow;
                     }
                  }
               } else {
                  int excessPerRow = excess / rowSpan;

                  for (int var32 = startRow; var32 < startRow + rowSpan; var32++) {
                     heights[var32] += excessPerRow;
                  }
               }
            }
         }

         this._yOffsets = new int[numRows + 1];
         int currentYOffset = 0;
         this._yOffsets[0] = 0;

         for (int y = 0; y < numRows; y++) {
            currentYOffset += 1 + heights[y] + 1 + 1;
            this._yOffsets[y + 1] = currentYOffset;
         }

         for (int var28 = 0; var28 < numFields; var28++) {
            TableCell cell = (TableCell)this.getField(var28);
            int startCol = cell.getStartCol();
            int colSpan = cell.getColSpan();
            int startRow = cell.getStartRow();
            int rowSpan = cell.getRowSpan();
            int cellX = this._xOffsets[startCol] + 1 + 1;
            int cellY = this._yOffsets[startRow] + 1 + 1;
            int cellSizeX = this._xOffsets[startCol + colSpan] - cellX - 1;
            int cellSizeY = this._yOffsets[startRow + rowSpan] - cellY - 1;
            this.setPositionChild(cell, cellX, cellY);
            this.layoutChild(cell, cellSizeX, cellSizeY);
         }

         width = this._xOffsets[numCols] + 1;
         height = this._yOffsets[numRows] + 1;
         this.setExtent(width, height);
      }
   }

   @Override
   protected void subpaint(Graphics g) {
      if (!this.areLargeTablesOn()) {
         super.subpaint(g);
      } else {
         this._drawGeneration++;
         int right = this.getWidth() - 1;
         int bottom = this.getHeight() - 1;
         if ((this._borders & 1) != 0) {
            g.drawLine(0, 0, right, 0);
         }

         if ((this._borders & 2) != 0) {
            g.drawLine(0, bottom, right, bottom);
         }

         if ((this._borders & 4) != 0) {
            g.drawLine(0, 0, 0, bottom);
         }

         if ((this._borders & 8) != 0) {
            g.drawLine(right, 0, right, bottom);
         }

         XYRect clip = g.getClippingRect();
         int startDrawCol = this.findIndex(this._xOffsets, 0, clip.x);
         int endDrawCol = this.findIndex(this._xOffsets, startDrawCol, clip.X2());
         int startDrawRow = this.findIndex(this._yOffsets, 0, clip.y);
         int endDrawRow = this.findIndex(this._yOffsets, startDrawRow, clip.Y2());
         if (endDrawCol < this._cells.sizeX() && endDrawRow < this._cells.sizeY()) {
            for (int i = startDrawCol; i <= endDrawCol; i++) {
               for (int j = startDrawRow; j <= endDrawRow; j++) {
                  TableCell cell = (TableCell)this._cells.elementAt(i, j);
                  if (cell == null) {
                     cell = new TableCell(this, j, i, 1, 1, 0);
                     this._cells.setElementAt(cell, i, j);
                     this.add(cell);
                  }

                  if (cell.requiresDraw(this._drawGeneration)) {
                     g.pushRegion(cell.getExtent());
                     cell.paint(g);
                     g.popContext();
                     int startCol = cell.getStartCol();
                     int colSpan = cell.getColSpan();
                     int startRow = cell.getStartRow();
                     int rowSpan = cell.getRowSpan();
                     int topLeftX = this._xOffsets[startCol];
                     int topLeftY = this._yOffsets[startRow];
                     if ((this._borders & 32) != 0 && cell.getStartCol() != 0) {
                        int bottomLeftY = this._yOffsets[startRow + rowSpan] - 1;
                        g.drawLine(topLeftX, topLeftY, topLeftX, bottomLeftY);
                     }

                     if ((this._borders & 16) != 0 && cell.getStartRow() != 0) {
                        int topRightX = this._xOffsets[startCol + colSpan] - 1;
                        g.drawLine(topLeftX, topLeftY, topRightX, topLeftY);
                     }
                  }
               }
            }
         }
      }
   }

   public int findIndex(int[] a, int startIndex, int value) {
      int i = startIndex + 1;

      while (i < a.length - 1 && a[i] <= value) {
         i++;
      }

      return i - 1;
   }

   public int getNumRows() {
      return this._cells.sizeY();
   }

   public int getNumCols() {
      return this._cells.sizeX();
   }

   @Override
   protected void onUnfocus() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      if (!this.areLargeTablesOn()) {
         return super.moveFocus(amount, status, time);
      }

      boolean notify;
      label98:
      for (notify = false; amount != 0; notify = true) {
         TableCell focus = (TableCell)this.getFieldWithFocus();
         if ((status & 1) == 0) {
            amount = focus.moveFocus(amount, status, time);
         }

         if (amount == 0) {
            break;
         }

         focus.onUnfocus();
         focus.focusChangeNotify(3);
         int oldFocusRow = this._currentFocusRow;
         int oldFocusCol = this._currentFocusCol;
         TableCell newFocus = null;

         do {
            if (amount > 0) {
               if ((status & 65536) != 0 || !Trackball.isSupported() && (status & 1) != 0) {
                  this._currentFocusCol++;
                  if (this._currentFocusCol >= this.getNumCols()) {
                     this._currentFocusCol = 0;
                     this._currentFocusRow++;
                  }
               } else {
                  this._currentFocusRow++;
               }
            } else if ((status & 65536) != 0 || !Trackball.isSupported() && (status & 1) != 0) {
               this._currentFocusCol--;
               if (this._currentFocusCol < 0) {
                  this._currentFocusCol = this.getNumCols() - 1;
                  this._currentFocusRow--;
               }
            } else {
               this._currentFocusRow--;
            }

            if (this._currentFocusRow >= this.getNumRows() || this._currentFocusRow < 0) {
               focus.onFocus(amount < 0 ? 1 : -1);
               focus.focusChangeNotify(1);
               this.setFieldWithFocus(focus);
               this._currentFocusRow = oldFocusRow;
               this._currentFocusCol = oldFocusCol;
               break label98;
            }

            newFocus = (TableCell)this._cells.elementAt(this._currentFocusCol, this._currentFocusRow);
         } while (newFocus == focus || !newFocus.isFocusable());

         newFocus.onFocus(amount < 0 ? -1 : 1);
         newFocus.focusChangeNotify(1);
         this.setFieldWithFocus(newFocus);
         oldFocusRow = this._currentFocusRow;
         oldFocusCol = this._currentFocusCol;
         amount = amount < 0 ? ++amount : --amount;
      }

      if (amount == 0 && notify) {
         this.focusChangeNotify(2);
      }

      return amount;
   }

   @Override
   protected void onFocus(int direction) {
      if (!this.areLargeTablesOn()) {
         super.onFocus(direction);
      } else if (direction != 0) {
         int numCells = this.getFieldCount();
         int currentCell = direction == 1 ? 0 : numCells - this.getNumCols();
         int searchDirection = direction;

         while (true) {
            TableCell newFocus = (TableCell)this.getField(currentCell);
            if (newFocus.isFocusable()) {
               this._currentFocusRow = newFocus.getStartRow();
               this._currentFocusCol = newFocus.getStartCol();
               newFocus.onFocus(direction);
               this.setFieldWithFocus(newFocus);
               return;
            }

            currentCell += searchDirection;
            if (currentCell < 0) {
               currentCell = numCells - this.getNumCols();
               searchDirection = 1;
            }

            Asserts.productionAssert(currentCell >= 0 && currentCell < numCells);
         }
      }
   }
}
