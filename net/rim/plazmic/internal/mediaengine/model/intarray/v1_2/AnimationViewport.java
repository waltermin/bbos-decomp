package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.MediaViewport;
import net.rim.plazmic.internal.mediaengine.ui.LayoutManager;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dContext;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;
import net.rim.plazmic.internal.mediaengine.ui.Pannable;
import net.rim.plazmic.internal.mediaengine.ui.Zoomable;
import net.rim.plazmic.internal.mediaengine.util.Platform;

public final class AnimationViewport implements MediaViewport, Pannable, Zoomable {
   private AnimationModel _model;
   private int _style;
   private int _zoomFactor;
   private int _zoomOriginX = 0;
   private int _zoomOriginY = 0;
   private int _panXAmount;
   private int _panXDelta;
   private int _panYAmount;
   private int _panYDelta;
   private int _alignX;
   private int _alignY;
   private final int[] _alignmentMatrix;
   private final int[] _rootSVGMatrix;
   private Platform _platform;
   private PME12Graphics _meGraphic;
   private MEGraphics2dContext _baseContext;
   private MEGraphics2dContext _backgroundContext;
   protected MediaServices _services;
   private MEGraphics2dContext[] _contextList;
   private int[] _contextRenderIdx;
   private MEGraphics2dContext[] _leafContextList;
   private byte _dirty;
   private boolean _forceInvalidate;
   private int _virtualWidth;
   private int _virtualHeight;
   private LeafNode[] _renderLeafs;
   private int _numRenderLeafs;
   private XYRect _dirtyRect;
   private XYRect _oldDirtyRect;
   private XYRect _baseClip;
   private XYRect _paintClip;
   private int _firstDirtyLeafIdx;
   private int _volatileBufferLeafIdx;
   private int _staticBufferLeafIdx;
   private boolean _staticBufferLeafIdxComputed;
   private int _volatileBufferID;
   private boolean _volatileBufferEnabled;
   private int _staticBufferID;
   private boolean _staticBufferEnabled;
   boolean _staticBufferInitialized;
   int _contextListRender = 1;
   private int[] _viewbox = new int[]{
      0, 0, 0, 0, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550, 1852795252, 1685343264, 46
   };
   private boolean _viewboxInitialized = false;
   private static final int MIN_ZOOM_AMOUNT;
   private static final int MAX_ZOOM_AMOUNT;
   private static final int DEFAULT_ZOOM_FACTOR;
   private static final int DIRTY_ZOOM;
   private static final int DIRTY_ZOOM_ORIGIN_X;
   private static final int DIRTY_ZOOM_ORIGIN_Y;
   private static final int DIRTY_ALIGN_X;
   private static final int DIRTY_ALIGN_Y;
   private static final int DIRTY_PAN_X;
   private static final int DIRTY_PAN_Y;
   private static final int DIRTY_ALL;

   public final void resetContextData() {
      if (this._baseContext != null) {
         this._baseContext.reset();
      }

      if (this._backgroundContext != null) {
         this._backgroundContext.reset();
      }

      int length = this._contextList.length;

      for (int i = 0; i < length; i++) {
         if (this._contextList[i] != null) {
            this._contextList[i].reset();
         }
      }

      this._forceInvalidate = true;
   }

   @Override
   public final void setMedia(Object media) {
      this.resetZoomPan();
      if (media != null) {
         this._model = (AnimationModel)media;
         this._contextList = new MEGraphics2dContext[this._model._maxUID << 1];
         this._contextRenderIdx = new int[this._contextList.length];
         this._renderLeafs = new LeafNode[this._model._maxUID];
         this._numRenderLeafs = 0;
         this._firstDirtyLeafIdx = 0;
         this._staticBufferInitialized = false;
         this._staticBufferLeafIdxComputed = false;
         this._staticBufferLeafIdx = -1;
         this._volatileBufferLeafIdx = -1;
         this.pushContext(this._baseContext);
         this.createContextList(this._model._visualRoot);
         this._meGraphic.popContext();
         this._leafContextList = new MEGraphics2dContext[this._numRenderLeafs];
         this._staticBufferEnabled = (this._style & 2) != 0;
         this._volatileBufferEnabled = (this._style & 4) != 0;
         if (this._staticBufferLeafIdx == this._numRenderLeafs - 1) {
            this._volatileBufferEnabled = false;
         }

         if (this._staticBufferLeafIdx == -1) {
            this._staticBufferEnabled = false;
         }

         if (this._staticBufferEnabled && this._staticBufferID == -1) {
            if (!this._volatileBufferEnabled && this._volatileBufferID != -1) {
               this._staticBufferID = this._volatileBufferID;
               this._volatileBufferID = -1;
            } else {
               this._staticBufferID = this._meGraphic.createNewBuffer();
            }

            if (this._staticBufferID != -1) {
               this._meGraphic.switchDrawToBuffer(this._staticBufferID);
               this._meGraphic.clear(0);
            } else {
               this._staticBufferEnabled = false;
            }
         }

         if (this._volatileBufferEnabled && this._volatileBufferID == -1) {
            if (!this._staticBufferEnabled && this._staticBufferID != -1) {
               this._volatileBufferID = this._staticBufferID;
               this._staticBufferID = -1;
            } else {
               this._volatileBufferID = this._meGraphic.createNewBuffer();
            }

            if (this._volatileBufferID != -1) {
               this._meGraphic.switchDrawToBuffer(this._volatileBufferID);
               this._meGraphic.clear(0);
            } else {
               this._volatileBufferEnabled = false;
            }
         }

         if (this._model._hasbkgColor) {
            this._backgroundContext = new MEGraphics2dContext();
            this.initSVGProperties(this._backgroundContext);
            if (this._volatileBufferEnabled) {
               this._meGraphic.switchDrawToBuffer(this._volatileBufferID);
               this._meGraphic.setBackgroundColor(this._model._bkgColor);
            }

            if (this._staticBufferEnabled) {
               this._meGraphic.switchDrawToBuffer(this._staticBufferID);
               this._meGraphic.setBackgroundColor(this._model._bkgColor);
            }
         } else {
            this._backgroundContext = null;
            if (this._volatileBufferEnabled) {
               this._meGraphic.switchDrawToBuffer(this._volatileBufferID);
               this._meGraphic.setBackgroundColor(16777215);
            }

            if (this._staticBufferEnabled) {
               this._meGraphic.switchDrawToBuffer(this._staticBufferID);
               this._meGraphic.setBackgroundColor(16777215);
            }
         }

         this._dirty = (byte)(this._dirty | 128);
      }
   }

   @Override
   public final void setExtent(int width, int height) {
      if (width != Fixed32.toInt(this._baseContext.getWidth()) || height != Fixed32.toInt(this._baseContext.getHeight())) {
         int maxInt = Fixed32.toInt(Integer.MAX_VALUE);
         int fpWidth = width > maxInt ? Fixed32.toFP(maxInt) : Fixed32.toFP(width);
         int fpHeight = height > maxInt ? Fixed32.toFP(maxInt) : Fixed32.toFP(height);
         this._baseContext.setWidth(fpWidth, 0);
         this._baseContext.setHeight(fpHeight, 0);
         if (this._dirtyRect == null) {
            this._dirtyRect = (XYRect)(new Object(0, 0, width, height));
         } else {
            this._dirtyRect.set(0, 0, width, height);
         }

         if (this._paintClip == null) {
            this._paintClip = (XYRect)(new Object(0, 0, width, height));
         } else {
            this._paintClip.set(0, 0, fpWidth, fpHeight);
         }

         this._baseContext.setClip(0, 0, this._baseContext.getWidth(), this._baseContext.getHeight());
      }

      this.calculateVirtualExtent();
      if (this._baseClip == null) {
         this._baseClip = (XYRect)(new Object(0, 0, Fixed32.toInt(this._virtualWidth), Fixed32.toInt(this._virtualHeight)));
      } else {
         this._baseClip.set(0, 0, Fixed32.toInt(this._virtualWidth), Fixed32.toInt(this._virtualHeight));
      }
   }

