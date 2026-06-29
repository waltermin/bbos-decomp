package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNodeRect extends LeafNode {
   int[] _xPts = new int[5];
   int[] _yPts = new int[5];

   LeafNodeRect(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      super(model, nodeIdx, meGraphic, context);
   }

   @Override
   void renderImpl() {
      int x = super._model._nodes[super._visualNodeIdx + 10];
      int y = super._model._nodes[super._visualNodeIdx + 11];
      int width = super._model._nodes[super._visualNodeIdx + 23];
      int height = super._model._nodes[super._visualNodeIdx + 24];
      super._meGraphic.pushContext(super._context);
      super._meGraphic.drawRect(x, y, width, height);
      super._meGraphic.popContext();
   }

   @Override
   public void update() {
      int x = super._model._nodes[super._visualNodeIdx + 10];
      int y = super._model._nodes[super._visualNodeIdx + 11];
      int width = super._model._nodes[super._visualNodeIdx + 23];
      int height = super._model._nodes[super._visualNodeIdx + 24];
      if (width < 0) {
         width = 0;
      }

      if (height < 0) {
         height = 0;
      }

      this._xPts[0] = x;
      this._yPts[0] = y;
      this._xPts[1] = x + width;
      this._yPts[1] = y;
      this._xPts[2] = x + width;
      this._yPts[2] = y + height;
      this._xPts[3] = x;
      this._yPts[3] = y + height;
      this._xPts[4] = x;
      this._yPts[4] = y;
      super._context._coordsX = this._xPts;
      super._context._coordsY = this._yPts;
      super._context._coordsType = null;
      super._context._coordsOffset = null;
      super._context._coordsColour = null;
   }

   @Override
   public XYRect computeBoundingBox() {
      int strokeWidth = Fixed32.toRoundedInt(super._context.getStrokeWidth() << 2);
      int halfStrokeWidth = Fixed32.toRoundedInt(super._context.getStrokeWidth() << 1);
      int x = Fixed32.toRoundedInt(super._model._nodes[super._visualNodeIdx + 10]) - halfStrokeWidth;
      int y = Fixed32.toRoundedInt(super._model._nodes[super._visualNodeIdx + 11]) - halfStrokeWidth;
      int width = Fixed32.toRoundedInt(super._model._nodes[super._visualNodeIdx + 23]);
      int height = Fixed32.toRoundedInt(super._model._nodes[super._visualNodeIdx + 24]);
      width = width > 0 ? width + strokeWidth : strokeWidth;
      height = height > 0 ? height + strokeWidth : strokeWidth;
      super._boundingBox.set(x, y, width, height);
      this.computeTransformedBoundingBox();
      return super._boundingBox;
   }
}
