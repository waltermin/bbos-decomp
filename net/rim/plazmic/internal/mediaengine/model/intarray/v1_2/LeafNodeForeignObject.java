package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNodeForeignObject extends LeafNode {
   LeafNodeForeignObject(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      super(model, nodeIdx, meGraphic, context);
   }

   @Override
   void renderImpl() {
      int foIdx = super._model._nodes[super._visualNodeIdx + 29];
      if (foIdx != -1) {
         ForeignObject fo = super._model._foreignObjects[foIdx];
         if (fo != null) {
            super._meGraphic.pushContext(super._context);
            super._meGraphic.setOffset(0, 0);
            super._meGraphic.drawForeignObject(fo);
            super._meGraphic.popContext();
         }
      }
   }

   @Override
   public void update() {
      if (this.isBufferEnabled() && super._bufferId == -1) {
         super._bufferId = super._meGraphic
            .createNewBuffer(Fixed32.toRoundedInt(super._context.getWidth()), Fixed32.toRoundedInt(super._context.getHeight()), false);
      }
   }

   @Override
   public XYRect computeBoundingBox() {
      super._boundingBox
         .set(
            Fixed32.toRoundedInt(super._context.getX()),
            Fixed32.toRoundedInt(super._context.getY()),
            Fixed32.toRoundedInt(super._context.getWidth()),
            Fixed32.toRoundedInt(super._context.getHeight())
         );
      this.computeTransformedBoundingBox();
      return super._boundingBox;
   }
}
