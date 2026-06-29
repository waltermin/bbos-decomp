package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;

class LeafNode {
   int _visualNodeIdx;
   XYRect _boundingBox;
   int _bufferId = -1;
   boolean _renderToBuffer = false;
   boolean _isVisible = false;
   boolean _isForeignObject = false;
   MEGraphics2dContext _context;
   AnimationModel _model;
   PME12Graphics _meGraphic;
   LeafNode _nextLeaf;
   private static final int BBOX_CHANGE_BITS = -1912602624;
   static XYRect _dirtyRect = (XYRect)(new Object(0, 0, 0, 0));
   static XYRect _clippedBox = (XYRect)(new Object(0, 0, 0, 0));
   static boolean _bufferEnabled = false;
   static int[] _tempXCoords = new int[4];
   static int[] _tempYCoords = new int[4];

   LeafNode(AnimationModel model, int nodeIdx, PME12Graphics meGraphic, MEGraphics2dContext context) {
      this._visualNodeIdx = nodeIdx;
      this._model = model;
      this._meGraphic = meGraphic;
      this._context = context;
      this._isVisible = false;
      this._boundingBox = (XYRect)(new Object(0, 0, 0, 0));
      _clippedBox = (XYRect)(new Object(0, 0, 0, 0));
   }

   public void render() {
      XYRect clip = (XYRect)(new Object(this._context.getClip()));
      clip.set(Fixed32.toRoundedInt(clip.x), Fixed32.toRoundedInt(clip.y), Fixed32.toRoundedInt(clip.width), Fixed32.toRoundedInt(clip.height));
      clip.intersect(this._boundingBox);
      if (clip.width != 0 && clip.height != 0) {
         if (this.isBufferEnabled() && this._bufferId != -1) {
            this._meGraphic.applyBuffer(this._bufferId, clip, this._boundingBox.x, this._boundingBox.y);
         } else {
            this.renderImpl();
         }
      }
   }

   public void renderToBuffer() {
      if (this.isBufferEnabled() && this._bufferId != -1) {
         int orgBuffer = this._meGraphic.getActiveBufferId();
         this._meGraphic.switchDrawToBuffer(this._bufferId);
         this._meGraphic.clear(0);
         this.renderImpl();
         this._meGraphic.switchDrawToBuffer(orgBuffer);
      }
   }

   public boolean isBufferEnabled() {
      return this._renderToBuffer && _bufferEnabled;
   }

   void renderImpl() {
      throw null;
   }

   public void update() {
      throw null;
   }

   public void unionWithBoundingBox(XYRect rect, XYRect clip) {
      _clippedBox.set(this._boundingBox);
      if (clip.width != 0 && clip.height != 0) {
         _clippedBox.intersect(clip);
         if (rect.width != 0 && rect.height != 0) {
            if (_clippedBox.width != 0 && _clippedBox.height != 0) {
               rect.union(_clippedBox);
            }
         } else {
            rect.set(_clippedBox);
         }
      }
   }

   public void unionWithDirtyRect(XYRect rect, XYRect clip) {
      _clippedBox.set(_dirtyRect);
      if (clip.width != 0 && clip.height != 0) {
         _clippedBox.intersect(clip);
         if (rect.width != 0 && rect.height != 0) {
            if (_clippedBox.width != 0 && _clippedBox.height != 0) {
               rect.union(_clippedBox);
            }
         } else {
            rect.set(_clippedBox);
         }
      }
   }

   public XYRect computeDirtyRect(XYRect clip) {
      _dirtyRect.set(this._boundingBox);
      if ((this._context.getDirtyOR() & -1912602624) != 0) {
         this.computeBoundingBox();
         this.unionWithBoundingBox(_dirtyRect, clip);
      }

      return _dirtyRect;
   }

   public XYRect computeBoundingBox() {
      throw null;
   }

   protected boolean computeTransformedBoundingBox() {
      if (this._boundingBox.width != 0 && this._boundingBox.height != 0) {
         int[] transformMatrix = this._context.getCurrentTransformMatrix();
         if (!VecMath.isIdentity(transformMatrix, 0)) {
            if (VecMath.isTranslation(transformMatrix, 0)) {
               this._boundingBox.x = this._boundingBox.x + Fixed32.toRoundedInt(transformMatrix[2]);
               this._boundingBox.y = this._boundingBox.y + Fixed32.toRoundedInt(transformMatrix[5]);
            } else {
               _tempXCoords[0] = this._boundingBox.x;
               _tempXCoords[1] = this._boundingBox.x + this._boundingBox.width;
               _tempXCoords[2] = this._boundingBox.x + this._boundingBox.width;
               _tempXCoords[3] = this._boundingBox.x;
               _tempYCoords[0] = this._boundingBox.y;
               _tempYCoords[1] = this._boundingBox.y;
               _tempYCoords[2] = this._boundingBox.y + this._boundingBox.height;
               _tempYCoords[3] = this._boundingBox.y + this._boundingBox.height;
               int[] xformX = new int[4];
               int[] xformY = new int[4];
               VecMath.transformPoints(transformMatrix, 0, _tempXCoords, _tempYCoords, xformX, xformY);
               int maxX;
               int minX = maxX = xformX[0];
               int maxY;
               int minY = maxY = xformY[0];

               for (int i = 1; i < 4; i++) {
                  if (xformX[i] < minX) {
                     minX = xformX[i];
                  }

                  if (xformX[i] > maxX) {
                     maxX = xformX[i];
                  }

                  if (xformY[i] < minY) {
                     minY = xformY[i];
                  }

                  if (xformY[i] > maxY) {
                     maxY = xformY[i];
                  }
               }

               this._boundingBox.set(minX, minY, maxX - minX, maxY - minY);
            }
         }

         this._boundingBox.x--;
         this._boundingBox.y--;
         this._boundingBox.width += 2;
         this._boundingBox.height += 2;
         return true;
      } else {
         return false;
      }
   }

   public boolean intersects(XYRect clip) {
      return this._boundingBox.intersects(clip);
   }
}
