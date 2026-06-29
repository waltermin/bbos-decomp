package net.rim.device.api.ui.chart;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;

public class PointRenderer extends ChartRenderer {
   private boolean _showLines = true;
   private int _lineColor;
   private int _pointSize = 4;
   private int _markerColor;
   private int[] _matrix = new int[9];

   public PointRenderer(ChartField field) {
      super(field);
   }

   @Override
   public int getPreferredWidth() {
      return 2 * this._pointSize * this.getDataset().getSize() + this._pointSize;
   }

   @Override
   public int getPreferredHeight() {
      return this.getRangeAxis().getMax();
   }

   public boolean isLinesDisplayed() {
      return this._showLines;
   }

   @Override
   public void layout(int width, int height) {
      this.setExtent(width, height);
   }

   @Override
   public void paint(Graphics graphics) {
      XYDataset dataset = this.getDataset();
      int rangeMax = this.getRangeAxis().getMax();
      int rangeMin = this.getRangeAxis().getMin();
      int domainMax = this.getDomainAxis().getMax();
      int domainMin = this.getDomainAxis().getMin();
      int scaleX = 65536 * this.getWidth() / (domainMax - domainMin);
      int scaleY = 65536 * this.getHeight() / (rangeMax - rangeMin);
      VecMath.scale(VecMath.IDENTITY_3X3, 0, scaleX, -scaleY, this._matrix);
      VecMath.translate(
         this._matrix,
         0,
         -65536 * this.getWidth() * domainMin / (domainMax - domainMin),
         65536 * this.getHeight() * rangeMax / (rangeMax - rangeMin),
         this._matrix
      );
      graphics.setMatrix(this._matrix);
      if (this._showLines) {
         int[] xArray = dataset.getXArrayInternal();
         int[] yArray = dataset.getYArrayInternal();
         graphics.setColor(this._lineColor);
         graphics.setStrokeWidth(2);
         graphics.drawPathOutline(xArray, yArray, null, null, false);
         graphics.setStrokeWidth(1);
      }

      if (this._pointSize > 0) {
         XYPoint point = new XYPoint();
         int pointCenterTweak = this._pointSize >> 1;
         graphics.setColor(this._markerColor);
         int end = dataset.getSize();

         for (int index = 0; index < end; index++) {
            dataset.getPoint(index, point);
            graphics.fillEllipse32(
               65536 * point.x,
               65536 * point.y,
               65536 * point.x + Fixed32.div(65536 * pointCenterTweak, scaleX),
               65536 * point.y,
               65536 * point.x,
               65536 * point.y + Fixed32.div(65536 * pointCenterTweak, scaleY),
               0,
               360
            );
         }
      }

      graphics.setMatrix(VecMath.IDENTITY_3X3);
   }

   public void setLineColor(int color) {
      this._lineColor = color;
   }

   public void setLinesDisplayed(boolean showLines) {
      if (this._showLines != showLines) {
         this._showLines = showLines;
      }
   }

   public void setMarkerColor(int color) {
      this._markerColor = color;
   }

   public void setPointSize(int pointSize) {
      if (this._pointSize != pointSize) {
         if (pointSize < 0) {
            throw new IllegalArgumentException();
         }

         this._pointSize = pointSize;
      }
   }
}
