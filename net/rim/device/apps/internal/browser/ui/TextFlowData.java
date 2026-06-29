package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntStack;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.internal.ui.TextFlowNative;
import net.rim.device.internal.ui.TextFlowRegion;
import net.rim.vm.Array;

public final class TextFlowData {
   private StringBuffer _text;
   private boolean _appendFailed;
   private Font _defaultFont;
   private int _regionCount;
   private int[] _regionParentIds;
   private int[] _regionCellOOLIndex;
   private int[] _regionStartOffsets;
   private int[] _regionEndOffsets;
   private int[] _regionLengths;
   private int[] _regionSortedByY;
   private TextFlowRegion[] _regions;
   private short[] _regionFlags;
   private IntStack _currentRegionStack = new IntStack();
   private FontCache _fontCache;
   private int _focusRegionCount;
   private int[] _focusRegionStarts;
   private Object[] _focusRegionCookies;
   private long[] _focusRegionScalarCookies;
   private boolean _focusOn;
   private TextFlowData$RegionComparator _regionComparator = new TextFlowData$RegionComparator(this);
   private int _outoflineCount;
   private int[] _outoflineRegionStarts;
   private Object[] _outoflineObjects;
   private TextFlowCell[] _textFlowCells;
   private byte[] _outoflineFlags;
   private IntStack _nesting = new IntStack();
   private int _nextCellId = 1;
   private static final short REGION_INLINE = 64;
   private static final short REGION_OUTOFLINE = 128;
   private static final short REGION_TEXT = 256;
   private static final short REGION_DIRTY = 512;
   private static final byte OUTOFLINE_TYPE_MASK = 7;
   private static final byte OUTOFLINE_INVALID = 0;
   private static final byte OUTOFLINE_NESTED = 1;
   private static final byte OUTOFLINE_NEWLINE = 2;
   private static final byte OUTOFLINE_LEAF = 3;
   private static final byte OUTOFLINE_DIRTY = 8;
   public static final int INVALID_SCALAR_COOKIE = 0;
   private static String INLINE_OBJECT_TEXT = "￼";
   private static String OUTOFLINE_OBJECT_TEXT = "\u200b";
   private static final int INLINE_OBJECT_TEXT_LENGTH = INLINE_OBJECT_TEXT.length();
   private static final int OUTOFLINE_OBJECT_TEXT_LENGTH = OUTOFLINE_OBJECT_TEXT.length();
   public static final int INVALID_COLOUR = -2;
   public static final int DEFAULT_COLOUR = -1;

   public TextFlowData() {
      this._text = new StringBuffer();
      this._fontCache = FontCache.getInstance();
      this._regionStartOffsets = new int[256];
      this._regionEndOffsets = new int[256];
      this._regionLengths = new int[256];
      this._regionFlags = new short[256];
      this._regions = new TextFlowRegion[256];
      this._regionCellOOLIndex = new int[256];
      this._regionParentIds = new int[256];
      this._currentRegionStack = new IntStack();
      this._regionSortedByY = new int[256];
      this._outoflineRegionStarts = new int[0];
      this._outoflineObjects = new Object[0];
      this._textFlowCells = new TextFlowCell[0];
      this._outoflineFlags = new byte[0];
      this._focusRegionStarts = new int[0];
      this._focusRegionCookies = new Object[0];
      this._focusRegionScalarCookies = new long[0];
   }

   public final TextFlowNative getTextFlowNative() {
      return new TextFlowNative(this._text, this._regionStartOffsets, this._regionEndOffsets, this._regions, this._regionFlags, this._regionParentIds);
   }

   public final void setDefaultFont(Font f) {
      this._defaultFont = f;
   }

   public final StringBuffer getText() {
      return this._text;
   }

   public final short getNewCellId() {
      return (short)(this._nextCellId++);
   }

   public final int getNumCells() {
      return this._nextCellId - 1;
   }

   public final int getTextLength() {
      return this._text.length();
   }

   public final int getRegionCount() {
      return this._regionCount;
   }

   public final int[] getRegionStartOffsets() {
      return this._regionStartOffsets;
   }

   public final int[] getRegionEndOffsets() {
      return this._regionEndOffsets;
   }

