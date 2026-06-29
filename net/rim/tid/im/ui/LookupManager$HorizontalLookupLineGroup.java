package net.rim.tid.im.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.ByteVector;
import net.rim.device.api.util.IntVector;

class LookupManager$HorizontalLookupLineGroup extends LookupManager$LookupLineGroup {
   private ByteVector _frameOffsets;
   private ByteVector _frameArrows;
   private StringBuffer _infoString;
   private ByteVector _indexesOfFramesWithOneVariant;
   private IntVector _variantsLengths;
   private byte _leftLabelWidth;
   private byte _rightLabelWidth;
   private String _leftLabel;
   private String _rightLabel;
   private byte _firstVariantLength;
   private FontMetrics _metrics;
   private int _indentTop;
   private final LookupManager this$0;

   public LookupManager$HorizontalLookupLineGroup(LookupManager _1, boolean isAdditionalVariants) {
      super(_1, isAdditionalVariants);
      this.this$0 = _1;
      this._indexesOfFramesWithOneVariant = (ByteVector)(new Object(1));
      this._variantsLengths = (IntVector)(new Object(10, 3));
      this._metrics = (FontMetrics)(new Object());
   }

   @Override
   public void init() {
      this._frameOffsets = (ByteVector)(new Object(10, 2));
      switch (this.this$0._type & 3) {
         case 0:
            this._frameArrows = (ByteVector)(new Object(10, 2));
         case -1:
            return;
         case 1:
         default:
            this._infoString = (StringBuffer)(new Object(5));
      }
   }

   @Override
   public void reset() {
      if (this._frameOffsets == null) {
         this.init();
      }

      this.this$0._currentIndex = 0;
      super._firstItem = -1;
      super._lastItem = -1;
      this._indexesOfFramesWithOneVariant.setSize(0);
      this._frameOffsets.setSize(0);
      this._variantsLengths.setSize(0);
      if (this._frameArrows != null) {
         this._frameArrows.setSize(0);
      }
   }

   @Override
   public void listOfVariantsChanged() {
      super._lastItem = super._firstItem = -1;
      if (this._frameOffsets == null) {
         this.init();
      }

      this._frameOffsets.setSize(0);
      this._variantsLengths.setSize(0);
      this._indexesOfFramesWithOneVariant.setSize(0);
      if (this._frameArrows != null) {
         this._frameArrows.setSize(0);
      }

      this.computeIndexes();
   }

   private int calcFrames(Font aFont) {
      int size = this._frameOffsets.size();
      int frameIndex = this.getFrameFor();
      if (frameIndex >= 0) {
         super._firstItem = this._frameOffsets.elementAt(frameIndex) & 255;
         super._lastItem = this._frameOffsets.elementAt(frameIndex + 1) & 255;
         return frameIndex >> 1;
      }

      int width = this.getAvailableWidth();
      if (width <= 0) {
         return -1;
      }

      int last = size > 0 ? this._frameOffsets.elementAt(size - 1) & 0xFF : super._startIndex;

      do {
         last = this.calculateNextFrame(aFont, last, width, (this.this$0._type & 8) != 0);
      } while (last != -1);

      return this._frameOffsets.size() / 2 - 1;
   }

   private int calculateNextFrame(Font aFont, int aStartIndex, int aWidth, boolean aDrawNumbers) {
      int currentIndex = this.this$0._currentIndex >= super._startIndex && this.this$0._currentIndex < super._endIndex
         ? this.this$0._currentIndex
         : super._startIndex;
      if (aStartIndex > currentIndex) {
         return -1;
      }

      int indexInFrame = 0;
      int flag = 0;
      int index = aStartIndex;

      do {
         if (aDrawNumbers) {
            aWidth -= aFont.getBounds(this.this$0._numbers[indexInFrame]);
         }

         this.this$0._currentVariant.getVariantAt(index, this.this$0._current);
         Font fontToMeasure = this.this$0.transformFontForVariantIndex(aFont, index);
         int current_element_length = fontToMeasure.getBounds(this.this$0._current._variants, this.this$0._current._offset, this.this$0._current._length);
         if (current_element_length + 5 > aWidth) {
            break;
         }

         if (index == super._startIndex && indexInFrame == 0) {
            this._firstVariantLength = (byte)(current_element_length + 5);
         }

         flag++;
         this._variantsLengths.addElement(current_element_length);
         index++;
         indexInFrame++;
         aWidth -= current_element_length + 5;
      } while (index < super._endIndex && indexInFrame < 10);

      if (flag == 0) {
         index++;
         indexInFrame++;
         this._variantsLengths.addElement(0);
         this._indexesOfFramesWithOneVariant.addElement((byte)aStartIndex);
      }

      this._frameOffsets.addElement((byte)aStartIndex);
      this._frameOffsets.addElement((byte)index);
      super._firstItem = aStartIndex;
      super._lastItem = index;
      if (this._frameArrows != null) {
         this._frameArrows.addElement((byte)(super._firstItem == super._startIndex ? 0 : 1));
         this._frameArrows.addElement((byte)(super._lastItem == super._endIndex ? 0 : 1));
      }

      return index;
   }

