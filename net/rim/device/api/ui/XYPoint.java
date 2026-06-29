package net.rim.device.api.ui;

public final class XYPoint {
   public int x;
   public int y;

   public XYPoint() {
   }

   public XYPoint(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public XYPoint(XYPoint point) {
      this.x = point.x;
      this.y = point.y;
   }

   public final void set(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public final void set(XYPoint point) {
      this.x = point.x;
      this.y = point.y;
   }

   @Override
   public final String toString() {
      return "(" + this.x + ',' + this.y + ')';
   }

   public final void translate(int dx, int dy) {
      this.x += dx;
      this.y += dy;
   }

   public final void translate(XYPoint point) {
      this.x = this.x + point.x;
      this.y = this.y + point.y;
   }
}
