package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;

public class Border {
   private int _top;
   private int _right;
   private int _bottom;
   private int _left;
   private boolean _transparent;
   private static XYEdges _edges = new XYEdges();

   public Border(int top, int right, int bottom, int left) {
      this._top = top;
      this._right = right;
      this._bottom = bottom;
      this._left = left;
   }

   public Background getBackground() {
      return null;
   }

   public final int getBottom() {
      return this._bottom;
   }

   public final XYEdges getEdges() {
      _edges.set(this._top, this._right, this._bottom, this._left);
      return _edges;
   }

   public final void getEdges(XYEdges edges) {
      edges.set(this._top, this._right, this._bottom, this._left);
   }

   public final int getLeft() {
      return this._left;
   }

   public final int getRight() {
      return this._right;
   }

   public final int getTop() {
      return this._top;
   }

   public final boolean isTransparent() {
      return this._transparent;
   }

   public void paint(Graphics _1, XYRect _2) {
      throw null;
   }

   protected final void setTransparent(boolean transparent) {
      this._transparent = transparent;
   }
}
