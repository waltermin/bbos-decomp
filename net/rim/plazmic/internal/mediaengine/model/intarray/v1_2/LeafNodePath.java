package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNodePath extends LeafNode {
   private XYRect _baseBoundingBox = null;

   LeafNodePath(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      super(model, nodeIdx, meGraphic, context);
   }

   @Override
   void renderImpl() {
      int xCoordsIdx = super._model._nodes[super._visualNodeIdx + 23];
      int yCoordsIdx = super._model._nodes[super._visualNodeIdx + 24];
      int offsetsIdx = super._model._nodes[super._visualNodeIdx + 25];
      int pointTypesIdx = super._model._nodes[super._visualNodeIdx + 26];
      int[] xCoords = super._model._coords[xCoordsIdx];
      int[] finalXCoords = super._model._finalCoords[xCoordsIdx];
      int[] yCoords = super._model._coords[yCoordsIdx];
      int[] finalYCoords = super._model._finalCoords[yCoordsIdx];
      int[] offsets = null;
      byte[] pointTypes = null;
      if (offsetsIdx > -1) {
         offsets = super._model._coords[offsetsIdx];
      }

      if (pointTypesIdx > -1) {
         pointTypes = super._model._pointTypes[pointTypesIdx];
      }

      super._meGraphic.pushContext(super._context);
      super._meGraphic.drawPath(xCoords, yCoords, finalXCoords, finalYCoords, pointTypes, offsets, false);
      super._meGraphic.popContext();
   }

   @Override
   public void update() {
      int xCoordsIdx = super._model._nodes[super._visualNodeIdx + 23];
      int yCoordsIdx = super._model._nodes[super._visualNodeIdx + 24];
      int offsetsIdx = super._model._nodes[super._visualNodeIdx + 25];
      int pointTypesIdx = super._model._nodes[super._visualNodeIdx + 26];
      int[] xCoords = super._model._coords[xCoordsIdx];
      int[] finalXCoords = super._model._finalCoords[xCoordsIdx];
      int[] yCoords = super._model._coords[yCoordsIdx];
      int[] finalYCoords = super._model._finalCoords[yCoordsIdx];
      int[] offsets = null;
      byte[] pointTypes = null;
      if (offsetsIdx > -1) {
         offsets = super._model._coords[offsetsIdx];
      }

      if (pointTypesIdx > -1) {
         pointTypes = super._model._pointTypes[pointTypesIdx];
      }

      super._context._coordsX = xCoords;
      super._context._coordsY = yCoords;
      super._context._coordsOffset = offsets;
      super._context._coordsType = pointTypes;
      super._context._finalCoordsX = finalXCoords;
      super._context._finalCoordsY = finalYCoords;
   }

   @Override
   public XYRect computeBoundingBox() {
      if (this._baseBoundingBox == null) {
         int xCoordsIdx = super._model._nodes[super._visualNodeIdx + 23];
         int yCoordsIdx = super._model._nodes[super._visualNodeIdx + 24];
         int[] xCoords = super._model._coords[xCoordsIdx];
         int[] yCoords = super._model._coords[yCoordsIdx];
         int nPts = xCoords.length;
         int minX;
         int minY;
         int maxX;
         int maxY;
         if (nPts > 0) {
            minX = maxX = Fixed32.toRoundedInt(xCoords[0]);
            minY = maxY = Fixed32.toRoundedInt(yCoords[0]);

            for (int iPt = 1; iPt < nPts; iPt++) {
               int x = Fixed32.toRoundedInt(xCoords[iPt]);
               int y = Fixed32.toRoundedInt(yCoords[iPt]);
               if (x < minX) {
                  minX = x;
               } else if (x > maxX) {
                  maxX = x;
               }

               if (y < minY) {
                  minY = y;
               } else if (y > maxY) {
                  maxY = y;
               }
            }
         } else {
            maxY = 0;
            minY = 0;
            maxX = 0;
            minX = 0;
         }

         this._baseBoundingBox = new XYRect(minX, minY, maxX - minX, maxY - minY);
      }

      int mitreLengthLimit = 4;
      int strokeWidth = Fixed32.toRoundedInt(super._context.getStrokeWidth()) * mitreLengthLimit;
      int halfStrokeWidth = strokeWidth >> 1;
      super._boundingBox
         .set(
            this._baseBoundingBox.x - halfStrokeWidth,
            this._baseBoundingBox.y - halfStrokeWidth,
            this._baseBoundingBox.width + strokeWidth,
            this._baseBoundingBox.height + strokeWidth
         );
      this.computeTransformedBoundingBox();
      return super._boundingBox;
   }
}