   public final int getRegionStartOffset(int regionIndex) {
      return regionIndex >= this._regionCount ? this._text.length() : this._regionStartOffsets[regionIndex];
   }

   public final int getRegionEndOffset(int regionIndex) {
      if (regionIndex >= this._regionCount) {
         return this._text.length();
      }

      int result = this._regionEndOffsets[regionIndex];
      if (result == Integer.MAX_VALUE) {
         result = this._text.length();
      }

      return result;
   }

   public final int getRegionLength(int regionIndex) {
      return this.getRegionEndOffset(regionIndex) - this._regionStartOffsets[regionIndex];
   }

   public final int getRegionParentId(int regionIndex) {
      return this._regionParentIds[regionIndex];
   }

   public final int getNumSubRegions(int regionIndex) {
      int result = this._regionLengths[regionIndex];
      if (result == Integer.MAX_VALUE) {
         result = this._regionCount;
      }

      return result - regionIndex - 1;
   }

   public final boolean isRegionOutOfLine(int regionIndex) {
      return (this._regionFlags[regionIndex] & 128) != 0;
   }

   public final boolean isRegionInline(int regionIndex) {
      return (this._regionFlags[regionIndex] & 64) != 0;
   }

   public final boolean isRegionText(int regionIndex) {
      return (this._regionFlags[regionIndex] & 256) != 0;
   }

   public final boolean isRegionNewLine(int regionIndex) {
      return (this._regionFlags[regionIndex] & 128) != 0 && this.isOutOfLineNewline(this.getOutoflineIndexFromRegionIndex(regionIndex));
   }

   public final boolean isRegionDirty(int regionIndex) {
      return (this._regionFlags[regionIndex] & 512) != 0;
   }

   public final short getRegionFlags(int regionIndex) {
      return this._regionFlags[regionIndex];
   }

   public final void setRegionDirty(int regionIndex, boolean state) {
      if (state) {
         this._regionFlags[regionIndex] = (short)(this._regionFlags[regionIndex] | 512);
      } else {
         this._regionFlags[regionIndex] = (short)(this._regionFlags[regionIndex] & -513);
      }
   }

   public final TextFlowRegion getRegion(int regionIndex) {
      return this._regions[regionIndex];
   }

   public final short getRegionMargin(int regionIndex) {
      return this._regions[regionIndex]._margin;
   }

   public final Object getRegionObject(int regionIndex) {
      return this._regions[regionIndex]._obj;
   }

   public final void setRegionObjectExtent(int regionIndex, short width, short height) {
      TextFlowRegion region = this._regions[regionIndex];
      Asserts.productionStateAssert(region._obj != null);
      region._width = width;
      region._height = height;
   }

   public final int getOutOfLineObjectCount() {
      return this._outoflineCount;
   }

   public final Object getOutOfLineObject(int index) {
      return this._outoflineObjects[index];
   }

   public final int findRegionIndex(TextFlowRegion region) {
      for (int i = 0; i < this._regionCount; i++) {
         if (this._regions[i] == region) {
            return i;
         }
      }

      return -1;
   }

   public final int nextOutOfLineIndex(int index, int lastOOLIndex, int posToSearchAfter) {
      int oolPos = this.getOutOfLineObjectOffset(index);
      if (oolPos >= posToSearchAfter) {
         posToSearchAfter = oolPos + 1;
      }

      int searchIndex = this.getOutoflineIndexFromTextPosition(index, lastOOLIndex, posToSearchAfter);
      int result;
      if (searchIndex >= 0 && searchIndex < this._outoflineCount) {
         while (searchIndex != this._outoflineCount && (this.isOutOfLineAltText(searchIndex) || posToSearchAfter > this.getOutOfLineObjectOffset(searchIndex))) {
            searchIndex++;
         }

         result = searchIndex;
      } else {
         result = this._outoflineCount;
      }

      Asserts.productionStateAssert(result == this._outoflineCount || result > index);
      return result;
   }

   public final boolean isOutOfLineAltText(int index) {
      if (index == this._outoflineCount) {
         return false;
      } else {
         return this.isOutOfLineCell(index) ? false : this.isRegionText(this._outoflineRegionStarts[index]);
      }
   }

