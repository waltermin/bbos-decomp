package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.ui.Edit$BidiLineRuns;
import net.rim.device.internal.ui.RichText;
import net.rim.device.internal.ui.TextFlowRegion;
import net.rim.vm.Array;

final class TextFlowCell$Painter {
   private TextFlowData _textFlowData;
   private TextFlowLayout _textFlowLayout;
   private TextFlowCell _currentCell;
   private Graphics _graphics;
   private TextGraphics _textGraphics = (TextGraphics)(new Object(Font.getDefault()));
   private TextFlowRegion _textRegion;
   private Font _managerFont;
   private int _lineNumber;
   private int _x;
   private int _y;
   private int _textPosition;
   private int _defaultFg;
   private int _region;
   private int _nextPaintingRegion;
   private int _scale;
   private int _textRegionCharsRemain;
   private int _fontBaseline;
   private Font _font;
   private int _bgColour;
   private boolean _selecting;
   private int[] _bidiDataRuns = new int[256];
   private int[] _bidiStyleBreaks = new int[256];
   private int _bidiTextOffset;
   private int _numBidiDataRuns;
   private int _cellBgColour;

   public final void StartPainting(TextFlowCell cell, Graphics graphics, Font managerFont, int lineNumber, int y, int textPosition, int scale) {
      this._currentCell = cell;
      this._textFlowLayout = cell._manager._textFlowLayout;
      this._textFlowData = cell._manager._textFlowData;
      this._lineNumber = lineNumber;
      short[] lineCellIds = this._textFlowLayout.getLineCellIds();
      short[] lineLengths = this._textFlowLayout.getLineLengths();

      for (int cellNestingMasked = this._currentCell._cellNesting & '\uffff';
         (lineCellIds[this._lineNumber] & '\uffff') > cellNestingMasked && this._textPosition < this._currentCell._layoutEndPosition;
         this._lineNumber++
      ) {
         this._textPosition = this._textPosition + lineLengths[this._lineNumber];
      }

      this._graphics = graphics;
      this._managerFont = managerFont;
      if (scale > 65536) {
         this._textGraphics.setFontSpec(this._managerFont);
      }

      this._lineNumber = lineNumber;
      this._y = y;
      this._textPosition = textPosition;
      this._defaultFg = graphics.getColor();
      this._scale = scale;
      this._cellBgColour = this._textFlowData.getRegion(cell._textStartRegion)._backgroundColour;
      this.Start();
   }

   public final void StartMeasuring(TextFlowCell cell, int textPosition) {
      this._currentCell = cell;
      this._textFlowLayout = cell._manager._textFlowLayout;
      this._textFlowData = cell._manager._textFlowData;
      this._currentCell.cacheFromTextPosition(textPosition);
      this._y = this._currentCell._cachePixelLine + this._currentCell._rowHeightPaddingTop;
      this._lineNumber = this._currentCell._cacheTextLine;
      this._textPosition = this._currentCell._cacheTextOffset;
      this.Start();
   }

   private final void Start() {
      this._x = this._textFlowLayout.getLineXOffsets()[this._lineNumber];
      this.updateRegionState(true, this._textFlowData.getFirstRegionIndexFromTextPosition(this._textPosition));
   }

   public final void End() {
      if (this._graphics != null) {
         this._graphics.setColor(this._defaultFg);
      }

      this._graphics = null;
      this._managerFont = null;
      this._textRegion = null;
      this._font = null;
      this._currentCell = null;
      this._textFlowLayout = null;
      this._textFlowData = null;
      this._bidiTextOffset = 0;
      this._numBidiDataRuns = 0;
      this._cellBgColour = -1;
      this._scale = 65536;
   }

   public final int getYPosition() {
      return this._y;
   }

   public final int getXPosition() {
      return this._x;
   }

   public final int getTextPosition() {
      return this._textPosition;
   }

   public final int getLineNumber() {
      return this._lineNumber;
   }

