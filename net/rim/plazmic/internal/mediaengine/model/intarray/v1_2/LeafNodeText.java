package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNodeText extends LeafNode {
   int[] _endBaselnPrev;

   LeafNodeText(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      super(model, nodeIdx, meGraphic, context);
      if (context._textBuffer == null) {
         context._textBuffer = (StringBuffer)(new Object());
      }

      this._endBaselnPrev = new int[2];
   }

   @Override
   public void renderImpl() {
      int textIdx = super._model._nodes[super._visualNodeIdx + 24];
      char[] text = (char[])super._model._convertedTextStrings[textIdx];
      int startIndex = super._model._nodes[super._visualNodeIdx + 25];
      int length = text != null ? text.length - startIndex : 0;
      if (super._nextLeaf != null && text != null) {
         int nextStartIndex = super._model._nodes[super._nextLeaf._visualNodeIdx + 25];
         length = nextStartIndex > text.length ? text.length - startIndex : nextStartIndex - startIndex;
      }

      if (length > 0) {
         super._meGraphic.pushContext(super._context);
         super._meGraphic.drawText(text, startIndex, length);
         super._meGraphic.popContext();
      }

      LeafNode nextLeaf = super._nextLeaf;

      while (nextLeaf != null && !super._model.bitsAreSet(nextLeaf._visualNodeIdx, 16)) {
         nextLeaf = nextLeaf._nextLeaf;
      }
   }

   @Override
   public void update() {
      int textIdx = super._model._nodes[super._visualNodeIdx + 24];
      char[] text = (char[])super._model._convertedTextStrings[textIdx];
      if (text != null) {
         int startIndex = super._model._nodes[super._visualNodeIdx + 25];
         int length = text.length - startIndex;
         if (super._nextLeaf != null) {
            int nextStartIndex = super._model._nodes[super._nextLeaf._visualNodeIdx + 25];
            length = nextStartIndex > text.length ? text.length - startIndex : nextStartIndex - startIndex;
         }

         if (length >= 0) {
            super._context._textLength = length;
            super._context._textStartIndex = startIndex;
            super._context._textBuffer.setLength(0);
            super._context._textBuffer.append(text);
         }

         if (this.isBufferEnabled() && super._bufferId == -1) {
            this.computeBoundingBox();
            super._bufferId = super._meGraphic.createNewBuffer(super._boundingBox.width, super._boundingBox.height, false);
         }
      }
   }

   @Override
   public XYRect computeBoundingBox() {
      int textIdx = super._model._nodes[super._visualNodeIdx + 24];
      char[] text = (char[])super._model._convertedTextStrings[textIdx];
      int startIndex = super._model._nodes[super._visualNodeIdx + 25];
      int length = text.length - startIndex;
      if (super._nextLeaf != null) {
         int nextStartIndex = super._model._nodes[super._nextLeaf._visualNodeIdx + 25];
         length = nextStartIndex - startIndex;
      }

      int[] currentTextPos = LeafNode._tempXCoords;
      super._meGraphic.fillTextBounds(text, startIndex, length, super._context, currentTextPos, super._boundingBox);
      if (super._nextLeaf != null) {
         LeafNodeText next = (LeafNodeText)super._nextLeaf;
         next._context.setCurrentTextPosX(currentTextPos[0]);
         next._context.setCurrentTextPosY(currentTextPos[1]);
      }

      return super._boundingBox;
   }
}
