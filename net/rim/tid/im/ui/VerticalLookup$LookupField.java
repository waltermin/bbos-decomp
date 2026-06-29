package net.rim.tid.im.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;

class VerticalLookup$LookupField extends Manager {
   VerticalLookup _parent;

   VerticalLookup$LookupField() {
      super(0);
   }

   @Override
   protected void sublayout(int width, int height) {
      this.setExtent(width, (VerticalLookup._lookupFont.getHeight() + 1) * this._parent._maxVisible);
   }

   public void setParent(VerticalLookup parent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void paint(Graphics graphics) {
      graphics.setFont(VerticalLookup._lookupFont);
      int fontHeight = VerticalLookup._lookupFont.getHeight();
      if (this._parent._currentVariant != null && this._parent._currentVariantIndex != -1) {
         int x = 0;
         int y = 0;
         int dy = fontHeight + 1;
         int insideWidth = this._parent._longestVariantWidth;
         int width;
         if ((this._parent._numberingStyle & 1) != 0) {
            insideWidth += this._parent._widestNumWidth;
            width = insideWidth + 4;
         } else {
            insideWidth += this._parent._downArrowBitmap.getWidth();
            width = insideWidth;
         }

         this.paintHeader(graphics, 0, 0, 0, width);
         int i = this._parent._firstItem;

         for (int numIndex = 0; i <= this._parent._lastItem; numIndex++) {
            int currentX = x;
            if ((this._parent._numberingStyle & 1) != 0) {
               this.paintItemNumber(graphics, numIndex, currentX, y);
               currentX += this._parent._widestNumWidth;
            }

            this.paintItem(graphics, i, currentX, y, fontHeight - 1, this._parent._longestVariantWidth);
            this.checkHighLighting(graphics, i, x, y, fontHeight - 1, insideWidth);
            y += dy;
            i++;
         }

         if (this._parent._showArrows && (this._parent._numberingStyle & 2) != 0) {
            this.sideArrows(graphics, width, this._parent._statusAreaWidth);
         }

         this.paintFooter(graphics, 0, y, 0, width);
         graphics.setFont(VerticalLookup._defaultFont);
      }
   }

   protected void paintItemNumber(Graphics graphics, int index, int x, int y) {
      int current = graphics.getColor();
      graphics.setColor(255);
      graphics.drawText(VerticalLookup.NUMBERS[index + 1], x, y);
      graphics.setColor(current);
   }

   protected void paintItem(Graphics graphics, int index, int x, int y, int height, int width) {
      this._parent._currentVariant.getVariantAt(index, this._parent._current);
      graphics.drawText(this._parent._current._variants, this._parent._current._offset, this._parent._current._length, x, y, 64, width);
   }

   protected void checkHighLighting(Graphics graphics, int index, int x, int y, int height, int width) {
      if (index == this._parent._currentVariantIndex && this._parent._currentVariant.isCurrentVariantHiglighted()) {
         graphics.invert(x, y, width, height);
      }
   }

   protected void paintHeader(Graphics graphics, int x, int y, int height, int width) {
      if ((this._parent._numberingStyle & 2) == 0) {
         if (this._parent._firstItem != 0) {
            int arrowShift = width - this._parent._upArrowBitmap.getWidth();
            graphics.drawBitmap(
               x + arrowShift, y, this._parent._upArrowBitmap.getWidth(), this._parent._upArrowBitmap.getHeight(), this._parent._upArrowBitmap, 0, 0
            );
         }
      }
   }

   protected void paintFooter(Graphics graphics, int x, int y, int height, int width) {
      if ((this._parent._numberingStyle & 2) == 0) {
         if (this._parent._lastItem + 1 < this._parent.getTotalItems()) {
            int arrowShift = width - this._parent._downArrowBitmap.getWidth();
            graphics.drawBitmap(
               x + arrowShift,
               y - this._parent._downArrowBitmap.getHeight(),
               this._parent._downArrowBitmap.getWidth(),
               this._parent._downArrowBitmap.getHeight(),
               this._parent._downArrowBitmap,
               0,
               0
            );
         }
      }
   }

   protected void sideArrows(Graphics graphics, int x, int width) {
      Font fm = VerticalLookup._lookupFont;
      if (this._parent._maxVisible <= 3) {
         fm = fm.derive(fm.getStyle(), 8, 3);
         graphics.setFont(fm);
      } else if (fm.getHeight(3) > 10) {
         fm = fm.derive(fm.getStyle(), 10, 3);
         graphics.setFont(fm);
      }

      int fontHeight = fm.getHeight();
      int y = 2;
      graphics.drawLine(x, 0, x, this.getHeight());
      StringBuffer widthCalcBuffer = this._parent._widthCalculationBuffer;
      widthCalcBuffer.setLength(0);
      widthCalcBuffer.append("▲");
      int arrowShift = this.calcMiddlePositioningShift(widthCalcBuffer, this._parent._statusAreaWidth, fm);
      if (this._parent._firstItem > 0) {
         graphics.drawText("▲", x + arrowShift, y);
      }

      if (this._parent._lastItem < this._parent.getTotalItems() - 1) {
         y = this.getHeight() - 2 - fontHeight;
         graphics.drawText("▼", x + arrowShift, y);
      }

      widthCalcBuffer.setLength(0);
      widthCalcBuffer.append(this._parent._currentVariantIndex + 1);
      int leadingShift = this.calcMiddlePositioningShift(widthCalcBuffer, width, fm);
      int yMid = this.getHeight() / 2 + 2;
      int current = graphics.getColor();
      graphics.setColor(255);
      graphics.drawLine(x + 2 + 1, yMid, x + width - 2, yMid);
      graphics.drawLine(x + 2 + 1, yMid - 1, x + width - 2, yMid - 1);
      graphics.drawText(widthCalcBuffer, 0, widthCalcBuffer.length(), x + leadingShift, yMid - fontHeight - 2, 0, width);
      int maxVariantsShift = this.calcMiddlePositioningShift(this._parent._maxVariantsStringHolder, width, fm);
      graphics.drawText(this._parent._maxVariantsStringHolder, x + maxVariantsShift, yMid + 2);
      graphics.setColor(current);
      graphics.setFont(VerticalLookup._defaultFont);
   }

   int calcMiddlePositioningShift(StringBuffer text, int areaWidth, Font font) {
      int textWidth = font.getBounds(text);
      return Math.max(0, areaWidth / 2 - textWidth / 2);
   }

   int calcMiddlePositioningShift(String text, int areaWidth, Font font) {
      int textWidth = font.getBounds(text);
      return Math.max(0, areaWidth / 2 - textWidth / 2);
   }
}
