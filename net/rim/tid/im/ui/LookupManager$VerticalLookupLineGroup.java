package net.rim.tid.im.ui;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

class LookupManager$VerticalLookupLineGroup extends LookupManager$LookupLineGroup {
   private int _firstItem;
   private int _lastItem;
   private int _maxVariantsPerFrame;
   private int _longestVariantPixelLength;
   private int _lastComposedY;
   private int _lastComposedHeight;
   private boolean _showScrollbar;
   private int _boundsWidth;
   private int _boundsHeight;
   private int _allowedHeight;
   private int _fontHeight;
   private int[] _ascents;
   private final LookupManager this$0;
   private static final int MAX_ALLOWED_VARIANTS_PER_FRAME;
   private static final int LEFT_INDENT;
   private static final int RIGHT_INDENT;

   public LookupManager$VerticalLookupLineGroup(LookupManager _1, boolean isAdditionalVariants) {
      super(_1, isAdditionalVariants);
      this.this$0 = _1;
      this._firstItem = -1;
      this._lastItem = -1;
   }

   @Override
   public void init() {
      this._firstItem = this._lastItem = -1;
   }

   @Override
   public void reset() {
      this.init();
   }

   @Override
   public void listOfVariantsChanged() {
      if (this.this$0._auxilaryElementsFont != null) {
         this.init();
         this.computeIndexes();
         this.calcFrames();
      }
   }

   @Override
   public void paint(Graphics graphics, Font fm, DrawTextParam param, int y) {
      if (this._longestVariantPixelLength > 0) {
         this.computeFirstAndLastItem();
         int x = 0;
         int from;
         int to;
         int delta;
         if (this.this$0._isPositionAboveComposedText && this.this$0._allowRevertedVariants) {
            from = this._lastItem - 1;
            to = this._firstItem - 1;
            delta = -1;
         } else {
            from = this._firstItem;
            to = this._lastItem;
            delta = 1;
         }

         for (int i = from; i != to; i += delta) {
            int fg = graphics.getColor();
            int bg = graphics.getBackgroundColor();
            if (i == this.this$0._currentIndex && this.this$0._currentVariant.isCurrentVariantHiglighted()) {
               graphics.setColor(this.this$0._backgroundColor);
               graphics.fillRect(x, y, this._longestVariantPixelLength, this._fontHeight);
               graphics.setColor(this.this$0._foregroundColor);
            }

            Font fontToDraw = this.this$0.transformFontForVariantIndex(fm, i);
            if (fontToDraw != fm) {
               graphics.setFont(fontToDraw);
            }

            this.this$0._currentVariant.getVariantAt(i, this.this$0._current);
            int adjustedY = y + Math.max(this._ascents[i - super._startIndex] - fontToDraw.getAscent() - fontToDraw.getLeading(), 0);
            graphics.drawText(this.this$0._current._variants, this.this$0._current._offset, this.this$0._current._length, x + 1, adjustedY, param, null);
            if (fontToDraw != fm) {
               graphics.setFont(fm);
            }

            graphics.setColor(fg);
            graphics.setBackgroundColor(bg);
            y += this._fontHeight;
            if (i != to - delta) {
               if (this.this$0._drawHorizontalSeparators) {
                  graphics.drawLine(x - 2, y, x + this._longestVariantPixelLength + 2, y);
               }

               y += 3;
            }
         }

         x += this._longestVariantPixelLength + 2;
         if (this._showScrollbar) {
            this.drawScrollbar(graphics, param, x);
            graphics.setFont(fm);
         }
      }
   }

   private void computeFirstAndLastItem() {
      int index = this.this$0._currentIndex >= super._startIndex && this.this$0._currentIndex < super._endIndex ? this.this$0._currentIndex : 0;
      int variantCount = Math.min(this._maxVariantsPerFrame, super._endIndex - super._startIndex);
      if (index < this._firstItem) {
         this._firstItem = index;
         this._lastItem = this._firstItem + variantCount;
      } else {
         if (index >= this._lastItem) {
            this._lastItem = index + 1;
            this._firstItem = this._lastItem - variantCount;
         }
      }
   }

   private void drawScrollbar(Graphics graphics, DrawTextParam param, int x) {
      String upArrow;
      String downArrow;
      if (this.this$0._isPositionAboveComposedText && this.this$0._allowRevertedVariants) {
         upArrow = this._lastItem == super._endIndex ? null : LookupManager._upArrow;
         downArrow = this._firstItem == super._startIndex ? null : LookupManager._downArrow;
      } else {
         upArrow = this._firstItem == super._startIndex ? null : LookupManager._upArrow;
         downArrow = this._lastItem == super._endIndex ? null : LookupManager._downArrow;
      }

      graphics.setFont(this.this$0._auxilaryElementsFont);
      int y1 = 2;
      int y2 = this.this$0._lastLookupHeight - this.this$0._delegator.getBorderTop() - this.this$0._delegator.getBorderBottom() - 2;
      if (upArrow != null) {
         graphics.drawText(upArrow, 0, 1, x + 2, y1, param, null);
      }

      if (downArrow != null) {
         graphics.drawText(downArrow, 0, 1, x + 2, y2 - graphics.getFont().getHeight(), param, null);
      }

      graphics.drawLine(x, y1, x, y2);
   }

