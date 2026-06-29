package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.PMEGraphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.MediaViewport;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.ImageForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.LayoutManager;
import net.rim.plazmic.internal.mediaengine.ui.PME12Graphics;
import net.rim.plazmic.internal.mediaengine.ui.Pannable;
import net.rim.plazmic.internal.mediaengine.ui.Zoomable;
import net.rim.plazmic.internal.mediaengine.util.Platform;

public class PMEGraphicsViewport implements MediaViewport, Pannable, Zoomable {
   private AnimationModel _model;
   private ModelInteractorImpl _modelInteractor;
   public int _bkgColor = 16777215;
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
   private Platform _platform = MediaFactory.getPlatform();
   private PME12Graphics _meGraphic;
   protected MediaServices _services;
   private byte _dirty;
   private boolean _forceInvalidate;
   private int _virtualWidth;
   private int _virtualHeight;
   private XYRect _dirtyRect;
   private XYRect _baseClip;
   private XYRect _paintClip;
   private int _fpWidth;
   private int _fpHeight;
   PMEGraphics _pmeGraphics;
   private PMEGraphicsViewport$PMEConvertedImages _images;
   char[][] _convertedTextStrings;
   private int[] _stats;
   private int _renderLogState;
   private int _renderLogNode;
   private int _updateLogState;
   private int _updateLogNode;
   private int _paintLock;
   private static final int MIN_ZOOM_AMOUNT = 16;
   private static final int MAX_ZOOM_AMOUNT = 3276800;
   private static final int DEFAULT_ZOOM_FACTOR = 65536;
   private static final int DIRTY_ZOOM = 1;
   private static final int DIRTY_ZOOM_ORIGIN_X = 2;
   private static final int DIRTY_ZOOM_ORIGIN_Y = 4;
   private static final int DIRTY_ALIGN_X = 8;
   private static final int DIRTY_ALIGN_Y = 16;
   private static final int DIRTY_PAN_X = 32;
   private static final int DIRTY_PAN_Y = 64;
   private static final int DIRTY_ALL = 128;

   public PMEGraphicsViewport() {
      this._alignmentMatrix = new int[9];
      this._rootSVGMatrix = new int[9];
      this.resetZoomPan();
      this._platform.setIdentity(this._alignmentMatrix, 0);
      this._platform.setIdentity(this._rootSVGMatrix, 0);
      this._pmeGraphics = (PMEGraphics)(new Object());
      this._dirtyRect = (XYRect)(new Object());
      this._paintClip = (XYRect)(new Object());
      this._baseClip = (XYRect)(new Object());
   }

   public PMEGraphicsViewport(Object m) {
      this();
      this.setMedia(m);
   }

   @Override
   public Object getMedia() {
      return this._model;
   }

   @Override
   public void setMedia(Object media) {
      this.resetZoomPan();
      if (media != null) {
         this._model = (AnimationModel)media;
         if (media instanceof ModelInteractorImpl) {
            this._modelInteractor = (ModelInteractorImpl)media;
         } else {
            this._modelInteractor = null;
         }

         this._stats = new int[8];
         int iStats = 0;
         this._stats[iStats] = 14;
         iStats += 4;
         this._stats[iStats] = 15;
         this.setPMEGraphics();
         this._dirty = (byte)(this._dirty | 128);
      }
   }