   private final void setBidiData() {
      if ((this._textFlowLayout.getLineFlags()[this._lineNumber] & 128) == 0) {
         this._bidiTextOffset = 0;
         this._numBidiDataRuns = 0;
      } else {
         byte[] bidiState = this._textFlowLayout.getLineBidiState(this._lineNumber);
         int lineLength = this._textFlowLayout.getLineLengths()[this._lineNumber];
         int lineEnd = this._textPosition + lineLength;
         int maxNextRegion = this._region;
         int currentRegion = this._region;
         int styleCount = 0;
         this._bidiStyleBreaks[styleCount++] = this._textPosition;
         int currentPos = Math.min(this._textFlowData.getRegionEndOffset(currentRegion), this._textFlowData.getRegionStartOffset(maxNextRegion + 1));

         while (currentPos < lineEnd) {
            if (this._bidiStyleBreaks.length <= styleCount) {
               Array.resize(this._bidiStyleBreaks, styleCount + 256);
            }

            this._bidiStyleBreaks[styleCount++] = currentPos;
            if (this._textFlowData.getRegionStartOffset(maxNextRegion + 1) == currentPos) {
               currentRegion = maxNextRegion + 1;
            } else {
               currentRegion = this._textFlowData.getRegionParentId(currentRegion);
               if (currentRegion == -1) {
                  break;
               }
            }

            maxNextRegion = Math.max(currentRegion, maxNextRegion);
            int nextPos = Math.min(this._textFlowData.getRegionEndOffset(currentRegion), this._textFlowData.getRegionStartOffset(maxNextRegion + 1));
            if (nextPos > lineEnd) {
               nextPos = lineEnd;
            }

            if (this._currentCell._manager._selectionCellBegin > currentPos && this._currentCell._manager._selectionCellBegin < nextPos) {
               if (this._bidiStyleBreaks.length <= styleCount) {
                  Array.resize(this._bidiStyleBreaks, styleCount + 256);
               }

               this._bidiStyleBreaks[styleCount++] = this._currentCell._manager._selectionCellBegin;
            }

            if (this._currentCell._manager._selectionCellEnd > currentPos && this._currentCell._manager._selectionCellEnd < nextPos) {
               if (this._bidiStyleBreaks.length <= styleCount) {
                  Array.resize(this._bidiStyleBreaks, styleCount + 256);
               }

               this._bidiStyleBreaks[styleCount++] = this._currentCell._manager._selectionCellEnd;
            }

            currentPos = nextPos;
         }

         if (this._bidiStyleBreaks.length <= styleCount) {
            Array.resize(this._bidiStyleBreaks, styleCount + 1);
         }

         this._bidiStyleBreaks[styleCount] = lineEnd;
         short flags = this._textFlowData.getRegionFlags(this._region);
         int dir = 0;
         if ((flags & 16) != 0) {
            dir = 2;
         } else if ((flags & 32) != 0) {
            dir = 3;
         } else if ((flags & 8) != 0) {
            dir = 1;
         }

         Edit$BidiLineRuns bidiData = RichText.getBidiOrder(
            this._textFlowData.getText(),
            this._textPosition,
            lineLength,
            bidiState,
            (this._textFlowLayout.getLineFlags()[this._lineNumber] & 1) != 0,
            dir,
            this._bidiStyleBreaks,
            0,
            styleCount
         );
         if (bidiData.isIgnored()) {
            this._bidiTextOffset = 0;
            this._numBidiDataRuns = 0;
         } else {
            if (this._bidiDataRuns.length < bidiData._runs.length) {
               Array.resize(this._bidiDataRuns, bidiData._runs.length);
            }

            System.arraycopy(bidiData._runs, 0, this._bidiDataRuns, 0, bidiData._runs.length);
            this._bidiTextOffset = this._textPosition;
            this._numBidiDataRuns = bidiData._runs.length;
         }
      }
   }

