package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.browser.util.TableItem;
import net.rim.device.internal.ui.Animation;
import net.rim.device.internal.ui.AnimationListener;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.TextFlowNative;
import net.rim.device.internal.ui.TextFlowRegion;

final class TextFlowCell implements TableItem, Animation {
   private int _flags = 6;
   private TextFlowCell _parent;
   private boolean _containsSubCells;
   private boolean _reflow;
   private int _textStartPosition;
   private int _textStartRegion;
   private int _outoflineObjectStart;
   private int _outoflineObjectEndData;
   private int _cellSpacing;
   private int _cellPadding;
   private Border _border;
   private int _contentWidth;
   private int _specifiedWidth;
   private int _maxCalculatedWidth = -1;
   private int _lastOOLWidth;
   private int _minCalculatedWidth = -1;
   private int _specifiedWidthCalculated = -1;
   private int _minRowWidth;
   private int _rowHeightPaddingTop;
   private int _rowHeightPaddingBottom;
   private int _contentHeight;
   private int _minHeight = -1;
   private int _x;
   private int _y;
   private int _lastLayoutWidth;
   private int _lastLayoutYPos;
   private int _layoutEndPosition;
   private int _lastLayoutStartLine;
   short _cellNesting;
   private int _cellOOLIndex;
   private int _rowStart = -1;
   private int _rowEnd = -1;
   private int _colStart = -1;
   private int _colEnd = -1;
   private int _currentRow;
   private net.rim.device.apps.internal.browser.util.Table _cellCollection;
   private int _leftEnd;
   private int _rightEnd;
   private int _left;
   private int _right;
   private int _leftEnd2;
   private int _rightEnd2;
   private int _left2;
   private int _right2;
   private int _cachePixelLine;
   private int _cacheTextLine;
   private int _cacheTextOffset;
   private EncodedImage _backgroundImage;
   private int _backgroundXPos;
   private int _backgroundYPos;
   private int _preferredSelectX;
   TextFlowCell$AnimationProperties _animationProperties;
   private TextFlowManager _manager;
   private static final boolean DEBUG;
   private static final int MIN_TEXT_LAYOUT_WIDTH;

   final void setStartValues() {
      this._textStartPosition = this._manager._textFlowData.getTextLength();
      this._textStartRegion = this._manager._textFlowData.getRegionCount();
      this._outoflineObjectStart = this._manager._textFlowData.getOutOfLineObjectCount();
      this._layoutEndPosition = this._textStartPosition;
      this._outoflineObjectEndData = Integer.MAX_VALUE;
   }

   final void setEndValues() {
      this._outoflineObjectEndData = this._manager._textFlowData.getOutOfLineObjectCount();
   }

   public final void setBackgroundImage(EncodedImage image) {
      if (image == null
         || (!image.hasTransparency() || image.getHeight() != 1 || image.getWidth() != 1) && image.getScaledWidth() != 0 && image.getScaledHeight() != 0) {
         this._backgroundImage = image;
         this._flags |= 32;
      }
   }

   public final void setBackgroundImageFixed(boolean value) {
      if (value) {
         this._flags |= 1;
      } else {
         this._flags &= -2;
      }
   }

   public final void setBackgroundImageRepeat(boolean repeatX, boolean repeatY) {
      if (repeatX) {
         this._flags |= 2;
      } else {
         this._flags &= -3;
      }

      if (repeatY) {
         this._flags |= 4;
      } else {
         this._flags &= -5;
      }
   }

   public final void setBackgroundImagePos(int xPos, int yPos) {
      this._backgroundXPos = xPos;
      this._backgroundYPos = yPos;
   }

   public final boolean isBackgroundImageSet() {
      return (this._flags & 32) == 0 && this._parent != null ? this._parent.isBackgroundImageSet() : (this._flags & 32) != 0;
   }

   public final void setWidth(int width) {
      this.setWidth(width, true);
   }

   final void setSpecifiedWidth(int width) {
      this._specifiedWidth = width;
   }

   public final void setMinHeight(int height) {
      this._minHeight = height;
   }

   public final int getContentWidth() {
      return this._contentWidth;
   }

   public final int getWidth() {
      return this._contentWidth + this.getPaddingBorderSpacingWidth();
   }

   final int getPaddingBorderSpacingWidth() {
      int spacing = this._cellSpacing;
      if (this._colStart == 0) {
         spacing <<= 1;
      }

      int borderWidth = this._border != null ? this._border.getLeft() + this._border.getRight() : 0;
      return borderWidth + this.getTotalWidthPadding() + spacing;
   }

   public final int getHeight() {
      int spacing = this._cellSpacing;
      if (this._rowStart == 0) {
         spacing <<= 1;
      }

      int borderWidth = this._border != null ? this._border.getTop() + this._border.getBottom() : 0;
      return this._rowHeightPaddingTop + this._rowHeightPaddingBottom + this._contentHeight + borderWidth + this.getTotalHeightPadding() + spacing;
   }

   public final int getPreferredWidth() {
      this.updateCalculatedWidth();
      return this._maxCalculatedWidth + this.getPaddingBorderSpacingWidth();
   }

   public final int getMinPreferredWidth() {
      this.updateCalculatedWidth();
      return this._minCalculatedWidth + this.getPaddingBorderSpacingWidth();
   }

   final void fieldWidthAdjusted(int oldWidth, int newOOLWidth) {
      this.fieldWidthAdjusted(null, oldWidth, newOOLWidth);
   }

   public final int getTextStartPosition() {
      return this._textStartPosition;
   }

   public final int getCellOutOfLineIndex() {
      return this._cellOOLIndex;
   }

   final short getCellNestingId() {
      return this._cellNesting;
   }

   public final void finishRow() {
      if (this._cellCollection != null) {
         this._cellCollection.finishRow();
      }
   }

   public final void forceNewline() {
   }

   final void forceLineBreak() {
      this._manager._textFlowData.forceLineBreak();
   }

   public final void appendText(String text, int offset, int length) {
      this._manager._textFlowData.appendText(text, offset, length);
      this.invalidateWalk();
   }

   public final void forceReflow() {
      this._maxCalculatedWidth = -1;
      this._specifiedWidthCalculated = -1;
      this._reflow = true;
   }

   public final void appendOutOfLineCell(TextFlowCell cell, int colspan, int rowspan) {
      this._manager._textFlowData.pushOutOfLine(cell);
      this.invalidateWalk();
      if (this._cellCollection != null) {
         this._cellCollection.addCell(cell, colspan, rowspan);
      }

      this._containsSubCells = true;
   }

   public final void appendInlineField(Field field, int alignment) {
      boolean openfocus = false;
      boolean isfocusregionopen = this._manager._textFlowData.isFocusRegionOpen();
      if (field.isFocusable()) {
         openfocus = !isfocusregionopen;
      } else {
         Asserts.productionStateAssert(!isfocusregionopen);
      }

      if (openfocus) {
         this._manager._textFlowData.startFocusRegion(field, 0);
      }

      this._manager._textFlowData.appendObject(field, alignment);
      if (openfocus) {
         this._manager._textFlowData.endFocusRegion();
      }

      this.invalidateWalk();
   }

   public final void appendOutOfLineField(Field field) {
      boolean openfocus = false;
      boolean isfocusregionopen = this._manager._textFlowData.isFocusRegionOpen();
      if (field.isFocusable()) {
         openfocus = !isfocusregionopen;
      } else {
         Asserts.productionStateAssert(!isfocusregionopen);
      }

      if (openfocus) {
         this._manager._textFlowData.startFocusRegion(field, 0);
      }

      this._manager._textFlowData.appendOutOfLineObject(field);
      if (openfocus) {
         this._manager._textFlowData.endFocusRegion();
      }

      this.invalidateWalk();
   }

   public final int appendOutOfLineFieldAlt(Field field, String text, int offset, int length) {
      boolean openfocus = false;
      boolean isfocusregionopen = this._manager._textFlowData.isFocusRegionOpen();
      if (field.isFocusable()) {
         openfocus = !isfocusregionopen;
      } else {
         Asserts.productionStateAssert(!isfocusregionopen);
      }

      if (openfocus) {
         this._manager._textFlowData.startFocusRegion(field, 0);
      }

      int rc = this._manager._textFlowData.appendOutOfLineObjectAlt(field, text, offset, length);
      if (openfocus) {
         this._manager._textFlowData.endFocusRegion();
      }

      this.invalidateWalk();
      return rc;
   }

   public final int getFocusIndexFromXYPosition(int xPos, int yPos) {
      return this._manager._textFlowData.getFocusIndexAtXYPosition(xPos, yPos);
   }

   public final int getNextFocusIndex(int focusIndex, int focusState, int direction, boolean horizontal) {
      int item = -1;
      if (focusIndex >= 0 && focusIndex < this._manager._textFlowData.getFocusRegionCount()) {
         int startRegion = this._manager._textFlowData.getFocusRegion(focusIndex);
         item = this._manager._textFlowData.getRegionCellOOLIndex(startRegion);
      }

      TextFlowCell subCell;
      if (item != -1) {
         Object obj = this._manager._textFlowData.getOutOfLineObject(item);
         Asserts.productionAssert(obj instanceof TextFlowCell);
         subCell = (TextFlowCell)obj;
      } else {
         subCell = this;
      }

      int nextIndex = focusIndex + focusState + direction;
      int newFocusIndex = subCell.getNextFocusIndexInternal(focusIndex, focusState, direction, horizontal);
      return newFocusIndex == -1 && nextIndex >= 0 ? nextIndex : newFocusIndex;
   }

   public final int getNextFocusIndexInternal(int focusIndex, int focusState, int direction, boolean horizontal) {
      TextFlowData textFlowData = this._manager._textFlowData;
      int focusCount = textFlowData.getFocusRegionCount();

      for (focusIndex += focusState + direction; focusIndex >= 0; focusIndex += direction) {
         if (focusIndex >= focusCount) {
            return focusIndex;
         }

         int region = textFlowData.getFocusRegion(focusIndex);
         if (textFlowData.getRegionStartOffset(region) != textFlowData.getRegionEndOffset(region) && (textFlowData.getRegion(region).getFlags() & 1024) == 0) {
            return focusIndex;
         }
      }

      return focusIndex;
   }

   public final void getTextFocusRect(int focusIndex, XYRect rect, boolean accurateXCoordsNeeded) {
      int region = this._manager._textFlowData.getFocusRegion(focusIndex);
      if ((this._manager._textFlowData.getRegion(region).getFlags() & 1024) == 0) {
         int regionTextStart = this._manager._textFlowData.getRegionStartOffset(region);
         int regionTextEnd = this._manager._textFlowData.getRegionEndOffset(region);
         if (regionTextStart != regionTextEnd) {
            this._manager._textFlowData.getRegion(region).getCoords(rect);
         }
      }
   }

   public final String getTextFocusText(int focusIndex) {
      int region = this._manager._textFlowData.getFocusRegion(focusIndex);
      int regionTextStart = this._manager._textFlowData.getRegionStartOffset(region);
      int regionTextEnd = this._manager._textFlowData.getRegionEndOffset(region);
      char[] chars = new char[regionTextEnd - regionTextStart];
      this._manager._textFlowData.getText().getChars(regionTextStart, regionTextEnd, chars, 0);
      return (String)(new Object(chars));
   }

   public final String getSelectionText() {
      return this._manager._textFlowData.getTextRange(this._manager._selectionCellBegin, this._manager._selectionCellEnd);
   }

   public final void getCharacterRect(int textPosition, XYRect rect) {
      int index = this.findTextFlowCellFromTextPos(textPosition);
      TextFlowCell cell;
      if (index != -1) {
         cell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(index);
      } else {
         cell = this;
      }

      if (cell != this) {
         cell.getCharacterRect(textPosition, rect);
      } else {
         this.cacheFromTextPosition(textPosition);
         rect.y = this._cachePixelLine + this._rowHeightPaddingTop;
         rect.height = this._manager._textFlowLayout.getLineHeights()[this._cacheTextLine];
         rect.x = this._manager._painter.GetXOffsetOfTextPosition(this, textPosition, false);
         rect.width = this._manager._painter.GetXOffsetOfTextPosition(this, textPosition + 1, true) - rect.x;
      }
   }

   public final void drawPosition(int position, Graphics graphics, boolean on) {
      int index = this.findTextFlowCellFromTextPos(position);
      TextFlowCell cell;
      if (index != -1) {
         cell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(index);
      } else {
         cell = this;
      }

      cell.drawTextHighlight(position, position + 1, graphics, on, 1, false, 65536);
   }

   public final void drawSelection(int carat, int anchor, Graphics graphics, boolean on) {
      int halfpos;
      if (carat < anchor) {
         halfpos = carat - 1;
      } else {
         halfpos = carat;
      }

      if (halfpos >= this._textStartPosition && halfpos < this._manager._textFlowData.getTextLength()) {
         int index = this.findTextFlowCellFromTextPos(halfpos);
         TextFlowCell cell;
         if (index != -1) {
            cell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(index);
         } else {
            cell = this;
         }

         cell.drawTextHighlight(halfpos, halfpos + 1, graphics, on, 1, true, 65536);
      }
   }

   public final void drawTextFocus(int focusIndex, Graphics graphics, boolean on, int scale) {
      int region = this._manager._textFlowData.getFocusRegion(focusIndex);
      int regionTextStart = this._manager._textFlowData.getRegionStartOffset(region);
      int regionTextEnd = this._manager._textFlowData.getRegionEndOffset(region);
      if (regionTextStart != regionTextEnd) {
         int item = this._manager._textFlowData.getRegionCellOOLIndex(region);
         TextFlowCell subCell;
         if (item != -1) {
            Object obj = this._manager._textFlowData.getOutOfLineObject(item);
            Asserts.productionAssert(obj instanceof TextFlowCell);
            subCell = (TextFlowCell)obj;
         } else {
            subCell = this;
         }

         subCell.drawTextHighlight(regionTextStart, regionTextEnd, graphics, on, 1, false, scale);
      }
   }

   public final int getLayoutEndPosition() {
      return this._layoutEndPosition;
   }

   public final int[] layout(int invalidLayoutStart, int invalidLayoutEnd) {
      TextFlowNative cellLayout = this._manager._textFlowLayout.getTextFlowNative();
      this._manager._leftObjects.ensureCapacity(this._manager._textFlowData.getNumCells() + 1);
      this._manager._rightObjects.ensureCapacity(this._manager._textFlowData.getNumCells() + 1);
      this.layout0(cellLayout, invalidLayoutStart, invalidLayoutEnd);
      int startLine = cellLayout.getStartLine();
      if (startLine >= 0) {
         this._manager._textFlowLayout.copyCellData(startLine, Math.max(cellLayout.getEndLine() - startLine, 0), this._manager._textFlowData.getNumCells());
         this._manager._lastLayoutStartY = cellLayout.getStartYPos();
         this._manager._textFlowLayout.setMaxXOffset(cellLayout.getMaxXOffset());
      }

      cellLayout.reset();
      this._manager._textFlowData.sortRegionsByY();
      return new int[]{this._manager._textFlowLayout.getMaxXOffset(), this._contentHeight};
   }

   final void revalidateManagers() {
      TextFlowData textFlowData = this._manager._textFlowData;
      int endRegion = this.getTextEndRegion();

      for (int i = this._textStartRegion; i < endRegion; i++) {
         Object regionObject = textFlowData.getRegionObject(i);
         if (regionObject instanceof Object) {
            Manager field = (Manager)regionObject;
            if ((textFlowData.getRegion(i).getFlags() & 1024) != 0) {
               this._manager.layoutField(field, 0, 0);
            } else {
               this._manager.layoutField(field, this._contentWidth, 32767);
            }
         }
      }
   }