   private void setPMEGraphics() {
      synchronized (this._pmeGraphics) {
         boolean bChangedNodes = this._pmeGraphics.setNodes(this._model._nodes);
         boolean bChangedCoords = this._pmeGraphics.setCoords(this._model._coords);
         boolean bChangedTypes = this._pmeGraphics.setPointTypes(this._model._pointTypes);
         boolean bChangedFonts = this._pmeGraphics.setFontFamilies(this._model._platformFontFamilyStrings);
         boolean bChangedFO = this._pmeGraphics.setForeignObjects(this._model._foreignObjects);
         this._pmeGraphics.setDefaultFontFamily("BBMillbank");
         this._pmeGraphics.setRootIndex(this._model._visualRoot);
         this._pmeGraphics.setTransform(this._alignmentMatrix);
         this._pmeGraphics.setViewport(0, 0, this._fpWidth, this._fpHeight);
         if (PMEGraphics.logStats()) {
            this._pmeGraphics.setStats(this._stats);
         } else {
            this._pmeGraphics.setStats(null);
         }

         if (this._model._images != null) {
            int nImages = this._model._images.length;
            if (this._images == null) {
               this._images = new PMEGraphicsViewport$PMEConvertedImages(this, nImages);
            } else {
               this._images.grow(nImages);
            }

            for (int iImage = 0; iImage < nImages; iImage++) {
               this._images.setImage(this._model._images[iImage], iImage);
            }
         } else {
            this._images = null;
         }

         boolean bChangeImage = this._images != null ? this._pmeGraphics.setImages(this._images._imageConverted) : this._pmeGraphics.setImages(null);
         if (this._model._convertedTextStrings != null) {
            synchronized (this._model._convertedTextStrings) {
               int nStrings = this._model._convertedTextStrings.length;
               if (this._convertedTextStrings == null || this._convertedTextStrings.length < nStrings) {
                  this._convertedTextStrings = new char[nStrings][];
               }

               int nChars = 0;

               for (int i = 0; i < nStrings; i++) {
                  if (this._model._convertedTextStrings[i] != null) {
                     nChars = this._model._convertedTextStrings[i].length;
                     if (this._convertedTextStrings[i] == null || this._convertedTextStrings[i].length != nChars) {
                        this._convertedTextStrings[i] = new char[nChars];
                     }

                     System.arraycopy(this._model._convertedTextStrings[i], 0, this._convertedTextStrings[i], 0, nChars);
                  }
               }
            }
         }

         boolean bChangedStrings = this._pmeGraphics.setStrings(this._convertedTextStrings);
         if (bChangedNodes || bChangedCoords || bChangedTypes || bChangedFonts || bChangedStrings || bChangeImage || bChangedFO) {
            this._dirty = (byte)(this._dirty | 128);
         }
      }
   }

   @Override
   public void setExtent(int width, int height) {
      int maxInt = Fixed32.toInt(Integer.MAX_VALUE);
      this._fpWidth = width > maxInt ? Fixed32.toFP(maxInt) : Fixed32.toFP(width);
      this._fpHeight = height > maxInt ? Fixed32.toFP(maxInt) : Fixed32.toFP(height);
      if (this._dirtyRect == null) {
         this._dirtyRect = (XYRect)(new Object(0, 0, width, height));
      } else {
         this._dirtyRect.set(0, 0, width, height);
      }

      if (this._paintClip == null) {
         this._paintClip = (XYRect)(new Object(0, 0, width, height));
      } else {
         this._paintClip.set(0, 0, this._fpWidth, this._fpHeight);
      }

      this.calculateVirtualExtent();
      if (this._baseClip == null) {
         this._baseClip = (XYRect)(new Object(0, 0, Fixed32.toInt(this._virtualWidth), Fixed32.toInt(this._virtualHeight)));
      } else {
         this._baseClip.set(0, 0, Fixed32.toInt(this._virtualWidth), Fixed32.toInt(this._virtualHeight));
      }
   }

   private void calculateVirtualExtent() {
      int rootSVG = this._model._visualRoot;
      int bits = this._model._nodes[rootSVG + 8];
      int widthSVG = this._model._nodes[rootSVG + 25];
      int heightSVG = this._model._nodes[rootSVG + 26];
      if ((bits & 16384) != 0) {
         this._virtualWidth = Fixed32.mul(this._fpWidth, widthSVG);
      } else {
         this._virtualWidth = widthSVG;
      }

      if ((bits & 32768) != 0) {
         this._virtualHeight = Fixed32.mul(this._fpHeight, heightSVG);
      } else {
         this._virtualHeight = heightSVG;
      }
   }