   public final void MeasureLineStartEnd(int startPos, int endPos, boolean end) {
      this._x = this._textFlowLayout.getLineXOffsets()[this._lineNumber];
      if (this._numBidiDataRuns <= 3) {
         int toPos = end ? endPos : startPos;
         DrawTextParam drawParam = null;
         if (this._numBidiDataRuns == 3) {
            drawParam = Ui.getTmpDrawTextParam();
            drawParam.iReverse = this._bidiDataRuns[2];
         }

         this._x = this.doChars(this._x, toPos - this._textPosition, drawParam);
         if (drawParam != null) {
            Ui.returnTmpDrawTextParam(drawParam);
         }
      } else {
         int toPos = startPos;
         if (end) {
            for (int i = this._numBidiDataRuns - 1; i >= 0; i -= 3) {
               int runStart = this._bidiDataRuns[i - 2] + this._bidiTextOffset;
               int runLength = this._bidiDataRuns[i - 1];
               if (runStart >= startPos && runStart + runLength <= endPos) {
                  toPos = runStart + runLength;
                  break;
               }
            }
         } else {
            for (int i = 0; i < this._numBidiDataRuns; i += 3) {
               int runStart = this._bidiDataRuns[i] + this._bidiTextOffset;
               int runLength = this._bidiDataRuns[i + 1];
               if (runStart >= startPos && runStart + runLength <= endPos) {
                  toPos = runStart;
                  break;
               }
            }
         }

         DrawTextParam drawParam = Ui.getTmpDrawTextParam();
         boolean continueMeasuring = true;

         for (int i = 0; continueMeasuring && i < this._numBidiDataRuns; i++) {
            int runStart = this._bidiDataRuns[i++] + this._bidiTextOffset;
            int runLength = this._bidiDataRuns[i++];
            if (end) {
               if (runStart < toPos && runStart + runLength >= toPos) {
                  runLength = toPos - runStart;
                  continueMeasuring = false;
               }
            } else if (runStart <= toPos && runStart + runLength > toPos) {
               runLength = toPos - runStart;
               continueMeasuring = false;
            }

            drawParam.iReverse = this._bidiDataRuns[i];
            this._textPosition = runStart;
            this.updateRegionState(true, this._textFlowData.getFirstRegionIndexFromTextPosition(this._textPosition));
            this._x = this.doChars(this._x, runLength, drawParam);
         }

         Ui.returnTmpDrawTextParam(drawParam);
      }
   }

   public final void MeasureLine(int toPos, int toXPos, boolean end) {
      this._x = this._textFlowLayout.getLineXOffsets()[this._lineNumber];
      if (this._numBidiDataRuns <= 3) {
         DrawTextParam drawParam = null;
         if (this._numBidiDataRuns == 3) {
            drawParam = Ui.getTmpDrawTextParam();
            drawParam.iReverse = this._bidiDataRuns[2];
         }

         if (toXPos != Integer.MAX_VALUE) {
            if (drawParam == null) {
               drawParam = Ui.getTmpDrawTextParam();
            }

            drawParam.iMaxAdvance = toXPos - this._x;
         }

         this._x = this.doChars(this._x, toPos - this._textPosition, drawParam);
         if (drawParam != null) {
            Ui.returnTmpDrawTextParam(drawParam);
         }
      } else {
         DrawTextParam drawParam = Ui.getTmpDrawTextParam();
         if (toXPos != Integer.MAX_VALUE) {
            drawParam.iMaxAdvance = toXPos - this._x;
         }

         boolean continueMeasuring = true;

         for (int i = 0; continueMeasuring && i < this._numBidiDataRuns; i++) {
            int runStart = this._bidiDataRuns[i++] + this._bidiTextOffset;
            int runLength = this._bidiDataRuns[i++];
            if (end) {
               if (runStart < toPos && runStart + runLength >= toPos) {
                  runLength = toPos - runStart;
                  continueMeasuring = false;
               }
            } else if (runStart <= toPos && runStart + runLength > toPos) {
               runLength = toPos - runStart;
               continueMeasuring = false;
            }

            drawParam.iReverse = this._bidiDataRuns[i];
            this._textPosition = runStart;
            this.updateRegionState(true, this._textFlowData.getFirstRegionIndexFromTextPosition(this._textPosition));
            this._x = this.doChars(this._x, runLength, drawParam);
         }

         Ui.returnTmpDrawTextParam(drawParam);
      }
   }