   public final void paint(Graphics param1, Font param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 004: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowLayout Lnet/rim/device/apps/internal/browser/ui/TextFlowLayout;
      // 007: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowLayout.getLineCount ()I
      // 00a: ifne 00e
      // 00d: return
      // 00e: aload 0
      // 00f: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 012: ifnull 032
      // 015: aload 0
      // 016: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 019: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties._animationStyle I
      // 01c: bipush 1
      // 01d: iand
      // 01e: ifeq 032
      // 021: aload 0
      // 022: invokestatic net/rim/device/internal/ui/AnimationThread.addAnimation (Lnet/rim/device/internal/ui/Animation;)V
      // 025: aload 0
      // 026: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 029: dup
      // 02a: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties._animationStyle I
      // 02d: bipush 1
      // 02e: ixor
      // 02f: putfield net/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties._animationStyle I
      // 032: aload 1
      // 033: invokevirtual net/rim/device/api/ui/Graphics.getClippingRect ()Lnet/rim/device/api/ui/XYRect;
      // 036: astore 4
      // 038: aload 4
      // 03a: getfield net/rim/device/api/ui/XYRect.y I
      // 03d: iload 3
      // 03e: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32MultToInt (II)I
      // 041: istore 5
      // 043: iload 5
      // 045: aload 4
      // 047: getfield net/rim/device/api/ui/XYRect.height I
      // 04a: iload 3
      // 04b: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32MultToInt (II)I
      // 04e: iadd
      // 04f: istore 6
      // 051: aload 1
      // 052: bipush 8
      // 054: invokevirtual net/rim/device/api/ui/Graphics.isDrawingStyleSet (I)Z
      // 057: ifeq 05d
      // 05a: goto 4f1
      // 05d: aload 1
      // 05e: bipush 32
      // 060: invokevirtual net/rim/device/api/ui/Graphics.isDrawingStyleSet (I)Z
      // 063: ifeq 069
      // 066: goto 4f1
      // 069: aload 0
      // 06a: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 06d: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 070: aload 0
      // 071: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._textStartRegion I
      // 074: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getRegion (I)Lnet/rim/device/internal/ui/TextFlowRegion;
      // 077: getfield net/rim/device/internal/ui/TextFlowRegion._backgroundColour I
      // 07a: istore 7
      // 07c: iload 7
      // 07e: bipush -1
      // 080: if_icmpeq 0a7
      // 083: aload 0
      // 084: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._parent Lnet/rim/device/apps/internal/browser/ui/TextFlowCell;
      // 087: ifnull 0a7
      // 08a: iload 7
      // 08c: aload 0
      // 08d: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 090: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 093: aload 0
      // 094: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._parent Lnet/rim/device/apps/internal/browser/ui/TextFlowCell;
      // 097: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._textStartRegion I
      // 09a: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getRegion (I)Lnet/rim/device/internal/ui/TextFlowRegion;
      // 09d: getfield net/rim/device/internal/ui/TextFlowRegion._backgroundColour I
      // 0a0: if_icmpne 0a7
      // 0a3: bipush -1
      // 0a5: istore 7
      // 0a7: iload 7
      // 0a9: bipush -1
      // 0ab: if_icmpeq 0d8
      // 0ae: aload 1
      // 0af: invokevirtual net/rim/device/api/ui/Graphics.getColor ()I
      // 0b2: istore 8
      // 0b4: aload 1
      // 0b5: iload 7
      // 0b7: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 0ba: aload 1
      // 0bb: aload 4
      // 0bd: getfield net/rim/device/api/ui/XYRect.x I
      // 0c0: aload 4
      // 0c2: getfield net/rim/device/api/ui/XYRect.y I
      // 0c5: aload 4
      // 0c7: getfield net/rim/device/api/ui/XYRect.width I
      // 0ca: aload 4
      // 0cc: getfield net/rim/device/api/ui/XYRect.height I
      // 0cf: invokevirtual net/rim/device/api/ui/Graphics.fillRect (IIII)V
      // 0d2: aload 1
      // 0d3: iload 8
      // 0d5: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 0d8: aload 0
      // 0d9: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 0dc: ifnonnull 0e2
      // 0df: goto 4f1
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 0e6: invokevirtual net/rim/device/api/system/EncodedImage.getScaledWidth ()I
      // 0e9: istore 8
      // 0eb: aload 0
      // 0ec: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 0ef: invokevirtual net/rim/device/api/system/EncodedImage.getScaledHeight ()I
      // 0f2: istore 9
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._x I
      // 0f8: aload 0
      // 0f9: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._colStart I
      // 0fc: ifne 106
      // 0ff: aload 0
      // 100: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 103: goto 107
      // 106: bipush 0
      // 107: iadd
      // 108: istore 12
      // 10a: aload 0
      // 10b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 10e: aload 0
      // 10f: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowStart I
      // 112: ifne 11c
      // 115: aload 0
      // 116: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 119: goto 11d
      // 11c: bipush 0
      // 11d: iadd
      // 11e: istore 13
      // 120: iload 12
      // 122: istore 14
      // 124: iload 13
      // 126: istore 15
      // 128: aload 0
      // 129: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._flags I
      // 12c: bipush 1
      // 12d: iand
      // 12e: ifeq 135
      // 131: bipush 1
      // 132: goto 136
      // 135: bipush 0
      // 136: istore 16
      // 138: aload 0
      // 139: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundXPos I
      // 13c: ldc_w 1073741824
      // 13f: iand
      // 140: ifne 16e
      // 143: aload 0
      // 144: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundXPos I
      // 147: ldc_w -2147483648
      // 14a: iand
      // 14b: ifne 15e
      // 14e: iload 14
      // 150: aload 0
      // 151: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundXPos I
      // 154: ldc_w 65535
      // 157: iand
      // 158: iadd
      // 159: istore 14
      // 15b: goto 1b1
      // 15e: iload 14
      // 160: aload 0
      // 161: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundXPos I
      // 164: ldc_w 65535
      // 167: iand
      // 168: isub
      // 169: istore 14
      // 16b: goto 1b1
      // 16e: bipush 0
      // 16f: aload 0
      // 170: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundXPos I
      // 173: sipush 255
      // 176: iand
      // 177: bipush 100
      // 179: invokestatic net/rim/device/api/util/MathUtilities.clamp (III)I
      // 17c: istore 17
      // 17e: iload 16
      // 180: ifeq 18f
      // 183: aload 0
      // 184: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 187: invokevirtual net/rim/device/api/ui/Field.getWidth ()I
      // 18a: istore 18
      // 18c: goto 19a
      // 18f: aload 0
      // 190: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._contentWidth I
      // 193: aload 0
      // 194: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.getTotalWidthPadding ()I
      // 197: iadd
      // 198: istore 18
      // 19a: iload 18
      // 19c: iload 17
      // 19e: imul
      // 19f: iload 8
      // 1a1: iload 17
      // 1a3: imul
      // 1a4: isub
      // 1a5: bipush 100
      // 1a7: idiv
      // 1a8: istore 19
      // 1aa: iload 14
      // 1ac: iload 19
      // 1ae: iadd
      // 1af: istore 14
      // 1b1: aload 0
      // 1b2: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundYPos I
      // 1b5: ldc_w 1073741824
      // 1b8: iand
      // 1b9: ifne 1e7
      // 1bc: aload 0
      // 1bd: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundYPos I
      // 1c0: ldc_w -2147483648
      // 1c3: iand
      // 1c4: ifne 1d7
      // 1c7: iload 15
      // 1c9: aload 0
      // 1ca: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundYPos I
      // 1cd: ldc_w 65535
      // 1d0: iand
      // 1d1: iadd
      // 1d2: istore 15
      // 1d4: goto 254
      // 1d7: iload 15
      // 1d9: aload 0
      // 1da: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundYPos I
      // 1dd: ldc_w 65535
      // 1e0: iand
      // 1e1: isub
      // 1e2: istore 15
      // 1e4: goto 254
      // 1e7: aload 0
      // 1e8: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundYPos I
      // 1eb: sipush 255
      // 1ee: iand
      // 1ef: istore 17
      // 1f1: iload 17
      // 1f3: iflt 1fd
      // 1f6: iload 17
      // 1f8: bipush 100
      // 1fa: if_icmple 205
      // 1fd: new java/lang/Object
      // 200: dup
      // 201: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 204: athrow
      // 205: iload 16
      // 207: ifeq 216
      // 20a: aload 0
      // 20b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 20e: invokevirtual net/rim/device/api/ui/Field.getHeight ()I
      // 211: istore 18
      // 213: goto 23d
      // 216: aload 0
      // 217: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._contentHeight I
      // 21a: aload 0
      // 21b: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.getTotalHeightPadding ()I
      // 21e: iadd
      // 21f: istore 18
      // 221: aload 0
      // 222: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._parent Lnet/rim/device/apps/internal/browser/ui/TextFlowCell;
      // 225: ifnonnull 23d
      // 228: iload 18
      // 22a: aload 0
      // 22b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 22e: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._layoutHeight I
      // 231: if_icmpge 23d
      // 234: aload 0
      // 235: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 238: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._layoutHeight I
      // 23b: istore 18
      // 23d: iload 18
      // 23f: iload 17
      // 241: imul
      // 242: iload 9
      // 244: iload 17
      // 246: imul
      // 247: isub
      // 248: bipush 100
      // 24a: idiv
      // 24b: istore 19
      // 24d: iload 15
      // 24f: iload 19
      // 251: iadd
      // 252: istore 15
      // 254: aload 0
      // 255: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._flags I
      // 258: bipush 2
      // 25a: iand
      // 25b: ifeq 262
      // 25e: bipush 1
      // 25f: goto 263
      // 262: bipush 0
      // 263: istore 17
      // 265: aload 0
      // 266: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._flags I
      // 269: bipush 4
      // 26b: iand
      // 26c: ifeq 273
      // 26f: bipush 1
      // 270: goto 274
      // 273: bipush 0
      // 274: istore 18
      // 276: iload 16
      // 278: ifne 27e
      // 27b: goto 31f
      // 27e: aload 4
      // 280: getfield net/rim/device/api/ui/XYRect.y I
      // 283: ifeq 295
      // 286: aload 4
      // 288: getfield net/rim/device/api/ui/XYRect.y I
      // 28b: aload 0
      // 28c: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 28f: invokevirtual net/rim/device/api/ui/Manager.getVerticalScroll ()I
      // 292: if_icmpne 2a4
      // 295: aload 4
      // 297: getfield net/rim/device/api/ui/XYRect.height I
      // 29a: aload 0
      // 29b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 29e: invokevirtual net/rim/device/api/ui/Field.getHeight ()I
      // 2a1: if_icmpeq 2c7
      // 2a4: aload 0
      // 2a5: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 2a8: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.invalidate ()V
      // 2ab: aload 0
      // 2ac: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 2af: ifnull 2c6
      // 2b2: aload 0
      // 2b3: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 2b6: ldc_w 65536
      // 2b9: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 2bc: aload 0
      // 2bd: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 2c0: ldc_w 65536
      // 2c3: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 2c6: return
      // 2c7: iload 14
      // 2c9: istore 10
      // 2cb: iload 5
      // 2cd: iload 15
      // 2cf: iload 13
      // 2d1: isub
      // 2d2: iadd
      // 2d3: istore 11
      // 2d5: iload 17
      // 2d7: ifeq 2f7
      // 2da: iload 14
      // 2dc: iload 12
      // 2de: isub
      // 2df: iload 8
      // 2e1: irem
      // 2e2: istore 19
      // 2e4: iload 19
      // 2e6: ifle 2f4
      // 2e9: iload 8
      // 2eb: iload 19
      // 2ed: isub
      // 2ee: ineg
      // 2ef: istore 10
      // 2f1: goto 2f7
      // 2f4: bipush 0
      // 2f5: istore 10
      // 2f7: iload 18
      // 2f9: ifeq 371
      // 2fc: iload 11
      // 2fe: iload 5
      // 300: isub
      // 301: iload 9
      // 303: irem
      // 304: istore 19
      // 306: iload 19
      // 308: ifle 318
      // 30b: iload 5
      // 30d: iload 9
      // 30f: iload 19
      // 311: isub
      // 312: isub
      // 313: istore 11
      // 315: goto 371
      // 318: iload 5
      // 31a: istore 11
      // 31c: goto 371
      // 31f: iload 14
      // 321: istore 10
      // 323: iload 15
      // 325: istore 11
      // 327: iload 17
      // 329: ifeq 34c
      // 32c: iload 14
      // 32e: iload 12
      // 330: isub
      // 331: iload 8
      // 333: irem
      // 334: istore 19
      // 336: iload 19
      // 338: ifle 348
      // 33b: iload 12
      // 33d: iload 8
      // 33f: iload 19
      // 341: isub
      // 342: isub
      // 343: istore 10
      // 345: goto 34c
      // 348: iload 12
      // 34a: istore 10
      // 34c: iload 18
      // 34e: ifeq 371
      // 351: iload 15
      // 353: iload 13
      // 355: isub
      // 356: iload 9
      // 358: irem
      // 359: istore 19
      // 35b: iload 19
      // 35d: ifle 36d
      // 360: iload 13
      // 362: iload 9
      // 364: iload 19
      // 366: isub
      // 367: isub
      // 368: istore 11
      // 36a: goto 371
      // 36d: iload 13
      // 36f: istore 11
      // 371: iload 10
      // 373: iload 3
      // 374: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 377: istore 10
      // 379: iload 11
      // 37b: iload 3
      // 37c: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 37f: istore 11
      // 381: iload 17
      // 383: ifne 38b
      // 386: iload 18
      // 388: ifeq 38f
      // 38b: bipush 1
      // 38c: goto 390
      // 38f: bipush 0
      // 390: istore 19
      // 392: aload 4
      // 394: getfield net/rim/device/api/ui/XYRect.x I
      // 397: iload 10
      // 399: isub
      // 39a: aload 4
      // 39c: getfield net/rim/device/api/ui/XYRect.width I
      // 39f: iadd
      // 3a0: istore 20
      // 3a2: aload 4
      // 3a4: getfield net/rim/device/api/ui/XYRect.y I
      // 3a7: iload 11
      // 3a9: isub
      // 3aa: aload 4
      // 3ac: getfield net/rim/device/api/ui/XYRect.height I
      // 3af: iadd
      // 3b0: istore 21
      // 3b2: iload 17
      // 3b4: ifeq 3bf
      // 3b7: iload 18
      // 3b9: ifeq 3bf
      // 3bc: goto 3ef
      // 3bf: iload 17
      // 3c1: ifeq 3cf
      // 3c4: iload 9
      // 3c6: iload 3
      // 3c7: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 3ca: istore 21
      // 3cc: goto 3ef
      // 3cf: iload 18
      // 3d1: ifeq 3df
      // 3d4: iload 8
      // 3d6: iload 3
      // 3d7: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 3da: istore 20
      // 3dc: goto 3ef
      // 3df: iload 9
      // 3e1: iload 3
      // 3e2: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 3e5: istore 21
      // 3e7: iload 8
      // 3e9: iload 3
      // 3ea: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 3ed: istore 20
      // 3ef: aload 0
      // 3f0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 3f3: iload 3
      // 3f4: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 3f7: aload 0
      // 3f8: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 3fb: iload 3
      // 3fc: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 3ff: iload 19
      // 401: ifeq 41c
      // 404: aload 1
      // 405: bipush -97
      // 407: iload 10
      // 409: iload 11
      // 40b: iload 20
      // 40d: iload 21
      // 40f: aload 0
      // 410: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 413: bipush 0
      // 414: bipush 0
      // 415: bipush 0
      // 416: invokevirtual net/rim/device/api/ui/Graphics.tileRopImage (IIIIILnet/rim/device/api/system/EncodedImage;III)V
      // 419: goto 441
      // 41c: aload 4
      // 41e: iload 10
      // 420: iload 11
      // 422: iload 20
      // 424: iload 21
      // 426: invokevirtual net/rim/device/api/ui/XYRect.intersects (IIII)Z
      // 429: ifeq 441
      // 42c: aload 1
      // 42d: bipush -97
      // 42f: iload 10
      // 431: iload 11
      // 433: iload 20
      // 435: iload 21
      // 437: aload 0
      // 438: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 43b: bipush 0
      // 43c: bipush 0
      // 43d: bipush 0
      // 43e: invokevirtual net/rim/device/api/ui/Graphics.ropImage (IIIIILnet/rim/device/api/system/EncodedImage;III)V
      // 441: aload 0
      // 442: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 445: ifnonnull 44b
      // 448: goto 4f1
      // 44b: aload 0
      // 44c: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 44f: ldc_w 65536
      // 452: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 455: aload 0
      // 456: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 459: ldc_w 65536
      // 45c: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 45f: goto 4f1
      // 462: astore 8
      // 464: aload 0
      // 465: aconst_null
      // 466: putfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 469: aload 0
      // 46a: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 46d: ifnull 4f1
      // 470: aload 0
      // 471: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 474: ldc_w 65536
      // 477: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 47a: aload 0
      // 47b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 47e: ldc_w 65536
      // 481: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 484: goto 4f1
      // 487: astore 8
      // 489: aload 0
      // 48a: aconst_null
      // 48b: putfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 48e: aload 0
      // 48f: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 492: ifnull 4f1
      // 495: aload 0
      // 496: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 499: ldc_w 65536
      // 49c: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 49f: aload 0
      // 4a0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4a3: ldc_w 65536
      // 4a6: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 4a9: goto 4f1
      // 4ac: astore 8
      // 4ae: aload 0
      // 4af: aconst_null
      // 4b0: putfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4b3: aload 0
      // 4b4: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4b7: ifnull 4f1
      // 4ba: aload 0
      // 4bb: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4be: ldc_w 65536
      // 4c1: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 4c4: aload 0
      // 4c5: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4c8: ldc_w 65536
      // 4cb: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 4ce: goto 4f1
      // 4d1: astore 22
      // 4d3: aload 0
      // 4d4: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4d7: ifnull 4ee
      // 4da: aload 0
      // 4db: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4de: ldc_w 65536
      // 4e1: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 4e4: aload 0
      // 4e5: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._backgroundImage Lnet/rim/device/api/system/EncodedImage;
      // 4e8: ldc_w 65536
      // 4eb: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 4ee: aload 22
      // 4f0: athrow
      // 4f1: iload 5
      // 4f3: aload 0
      // 4f4: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 4f7: aload 0
      // 4f8: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell.getHeight ()I
      // 4fb: iadd
      // 4fc: if_icmplt 500
      // 4ff: return
      // 500: aload 0
      // 501: iload 5
      // 503: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.cacheFromYOffset (I)V
      // 506: aload 0
      // 507: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._outoflineObjectStart I
      // 50a: istore 7
      // 50c: aload 0
      // 50d: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 510: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 513: iload 7
      // 515: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectOffset (I)I
      // 518: istore 8
      // 51a: iload 8
      // 51c: aload 0
      // 51d: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._layoutEndPosition I
      // 520: if_icmpge 5b1
      // 523: aload 0
      // 524: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 527: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 52a: aload 0
      // 52b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 52e: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 531: iload 7
      // 533: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectRegion (I)I
      // 536: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getRegionCellOOLIndex (I)I
      // 539: aload 0
      // 53a: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellOOLIndex I
      // 53d: if_icmpne 59d
      // 540: aload 0
      // 541: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 544: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 547: iload 7
      // 549: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.isOutOfLineCell (I)Z
      // 54c: ifeq 59d
      // 54f: aload 0
      // 550: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 553: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 556: iload 7
      // 558: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObject (I)Ljava/lang/Object;
      // 55b: checkcast net/rim/device/apps/internal/browser/ui/TextFlowCell
      // 55e: astore 9
      // 560: aload 9
      // 562: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell.getHeight ()I
      // 565: istore 10
      // 567: iload 5
      // 569: aload 9
      // 56b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 56e: if_icmplt 594
      // 571: iload 5
      // 573: iload 10
      // 575: aload 9
      // 577: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 57a: iadd
      // 57b: if_icmpge 594
      // 57e: aload 0
      // 57f: iload 8
      // 581: aload 0
      // 582: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 585: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 588: iload 7
      // 58a: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectLength (I)I
      // 58d: iadd
      // 58e: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.cacheFromTextPosition (I)V
      // 591: goto 5b1
      // 594: aload 9
      // 596: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.getOutOfLineObjectEnd ()I
      // 599: bipush 1
      // 59a: isub
      // 59b: istore 7
      // 59d: iinc 7 1
      // 5a0: aload 0
      // 5a1: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 5a4: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 5a7: iload 7
      // 5a9: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectOffset (I)I
      // 5ac: istore 8
      // 5ae: goto 51a
      // 5b1: aload 0
      // 5b2: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 5b5: ifnull 5c2
      // 5b8: aload 0
      // 5b9: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 5bc: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties._drawText Z
      // 5bf: ifeq 63a
      // 5c2: aload 0
      // 5c3: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cacheTextOffset I
      // 5c6: aload 0
      // 5c7: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._layoutEndPosition I
      // 5ca: if_icmpge 63a
      // 5cd: aload 0
      // 5ce: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 5d1: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._painter Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$Painter;
      // 5d4: astore 9
      // 5d6: aload 9
      // 5d8: aload 0
      // 5d9: aload 1
      // 5da: aload 2
      // 5db: aload 0
      // 5dc: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cacheTextLine I
      // 5df: aload 0
      // 5e0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cachePixelLine I
      // 5e3: aload 0
      // 5e4: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowHeightPaddingTop I
      // 5e7: iadd
      // 5e8: aload 0
      // 5e9: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cacheTextOffset I
      // 5ec: iload 3
      // 5ed: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell$Painter.StartPainting (Lnet/rim/device/apps/internal/browser/ui/TextFlowCell;Lnet/rim/device/api/ui/Graphics;Lnet/rim/device/api/ui/Font;IIII)V
      // 5f0: aload 0
      // 5f1: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 5f4: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowLayout Lnet/rim/device/apps/internal/browser/ui/TextFlowLayout;
      // 5f7: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowLayout.getLineCellIds ()[S
      // 5fa: astore 10
      // 5fc: aload 0
      // 5fd: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellNesting S
      // 600: ldc_w 65535
      // 603: iand
      // 604: istore 11
      // 606: aload 9
      // 608: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell$Painter.getYPosition ()I
      // 60b: iload 6
      // 60d: if_icmpge 635
      // 610: aload 9
      // 612: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell$Painter.getTextPosition ()I
      // 615: aload 0
      // 616: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._layoutEndPosition I
      // 619: if_icmpge 635
      // 61c: aload 10
      // 61e: aload 9
      // 620: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell$Painter.getLineNumber ()I
      // 623: saload
      // 624: ldc_w 65535
      // 627: iand
      // 628: iload 11
      // 62a: if_icmplt 635
      // 62d: aload 9
      // 62f: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell$Painter.PaintLine ()V
      // 632: goto 606
      // 635: aload 9
      // 637: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell$Painter.End ()V
      // 63a: aload 1
      // 63b: aload 2
      // 63c: invokevirtual net/rim/device/api/ui/Graphics.setFont (Lnet/rim/device/api/ui/Font;)V
      // 63f: aload 0
      // 640: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._outoflineObjectStart I
      // 643: istore 7
      // 645: aload 0
      // 646: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 649: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 64c: iload 7
      // 64e: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectOffset (I)I
      // 651: aload 0
      // 652: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._layoutEndPosition I
      // 655: if_icmplt 65b
      // 658: goto 83c
      // 65b: aload 0
      // 65c: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 65f: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 662: iload 7
      // 664: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.isOutOfLineLeaf (I)Z
      // 667: ifeq 6f4
      // 66a: aload 0
      // 66b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 66e: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 671: iload 7
      // 673: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.isOutOfLineAltText (I)Z
      // 676: ifne 6f4
      // 679: aload 0
      // 67a: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 67d: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 680: iload 7
      // 682: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObject (I)Ljava/lang/Object;
      // 685: checkcast java/lang/Object
      // 688: astore 9
      // 68a: aload 9
      // 68c: ifnonnull 692
      // 68f: goto 836
      // 692: aload 9
      // 694: invokevirtual net/rim/device/api/ui/Field.getTop ()I
      // 697: iload 6
      // 699: if_icmplt 69f
      // 69c: goto 83c
      // 69f: aload 0
      // 6a0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 6a3: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 6a6: aload 0
      // 6a7: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 6aa: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 6ad: iload 7
      // 6af: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectRegion (I)I
      // 6b2: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getRegionCellOOLIndex (I)I
      // 6b5: aload 0
      // 6b6: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellOOLIndex I
      // 6b9: if_icmpeq 6bf
      // 6bc: goto 836
      // 6bf: aload 9
      // 6c1: invokevirtual net/rim/device/api/ui/Field.getTop ()I
      // 6c4: aload 9
      // 6c6: invokevirtual net/rim/device/api/ui/Field.getHeight ()I
      // 6c9: iadd
      // 6ca: iload 5
      // 6cc: if_icmpgt 6d2
      // 6cf: goto 836
      // 6d2: iload 3
      // 6d3: ldc_w 65536
      // 6d6: if_icmple 6e7
      // 6d9: aload 0
      // 6da: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 6dd: aload 9
      // 6df: iload 3
      // 6e0: aload 1
      // 6e1: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.paintScaledField (Lnet/rim/device/api/ui/Field;ILnet/rim/device/api/ui/Graphics;)V
      // 6e4: goto 836
      // 6e7: aload 0
      // 6e8: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 6eb: aload 1
      // 6ec: aload 9
      // 6ee: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.paintField (Lnet/rim/device/api/ui/Graphics;Lnet/rim/device/api/ui/Field;)V
      // 6f1: goto 836
      // 6f4: aload 0
      // 6f5: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 6f8: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 6fb: aload 0
      // 6fc: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 6ff: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 702: iload 7
      // 704: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObjectRegion (I)I
      // 707: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getRegionCellOOLIndex (I)I
      // 70a: aload 0
      // 70b: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellOOLIndex I
      // 70e: if_icmpeq 714
      // 711: goto 836
      // 714: aload 0
      // 715: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 718: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 71b: iload 7
      // 71d: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.isOutOfLineCell (I)Z
      // 720: ifne 726
      // 723: goto 836
      // 726: aload 0
      // 727: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 72a: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._textFlowData Lnet/rim/device/apps/internal/browser/ui/TextFlowData;
      // 72d: iload 7
      // 72f: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowData.getOutOfLineObject (I)Ljava/lang/Object;
      // 732: checkcast net/rim/device/apps/internal/browser/ui/TextFlowCell
      // 735: astore 9
      // 737: aload 9
      // 739: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell.getHeight ()I
      // 73c: aload 9
      // 73e: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._minHeight I
      // 741: invokestatic java/lang/Math.max (II)I
      // 744: istore 10
      // 746: iload 10
      // 748: iflt 74f
      // 74b: bipush 1
      // 74c: goto 750
      // 74f: bipush 0
      // 750: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionAssert (Z)V
      // 753: aload 9
      // 755: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 758: iload 10
      // 75a: iadd
      // 75b: iload 5
      // 75d: if_icmpgt 763
      // 760: goto 82d
      // 763: iload 10
      // 765: ifgt 76b
      // 768: goto 82d
      // 76b: aload 9
      // 76d: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 770: iload 6
      // 772: if_icmplt 778
      // 775: goto 83c
      // 778: aload 9
      // 77a: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.updateAnimationOffset ()V
      // 77d: iload 10
      // 77f: aload 9
      // 781: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 784: isub
      // 785: aload 9
      // 787: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowStart I
      // 78a: ifne 795
      // 78d: aload 9
      // 78f: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 792: goto 796
      // 795: bipush 0
      // 796: isub
      // 797: istore 10
      // 799: aload 9
      // 79b: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell.getWidth ()I
      // 79e: aload 9
      // 7a0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 7a3: isub
      // 7a4: aload 9
      // 7a6: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._colStart I
      // 7a9: ifne 7b4
      // 7ac: aload 9
      // 7ae: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 7b1: goto 7b5
      // 7b4: bipush 0
      // 7b5: isub
      // 7b6: istore 11
      // 7b8: aload 9
      // 7ba: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._x I
      // 7bd: aload 9
      // 7bf: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._colStart I
      // 7c2: ifne 7cd
      // 7c5: aload 9
      // 7c7: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 7ca: goto 7ce
      // 7cd: bipush 0
      // 7ce: iadd
      // 7cf: istore 12
      // 7d1: aload 9
      // 7d3: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 7d6: aload 9
      // 7d8: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowStart I
      // 7db: ifne 7e6
      // 7de: aload 9
      // 7e0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 7e3: goto 7e7
      // 7e6: bipush 0
      // 7e7: iadd
      // 7e8: istore 13
      // 7ea: bipush 0
      // 7eb: istore 14
      // 7ed: aload 9
      // 7ef: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 7f2: ifnull 7ff
      // 7f5: aload 9
      // 7f7: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._animationProperties Lnet/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties;
      // 7fa: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell$AnimationProperties._animationOffset I
      // 7fd: istore 14
      // 7ff: aload 1
      // 800: iload 12
      // 802: iload 3
      // 803: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 806: iload 13
      // 808: iload 3
      // 809: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 80c: iload 11
      // 80e: iload 3
      // 80f: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 812: iload 10
      // 814: iload 3
      // 815: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 818: iload 14
      // 81a: bipush 0
      // 81b: invokevirtual net/rim/device/api/ui/Graphics.pushContext (IIIIII)Z
      // 81e: ifeq 829
      // 821: aload 9
      // 823: aload 1
      // 824: aload 2
      // 825: iload 3
      // 826: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowCell.paint (Lnet/rim/device/api/ui/Graphics;Lnet/rim/device/api/ui/Font;I)V
      // 829: aload 1
      // 82a: invokevirtual net/rim/device/api/ui/Graphics.popContext ()V
      // 82d: aload 9
      // 82f: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.getOutOfLineObjectEnd ()I
      // 832: bipush 1
      // 833: isub
      // 834: istore 7
      // 836: iinc 7 1
      // 839: goto 645
      // 83c: aload 0
      // 83d: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._border Lnet/rim/device/internal/ui/Border;
      // 840: ifnonnull 846
      // 843: goto 8e3
      // 846: aload 0
      // 847: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowStart I
      // 84a: ifne 854
      // 84d: aload 0
      // 84e: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 851: goto 855
      // 854: bipush 0
      // 855: istore 9
      // 857: aload 0
      // 858: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._colStart I
      // 85b: ifne 865
      // 85e: aload 0
      // 85f: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._cellSpacing I
      // 862: goto 866
      // 865: bipush 0
      // 866: istore 10
      // 868: aload 0
      // 869: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._contentWidth I
      // 86c: aload 0
      // 86d: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.getTotalWidthPadding ()I
      // 870: iadd
      // 871: aload 0
      // 872: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._border Lnet/rim/device/internal/ui/Border;
      // 875: invokevirtual net/rim/device/internal/ui/Border.getLeft ()I
      // 878: iadd
      // 879: aload 0
      // 87a: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._border Lnet/rim/device/internal/ui/Border;
      // 87d: invokevirtual net/rim/device/internal/ui/Border.getRight ()I
      // 880: iadd
      // 881: istore 11
      // 883: aload 0
      // 884: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowHeightPaddingTop I
      // 887: aload 0
      // 888: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._rowHeightPaddingBottom I
      // 88b: iadd
      // 88c: aload 0
      // 88d: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._contentHeight I
      // 890: iadd
      // 891: aload 0
      // 892: invokespecial net/rim/device/apps/internal/browser/ui/TextFlowCell.getTotalHeightPadding ()I
      // 895: iadd
      // 896: aload 0
      // 897: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._border Lnet/rim/device/internal/ui/Border;
      // 89a: invokevirtual net/rim/device/internal/ui/Border.getTop ()I
      // 89d: iadd
      // 89e: aload 0
      // 89f: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._border Lnet/rim/device/internal/ui/Border;
      // 8a2: invokevirtual net/rim/device/internal/ui/Border.getBottom ()I
      // 8a5: iadd
      // 8a6: istore 12
      // 8a8: aload 0
      // 8a9: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 8ac: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._borderPaint Lnet/rim/device/api/ui/XYRect;
      // 8af: aload 0
      // 8b0: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._x I
      // 8b3: iload 10
      // 8b5: iadd
      // 8b6: iload 3
      // 8b7: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 8ba: aload 0
      // 8bb: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._y I
      // 8be: iload 9
      // 8c0: iadd
      // 8c1: iload 3
      // 8c2: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 8c5: iload 11
      // 8c7: iload 3
      // 8c8: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 8cb: iload 12
      // 8cd: iload 3
      // 8ce: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.fixed32DivToInt (II)I
      // 8d1: invokevirtual net/rim/device/api/ui/XYRect.set (IIII)V
      // 8d4: aload 0
      // 8d5: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._border Lnet/rim/device/internal/ui/Border;
      // 8d8: aload 1
      // 8d9: aload 0
      // 8da: getfield net/rim/device/apps/internal/browser/ui/TextFlowCell._manager Lnet/rim/device/apps/internal/browser/ui/TextFlowManager;
      // 8dd: getfield net/rim/device/apps/internal/browser/ui/TextFlowManager._borderPaint Lnet/rim/device/api/ui/XYRect;
      // 8e0: invokevirtual net/rim/device/internal/ui/Border.paint (Lnet/rim/device/api/ui/Graphics;Lnet/rim/device/api/ui/XYRect;)V
      // 8e3: return
      // try (101 -> 323): 537 null
      // try (335 -> 524): 537 null
      // try (101 -> 323): 553 null
      // try (335 -> 524): 553 null
      // try (101 -> 323): 569 null
      // try (335 -> 524): 569 null
      // try (101 -> 323): 585 null
      // try (335 -> 524): 585 null
      // try (537 -> 541): 585 null
      // try (553 -> 557): 585 null
      // try (569 -> 573): 585 null
      // try (585 -> 586): 585 null
   }

