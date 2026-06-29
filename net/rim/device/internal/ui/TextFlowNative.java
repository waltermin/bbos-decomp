package net.rim.device.internal.ui;

import net.rim.vm.Array;

public final class TextFlowNative {
   private StringBuffer _text;
   private int[] _regionStartOffsets;
   private int[] _regionEndOffsets;
   private TextFlowRegion[] _regions;
   private short[] _regionFlags;
   private int[] _regionParentIds;
   private short _currentCellId;
   TextFlowNative$Lines _lines;
   private int _endTextPosition;
   private int _totalHeight;
   private int _inlineCount;
   private int[] _totalHeightStack;
   private int _startLine;
   private int _totalHeightTop;
   private int _maxXOffset;
   private int _endLine;
   private int _startYPos;
   private int _lastRegionIndex;

   public TextFlowNative(
      StringBuffer text, int[] regionStartOffsets, int[] regionEndOffsets, TextFlowRegion[] regions, short[] regionFlags, int[] regionParentIds
   ) {
      this._text = text;
      this._regionStartOffsets = regionStartOffsets;
      this._regionEndOffsets = regionEndOffsets;
      this._regions = regions;
      this._regionFlags = regionFlags;
      this._regionParentIds = regionParentIds;
      this._lines = new TextFlowNative$Lines();
      this._totalHeightStack = new int[16];
      this.reset();
   }

   public final void reset() {
      this._lines.reset();
      this._endTextPosition = -1;
      this._totalHeight = 0;
      this._inlineCount = 0;
      this._currentCellId = 0;
      this._startLine = -1;
      this._totalHeightTop = 0;
      this._endLine = 0;
      this._startYPos = 0;
      this._lastRegionIndex = 0;
      this._maxXOffset = 0;
   }

   public final void appendVerticalSpace(int aStartYPos, int aStartLine, int aHeight, short aXOffset, short aWidth) {
      if (this._startLine == -1 && aHeight > 0) {
         this._startLine = aStartLine;
         this._startYPos = aStartYPos;
      }

      this._totalHeight += aHeight;
      this._lines.appendVerticalSpace(aHeight, aXOffset, aWidth, this._currentCellId);
   }

   public final void appendZeroHeightCharacters(int aStartYPos, int aStartLine, int aChars, short aXOffset, short aWidth) {
      if (this._startLine == -1 && aChars > 0) {
         this._startLine = aStartLine;
         this._startYPos = aStartYPos;
      }

      this._lines.appendZeroHeightCharacters(aChars, aXOffset, aWidth, this._currentCellId);
   }

   public final void appendZeroHeightZeroWidthCharacter(int aStartYPos, int aStartLine, short aXOffset) {
      if (this._startLine == -1) {
         this._startLine = aStartLine;
         this._startYPos = aStartYPos;
      }

      this._lines.appendZeroHeightZeroWidthCharacter(aXOffset, this._currentCellId);
   }

   public final void pushCell(short nextCellId) {
      if (this._totalHeightTop + 1 >= this._totalHeightStack.length) {
         Array.resize(this._totalHeightStack, this._totalHeightStack.length << 1);
      }

      this._totalHeightTop++;
      this._totalHeightStack[this._totalHeightTop] = this._totalHeight;
      this._currentCellId = nextCellId;
      this._totalHeight = 0;
   }

   public final void popCell(short parentCellId) {
      this._totalHeight = this._totalHeightStack[this._totalHeightTop];
      this._totalHeightStack[this._totalHeightTop] = 0;
      this._totalHeightTop--;
      this._currentCellId = parentCellId;
   }

   public final native void wordWrap(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public final int getLineCount() {
      return this._lines.getCount();
   }

   public final int getEndTextPosition() {
      return this._endTextPosition;
   }

   public final int getInlineCount() {
      return this._inlineCount;
   }

   public final int getStartLine() {
      return this._startLine;
   }

   public final int getStartYPos() {
      return this._startYPos;
   }

   public final void setEndLine(int endLine) {
      this._endLine = endLine;
   }

   public final int getEndLine() {
      return this._endLine;
   }

   public final void setMaxXOffset(int xPos) {
      this._maxXOffset = xPos;
   }

   public final int getMaxXOffset() {
      return this._maxXOffset;
   }

   public final int getCellYPos() {
      return this._totalHeightStack[this._totalHeightTop];
   }

   public final int getCellHeight() {
      return this._totalHeight;
   }

   public final TextFlowNative$Lines getLines() {
      return this._lines;
   }

   public final short[] getLineLengths() {
      return this._lines.getLengths();
   }

   public final short[] getLineXOffsets() {
      return this._lines.getXOffsets();
   }

   public final short[] getLineWidth(int aLineIndex) {
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

   public final void append(int aHeight, TextFlowNative$Lines aLines, int aStartLine, int aLineCount) {
      this._lines.append(aLines, aStartLine, aLineCount);
      this._totalHeight += aHeight;
   }
}