   public final void PaintLine() {
      if (this._textPosition < this._textFlowData.getRegionStartOffset(this._region)
         || this._textPosition >= this._textFlowData.getRegionEndOffset(this._region)) {
         Asserts.productionAssert(false);
      }

      short[] lineCellIds = this._textFlowLayout.getLineCellIds();
      if (lineCellIds[this._lineNumber] != this._currentCell._cellNesting) {
         short[] lineLengths = this._textFlowLayout.getLineLengths();

         for (int cellNestingMasked = this._currentCell._cellNesting & '\uffff';
            (lineCellIds[this._lineNumber] & '\uffff') > cellNestingMasked && this._textPosition < this._currentCell._layoutEndPosition;
            this._lineNumber++
         ) {
            this._textPosition = this._textPosition + lineLengths[this._lineNumber];
         }

         if (this._textPosition < this._currentCell._layoutEndPosition) {
            this._x = this._textFlowLayout.getLineXOffsets()[this._lineNumber];
            this.updateRegionState(true, this._textFlowData.getFirstRegionIndexFromTextPosition(this._textPosition));
         }
      } else {
         int lineEnd = this._textPosition + this._textFlowLayout.getLineLengths()[this._lineNumber];
         int selectionBegin = this._currentCell._manager._selectionCellBegin;
         int selectionEnd = this._currentCell._manager._selectionCellEnd;
         this.setBidiData();
         if (this._numBidiDataRuns > 3) {
            DrawTextParam drawParam = Ui.getTmpDrawTextParam();

            for (int i = 0; i < this._numBidiDataRuns; i++) {
               int runStart = this._bidiDataRuns[i++] + this._bidiTextOffset;
               int runLength = this._bidiDataRuns[i++];
               drawParam.iReverse = this._bidiDataRuns[i];
               this._textPosition = runStart;
               this.updateRegionState(true, this._textFlowData.getFirstRegionIndexFromTextPosition(this._textPosition));
               this._x = this.doChars(this._x, runLength, drawParam);
            }

            Ui.returnTmpDrawTextParam(drawParam);
            this._textPosition = lineEnd;
            this.updateRegionState(true, this._textFlowData.getFirstRegionIndexFromTextPosition(this._textPosition));
         } else {
            DrawTextParam drawParam = null;
            if (this._numBidiDataRuns == 3) {
               drawParam = Ui.getTmpDrawTextParam();
               drawParam.iReverse = this._bidiDataRuns[2];
            }

            while (true) {
               int length = lineEnd - this._textPosition;
               if (length <= 0) {
                  if (drawParam != null) {
                     Ui.returnTmpDrawTextParam(drawParam);
                  }
                  break;
               }

               if (this._selecting) {
                  if (this._textPosition + length > selectionEnd) {
                     length = selectionEnd - this._textPosition;
                  }
               } else if (this._textPosition + length > selectionBegin && this._textPosition < selectionEnd) {
                  length = selectionBegin - this._textPosition;
               }

               this._x = this.doChars(this._x, length, drawParam);
               this.updateRegionState(false, 0);
            }
         }

         this._y = this._y + this._textFlowLayout.getLineHeights()[this._lineNumber];
         this._lineNumber++;
         if (this._lineNumber < this._textFlowLayout.getLineCount()) {
            this._x = this._textFlowLayout.getLineXOffsets()[this._lineNumber];
            return;
         }
      }
   }

