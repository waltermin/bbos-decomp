package net.rim.device.api.ui.chart;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class Axis {
   private int _scaleTextWidth;
   private int _scalePadding = 0;
   private int _min;
   private int _max;
   private int _increment = 1;
   private int _edge;
   private int _x;
   private int _y;
   private int _width;
   private int _height;
   private int _majorLength;
   private int _scaleTickSpacing;
   private boolean _showValues = true;
   ChartField _field;
   private static final int AXIS_WIDTH = 2;
   public static final int PADDING_NONE = 0;
   public static final int PADDING_LOW = 12;
   public static final int PADDING_MEDIUM = 18;
   public static final int PADDING_HIGH = 24;
   private static final int X_SCALE_SPACER = 2;
   private static final int Y_SCALE_SPACER = 4;
   private static final int SCALE_TICK_BREADTH = 2;
   private static final int SCALE_TICK_LENGTH = 6;
   public static final int EDGE_TOP = 0;
   public static final int EDGE_RIGHT = 1;
   public static final int EDGE_BOTTOM = 2;
   public static final int EDGE_LEFT = 3;

   public Axis(ChartField field) {
      this._field = field;
   }

   private void calculateScaleAdvance(Font font) {
      int maxAdvance = 0;

      for (int index = this._min + this._min % this._increment; index <= this._max; index += this._increment) {
         int advance = font.getAdvance(Integer.toString(index));
         maxAdvance = Math.max(maxAdvance, advance);
      }

      this._scaleTextWidth = maxAdvance;
   }

   private void calculateScaleTickIndents() {
   }

   private void calculateScaleTickSpacings() {
      if (this._edge != 0 && this._edge != 2) {
         if (this._showValues) {
            this._scaleTickSpacing = this._scaleTextWidth;
         } else {
            this._scaleTickSpacing = this._scalePadding;
         }
      } else if (this._showValues) {
         Font font = this._field.getFont();
         this._scaleTickSpacing = font.getHeight();
      } else {
         this._scaleTickSpacing = this._scalePadding;
      }
   }

   void calculateWidth() {
   }

   public int getHeight() {
      return this._height;
   }

   public int getIncrement() {
      return this._increment;
   }

   public int getLeft() {
      return this._x;
   }

   public int getMax() {
      return this._max;
   }

   public int getMin() {
      return this._min;
   }

   public int getPreferredHeight() {
      this.calculateScaleAdvance(this._field.getFont());
      switch (this._edge) {
         case 0:
         case 2:
            return this._scalePadding + this._field.getFont().getHeight() + 6 + 2 + 2;
         default:
            return 0;
      }
   }

   public int getPreferredWidth() {
      this.calculateScaleAdvance(this._field.getFont());
      switch (this._edge) {
         case 1:
         case 3:
            return this._scalePadding + this._scaleTextWidth + 6 + 2 + 4;
         default:
            return 0;
      }
   }

   public int getScalePadding() {
      return this._scalePadding;
   }

   public int getTop() {
      return this._y;
   }

   public int getWidth() {
      return this._width;
   }

   public boolean isValuesDisplayed() {
      return this._showValues;
   }

   public void layout(int width, int height) {
      this._width = width;
      this._height = height;
      switch (this._edge) {
         case -1:
            return;
         case 0:
         default:
            this._majorLength = width;
            break;
         case 1:
            this._majorLength = height;
            break;
         case 2:
            this._majorLength = width;
            break;
         case 3:
            this._majorLength = height;
      }

      this._scaleTickSpacing = this._majorLength / ((this._max - this._min) / this._increment);
   }

   public void paint(Graphics graphics) {
      int x;
      int y;
      int width;
      int height;
      switch (this._edge) {
         case -1:
            return;
         case 0:
         default:
            x = 0;
            y = this._height - 2;
            width = this._width;
            height = 2;
            break;
         case 1:
            x = 0;
            y = 0;
            width = 2;
            height = this._height;
            break;
         case 2:
            x = 0;
            y = 0;
            width = this._width;
            height = 2;
            break;
         case 3:
            x = this._width - 2;
            y = 0;
            width = 2;
            height = this._height;
      }

      graphics.fillRect(x, y, width, height);
      this.paintScales(graphics);
   }

   private void paintScales(Graphics graphics) {
      int x = 0;
      int y = 0;
      int xIncrement = 0;
      int yIncrement = 0;
      int textYPos = 0;
      int tickWidth;
      int tickHeight;
      int textXPos;
      int drawstyle;
      if (this._edge != 0 && this._edge != 2) {
         if (this._edge == 3) {
            x = this.getWidth() - 6;
            textXPos = -this._scaleTextWidth;
         } else {
            textXPos = 0;
         }

         textYPos = -(this._field.getFont().getHeight() >> 1);
         y = this.getHeight();
         yIncrement = -this._scaleTickSpacing;
         tickWidth = 6;
         tickHeight = 2;
         drawstyle = 5;
      } else {
         xIncrement = this._scaleTickSpacing;
         tickWidth = 2;
         tickHeight = 6;
         textXPos = -(this._scaleTextWidth >> 1);
         if (this._edge == 2) {
            textYPos += 6;
         }

         drawstyle = 4;
      }

      for (int index = this._min + Math.abs(this._min) % this._increment; index <= this._max; index += this._increment) {
         if (this._showValues) {
            graphics.drawText(Integer.toString(index), x + textXPos, y + textYPos, drawstyle, this._scaleTextWidth);
         }

         graphics.fillRect(x, y, tickWidth, tickHeight);
         x += xIncrement;
         y += yIncrement;
      }
   }

   int mapValueToPixel(int value) {
      return this._majorLength - (value - this._min) * this._majorLength / (this._max - this._min);
   }

   void setEdge(int edge) {
      this._edge = edge;
   }

   public void setIncrement(int increment) {
      if (this._increment != increment) {
         if (0 >= increment || increment > this._max - this._min) {
            throw new IllegalArgumentException();
         }

         this._increment = increment;
      }
   }

   public void setMinMax(int min, int max) {
      if (min >= max) {
         throw new IllegalArgumentException();
      }

      this._min = min;
      this._max = max;
   }

   public void setPosition(int x, int y) {
      this._x = x;
      this._y = y;
   }

   public void setScalePadding(int scalePadding) {
      if (this._scalePadding != scalePadding) {
         switch (scalePadding) {
            case 0:
            case 12:
            case 18:
            case 24:
               this._scalePadding = scalePadding;
               break;
            default:
               throw new IllegalArgumentException();
         }
      }
   }

   public void setScaleValuesDisplayed(boolean showValues) {
      if (this._showValues != showValues) {
         this._showValues = showValues;
      }
   }
}