   public final boolean isOutOfLineNewline(int index) {
      return (this._outoflineFlags[index] & 7) == 2;
   }

   public final boolean isOutOfLineCell(int index) {
      return (this._outoflineFlags[index] & 7) == 1;
   }

   public final boolean isOutOfLineLeaf(int index) {
      return (this._outoflineFlags[index] & 7) == 3;
   }

   public final boolean isOutOfLineDirty(int index) {
      return (this._outoflineFlags[index] & 8) != 0;
   }

   public final void setOutOfLineDirty(int index, boolean dirty) {
      if (dirty) {
         this._outoflineFlags[index] = (byte)(this._outoflineFlags[index] | 8);
      } else {
         this._outoflineFlags[index] = (byte)(this._outoflineFlags[index] & -9);
      }
   }

   public final int getOutOfLineObjectLength(int index) {
      int region = this._outoflineRegionStarts[index];
      return this.getRegionEndOffset(region) - this._regionStartOffsets[region];
   }

   public final int getOutOfLineObjectEndOffset(int index) {
      return this.getRegionEndOffset(this._outoflineRegionStarts[index]);
   }

   public final int getOutOfLineObjectOffset(int index) {
      return index == this._outoflineCount ? Integer.MAX_VALUE : this._regionStartOffsets[this._outoflineRegionStarts[index]];
   }