   public final int GetXOffsetOfTextPosition(TextFlowCell cell, int textPosition, boolean end) {
      this._currentCell = cell;
      this._textFlowLayout = cell._manager._textFlowLayout;
      this._textFlowData = cell._manager._textFlowData;
      this._currentCell.cacheFromTextPosition(textPosition - (end ? 1 : 0));
      this._lineNumber = this._currentCell._cacheTextLine;
      this._textPosition = this._currentCell._cacheTextOffset;
      if (this._lineNumber >= this._textFlowLayout.getLineCount()) {
         this.End();
         return 0;
      } else {
         this.Start();
         this.setBidiData();
         this.MeasureLine(textPosition, Integer.MAX_VALUE, end);
         this.End();
         return this._x;
      }
   }

   public final int GetTextOffsetOfXPosition(TextFlowCell cell, int startOfLine, int xPos) {
      this._currentCell = cell;
      this._textFlowLayout = cell._manager._textFlowLayout;
      this._textFlowData = cell._manager._textFlowData;
      this._currentCell.cacheFromTextPosition(startOfLine);
      this._lineNumber = this._currentCell._cacheTextLine;
      this._textPosition = this._currentCell._cacheTextOffset;
      if (this._lineNumber >= this._textFlowLayout.getLineCount()) {
         this.End();
         return 0;
      } else {
         this.Start();
         this.setBidiData();
         this.MeasureLine(this._textPosition + this._textFlowLayout.getLineLengths()[this._lineNumber], xPos, false);
         this.End();
         return this._textPosition;
      }
   }

   public final int GetXOffsetOfLine(TextFlowCell cell, int textPosition, int textEndPosition, boolean end, int scale) {
      this._currentCell = cell;
      this._textFlowLayout = cell._manager._textFlowLayout;
      this._textFlowData = cell._manager._textFlowData;
      this._currentCell.cacheFromTextPosition(textPosition);
      this._lineNumber = this._currentCell._cacheTextLine;
      this._textPosition = this._currentCell._cacheTextOffset;
      if (this._lineNumber >= this._textFlowLayout.getLineCount()) {
         this.End();
         return 0;
      } else {
         this._scale = scale;
         this.Start();
         this.setBidiData();
         this.MeasureLineStartEnd(textPosition, textEndPosition, end);
         this.End();
         return this._x;
      }
   }

   private final void updateRegionState(boolean setRegion, int region) {
      int regionCount = this._textFlowData.getRegionCount();
      if (setRegion && region < regionCount) {
         while (region < regionCount && this._textFlowData.getRegionStartOffset(region + 1) <= this._textPosition) {
            region++;
         }

         int maxStartRegion = region;

         while (region > 0 && this._textFlowData.getRegionEndOffset(region) <= this._textPosition) {
            region--;
         }

         this._region = region;
         this._nextPaintingRegion = maxStartRegion + 1;
      }

      if (this._region >= regionCount) {
         this._selecting = false;
         this._textRegionCharsRemain = 0;
      } else {
         this._selecting = this._textPosition >= this._currentCell._manager._selectionCellBegin
            && this._textPosition < this._currentCell._manager._selectionCellEnd;
         this._bgColour = -1;
         this._textRegionCharsRemain = Math.min(
               this._textFlowData.getRegionStartOffset(this._nextPaintingRegion), this._textFlowData.getRegionEndOffset(this._region)
            )
            - this._textPosition;
         this._textRegion = this._textFlowData.getRegion(this._region);
         if (this._textFlowData.isRegionText(this._region)) {
            this._font = this._textRegion.getFont();
         } else {
            this._font = null;
         }

         if (this._graphics != null) {
            if (this._font != null) {
               this._fontBaseline = this._font.getBaseline();
               this._graphics.setFont(this._font);
            }

            if (!this._graphics.isDrawingStyleSet(8)) {
               if (this._selecting && this._textFlowData.isRegionText(this._region)) {
                  this._graphics.setColor(ThemeAttributeSet.getColor(this._currentCell._manager, 5));
                  this._graphics.setBackgroundColor(ThemeAttributeSet.getColor(this._currentCell._manager, 4));
                  return;
               }

               int colour = this._textRegion._foregroundColour;
               if (colour == -1) {
                  this._graphics.setColor(this._defaultFg);
               } else {
                  this._graphics.setColor(colour);
               }

               if (this._textRegion._backgroundColour != this._cellBgColour) {
                  this._bgColour = this._textRegion._backgroundColour;
                  return;
               }

               this._bgColour = -1;
            }
         }
      }
   }