   @Override
   public final void paint(Object g) {
      this.paint(g, 0, 0, this._baseContext.getWidth(), this._baseContext.getHeight(), 0, 0);
   }

   @Override
   public final void paint(Object g, int clipX, int clipY, int clipW, int clipH) {
      this.paint(g, clipX, clipY, clipW, clipH, 0, 0);
   }

   @Override
   public final void paint(Object g, int clipX, int clipY, int clipW, int clipH, int gOffsetX, int gOffsetY) {
      this._dirtyRect.set(0, 0, 0, 0);
      this._meGraphic.setGraphics(g);
      this._paintClip.set(Fixed32.toFP(clipX), Fixed32.toFP(clipY), Fixed32.toFP(clipW), Fixed32.toFP(clipH));
      boolean bStaticBufferEnabled = this._staticBufferEnabled && (this._style & 1) == 0;
      boolean bVolatileBufferEnabled = this._volatileBufferEnabled && (this._style & 1) == 0;
      if (bStaticBufferEnabled && this._staticBufferLeafIdx == this._numRenderLeafs - 1) {
         bVolatileBufferEnabled = false;
      }

      boolean bPan = false;
      if (this._dirty != 0 || this._forceInvalidate) {
         this.invalidate();
         if (bStaticBufferEnabled && !bVolatileBufferEnabled && (this._style & 8) != 0) {
            if (this._panXDelta == 0 && this._panYDelta == 0) {
               this.updateViewbox(true);
            } else {
               this.updateViewbox(false);
            }

            if ((this._panXDelta != 0 || this._panYDelta != 0)
               && Math.abs(this._panXDelta) < this._virtualWidth
               && Math.abs(this._panYDelta) < this._virtualHeight) {
               bPan = true;
            }
         }

         if (bPan) {
            int xDelta = Fixed32.toInt(this._panXDelta);
            int yDelta = Fixed32.toInt(this._panYDelta);
            this._meGraphic.panBuffer(this._staticBufferID, xDelta, yDelta);
            if (xDelta > 0) {
               this._baseContext.setClip(0, 0, this._panXDelta, this._paintClip.height);
               this.initStaticBuffer(false);
            } else if (xDelta < 0) {
               this._baseContext.setClip(this._paintClip.width + this._panXDelta, 0, -this._panXDelta, this._paintClip.height);
               this.initStaticBuffer(false);
            }

            if (yDelta > 0) {
               if (xDelta == 0) {
                  this._baseContext.setClip(0, 0, this._paintClip.width, this._panYDelta);
               } else if (xDelta > 0) {
                  this._baseContext.setClip(this._panXDelta, 0, this._paintClip.width - this._panXDelta, this._panYDelta);
               } else if (xDelta < 0) {
                  this._baseContext.setClip(0, 0, this._paintClip.width - this._panXDelta, this._panYDelta);
               }

               this.initStaticBuffer(this._staticBufferInitialized);
            } else if (yDelta < 0) {
               if (xDelta == 0) {
                  this._baseContext.setClip(0, this._paintClip.height + this._panYDelta, this._paintClip.width, -this._panYDelta);
               } else if (xDelta > 0) {
                  this._baseContext
                     .setClip(this._panXDelta, this._paintClip.height + this._panYDelta, this._paintClip.width - this._panXDelta, -this._panYDelta);
               } else if (xDelta < 0) {
                  this._baseContext.setClip(0, this._paintClip.height + this._panYDelta, this._paintClip.width - this._panXDelta, -this._panYDelta);
               }

               this.initStaticBuffer(this._staticBufferInitialized);
            }
         }

         this._panXDelta = 0;
         this._panYDelta = 0;
      }

      int nCtxList = 0;
      int iStartLeaf = 0;
      if (bStaticBufferEnabled) {
         if (!this._staticBufferInitialized || this._firstDirtyLeafIdx <= this._staticBufferLeafIdx || this._dirty != 0) {
            if (!bPan) {
               this.initStaticBuffer(false);
            }

            iStartLeaf = this._staticBufferLeafIdx + 1;
         }
      } else {
         this._staticBufferLeafIdx = -1;
      }

      if (!bVolatileBufferEnabled) {
         this._volatileBufferLeafIdx = -1;
      } else {
         boolean bVolatileBufferValid = this._volatileBufferLeafIdx < this._firstDirtyLeafIdx && this._volatileBufferLeafIdx != -1;
         if (bVolatileBufferValid) {
            if (this._volatileBufferLeafIdx + 1 < this._firstDirtyLeafIdx) {
               this._meGraphic.switchDrawToBuffer(this._volatileBufferID);

               for (int iLeaf = this._volatileBufferLeafIdx + 1; iLeaf < this._firstDirtyLeafIdx; iLeaf++) {
                  if (this._renderLeafs[iLeaf]._isVisible) {
                     if (this._contextListRender != 0) {
                        nCtxList = this.addRenderContext(this._renderLeafs[iLeaf], nCtxList);
                     } else {
                        this._renderLeafs[iLeaf].render();
                     }
                  }
               }

               if (nCtxList > 0) {
                  this._meGraphic.drawContextList(this._leafContextList, nCtxList);
                  nCtxList = 0;
               }

               this._volatileBufferLeafIdx = this._firstDirtyLeafIdx - 1;
            }
         } else {
            this._meGraphic.switchDrawToBuffer(this._volatileBufferID);
            this._meGraphic.clear(0);
            if (this._staticBufferLeafIdx >= 0) {
               this._meGraphic.applyBuffer(this._staticBufferID, this._baseContext.getClip());
            }

            for (int iLeaf = this._staticBufferLeafIdx + 1; iLeaf < this._firstDirtyLeafIdx; iLeaf++) {
               if (this._renderLeafs[iLeaf]._isVisible) {
                  if (this._contextListRender != 0) {
                     nCtxList = this.addRenderContext(this._renderLeafs[iLeaf], nCtxList);
                  } else {
                     this._renderLeafs[iLeaf].render();
                  }
               }
            }

            if (nCtxList > 0) {
               this._meGraphic.drawContextList(this._leafContextList, nCtxList);
               nCtxList = 0;
            }

            this._volatileBufferLeafIdx = this._firstDirtyLeafIdx - 1 > this._staticBufferLeafIdx ? this._firstDirtyLeafIdx - 1 : this._staticBufferLeafIdx;
         }

         iStartLeaf = this._volatileBufferLeafIdx + 1;
      }

      this._meGraphic.switchDrawToScreen();
      if (this._model._hasbkgColor) {
         this._meGraphic.setBackgroundColor(this._model._bkgColor);
         if (this._backgroundContext == null) {
            this._backgroundContext = new MEGraphics2dContext();
            this.initSVGProperties(this._backgroundContext);
         }

         this._backgroundContext.setClip(this._paintClip);
         this._backgroundContext.setFillColor(this._model._bkgColor);
         this.pushContext(this._backgroundContext);
         this._meGraphic.drawRect(this._paintClip.x, this._paintClip.y, this._paintClip.width, this._paintClip.height);
         this._meGraphic.popContext();
      }

      if (bVolatileBufferEnabled) {
         if (this._volatileBufferLeafIdx >= 0) {
            this._meGraphic.applyBuffer(this._volatileBufferID, this._paintClip);
         }
      } else if (bStaticBufferEnabled && this._staticBufferLeafIdx >= 0) {
         this._meGraphic.applyBuffer(this._staticBufferID, this._paintClip);
         iStartLeaf = this._staticBufferLeafIdx + 1;
      }

      this._paintClip.set(clipX, clipY, clipW, clipH);

      for (int iLeaf = iStartLeaf; iLeaf < this._numRenderLeafs; iLeaf++) {
         if (this._renderLeafs[iLeaf]._isVisible && this._renderLeafs[iLeaf].intersects(this._paintClip)) {
            if (this._contextListRender != 0) {
               nCtxList = this.addRenderContext(this._renderLeafs[iLeaf], nCtxList);
            } else {
               this._renderLeafs[iLeaf].render();
            }
         }
      }

      if (nCtxList > 0) {
         this._meGraphic.drawContextList(this._leafContextList, nCtxList);
      }

      this._firstDirtyLeafIdx = this._numRenderLeafs;
      if (bPan) {
         this._baseContext.setClip(0, 0, this._baseContext.getWidth(), this._baseContext.getHeight());
      }
   }

