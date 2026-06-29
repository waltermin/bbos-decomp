package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;

final class RowHeightAdjuster implements VariableRowHeightProvider {
   private IntIntHashtable _rowHeightExceptions = new IntIntHashtable();
   private int _rowHeightExceptionsSum;
   private int _rowHeight;
   private int _size;
   private int _lastStartNodeIndex;
   private int _lastStartNodeYPos;
   private TextMetrics _metrics = new TextMetrics();
   private int _currentY;
   private int _currentHeight;
   private int _currentAdjustment;
   private int _maxHeight;
   private int _maxAdjustment;
   private boolean _initialized;

   public final int getHeight() {
      return Math.max(this._rowHeight, (this._size - this._rowHeightExceptions.size()) * this._rowHeight + this._rowHeightExceptionsSum);
   }

   public final void clear() {
      this._rowHeightExceptions.clear();
      this._rowHeightExceptionsSum = 0;
   }

   public final int getRowForY(int y) {
      if (!this.hasVariableLineHeights()) {
         return Math.max(0, y / this._rowHeight);
      }

      int row = 0;
      int distance = Math.abs(this._lastStartNodeYPos - y);
      if (y <= distance) {
         int currY = this.getRowHeight(0);

         while (currY < y) {
            currY += this.getRowHeight(++row);
         }
      } else if (this.getHeight() - this.getRowHeight(this._size - 1) - y <= distance) {
         row = this._size;

         for (int currY = this.getHeight(); currY >= y; currY -= this.getRowHeight(row)) {
            row--;
         }
      } else {
         row = this._lastStartNodeIndex;
         if (this._lastStartNodeYPos < y) {
            int currY = this._lastStartNodeYPos + this.getRowHeight(row);

            while (currY < y) {
               currY += this.getRowHeight(++row);
            }
         } else {
            for (int currY = this._lastStartNodeYPos; currY > y; currY -= this.getRowHeight(row)) {
               row--;
            }
         }
      }

      return row;
   }

   public final int getYForRow(int row) {
      int y = 0;
      if (row < 0) {
         return 0;
      }

      if (!this.hasVariableLineHeights()) {
         y = row * this._rowHeight;
      } else {
         int distance = Math.abs(this._lastStartNodeIndex - row);
         if (row <= distance) {
            for (int currRow = 0; currRow < row; currRow++) {
               y += this.getRowHeight(currRow);
            }
         } else if (this._size - row <= distance) {
            y = this.getHeight();

            for (int currRow = this._size - 1; currRow >= row; currRow--) {
               y -= this.getRowHeight(currRow);
            }
         } else {
            y = this._lastStartNodeYPos;
            if (this._lastStartNodeIndex < row) {
               for (int currRow = this._lastStartNodeIndex; currRow < row; currRow++) {
                  y += this.getRowHeight(currRow);
               }
            } else {
               for (int currRow = this._lastStartNodeIndex - 1; currRow >= row; currRow--) {
                  y -= this.getRowHeight(currRow);
               }
            }
         }
      }

      this._lastStartNodeIndex = row;
      this._lastStartNodeYPos = y;
      return y;
   }

   public final int getRowHeight(int row) {
      int height = this._rowHeightExceptions.get(row);
      if (height < 0) {
         height = this._rowHeight;
      }

      return height & 0xFF;
   }

   public final void setRowHeight(int height) {
      if (height != this._rowHeight) {
         this._rowHeight = height;
      }
   }

   public final void setSize(int size) {
      if (size != this._size) {
         this._size = size;
         this._rowHeightExceptions.clear();
         this._rowHeightExceptionsSum = 0;
      }
   }

   public final boolean setRowHeight(int row, int rowHeight) {
      if (rowHeight > this._maxHeight) {
         this._maxHeight = rowHeight;
      }

      return this.finish(row);
   }

   public final boolean hasVariableLineHeights() {
      return this._rowHeightExceptions.size() != 0;
   }

   public final void insertedRow(int addedRowNumber) {
      if (this.hasVariableLineHeights()) {
         IntIntHashtable newRowHeightExceptions = new IntIntHashtable(this._rowHeightExceptions.size());
         IntEnumeration enumeration = this._rowHeightExceptions.keys();

         while (enumeration.hasMoreElements()) {
            int rowNumber = enumeration.nextElement();
            int height = this._rowHeightExceptions.get(rowNumber);
            if (rowNumber >= addedRowNumber) {
               rowNumber++;
            }

            newRowHeightExceptions.put(rowNumber, height);
         }

         this._rowHeightExceptions = newRowHeightExceptions;
      }

      this._size++;
   }