   @Override
   public final void paint(Object g) {
      this.paint(g, 0, 0, this._baseClip.width, this._baseClip.height, 0, 0);
   }

   @Override
   public final void paint(Object g, int clipX, int clipY, int clipW, int clipH) {
      this.paint(g, clipX, clipY, clipW, clipH, 0, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void paint(Object g, int clipX, int clipY, int clipW, int clipH, int gOffsetX, int gOffsetY) {
      if (this._dirty != 0 || this._forceInvalidate) {
         this.invalidate();
      }

      this._pmeGraphics.startStats(15);
      this._pmeGraphics.setClip(clipX, clipY, clipW, clipH);
      if (this._model._hasbkgColor) {
         this._pmeGraphics.setBackgroundColour(this._model._bkgColor);
         this._pmeGraphics.clear(g);
      }

      int nodeIdx = -1;
      boolean var14 = false /* VF: Semaphore variable */;
      boolean var17 = false /* VF: Semaphore variable */;

      label117: {
         label116: {
            try {
               label114:
               try {
                  var17 = true;
                  var14 = true;
                  this._paintLock++;

                  while (this._pmeGraphics.renderLeafNodes(g, nodeIdx) == 1) {
                     nodeIdx = this._pmeGraphics.getLastNode();
                     this._pmeGraphics.setFillContext(g);
                     nodeIdx = this.paintForeignObjects(g, nodeIdx);
                     if (nodeIdx == -1) {
                        var14 = false;
                        var17 = false;
                        break label116;
                     }
                  }

                  var14 = false;
                  var17 = false;
                  break label116;
               } finally {
                  if (var17) {
                     if (this._pmeGraphics.getLastReturn() == this._renderLogState && this._pmeGraphics.getLastNode() == this._renderLogNode) {
                        var14 = false;
                     } else {
                        String msg = ((StringBuffer)(new Object("PMEGraphics render fail: ")))
                           .append(this._pmeGraphics.getLastReturn())
                           .append(" on node: ")
                           .append(this._pmeGraphics.getLastNode())
                           .toString();
                        EventLogger.logEvent(-7509200465648525729L, msg.getBytes(), 3);
                        var14 = false;
                     }
                     break label114;
                  }
               }
            } finally {
               if (var14) {
                  this._paintLock--;
               }
            }

            this._paintLock--;
            break label117;
         }

         this._paintLock--;
      }

      this._renderLogState = this._pmeGraphics.getLastReturn();
      this._renderLogNode = this._pmeGraphics.getLastNode();
      this._pmeGraphics.stopStats(15);
      if (PMEGraphics.showClip()) {
         this._pmeGraphics.renderClip(g);
      }

      if (PMEGraphics.logStats()) {
         this._pmeGraphics.renderStats(g);
      }

      this._dirtyRect.set(0, 0, 0, 0);
   }

   private int paintForeignObjects(Object g, int nodeIdx) {
      ForeignObject fo = null;
      int nextNode = nodeIdx;
      nodeIdx = nextNode;
      int type = this._model._nodes[nodeIdx + 1];
      if (type == 42) {
         int imageIdx = this._model._nodes[nodeIdx + 29];
         Object image = this._model._images[imageIdx];
         if (image instanceof ImageForeignObject) {
            ImageForeignObject foImage = (ImageForeignObject)image;
            if (!foImage.isReady()) {
               foImage.setHandle(nodeIdx);
               foImage.setPosition(Fixed32.toInt(this._model._nodes[nodeIdx + 10]), Fixed32.toInt(this._model._nodes[nodeIdx + 11]));
               foImage.setExtent(Fixed32.toInt(this._model._nodes[nodeIdx + 23]), Fixed32.toInt(this._model._nodes[nodeIdx + 24]));
               foImage.setReady();
            }
         }

         fo = (ForeignObject)image;
      } else {
         int foIdx = this._model._nodes[nodeIdx + 29];
         if (foIdx != -1) {
            fo = this._model._foreignObjects[foIdx];
         }
      }

      if (fo != null) {
         XYRect viewport = (XYRect)(new Object(
            this._model._nodes[nodeIdx + 17], this._model._nodes[nodeIdx + 17 + 1], this._model._nodes[nodeIdx + 17 + 2], this._model._nodes[nodeIdx + 17 + 3]
         ));
         if (viewport != null && viewport.width > 0 && viewport.height > 0) {
            fo.setPosition(viewport.x, viewport.y);
            fo.setExtent(viewport.width, viewport.height);
            fo.draw(g, viewport.x, viewport.y);
         }
      }

      nextNode = this._pmeGraphics.getNextLeafNode(nodeIdx);
      if (nextNode == -1) {
         nodeIdx = -1;
      }

      return nodeIdx;
   }

   private boolean isForeignObject(int nodeIdx) {
      if (nodeIdx == -1) {
         return false;
      }

      int type = this._model._nodes[nodeIdx + 1];
      if (type == 44) {
         return true;
      }

      if (type != 42) {
         return false;
      }

      int imageIdx = this._model._nodes[nodeIdx + 29];
      Object image = this._model._images[imageIdx];
      return image != null && image instanceof Object;
   }

   private final void calculateAlignmentMatrix() {
      if (this._dirty != 0) {
         this._alignmentMatrix[0] = this._zoomFactor;
         this._alignmentMatrix[4] = this._zoomFactor;
         this._alignmentMatrix[2] = this._zoomOriginX - (int)((long)this._zoomFactor * (this._zoomOriginX - this._alignX) >> 16) + this._panXAmount;
         this._alignmentMatrix[5] = this._zoomOriginY - (int)((long)this._zoomFactor * (this._zoomOriginY - this._alignY) >> 16) + this._panYAmount;
      }
   }

   @Override
   public void invalidate(int x, int y, int width, int height) {
      this._dirtyRect.set(x, y, width, height);
      this.invalidate();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void invalidate() {
      if (this._paintLock != 0) {
         if (this._modelInteractor != null) {
            this._modelInteractor.triggerUpdate();
         }
      } else {
         this._pmeGraphics.startStats(14);
         if (this._dirty != 0) {
            this.calculateAlignmentMatrix();
            int bitsIdx = this._model._visualRoot + 8;
            this._model._nodes[bitsIdx] = this._model._nodes[bitsIdx] | 134217728;
         }

         if (this._model._images != null) {
            int len = this._model._images.length;

            for (int i = 0; i < len; i++) {
               Object image = this._model._images[i];
               if (image instanceof Object) {
                  ((LayoutManager)image).layout();
               }
            }
         }

         this.setPMEGraphics();
         boolean var5 = false /* VF: Semaphore variable */;

         label78:
         try {
            var5 = true;
            this._pmeGraphics.update(this._model._visualRoot);
            var5 = false;
         } finally {
            if (var5) {
               if (this._pmeGraphics.getLastReturn() != this._updateLogState || this._pmeGraphics.getLastNode() != this._updateLogNode) {
                  String msg = ((StringBuffer)(new Object("PMEGraphics update fail: ")))
                     .append(this._pmeGraphics.getLastReturn())
                     .append(" on  node: ")
                     .append(this._pmeGraphics.getLastNode())
                     .toString();
                  EventLogger.logEvent(-7509200465648525729L, msg.getBytes(), 3);
               }
               break label78;
            }
         }

         this._updateLogState = this._pmeGraphics.getLastReturn();
         this._updateLogNode = this._pmeGraphics.getLastNode();
         if (this._dirtyRect.width != 0 && this._dirtyRect.height != 0) {
            this._dirtyRect.union(this._pmeGraphics.getDirtyBounds());
         } else {
            this._dirtyRect.set(this._pmeGraphics.getDirtyBounds());
         }

         this._dirty = 0;
         this._forceInvalidate = false;
         this._pmeGraphics.stopStats(14);
      }
   }

   @Override
   public void dirtyAll() {
      this._dirty = -128;
   }

   @Override
   public int getDirtyX() {
      return this._dirtyRect.x;
   }

   @Override
   public int getDirtyY() {
      return this._dirtyRect.y;
   }

   @Override
   public int getDirtyWidth() {
      return this._dirtyRect.width;
   }

   @Override
   public int getDirtyHeight() {
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
   public boolean isContentWidthAbsolute() {
      boolean result = false;
      if (this._model != null) {
         int bits = this._model._nodes[this._model._visualRoot + 8];
         result = (bits & 16384) == 0;
      }

      return result;
   }

   @Override
   public boolean isContentHeightAbsolute() {
      boolean result = false;
      if (this._model != null) {
         int bits = this._model._nodes[this._model._visualRoot + 8];
         result = (bits & 32768) == 0;
      }

      return result;
   }

   @Override
   public void setStyle(int style) {
      this._style = style;
   }

   @Override
   public int getStyle() {
      return this._style;
   }

   @Override
   public void setServices(MediaServices s) {
      this._services = s;

      try {
         this._meGraphic = (PME12Graphics)MediaFactory.createObjectFromRegistry(new String[]{"FRAMEWORK", "GRAPHICS"});
      } finally {
         return;
      }
   }

   @Override
   public void dispose() {
   }

   @Override
   public void setPanX(int amount) {
      this._panXDelta = this._panXDelta + (amount - this._panXAmount);
      this._panXAmount = amount;
      this._dirty = (byte)(this._dirty | 32);
   }

   @Override
   public void setPanY(int amount) {
      this._panYDelta = this._panYDelta + (amount - this._panYAmount);
      this._panYAmount = amount;
      this._dirty = (byte)(this._dirty | 64);
   }

   @Override
   public int getPanX() {
      return this._panXAmount;
   }

   @Override
   public int getPanY() {
      return this._panYAmount;
   }

   @Override
   public boolean isPannable() {
      return this._model._isZoomAndPannable;
   }

   @Override
   public void getPanBounds(XYEdges theEdge) {
   }

   @Override
   public void setZoomAmount(int zoomFactor) {
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
   public int getZoomAmount() {
      return this._zoomFactor;
   }

   @Override
   public void setZoomOriginX(int xOrigin) {
      this._zoomOriginX = xOrigin;
      this._dirty = (byte)(this._dirty | 2);
   }

   @Override
   public void setZoomOriginY(int yOrigin) {
      this._zoomOriginY = yOrigin;
      this._dirty = (byte)(this._dirty | 4);
   }

   @Override
   public int getZoomOriginX() {
      return this._zoomOriginX;
   }

   @Override
   public int getZoomOriginY() {
      return this._zoomOriginY;
   }

   @Override
   public boolean isZoomable() {
      return this._model._isZoomAndPannable;
   }

   private void resetZoomPan() {
      this._panXDelta = -this._panXAmount;
      this._panXAmount = 0;
      this._panYDelta = -this._panYAmount;
      this._panYAmount = 0;
      this._zoomFactor = 65536;
      this._dirty = 97;
   }

   @Override
   public int getOriginX() {
      return this._alignmentMatrix[2] == 0 ? this._alignX >> 16 : this._alignmentMatrix[2] >> 16;
   }

   @Override
   public int getOriginY() {
      return this._alignmentMatrix[5] == 0 ? this._alignY >> 16 : this._alignmentMatrix[5] >> 16;
   }

   @Override
   public void setOrigin(int x, int y) {
      this._alignX = x << 16;
      this._alignY = y << 16;
      this._dirty = (byte)(this._dirty | 24);
   }

   @Override
   public int getAlignX() {
      return this._alignX >> 16;
   }

   @Override
   public int getAlignY() {
      return this._alignY >> 16;
   }
}