   @Override
   public final void invalidate(int x, int y, int width, int height) {
      this._dirtyRect.set(x, y, width, height);
      this.invalidate();
   }

   @Override
   public final void invalidate() {
      this._forceInvalidate = false;
      this._baseContext.invalidate();
      if (this._dirty != 0) {
         this.calculateAlignmentMatrix();
         this._baseContext.setDirtyBits(134217728);
         this._baseContext.setTransformMatrix(this._alignmentMatrix, 0);
      }

      this._oldDirtyRect.set(this._dirtyRect);
      this._dirtyRect.set(0, 0, 0, 0);
      this._firstDirtyLeafIdx = this._numRenderLeafs - 1;
      this.update(this._model._visualRoot, true);
      if (this._dirtyRect.width > 0 || this._dirtyRect.height > 0) {
         this._dirtyRect.x--;
         this._dirtyRect.y--;
         this._dirtyRect.width += 2;
         this._dirtyRect.height += 2;
      }

      this._dirtyRect.unionNoEmpty(this._oldDirtyRect);
      this._baseContext.setDirtyBits(0);
      this._dirty = 0;
   }

   @Override
   public final void dirtyAll() {
      this._dirty = -128;
   }

   @Override
   public final int getDirtyX() {
      return this._dirtyRect.x;
   }

   @Override
   public final int getDirtyY() {
      return this._dirtyRect.y;
   }

   @Override
   public final int getDirtyWidth() {
      return this._dirtyRect.width;
   }

   @Override
   public final int getDirtyHeight() {
      return this._dirtyRect.height;
   }

   @Override
   public final int getVirtualHeight() {
      if (this._model == null) {
         return 0;
      }

      if (this._virtualHeight == 0) {
         this.calculateVirtualExtent();
      }

      return Fixed32.toRoundedInt(this._virtualHeight);
   }

   @Override
   public final int getVirtualWidth() {
      if (this._model == null) {
         return 0;
      }

      if (this._virtualWidth == 0) {
         this.calculateVirtualExtent();
      }

      return Fixed32.toRoundedInt(this._virtualWidth);
   }

   @Override
   public final boolean isContentWidthAbsolute() {
      boolean result = false;
      if (this._model != null) {
         int bits = this._model._nodes[this._model._visualRoot + 8];
         result = (bits & 16384) == 0;
      }

      return result;
   }

   @Override
   public final boolean isContentHeightAbsolute() {
      boolean result = false;
      if (this._model != null) {
         int bits = this._model._nodes[this._model._visualRoot + 8];
         result = (bits & 32768) == 0;
      }

      return result;
   }

   @Override
   public final void setStyle(int style) {
      this._style = style;
   }

   @Override
   public final int getStyle() {
      return this._style;
   }

   @Override
   public final void setServices(MediaServices s) {
      this._services = s;

      try {
         this._meGraphic = (PME12Graphics)MediaFactory.createObjectFromRegistry(new String[]{"FRAMEWORK", "GRAPHICS"});
      } finally {
         return;
      }
   }

   @Override
   public final void dispose() {
   }

   @Override
   public final void setPanX(int amount) {
      this._panXDelta = this._panXDelta + (amount - this._panXAmount);
      this._panXAmount = amount;
      this._dirty = (byte)(this._dirty | 32);
   }

   @Override
   public final void setPanY(int amount) {
      this._panYDelta = this._panYDelta + (amount - this._panYAmount);
      this._panYAmount = amount;
      this._dirty = (byte)(this._dirty | 64);
   }

   @Override
   public final int getPanX() {
      return this._panXAmount;
   }

   @Override
   public final int getPanY() {
      return this._panYAmount;
   }

   @Override
   public final boolean isPannable() {
      return this._model._isZoomAndPannable;
   }

   @Override
   public final void getPanBounds(XYEdges theEdge) {
   }

   @Override
   public final void setZoomAmount(int zoomFactor) {
      if (zoomFactor < 16) {
         this._zoomFactor = 16;
      } else if (zoomFactor > 3276800) {
         this._zoomFactor = 3276800;
      } else {
         this._zoomFactor = zoomFactor;
      }

      this._dirty = (byte)(this._dirty | 1);
   }

   @Override
   public final int getZoomAmount() {
      return this._zoomFactor;
   }

   @Override
   public final void setZoomOriginX(int xOrigin) {
      this._zoomOriginX = xOrigin;
      this._dirty = (byte)(this._dirty | 2);
   }

   @Override
   public final void setZoomOriginY(int yOrigin) {
      this._zoomOriginY = yOrigin;
      this._dirty = (byte)(this._dirty | 4);
   }

   @Override
   public final int getZoomOriginX() {
      return this._zoomOriginX;
   }

   @Override
   public final int getZoomOriginY() {
      return this._zoomOriginY;
   }

   @Override
   public final boolean isZoomable() {
      return this._model._isZoomAndPannable;
   }

   @Override
   public final int getOriginX() {
      return this._alignmentMatrix[2] == 0 ? this._alignX >> 16 : this._alignmentMatrix[2] >> 16;
   }

   @Override
   public final int getOriginY() {
      return this._alignmentMatrix[5] == 0 ? this._alignY >> 16 : this._alignmentMatrix[5] >> 16;
   }

   @Override
   public final void setOrigin(int x, int y) {
      this._alignX = x << 16;
      this._alignY = y << 16;
      this._dirty = (byte)(this._dirty | 24);
   }

   @Override
   public final int getAlignX() {
      return this._alignX >> 16;
   }

   @Override
   public final int getAlignY() {
      return this._alignY >> 16;
   }

   @Override
   public final Object getMedia() {
      return this._model;
   }

   public AnimationViewport(Object m) {
      this();
      this.setMedia(m);
   }

   private final void pushContext(MEGraphics2dContext ctx) {
      label17:
      try {
         MEGraphics2dContext parent = this._meGraphic.peekContext();
         ctx.setParent(parent);
      } finally {
         break label17;
      }

      this._meGraphic.pushContext(ctx);
   }

   private final void calculateAlignmentMatrix() {
      if (this._dirty != 0) {
         this._alignmentMatrix[0] = this._zoomFactor;
         this._alignmentMatrix[4] = this._zoomFactor;
         this._alignmentMatrix[2] = this._zoomOriginX - (int)((long)this._zoomFactor * (this._zoomOriginX - this._alignX) >> 16) + this._panXAmount;
         this._alignmentMatrix[5] = this._zoomOriginY - (int)((long)this._zoomFactor * (this._zoomOriginY - this._alignY) >> 16) + this._panYAmount;
      }
   }

