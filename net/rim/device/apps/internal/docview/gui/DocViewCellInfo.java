package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Arrays;

public final class DocViewCellInfo {
   private int[] _regionsOffset = new int[2];
   private int[] _regionsFontType = new int[1];
   private int[] _regionsForeColor = new int[1];
   private int[] _regionsBgColor = new int[1];
   private byte[] _regionIsParaDelimiter = new byte[1];
   private int _bgCellColor = -1;
   private int _skipRegionCount = -1;
   private boolean _complete;
   byte _textDirection = 0;

   DocViewCellInfo(
      int startIndex,
      int length,
      int fontStyleType,
      int foreColor,
      int bgColor,
      int fontSize,
      boolean isCompleteCell,
      byte textDirection,
      boolean isParaSeparator
   ) {
      this.initialize(startIndex, length, fontStyleType, foreColor, bgColor, fontSize, isCompleteCell, textDirection, isParaSeparator);
   }

   DocViewCellInfo(int cellBgColor, boolean isCompleteCell) {
      this.initialize(0, 0, 0, -1, -1, 0, isCompleteCell, (byte)0, false);
      this.setCellBgColor(cellBgColor);
   }

   final void initialize(
      int startIndex,
      int length,
      int fontStyleType,
      int foreColor,
      int bgColor,
      int fontSize,
      boolean isCompleteCell,
      byte textDirection,
      boolean isParaSeparator
   ) {
      this._regionsOffset[0] = startIndex;
      this._regionsOffset[1] = length;
      this._regionsFontType[0] = fontStyleType;
      this._regionsForeColor[0] = foreColor;
      this._regionsBgColor[0] = bgColor;
      this._regionIsParaDelimiter[0] = (byte)(isParaSeparator ? 1 : 0);
      this._textDirection = textDirection;
      this.setComplete(isCompleteCell);
   }

   final void setTextDirection(byte textDirection) {
      this._textDirection = textDirection;
   }

   final void addParaSeparator(int startIndex) {
      this.addRegionInternal(startIndex, 1, 0, -1, -1, 0, true);
   }

   final void addTextRegion(int startIndex, int length, int fontStyleType, int foreColor, int bgColor, int fontSize, boolean isCompleteCell) {
      this.setComplete(isCompleteCell);
      if (length > 0) {
         this.addRegionInternal(startIndex, length, fontStyleType, foreColor, bgColor, fontSize, false);
      }
   }

   private final void setComplete(boolean isCompleteCell) {
      if (!this._complete) {
         if (isCompleteCell) {
            this._complete = true;
            return;
         }
      } else if (!isCompleteCell) {
         this._skipRegionCount = this._regionsFontType.length;
         this._complete = false;
      }
   }

   private final void addRegionInternal(int startIndex, int length, int fontStyleType, int foreColor, int bgColor, int fontSize, boolean isParaDelimiter) {
      int regionsOffsetLength = this._regionsOffset.length;
      int regionsCount = regionsOffsetLength >> 1;
      boolean append = this._skipRegionCount == -1;
      int regionToCompareIdx = append ? regionsCount - 1 : regionsCount - this._skipRegionCount;
      boolean mightBeIdenticalAndConsecutive = !isParaDelimiter && this._regionIsParaDelimiter[regionToCompareIdx] == 0;
      if (mightBeIdenticalAndConsecutive) {
         if (append) {
            if (this._regionsOffset[regionToCompareIdx * 2] + this._regionsOffset[regionToCompareIdx * 2 + 1] != startIndex) {
               mightBeIdenticalAndConsecutive = false;
            }
         } else if (startIndex + length != this._regionsOffset[regionToCompareIdx * 2]) {
            mightBeIdenticalAndConsecutive = false;
         }
      }

      if (mightBeIdenticalAndConsecutive
         && this._regionsFontType[regionToCompareIdx] == fontStyleType
         && this._regionsForeColor[regionToCompareIdx] == foreColor
         && this._regionsBgColor[regionToCompareIdx] == bgColor) {
         if (append) {
            this._regionsOffset[regionsOffsetLength - 1] = this._regionsOffset[regionsOffsetLength - 1] + length;
         } else {
            this._regionsOffset[(regionsCount - this._skipRegionCount) * 2 + 1] = this._regionsOffset[(regionsCount - this._skipRegionCount) * 2 + 1] + length;
         }
      } else {
         if (append) {
            Arrays.add(this._regionsOffset, startIndex);
            Arrays.add(this._regionsOffset, length);
         } else {
            Arrays.insertAt(this._regionsOffset, length, (regionsCount - this._skipRegionCount) * 2);
            Arrays.insertAt(this._regionsOffset, startIndex, (regionsCount - this._skipRegionCount) * 2);
         }

         int newIndex = append ? regionsCount : regionsCount - this._skipRegionCount;
         Arrays.insertAt(this._regionsFontType, fontStyleType, newIndex);
         Arrays.insertAt(this._regionsForeColor, foreColor, newIndex);
         Arrays.insertAt(this._regionsBgColor, bgColor, newIndex);
         Arrays.insertAt(this._regionIsParaDelimiter, (byte)(isParaDelimiter ? 1 : 0), newIndex);
      }
   }

   final void setCellBgColor(int color) {
      this._bgCellColor = color;
   }

   public final int[] getRegionsOffsetIndeces() {
      return this._regionsOffset;
   }

   public final int[] getRegionsFontType() {
      return this._regionsFontType;
   }

   public final int[] getRegionsForeColor() {
      return this._regionsForeColor;
   }

   public final int[] getRegionsBgColor() {
      return this._regionsBgColor;
   }

   public final int getCellContentLength() {
      int length = 0;
      int regionLength = this._regionsOffset.length;

      for (int i = 0; i <= regionLength - 2; i += 2) {
         length += this._regionsOffset[i + 1];
      }

      return length;
   }

   public final boolean hasText(boolean considerParaDelimiters) {
      int regionLength = this._regionsOffset.length;

      for (int i = 0; i <= regionLength - 2; i += 2) {
         if ((considerParaDelimiters || this._regionIsParaDelimiter[i >> 1] != 1) && this._regionsOffset[i + 1] > 0) {
            return true;
         }
      }

      return false;
   }

   public final int getCellBgColor() {
      return this._bgCellColor;
   }
}