   public final void deletedRow(int deletedRowNumber) {
      if (this.hasVariableLineHeights()) {
         IntIntHashtable newRowHeightExceptions = new IntIntHashtable(this._rowHeightExceptions.size());
         IntEnumeration enumeration = this._rowHeightExceptions.keys();

         while (enumeration.hasMoreElements()) {
            int rowNumber = enumeration.nextElement();
            int height = this._rowHeightExceptions.get(rowNumber);
            if (rowNumber > deletedRowNumber) {
               rowNumber--;
            } else if (rowNumber == deletedRowNumber) {
               continue;
            }

            newRowHeightExceptions.put(rowNumber, height);
         }

         this._rowHeightExceptions = newRowHeightExceptions;
      }

      this._size--;
   }

   public final void start(int row, int y) {
      this._initialized = true;
      int rowInfo = this.getRowInfo(row);
      this._currentHeight = rowInfo & 0xFF;
      this._currentAdjustment = rowInfo >> 8;
      this._currentY = y + this._currentAdjustment;
      this._maxHeight = this._maxAdjustment = 0;
   }

   public final boolean finish(int row) {
      this.checkReduceHeight();
      this._initialized = false;
      this._maxHeight = 0;
      return this.setRowHeight(row, this._currentHeight, this._currentAdjustment);
   }

   @Override
   public final int getAdjustedY(Font font, StringBuffer text, int offset, int len, int y) {
      if (!this._initialized) {
         this._initialized = true;
         this._currentHeight = 0;
         this._currentAdjustment = 0;
         this._currentY = y;
         this._maxHeight = this._maxAdjustment = 0;
      }

      if (len != 0) {
         font.measureText(text, offset, len, null, this._metrics);
      } else {
         this._metrics.reset();
      }

      return this.calculateYValue(font, y);
   }

   @Override
   public final int getAdjustedY(Font font, String text, int y) {
      if (!this._initialized) {
         this._initialized = true;
         this._currentHeight = 0;
         this._currentAdjustment = 0;
         this._currentY = y;
         this._maxHeight = this._maxAdjustment = 0;
      }

      if (text != null && text.length() != 0) {
         font.measureText(text, 0, text.length(), null, this._metrics);
      } else {
         this._metrics.reset();
      }

      return this.calculateYValue(font, y);
   }

   @Override
   public final int getAdjustedY(int currentY) {
      return this._currentY;
   }

   private final int getRowInfo(int row) {
      int height = this._rowHeightExceptions.get(row);
      if (height < 0) {
         height = this._rowHeight;
      }

      return height;
   }

   public RowHeightAdjuster() {
   }

   private final int calculateYValue(Font font, int y) {
      int baseline = font.getBaseline();
      int above = Math.max(-this._metrics.iBoundsTlY, baseline);
      int below = Math.max(this._metrics.iBoundsBrY, font.getDescent());
      int height = Math.max(font.getHeight(), above + below);
      int adjustment = above - baseline;
      if (height > this._maxHeight) {
         this._maxHeight = height;
      }

      if (this._maxHeight > this._currentHeight) {
         this._currentHeight = this._maxHeight;
      }

      if (adjustment > this._maxAdjustment) {
         this._maxAdjustment = adjustment;
      }

      if (this._maxAdjustment > this._currentAdjustment) {
         this._currentAdjustment = this._maxAdjustment;
      }

      this._currentY = y + this._currentAdjustment;
      return this._currentY;
   }

   public RowHeightAdjuster(int height, int size) {
      this._rowHeight = height;
      this._size = size;
   }

   private final boolean setRowHeight(int row, int rowHeight, int yAdjustment) {
      boolean changed = false;
      if (rowHeight <= 0) {
         throw new IllegalArgumentException("Invalid rowHeight");
      }

      if (this._rowHeightExceptions.containsKey(row)) {
         int oldRowHeight = this._rowHeightExceptions.get(row);
         if (oldRowHeight == (rowHeight | yAdjustment << 8)) {
            return changed;
         }

         this._rowHeightExceptionsSum -= oldRowHeight & 0xFF;
         this._rowHeightExceptions.remove(row);
         changed = true;
      }

      int height = rowHeight;
      if (height != this._rowHeight) {
         this._rowHeightExceptions.put(row, rowHeight | yAdjustment << 8);
         this._rowHeightExceptionsSum += height & 0xFF;
         changed = true;
      }

      return changed;
   }

   private final void checkReduceHeight() {
      if (this._maxHeight > 0
         && (this._maxAdjustment < this._currentAdjustment || this._currentHeight != this._maxHeight || this._currentHeight < this._rowHeight)) {
         this._currentHeight = Math.max(this._maxHeight, this._rowHeight);
         this._currentAdjustment = this._maxAdjustment;
         this._currentY = this._currentY - (this._currentAdjustment - this._maxAdjustment);
      }
   }
}
