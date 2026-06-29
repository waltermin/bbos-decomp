package net.rim.device.apps.api.utility.columninfo;

import net.rim.device.api.util.Arrays;

public class ColumnInformation {
   private int[] _columnWidths;
   private int[] _columnGaps;
   private boolean[] _columnFilled;
   private int[] _columnOffsets;
   private int[] _columnMap;
   private boolean _columnOffsetsValid;

   public ColumnInformation(int numberOfColumns) {
      this._columnWidths = new int[numberOfColumns];
      this._columnGaps = new int[numberOfColumns];
      this._columnOffsets = new int[numberOfColumns];
      this._columnFilled = new boolean[numberOfColumns];
      this._columnMap = new int[numberOfColumns];
      int i = 0;

      while (i < numberOfColumns) {
         this._columnMap[i] = i++;
      }
   }

   public void resetColumnWidthsGapsAndOffsets() {
      Arrays.fill(this._columnWidths, 0);
      Arrays.fill(this._columnGaps, 0);
      Arrays.fill(this._columnOffsets, 0);
      this._columnOffsetsValid = false;
   }

   public void mapColumn(int columnNumber, int mappedColumnNumber) {
      this._columnMap[columnNumber] = mappedColumnNumber;
   }

   public void setColumnWidth(int columnNumber, int width) {
      this.setColumnWidthGap(columnNumber, width, this._columnGaps[columnNumber]);
   }

   public void setColumnWidthGap(int columnNumber, int width, int gap) {
      columnNumber = this._columnMap[columnNumber];
      this._columnWidths[columnNumber] = width;
      this._columnGaps[columnNumber] = gap;
      this._columnOffsetsValid = false;
   }

   public int getColumnWidth(int columnNumber) {
      columnNumber = this._columnMap[columnNumber];
      return this._columnWidths[columnNumber];
   }

   public int getColumnGap(int columnNumber) {
      columnNumber = this._columnMap[columnNumber];
      return this._columnGaps[columnNumber];
   }

   public int getColumnOffset(int columnNumber) {
      columnNumber = this._columnMap[columnNumber];
      if (!this._columnOffsetsValid) {
         int curr_offset = 0;
         this._columnOffsets[0] = curr_offset;
         int n = this._columnOffsets.length - 1;

         for (int i = 0; i < n; i++) {
            int width = this._columnWidths[i];
            if (width > 0) {
               curr_offset += width + this._columnGaps[i];
            }

            this._columnOffsets[i + 1] = curr_offset;
         }

         this._columnOffsetsValid = true;
      }

      return this._columnOffsets[columnNumber];
   }

   public void clearColumnFilled() {
      for (int i = this._columnFilled.length - 1; i >= 0; i--) {
         this._columnFilled[i] = false;
      }
   }

   public void setColumnFilled(int columnNumber) {
      columnNumber = this._columnMap[columnNumber];
      this._columnFilled[columnNumber] = true;
   }

   public void clearColumnFilled(int columnNumber) {
      columnNumber = this._columnMap[columnNumber];
      this._columnFilled[columnNumber] = false;
   }

   public boolean getColumnFilled(int columnNumber) {
      columnNumber = this._columnMap[columnNumber];
      return this._columnFilled[columnNumber] || this._columnWidths[columnNumber] == 0;
   }
}