   public final int getVisibleTextPosition(int index, int y, int direction) {
      this._preferredSelectX = -1;
      if (this._textStartPosition == this._layoutEndPosition) {
         return -1;
      }

      int oolIndex = this.findTextFlowCell(y);
      Object aCell = null;
      if (oolIndex > index) {
         aCell = this._manager._textFlowData.getOutOfLineObject(oolIndex);
      }

      if (aCell instanceof TextFlowCell && aCell != this) {
         return ((TextFlowCell)aCell).getVisibleTextPosition(oolIndex, y, direction);
      }

      int pos = this.getTextPositionFromYOffset(this._cellOOLIndex, y, false);
      this.cacheFromTextPosition(pos);
      int line = this._cacheTextLine;
      int lineCount = this._manager._textFlowLayout.getLineCount();
      int lineY = this._cachePixelLine + this._rowHeightPaddingTop;
      short[] heights = this._manager._textFlowLayout.getLineHeights();
      short[] lengths = this._manager._textFlowLayout.getLineLengths();
      if (direction >= 0) {
         while (lineY < y) {
            pos += lengths[line];
            lineY += heights[line];
            if (++line == lineCount) {
               return -1;
            }
         }

         pos = this.getNextTextCharacter(pos, 1, true);
      } else {
         while (lineY + heights[line] > y + 1) {
            if (--line < 0) {
               return -1;
            }

            pos -= lengths[line];
            lineY -= heights[line];
         }

         pos = this.getNextTextCharacter(pos, -1, true);
      }

      return pos;
   }

   final boolean nextSelectionPosition(SelectionPosition position, int amount, boolean horizontal, boolean allowImageSelection) {
      if (this._manager._textFlowData.getRegionCount() == 0) {
         return false;
      }

      int oldTextPos = position.textPosition;
      int oldSelectedRegion = position.selectedRegion;
      if (position.selectedRegion != -1) {
         position.textPosition = this._manager._textFlowData.getRegionStartOffset(position.selectedRegion);
         position.selectedRegion = -1;
      }

      if (position.textPosition < 0) {
         position.textPosition = 0;
      }

      int index = this.findTextFlowCellFromTextPos(position.textPosition);
      TextFlowCell cell;
      if (index != -1) {
         cell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(index);
      } else {
         cell = this;
      }

      if (cell.nextSelectionPositionInternal(position, amount, horizontal, allowImageSelection)) {
         if (!horizontal && position.selectedRegion == -1) {
            index = this.findTextFlowCellFromTextPos(position.textPosition);
            if (index != -1) {
               cell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(index);
            } else {
               cell = this;
            }

            cell.nextSelectionPositionClosestInternal(position);
            return true;
         } else {
            return true;
         }
      } else {
         position.textPosition = oldTextPos;
         position.selectedRegion = oldSelectedRegion;
         return false;
      }
   }

   public final int getNextTextCharacter(int position, int direction, boolean checkCurrent) {
      TextFlowData textFlowData = this._manager._textFlowData;
      if (!checkCurrent) {
         position += direction;
      }

      StringBuffer text = textFlowData.getText();
      if (direction == 0) {
         direction = 1;
      }

      for (int textLength = text.length(); position >= 0 && position < textLength; position += direction) {
         int region = textFlowData.getLastRegionIndexFromTextPosition(position);
         if (textFlowData.isRegionText(region)) {
            return position;
         }
      }

      return -1;
   }

   public final int getTextPositionFromYOffset(int index, int y, boolean lineEnd) {
      return this.getTextPositionFromXYOffset(index, 0, y, lineEnd);
   }

   public final int getTextPositionFromXYOffset(int index, int x, int y, boolean lineEnd) {
      if (this._manager._textFlowLayout.getLineCount() == 0) {
         return 0;
      }

      int oolIndex = this.findTextFlowCell(x, y);
      Object aCell = null;
      if (oolIndex > index) {
         aCell = this._manager._textFlowData.getOutOfLineObject(oolIndex);
      }

      if (aCell instanceof TextFlowCell && aCell != this) {
         return ((TextFlowCell)aCell).getTextPositionFromXYOffset(oolIndex, x, y, lineEnd);
      }

      this.cacheFromYOffset(y);
      if (this._cacheTextOffset == this._layoutEndPosition) {
         this.cacheFromTextPosition(this._layoutEndPosition - 1);
      }

      if (lineEnd) {
         int lineLength = this._manager._textFlowLayout.getLineLengths()[this._cacheTextLine];
         return lineLength == 0 ? this._cacheTextOffset : this._cacheTextOffset + lineLength - 1;
      } else {
         return x != 0 ? this._manager._painter.GetTextOffsetOfXPosition(this, this._cacheTextOffset, x) : this._cacheTextOffset;
      }
   }

   public final int getYOffsetFromTextPosition(int textPosition, boolean top) {
      if (this._manager._textFlowLayout.getLineCount() == 0) {
         return 0;
      }

      int index = this.findTextFlowCellFromTextPos(textPosition);
      TextFlowCell cell;
      if (index != -1) {
         cell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(index);
      } else {
         cell = this;
      }

      return cell.getYOffsetFromTextPositionInternal(textPosition, top);
   }