   private void calcFrames() {
      this._fontHeight = this.this$0._predictiveElementsFont.getHeight();
      this._longestVariantPixelLength = 0;
      if (this._ascents == null || this._ascents.length < super._endIndex - super._startIndex) {
         this._ascents = new int[super._endIndex - super._startIndex];
      }

      for (int index = super._startIndex; index < super._endIndex; index++) {
         this.this$0._currentVariant.getVariantAt(index, this.this$0._current);
         Font fontToMeasure = this.this$0.transformFontForVariantIndex(this.this$0._predictiveElementsFont, index);
         fontToMeasure.measureText(this.this$0._current._variants, this.this$0._current._offset, this.this$0._current._length, null, this.this$0._textMetrics);
         int variantLen = Math.max(
            this.this$0._textMetrics.iBoundsBrX - this.this$0._textMetrics.iBoundsTlX,
            this.this$0._textMetrics.iAdvanceX >= this.this$0._textMetrics.iBoundsBrX
               ? this.this$0._textMetrics.iAdvanceX
               : this.this$0._textMetrics.iBoundsBrX
         );
         this._longestVariantPixelLength = Math.max(this._longestVariantPixelLength, variantLen);
         int height = this.this$0._textMetrics.iBoundsBrY - this.this$0._textMetrics.iBoundsTlY;
         if (height > this._fontHeight) {
            this._fontHeight = height;
         }

         this._ascents[index - super._startIndex] = -this.this$0._textMetrics.iBoundsTlY;
      }

      int heightPerVariant = this._fontHeight + 3;
      this._maxVariantsPerFrame = Math.min(4, this._allowedHeight / heightPerVariant);
      this._showScrollbar = this._maxVariantsPerFrame < super._endIndex - super._startIndex;
      this._longestVariantPixelLength += 3;
      this._firstItem = super._startIndex;
      int variantCount = Math.min(this._maxVariantsPerFrame, super._endIndex - super._startIndex);
      this._lastItem = super._startIndex + variantCount;
      if (this._lastItem == this._firstItem) {
         this._boundsHeight = 0;
         this._boundsWidth = 0;
      } else {
         this._boundsHeight = (this._fontHeight + 3) * (this._lastItem - this._firstItem) - 3;
         this._boundsWidth = this._showScrollbar ? this._longestVariantPixelLength + 4 + super._typeWidth : this._longestVariantPixelLength;
      }
   }

   @Override
   public int getHeight(Font font) {
      return this._boundsHeight;
   }

   @Override
   public int getWidth(Font font) {
      return this._boundsWidth;
   }

   @Override
   public void layout(Font aFont, boolean firstLayout, int allowedHeight) {
      if (firstLayout) {
         super._typeWidth = (byte)this.this$0._auxilaryElementsFont.getBounds(LookupManager._leftArrow);
      }

      if (firstLayout || this.this$0._composedY != this._lastComposedY || this.this$0._composedHeight != this._lastComposedHeight) {
         this._lastComposedY = this.this$0._composedY;
         this._lastComposedHeight = this.this$0._composedHeight;
      }

      this._allowedHeight = allowedHeight;
      this.listOfVariantsChanged();
   }

   @Override
   public boolean handleElementTraverse(int action) {
      switch (action) {
         case -110:
            if (this.this$0._isPositionAboveComposedText && this.this$0._allowRevertedVariants) {
               int index = this.this$0._currentVariant.getCurrentVariantIndex();
               this.this$0.prevElement();
               if (index != this.this$0._currentVariant.getCurrentVariantIndex()) {
                  return true;
               }

               return false;
            }

            this.this$0.nextElement();
            return true;
         case -109:
            if (this.this$0._isPositionAboveComposedText && this.this$0._allowRevertedVariants) {
               this.this$0.nextElement();
               return true;
            } else {
               int index = this.this$0._currentVariant.getCurrentVariantIndex();
               this.this$0.prevElement();
               if (index != this.this$0._currentVariant.getCurrentVariantIndex()) {
                  return true;
               }

               return false;
            }
         case 32:
            return true;
         default:
            return false;
      }
   }

   @Override
   public Font getFont(Font fm) {
      return this.this$0._predictiveElementsFont;
   }

   @Override
   public int getVerticalElementCount() {
      return super._endIndex - super._startIndex;
   }

   @Override
   public int getMaxVerticalElementsPerFrame() {
      return this._maxVariantsPerFrame;
   }
}
