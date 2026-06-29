package net.rim.tid.im.ui;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;

class LookupImpl2$LookupField extends Manager {
   LookupImpl2 _outer;
   private DrawTextParam _drawTextParam = (DrawTextParam)(new Object());

   LookupImpl2$LookupField() {
      super(0);
   }

   @Override
   protected void sublayout(int width, int height) {
      height = this.getFont().getHeight();
      height += height / 3 + height % 3;
      this.setExtent(width, height);
   }

   private void drawOverlaped(Graphics graphics, Font font, int x, int y, int overlapLength) {
      int fg = graphics.getColor();
      graphics.setColor(8421504);
      graphics.drawText(this._outer._current._variants, this._outer._current._offset, overlapLength, x, y, this._drawTextParam, null);
      graphics.setColor(fg);
      x += font.getBounds(this._outer._current._variants, this._outer._current._offset, overlapLength);
      graphics.drawText(
         this._outer._current._variants,
         this._outer._current._offset + overlapLength,
         this._outer._current._length - overlapLength,
         x,
         y,
         this._drawTextParam,
         null
      );
   }

   @Override
   protected void paint(Graphics graphics) {
      Font fm = this.getFont();
      graphics.setFont(fm);
      if (this._outer._currentVariant != null && this._outer._currentIndex != -1 && this._outer._currentVariant.getVariantsCount() != 0) {
         int frameIndex = this._outer.calcFrames(fm);
         if (frameIndex >= 0) {
            int x = 0;
            int fontHeight = fm.getHeight();
            int y = (this.getHeight() - fontHeight >> 1) + 2;
            if (this._outer._leftLabel != null) {
               x += this._outer.paintLeftLabel(graphics, x, y);
               graphics.setFont(fm);
            }

            int len = 0;
            int numLength = 0;
            if (this._outer._indexesOfFramesWithOneVariant.contains((byte)frameIndex)) {
               Font fontToDraw = this._outer.transformFontForVariantIndex(fm, this._outer._firstItem);
               if (fontToDraw != fm) {
                  graphics.setFont(fontToDraw);
               }

               graphics.drawText(this._outer.formLongVariant(fm), x, y, 0, -1);
               if (fontToDraw != fm) {
                  graphics.setFont(fm);
               }
            } else {
               boolean drawNumbers = (this._outer._type & 8) != 0;
               int i = this._outer._firstItem;

               for (int num_index = 0; i < this._outer._lastItem; num_index++) {
                  if (drawNumbers) {
                     numLength = fm.getBounds(this._outer._numbers[num_index]);
                  }

                  len = this._outer._variantsLengths.elementAt(i) + numLength;
                  this._outer._currentVariant.getVariantAt(i, this._outer._current);
                  int fg = graphics.getColor();
                  int bg = graphics.getBackgroundColor();
                  if (i == this._outer._currentIndex && this._outer._currentVariant.isCurrentVariantHiglighted()) {
                     graphics.setColor(this._outer._backgroundColor);
                     graphics.fillRect(x, y - (fontHeight >> 1), len, this._outer.getBounds().height);
                     graphics.setColor(this._outer._foregroundColor);
                  }

                  if (drawNumbers) {
                     int current = graphics.getColor();
                     graphics.setColor(255);
                     graphics.drawText(this._outer._numbers[num_index], x, y);
                     graphics.setColor(current);
                  }

                  Font fontToDraw = this._outer.transformFontForVariantIndex(fm, i);
                  if (fontToDraw != fm) {
                     graphics.setFont(fontToDraw);
                  }

                  if (this._outer._currentVariant.getOverlapLengthFor(i) > 0) {
                     this.drawOverlaped(graphics, fm, x + numLength, y, this._outer._currentVariant.getOverlapLengthFor(i));
                  } else {
                     graphics.drawText(
                        this._outer._current._variants, this._outer._current._offset, this._outer._current._length, x + numLength, y, this._drawTextParam, null
                     );
                  }

                  if (fontToDraw != fm) {
                     graphics.setFont(fm);
                  }

                  graphics.setColor(fg);
                  graphics.setBackgroundColor(bg);
                  x += len + 5;
                  if (i != this._outer._lastItem) {
                     graphics.drawLine(x - 3, y - 2, x - 3, y + fontHeight);
                  }

                  i++;
               }
            }

            graphics.setFont(this._outer._auxilaryElementsFont);
            x = this._outer.getArrowsStartPosition();
            switch (this._outer._type & 3) {
               case -1:
                  break;
               case 0:
               default:
                  if (this._outer._frameArrows.elementAt(frameIndex * 2) != 0) {
                     graphics.drawText(LookupImpl2._leftArrow, x, y);
                  }

                  if (this._outer._frameArrows.elementAt(frameIndex * 2 + 1) != 0) {
                     graphics.drawText(LookupImpl2._rightArrow, x + 2 + this._outer._typeWidth, y);
                  }
                  break;
               case 1:
                  this._outer._infoString.setLength(0);
                  this._outer
                     ._infoString
                     .append(
                        ((StringBuffer)(new Object()))
                           .append(this._outer._currentIndex + 1)
                           .append("/")
                           .append(this._outer._currentVariant.getVariantsCount())
                           .toString()
                     );
                  graphics.drawText(this._outer._infoString, 0, this._outer._infoString.length(), x, y, 0, -1);
            }

            if (this._outer._rightLabel != null) {
               this._outer.paintRightLabel(graphics, this.getContentWidth(), this.getHeight());
            }
         }
      }
   }

   void setOuter(LookupImpl2 outer) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