   private int getFrameFor() {
      int len = this._frameOffsets.size();
      if (this.this$0._currentIndex >= super._startIndex && this.this$0._currentIndex < super._endIndex) {
         int res = -1;

         for (int i = 0; i < len; i += 2) {
            int start = this._frameOffsets.elementAt(i) & 255;
            int end = this._frameOffsets.elementAt(i + 1) & 255;
            if (this.this$0._currentIndex >= start && this.this$0._currentIndex < end) {
               return i;
            }
         }

         return res;
      } else {
         return len == 0 ? -1 : 0;
      }
   }

   @Override
   public void paint(Graphics graphics, Font fm, DrawTextParam param, int y) {
      int frameIndex = this.calcFrames(fm);
      if (frameIndex >= 0) {
         int x = 0;
         int textY = y + this._indentTop;
         int fontHeight = fm.getHeight();
         if (this._leftLabel != null && !super._isAdditionalVariants) {
            x += this.paintLeftLabel(graphics, x, y);
            graphics.setFont(fm);
         }

         int len = 0;
         int numLength = 0;
         boolean isOutOfFocus = this.this$0._currentIndex < super._startIndex || this.this$0._currentIndex >= super._endIndex;
         if (this._indexesOfFramesWithOneVariant.contains((byte)frameIndex)) {
            Font fontToDraw = this.this$0.transformFontForVariantIndex(fm, super._firstItem);
            if (fontToDraw != fm) {
               graphics.setFont(fontToDraw);
            }

            graphics.drawText(this.formLongVariant(fm), x, textY, 0, -1);
            if (fontToDraw != fm) {
               graphics.setFont(fm);
            }
         } else {
            boolean drawNumbers = (this.this$0._type & 8) != 0;
            int firstItem;
            int lastItem;
            if (isOutOfFocus) {
               firstItem = super._startIndex;
               lastItem = super._startIndex + 1;
            } else {
               firstItem = super._firstItem;
               lastItem = super._lastItem;
            }

            int i = firstItem;

            for (int num_index = 0; i < lastItem; num_index++) {
               if (drawNumbers) {
                  numLength = fm.getBounds(this.this$0._numbers[num_index]);
               }

               len = this._variantsLengths.elementAt(i - super._startIndex) + numLength;
               this.this$0._currentVariant.getVariantAt(i, this.this$0._current);
               int fg = graphics.getColor();
               int bg = graphics.getBackgroundColor();
               if (i == this.this$0._currentIndex && this.this$0._currentVariant.isCurrentVariantHiglighted()) {
                  graphics.setColor(this.this$0._backgroundColor);
                  graphics.fillRect(x, y, len + 1, fm.getHeight() + this._indentTop);
                  graphics.setColor(this.this$0._foregroundColor);
               }

               if (drawNumbers) {
                  int current = graphics.getColor();
                  graphics.setColor(255);
                  graphics.drawText(this.this$0._numbers[num_index], x, textY);
                  graphics.setColor(current);
               }

               Font fontToDraw = this.this$0.transformFontForVariantIndex(fm, i);
               if (fontToDraw != fm) {
                  graphics.setFont(fontToDraw);
               }

               if (this.this$0._currentVariant.getOverlapLengthFor(i) > 0) {
                  this.drawOverlaped(graphics, fm, x + numLength, textY, this.this$0._currentVariant.getOverlapLengthFor(i), param);
               } else {
                  graphics.drawText(
                     this.this$0._current._variants, this.this$0._current._offset, this.this$0._current._length, x + numLength, textY, param, null
                  );
               }

               if (fontToDraw != fm) {
                  graphics.setFont(fm);
               }

               graphics.setColor(fg);
               graphics.setBackgroundColor(bg);
               x += len + 5;
               if (i != lastItem) {
                  graphics.drawLine(x - 3, y + this._indentTop, x - 3, y + fontHeight);
               }

               i++;
            }
         }

         graphics.setFont(this.this$0._auxilaryElementsFont);
         x = this.getArrowsStartPosition();
         switch (this.this$0._type & 3) {
            case -1:
               break;
            case 0:
            default:
               if (this._frameArrows.elementAt(frameIndex * 2) != 0 && !isOutOfFocus) {
                  graphics.drawText(LookupManager._leftArrow, x, textY);
               }

               if (!isOutOfFocus) {
                  x += super._typeWidth + 2;
               }

               if (this._frameArrows.elementAt(frameIndex * 2 + 1) != 0 || isOutOfFocus) {
                  graphics.drawText(LookupManager._rightArrow, x, textY);
               }
               break;
            case 1:
               this._infoString.setLength(0);
               this._infoString
                  .append(
                     ((StringBuffer)(new Object()))
                        .append(this.this$0._currentIndex + 1)
                        .append("/")
                        .append(this.this$0._currentVariant.getVariantsCount())
                        .toString()
                  );
               graphics.drawText(this._infoString, 0, this._infoString.length(), x, textY, 0, -1);
         }

         if (this._rightLabel != null && !super._isAdditionalVariants) {
            this.paintRightLabel(graphics, y, this.this$0.getWidth(fm), this.getHeight(fm), fm);
         }

         graphics.setFont(fm);
      }
   }