   private final MEGraphics2dContext getContext(int idx) {
      int contextIndex = this._model._nodes[idx + 9] << 1;
      MEGraphics2dContext ctx = this._contextList[contextIndex];
      if (ctx == null) {
         ctx = new MEGraphics2dContext();
         this._contextList[contextIndex] = ctx;
      }

      return ctx;
   }

   private final int getContextIndex(int idx) {
      return this._model._nodes[idx + 9] << 1;
   }

   private final int getChildNodeIndex(int position, int groupIdx) {
      int index = 0;

      for (int child = this._model._nodes[groupIdx + 6]; child != -1; index++) {
         if (index == position) {
            return child;
         }

         child = this._model._nodes[child + 4];
      }

      return -1;
   }

   private final void setShapeNodeData(int idx, MEGraphics2dContext ctx) {
      this.setFillData(idx, ctx);
      this.setStrokeData(idx, ctx);
   }

   private final void setVisualNodeData(int idx, MEGraphics2dContext ctx) {
      int bits = this._model._nodes[idx + 8];
      if ((bits & 64) != 0) {
         ctx.setVisibility((bits & 128) != 0 ? 1 : 0);
      } else {
         ctx.setVisibility(Integer.MAX_VALUE);
      }

      int transformAttrIdx = this._model._nodes[idx + 12];
      if (transformAttrIdx != -1 && ctx.isDirtyTransform()) {
         int matrixOffset = transformAttrIdx + 3;
         ctx.setTransformMatrix(this._model._nodes, matrixOffset);
      }

      int opacity = this._model._nodes[idx + 13];
      ctx.setObjectOpacity(opacity);
   }

   private final void setContainerNodeData(int idx, MEGraphics2dContext ctx) {
      this.setVisualNodeData(idx, ctx);
      this.setShapeNodeData(idx, ctx);
      int bits = this._model._nodes[idx + 8];
      if ((bits & 1024) != 0) {
         int hints = this._model._nodes[idx + 14];
         int whatHints = hints & 0xFF000000;
         whatHints >>>= 24;
         if ((whatHints & 1) != 0) {
            this.setImageRenderingHints(hints, ctx);
         }

         if ((whatHints & 4) != 0) {
            this.setShapeRenderingHints(hints, ctx);
         }

         if ((whatHints & 2) != 0) {
            this.setTextRenderingHints(hints, ctx);
         }
      }
   }

   private final boolean setViewportNodeData(int index, MEGraphics2dContext ctx) {
      int bits = this._model._nodes[index + 8];
      int wUnit = (bits & 16384) == 0 ? 0 : 10;
      int hUnit = (bits & 32768) == 0 ? 0 : 10;
      int width = this._model._nodes[index + 23];
      int height = this._model._nodes[index + 24];
      int x = index == this._model._visualRoot ? 0 : this._model._nodes[index + 10];
      int y = index == this._model._visualRoot ? 0 : this._model._nodes[index + 11];
      ctx.setViewport(x, y, width, wUnit, height, hUnit);
      if ((bits & 524288) == 0) {
         ctx.setClip(x, 0, y, 0, width, wUnit, height, hUnit);
      }

      return width > 0 && height > 0;
   }

   private final MEGraphics2dContext getViewboxContext(int index) {
      int contextIndex = (this._model._nodes[index + 9] << 1) + 1;
      MEGraphics2dContext context = this._contextList[contextIndex];
      if (context == null) {
         context = new MEGraphics2dContext();
         this._contextList[contextIndex] = context;
      }

      return context;
   }

   private final boolean setViewboxData(int index, MEGraphics2dContext context) {
      int width = 1;
      int height = 1;
      int viewboxAttrIdx = this._model._nodes[index + 28];
      if (this._model._nodes[index + 1] == 42) {
         int imageIdx = this._model._nodes[index + 29];
         Object image = this._model._images[imageIdx];
         if (image != null) {
            width = Fixed32.toFP(this._platform.getImageWidth(image));
            height = Fixed32.toFP(this._platform.getImageHeight(image));
            this._model._nodes[viewboxAttrIdx + 3] = 0;
            this._model._nodes[viewboxAttrIdx + 4] = 0;
            this._model._nodes[viewboxAttrIdx + 5] = width;
            this._model._nodes[viewboxAttrIdx + 6] = height;
         }
      }

      if (viewboxAttrIdx != -1 || this._model._nodes[index + 1] == 42) {
         width = this._model._nodes[viewboxAttrIdx + 5];
         height = this._model._nodes[viewboxAttrIdx + 6];
         context.setViewbox(this._model._nodes[viewboxAttrIdx + 3], this._model._nodes[viewboxAttrIdx + 4], width, height);
      } else if (context.isParentViewboxDirty()) {
         height = Integer.MIN_VALUE;
         width = Integer.MIN_VALUE;
         context.setViewbox(Integer.MIN_VALUE, Integer.MIN_VALUE, width, height);
      }

      return width > 0 && height > 0 || width == Integer.MIN_VALUE && height == Integer.MIN_VALUE;
   }

   private final void setAspectRatioData(int index, MEGraphics2dContext context) {
      if (this._model._nodes[index + 28] != -1) {
         int nodeBits = this._model._nodes[index + 8];
         if ((nodeBits & 8192) != 0) {
            context.setAspectRatio(this._model._nodes[index + 27]);
         }
      }
   }

   private final void setStrokeData(int idx, MEGraphics2dContext ctx) {
      int nodeBits = this._model._nodes[idx + 8];
      if ((nodeBits & 4096) != 0) {
         ctx.setStrokePaint(Integer.MIN_VALUE);
      }

      int strokeAttrIdx = this._model._nodes[idx + 22];
      if (strokeAttrIdx != -1) {
         int strokeBits = this._model._nodes[strokeAttrIdx + 3];
         int paintAttrIdx = this._model._nodes[strokeAttrIdx + 4];
         if (paintAttrIdx != -1) {
            int paintType = this._model._nodes[paintAttrIdx + 1];
            switch (paintType) {
               case 74:
                  if (this._model._nodes[paintAttrIdx + 3] == Integer.MIN_VALUE) {
                     ctx.setStrokePaint(Integer.MIN_VALUE);
                  } else if (this._model._nodes[paintAttrIdx + 3] == Integer.MAX_VALUE) {
                     ctx.setStrokePaint(Integer.MAX_VALUE);
                  } else {
                     ctx.setStrokePaint(1);
                     int color = this._model._nodes[paintAttrIdx + 3] & 16777215;
                     ctx.setStrokeColor(color);
                  }
            }
         }

         if ((strokeBits & 4) != 0) {
            int strokeOpacity = this._model._nodes[strokeAttrIdx + 5];
            ctx.setStrokeOpacity(strokeOpacity);
         }

         if ((strokeBits & 8) != 0) {
            int strokeWidth = this._model._nodes[strokeAttrIdx + 6];
            ctx.setStrokeWidth(strokeWidth);
         }

         int strokeStyle = this._model._nodes[strokeAttrIdx + 7];
         if ((strokeBits & 16) != 0) {
            int linecapStyle = strokeStyle & 240;
            if (linecapStyle == 240) {
               ctx.setStrokeLinecap(Integer.MAX_VALUE);
            } else {
               ctx.setStrokeLinecap(linecapStyle);
            }
         }

         if ((strokeBits & 32) != 0) {
            int linejoinStyle = strokeStyle & 15;
            if (linejoinStyle == 15) {
               ctx.setStrokeLinejoin(Integer.MAX_VALUE);
               return;
            }

            ctx.setStrokeLinejoin(linejoinStyle);
         }
      }
   }

