package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.ImageForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNodeImage extends LeafNode {
   LeafNodeImage(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      super(model, nodeIdx, meGraphic, context);
   }

   @Override
   void renderImpl() {
      int imageIdx = super._model._nodes[super._visualNodeIdx + 29];
      Object image = super._model._images[imageIdx];
      if (image instanceof ImageForeignObject) {
         ImageForeignObject fo = (ImageForeignObject)image;
         if (!fo.isReady()) {
            fo.setHandle(super._visualNodeIdx);
            fo.setPosition(Fixed32.toInt(super._model._nodes[super._visualNodeIdx + 10]), Fixed32.toInt(super._model._nodes[super._visualNodeIdx + 11]));
            fo.setExtent(Fixed32.toInt(super._model._nodes[super._visualNodeIdx + 23]), Fixed32.toInt(super._model._nodes[super._visualNodeIdx + 24]));
            fo.setReady();
         }
      }

      super._meGraphic.pushContext(super._context);
      super._meGraphic.setOffset(0, 0);
      super._meGraphic.drawImage(image);
      super._meGraphic.popContext();
   }

   @Override
   public void update() {
      int imageIdx = super._model._nodes[super._visualNodeIdx + 29];
      Object image = super._model._images[imageIdx];
      super._isForeignObject = image instanceof Object;
      if (!super._isForeignObject) {
         super._context._image = super._meGraphic.getBitmapObject(image);
         if (super._context._image == null) {
            super._isVisible = false;
            return;
         }
      } else if (this.isBufferEnabled()) {
         XYRect rectBounds = (XYRect)(new Object());
         super._meGraphic.fillImageBounds(image, rectBounds);
         if (super._bufferId == -1) {
            super._bufferId = super._meGraphic.createNewBuffer(rectBounds.width, rectBounds.height, rectBounds.width < 300);
         }

         super._context._image = super._meGraphic.getBuffer(super._bufferId);
         if (super._context._image == null) {
            super._isVisible = false;
         }
      }
   }

   @Override
   public XYRect computeBoundingBox() {
      int x = Fixed32.toRoundedInt(super._context.getX());
      int y = Fixed32.toRoundedInt(super._context.getY());
      int imageIdx = super._model._nodes[super._visualNodeIdx + 29];
      Object image = super._model._images[imageIdx];
      if (image != null) {
         super._meGraphic.fillImageBounds(image, super._boundingBox);
         super._boundingBox.x = x;
         super._boundingBox.y = y;
         this.computeTransformedBoundingBox();
      } else {
         super._boundingBox.set(0, 0, 0, 0);
      }

      return super._boundingBox;
   }

   @Override
   protected boolean computeTransformedBoundingBox() {
      if (super.computeTransformedBoundingBox()) {
         super._boundingBox.x++;
         super._boundingBox.y++;
         super._boundingBox.width -= 2;
         super._boundingBox.height -= 2;
         return true;
      } else {
         return false;
      }
   }
}
