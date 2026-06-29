package net.rim.device.api.ui;

public final class XYEdges {
   public int bottom;
   public int left;
   public int right;
   public int top;

   public XYEdges() {
   }

   public XYEdges(int top, int right, int bottom, int left) {
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      this.left = left;
   }

   public final boolean isEmpty() {
      return this.top == 0 && this.right == 0 && this.bottom == 0 && this.left == 0;
   }

   public final void set(int top, int right, int bottom, int left) {
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      this.left = left;
   }
}