   private final void setFillData(int idx, MEGraphics2dContext ctx) {
      int nodeBits = this._model._nodes[idx + 8];
      if ((nodeBits & 2048) != 0) {
         ctx.setFillPaint(Integer.MIN_VALUE);
      }

      int fillAttrIdx = this._model._nodes[idx + 21];
      if (fillAttrIdx != -1) {
         int fillBits = this._model._nodes[fillAttrIdx + 3];
         int paintAttrIdx = this._model._nodes[fillAttrIdx + 4];
         if (paintAttrIdx != -1) {
            int paintType = this._model._nodes[paintAttrIdx + 1];
            switch (paintType) {
               case 76:
                  if (this._model._nodes[paintAttrIdx + 3] == Integer.MAX_VALUE) {
                     ctx.setFillPaint(2);
                     int imageIdx = this._model._nodes[paintAttrIdx + 4];
                     int textureIdx = this._model._nodes[imageIdx + 29];
                     ctx.setFillTexture(this._meGraphic.getBitmapObject(this._model._images[textureIdx]));
                     ctx.setFillTextureDimensions(
                        this._model._nodes[imageIdx + 10],
                        this._model._nodes[imageIdx + 11],
                        this._model._nodes[imageIdx + 23],
                        this._model._nodes[imageIdx + 24]
                     );
                     int transformIdx = this._model._nodes[imageIdx + 12];
                     if (transformIdx != -1) {
                        int matrixOffset = transformIdx + 3;
                        ctx.setFillTextureTransformMatrix(this._model._nodes, matrixOffset);
                     }
                     break;
                  }
               case 74:
                  if (this._model._nodes[paintAttrIdx + 3] == Integer.MAX_VALUE) {
                     ctx.setFillPaint(Integer.MAX_VALUE);
                  } else if (this._model._nodes[paintAttrIdx + 3] == Integer.MIN_VALUE) {
                     ctx.setFillPaint(Integer.MIN_VALUE);
                  } else {
                     ctx.setFillPaint(1);
                     int color = this._model._nodes[paintAttrIdx + 3] & 16777215;
                     ctx.setFillColor(color);
                  }
            }
         }

         if ((fillBits & 4) != 0) {
            int fillOpacity = this._model._nodes[fillAttrIdx + 5];
            ctx.setFillOpacity(fillOpacity);
         }
      }
   }

   private final void setTextAttrData(int textAttrIdx, MEGraphics2dContext ctx) {
      if (textAttrIdx != -1) {
         int bits = this._model._nodes[textAttrIdx + 3];
         if ((bits & 64) != 0) {
            int fontFamilyIndex = this._model._nodes[textAttrIdx + 7];
            if (fontFamilyIndex != Integer.MAX_VALUE && fontFamilyIndex != -1) {
               String fontFamily = this._model._platformFontFamilyStrings[fontFamilyIndex];
               ctx.setFontFamilyName(fontFamily);
            } else if (fontFamilyIndex == Integer.MAX_VALUE) {
               ctx.setFontFamilyName("inherit");
            } else {
               ctx.setFontFamilyName("BBMillbank");
            }
         }

         if ((bits & 32) != 0) {
            int fontPixelSize = this._model._nodes[textAttrIdx + 4];
            ctx.setFontPixelSize(fontPixelSize);
         }

         if ((bits & 16) != 0) {
            int weight = this._model._nodes[textAttrIdx + 5];
            ctx.setFontWeight(weight);
         }

         if ((bits & 2) != 0) {
            int style = this._model._nodes[textAttrIdx + 6];
            ctx.setFontStyle(style);
         }

         if ((bits & 128) != 0) {
            int taBits = (bits & 1536) >>> 9;
            switch (taBits) {
               case -1:
                  break;
               case 0:
               default:
                  ctx.setTextAnchor(1);
                  break;
               case 1:
                  ctx.setTextAnchor(2);
                  break;
               case 2:
                  ctx.setTextAnchor(3);
            }
         }

         if ((bits & 8) != 0) {
            int decoration = this._model._nodes[textAttrIdx + 8];
            ctx.setTextDecoration(decoration);
         }
      }
   }

   private final void setShapeRenderingHints(int hints, MEGraphics2dContext ctx) {
      hints &= 255;
      hints >>>= 0;
      switch (hints) {
         case 0:
            ctx.setShapeRenderingHints(0);
            return;
         case 1:
         default:
            ctx.setShapeRenderingHints(1);
            return;
         case 2:
            ctx.setShapeRenderingHints(2);
            return;
         case 3:
            ctx.setShapeRenderingHints(3);
      }
   }

   private final void resetZoomPan() {
      this._panXDelta = -this._panXAmount;
      this._panXAmount = 0;
      this._panYDelta = -this._panYAmount;
      this._panYAmount = 0;
      this._zoomFactor = 65536;
      this._dirty = 97;
   }

   private final void setImageRenderingHints(int hints, MEGraphics2dContext ctx) {
      hints &= 16711680;
      hints >>>= 16;
      if ((hints & 1) != 0) {
         ctx.setImageRenderingHints(1);
      } else if ((hints & 3) != 0) {
         ctx.setImageRenderingHints(3);
      } else {
         ctx.setImageRenderingHints(0);
      }
   }

   private final void setTextRenderingHints(int hints, MEGraphics2dContext ctx) {
      hints &= 65280;
      hints >>>= 8;
      int truncateHint = hints & 4;
      hints &= -5;
      switch (hints) {
         case 0:
            ctx.setTextRenderingHints(0 | truncateHint);
            return;
         case 1:
         default:
            ctx.setTextRenderingHints(1 | truncateHint);
            return;
         case 2:
            ctx.setTextRenderingHints(2 | truncateHint);
            return;
         case 3:
            ctx.setTextRenderingHints(3 | truncateHint);
      }
   }

   private final void calculateVirtualExtent() {
      int rootSVG = this._model._visualRoot;
      int rootContextIdx = this._model._nodes[rootSVG + 9] << 1;
      int bits = this._model._nodes[rootSVG + 8];
      MEGraphics2dContext ctx = this._contextList[rootContextIdx];
      if (ctx == null) {
         ctx = new MEGraphics2dContext();
         this._contextList[rootContextIdx] = ctx;
      }

      if ((bits & 16384) != 0) {
         this._virtualWidth = this._baseContext.calculatePercentage32(this._baseContext.getWidth(), this._model._nodes[rootSVG + 25]);
      } else {
         this._virtualWidth = this._model._nodes[rootSVG + 25];
      }

      if ((bits & 32768) != 0) {
         this._virtualHeight = this._baseContext.calculatePercentage32(this._baseContext.getHeight(), this._model._nodes[rootSVG + 26]);
      } else {
         this._virtualHeight = this._model._nodes[rootSVG + 26];
      }
   }

   private final void initStaticBuffer(boolean invalidateClips) {
      int nCtxList = 0;
      this._meGraphic.switchDrawToBuffer(this._staticBufferID);
      XYRect clip = this._baseContext.getClip();
      this._meGraphic.clear(Fixed32.toInt(clip.x), Fixed32.toInt(clip.y), Fixed32.toInt(clip.width), Fixed32.toInt(clip.height), 0);

      for (int iLeaf = 0; iLeaf <= this._staticBufferLeafIdx; iLeaf++) {
         if (invalidateClips) {
            this._renderLeafs[iLeaf]._context.invalidateClip();
            this._renderLeafs[iLeaf]._context.getClip();
         }

         if (this._renderLeafs[iLeaf]._isVisible) {
            if (this._contextListRender != 0) {
               nCtxList = this.addRenderContext(this._renderLeafs[iLeaf], nCtxList);
            } else {
               this._renderLeafs[iLeaf].render();
            }
         }
      }

      if (nCtxList > 0) {
         this._meGraphic.drawContextList(this._leafContextList, nCtxList);
      }

      this._staticBufferInitialized = true;
   }

