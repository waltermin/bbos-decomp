package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNodeEllipse extends LeafNode {
   int[] _xPts = new int[3];
   int[] _yPts = new int[3];

   LeafNodeEllipse(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      super(model, nodeIdx, meGraphic, context);
   }

   @Override
   void renderImpl() {
      int cx = super._model._nodes[super._visualNodeIdx + 10];
      int cy = super._model._nodes[super._visualNodeIdx + 11];
      int rx = super._model._nodes[super._visualNodeIdx + 23];
      int ry = super._model._nodes[super._visualNodeIdx + 24];
      super._meGraphic.pushContext(super._context);
      super._meGraphic.drawEllipse(cx, cy, rx, ry);
      super._meGraphic.popContext();
   }

   @Override
   public void update() {
      int cx = super._model._nodes[super._visualNodeIdx + 10];
      int cy = super._model._nodes[super._visualNodeIdx + 11];
      int rx = super._model._nodes[super._visualNodeIdx + 23];
      int ry = super._model._nodes[super._visualNodeIdx + 24];
      rx = rx > 32768 ? rx - 32768 : 0;
      ry = ry > 32768 ? ry - 32768 : 0;
      this._xPts[0] = cx;
      this._yPts[0] = cy;
      this._xPts[1] = cx + rx;
      this._yPts[1] = cy;
      this._xPts[2] = cx;
      this._yPts[2] = cy + ry;
      super._context._coordsX = this._xPts;
      super._context._coordsY = this._yPts;
      super._context.setX(cx);
      super._context.setY(cy);
      super._context.setWidth(rx, 0);
      super._context.setHeight(ry, 0);
   }

   @Override
   public XYRect computeBoundingBox() {
      int cx = super._model._nodes[super._visualNodeIdx + 10];
      int cy = super._model._nodes[super._visualNodeIdx + 11];
      int rx = super._model._nodes[super._visualNodeIdx + 23];
      int ry = super._model._nodes[super._visualNodeIdx + 24];
      rx = rx > 32768 ? rx - 32768 : 0;
      ry = ry > 32768 ? ry - 32768 : 0;
      int strokeWidth = super._context.getStrokeWidth();
      int halfStrokeWidth = strokeWidth >> 1;
      int x = Fixed32.toRoundedInt(cx - rx - halfStrokeWidth);
      int y = Fixed32.toRoundedInt(cy - ry - halfStrokeWidth);
      int width = Fixed32.toRoundedInt(2 * rx + strokeWidth);
      int height = Fixed32.toRoundedInt(2 * ry + strokeWidth);
      super._boundingBox.set(x, y, width, height);
      this.computeTransformedBoundingBox();
      return super._boundingBox;
   }
}
