package net.rim.device.api.ui.chart;

import net.rim.device.api.ui.Graphics;

public class ChartRenderer {
   private ChartField _field;
   private XYDataset _dataset;
   private Axis _domain;
   private Axis _range;
   private int _x;
   private int _y;
   private int _width;
   private int _height;
   private int _preferredWidth;
   private int _preferredHeight;

   protected ChartRenderer(ChartField field) {
      this._field = field;
   }

   public Axis getDomainAxis() {
      return this._domain;
   }

   public ChartField getField() {
      return this._field;
   }

   public int getHeight() {
      return this._height;
   }

   public int getLeft() {
      return this._x;
   }

   public int getPreferredHeight() {
      return this._preferredHeight;
   }

   public int getPreferredWidth() {
      return this._preferredWidth;
   }

   public Axis getRangeAxis() {
      return this._range;
   }

   public int getTop() {
      return this._y;
   }

   public int getWidth() {
      return this._width;
   }

   public void layout(int _1, int _2) {
      throw null;
   }

   public void paint(Graphics _1) {
      throw null;
   }

   public XYDataset getDataset() {
      return this._dataset;
   }

   public void setAxis(Axis domain, Axis range) {
      this._domain = domain;
      this._range = range;
   }

   public void setDataset(XYDataset dataset) {
      this._dataset = dataset;
   }

   protected void setExtent(int width, int height) {
      this._width = width;
      this._height = height;
   }

   public void setPosition(int x, int y) {
      this._x = x;
      this._y = y;
   }

   public void setPreferredHeight(int height) {
      this._preferredHeight = height;
   }

   public void setPreferredWidth(int width) {
      this._preferredWidth = width;
   }
}