   private final void initSVGProperties(MEGraphics2dContext ctx) {
      ctx.setVisibility(1);
      ctx.setFillPaint(1);
      ctx.setFillColor(0);
      ctx.setFillOpacity(255);
      ctx.setStrokePaint(Integer.MIN_VALUE);
      ctx.setStrokeOpacity(255);
      ctx.setStrokeWidth(Fixed32.toFP(1));
      ctx.setStrokeLinecap(16);
      ctx.setStrokeLinejoin(1);
      ctx.setStrokeMiterlimit(4);
      ctx.setShapeRenderingHints(0);
      ctx.setImageRenderingHints(0);
      ctx.setTextRenderingHints(0);
      ctx.setFontFamilyName("BBMillbank");
      ctx.setFontPixelSize(Fixed32.toFP(12));
      ctx.setFontStyle(1);
      ctx.setTextAnchor(1);
      ctx.setFontWeight(400);
      ctx.setTextDecoration(1);
      ctx.setX(0);
      ctx.setY(0);
      ctx.setDx(0);
      ctx.setDy(0);
   }

   private final void resetViewboxData(MEGraphics2dContext vbContext) {
      if (vbContext.getWidth() == Integer.MIN_VALUE
         && vbContext.getHeight() == Integer.MIN_VALUE
         && vbContext.getX() == Integer.MIN_VALUE
         && vbContext.getY() == Integer.MIN_VALUE) {
         vbContext.setViewbox(0, 0, vbContext.getParent().getWidth(), vbContext.getParent().getHeight());
      }
   }

   public AnimationViewport() {
      this._platform = MediaFactory.getPlatform();
      this._baseContext = new MEGraphics2dContext();
      this.initSVGProperties(this._baseContext);
      this._baseContext.setWidth(0, 0);
      this._baseContext.setHeight(0, 0);
      this._baseContext.setClip(0, 0, 0, 0);
      this._staticBufferID = -1;
      this._volatileBufferID = -1;
      this._alignmentMatrix = new int[9];
      this._rootSVGMatrix = new int[9];
      this.resetZoomPan();
      this._platform.setIdentity(this._alignmentMatrix, 0);
      this._platform.setIdentity(this._rootSVGMatrix, 0);
      this._dirtyRect = (XYRect)(new Object());
      this._oldDirtyRect = (XYRect)(new Object());
      this._paintClip = (XYRect)(new Object());
      this._baseClip = (XYRect)(new Object());
   }

   private final int addRenderLeaf(LeafNode leafNode) {
      int retIdx = -1;
      if (this._numRenderLeafs < this._renderLeafs.length) {
         retIdx = this._numRenderLeafs;
         this._renderLeafs[this._numRenderLeafs] = leafNode;
         this._numRenderLeafs++;
         if (!this._staticBufferLeafIdxComputed && this._model._minAnimatedNodeIdx > leafNode._visualNodeIdx) {
            if (leafNode instanceof LeafNodeImage) {
               int imageIdx = this._model._nodes[leafNode._visualNodeIdx + 29];
               Object image = this._model._images[imageIdx];
               if (image instanceof Object) {
                  this._staticBufferLeafIdxComputed = true;
                  return retIdx;
               }

               this._staticBufferLeafIdx = retIdx;
               return retIdx;
            }

            if (leafNode instanceof LeafNodeForeignObject) {
               this._staticBufferLeafIdxComputed = true;
               return retIdx;
            }

            this._staticBufferLeafIdx = retIdx;
            return retIdx;
         }

         this._staticBufferLeafIdxComputed = true;
      }

      return retIdx;
   }