   private void drawOverlaped(Graphics graphics, Font font, int x, int y, int overlapLength, DrawTextParam param) {
      int fg = graphics.getColor();
      graphics.setColor(8421504);
      graphics.drawText(this.this$0._current._variants, this.this$0._current._offset, overlapLength, x, y, param, null);
      graphics.setColor(fg);
      x += font.getBounds(this.this$0._current._variants, this.this$0._current._offset, overlapLength);
      graphics.drawText(
         this.this$0._current._variants, this.this$0._current._offset + overlapLength, this.this$0._current._length - overlapLength, x, y, param, null
      );
   }

   @Override
   public int getHeight(Font font) {
      if (super._startIndex == super._endIndex) {
         return 0;
      }

      font.getMetrics(this._metrics);
      this._indentTop = this._metrics.iMaxAscent - this._metrics.iAscent;
      return this._metrics.iMaxAscent + this._metrics.iMaxDescent + this._metrics.iLeadingAbove + this._metrics.iLeadingBelow;
   }

   @Override
   public int getWidth(Font font) {
      if (this.this$0._currentIndex >= super._startIndex && this.this$0._currentIndex < super._endIndex && (super._style & 2) == 0) {
         int totalPadding = this.this$0._delegator.getBorderLeft()
            + this.this$0._delegator.getBorderRight()
            + this.this$0._delegator.getPaddingLeft()
            + this.this$0._delegator.getPaddingRight();
         return Display.getWidth() - totalPadding;
      } else {
         return this._firstVariantLength + super._typeWidth + 2 + this._rightLabelWidth + 6 + 4;
      }
   }

   @Override
   public void setLabels(String leftLabel, String rightLabel) {
      this._leftLabel = leftLabel;
      this._rightLabel = rightLabel;
      if (this.this$0._composedWidth != 0) {
         this.calculateLabels();
      }
   }

   private int getAvailableWidth() {
      int adjustment = 0;
      if (this._leftLabelWidth != 0 && !super._isAdditionalVariants) {
         adjustment += this._leftLabelWidth + 4;
      }

      if (this._rightLabelWidth != 0 && !super._isAdditionalVariants) {
         adjustment += this._rightLabelWidth + 4;
      }

      switch (this.this$0._type & 3) {
         case -1:
            break;
         case 0:
            adjustment += 2 * (super._typeWidth + 2);
            break;
         case 1:
         default:
            adjustment += super._typeWidth + 2;
      }

      int totalPadding = this.this$0._delegator.getBorderLeft()
         + this.this$0._delegator.getBorderRight()
         + this.this$0._delegator.getPaddingLeft()
         + this.this$0._delegator.getPaddingRight();
      return Display.getWidth() - totalPadding - adjustment;
   }

