package net.rim.device.api.ui.chart;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.util.MathUtilities;

public class BarRenderer extends ChartRenderer {
   private int _barWidth = 20;
   private int _barPadding = 5;
   private boolean _displayBarValues;
   private int _fillColor;
   private int _strokeColor;

   public BarRenderer(ChartField field) {
      super(field);
   }

   @Override
   public int getPreferredWidth() {
      return (this._barWidth + this._barPadding) * this.getDataset().getSize() + this._barPadding;
   }

   @Override
   public int getPreferredHeight() {
      return this.getRangeAxis().getMax();
   }

   @Override
   public void layout(int width, int height) {
      this.setExtent(width, height);
   }

   @Override
   public void paint(Graphics graphics) {
      XYDataset dataset = this.getDataset();
      int barSpacing = this._barWidth + this._barPadding;
      int initialColour = graphics.getColor();
      Font font = this.getField().getFont();
      int fontHeight = font.getHeight();
      XYPoint point = new XYPoint();
      int axisMax = this.getRangeAxis().getMax();
      int axisMin = this.getRangeAxis().getMin();
      int scale = axisMax - axisMin;
      int base = MathUtilities.clamp(-1, axisMin, 1) == MathUtilities.clamp(-1, axisMax, 1) ? axisMin : 0;
      int zeroLine = base == 0 ? axisMax : -axisMin;
      int last = dataset.getSize();
      int index = 0;

      for (int x = 0; index < last; x += barSpacing) {
         dataset.getPoint(index, point);
         int value = point.y;
         int barHeight = (value - base) * this.getHeight() / scale;
         int y;
         if (barHeight < 0) {
            y = zeroLine;
            barHeight = -barHeight;
         } else {
            y = zeroLine - barHeight;
         }

         graphics.setColor(this._fillColor);
         graphics.fillRect(x, y, this._barWidth, barHeight);
         graphics.setColor(this._strokeColor);
         graphics.drawRect(x, y, this._barWidth, barHeight);
         if (this._displayBarValues) {
            String valueString = Integer.toString(value);
            if (value < 0) {
               y += barHeight;
            } else {
               y -= fontHeight;
            }

            graphics.setColor(initialColour);
            graphics.drawText(valueString, x, y, 4, this._barWidth);
         }

         index++;
      }
   }

   public void setDisplayBarValues(boolean displayBarValues) {
      this._displayBarValues = displayBarValues;
   }

   public void setFillColor(int color) {
      this._fillColor = color;
   }

   public void setStrokeColor(int color) {
      this._strokeColor = color;
   }
}