   public final boolean reflow(Object lockObj) {
      this._reflow = false;
      if (this._parent != null && (this._flags & 16) == 0) {
         int availableWidth = this._parent.getContentWidth();
         int colCount = 0;
         int rowCount = 0;
         if (this._cellCollection != null && (colCount = this._cellCollection.getColCount()) >= 1 && this._cellCollection.getRowCount() >= 1) {
            int borderAndPadding = this.getTotalWidthPadding();
            if (this._border != null) {
               borderAndPadding += this._border.getLeft();
               borderAndPadding += this._border.getRight();
            }

            if (availableWidth - borderAndPadding < 0) {
               return false;
            }

            int[] calculatedWidths = new int[colCount];
            int[] maxWidths = new int[colCount];
            int[] preferredWidths = new int[colCount];
            int[] minWidths = new int[colCount];
            this.determinePreferredMinMaxCellWidths(preferredWidths, minWidths, maxWidths);
            int minTableWidth = borderAndPadding;
            int maxTableWidth = borderAndPadding;
            int minTableWidthWithPreferred = borderAndPadding;
            int maxTableWidthWithPreferred = borderAndPadding;
            int numPreferred = 0;

            for (int i = 0; i < colCount; i++) {
               minTableWidth += minWidths[i];
               maxTableWidth += maxWidths[i];
               if (preferredWidths[i] == -1) {
                  minTableWidthWithPreferred += minWidths[i];
                  maxTableWidthWithPreferred += maxWidths[i];
               } else {
                  minTableWidthWithPreferred += preferredWidths[i];
                  maxTableWidthWithPreferred += preferredWidths[i];
                  numPreferred++;
               }
            }

            int specifiedWidth = this.getSpecifiedWidth();
            if (specifiedWidth == -1) {
               if (numPreferred == colCount && maxTableWidthWithPreferred <= availableWidth) {
                  this.setWidth(maxTableWidthWithPreferred);

                  for (int i = 0; i < colCount; i++) {
                     if (preferredWidths[i] == -1) {
                        calculatedWidths[i] = maxWidths[i];
                     } else {
                        calculatedWidths[i] = preferredWidths[i];
                     }
                  }

                  return this.setTableCellWidths(lockObj, calculatedWidths);
               } else {
                  if (maxTableWidth <= availableWidth) {
                     this.setWidth(maxTableWidth);
                     System.arraycopy(maxWidths, 0, calculatedWidths, 0, colCount);
                     return this.setTableCellWidths(lockObj, calculatedWidths);
                  }

                  if (minTableWidth > availableWidth) {
                     this.setWidth(availableWidth);
                     return this.setTableCellWidths(lockObj, maxWidths);
                  }

                  this.setWidth(availableWidth);
                  int diff = availableWidth - minTableWidth;
                  int maxDiff = maxTableWidth - minTableWidth;

                  for (int i = 0; i < colCount; i++) {
                     int colDiff = maxWidths[i] - minWidths[i];
                     calculatedWidths[i] = minWidths[i] + colDiff * diff / maxDiff;
                  }

                  return this.setTableCellWidths(lockObj, calculatedWidths);
               }
            } else {
               if (specifiedWidth > availableWidth) {
                  this.setWidth(availableWidth);
                  return false;
               }

               if (specifiedWidth < minTableWidth) {
                  this.setWidth(minTableWidth);
                  System.arraycopy(minWidths, 0, calculatedWidths, 0, colCount);
               } else {
                  int maxDiff = maxTableWidth - minTableWidth;
                  if (numPreferred > 0 && specifiedWidth >= minTableWidthWithPreferred) {
                     if (numPreferred == colCount) {
                        if (maxDiff > 0) {
                           int diff = specifiedWidth - minTableWidthWithPreferred;
                           this.growCellsProportionally(maxDiff, diff, minWidths, maxWidths, preferredWidths, calculatedWidths);
                        } else {
                           int diff = (specifiedWidth - minTableWidthWithPreferred) / colCount;
                           this.growCellsEqually(diff, minWidths, preferredWidths, calculatedWidths);
                        }
                     } else {
                        int maxNonPreferred = 0;
                        int minNonPreferred = 0;

                        for (int i = 0; i < colCount; i++) {
                           if (preferredWidths[i] == -1) {
                              maxNonPreferred += maxWidths[i];
                              minNonPreferred += minWidths[i];
                           }
                        }

                        maxDiff = maxNonPreferred - minNonPreferred;
                        if (maxDiff > 0) {
                           int diff = specifiedWidth - minTableWidthWithPreferred;

                           for (int i = 0; i < colCount; i++) {
                              if (preferredWidths[i] == -1) {
                                 int colDiff = maxWidths[i] - minWidths[i];
                                 calculatedWidths[i] = minWidths[i] + colDiff * diff / maxDiff;
                              } else {
                                 calculatedWidths[i] = preferredWidths[i];
                              }
                           }
                        } else {
                           int diff = (specifiedWidth - minTableWidthWithPreferred) / (colCount - numPreferred);

                           for (int i = 0; i < colCount; i++) {
                              if (preferredWidths[i] == -1) {
                                 calculatedWidths[i] = minWidths[i] + diff;
                              } else {
                                 calculatedWidths[i] = preferredWidths[i];
                              }
                           }
                        }
                     }
                  } else if (maxDiff > 0) {
                     int diff = specifiedWidth - minTableWidth;
                     this.growCellsProportionally(maxDiff, diff, minWidths, maxWidths, minWidths, calculatedWidths);
                  } else {
                     int diff = (specifiedWidth - minTableWidth) / colCount;
                     this.growCellsEqually(diff, minWidths, minWidths, calculatedWidths);
                  }

                  this.setWidth(specifiedWidth);
               }

               return this.setTableCellWidths(lockObj, calculatedWidths);
            }
         } else {
            int specifiedWidth = this.getSpecifiedWidth();
            if (specifiedWidth != -1 && specifiedWidth != this._contentWidth && specifiedWidth <= this._parent._contentWidth) {
               this.setWidth(specifiedWidth, false);
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public final long getExecutionTime() {
      return this._animationProperties._nextAnimationTime;
   }

   @Override
   public final boolean animate() {
      synchronized (Application.getEventLock()) {
         if (this._animationProperties == null || this._animationProperties._loopCount <= 0) {
            return false;
         }

         this.updateAnimationOffset();
         if ((this._animationProperties._animationStyle & 1) != 0) {
            return false;
         }

         int yTop = this._manager.getVerticalScroll();
         int yBottom = yTop + this._manager.getHeight();
         int y2 = this._y + this.getHeight();
         if ((this._y < yTop || this._y > yBottom) && (y2 < yTop || y2 > yBottom)) {
            this._animationProperties._animationStyle ^= 1;
            return false;
         }

         if ((this._animationProperties._animationStyle & 32) == 0) {
            if ((this._animationProperties._animationStyle & 64) == 0) {
               if ((this._animationProperties._animationStyle & 16) != 0) {
                  this._animationProperties._drawText = !this._animationProperties._drawText;
               }
            } else if ((this._animationProperties._animationStyle & 4) != 0
               && this._animationProperties._animationOffset + this._animationProperties._scrollAmount + this._animationProperties._maxLineWidth
                  > this._contentWidth
               && this._animationProperties._animationOffset > 0) {
               TextFlowCell$AnimationProperties.access$210(this._animationProperties);
               if (this._animationProperties._loopCount > 0) {
                  this._animationProperties._animationOffset = -this._animationProperties._maxLineWidth;
               } else {
                  this._animationProperties._animationOffset = this._contentWidth - this._animationProperties._maxLineWidth;
               }
            } else if ((this._animationProperties._animationStyle & 8) != 0
               && this._animationProperties._animationOffset + this._animationProperties._scrollAmount + this._animationProperties._maxLineWidth
                  > this._contentWidth
               && this._animationProperties._animationOffset > 0) {
               TextFlowCell$AnimationProperties.access$210(this._animationProperties);
               this._animationProperties._animationStyle ^= 64;
               this._animationProperties._animationStyle |= 32;
               this._animationProperties._animationOffset = Math.max(this._contentWidth - this._animationProperties._maxLineWidth, 0);
            } else if (this._animationProperties._animationOffset > this._contentWidth) {
               TextFlowCell$AnimationProperties.access$210(this._animationProperties);
               this._animationProperties._animationOffset = -this._animationProperties._maxLineWidth;
            } else {
               TextFlowCell$AnimationProperties.access$512(this._animationProperties, this._animationProperties._scrollAmount);
            }
         } else if ((this._animationProperties._animationStyle & 4) != 0
            && this._animationProperties._animationOffset - this._animationProperties._scrollAmount < 0) {
            TextFlowCell$AnimationProperties.access$210(this._animationProperties);
            if (this._animationProperties._loopCount > 0) {
               this._animationProperties._animationOffset = this._contentWidth;
            } else {
               this._animationProperties._animationOffset = 0;
            }
         } else if ((this._animationProperties._animationStyle & 8) != 0
            && this._animationProperties._animationOffset + this._animationProperties._scrollAmount + this._animationProperties._maxLineWidth
               < this._contentWidth
            && this._animationProperties._animationOffset - this._animationProperties._scrollAmount < 0) {
            TextFlowCell$AnimationProperties.access$210(this._animationProperties);
            this._animationProperties._animationStyle ^= 32;
            this._animationProperties._animationStyle |= 64;
            this._animationProperties._animationOffset = Math.min(this._contentWidth - this._animationProperties._maxLineWidth, 0);
         } else if (this._animationProperties._animationOffset < -this._animationProperties._maxLineWidth) {
            TextFlowCell$AnimationProperties.access$210(this._animationProperties);
            this._animationProperties._animationOffset = this._contentWidth;
         } else {
            TextFlowCell$AnimationProperties.access$520(this._animationProperties, this._animationProperties._scrollAmount);
         }

         if ((this._flags & 32) == 0 && this.isBackgroundImageSet()) {
            this._manager.invalidate();
         } else {
            this._manager.invalidate(this._x, this._y, this.getWidth(), this.getHeight());
         }
      }

      this._animationProperties._nextAnimationTime = System.currentTimeMillis() + this._animationProperties._scrollDelay;
      return this._animationProperties._loopCount > 0;
   }

   @Override
   public final void addAnimationListener(AnimationListener a) {
   }

   @Override
   public final void removeAnimationListener(AnimationListener a) {
   }

   @Override
   public final void setTableItems(int colStart, int colEnd, int rowStart, int rowEnd) {
      this._colStart = colStart;
      this._colEnd = colEnd;
      this._rowStart = rowStart;
      this._rowEnd = rowEnd;
   }

   private final void setWidth(int width, boolean subtractBorder) {
      int previousWidth = this._contentWidth;
      if (this._parent != null && width > this._parent._contentWidth) {
         if ((this._parent._flags & 16) == 0) {
            this._parent.setWidth(width, false);
         }

         if (width > this._parent._contentWidth) {
            width = this._parent._contentWidth;
         }
      }

      int spacing = 0;
      if (subtractBorder) {
         spacing = this._cellSpacing;
         if (this._colStart == 0) {
            spacing <<= 1;
         }
      }

      int borderWidth = subtractBorder && this._border != null ? this._border.getLeft() + this._border.getRight() : 0;
      int padding = subtractBorder ? this.getTotalWidthPadding() : 0;
      this._contentWidth = Math.max(0, width - borderWidth - padding - spacing);
      if (this._cellOOLIndex != -1 && previousWidth != this._contentWidth) {
         int start = this._manager._textFlowData.getOutOfLineObjectOffset(this._cellOOLIndex);
         this._manager.setInvalidStartEnd(start, start + this._manager._textFlowData.getOutOfLineObjectLength(this._cellOOLIndex));
      }
   }

   private final int calculateActiveObjectsEnd(TextFlowCell$ExtentStack objects) {
      int end = Integer.MAX_VALUE;
      if (!objects.empty(this._cellNesting)) {
         end = objects.getTop(this._cellNesting) + objects.getHeight(this._cellNesting);
      }

      return end;
   }

   private final void resetLayoutState() {
      this._manager._leftObjects.clear(this._cellNesting);
      this._manager._rightObjects.clear(this._cellNesting);
      this._leftEnd = Integer.MAX_VALUE;
      this._rightEnd = Integer.MAX_VALUE;
      this._left = this._x
         + (this._border != null ? this._border.getLeft() : 0)
         + (this._cellPadding >> 0 & 0xFF)
         + (this._colStart == 0 ? this._cellSpacing : 0);
      this._right = this._left;
   }

   private final void resetLayoutStateToOOLIndex(int oolIndex) {
      this.resetLayoutToOOLIndex(this._manager._leftObjects, oolIndex, true);
      this.resetLayoutToOOLIndex(this._manager._rightObjects, oolIndex, false);
   }

   private final void resetLayoutToOOLIndex(TextFlowCell$ExtentStack objects, int oolIndexToCheck, boolean left) {
      for (int i = objects.size(this._cellNesting) - 1; i >= 0; i--) {
         int oolIndex = objects.getOOLIndex(this._cellNesting, i);
         objects.pop(this._cellNesting);
         if (oolIndex == oolIndexToCheck) {
            break;
         }
      }

      int objectSize = objects.size(this._cellNesting);
      int yValue;
      int xValue;
      if (objectSize > 0) {
         xValue = objects.getLeft(this._cellNesting, objectSize - 1) + objects.getWidth(this._cellNesting, objectSize - 1);
         yValue = objects.getTop(this._cellNesting, objectSize - 1) + objects.getHeight(this._cellNesting, objectSize - 1);
      } else {
         xValue = this._x
            + (this._border != null ? this._border.getLeft() : 0)
            + (this._cellPadding >> 0 & 0xFF)
            + (this._colStart == 0 ? this._cellSpacing : 0);
         yValue = Integer.MAX_VALUE;
      }

      if (left) {
         this._left = xValue;
         this._leftEnd = yValue;
      } else {
         this._right = xValue;
         this._rightEnd = yValue;
      }
   }

   private final void saveLayoutState() {
      this._manager._leftObjects2.copy(this._cellNesting, this._manager._leftObjects);
      this._manager._rightObjects2.copy(this._cellNesting, this._manager._rightObjects);
      this._leftEnd2 = this._leftEnd;
      this._rightEnd2 = this._rightEnd;
      this._left2 = this._left;
      this._right2 = this._right;
      this._manager._leftObjects.clear(this._cellNesting);
      this._manager._rightObjects.clear(this._cellNesting);
      this.resetLayoutState();
   }

   private final void restoreLayoutState() {
      this._manager._leftObjects.copy(this._cellNesting, this._manager._leftObjects2);
      this._manager._rightObjects.copy(this._cellNesting, this._manager._rightObjects2);
      this._leftEnd = this._leftEnd2;
      this._rightEnd = this._rightEnd2;
      this._left = this._left2;
      this._right = this._right2;
      this._manager._leftObjects2.clear(this._cellNesting);
      this._manager._rightObjects2.clear(this._cellNesting);
   }

   private final int layoutOutOfLineObject(
      int y, int regionWidth, int regionHeight, int outoflineIndex, TextFlowNative cellLayout, int invalidLayoutStart, int invalidLayoutEnd
   ) {
      int consumeChars = this._manager._textFlowData.getOutOfLineObjectLength(outoflineIndex);
      int regionIndex = this._manager._textFlowData.getOutOfLineObjectRegion(outoflineIndex);
      int realStartPos = this._manager._textFlowData.getRegionStartOffset(regionIndex);
      if ((this._manager._textFlowData.getRegion(regionIndex).getFlags() & 1024) == 0 && !this._manager._textFlowData.isOutOfLineNewline(outoflineIndex)) {
         int consumeWidth = 0;
         Field field = null;
         TextFlowCell child = null;
         int width;
         int margin;
         if (!this._manager._textFlowData.isOutOfLineCell(outoflineIndex)) {
            field = (Field)this._manager._textFlowData.getOutOfLineObject(outoflineIndex);
            if (this._manager._textFlowData.isOutOfLineDirty(outoflineIndex)) {
               this._manager.layoutField(field, this._contentWidth, 1073741823);
               this._manager._textFlowData.setOutOfLineDirty(outoflineIndex, false);
            }

            width = field.getWidth();
            margin = this._manager._textFlowData.getRegionMargin(regionIndex);
            if (width + (margin << 1) <= this._contentWidth) {
               width += margin << 1;
            }
         } else {
            child = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(outoflineIndex);
            int oldContentWidth = this._contentWidth;
            if ((child._flags & 16) == 0 && (child._reflow && child.reflow(null)) | child.adjustContentWidth(regionWidth, this._contentWidth)) {
               if (this._contentWidth != oldContentWidth) {
                  return 0;
               }

               invalidLayoutStart = child.getTextStartPosition();
               invalidLayoutEnd = child.getTextEnd();
            }

            width = child.getWidth();
            margin = 0;
         }

         if (child != null && this._currentRow < child._rowStart) {
            if (this._currentRow != -1) {
               while (this._currentRow < child._rowStart) {
                  this.resetLayoutState();
                  this.pushRemainingCells(this._currentRow, 0);
                  this.fixupLastRow(cellLayout, y, 0);
               }

               return 0;
            }

            this._currentRow = child._rowStart;
         }

         if (width > regionWidth && regionWidth != this._contentWidth) {
            cellLayout.appendVerticalSpace(y, this._lastLayoutStartLine, regionHeight, (short)this._left, (short)regionWidth);
            consumeChars = 0;
         } else {
            long fieldAlignment = 4294967296L;
            int height;
            int rowEnd;
            if (field != null) {
               cellLayout.appendZeroHeightCharacters(y, this._lastLayoutStartLine, consumeChars, (short)this._left, (short)regionWidth);
               height = field.getHeight();
               fieldAlignment = field.getStyle();
               rowEnd = this._currentRow;
            } else {
               Asserts.productionStateAssert(child._parent == this);
               Asserts.productionStateAssert(this._currentRow == child._rowStart);
               int childStart = child.getTextStartPosition();
               if (cellLayout.getStartLine() != -1) {
                  invalidLayoutStart = childStart;
               }

               if (invalidLayoutStart <= childStart) {
                  cellLayout.appendZeroHeightCharacters(y, this._lastLayoutStartLine, childStart - realStartPos, (short)this._left, (short)regionWidth);
                  invalidLayoutStart = childStart;
               }

               cellLayout.pushCell(child._cellNesting);
               int xPosOfTable = this._left;
               int halignMasked = child._flags & 6144;
               switch (halignMasked) {
                  case 4096:
                     if (child._cellCollection != null) {
                        xPosOfTable = this._left + (regionWidth - width >> 1);
                     }
                     break;
                  case 6144:
                     xPosOfTable = this._left + (regionWidth - width);
                     fieldAlignment = 8589934592L;
               }

               child.setPosition(xPosOfTable, y);
               child.layout0(cellLayout, invalidLayoutStart, invalidLayoutEnd);
               cellLayout.popCell(this._cellNesting);
               this._manager._textFlowData.setOutOfLineDirty(outoflineIndex, false);
               height = Math.max(child.getHeight(), child._minHeight);
               if (height == 0) {
                  height = 1;
               }

               if (halignMasked == 0 || halignMasked == 4096) {
                  if (child._cellCollection != null) {
                     width = regionWidth;
                  }

                  if (width == regionWidth) {
                     consumeWidth += this.calculateActiveObjectsToDeactivate(this._manager._leftObjects, y + height, false);
                     consumeWidth += this.calculateActiveObjectsToDeactivate(this._manager._rightObjects, y + height, false);
                  }
               }

               if (cellLayout.getStartLine() == -1) {
                  this._lastLayoutStartLine = this._manager._textFlowLayout.getLineCellEnd(child._cellNesting) + 1;
               }

               rowEnd = child._rowEnd;
            }

            TextFlowCell$ExtentStack objects;
            int x;
            int objectX;
            int objectWidth;
            switch ((int)((fieldAlignment & 12884901888L) >> 32)) {
               case 1:
                  objects = this._manager._leftObjects;
                  x = this._left;
                  objectX = x;
                  objectWidth = width;
                  this._left += width + consumeWidth;
                  this._leftEnd = y + height;
                  break;
               case 2:
               default:
                  objects = this._manager._rightObjects;
                  this._right += width;
                  x = this._left + (regionWidth - width);
                  objectX = x;
                  objectWidth = width;
                  this._rightEnd = y + height;
                  break;
               case 3:
                  objects = this._manager._leftObjects;
                  objectX = this._left;
                  objectWidth = regionWidth;
                  x = this._left + (regionWidth - width >> 1);
                  this._left += regionWidth;
                  this._leftEnd = y + height;
            }

            objects.push(this._cellNesting, objectX, y, objectWidth + consumeWidth, height, rowEnd, outoflineIndex);
            int maxX = objectX + objectWidth;
            if (maxX > cellLayout.getMaxXOffset()) {
               cellLayout.setMaxXOffset(maxX);
            }

            TextFlowRegion region = this._manager._textFlowData.getRegion(regionIndex);
            region.setPosition(x + margin, y, x + margin + width, y + height);
            if (field != null) {
               this._manager.placeField(field, x + margin, y);
            } else if (this._cellCollection != null) {
               this.pushRemainingCells(this._currentRow - 1, child._colEnd);
               boolean foundAsLast = true;

               for (int i = this._cellCollection.getColCount() - 1; i >= 0; i--) {
                  Object obj = this._cellCollection.getCell(this._currentRow, i);
                  if (obj != null) {
                     foundAsLast = child == obj;
                     break;
                  }
               }

               if (foundAsLast) {
                  this.fixupLastRow(cellLayout, y, 0);
               }
            }
         }
      } else {
         this.deactivateActiveObjects(this._manager._leftObjects, this._manager._rightObjects, y, this._contentWidth, Integer.MAX_VALUE, cellLayout, true);
         cellLayout.appendZeroHeightCharacters(y, this._lastLayoutStartLine, consumeChars, (short)0, (short)this._contentWidth);
      }

      return consumeChars;
   }

   private final int getSpecifiedWidth() {
      this.updateSpecifiedWidth();
      return this._specifiedWidthCalculated == -1 ? -1 : this._specifiedWidthCalculated + this.getPaddingBorderSpacingWidth();
   }

   public TextFlowCell(TextFlowManager manager) {
      this(manager, -1, null, -1, 0, 0, null, false, 16, (short)0, -1, -1);
   }

   private final void layout0(TextFlowNative cellLayout, int invalidLayoutStart, int invalidLayoutEnd) {
      TextFlowData textFlowData = this._manager._textFlowData;
      int textEndRegion = this.getTextEndRegion();
      int textEndPositionData = this.getTextEnd();
      TextFlowCell startCell = null;
      int lineCountStart = this._manager._textFlowLayout.getLineCellStart(this._cellNesting);
      if (lineCountStart < 0) {
         lineCountStart = this._manager._textFlowLayout.getLineCount();
      }

      if (this._lastLayoutWidth != this._contentWidth) {
         invalidLayoutStart = this._textStartPosition;
         invalidLayoutEnd = textEndPositionData;
      }

      if ((this._cellNesting & '\uffff') > 0 && invalidLayoutStart <= this._textStartPosition && this._textStartPosition == textEndPositionData) {
         cellLayout.appendZeroHeightZeroWidthCharacter(this._y, lineCountStart, (short)0);
      }

      if (invalidLayoutStart >= textEndPositionData) {
         this._flags &= -65;
         if (this._parent == null) {
            this._manager._lastLayoutEndY = this._contentHeight;
         }

         this._lastLayoutStartLine = lineCountStart;
         cellLayout.setEndLine(lineCountStart);
         this._lastLayoutWidth = this._contentWidth;
         this._lastLayoutYPos = this._y
            + (this._cellPadding >> 16 & 0xFF)
            + (this._border != null ? this._border.getTop() : 0)
            + (this._rowStart == 0 ? this._cellSpacing : 0);
      } else {
         if (invalidLayoutEnd < this._layoutEndPosition) {
            this.saveLayoutState();
         }

         int startPosition;
         if (this._cellOOLIndex >= 0 && textFlowData.isOutOfLineDirty(this._cellOOLIndex)) {
            startPosition = this._textStartPosition;
            invalidLayoutEnd = textEndPositionData;
         } else if (invalidLayoutStart <= this._textStartPosition) {
            startPosition = this._textStartPosition;
         } else {
            int regionId = textFlowData.getFirstRegionIndexFromTextPosition(invalidLayoutStart);
            int anIndex = textFlowData.getRegionCellOOLIndex(regionId);
            Object aCell = null;
            if (anIndex >= 0) {
               aCell = textFlowData.getOutOfLineObject(anIndex);
            }

            if (aCell instanceof TextFlowCell && aCell != this) {
               TextFlowCell cell = (TextFlowCell)aCell;

               while (cell._parent != this) {
                  cell = cell._parent;
               }

               startPosition = textFlowData.getOutOfLineObjectOffset(cell._cellOOLIndex);
               this.resetLayoutStateToOOLIndex(cell._cellOOLIndex);
               startCell = cell;
            } else if (invalidLayoutStart == this._layoutEndPosition) {
               this.cacheFromTextPosition(invalidLayoutStart - 1);
               startPosition = this._cacheTextOffset;
               regionId = textFlowData.getFirstRegionIndexFromTextPosition(startPosition);
               anIndex = textFlowData.getRegionCellOOLIndex(regionId);
               aCell = null;
               if (anIndex >= 0) {
                  aCell = textFlowData.getOutOfLineObject(anIndex);
               }

               if (aCell instanceof TextFlowCell && aCell != this) {
                  TextFlowCell cell = (TextFlowCell)aCell;

                  while (cell._parent != this) {
                     cell = cell._parent;
                  }

                  startPosition = textFlowData.getOutOfLineObjectOffset(cell._cellOOLIndex);
                  this.resetLayoutState();
                  startCell = cell;
               } else if (this._manager._textFlowLayout.getLineHeights()[this._cacheTextLine] == 0) {
                  startPosition = invalidLayoutStart;
               }
            } else {
               this.cacheFromTextPosition(invalidLayoutStart - 1);
               startPosition = this._cacheTextOffset;
               int line = this._cacheTextLine;
               if (line < this._manager._textFlowLayout.getLineCount()) {
                  short[] lineLengths = this._manager._textFlowLayout.getLineLengths();
                  short[] lineWidthsNominal = this._manager._textFlowLayout.getLineWidthsNominal();
                  short[] lineCellIds = this._manager._textFlowLayout.getLineCellIds();
                  int currentWidth = lineWidthsNominal[line];

                  while (lineCellIds[line] != this._cellNesting || currentWidth < this._lastLayoutWidth) {
                     if (line == 0 || line < lineCountStart) {
                        startPosition = this._textStartPosition;
                        break;
                     }

                     if (lineCellIds[line] != this._cellNesting) {
                        regionId = textFlowData.getFirstRegionIndexFromTextPosition(startPosition);
                        anIndex = textFlowData.getRegionCellOOLIndex(regionId);
                        aCell = null;
                        if (anIndex >= 0) {
                           aCell = textFlowData.getOutOfLineObject(anIndex);
                        }

                        if (aCell instanceof TextFlowCell && aCell != this) {
                           TextFlowCell cell = (TextFlowCell)aCell;

                           while (cell._parent != this) {
                              cell = cell._parent;
                           }

                           line = this._manager._textFlowLayout.getLineCellStart(cell._cellNesting) - 1;
                           startPosition = textFlowData.getOutOfLineObjectOffset(cell._cellOOLIndex);
                           currentWidth = lineWidthsNominal[line];
                        }
                     }

                     if (currentWidth < this._lastLayoutWidth) {
                        line--;
                        startPosition -= lineLengths[line];
                        if (lineCellIds[line] == this._cellNesting) {
                           currentWidth = lineWidthsNominal[line];
                        }
                     }
                  }
               }

               this.resetLayoutState();
            }
         }

         int startY;
         int lastLayoutStartYPos;
         int startLine;
         int cachedStartY;
         int outoflineIndex;
         if (startPosition <= this._textStartPosition) {
            this.resetLayoutState();
            startY = this._y
               + (this._border != null ? this._border.getTop() : 0)
               + (this._cellPadding >> 16 & 0xFF)
               + (this._rowStart == 0 ? this._cellSpacing : 0);
            startLine = lineCountStart;
            outoflineIndex = this._outoflineObjectStart;
            cachedStartY = 0;
            lastLayoutStartYPos = this._lastLayoutYPos;
         } else if (startCell != null) {
            if (this._cellNesting == 0) {
               int maxX = this._manager._textFlowLayout.getMaxXOffset();
               if (maxX > cellLayout.getMaxXOffset()) {
                  cellLayout.setMaxXOffset(maxX);
               }
            }

            this.updateLayoutStateFromCell(startCell, startPosition, lineCountStart);
            this.cacheFromTextPosition(startPosition);
            startY = this._cachePixelLine;
            startLine = this._cacheTextLine;
            outoflineIndex = startCell._cellOOLIndex;
            cachedStartY = startY - this._lastLayoutYPos;
            lastLayoutStartYPos = startY;
         } else {
            if (this._cellNesting == 0) {
               int maxX = this._manager._textFlowLayout.getMaxXOffset();
               if (maxX > cellLayout.getMaxXOffset()) {
                  cellLayout.setMaxXOffset(maxX);
               }
            }

            this.cacheFromTextPosition(startPosition);
            startY = this._cachePixelLine;
            startLine = this._cacheTextLine;
            outoflineIndex = textFlowData.getOutoflineIndexFromTextPosition(startPosition - 1) + 1;
            cachedStartY = startY - this._lastLayoutYPos;
            lastLayoutStartYPos = startY;
         }

         Asserts.productionStateAssert(cachedStartY >= 0 && startY >= 0);
         this._lastLayoutWidth = this._contentWidth;
         this._lastLayoutStartLine = cellLayout.getStartLine() != -1 ? cellLayout.getStartLine() + cellLayout.getLineCount() : startLine;
         int oolEndData = this.getOutOfLineObjectEnd();
         if (textFlowData.isOutOfLineAltText(outoflineIndex)) {
            outoflineIndex = textFlowData.nextOutOfLineIndex(outoflineIndex, oolEndData, startPosition);
         }

         this._currentRow = -1;
         int nextOutOfLineObjectOffset = textFlowData.getOutOfLineObjectOffset(outoflineIndex);
         int y = startY;
         int textPosition = startPosition;
         int adjustX = this._x
            + (this._border != null ? this._border.getLeft() : 0)
            + (this._cellPadding >> 0 & 0xFF)
            + (this._colStart == 0 ? this._cellSpacing : 0);
         if (this._rowHeightPaddingTop > 0) {
            this.moveRegions(this._textStartRegion, textFlowData.getFirstRegionIndexFromTextPosition(textPosition), 0, -this._rowHeightPaddingTop);
            this.moveOutoflineFields(this._outoflineObjectStart, outoflineIndex, -this._rowHeightPaddingTop);
         }

         while (textPosition < textEndPositionData) {
            y = startY + cellLayout.getCellHeight();
            if (this._leftEnd <= y) {
               this._left = this._left - this.calculateActiveObjectsToDeactivate(this._manager._leftObjects, y, true);
               this._leftEnd = this.calculateActiveObjectsEnd(this._manager._leftObjects);
            }

            if (this._rightEnd <= y) {
               this._right = this._right - this.calculateActiveObjectsToDeactivate(this._manager._rightObjects, y, true);
               this._rightEnd = this.calculateActiveObjectsEnd(this._manager._rightObjects);
            }

            int regionWidth = this._contentWidth - (this._left - adjustX + (this._right - adjustX));
            if (textPosition >= invalidLayoutEnd && regionWidth == this._contentWidth) {
               this.cacheFromTextPosition(textPosition);
               if (this._cacheTextOffset == textPosition && this._manager._textFlowLayout.getLineWidthsNominal()[this._cacheTextLine] >= this._contentWidth) {
                  break;
               }
            }

            int regionHeight = Integer.MAX_VALUE;
            int leftPixelsRemain = this._leftEnd - y;
            int rightPixelsRemain = this._rightEnd - y;
            if (regionHeight > leftPixelsRemain) {
               regionHeight = leftPixelsRemain;
            }

            if (regionHeight > rightPixelsRemain) {
               regionHeight = rightPixelsRemain;
            }

            if (regionHeight == 0 && regionWidth != this._contentWidth) {
               break;
            }

            if (nextOutOfLineObjectOffset != textPosition && (nextOutOfLineObjectOffset == Integer.MAX_VALUE || nextOutOfLineObjectOffset >= textPosition)) {
               int regionIndex = textFlowData.getFirstRegionIndexFromTextPosition(textPosition);
               if (regionWidth < 20 && regionWidth < this._contentWidth) {
                  cellLayout.appendVerticalSpace(y, this._lastLayoutStartLine, regionHeight, (short)this._left, (short)regionWidth);
               } else {
                  int charsAvail;
                  if (textEndPositionData > nextOutOfLineObjectOffset) {
                     charsAvail = nextOutOfLineObjectOffset - textPosition;
                  } else {
                     charsAvail = textEndPositionData - textPosition;
                  }

                  int nextRegionWidth = regionWidth;
                  if (regionHeight == leftPixelsRemain) {
                     nextRegionWidth += this.calculateActiveObjectsToDeactivate(this._manager._leftObjects, y + regionHeight, false);
                  }

                  if (regionHeight == rightPixelsRemain) {
                     nextRegionWidth += this.calculateActiveObjectsToDeactivate(this._manager._rightObjects, y + regionHeight, false);
                  }

                  int nominalTextEnd = Integer.MAX_VALUE;
                  if (regionWidth == this._contentWidth) {
                     nominalTextEnd = invalidLayoutEnd;
                  }

                  int nominalEndTextPosition = textPosition + charsAvail;

                  for (int il = regionIndex; textFlowData.getRegionStartOffset(il) < nominalEndTextPosition; il++) {
                     Object regionObject = textFlowData.getRegionObject(il);
                     if (regionObject != null && textFlowData.isRegionDirty(il)) {
                        if (!(regionObject instanceof Object)) {
                           if (regionObject instanceof TextFlowCell) {
                              textFlowData.setRegionDirty(il, false);
                           }
                        } else {
                           Field field = (Field)regionObject;
                           TextFlowRegion region = textFlowData.getRegion(il);
                           if ((region.getFlags() & 1024) != 0) {
                              this._manager.layoutField(field, 0, 0);
                           } else {
                              this._manager.layoutField(field, this._contentWidth, 32767);
                           }

                           textFlowData.setRegionObjectExtent(il, (short)field.getWidth(), (short)field.getHeight());
                           textFlowData.setRegionDirty(il, false);
                        }
                     }
                  }

                  int inlineRegionIndex = cellLayout.getInlineCount();
                  int startTextLayoutPosition = textPosition;
                  if (this._animationProperties != null || (this._flags & 256) != 0) {
                     regionWidth = Integer.MAX_VALUE;
                  }

                  cellLayout.wordWrap(this._lastLayoutStartLine, this._left, y, regionWidth, regionHeight, textPosition, charsAvail, nominalTextEnd);
                  int charsConsumed = cellLayout.getEndTextPosition() - textPosition;
                  textPosition += charsConsumed;
                  this.placeInlineFields(cellLayout, inlineRegionIndex, regionIndex, startTextLayoutPosition, textPosition);
               }
            } else {
               int consumeChars = this.layoutOutOfLineObject(y, regionWidth, regionHeight, outoflineIndex, cellLayout, invalidLayoutStart, invalidLayoutEnd);
               if (consumeChars > 0) {
                  textPosition += consumeChars;
                  outoflineIndex = textFlowData.nextOutOfLineIndex(outoflineIndex, oolEndData, textPosition);
                  nextOutOfLineObjectOffset = textFlowData.getOutOfLineObjectOffset(outoflineIndex);
               }
            }
         }

         if (textPosition >= textEndPositionData) {
            if (this._parent == null) {
               this._manager._lastLayoutEndY = this._contentHeight;
            }

            this.deactivateActiveObjects(
               this._manager._leftObjects,
               this._manager._rightObjects,
               startY + cellLayout.getCellHeight(),
               this._contentWidth,
               Integer.MAX_VALUE,
               cellLayout,
               false
            );
            if (cellLayout.getCellHeight() == 0
               && cellLayout.getLineCount() > 0
               && cellLayout.getLineCellIds()[cellLayout.getLineCount() - 1] != this._cellNesting) {
               cellLayout.appendZeroHeightZeroWidthCharacter(y, lineCountStart, (short)0);
            }

            this._contentHeight = cachedStartY + cellLayout.getCellHeight();
            this._rowHeightPaddingTop = 0;
            this._rowHeightPaddingBottom = 0;
            Asserts.productionStateAssert(this._contentHeight >= 0);
            cellLayout.setEndLine(Math.max(cellLayout.getStartLine(), this._manager._textFlowLayout.getLineCellEnd(this._cellNesting) + 1));
         } else {
            this.cacheFromTextPosition(textPosition);
            int endY = this._cachePixelLine;
            if (this._parent == null) {
               this._manager._lastLayoutEndY = endY;
            }

            int adjustHeight = cellLayout.getCellHeight() - (endY - lastLayoutStartYPos);
            this._contentHeight += adjustHeight;
            int oldRowHeightPadding = this._rowHeightPaddingTop;
            this._rowHeightPaddingTop = 0;
            this._rowHeightPaddingBottom = 0;
            Asserts.productionStateAssert(this._contentHeight >= 0);
            if ((this._cellNesting & '\uffff') > 0) {
               int lineCountEnd = this._manager._textFlowLayout.getLineCellEnd(this._cellNesting) + 1;
               cellLayout.append(adjustHeight, this._manager._textFlowLayout.getLines(), this._cacheTextLine, lineCountEnd - this._cacheTextLine);
            }

            this.restoreLayoutState();
            this.moveRegions(textFlowData.getFirstRegionIndexFromTextPosition(textPosition), textEndRegion, textPosition, adjustHeight - oldRowHeightPadding);
            this.moveOutoflineFields(outoflineIndex, this.getOutOfLineObjectEnd(), adjustHeight - oldRowHeightPadding);
            cellLayout.setEndLine(this._cacheTextLine);
         }

         if (this._cellCollection == null) {
            this._flags &= -9;
         } else {
            boolean allOnSameYPos = true;
            int colCount = this._cellCollection.getColCount();

            for (int i = this._cellCollection.getRowCount() - 1; i >= 0 && allOnSameYPos; i--) {
               int rowYPos = -1;

               for (int j = colCount - 1; j >= 0 && allOnSameYPos; j--) {
                  Object obj = this._cellCollection.getCell(i, j);
                  if (obj != null) {
                     TextFlowCell cell = (TextFlowCell)obj;
                     if (cell._rowStart == i) {
                        if (rowYPos == -1) {
                           rowYPos = cell._y;
                        } else if (allOnSameYPos) {
                           allOnSameYPos = cell._y == rowYPos;
                        }
                     }
                  }
               }
            }

            this._flags &= -9;
            if (allOnSameYPos) {
               this._flags |= 8;
            }
         }

         this._flags &= -65;
         this._lastLayoutYPos = this._y
            + (this._cellPadding >> 16 & 0xFF)
            + (this._border != null ? this._border.getTop() : 0)
            + (this._rowStart == 0 ? this._cellSpacing : 0);
         if (this._animationProperties != null) {
            this._animationProperties._maxLineWidth = -1;
         }

         this.cacheClear();
         this._layoutEndPosition = textEndPositionData;
      }
   }

   private final void pushRemainingCells(int oldRow, int pos) {
      if (oldRow >= 0 && this._cellCollection.getRowCount() > oldRow) {
         int colCount = this._cellCollection.getColCount();

         while (pos < colCount) {
            TextFlowCell cell = (TextFlowCell)this._cellCollection.getCell(oldRow, pos);
            if (cell == null) {
               pos++;
            } else {
               if (cell._rowEnd <= this._currentRow) {
                  return;
               }

               int x = this._left;
               int cellHeight = cell.getHeight();
               int cellWidth = cell.getWidth();
               this._left = cell._x + cellWidth;
               this._leftEnd = cell._y + cellHeight;
               this._manager._leftObjects.push(this._cellNesting, x, cell._y, this._left - x, cellHeight, cell._rowEnd, cell._cellOOLIndex);
               pos = cell._colEnd;
            }
         }
      }
   }

   private final void updateLayoutStateFromCell(TextFlowCell child, int startPosition, int lineCountStart) {
      int childRow = child._rowStart;
      int childColumn = child._colStart;
      if (childColumn > 0) {
         int lastLeft = Integer.MAX_VALUE;

         int startColumn;
         for (startColumn = childColumn - 1; startColumn > 0; startColumn--) {
            TextFlowCell cell = (TextFlowCell)this._cellCollection.getCell(childRow, startColumn);
            if (cell != null) {
               if (cell._left + cell.getWidth() > lastLeft) {
                  break;
               }

               lastLeft = cell._left;
               startColumn = cell._colStart;
               if (startColumn == 0) {
                  break;
               }
            }
         }

         for (int column = startColumn; column < childColumn; column++) {
            TextFlowCell cell = (TextFlowCell)this._cellCollection.getCell(childRow, column);
            if (cell != null) {
               int x = this._left;
               int cellHeight = cell.getHeight();
               int cellWidth = cell.getWidth();
               this._left += cellWidth;
               this._leftEnd = cell._y + cellHeight;
               this._manager._leftObjects.push(this._cellNesting, x, cell._y, cellWidth, cellHeight, cell._rowEnd, cell._cellOOLIndex);
               column = cell._colEnd - 1;
            }
         }
      } else {
         this.cacheFromTextPosition(startPosition);
         int line = this._cacheTextLine - 1;
         int startY = this._cachePixelLine;
         short[] lineLengths = this._manager._textFlowLayout.getLineLengths();
         short[] lineWidthsNominal = this._manager._textFlowLayout.getLineWidthsNominal();
         short[] lineCellIds = this._manager._textFlowLayout.getLineCellIds();
         TextFlowData textFlowData = this._manager._textFlowData;

         while (line >= lineCountStart && lineCellIds[line] >= this._cellNesting) {
            if (lineCellIds[line] == this._cellNesting) {
               if (lineCellIds[line + 1] == this._cellNesting) {
                  return;
               }

               TextFlowCell cell = textFlowData.getTextFlowCellFromNestingId(lineCellIds[line + 1]);
               if (cell._y <= this._cachePixelLine && cell._y + cell.getHeight() >= this._cachePixelLine) {
                  int cellHeight = cell.getHeight();
                  int cellWidth = cell.getWidth();
                  if ((cell._flags & 6144) == 6144) {
                     this._right = cellWidth;
                     this._rightEnd = cell._y + cellHeight;
                     this._manager._rightObjects.push(this._cellNesting, cell._x, cell._y, cellWidth, cellHeight, cell._rowEnd, cell._cellOOLIndex);
                     return;
                  }

                  this._left = cell._x + cellWidth;
                  this._leftEnd = cell._y + cellHeight;
                  this._manager._leftObjects.push(this._cellNesting, cell._x, cell._y, cellWidth, cellHeight, cell._rowEnd, cell._cellOOLIndex);
                  return;
               }

               return;
            }

            line--;
         }
      }
   }

   private final void validateLayout() {
   }

   private final void validateLayoutPrior(TextFlowNative cellLayout) {
   }

   private final void validateLayoutAfter(TextFlowNative cellLayout) {
   }

   private final void deactivateActiveObjects(
      TextFlowCell$ExtentStack left, TextFlowCell$ExtentStack right, int y, int width, int maxY, TextFlowNative cellLayout, boolean pop
   ) {
      int leftWidth = this._left;
      int leftEnd = this._leftEnd;
      int rightWidth = this._right;
      int rightEnd = this._rightEnd;
      int leftCount = left.size(this._cellNesting);
      int rightCount = right.size(this._cellNesting);

      while (leftCount + rightCount > 0) {
         while (leftEnd <= y) {
            if (--leftCount == 0) {
               leftEnd = Integer.MAX_VALUE;
               leftWidth = 0;
            } else if (!pop || maxY == Integer.MAX_VALUE || left.getRowEnd(this._cellNesting, leftCount - 1) == this._currentRow) {
               leftEnd = left.getTop(this._cellNesting, leftCount - 1) + left.getHeight(this._cellNesting, leftCount - 1);
               leftWidth = left.getLeft(this._cellNesting, leftCount - 1) + left.getWidth(this._cellNesting, leftCount - 1);
            }
         }

         while (rightEnd <= y) {
            if (--rightCount == 0) {
               rightEnd = Integer.MAX_VALUE;
               rightWidth = 0;
            } else if (!pop || maxY == Integer.MAX_VALUE || right.getRowEnd(this._cellNesting, rightCount - 1) == this._currentRow) {
               rightEnd = right.getTop(this._cellNesting, rightCount - 1) + right.getHeight(this._cellNesting, rightCount - 1);
               rightWidth = width - right.getLeft(this._cellNesting, rightCount - 1);
            }
         }

         if (leftCount + rightCount == 0) {
            break;
         }

         int regionHeight = Math.min(Integer.MAX_VALUE, Math.min(leftEnd - y, rightEnd - y));
         if (regionHeight > 0) {
            cellLayout.appendVerticalSpace(
               y, this._lastLayoutStartLine, Math.min(Math.max(maxY - y, 0), regionHeight), (short)leftWidth, (short)(width - (leftWidth + rightWidth))
            );
            y += regionHeight;
         }
      }

      if (pop) {
         this._left = this._x
            + (this._border != null ? this._border.getLeft() : 0)
            + (this._cellPadding >> 0 & 0xFF)
            + (this._colStart == 0 ? this._cellSpacing : 0);
         this._right = this._left;
         this._leftEnd = Integer.MAX_VALUE;
         this._rightEnd = Integer.MAX_VALUE;
         left.clear(this._cellNesting);
         right.clear(this._cellNesting);
      }
   }

   private final int moveOutoflineFields(int startIndex, int endIndex, int adjustY) {
      if (adjustY == 0) {
         return this.getOutOfLineObjectEnd();
      }

      int index = startIndex;
      int textEndRegion = this.getTextEndRegion();

      int regionIndex;
      while (index < endIndex && (regionIndex = this._manager._textFlowData.getOutOfLineObjectRegion(index)) < textEndRegion) {
         if (this._manager._textFlowData.getRegionCellOOLIndex(regionIndex) == this._cellOOLIndex) {
            Object object = this._manager._textFlowData.getOutOfLineObject(index);
            if (!(object instanceof Object)) {
               if (object instanceof TextFlowCell) {
                  TextFlowCell cell = (TextFlowCell)object;
                  cell._y += adjustY;
                  cell._lastLayoutYPos = cell._y
                     + (cell._cellPadding >> 16 & 0xFF)
                     + (cell._border != null ? cell._border.getTop() : 0)
                     + (cell._rowStart == 0 ? cell._cellSpacing : 0);
                  index++;
                  cell.cacheClear();
                  index = cell.moveOutoflineFields(index, endIndex, adjustY);
                  cell.moveRegions(cell._textStartRegion, cell.getTextEndRegion(), 0, adjustY);
                  continue;
               }
            } else {
               Field field = (Field)object;
               int x = field.getLeft();
               int y = field.getTop() + adjustY;
               this._manager.placeField(field, x, y);
            }
         }

         index++;
      }

      return index;
   }

   private final void placeInlineFields(TextFlowNative cellLayout, int startIndex, int startRegion, int startTextPosition, int endTextPosition) {
      TextFlowData textFlowData = this._manager._textFlowData;
      int index = startIndex;
      int textEndRegion = this.getTextEndRegion();

      for (int region = startRegion; region < textEndRegion; region++) {
         int endPos = textFlowData.getRegionEndOffset(region);
         if (endPos > startTextPosition) {
            int startPos = textFlowData.getRegionStartOffset(region);
            if (startPos >= endTextPosition) {
               break;
            }

            if (endTextPosition >= endPos) {
               Object object = textFlowData.getRegionObject(region);
               if (object != null && textFlowData.isRegionInline(region)) {
                  Field field = (Field)object;
                  TextFlowRegion regionRef = textFlowData.getRegion(region);
                  if ((regionRef.getFlags() & 1024) != 0) {
                     this._manager.placeField(field, Integer.MAX_VALUE, Integer.MAX_VALUE);
                  } else {
                     this._manager.placeField(field, regionRef.getOffsetXLeft(), regionRef.getOffsetYTop());
                  }

                  index++;
               }
            }
         }
      }

      if (index != cellLayout.getInlineCount()) {
         Asserts.productionAssert(false);
      }
   }

   private final void moveRegions(int startRegion, int endRegion, int startTextPosition, int adjustHeight) {
      TextFlowData textFlowData = this._manager._textFlowData;

      for (int region = startRegion; region < endRegion; region++) {
         if (textFlowData.getRegionCellOOLIndex(region) == this._cellOOLIndex) {
            int endPos = textFlowData.getRegionEndOffset(region);
            if (endPos > startTextPosition) {
               TextFlowRegion regionObj = textFlowData.getRegion(region);
               int topAdjustHeight;
               if (textFlowData.getRegionStartOffset(region) < startTextPosition) {
                  topAdjustHeight = 0;
               } else {
                  topAdjustHeight = adjustHeight;
               }

               regionObj.setPosition(
                  regionObj.getOffsetXLeft(),
                  regionObj.getOffsetYTop() + topAdjustHeight,
                  regionObj.getOffsetXRight(),
                  regionObj.getOffsetYBottom() + adjustHeight
               );
               Object object = textFlowData.getRegionObject(region);
               if (object != null && textFlowData.isRegionInline(region)) {
                  Field field = (Field)object;
                  this._manager.placeField(field, field.getLeft(), field.getTop() + adjustHeight);
               }
            }
         }
      }
   }

   private final int calculateActiveObjectsToDeactivate(TextFlowCell$ExtentStack objects, int y, boolean pop) {
      int pixels = 0;

      for (int i = objects.size(this._cellNesting) - 1; i >= 0 && objects.getTop(this._cellNesting, i) + objects.getHeight(this._cellNesting, i) <= y; i--) {
         pixels += objects.getWidth(this._cellNesting, i);
         if (pop) {
            objects.pop(this._cellNesting);
         }
      }

      return pixels;
   }

   private final void fieldWidthAdjusted(TextFlowCell child, int oldWidth, int newOOLWidth) {
      int newMinRowWidth = this._minRowWidth;
      if (child != null && this._cellCollection != null) {
         newMinRowWidth = this._minRowWidth - child.getMinPreferredWidth() + newOOLWidth;
      }

      if (this._maxCalculatedWidth != -1
         && (oldWidth == -1 || this._lastOOLWidth == oldWidth || this._lastOOLWidth < newOOLWidth || newMinRowWidth > this._minRowWidth)) {
         if (this._parent != null) {
            this._parent.fieldWidthAdjusted(this, Math.max(this.getSpecifiedWidth(), this._maxCalculatedWidth), newOOLWidth);
         }

         this._reflow = true;
         this._maxCalculatedWidth = -1;
      }
   }

   private final void updateSpecifiedWidth() {
      if (this._specifiedWidthCalculated == -1) {
         if (this._specifiedWidth != Integer.MIN_VALUE) {
            if (this._specifiedWidth < 0 && this._parent != null) {
               int parentSpecifiedWidth = this._parent.getSpecifiedWidth();
               if (parentSpecifiedWidth != -1) {
                  parentSpecifiedWidth -= this._parent.getTotalWidthPadding();
                  parentSpecifiedWidth -= this._parent._border != null ? this._parent._border.getLeft() + this._parent._border.getRight() : 0;
                  this._specifiedWidthCalculated = parentSpecifiedWidth * this._specifiedWidth * -1 / 100;
                  return;
               }
            } else {
               this._specifiedWidthCalculated = this._specifiedWidth;
            }
         }
      }
   }

   private final void updateCalculatedWidth() {
      if (this._maxCalculatedWidth == -1) {
         int maxLineSum = 0;
         int lineSum = 0;
         int maxOolWidth = -1;
         int minTextWidth = 0;
         if (this._cellCollection != null) {
            int colCount = this._cellCollection.getColCount();
            int[] maxWidths = new int[colCount];
            int[] preferredWidths = new int[colCount];
            int[] minWidths = new int[colCount];
            this.determinePreferredMinMaxCellWidths(preferredWidths, minWidths, maxWidths);

            for (int i = 0; i < colCount; i++) {
               maxLineSum += maxWidths[i];
            }

            int rowCount = this._cellCollection.getRowCount();

            for (int row = 0; row < rowCount; row++) {
               int currentRowMinWidth = 0;
               int currentRowMaxWidth = 0;

               for (int col = 0; col < colCount; col++) {
                  TextFlowCell cell = (TextFlowCell)this._cellCollection.getCell(row, col);
                  if (cell != null) {
                     maxOolWidth = Math.max(maxOolWidth, cell.getMinPreferredWidth());
                     currentRowMaxWidth += cell.getPreferredWidth();
                     currentRowMinWidth += cell.getMinPreferredWidth();
                     col = cell._colEnd - 1;
                  }
               }

               this._minRowWidth = Math.max(currentRowMinWidth, this._minRowWidth);
               maxLineSum = Math.max(maxLineSum, currentRowMaxWidth);
            }
         } else {
            TextFlowNative textFlowNative = this._manager._textFlowLayout.getTextFlowNative();
            int regionCount = this._manager._textFlowData.getRegionCount();
            int region = this._textStartRegion;
            int nextPaintingRegion = this._textStartRegion;
            int textEndPos = this.getTextEnd();
            int textEndRegion = this.getTextEndRegion();
            TextFlowData textFlowData = this._manager._textFlowData;
            if (this._textStartPosition < textEndPos) {
               while (textFlowData.getRegionStartOffset(region + 1) <= this._textStartPosition) {
                  region++;
               }

               int maxRegionIndex = region;
               int textPosition = this._textStartPosition;

               while (textPosition < textEndPos && region < textEndRegion) {
                  int nextRegion = -1;
                  if (textFlowData.getRegionCellOOLIndex(region) != this._cellOOLIndex) {
                     textPosition = textFlowData.getRegionStartOffset(maxRegionIndex + 1);
                  } else {
                     int endPos = Math.min(textFlowData.getRegionStartOffset(maxRegionIndex + 1), textFlowData.getRegionEndOffset(region));
                     boolean floating = false;
                     if (textFlowData.isRegionText(region)) {
                        if (endPos > textPosition) {
                           TextFlowRegion regionObj = textFlowData.getRegion(region);
                           regionObj.calculateTextWidth(textFlowData.getText(), textPosition, endPos);
                           lineSum += regionObj.getPreferredTextWidth();
                           minTextWidth = Math.max(minTextWidth, regionObj.getMinTextWidth());
                        }
                     } else if (!textFlowData.isRegionOutOfLine(region)) {
                        if (textFlowData.isRegionInline(region)) {
                           int inlineWidth = 0;
                           Object obj = textFlowData.getRegionObject(region);
                           if (obj instanceof Object) {
                              inlineWidth = Math.max(this._manager.getPreferredWidthOfChildPublic((Field)obj), inlineWidth);
                           }

                           lineSum += inlineWidth;
                           minTextWidth = Math.max(minTextWidth, inlineWidth);
                        }
                     } else {
                        Object obj = textFlowData.getRegionObject(region);
                        int objectWidth = 0;
                        if (obj instanceof Object) {
                           objectWidth = this._manager.getPreferredWidthOfChildPublic((Field)obj);
                        } else if (obj instanceof TextFlowCell) {
                           TextFlowCell cell = (TextFlowCell)obj;
                           objectWidth = cell.getMinPreferredWidth();
                           lineSum += cell.getPreferredWidth();
                           if (cell._cellCollection != null) {
                              objectWidth = Math.max(objectWidth, cell._minRowWidth);
                           }

                           int halignMasked = cell._flags & 6144;
                           if (halignMasked != 0 && halignMasked != 4096) {
                              floating = true;
                           }

                           endPos = textFlowData.getRegionEndOffset(region);
                           nextRegion = cell.getTextEndRegion();
                        }

                        maxOolWidth = Math.max(objectWidth, maxOolWidth);
                        maxLineSum = Math.max(maxLineSum, lineSum);
                        if (!floating) {
                           lineSum = 0;
                        }
                     }

                     TextFlowRegion regionObj = textFlowData.getRegion(region);
                     if (!floating && (regionObj.getFlags() & 960) != 0) {
                        maxLineSum = Math.max(maxLineSum, lineSum);
                        lineSum = 0;
                     }

                     textPosition = endPos;
                  }

                  if (nextRegion != -1) {
                     maxRegionIndex = nextRegion;
                     region = nextRegion;
                  } else {
                     int parentRegionId = textFlowData.getRegionParentId(region);
                     if (parentRegionId != -1) {
                        if (textFlowData.getRegionParentId(maxRegionIndex + 1) == parentRegionId
                           && textFlowData.getRegionStartOffset(maxRegionIndex + 1) == textPosition) {
                           region = maxRegionIndex + 1;
                        } else {
                           region = parentRegionId;
                        }
                     } else {
                        region = maxRegionIndex + 1;
                     }

                     maxRegionIndex = Math.max(region, maxRegionIndex);
                  }
               }
            }
         }

         maxLineSum = Math.max(maxLineSum, lineSum);
         if (maxOolWidth != -1) {
            this._lastOOLWidth = maxOolWidth;
         } else {
            maxOolWidth = 0;
         }

         this._maxCalculatedWidth = Math.max(maxOolWidth, maxLineSum);
         if (this._specifiedWidth > 0) {
            this._maxCalculatedWidth = Math.max(this._specifiedWidth, this._maxCalculatedWidth);
         }

         if ((this._flags & 384) != 0) {
            this._minCalculatedWidth = this._maxCalculatedWidth;
         } else {
            this._minCalculatedWidth = Math.max(maxOolWidth, minTextWidth);
         }
      }
   }

   private final void nextSelectionPositionClosestInternal(SelectionPosition position) {
      TextFlowCell$Painter painter = this._manager._painter;
      painter.StartMeasuring(this, position.textPosition);
      int closest = -1;
      int distance = Integer.MAX_VALUE;
      int end = painter.getTextPosition() + this._manager._textFlowLayout.getLineLengths()[painter.getLineNumber()];
      int pos = this.getNextTextCharacter(painter.getTextPosition(), 1, true);
      if (pos == -1) {
         closest = painter.getTextPosition();
      } else {
         do {
            painter.MeasureLine(pos, Integer.MAX_VALUE, false);
            int test = Math.abs(this._preferredSelectX - painter.getXPosition());
            if (test > distance) {
               break;
            }

            distance = test;
            closest = painter.getTextPosition();
            pos = this.getNextTextCharacter(pos, 1, false);
         } while (pos != -1 && pos < end);
      }

      painter.End();
      position.textPosition = closest;
   }

   private final boolean nextSelectionPositionInternal(SelectionPosition position, int amount, boolean horizontal, boolean allowImageSelection) {
      int startTextPosition = position.textPosition;
      int startRegion = position.selectedRegion;
      int direction = amount < 0 ? -1 : 1;
      if (horizontal) {
         this._preferredSelectX = -1;
      } else if (this._preferredSelectX == -1) {
         this._preferredSelectX = this._manager._painter.GetXOffsetOfTextPosition(this, position.textPosition, false);
      }

      while (amount != 0) {
         if (!horizontal && position.selectedRegion == -1) {
            int currentPosition = position.textPosition;
            this.cacheFromTextPosition(currentPosition);
            int lineStartPosition = this._cacheTextOffset;
            int searchStartPosition = lineStartPosition;
            int lineIndex = this._cacheTextLine;
            int lineCount = this._manager._textFlowLayout.getLineCount();
            if (lineIndex >= lineCount) {
               return false;
            }

            if (direction > 0 && lineIndex + 1 < lineCount) {
               int nextCellId = this._manager._textFlowLayout.getLineCellIds()[lineIndex + 1] & '\uffff';
               if (nextCellId != this._cellNesting) {
                  int nextPos = lineStartPosition + this._manager._textFlowLayout.getLineLengths()[lineIndex];
                  int regionCount = this._manager._textFlowData.getRegionCount();
                  int region = this._manager._textFlowData.getLastRegionIndexFromTextPosition(nextPos);
                  if (region >= regionCount) {
                     return false;
                  }

                  int nextCellOOL = this._manager._textFlowData.getRegionCellOOLIndex(region);
                  position.textPosition = nextPos;
                  position.selectedRegion = -1;
                  if (nextCellOOL != this._cellOOLIndex) {
                     TextFlowCell nextCell;
                     if (nextCellOOL == -1) {
                        nextCell = this._manager._rootCell;
                     } else {
                        nextCell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(nextCellOOL);
                     }

                     return nextCell.nextSelectionPositionInternal(position, amount, horizontal, allowImageSelection);
                  }
               }
            }

            if (direction < 0 && lineIndex > 0) {
               int nextCellId = this._manager._textFlowLayout.getLineCellIds()[lineIndex - 1] & '\uffff';
               if (nextCellId != this._cellNesting) {
                  int region = this._manager._textFlowData.getLastRegionIndexFromTextPosition(lineStartPosition - 1);
                  if (region < 0) {
                     return false;
                  }

                  int nextCellOOL = this._manager._textFlowData.getRegionCellOOLIndex(region);
                  position.textPosition = lineStartPosition - 1;
                  position.selectedRegion = -1;
                  if (nextCellOOL != this._cellOOLIndex) {
                     TextFlowCell nextCell;
                     if (nextCellOOL == -1) {
                        nextCell = this._manager._rootCell;
                     } else {
                        nextCell = (TextFlowCell)this._manager._textFlowData.getOutOfLineObject(nextCellOOL);
                     }

                     return nextCell.nextSelectionPositionInternal(position, amount, horizontal, allowImageSelection);
                  }
               }
            }

            position.textPosition = direction < 0
               ? lineStartPosition
               : (searchStartPosition = lineStartPosition + this._manager._textFlowLayout.getLineLengths()[lineIndex] - 1);
            position.selectedRegion = -1;
            if (!this.nextCharacterOrImage(position, direction, false, allowImageSelection)) {
               if (position.textPosition == searchStartPosition && position.selectedRegion == startRegion) {
                  position.textPosition = currentPosition;
                  return false;
               }

               position.textPosition = currentPosition;
               return true;
            }

            amount -= direction;
         } else {
            if (!this.nextCharacterOrImage(position, direction, false, allowImageSelection)) {
               if (position.textPosition == startTextPosition && position.selectedRegion == startRegion) {
                  return false;
               }

               return true;
            }

            amount -= direction;
         }
      }

      return true;
   }

   private final boolean nextCharacterOrImage(SelectionPosition position, int direction, boolean checkCurrent, boolean allowImageSelection) {
      TextFlowData textFlowData = this._manager._textFlowData;
      int textPosition = position.textPosition;
      if (!checkCurrent && textPosition != -1 && (direction > 0 || textPosition != 0)) {
         textPosition += direction;
      }

      boolean hadRegion = true;
      int region = position.selectedRegion;
      if (region == -1) {
         region = textFlowData.getLastRegionIndexFromTextPosition(textPosition);
         hadRegion = false;
      }

      if (region >= textFlowData.getRegionCount()) {
         return false;
      }

      if (textPosition != -1) {
         StringBuffer text = textFlowData.getText();
         if (direction == 0) {
            direction = 1;
         }

         for (int textLength = text.length(); textPosition >= 0 && textPosition < textLength; textPosition += direction) {
            region = textFlowData.getLastRegionIndexFromTextPosition(textPosition);
            if (textFlowData.isRegionText(region)) {
               position.selectedRegion = -1;
               position.textPosition = textPosition;
               return true;
            }

            if (allowImageSelection && textFlowData.getRegionObject(region) instanceof BrowserBitmapField) {
               position.selectedRegion = region;
               position.textPosition = -1;
               return true;
            }
         }
      }

      return false;
   }

   public TextFlowCell(
      TextFlowManager manager,
      int totalWidth,
      TextFlowCell parent,
      short nestingId,
      int oolIndex,
      int animationStyle,
      int scrollDelay,
      int scrollAmount,
      int loopCount,
      Border border
   ) {
      this(manager, parent._contentWidth, border, totalWidth, 0, 0, parent, false, 16, nestingId, oolIndex, -1);
      this._animationProperties = new TextFlowCell$AnimationProperties();
      this._animationProperties._animationStyle = animationStyle;
      this._animationProperties._scrollDelay = scrollDelay;
      this._animationProperties._scrollAmount = scrollAmount;
      this._animationProperties._loopCount = loopCount;
      this._animationProperties._animationStyle = animationStyle | 1;
   }

   private final int getOutOfLineObjectEnd() {
      return this._outoflineObjectEndData == Integer.MAX_VALUE ? this._manager._textFlowData.getOutOfLineObjectCount() : this._outoflineObjectEndData;
   }

   private final int getTextEnd() {
      int region = this._textStartRegion;
      if (this._cellOOLIndex != -1) {
         region = this._manager._textFlowData.getOutOfLineObjectRegion(this._cellOOLIndex);
      }

      return this._manager._textFlowData.getRegionEndOffset(region);
   }

   private final int getTextEndRegion() {
      return this._textStartRegion + this._manager._textFlowData.getNumSubRegions(this._textStartRegion) + 1;
   }

   private final int getYOffsetFromTextPositionInternal(int textPosition, boolean top) {
      this.cacheFromTextPosition(textPosition);
      return top
         ? this._cachePixelLine + this._rowHeightPaddingTop
         : this._cachePixelLine + this._rowHeightPaddingTop + this._manager._textFlowLayout.getLineHeights()[this._cacheTextLine] - 1;
   }

   private final void setPosition(int x, int y) {
      if (y != this._y || x != this._x) {
         this._manager._textFlowData.setOutOfLineDirty(this._cellOOLIndex, true);
      }

      this._x = x;
      this._y = y;
   }

   private final void cacheFromTextPosition(int textPosition) {
      short[] lineCellIds = this._manager._textFlowLayout.getLineCellIds();
      short[] lineLengths = this._manager._textFlowLayout.getLineLengths();
      short[] lineHeights = this._manager._textFlowLayout.getLineHeights();
      int distToCache = Math.abs(this._cacheTextOffset - textPosition);
      int distToEnd = this._layoutEndPosition - textPosition;
      int disToTop = textPosition - this._textStartPosition;
      int lineCountStart = this._manager._textFlowLayout.getLineCellStart(this._cellNesting);
      int lineCountEnd = this._manager._textFlowLayout.getLineCellEnd(this._cellNesting) + 1;
      int cellNestingMasked = this._cellNesting & '\uffff';
      int searchPixelLine;
      int searchTextLine;
      int searchOffset;
      if (distToEnd == 0) {
         searchTextLine = lineCountEnd;
         searchOffset = textPosition;
         searchPixelLine = this._lastLayoutYPos + this._contentHeight;

         while (searchTextLine > 0 && lineLengths[searchTextLine - 1] == 0 && lineCellIds[searchTextLine - 1] == this._cellNesting) {
            if (lineCellIds[searchTextLine - 1] == this._cellNesting) {
               searchPixelLine -= lineHeights[searchTextLine - 1];
            }

            searchTextLine--;
         }
      } else {
         if (distToCache < disToTop && distToCache < distToEnd) {
            searchPixelLine = this._cachePixelLine;
            searchTextLine = this._cacheTextLine;
            searchOffset = this._cacheTextOffset;
         } else if (disToTop <= distToEnd) {
            searchPixelLine = this._lastLayoutYPos;
            searchTextLine = lineCountStart;
            searchOffset = this._textStartPosition;
         } else {
            searchTextLine = lineCountEnd - 1;
            searchPixelLine = this._lastLayoutYPos + this._contentHeight;
            searchOffset = this._layoutEndPosition;
            int size = this._manager._textFlowLayout.getLineCount();

            for (int i = searchTextLine; i < size && (lineCellIds[i] & '\uffff') >= cellNestingMasked; i++) {
               searchPixelLine -= lineHeights[i];
               searchOffset -= lineLengths[i];
            }
         }

         while (searchTextLine > 0 && textPosition < searchOffset) {
            if (lineCellIds[searchTextLine - 1] == this._cellNesting) {
               searchTextLine--;
               searchOffset -= lineLengths[searchTextLine];
               searchPixelLine -= lineHeights[searchTextLine];

               while (searchTextLine > 0 && (lineCellIds[searchTextLine - 1] & '\uffff') > cellNestingMasked) {
                  searchTextLine--;
                  searchOffset -= lineLengths[searchTextLine];
               }
            } else {
               if ((lineCellIds[searchTextLine - 1] & '\uffff') <= cellNestingMasked) {
                  break;
               }

               TextFlowCell cell = this._manager._textFlowData.getTextFlowCellFromNestingId(lineCellIds[searchTextLine - 1]);
               searchTextLine = this._manager._textFlowLayout.getLineCellStart(cell._cellNesting);
               searchPixelLine = cell._lastLayoutYPos
                  - (cell._cellPadding >> 16 & 0xFF)
                  - (cell._border != null ? cell._border.getTop() : 0)
                  - (cell._rowStart == 0 ? cell._cellSpacing : 0);
               searchOffset = cell._textStartPosition;
            }
         }

         while (searchTextLine < lineCountEnd && textPosition >= searchOffset + lineLengths[searchTextLine]) {
            if (lineCellIds[searchTextLine] == this._cellNesting) {
               searchPixelLine += lineHeights[searchTextLine];
               searchOffset += lineLengths[searchTextLine];
               searchTextLine++;
            } else {
               if ((lineCellIds[searchTextLine] & '\uffff') <= cellNestingMasked) {
                  break;
               }

               TextFlowCell cell = this._manager._textFlowData.getTextFlowCellFromNestingId(lineCellIds[searchTextLine]);
               int endPos = cell.getTextEnd();
               if (textPosition < endPos) {
                  break;
               }

               searchTextLine = this._manager._textFlowLayout.getLineCellEnd(cell._cellNesting) + 1;
               searchOffset = endPos;
            }
         }
      }

      this._cachePixelLine = searchPixelLine;
      this._cacheTextLine = searchTextLine;
      this._cacheTextOffset = searchOffset;
   }

   private final void cacheFromYOffset(int y) {
      short[] lineCellIds = this._manager._textFlowLayout.getLineCellIds();
      short[] lineLengths = this._manager._textFlowLayout.getLineLengths();
      short[] lineHeights = this._manager._textFlowLayout.getLineHeights();
      int lineCountStart = this._manager._textFlowLayout.getLineCellStart(this._cellNesting);
      int lineCountEnd = this._manager._textFlowLayout.getLineCellEnd(this._cellNesting) + 1;
      int topYPos = this._lastLayoutYPos;
      int bottomYPos = topYPos + this._contentHeight;
      if (this._contentHeight > 0 && y > bottomYPos) {
         y = bottomYPos;
      }

      if (y < topYPos) {
         y = topYPos;
      }

      int distToCache = Math.abs(this._cachePixelLine - y);
      int distToEnd = bottomYPos - y;
      int disToTop = y - topYPos;
      int cellNestingMasked = this._cellNesting & '\uffff';
      int searchPixelLine;
      int searchTextLine;
      int searchOffset;
      if (distToCache < disToTop && distToCache < distToEnd) {
         searchPixelLine = this._cachePixelLine;
         searchTextLine = this._cacheTextLine;
         searchOffset = this._cacheTextOffset;
      } else if (disToTop <= distToEnd) {
         searchPixelLine = topYPos;
         searchTextLine = lineCountStart;
         searchOffset = this._textStartPosition;
      } else {
         searchTextLine = lineCountEnd - 1;
         searchPixelLine = bottomYPos;
         searchOffset = this._layoutEndPosition;
         int size = this._manager._textFlowLayout.getLineCount();

         for (int i = searchTextLine; i < size && (lineCellIds[i] & '\uffff') >= cellNestingMasked; i++) {
            searchPixelLine -= lineHeights[i];
            searchOffset -= lineLengths[i];
         }
      }

      while (searchTextLine > 0 && y < searchPixelLine) {
         if (lineCellIds[searchTextLine - 1] == this._cellNesting) {
            searchTextLine--;
            searchOffset -= lineLengths[searchTextLine];
            searchPixelLine -= lineHeights[searchTextLine];

            while (searchTextLine > 0 && (lineCellIds[searchTextLine - 1] & '\uffff') > cellNestingMasked) {
               searchTextLine--;
               searchOffset -= lineLengths[searchTextLine];
            }
         } else {
            if ((lineCellIds[searchTextLine - 1] & '\uffff') <= cellNestingMasked) {
               break;
            }

            TextFlowCell cell = this._manager._textFlowData.getTextFlowCellFromNestingId(lineCellIds[searchTextLine - 1]);
            searchTextLine = this._manager._textFlowLayout.getLineCellStart(cell._cellNesting);
            searchPixelLine = cell._lastLayoutYPos;
            searchOffset = cell._textStartPosition;
         }
      }

      while (searchTextLine < lineCountEnd && y > searchPixelLine + lineHeights[searchTextLine]) {
         if (lineCellIds[searchTextLine] == this._cellNesting) {
            searchPixelLine += lineHeights[searchTextLine];
            searchOffset += lineLengths[searchTextLine];
            searchTextLine++;
         } else {
            if ((lineCellIds[searchTextLine] & '\uffff') <= cellNestingMasked) {
               break;
            }

            TextFlowCell cell = this._manager._textFlowData.getTextFlowCellFromNestingId(lineCellIds[searchTextLine]);
            int endPos = cell._lastLayoutYPos + cell._contentHeight + (cell._border != null ? cell._border.getBottom() : 0);
            if (y < endPos) {
               break;
            }

            searchTextLine = this._manager._textFlowLayout.getLineCellEnd(cell._cellNesting) + 1;
            searchOffset = cell.getTextEnd();
         }
      }

      this._cachePixelLine = searchPixelLine;
      this._cacheTextLine = searchTextLine;
      this._cacheTextOffset = searchOffset;
   }

   private final int findTextFlowCell(int x, int y) {
      int count = this._manager._textFlowData.getNumCells();
      TextFlowCell[] cells = this._manager._textFlowData.getTextFlowCells();
      int lastCellId = this._cellOOLIndex;

      for (int i = 0; i < count; i++) {
         TextFlowCell cell = cells[i];
         if (cell != null
            && cell._contentWidth > 0
            && cell._contentHeight > 0
            && x >= cell._x
            && y >= cell._y
            && x < cell._contentWidth + cell._x
            && y < cell._contentHeight + cell._y) {
            if (!cell._containsSubCells) {
               return cell._cellOOLIndex;
            }

            lastCellId = cell._cellOOLIndex;
         }
      }

      return lastCellId;
   }

   private final int findTextFlowCell(int y) {
      this.cacheFromYOffset(y);
      if (this._cacheTextOffset == this._layoutEndPosition) {
         this.cacheFromTextPosition(this._layoutEndPosition - 1);
      }

      int oolIndex = this._manager._textFlowData.getCellIndexFromTextPosition(this._cacheTextOffset);
      int count = this._manager._textFlowData.getOutOfLineObjectCount();
      if (oolIndex >= 0 && oolIndex <= count) {
         while (!this._manager._textFlowData.isOutOfLineCell(oolIndex)) {
            if (++oolIndex > count) {
               return this._cellOOLIndex;
            }
         }

         return oolIndex;
      } else {
         return this._cellOOLIndex;
      }
   }

   private final int findTextFlowCellFromTextPos(int textPos) {
      int oolIndex = this._manager._textFlowData.getCellIndexFromTextPosition(textPos);
      int count = this._manager._textFlowData.getOutOfLineObjectCount();
      if (oolIndex >= 0 && oolIndex <= count) {
         int startPos = this._manager._textFlowData.getOutOfLineObjectOffset(oolIndex);
         if (startPos <= textPos && startPos + this._manager._textFlowData.getOutOfLineObjectLength(oolIndex) < textPos) {
         }

         return !this._manager._textFlowData.isOutOfLineCell(oolIndex) ? this._cellOOLIndex : oolIndex;
      } else {
         return this._cellOOLIndex;
      }
   }

   private final void invalidateWalk() {
      for (TextFlowCell cell = this; cell != null && cell._maxCalculatedWidth != -1; cell = cell._parent) {
         cell._maxCalculatedWidth = -1;
         cell._specifiedWidthCalculated = -1;
      }
   }

   private final void cacheClear() {
      this._cachePixelLine = 0;
      this._cacheTextLine = 0;
      this._cacheTextOffset = 0;
   }

   private final void validateCachedValues() {
   }

   private final boolean adjustContentWidth(int freeRegionWidth, int totalRegionWidth) {
      if (this._parent == null) {
         return false;
      }

      this.updateCalculatedWidth();
      int padding = (this._border != null ? this._border.getLeft() + this._border.getRight() : 0)
         + this.getTotalWidthPadding()
         + (this._colStart == 0 ? 2 : 1) * this._cellSpacing;
      int calcWidth = this._contentWidth;
      if (this._specifiedWidth == Integer.MIN_VALUE) {
         calcWidth = Math.max(this._minCalculatedWidth, Math.min(this._maxCalculatedWidth, freeRegionWidth - padding));
         if (this._parent._cellCollection != null && calcWidth < this._contentWidth && this._contentWidth <= freeRegionWidth) {
            calcWidth = this._contentWidth;
         }
      } else if (this._specifiedWidth < 0) {
         int specifiedWidth = (this._specifiedWidth == -100 ? freeRegionWidth : totalRegionWidth * this._specifiedWidth * -1 / 100) - padding;
         if (this._minCalculatedWidth < specifiedWidth) {
            calcWidth = specifiedWidth;
         }
      }

      int maxWidth = Math.max(Math.max(this._minRowWidth, this._minCalculatedWidth), calcWidth);
      if (maxWidth + padding > this._parent._contentWidth) {
         maxWidth = this._parent._contentWidth - padding;
      }

      if (maxWidth <= 0) {
         maxWidth = 1;
      }

      boolean result = maxWidth != this._contentWidth;
      this._contentWidth = maxWidth;
      this._flags &= -65;
      if (result) {
         this._flags |= 64;
      }

      return result;
   }

   private final void fixupLastRow(TextFlowNative cellLayout, int y, int consumeChars) {
      int maxEndPos = 0;
      boolean allSameYPos = true;
      if (this._currentRow >= 0 && this._currentRow < this._cellCollection.getRowCount()) {
         int maxYPos = 0;
         int minYPos = Integer.MAX_VALUE;
         int rowYPos = -1;
         int maxEndRowPos = 0;
         int colCount = this._cellCollection.getColCount();
         int maxBaseline = 0;
         int i = 0;

         while (i < colCount) {
            Object obj = this._cellCollection.getCell(this._currentRow, i);
            if (obj == null) {
               i++;
            } else {
               TextFlowCell cell = (TextFlowCell)obj;
               maxYPos = Math.max(cell._y, maxYPos);
               minYPos = Math.min(cell._y, minYPos);
               if (cell._rowEnd == this._currentRow + 1) {
                  int cellHeight = Math.max(cell.getHeight(), cell._minHeight);
                  maxEndPos = Math.max(cellHeight + cell._y, maxEndPos);
               } else if (cell._rowStart >= 0 && cell._rowEnd != cell._rowStart + 1) {
                  int cellHeight = Math.max(cell.getHeight(), cell._minHeight) * (this._currentRow - cell._rowStart + 1) / (cell._rowEnd - cell._rowStart);
                  maxEndRowPos = Math.max(cellHeight + cell._y, maxEndRowPos);
               }

               if (cell._rowStart == this._currentRow) {
                  if (rowYPos == -1) {
                     rowYPos = cell._y;
                  } else if (allSameYPos) {
                     allSameYPos = cell._y == rowYPos;
                  }
               }

               i = cell._colEnd;
            }
         }

         i = 0;

         while (allSameYPos && i < colCount) {
            Object obj = this._cellCollection.getCell(this._currentRow, i);
            if (obj != null) {
               TextFlowCell cell = (TextFlowCell)obj;
               int cellHeight = cell.getHeight();
               if (cell._y + cellHeight <= maxYPos) {
                  allSameYPos = false;
                  break;
               }

               int oldLayoutStartLine = this._manager._textFlowLayout.getLineCellStart(cell._cellNesting);
               int cellLayoutStartLine = cellLayout.getStartLine();
               if (cellLayoutStartLine != -1 && oldLayoutStartLine >= cellLayoutStartLine) {
                  if (cell._lastLayoutStartLine - cellLayoutStartLine < cellLayout.getLineCount()) {
                     maxBaseline = Math.max(maxBaseline, cellLayout.getLineBaselines()[cell._lastLayoutStartLine - cellLayoutStartLine]);
                  }
               } else {
                  maxBaseline = Math.max(maxBaseline, this._manager._textFlowLayout.getLineBaselines()[oldLayoutStartLine]);
               }

               i = cell._colEnd;
            } else {
               i++;
            }
         }

         if (maxEndPos == 0) {
            maxEndPos = maxYPos + 1;
         } else {
            maxEndPos = Math.max(maxEndRowPos, maxEndPos);
         }

         if (allSameYPos && maxEndPos != minYPos) {
            if (maxEndPos - maxYPos <= 0) {
               maxEndPos = maxYPos + 1;
            }

            i = 0;

            while (i < colCount) {
               Object obj = this._cellCollection.getCell(this._currentRow, i);
               if (obj == null) {
                  i++;
               } else {
                  TextFlowCell cell = (TextFlowCell)obj;
                  int cellHeight = cell.getHeight();
                  int extraPad = 0;
                  if (cell._rowEnd != this._currentRow + 1 && cell._rowStart >= 0 && cell._rowEnd != cell._rowStart + 1) {
                     extraPad = 1;
                  }

                  if (cell._y + cellHeight >= maxEndPos) {
                     cell._rowHeightPaddingBottom += extraPad;
                  } else {
                     int topAdjust = 0;
                     int bottomAdjust = 0;
                     switch (cell._flags & 1536) {
                        case 512:
                           int myBaseline = 0;
                           int oldLayoutStartLine = this._manager._textFlowLayout.getLineCellStart(cell._cellNesting);
                           short var34;
                           if (oldLayoutStartLine < cellLayout.getStartLine()) {
                              var34 = this._manager._textFlowLayout.getLineBaselines()[oldLayoutStartLine];
                           } else {
                              var34 = cellLayout.getLineBaselines()[cell._lastLayoutStartLine - cellLayout.getStartLine()];
                           }

                           topAdjust = maxBaseline - var34;
                           bottomAdjust = Math.max(maxEndPos - cell._y - cellHeight - topAdjust, 0) + extraPad;
                           break;
                        case 1024:
                           topAdjust = Math.max(maxEndPos - cell._y - cellHeight, 0);
                           bottomAdjust = extraPad;
                           break;
                        case 1536:
                           int diff = Math.max(maxEndPos - cell._y - cellHeight, 0) >> 1;
                           topAdjust = diff;
                           bottomAdjust = diff + extraPad;
                           break;
                        default:
                           bottomAdjust = Math.max(maxEndPos - cell._y - cellHeight, 0) + extraPad;
                     }

                     if (topAdjust > 0) {
                        cell.moveRegions(cell._textStartRegion, cell.getTextEndRegion(), 0, topAdjust);
                        cell.moveOutoflineFields(cell._outoflineObjectStart, cell.getOutOfLineObjectEnd(), topAdjust);
                        cell._rowHeightPaddingTop += topAdjust;
                     }

                     cell._rowHeightPaddingBottom += bottomAdjust;
                  }

                  i = cell._colEnd;
               }
            }
         }
      }

      this._currentRow++;
      this.deactivateActiveObjects(
         this._manager._leftObjects, this._manager._rightObjects, y, this._contentWidth, allSameYPos ? maxEndPos : Integer.MAX_VALUE, cellLayout, true
      );
      if (consumeChars >= 0) {
         cellLayout.appendZeroHeightCharacters(y, this._lastLayoutStartLine, consumeChars, (short)0, (short)this._contentWidth);
      }

      this.pushRemainingCells(this._currentRow - 1, 0);
   }

   private final void drawTextHighlight(int regionTextStart, int regionTextEnd, Graphics graphics, boolean on, int style, boolean half, int scale) {
      short[] lineWidths = this._manager._textFlowLayout.getLineWidths();
      short[] lineLengths = this._manager._textFlowLayout.getLineLengths();
      short[] lineHeights = this._manager._textFlowLayout.getLineHeights();
      short[] lineXOffsets = this._manager._textFlowLayout.getLineXOffsets();
      this.cacheFromTextPosition(regionTextStart);
      int y = RendererControl.fixed32DivToInt(this._cachePixelLine + this._rowHeightPaddingTop, scale);
      int line = this._cacheTextLine;
      int textPosition = this._cacheTextOffset;

      for (int startX = this._manager._painter.GetXOffsetOfLine(this, regionTextStart, regionTextEnd, false, scale) - lineXOffsets[line];
         textPosition < regionTextEnd;
         startX = 0
      ) {
         if (line >= this._manager._textFlowLayout.getLineCount()) {
            return;
         }

         int lineLength = lineLengths[line];
         int lineWidth = RendererControl.fixed32DivToInt(lineWidths[line], scale);
         int lineHeight = RendererControl.fixed32DivToInt(lineHeights[line], scale);
         int offsetY = 0;
         if (half) {
            offsetY = lineHeight >> 1;
         }

         if (textPosition + lineLength >= regionTextEnd) {
            lineWidth = this._manager._painter.GetXOffsetOfLine(this, Math.max(regionTextStart, textPosition), regionTextEnd, true, scale) - lineXOffsets[line];
         }

         this._manager
            .callbackDrawHighlightRegion(
               graphics, style, on, startX + RendererControl.fixed32DivToInt(lineXOffsets[line], scale), y + offsetY, lineWidth - startX, lineHeight - offsetY
            );
         y += lineHeight;
         textPosition += lineLength;
         line++;
      }
   }

   private final void updateAnimationOffset() {
      if (this._animationProperties != null && this._animationProperties._animationStyle != 0 && this._animationProperties._maxLineWidth == -1) {
         int lineCountStart = this._manager._textFlowLayout.getLineCellStart(this._cellNesting);
         int lineCountEnd = this._manager._textFlowLayout.getLineCellEnd(this._cellNesting) + 1;
         short[] lineWidths = this._manager._textFlowLayout.getLineWidths();

         for (int i = lineCountStart; i < lineCountEnd; i++) {
            this._animationProperties._maxLineWidth = Math.max(this._animationProperties._maxLineWidth, lineWidths[i]);
         }

         if ((this._animationProperties._animationStyle & 1) != 0) {
            this._animationProperties._animationStyle ^= 1;
            if ((this._animationProperties._animationStyle & 8) != 0) {
               if ((this._animationProperties._animationStyle & 64) != 0) {
                  this._animationProperties._animationOffset = Math.min(this._contentWidth - this._animationProperties._maxLineWidth, 0);
                  return;
               }

               this._animationProperties._animationOffset = Math.max(this._contentWidth - this._animationProperties._maxLineWidth, 0);
               return;
            }

            if ((this._animationProperties._animationStyle & 6) != 0) {
               if ((this._animationProperties._animationStyle & 64) != 0) {
                  this._animationProperties._animationOffset = -this._animationProperties._maxLineWidth;
                  return;
               }

               this._animationProperties._animationOffset = this._contentWidth;
            }
         }
      }
   }

   private final int getTotalHeightPadding() {
      return (this._cellPadding >> 16 & 0xFF) + (this._cellPadding >> 24 & 0xFF);
   }

   private final void forceNewlineInternal() {
      this._manager._textFlowData.appendNewline();
   }

   public TextFlowCell(
      TextFlowManager manager,
      int totalWidth,
      Border border,
      int preferredWidth,
      int cellSpacing,
      int cellPadding,
      TextFlowCell parent,
      boolean isTable,
      int flags,
      short nestingId,
      int oolIndex,
      int minHeight
   ) {
      this._parent = parent;
      this._cellNesting = nestingId;
      this._cellOOLIndex = oolIndex;
      this._flags |= flags;
      if (this._parent != null && totalWidth > this._parent._contentWidth) {
         totalWidth = this._parent._contentWidth;
      }

      this._border = border;
      this._cellSpacing = cellSpacing;
      this._cellPadding = cellPadding;
      int borderWidth = this._border != null ? this._border.getLeft() + this._border.getRight() : 0;
      this._contentWidth = Math.max(
         1,
         totalWidth
            - borderWidth
            - this.getTotalWidthPadding()
            - (this._parent != null && this._parent._cellCollection != null && this._parent._cellCollection.getCurrentCol() == 0 ? 2 : 1) * this._cellSpacing
      );
      this._minHeight = minHeight;
      this._specifiedWidth = preferredWidth;
      this._manager = manager;
      if (isTable) {
         this._cellCollection = new net.rim.device.apps.internal.browser.util.Table();
      }

      this._manager._leftObjects.ensureCapacity(this._manager._textFlowData.getNumCells() + 1);
      this._manager._rightObjects.ensureCapacity(this._manager._textFlowData.getNumCells() + 1);
      this.resetLayoutState();
   }

   private final void divUpWidths(int[] widths, int start, int end, int cellWidth) {
      for (int i = start; i < end; i++) {
         cellWidth -= widths[i];
      }

      if (cellWidth > 0) {
         int cellWidth0 = cellWidth / (end - start);
         int remainder = cellWidth - (end - start);

         for (int i = start; i < end; i++) {
            widths[i] += cellWidth0 + (remainder > 0 ? 1 : 0);
            remainder--;
         }
      }
   }

   private final void growCellsProportionally(int maxDiff, int diff, int[] minWidths, int[] maxWidths, int[] preferredWidths, int[] calculatedWidths) {
      int colCount = this._cellCollection.getColCount();

      for (int i = 0; i < colCount; i++) {
         int colDiff = maxWidths[i] - minWidths[i];
         calculatedWidths[i] = (preferredWidths[i] == Integer.MIN_VALUE ? minWidths[i] : preferredWidths[i]) + colDiff * diff / maxDiff;
      }
   }

   private final void growCellsEqually(int diff, int[] minWidths, int[] preferredWidths, int[] calculatedWidths) {
      int colCount = this._cellCollection.getColCount();

      for (int i = 0; i < colCount; i++) {
         calculatedWidths[i] = (preferredWidths[i] == Integer.MIN_VALUE ? minWidths[i] : preferredWidths[i]) + diff;
      }
   }

   private final void determinePreferredMinMaxCellWidths(int[] preferredWidths, int[] minWidths, int[] maxWidths) {
      Arrays.fill(preferredWidths, -1);
      int rowCount = this._cellCollection.getRowCount();
      int colCount = this._cellCollection.getColCount();

      for (int i = 0; i < rowCount; i++) {
         for (int j = 0; j < colCount; j++) {
            TextFlowCell cell = (TextFlowCell)this._cellCollection.getCell(i, j);
            if (cell != null) {
               int end = cell._colEnd;
               int minWidth = cell.getMinPreferredWidth();
               int maxWidth = cell.getPreferredWidth();
               int preferredWidth = cell.getSpecifiedWidth();
               if (end == j + 1) {
                  if (minWidth > minWidths[j]) {
                     minWidths[j] = minWidth;
                  }

                  if (maxWidth > maxWidths[j]) {
                     maxWidths[j] = maxWidth;
                  }

                  if (preferredWidth != -1) {
                     if (preferredWidth < minWidth) {
                        preferredWidth = minWidth;
                     }

                     preferredWidths[j] = Math.max(preferredWidths[j], preferredWidth);
                  }
               } else {
                  this.divUpWidths(minWidths, j, end, minWidth);
                  this.divUpWidths(maxWidths, j, end, maxWidth);
                  if (preferredWidth != -1) {
                     this.divUpWidths(preferredWidths, j, end, preferredWidth);
                  }

                  j = end - 1;
               }
            }
         }
      }
   }

   private final int getTotalWidthPadding() {
      return (this._cellPadding >> 0 & 0xFF) + (this._cellPadding >> 8 & 0xFF);
   }

   private final boolean setTableCellWidths(Object lockObj, int[] calculatedWidths) {
      boolean changed = false;
      if (lockObj == null) {
         this._manager.assertHaveEventLock();
         lockObj = calculatedWidths;
      }

      int rowCount = this._cellCollection.getRowCount();
      int colCount = this._cellCollection.getColCount();

      for (int row = 0; row < rowCount; row++) {
         for (int col = 0; col < colCount; col++) {
            TextFlowCell cell = (TextFlowCell)this._cellCollection.getCell(row, col);
            if (cell != null) {
               int width = 0;

               for (int j = col; j < cell._colEnd; j++) {
                  width += calculatedWidths[j];
               }

               col = cell._colEnd - 1;
               if (cell.getWidth() != width) {
                  changed = true;
                  synchronized (lockObj) {
                     cell.setWidth(width);
                  }

                  if (cell._containsSubCells) {
                     int endIndex = cell.getOutOfLineObjectEnd();
                     TextFlowData data = this._manager._textFlowData;

                     for (int i = cell._outoflineObjectStart; i < endIndex; i++) {
                        Object obj = data.getOutOfLineObject(i);
                        if (obj instanceof TextFlowCell) {
                           ((TextFlowCell)obj).reflow(lockObj);
                        }
                     }
                  }
               }
            }
         }
      }

      return changed;
   }
}