   public final int getOutOfLineObjectRegion(int index) {
      return index == this._outoflineCount ? Integer.MAX_VALUE : this._outoflineRegionStarts[index];
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void append(String text, int offset, int length) {
      if (this._regionCount != 0) {
         if (length < 0) {
            length = text.length() - offset;
         }

         int newLength = this._text.length() + length;
         if (!this._appendFailed) {
            boolean var7 = false /* VF: Semaphore variable */;

            try {
               var7 = true;
               StringUtilities.append(this._text, text, offset, length);
               var7 = false;
            } finally {
               if (var7) {
                  this._appendFailed = true;
                  return;
               }
            }

            this._regionStartOffsets[this._regionCount] = newLength;
         }
      }
   }

   private final int currrentNestingIndex() {
      int size = this._nesting.size();
      return size > 0 ? this._nesting.elementAt(size - 1) : -1;
   }

   private final int currrentParentIndex() {
      int size = this._currentRegionStack.size();
      return size > 0 ? this._currentRegionStack.elementAt(size - 1) : -1;
   }

   public final void pushRegion(TextFlowRegion region) {
      if (this._regionCount + 1 >= this._regionStartOffsets.length) {
         int newsize = this._regionStartOffsets.length + Array.getSectionSize(this._regionStartOffsets);
         Array.resize(this._regionStartOffsets, newsize);
         Array.resize(this._regionEndOffsets, newsize);
         Array.resize(this._regionLengths, newsize);
         Array.resize(this._regionFlags, newsize);
         Array.resize(this._regions, newsize);
         Array.resize(this._regionCellOOLIndex, newsize);
         Array.resize(this._regionParentIds, newsize);
         Array.resize(this._regionSortedByY, newsize);
      }

      int regionIndex = this._regionCount;
      this._regions[regionIndex] = region;
      this._regionSortedByY[regionIndex] = regionIndex;
      this._regionCellOOLIndex[regionIndex] = this.currrentNestingIndex();
      this._regionStartOffsets[regionIndex] = this._text.length();
      this._regionEndOffsets[regionIndex] = Integer.MAX_VALUE;
      this._regionEndOffsets[regionIndex + 1] = 2147483646;
      this._regionLengths[regionIndex] = Integer.MAX_VALUE;
      this._regionParentIds[regionIndex] = this.currrentParentIndex();
      this._currentRegionStack.push(regionIndex);
      this._regionCount++;
   }

   public final void popRegion() {
      int regionIndex = this._currentRegionStack.pop();
      this._regionEndOffsets[regionIndex] = this._text.length();
      this._regionLengths[regionIndex] = this._regionCount;
   }

   public final int getFocusRegionCount() {
      return this._focusRegionCount;
   }

   public final int getFocusRegion(int index) {
      return this._focusRegionStarts[index];
   }

   public final Object getFocusRegionCookie(int index) {
      return this._focusRegionCookies[index];
   }

   public final long getFocusRegionScalarCookie(int index) {
      return this._focusRegionScalarCookies[index];
   }

   public final boolean isFocusRegionOpen() {
      return this._focusOn;
   }

   public final void startFocusRegion(Object cookie, long scalarCookie) {
      Asserts.productionStateAssert(!this._focusOn);
      this._focusRegionCount++;
      if (this._focusRegionCount >= this._focusRegionStarts.length) {
         int newsize = this._focusRegionStarts.length + Array.getSectionSize(this._focusRegionStarts);
         Array.resize(this._focusRegionStarts, newsize);
         Array.resize(this._focusRegionCookies, newsize);
         Array.resize(this._focusRegionScalarCookies, newsize);
      }

      int regionIndex = this._currentRegionStack.peek();
      this._focusRegionStarts[this._focusRegionCount - 1] = regionIndex;
      this._focusRegionCookies[this._focusRegionCount - 1] = cookie;
      this._focusRegionScalarCookies[this._focusRegionCount - 1] = scalarCookie;
      this._regions[regionIndex].setFindMaxWidthFlag(true);
      this._focusOn = true;
   }

   public final void endFocusRegion() {
      Asserts.productionStateAssert(this._focusOn);
      this._focusOn = false;
   }

   public final void pushOutOfLine(TextFlowCell cell) {
      int index = this._outoflineCount;
      int regionIndex = this._currentRegionStack.peek();
      this.ensureFontSet(this._regions[regionIndex]);
      this.appendOutOfLine(regionIndex, Integer.MAX_VALUE, cell, (byte)1);
      this.append(OUTOFLINE_OBJECT_TEXT, 0, OUTOFLINE_OBJECT_TEXT_LENGTH);
      this._nesting.push(index);
      TextFlowRegion oldRegion = this._regions[regionIndex];
      this._regionFlags[regionIndex] = 128;
      this._regions[regionIndex] = new TextFlowRegion();
      this._regions[regionIndex]._obj = cell;
      this._regions[regionIndex].inherit(oldRegion);
      cell.setStartValues();
      this.pushRegion(oldRegion);
      int cellIndex = cell._cellNesting & '\uffff';
      if (cellIndex >= this._textFlowCells.length) {
         Array.resize(this._textFlowCells, this._textFlowCells.length + Array.getSectionSize(this._textFlowCells));
      }

      this._textFlowCells[cellIndex] = cell;
   }

   public final void popOutOfLine() {
      this._nesting.pop();
      this.popRegion();
   }

   public final int getRegionCellOOLIndex(int region) {
      return this._regionCellOOLIndex[region];
   }

   public final void replaceField(int region, Object oldField, Object newField) {
      if (this.isRegionInline(region)) {
         Asserts.productionArgumentAssert(this._regions[region]._obj == oldField);
         this._regions[region]._obj = newField;
      } else {
         int ool = this.getOutoflineIndexFromRegionIndex(region);
         Asserts.productionArgumentAssert(this._outoflineObjects[ool] == oldField);
         this._outoflineObjects[ool] = newField;
      }
   }

   private final void appendOutOfLine(int startRegion, int span, Object object, byte flags) {
      if (this._outoflineCount >= this._outoflineRegionStarts.length) {
         int newsize = this._outoflineRegionStarts.length + Array.getSectionSize(this._outoflineRegionStarts);
         Array.resize(this._outoflineRegionStarts, newsize);
         Array.resize(this._outoflineObjects, newsize);
         Array.resize(this._outoflineFlags, newsize);
      }

      int regionIndex = this._outoflineCount;
      this._outoflineRegionStarts[regionIndex] = startRegion;
      this._outoflineObjects[regionIndex] = object;
      this._outoflineFlags[regionIndex] = flags;
      this._outoflineCount++;
   }

   public final void appendNewline() {
   }

   public final void appendOutOfLineObject(Object object) {
      int regionIndex = this._currentRegionStack.peek();
      this.ensureFontSet(this._regions[regionIndex]);
      Asserts.productionStateAssert(this._regions[regionIndex]._obj == null);
      this.appendOutOfLine(regionIndex, 0, object, (byte)11);
      this.append(OUTOFLINE_OBJECT_TEXT, 0, OUTOFLINE_OBJECT_TEXT_LENGTH);
      this._regions[regionIndex]._obj = object;
      this._regionFlags[regionIndex] = 128;
   }

   public final int appendOutOfLineObjectAlt(Object object, String text, int offset, int length) {
      int regionIndex = this._currentRegionStack.peek();
      this.ensureFontSet(this._regions[regionIndex]);
      Asserts.productionStateAssert(this._regions[regionIndex]._obj == null);
      this.appendOutOfLine(regionIndex, 0, object, (byte)11);
      this.append(text, offset, length);
      this._regions[regionIndex]._obj = object;
      this._regionFlags[regionIndex] = 384;
      return this._outoflineCount - 1;
   }

   public final void toggleOutOfLineObjectAlt(int tag, boolean text) {
      int regionIndex = this._outoflineRegionStarts[tag];
      if (text) {
         this._regionFlags[regionIndex] = (short)(this._regionFlags[regionIndex] | 256);
      } else {
         this._regionFlags[regionIndex] = (short)(this._regionFlags[regionIndex] & -257);
      }
   }

   public final void forceLineBreak() {
   }

   private final void ensureFontSet(TextFlowRegion region) {
      if (region.getFont() == null) {
         Asserts.productionStateAssert(region.getFontFamily() != null);
         region.setFont(this._fontCache.getFont(this._defaultFont, region.getFontStyle(), region.getFontHeight(), 0, null, region.getFontFamily()));
      }
   }

   public final void appendText(String text, int offset, int length) {
      int regionIndex = this._currentRegionStack.peek();
      this.ensureFontSet(this._regions[regionIndex]);
      this.append(text, offset, length);
      this._regions[regionIndex].invalidateTextWidths();
      this._regionFlags[regionIndex] = 320;
   }

   public final void appendObject(Object object, int alignment) {
      int regionIndex = this._currentRegionStack.peek();
      Asserts.productionStateAssert(this._regions[regionIndex]._obj == null);
      this.ensureFontSet(this._regions[regionIndex]);
      this.append(INLINE_OBJECT_TEXT, 0, INLINE_OBJECT_TEXT_LENGTH);
      this._regions[regionIndex]._obj = object;
      this._regions[regionIndex].setVAlignment((short)alignment);
      this._regionFlags[regionIndex] = 576;
   }

   public final int getFirstRegionIndexFromTextPosition(int regionStart, int textPosition) {
      int region = this.getRegionIndexFromTextPosition(regionStart, textPosition);
      if (region == Integer.MAX_VALUE) {
         return region;
      }

      while (region > regionStart && this._regionStartOffsets[region] == this._regionStartOffsets[region - 1]) {
         region--;
      }

      return region;
   }

   public final int getFirstRegionIndexFromTextPosition(int textPosition) {
      return this.getFirstRegionIndexFromTextPosition(0, textPosition);
   }

   public final int getLastRegionIndexFromTextPosition(int textPosition) {
      return this.getLastRegionIndexFromTextPosition(0, textPosition);
   }

   private final int getRegionIndexFromTextPosition(int regionStart, int textPosition) {
      if (textPosition >= this._text.length()) {
         return Integer.MAX_VALUE;
      }

      int regionIndex = Arrays.binarySearch(this._regionStartOffsets, textPosition, regionStart, this._regionCount);
      if (regionIndex < 0) {
         regionIndex = -regionIndex - 2;
      }

      return regionIndex;
   }

   public final int getLastRegionIndexFromTextPosition(int regionStart, int textPosition) {
      int region = this.getRegionIndexFromTextPosition(regionStart, textPosition);
      if (region == Integer.MAX_VALUE) {
         return region;
      }

      while (this._regionStartOffsets[region] == this._regionStartOffsets[region + 1]) {
         region++;
      }

      if (this._regionEndOffsets[region] <= textPosition) {
         for (int parentRegionId = this.getRegionParentId(region); parentRegionId != -1; parentRegionId = this.getRegionParentId(region)) {
            region = parentRegionId;
            if (this._regionEndOffsets[region] > textPosition) {
               return region;
            }
         }
      }

      return region;
   }

   public final int getOutoflineIndexFromTextPosition(int textPosition) {
      return this.getOutoflineIndexFromTextPosition(0, this._outoflineCount, textPosition);
   }

   private final int getOutoflineIndexFromTextPosition(int oolStart, int lastOOLIndex, int textPosition) {
      if (textPosition == this._text.length()) {
         return Integer.MAX_VALUE;
      }

      if (oolStart < 0 || oolStart >= this._outoflineCount) {
         oolStart = 0;
         lastOOLIndex = this._outoflineCount;
      }

      if (this._outoflineCount == 0) {
         return -1;
      }

      if (textPosition < this._regionStartOffsets[this._outoflineRegionStarts[0]]) {
         return -1;
      }

      int low = oolStart;
      int high = lastOOLIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int midVal = this._regionStartOffsets[this._outoflineRegionStarts[mid]];
         if (midVal < textPosition) {
            low = mid + 1;
         } else {
            if (midVal <= textPosition) {
               Asserts.productionAssert(textPosition >= midVal && textPosition <= this.getOutOfLineObjectEndOffset(mid));
               return mid;
            }

            high = mid - 1;
         }
      }

      Asserts.productionAssert(textPosition >= this._regionStartOffsets[this._outoflineRegionStarts[low - 1]]);
      return low - 1;
   }

