package net.rim.device.api.ui;

public final class XYDimension {
   public int width;
   public int height;

   public XYDimension() {
   }

   public XYDimension(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public XYDimension(XYDimension dimension) {
      this.width = dimension.width;
      this.height = dimension.height;
   }

   public final void set(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public final void set(XYDimension dimension) {
      this.width = dimension.width;
      this.height = dimension.height;
   }

   @Override
   public final String toString() {
      return "+(" + this.width + ',' + this.height + ')';
   }
}
