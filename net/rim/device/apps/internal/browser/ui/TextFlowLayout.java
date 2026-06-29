package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.TextFlowNative;
import net.rim.device.internal.ui.TextFlowNative$Lines;
import net.rim.vm.Array;

public final class TextFlowLayout {
   private TextFlowNative$Lines _lines = (TextFlowNative$Lines)(new Object());
   private int[] _lineCellStart = new int[0];
   private int[] _lineCellEnd = new int[0];
   private TextFlowNative _textFlowNative;
   private int _maxXOffset;

   public TextFlowLayout(TextFlowData textFlowData) {
      this._textFlowNative = textFlowData.getTextFlowNative();
   }

   public final TextFlowNative getTextFlowNative() {
      return this._textFlowNative;
   }

   public final int getMaxXOffset() {
      return this._maxXOffset;
   }

   public final void setMaxXOffset(int offset) {
      this._maxXOffset = offset;
   }

   public final void clear() {
      this._lines.reset();
      this._textFlowNative.reset();
      this._maxXOffset = 0;
   }

   public final void copyCellData(int aDestStartLine, int aDestLineCount, int aNumCells) {
      this._lines.replace(aDestStartLine, aDestLineCount, this._textFlowNative.getLines());
      this.updateStartsAndEnds(aNumCells);
   }

   private final void updateStartsAndEnds(int numCells) {
      if (this._lineCellStart.length < numCells) {
         int newsize = Math.max(numCells, this._lineCellStart.length + Array.getSectionSize(this._lineCellStart));
         Array.resize(this._lineCellStart, newsize);
         Array.resize(this._lineCellEnd, newsize);
      }

      Arrays.fill(this._lineCellStart, -1);
      Arrays.fill(this._lineCellEnd, -1);
      int n = this._lines.getCount();

      for (int i = 0; i < n; i++) {
         int cellId = this._lines.getCellIds()[i];
         if (cellId > 0) {
            if (this._lineCellStart[cellId - 1] == -1) {
               this._lineCellStart[cellId - 1] = i;
            }

            this._lineCellEnd[cellId - 1] = i;
         }
      }
   }

   public final int getLineCellStart(short cellId) {
      if (cellId <= 0) {
         return 0;
      }

      if (cellId > this._lineCellStart.length) {
         return this._lines.getCount();
      }

      int value = this._lineCellStart[cellId - 1];
      return value >= 0 ? value : this._lines.getCount();
   }

   public final int getLineCellEnd(short cellId) {
      int n = this._lines.getCount();
      if (n == 0) {
         return -1;
      } else {
         return cellId > 0 && cellId <= this._lineCellEnd.length ? this._lineCellEnd[cellId - 1] : n - 1;
      }
   }

   public final TextFlowNative$Lines getLines() {
      return this._lines;
   }

   public final int getLineCount() {
      return this._lines.getCount();
   }

   public final short[] getLineLengths() {
      return this._lines.getLengths();
   }

   public final short[] getLineXOffsets() {
      return this._lines.getXOffsets();
   }

   public final short[] getLineWidths() {
      return this._lines.getWidths();
   }

   public final short[] getLineWidthsNominal() {
      return this._lines.getWidthsNominal();
   }

   public final byte[] getLineFlags() {
      return this._lines.getFlags();
   }

   public final short[] getLineBaselines() {
      return this._lines.getBaselines();
   }

   public final short[] getLineHeights() {
      return this._lines.getHeights();
   }

   public final short[] getLineCellIds() {
      return this._lines.getCellIds();
   }

   public final byte[] getLineBidiState(int aLineIndex) {
      return this._lines.getBidiState(aLineIndex);
   }
}