   public final int getCellIndexFromTextPosition(int textPosition) {
      if (textPosition == this._text.length()) {
         return Integer.MAX_VALUE;
      }

      int regionIndex = this.getLastRegionIndexFromTextPosition(textPosition);
      return this._outoflineCount != 0 && regionIndex >= this._outoflineRegionStarts[0] ? this.getRegionCellOOLIndex(regionIndex) : -1;
   }

   public final int getOutoflineIndexFromRegionIndex(int regionIndex) {
      int index = Arrays.binarySearch(this._outoflineRegionStarts, regionIndex, 0, this._outoflineCount);
      if (index < 0) {
         index = -index - 2;
      }

      Asserts.productionAssert(regionIndex >= this._outoflineRegionStarts[index]);
      Asserts.productionAssert(index >= this._outoflineCount - 1 || regionIndex < this._outoflineRegionStarts[index + 1]);
      return index;
   }

   public final TextFlowCell getTextFlowCellFromNestingId(short nestingId) {
      return this._textFlowCells[nestingId & 65535];
   }

   public final TextFlowCell[] getTextFlowCells() {
      return this._textFlowCells;
   }

   private final boolean isRegionEmpty(int regionIndex) {
      return this._regionStartOffsets[regionIndex] == this._regionEndOffsets[regionIndex];
   }