   private int getArrowsStartPosition() {
      int adjustment = 0;
      boolean isOutOfFocus = this.this$0._currentIndex < super._startIndex || this.this$0._currentIndex >= super._endIndex;
      if (this._rightLabelWidth != 0 && !super._isAdditionalVariants) {
         adjustment += this._rightLabelWidth + 10;
      }

      switch (this.this$0._type & 3) {
         case -1:
            break;
         case 0:
         default:
            adjustment += super._typeWidth + 2;
            if (!isOutOfFocus) {
               adjustment += super._typeWidth + 2;
            }
            break;
         case 1:
            adjustment += super._typeWidth;
      }

      return this.this$0.getContentWidth() - adjustment;
   }

   private String formLongVariant(Font aFont) {
      this.this$0._currentVariant.getVariantAt(this.this$0._currentIndex, this.this$0._current);
      StringBuffer buffer = (StringBuffer)(new Object(" ..."));
      if ((this.this$0._type & 8) != 0) {
         buffer.insert(0, "1");
      }

      int width = this.getAvailableWidth();

      for (int i = 0; i < this.this$0._current._length; i++) {
         buffer.insert(i, this.this$0._current._variants[this.this$0._current._offset + i]);
         if (aFont.getBounds(buffer) > width) {
            buffer.deleteCharAt(i);
            break;
         }
      }

      return buffer.toString();
   }

   @Override
   public void layout(Font aFont, boolean firstLayout, int allowedHeight) {
      if (firstLayout) {
         this.calculateLabels();
         switch (this.this$0._type & 3) {
            case -1:
               break;
            case 0:
            default:
               super._typeWidth = (byte)this.this$0._auxilaryElementsFont.getBounds(LookupManager._leftArrow);
               break;
            case 1:
               super._typeWidth = (byte)this.this$0._auxilaryElementsFont.getBounds(this.this$0._currentVariant.getVariantsCount() > 9 ? "20/20" : "0/0");
         }

         this.calcFrames(aFont);
      }
   }

   private void calculateLabels() {
      if (this._leftLabel != null) {
         this._leftLabelWidth = (byte)this.this$0._auxilaryElementsFont.getBounds(this._leftLabel);
      }

      if (this._rightLabel != null) {
         this.this$0._auxilaryElementsFont.measureText(this._rightLabel, 0, this._rightLabel.length(), null, this.this$0._textMetrics);
         this._rightLabelWidth = (byte)(this.this$0._textMetrics.iBoundsBrX - this.this$0._textMetrics.iBoundsTlX);
      }
   }

   private int paintLeftLabel(Graphics graphics, int x, int y) {
      graphics.setFont(this.this$0._auxilaryElementsFont);
      graphics.drawText(this._leftLabel, 0, this._leftLabel.length(), x, y, 0, -1);
      return this._leftLabelWidth + 2;
   }

   private void paintRightLabel(Graphics graphics, int y, int width, int height, Font fm) {
      int fg = graphics.getColor();
      int bg = graphics.getBackgroundColor();
      int x = width - (this._rightLabelWidth + 4);
      graphics.setFont(this.this$0._auxilaryElementsFont);
      graphics.setColor(this.this$0._backgroundColor);
      graphics.fillRoundRect(x - 2, y, this._rightLabelWidth + 6, height, height / 3, height / 3);
      graphics.setColor(this.this$0._foregroundColor);
      graphics.drawText(this._rightLabel, x, y + (height + 1 - this.this$0._auxilaryElementsFont.getHeight()) / 2);
      graphics.setColor(fg);
      graphics.setBackgroundColor(bg);
   }

   @Override
   public boolean handleElementTraverse(int action) {
      switch (action) {
         case -110:
            return this.this$0._isPositionAboveComposedText;
         case -109:
            if (!this.this$0._isPositionAboveComposedText) {
               return true;
            }

            return false;
         case 31:
            this.this$0.nextElement();
            return true;
         case 32:
            this.this$0.prevElement();
            return true;
         default:
            return false;
      }
   }

   @Override
   public int getVerticalElementCount() {
      return 1;
   }

   @Override
   public int getMaxVerticalElementsPerFrame() {
      return 1;
   }
}
