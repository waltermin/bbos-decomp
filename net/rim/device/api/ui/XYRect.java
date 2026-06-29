package net.rim.device.api.ui;

import net.rim.device.api.util.MathUtilities;

public final class XYRect {
   public int x;
   public int y;
   public int width;
   public int height;

   public XYRect() {
   }

   public XYRect(XYPoint topLeft, XYPoint bottomBound) {
      this.x = topLeft.x;
      this.y = topLeft.y;
      this.width = bottomBound.x - this.x;
      this.height = bottomBound.y - this.y;
   }

   public XYRect(XYRect rect) {
      this.x = rect.x;
      this.y = rect.y;
      this.width = rect.width;
      this.height = rect.height;
   }

   public XYRect(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public final void add(XYEdges edges) {
      this.x = this.x - edges.left;
      this.y = this.y - edges.top;
      this.width = this.width + edges.left + edges.right;
      this.height = this.height + edges.top + edges.bottom;
   }

   public final boolean contains(int x, int y, int width, int height) {
      if (this.width > 0 && this.height > 0 && width > 0 && height > 0) {
         return x >= this.x && y >= this.y && x + width <= this.x + this.width && y + height <= this.y + this.height;
      } else {
         return width != 0 && height != 0 ? false : x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
      }
   }

   public final boolean contains(int x, int y) {
      return this.width <= 0 || this.height <= 0 ? false : x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
   }

   public final boolean contains(XYPoint point) {
      return this.contains(point.x, point.y);
   }

   public final boolean contains(XYRect rect) {
      return this.contains(rect.x, rect.y, rect.width, rect.height);
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof XYRect)) {
         return false;
      }

      XYRect rect = (XYRect)obj;
      return this.x == rect.x && this.y == rect.y && this.width == rect.width && this.height == rect.height;
   }

   public final void intersect(int rect_x, int rect_y, int rect_width, int rect_height) {
      int x2 = this.X2();
      int y2 = this.Y2();
      int newX1 = MathUtilities.clamp(this.x, rect_x, x2);
      int newY1 = MathUtilities.clamp(this.y, rect_y, y2);
      int newX2 = MathUtilities.clamp(this.x, rect_x + rect_width, x2);
      int newY2 = MathUtilities.clamp(this.y, rect_y + rect_height, y2);
      this.x = newX1;
      this.y = newY1;
      this.width = newX2 - newX1;
      this.height = newY2 - newY1;
   }

   public final void intersect(XYRect rect) {
      int x2 = this.X2();
      int y2 = this.Y2();
      int newX1 = MathUtilities.clamp(this.x, rect.x, x2);
      int newY1 = MathUtilities.clamp(this.y, rect.y, y2);
      int newX2 = MathUtilities.clamp(this.x, rect.X2(), x2);
      int newY2 = MathUtilities.clamp(this.y, rect.Y2(), y2);
      this.x = newX1;
      this.y = newY1;
      this.width = newX2 - newX1;
      this.height = newY2 - newY1;
   }

   public final boolean isEmpty() {
      return this.width == 0 || this.height == 0;
   }

   public final boolean intersects(int rect_x, int rect_y, int rect_width, int rect_height) {
      return this.x + this.width > rect_x && this.x < rect_x + rect_width && this.y + this.height > rect_y && this.y < rect_y + rect_height;
   }

   public final boolean intersects(XYRect rect) {
      return this.x + this.width > rect.x && this.x < rect.X2() && this.y + this.height > rect.y && this.y < rect.Y2();
   }

   public final void set(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public final void set(XYRect rect) {
      this.x = rect.x;
      this.y = rect.y;
      this.width = rect.width;
      this.height = rect.height;
   }

   public final void setLocation(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public final void setLocation(XYPoint topLeft) {
      this.x = topLeft.x;
      this.y = topLeft.y;
   }

   public final void setSize(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public final void subtract(XYEdges edges) {
      this.x = this.x + edges.left;
      this.y = this.y + edges.top;
      this.width = this.width - (edges.left + edges.right);
      this.height = this.height - (edges.top + edges.bottom);
   }

   public final void translate(int horizontal, int vertical) {
      this.x += horizontal;
      this.y += vertical;
   }

   public final void translate(XYPoint point) {
      this.translate(point.x, point.y);
   }

   public final void union(int ox, int oy, int owidth, int oheight) {
      if (this.width > 0 && this.height > 0) {
         if (ox + owidth > this.x + this.width) {
            this.width = this.width + (ox + owidth - (this.x + this.width));
         }

         if (oy + oheight > this.y + this.height) {
            this.height = this.height + (oy + oheight - (this.y + this.height));
         }

         if (ox < this.x) {
            this.width = this.width + (this.x - ox);
            this.x = ox;
         }

         if (oy < this.y) {
            this.height = this.height + (this.y - oy);
            this.y = oy;
            return;
         }
      } else {
         this.set(ox, oy, owidth, oheight);
      }
   }

   public final void union(XYRect rect) {
      this.union(rect.x, rect.y, rect.width, rect.height);
   }

   public final void unionNoEmpty(XYRect rect) {
      this.unionNoEmpty(rect.x, rect.y, rect.width, rect.height);
   }

   public final void unionNoEmpty(int x, int y, int width, int height) {
      if (this.isEmpty()) {
         this.set(x, y, width, height);
      } else {
         if (width > 0 || height > 0) {
            this.union(x, y, width, height);
         }
      }
   }

   public final int X2() {
      return this.x + this.width;
   }

   public final int Y2() {
      return this.y + this.height;
   }
}
