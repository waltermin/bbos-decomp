package net.rim.device.api.ui.chart;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;

public class PieRenderer extends ChartRenderer {
   private int _slicePadding;
   private int _valuesTotal;
   public static final int PADDING_NONE = 0;
   public static final int PADDING_LOW = 4;
   public static final int PADDING_MEDIUM = 8;
   public static final int PADDING_HIGH = 12;

   public PieRenderer(ChartField field) {
      super(field);
   }

   public int getSlicePadding() {
      return this._slicePadding;
   }

   @Override
   public void layout(int width, int height) {
      this.setExtent(width, height);
   }

   @Override
   public void paint(Graphics graphics) {
      XYDataset dataset = this.getDataset();
      int currentAngle = 0;
      int lastAngle = 0;
      int numSlices = dataset.getSize();
      XYPoint point = new XYPoint();
      int width = this.getWidth();
      int height = this.getHeight();
      this._valuesTotal = 0;

      for (int index = 0; index < numSlices; index++) {
         this._valuesTotal = this._valuesTotal + dataset.getY(index);
      }

      for (int index = 0; index < numSlices; index++) {
         dataset.getPoint(index, point);
         int color = point.x;
         int value = point.y;
         graphics.setColor(color);
         currentAngle = value * 360 / this._valuesTotal - this._slicePadding;
         if (currentAngle != 0) {
            graphics.fillArc(0, 0, width, height, lastAngle, currentAngle);
         }

         graphics.setColor(0);
         graphics.drawArc(0, 0, width, height, lastAngle, currentAngle);
         lastAngle += currentAngle + this._slicePadding;
      }
   }

   public void setSlicePadding(int padding) {
      if (this._slicePadding != padding) {
         checkSlicePadding(padding);
         this._slicePadding = padding;
      }
   }

   private static void checkSlicePadding(int padding) {
      switch (padding) {
         case 0:
         case 4:
         case 8:
         case 12:
            return;
         default:
            throw new IllegalArgumentException();
      }
   }
}