   private final int doChars(int currentX, int length, DrawTextParam drawParam) {
      boolean returnDrawParam = false;
      if (drawParam == null) {
         drawParam = Ui.getTmpDrawTextParam();
         returnDrawParam = true;
      }

      int originalX = currentX;
      boolean drawScaled = this._scale != 65536;
      int lineBaseline = 0;
      int originalMaxAdvance = drawParam.iMaxAdvance;
      StringBuffer text = this._textFlowData.getText();
      TextMetrics metrics = null;
      if (originalMaxAdvance != Integer.MAX_VALUE) {
         metrics = Ui.getTmpTextMetrics();
      }

      if (this._graphics != null) {
         lineBaseline = this._textFlowLayout.getLineBaselines()[this._lineNumber];
      }

      int regionCount = this._textFlowData.getRegionCount();

      while (length > 0) {
         if (originalMaxAdvance != Integer.MAX_VALUE) {
            drawParam.iMaxAdvance = originalMaxAdvance - (currentX - originalX);
            if (drawParam.iMaxAdvance <= 0) {
               break;
            }
         }

         Asserts.productionStateAssert(this._region < regionCount);
         int charsToDraw = length;
         if (charsToDraw > this._textRegionCharsRemain) {
            charsToDraw = this._textRegionCharsRemain;
         }

         if (this._font != null) {
            if (this._textFlowData.isRegionText(this._region)) {
               boolean hidden = (this._textRegion.getFlags() & 2048) != 0;
               if (!hidden && this._graphics != null) {
                  drawParam.iDrawNonPrintableCharacters = false;
                  if (!drawScaled) {
                     if (this._selecting) {
                        int width = this._font.measureText(text, this._textPosition, charsToDraw, drawParam, null);
                        this._graphics.clear(currentX, this._y, width, this._textFlowLayout.getLineHeights()[this._lineNumber]);
                     } else if (this._bgColour != -1) {
                        int width = this._font.measureText(text, this._textPosition, charsToDraw, drawParam, null);
                        int oldColour = this._graphics.getColor();
                        this._graphics.setColor(this._bgColour);
                        this._graphics.fillRect(currentX, this._y, width, this._textFlowLayout.getLineHeights()[this._lineNumber]);
                        this._graphics.setColor(oldColour);
                     }

                     if (charsToDraw > 0 && text.charAt(this._textPosition + charsToDraw - 1) == 173) {
                        currentX += this._graphics
                           .drawText(text, this._textPosition, charsToDraw, currentX, this._y + lineBaseline - this._fontBaseline, drawParam, null);
                        currentX += this._graphics.drawText("‐", 0, 1, currentX, this._y + lineBaseline - this._fontBaseline, drawParam, null);
                     } else {
                        currentX += this._graphics
                           .drawText(text, this._textPosition, charsToDraw, currentX, this._y + lineBaseline - this._fontBaseline, drawParam, null);
                     }
                  } else {
                     int height = RendererControl.fixed32DivToInt(this._font.getHeight(), this._scale);
                     if (height <= 0) {
                        height = 1;
                     }

                     this._textGraphics.setTypefaceName(this._font.getFontFamily().getName());
                     this._textGraphics.setHeight(height);
                     this._textGraphics.setStyle(this._font.getStyle());
                     int scaledCurrentX = RendererControl.fixed32DivToInt(currentX, this._scale);
                     int width = -1;
                     if (this._bgColour != -1) {
                        width = this._textGraphics.measureText(text, this._textPosition, charsToDraw, drawParam, null);
                        int oldColour = this._graphics.getColor();
                        this._graphics.setColor(this._bgColour);
                        this._graphics
                           .fillRect(
                              scaledCurrentX,
                              RendererControl.fixed32DivToInt(this._y, this._scale),
                              RendererControl.fixed32DivToInt(width, this._scale),
                              RendererControl.fixed32DivToInt(this._textFlowLayout.getLineHeights()[this._lineNumber], this._scale)
                           );
                        this._graphics.setColor(oldColour);
                     }

                     int result = this._textGraphics
                        .drawText(
                           this._graphics,
                           text,
                           this._textPosition,
                           charsToDraw,
                           scaledCurrentX,
                           RendererControl.fixed32DivToInt(this._y + lineBaseline - this._fontBaseline, this._scale),
                           drawParam,
                           null
                        );
                     result = RendererControl.fixed32MultToInt(result, this._scale);
                     currentX += Math.max(result, this._font.measureText(text, this._textPosition, charsToDraw, drawParam, null));
                  }
               } else {
                  drawParam.iDrawNonPrintableCharacters = true;
                  int unscaledWidth = this._font.measureText(text, this._textPosition, charsToDraw, drawParam, metrics);
                  if (drawScaled) {
                     int height = RendererControl.fixed32DivToInt(this._font.getHeight(), this._scale);
                     if (height <= 0) {
                        height = 1;
                     }

                     this._textGraphics.setTypefaceName(this._font.getFontFamily().getName());
                     this._textGraphics.setHeight(height);
                     this._textGraphics.setStyle(this._font.getStyle());
                     currentX += Math.max(
                        RendererControl.fixed32DivToInt(unscaledWidth, this._scale),
                        this._textGraphics.measureText(text, this._textPosition, charsToDraw, drawParam, metrics)
                     );
                  } else {
                     currentX += unscaledWidth;
                  }

                  if (metrics != null && metrics.iCharacters != charsToDraw) {
                     charsToDraw = metrics.iCharacters;
                     length = charsToDraw;
                  }
               }
            }
         } else {
            for (int i = 0; i < charsToDraw; i++) {
               if (text.charAt(this._textPosition + i) == '￼') {
                  Field field = (Field)this._textFlowData.getRegionObject(this._region);
                  if (this._graphics != null) {
                     if (drawScaled) {
                        this._currentCell._manager.paintScaledField(field, this._scale, this._graphics);
                     } else {
                        this._graphics.setFont(this._managerFont);
                        int oldFg = this._graphics.getColor();
                        int oldBg = this._graphics.getBackgroundColor();
                        this._graphics.setColor(0);
                        this._graphics.setBackgroundColor(16777215);
                        this._graphics.setStipple(-1);
                        if (this._numBidiDataRuns > 0) {
                           this._currentCell._manager.placeField(field, currentX, this._y);
                        }

                        this._currentCell._manager.paintField(this._graphics, field);
                        this._graphics.setColor(oldFg);
                        this._graphics.setBackgroundColor(oldBg);
                     }
                  }

                  int fieldWidth = field.getWidth();
                  if (originalMaxAdvance != Integer.MAX_VALUE && fieldWidth > drawParam.iMaxAdvance) {
                     charsToDraw = i;
                     length = i;
                     break;
                  }

                  currentX += field.getWidth();
               }
            }
         }

         length -= charsToDraw;
         this._textPosition += charsToDraw;
         this._textRegionCharsRemain -= charsToDraw;
         if (this._textRegionCharsRemain == 0) {
            this.updateRegionState(true, this._region);
         }
      }

      if (returnDrawParam) {
         Ui.returnTmpDrawTextParam(drawParam);
      }

      if (metrics != null) {
         Ui.returnTmpTextMetrics(metrics);
      }

      return currentX;
   }
}