   private final int binarySearchForY(int y) {
      int low = 0;
      int high = this._regionCount - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int midVal = this._regions[this._regionSortedByY[mid]].getOffsetYTop();
         if (midVal < y) {
            low = mid + 1;
         } else {
            if (midVal <= y) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   public final int getFocusIndexAtXYPosition(int x, int y) {
      int index = this.binarySearchForY(y);
      if (index == -1) {
         index = 0;
      } else if (index < 0) {
         index = -index - 2;
      }

      while (index < this._regionCount && this._regions[this._regionSortedByY[index]].getOffsetYTop() == y) {
         index++;
      }

      if (index >= this._regionCount) {
         index = this._regionCount - 1;
      }

      XYRect rect = Ui.getTmpXYRect();
      int bestIndex = -1;
      int smallestArea = Integer.MAX_VALUE;

      while (index >= 0) {
         int prevFocusIndex = Arrays.binarySearch(this._focusRegionStarts, this._regionSortedByY[index], 0, this._focusRegionCount);
         if (prevFocusIndex >= 0) {
            this._regions[this.getFocusRegion(prevFocusIndex)].getCoords(rect);
            if (rect.contains(x, y)) {
               int area = rect.width * rect.height;
               if (area < smallestArea) {
                  bestIndex = prevFocusIndex;
                  smallestArea = area;
               }
            }

            if (rect.Y2() < y) {
               break;
            }
         }

         index--;
      }

      Ui.returnTmpXYRect(rect);
      return bestIndex;
   }

   private final int getNextFocusIndexFromY(int regionSortedByYIndex, int direction) {
      if (direction > 0) {
         while (regionSortedByYIndex < this._regionCount) {
            int focusIndex = Arrays.binarySearch(this._focusRegionStarts, this._regionSortedByY[regionSortedByYIndex], 0, this._focusRegionCount);
            if (focusIndex >= 0) {
               return focusIndex;
            }

            regionSortedByYIndex++;
         }

         return this._focusRegionCount - 1;
      } else {
         if (regionSortedByYIndex >= this._regionCount) {
            return -1;
         }

         while (regionSortedByYIndex >= 0) {
            int focusIndex = Arrays.binarySearch(this._focusRegionStarts, this._regionSortedByY[regionSortedByYIndex], 0, this._focusRegionCount);
            if (focusIndex >= 0) {
               return focusIndex;
            }

            regionSortedByYIndex--;
         }

         return -1;
      }
   }

   public final int getFocusRegionNearY(int y, int direction) {
      int index = this.binarySearchForY(y);
      if (index == -1) {
         index = 0;
      } else if (index < 0) {
         index = -index - 2;
      }

      if (direction > 0 || direction < 0) {
         int nextFocusIndex = this.getNextFocusIndexFromY(index, direction);
         if (nextFocusIndex != -1) {
            return nextFocusIndex;
         }
      } else if (direction == 0) {
         int nextFocusIndex = this.getNextFocusIndexFromY(index, 1);
         int preFocusIndex = this.getNextFocusIndexFromY(index, -1);
         if (nextFocusIndex != -1 && preFocusIndex != -1) {
            int nextFocusRegionIndex = this.getFocusRegion(nextFocusIndex);
            int preFocusRegionIndex = this.getFocusRegion(preFocusIndex);
            int nextDistance = Math.min(
               Math.abs(this._regions[nextFocusRegionIndex].getOffsetYTop() - y), Math.abs(this._regions[nextFocusRegionIndex].getOffsetYBottom() - y)
            );
            int preDistance = Math.min(
               Math.abs(this._regions[preFocusRegionIndex].getOffsetYTop() - y), Math.abs(this._regions[preFocusRegionIndex].getOffsetYBottom() - y)
            );
            if (nextDistance <= preDistance) {
               return nextFocusIndex;
            }

            return preFocusIndex;
         }

         if (nextFocusIndex != -1) {
            return nextFocusIndex;
         }

         if (preFocusIndex != -1) {
            return preFocusIndex;
         }
      }

      return -1;
   }

   public final String getTextRange(int start, int end) {
      int region = this.getLastRegionIndexFromTextPosition(start);
      int count = 0;
      char[] chars = new char[end - start];
      int maxRegionIndex = region;
      int currentPos = start;

      for (int regionEndPos = Math.min(this.getRegionEndOffset(region), this.getRegionStartOffset(maxRegionIndex + 1));
         currentPos < end && count < chars.length;
         regionEndPos = Math.min(this.getRegionEndOffset(region), this.getRegionStartOffset(maxRegionIndex + 1))
      ) {
         if (regionEndPos > currentPos) {
            if (this.isRegionText(region)) {
               int copy = Math.min(chars.length - count, regionEndPos - currentPos);
               this._text.getChars(currentPos, currentPos + copy, chars, count);
               count += copy;
            }

            currentPos = regionEndPos;
         }

         int parentRegionId = this.getRegionParentId(region);
         if (parentRegionId != -1) {
            if (this.getRegionParentId(maxRegionIndex + 1) == parentRegionId && this.getRegionStartOffset(maxRegionIndex + 1) == regionEndPos) {
               region = maxRegionIndex + 1;
            } else {
               region = parentRegionId;
            }
         } else {
            region = maxRegionIndex + 1;
         }

         if (region >= this._regionCount) {
            break;
         }

         maxRegionIndex = Math.max(region, maxRegionIndex);
      }

      return new String(chars, 0, count);
   }

   public final long getAnchorFromRegionObject(Object obj) {
      for (int i = 0; i < this._regionCount; i++) {
         if (this._regions[i] == obj) {
            return this._regionStartOffsets[i];
         }
      }

      return 0;
   }

   public final void sortRegionsByY() {
      Arrays.sort(this._regionSortedByY, 0, this._regionCount, this._regionComparator);
   }
}