   private final void createContextList(int idx) {
      int contextIndex = this._model._nodes[idx + 9] << 1;
      MEGraphics2dContext ctx = this._contextList[contextIndex];
      if (ctx == null) {
         ctx = new MEGraphics2dContext();
         this._contextList[contextIndex] = ctx;
      }

      ctx._type = this._model._nodes[idx + 1];
      switch (ctx._type) {
         case 34:
            this.pushContext(ctx);
            this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodeEllipse(this._model, idx, this._meGraphic, ctx));
            this._meGraphic.popContext();
            return;
         case 36:
            this.pushContext(ctx);
            this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodeRect(this._model, idx, this._meGraphic, ctx));
            this._meGraphic.popContext();
            return;
         case 40:
            this.pushContext(ctx);
            this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodePath(this._model, idx, this._meGraphic, ctx));
            this._meGraphic.popContext();
            return;
         case 42:
            this.pushContext(ctx);
            MEGraphics2dContext viewboxContext = this.getViewboxContext(idx);
            if (viewboxContext != null) {
               viewboxContext._type = ctx._type;
               this.pushContext(viewboxContext);
               this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodeImage(this._model, idx, this._meGraphic, viewboxContext));
               this._meGraphic.popContext();
            } else {
               this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodeImage(this._model, idx, this._meGraphic, ctx));
            }

            this._meGraphic.popContext();
            return;
         case 44:
            this.pushContext(ctx);
            MEGraphics2dContext viewboxContext = this.getViewboxContext(idx);
            if (viewboxContext != null) {
               viewboxContext._type = ctx._type;
               this.pushContext(viewboxContext);
               this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodeForeignObject(this._model, idx, this._meGraphic, viewboxContext));
               this._meGraphic.popContext();
            } else {
               this._contextRenderIdx[contextIndex] = this.addRenderLeaf(new LeafNodeForeignObject(this._model, idx, this._meGraphic, ctx));
            }

            this._meGraphic.popContext();
            return;
         case 46:
            this.pushContext(ctx);
            MEGraphics2dContext viewboxContext = this.getViewboxContext(idx);
            if (viewboxContext != null) {
               this.pushContext(viewboxContext);
            }

            for (int child = this._model._nodes[idx + 6]; child != -1; child = this._model._nodes[child + 4]) {
               this.createContextList(child);
            }

            if (viewboxContext != null) {
               this._meGraphic.popContext();
            }

            this._meGraphic.popContext();
            return;
         case 48:
            this.pushContext(ctx);

            for (int child = this._model._nodes[idx + 6]; child != -1; child = this._model._nodes[child + 4]) {
               this.createContextList(child);
            }

            this._meGraphic.popContext();
            return;
         case 50:
            this.pushContext(ctx);
            this.createTextContext(idx, ctx, null);
            this._meGraphic.popContext();
            return;
         default:
            System.err.println(((StringBuffer)(new Object("AnimationViewport.render(): Unsupported node type: "))).append(ctx._type).toString());
      }
   }

   private final LeafNode createTextContext(int idx, MEGraphics2dContext ctx, LeafNode prevLeaf) {
      int child = this._model._nodes[idx + 6];
      int contextIndex = this._model._nodes[idx + 9] << 1;
      if (child == -1) {
         LeafNode newLeaf = new LeafNodeText(this._model, idx, this._meGraphic, ctx);
         this._contextRenderIdx[contextIndex] = this.addRenderLeaf(newLeaf);
         if (prevLeaf != null) {
            prevLeaf._nextLeaf = newLeaf;
         }

         return newLeaf;
      } else {
         while (child != -1) {
            MEGraphics2dContext childCtx = this.getContext(child);
            childCtx._type = ctx._type;
            this.pushContext(childCtx);
            prevLeaf = this.createTextContext(child, childCtx, prevLeaf);
            this._meGraphic.popContext();
            child = this._model._nodes[child + 4];
         }

         this._contextRenderIdx[contextIndex] = -1;
         return null;
      }
   }

   private final void update(int idx, boolean doRender) {
      int contextIndex = this._model._nodes[idx + 9] << 1;
      int bitsIdx = idx + 8;
      int bits = this._model._nodes[bitsIdx];
      this._model._nodes[bitsIdx] = this._model._nodes[bitsIdx] & ~(0xFF000000 & bits);
      MEGraphics2dContext ctx = this._contextList[contextIndex];
      ctx.invalidate();
      ctx.setDirtyBits(bits);
      MEGraphics2dContext viewboxContext = null;
      int type = this._model._nodes[idx + 1];
      switch (type) {
         case 42:
         case 44:
         case 46:
            viewboxContext = this.getViewboxContext(idx);
            if (viewboxContext != null) {
               viewboxContext.invalidate();
               viewboxContext.setDirtyBits(bits);
            }
         default:
            boolean display = (bits & 16) != 0;
            if (!display) {
               doRender = false;
               this._model._nodes[bitsIdx] = this._model._nodes[bitsIdx] | ctx.getDirtyOR();
            }

            boolean wasDisplayable = ctx.displayable();
            if (doRender || wasDisplayable) {
               switch (type) {
                  case 34:
                  case 36:
                  case 40:
                     this.updateLeafNode(idx, ctx, contextIndex, doRender);
                     break;
                  case 42:
                     this.updateImage(idx, ctx, contextIndex, doRender);
                     break;
                  case 44:
                     this.updateForeignObject(idx, ctx, contextIndex, doRender);
                     break;
                  case 46:
                     this.updateSVG(idx, ctx, doRender);
                     break;
                  case 48:
                     this.updateGroup(idx, ctx, doRender);
                     break;
                  case 50:
                     this.updateText(idx, ctx, contextIndex, null, false, doRender);
                     break;
                  default:
                     System.err.println(((StringBuffer)(new Object("AnimationViewport.render(): Unsupported node type: "))).append(type).toString());
               }
            }

            if (!doRender) {
               this._model._nodes[bitsIdx] = this._model._nodes[bitsIdx] | bits & 0xFF000000;
            }

            switch (type) {
               case 34:
               case 36:
               case 40:
               case 50:
                  if ((ctx.getDirtyOR() & 134217728) != 0) {
                     ctx.updateTransformMatrix();
                  }
                  break;
               case 42:
               case 44:
                  if (viewboxContext != null && (viewboxContext.getDirtyOR() & 134217728) != 0) {
                     viewboxContext.updateTransformMatrix();
                     return;
                  }
            }
      }
   }

   private final void updateSVG(int idx, MEGraphics2dContext ctx, boolean doRender) {
      if (idx == this._model._visualRoot) {
         this._baseClip.set(ctx.getClip());
         this._baseClip
            .set(Fixed32.toInt(this._baseClip.x), Fixed32.toInt(this._baseClip.y), Fixed32.toInt(this._baseClip.width), Fixed32.toInt(this._baseClip.height));
      }

      if (ctx.getDirtyBits() == 0 && (this._dirty & 128) == 0) {
         doRender &= this._model._nodes[idx + 23] > 0;
         doRender &= this._model._nodes[idx + 24] > 0;
      } else {
         this.setContainerNodeData(idx, ctx);
         doRender &= this.setViewportNodeData(idx, ctx);
         int textAttrIdx = this._model._nodes[idx + 29];
         this.setTextAttrData(textAttrIdx, ctx);
         MEGraphics2dContext viewboxContext = this.getViewboxContext(idx);
         if (viewboxContext != null) {
            doRender &= this.setViewboxData(idx, viewboxContext);
            this.setAspectRatioData(idx, viewboxContext);
            this.resetViewboxData(viewboxContext);
         }
      }

      ctx.setDisplayable(doRender);

      for (int child = this._model._nodes[idx + 6]; child != -1; child = this._model._nodes[child + 4]) {
         this.update(child, doRender);
      }
   }

   private final void updateGroup(int idx, MEGraphics2dContext ctx, boolean doRender) {
      if (ctx.getDirtyBits() != 0 || (this._dirty & 128) != 0) {
         this.setContainerNodeData(idx, ctx);
         int textAttrIdx = this._model._nodes[idx + 23];
         this.setTextAttrData(textAttrIdx, ctx);
      }

      ctx.setDisplayable(doRender);
      int currChild = this._model._nodes[idx + 24];
      int actChild;
      if (currChild != -2 && currChild != -1) {
         actChild = this.getChildNodeIndex(currChild, idx);
         if (actChild == -1) {
            actChild = currChild;
         }
      } else {
         actChild = -1;
      }

      for (int child = this._model._nodes[idx + 6]; child != -1; child = this._model._nodes[child + 4]) {
         switch (currChild) {
            case -3:
               this.update(child, doRender && child == actChild);
               break;
            case -2:
            default:
               this.update(child, doRender);
               break;
            case -1:
               this.update(child, false);
         }
      }
   }

   private final void updateLeafNode(int idx, MEGraphics2dContext ctx, int contextIndex, boolean doRender) {
      int leafNodeIdx = this._contextRenderIdx[contextIndex];
      LeafNode leafNode = this._renderLeafs[leafNodeIdx];
      if (ctx.getDirtyBits() != 0 || (this._dirty & 128) != 0) {
         this.setVisualNodeData(idx, ctx);
         this.setShapeNodeData(idx, ctx);
         int bits = this._model._nodes[idx + 8];
         if ((bits & 1024) != 0) {
            int hints = this._model._nodes[idx + 14];
            this.setShapeRenderingHints(hints, ctx);
         }

         leafNode.update();
      }

      if (ctx.getDirtyOR() != 0) {
         this.computeVisibilityAddDirtyRect(idx, leafNodeIdx, leafNode, doRender, false);
      }

      ctx.setDisplayable(doRender);
   }

   private final void updateImage(int idx, MEGraphics2dContext ctx, int contextIndex, boolean doRender) {
      int leafNodeIdx = this._contextRenderIdx[contextIndex];
      LeafNode leafNode = this._renderLeafs[leafNodeIdx];
      int bits = this._model._nodes[idx + 8];
      boolean bRenderBuffer = leafNode._renderToBuffer;
      if (ctx.getDirtyBits() != 0 || (this._dirty & 128) != 0) {
         this.setVisualNodeData(idx, ctx);
         this.setShapeNodeData(idx, ctx);
         doRender &= this.setViewportNodeData(idx, ctx);
         if ((bits & 1024) != 0) {
            int hints = this._model._nodes[idx + 14];
            this.setImageRenderingHints(hints, ctx);
         }

         MEGraphics2dContext viewboxContext = this.getViewboxContext(idx);
         if (viewboxContext != null) {
            doRender &= this.setViewboxData(idx, viewboxContext);
            this.setAspectRatioData(idx, viewboxContext);
            this.resetViewboxData(viewboxContext);
         }

         leafNode.update();
         bRenderBuffer &= leafNode._isForeignObject && (ctx.getDirtyBits() & -218103809) != 0;
      }

      if (ctx.getDirtyOR() != 0) {
         this.computeVisibilityAddDirtyRect(idx, leafNodeIdx, leafNode, doRender, false);
      }

      ctx.setDisplayable(doRender);
      if (leafNode._isForeignObject && (!leafNode._renderToBuffer || bRenderBuffer)) {
         int imageIdx = this._model._nodes[idx + 29];
         Object fo = this._model._images[imageIdx];
         if (fo instanceof Object) {
            ((LayoutManager)fo).layout();
         }
      }

      if (bRenderBuffer) {
         leafNode.renderToBuffer();
      }
   }

   private final void updateText(int idx, MEGraphics2dContext ctx, int contextIndex, MEGraphics2dContext prevLeaf, boolean isTspan, boolean doRender) {
      int leafNodeIdx = this._contextRenderIdx[contextIndex];
      LeafNode leafNode = leafNodeIdx != -1 ? this._renderLeafs[leafNodeIdx] : null;
      int bits = this._model._nodes[idx + 8];
      boolean bRenderBuffer = leafNode != null && leafNode._renderToBuffer;
      if (ctx.getDirtyBits() != 0 || (this._dirty & 128) != 0) {
         this.setVisualNodeData(idx, ctx);
         this.setShapeNodeData(idx, ctx);
         if ((this._model._nodes[idx + 8] & 1024) != 0) {
            int hints = this._model._nodes[idx + 14];
            this.setTextRenderingHints(hints, ctx);
         }

         int textAttrIdx = this._model._nodes[idx + 23];
         this.setTextAttrData(textAttrIdx, ctx);
         if ((bits & 8192) != 0) {
            if (isTspan && this._model._nodes[idx + 10] == Integer.MAX_VALUE) {
               int x = prevLeaf == null ? Integer.MAX_VALUE : Integer.MIN_VALUE;
               ctx.setX(x);
            } else {
               ctx.setX(this._model._nodes[idx + 10]);
            }
         } else if (prevLeaf == null) {
            ctx.setX(Integer.MAX_VALUE);
         } else {
            ctx.setX(Integer.MIN_VALUE);
         }

         if ((bits & 131072) != 0) {
            if (isTspan && this._model._nodes[idx + 11] == Integer.MAX_VALUE) {
               int y = prevLeaf == null ? Integer.MAX_VALUE : Integer.MIN_VALUE;
               ctx.setY(y);
            } else {
               ctx.setY(this._model._nodes[idx + 11]);
            }
         } else if (prevLeaf == null) {
            ctx.setY(Integer.MAX_VALUE);
         } else {
            ctx.setY(Integer.MIN_VALUE);
         }

         ctx.setDx(this._model._nodes[idx + 26]);
         ctx.setDy(this._model._nodes[idx + 27]);
         if ((bits & 262144) != 0) {
            ctx.setWidth(this._model._nodes[idx + 28], 0);
         }

         if (!isTspan) {
            ctx.setX(ctx.getX() == Integer.MIN_VALUE ? ctx.getDx() : ctx.getX() + ctx.getDx());
            ctx.setY(ctx.getY() == Integer.MIN_VALUE ? ctx.getDy() : ctx.getY() + ctx.getDy());
         }

         if (leafNode != null) {
            leafNode.update();
         }

         bRenderBuffer &= (ctx.getDirtyBits() & -218103809) != 0;
      }

      ctx.setDisplayable(doRender);

      for (int childIdx = this._model._nodes[idx + 6]; childIdx != -1; childIdx = this._model._nodes[childIdx + 4]) {
         MEGraphics2dContext childCtx = this.getContext(childIdx);
         int childCtxIndex = this.getContextIndex(childIdx);
         childCtx.invalidate();
         int bitsIdx = childIdx + 8;
         bits = this._model._nodes[bitsIdx];
         if (doRender) {
            this._model._nodes[bitsIdx] = this._model._nodes[bitsIdx] & ~(0xFF000000 & bits);
         }

         childCtx.setDirtyBits(bits);
         this.updateText(childIdx, childCtx, childCtxIndex, prevLeaf, true, doRender);
         prevLeaf = childCtx;
      }

      if (ctx.getDirtyOR() != 0 && leafNode != null) {
         this.computeVisibilityAddDirtyRect(idx, leafNodeIdx, leafNode, doRender, isTspan);
      }

      if (bRenderBuffer && leafNode != null) {
         leafNode.renderToBuffer();
      }
   }

   private final void updateForeignObject(int idx, MEGraphics2dContext ctx, int contextIndex, boolean doRender) {
      int leafNodeIdx = this._contextRenderIdx[contextIndex];
      LeafNode leafNode = this._renderLeafs[leafNodeIdx];
      boolean bRenderBuffer = leafNode._renderToBuffer;
      if (ctx.getDirtyBits() != 0 || (this._dirty & 128) != 0) {
         this.setVisualNodeData(idx, ctx);
         this.setShapeNodeData(idx, ctx);
         doRender &= this.setViewportNodeData(idx, ctx);
         MEGraphics2dContext viewboxContext = this.getViewboxContext(idx);
         if (viewboxContext != null) {
            doRender &= this.setViewboxData(idx, viewboxContext);
            this.setAspectRatioData(idx, viewboxContext);
            this.resetViewboxData(viewboxContext);
         }

         bRenderBuffer &= (ctx.getDirtyBits() & -218103809) != 0;
      }

      if (ctx.getDirtyOR() != 0) {
         this.computeVisibilityAddDirtyRect(idx, leafNodeIdx, leafNode, doRender, false);
      }

      ctx.setDisplayable(doRender);
      if (bRenderBuffer) {
         leafNode.renderToBuffer();
      }
   }

   private final void computeVisibilityAddDirtyRect(int idx, int leafNodeIdx, LeafNode leafNode, boolean doRender, boolean isTspan) {
      boolean wasVisible = leafNode._isVisible;
      if (leafNodeIdx < this._firstDirtyLeafIdx) {
         this._firstDirtyLeafIdx = leafNodeIdx;
      }

      if (!doRender) {
         leafNode._isVisible = false;
         if (wasVisible) {
            leafNode.unionWithBoundingBox(this._dirtyRect, this._baseClip);
         }
      } else {
         leafNode.computeDirtyRect(this._baseClip);
         boolean onScreen = isTspan || this._baseClip.intersects(leafNode._boundingBox);
         leafNode._isVisible = onScreen && leafNode._context.getVisibility() != 0;
         if (leafNode._isVisible || wasVisible) {
            leafNode.unionWithDirtyRect(this._dirtyRect, this._baseClip);
            return;
         }
      }
   }

   private final int addRenderContext(LeafNode renderLeaf, int nCtxList) {
      if (renderLeaf._context._type == 44 || renderLeaf._context._type == 42 && ((LeafNodeImage)renderLeaf)._isForeignObject) {
         if (nCtxList > 0) {
            this._meGraphic.drawContextList(this._leafContextList, nCtxList);
         }

         renderLeaf.render();
         return 0;
      } else {
         this._leafContextList[nCtxList] = renderLeaf._context;
         return nCtxList + 1;
      }
   }

   private final void updateViewbox(boolean getPanAmount) {
      int viewboxAttrIdx = this._model._nodes[this._model._visualRoot + 28];
      if (viewboxAttrIdx != -1) {
         int x = this._model._nodes[viewboxAttrIdx + 3];
         int y = this._model._nodes[viewboxAttrIdx + 4];
         int width = this._model._nodes[viewboxAttrIdx + 5];
         int height = this._model._nodes[viewboxAttrIdx + 6];
         if (this._viewboxInitialized) {
            if (getPanAmount && width == this._viewbox[2] && height == this._viewbox[3]) {
               int delta = this._viewbox[0] - x;
               if (delta != 0) {
                  this._panXDelta = (delta >> 16) * (this._virtualWidth >> 16) / (width >> 16) << 16;
               }

               delta = this._viewbox[1] - y;
               if (delta != 0) {
                  this._panYDelta = (delta >> 16) * (this._virtualHeight >> 16) / (height >> 16) << 16;
               }
            }
         } else {
            this._viewboxInitialized = true;
         }

         this._viewbox[0] = x;
         this._viewbox[1] = y;
         this._viewbox[2] = width;
         this._viewbox[3] = height;
      }
   }
}
