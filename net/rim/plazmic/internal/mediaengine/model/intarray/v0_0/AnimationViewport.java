package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0;

import net.rim.device.api.ui.XYEdges;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.MediaViewport;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2d;
import net.rim.plazmic.internal.mediaengine.ui.Pannable;
import net.rim.plazmic.internal.mediaengine.util.Platform;

public final class AnimationViewport implements MediaViewport, Pannable {
   private AnimationModel _model;
   private int _style;
   private int _originX;
   private int _originY;
   private int _panXAmount;
   private int _panYAmount;
   private Object[] _objects;
   private int[] _nodes;
   private int[][] _coords;
   private short[] _delta;
   private int _width;
   private int _height;
   private int _virtWidth;
   private int _virtHeight;
   private int _leftX;
   private int _rightX;
   private int _topY;
   private int _botY;
   private int _leftXOnScreen;
   private int _rightXOnScreen;
   private int _topYOnScreen;
   private int _botYOnScreen;
   private int _offsetX;
   private int _offsetY;
   private int _translateX;
   private int _translateY;
   private int _dirtyX1;
   private int _dirtyX2;
   private int _dirtyY1;
   private int _dirtyY2;
   private int _clipX;
   private int _clipY;
   private int _clipW;
   private int _clipH;
   private boolean _allClean;
   private boolean _allDirty;
   private boolean _firstPass;
   private Object _staticBuffer;
   private boolean _doneStatic = false;
   private boolean _transparent = false;
   private boolean _doneFirstDirtyCalc = false;
   Platform _platform;
   private int visindent = 0;
   protected MediaServices _services;
   private MEGraphics2d _meGraphic;
   public static final int DELTA_Y_OFFSET = 1;
   public static final int DELTA_WIDTH_OFFSET = 2;
   public static final int DELTA_HEIGHT_OFFSET = 3;
   public static final int DELTA_X_COORDS_OFFSET = 4;
   public static final int DELTA_Y_COORDS_OFFSET = 5;

   protected final void findHiddenInterpolator(int index) {
   }

   protected final void checkModel() {
      if (this._nodes != this._model.getNodes()) {
         this.setMedia(this._model);
      }
   }

   @Override
   public final Object getMedia() {
      return this._model;
   }

   @Override
   public final synchronized void setMedia(Object media) {
      this._firstPass = true;
      if (media == null) {
         this._model = null;
         this._objects = null;
         this._nodes = null;
         this._coords = (int[][])null;
         this._delta = null;
         this._virtWidth = this._virtHeight = 0;
      } else {
         if (media != this._model || this._model != null && this._nodes != this._model.getNodes()) {
            this._model = (AnimationModel)media;
            this._virtWidth = this._model.getWidth();
            this._virtHeight = this._model.getHeight();
            this._delta = this._model.delta;
            this._objects = this._model.getObjects();
            this._nodes = this._model.getNodes();
            this._coords = this._model.getPolygonCoords();
            this.resetClip();
         }
      }
   }

   @Override
   public final void setExtent(int width, int height) {
      this._firstPass = true;
      if (width > 0 && height > 0 && (width != this._width || height != this._height)) {
         this._width = width;
         this._height = height;
         this.resetClip();
      }
   }

   @Override
   public final void invalidate() {
      if (this._model != null && this._model.started) {
         this.checkModel();
         if (!this._firstPass) {
            this._allDirty = false;
            this._allClean = true;
            this.calculateDirty(0, this._originX + (this._panXAmount >> 16), this._originY + (this._panYAmount >> 16));
            if (this._allClean && !this._allDirty) {
               return;
            }
         } else {
            this._firstPass = false;
            this._allDirty = false;
            this.calculateDirty(0, this._originX + (this._panXAmount >> 16), this._originY + (this._panYAmount >> 16));
            this._allDirty = true;
         }
      }
   }

   @Override
   public final void paint(Object g) {
      this.paint(g, this.getDirtyX(), this.getDirtyY(), this.getDirtyWidth(), this.getDirtyHeight(), 0, 0);
   }

   @Override
   public final void paint(Object g, int clipX, int clipY, int clipW, int clipH) {
      this.paint(g, clipX, clipY, clipW, clipH, 0, 0);
   }

   @Override
   public final void paint(Object g, int clipX, int clipY, int clipW, int clipH, int gOffsetX, int gOffsetY) {
      if (this._model != null) {
         if (this._meGraphic == null) {
            this._meGraphic = this._platform.createGraphics(g);
         }

         this._meGraphic.setGraphics(g);
         this._meGraphic.setStrokeWidth(1);
         this._meGraphic.setStrokeLinecap(16);
         this._meGraphic.setStrokeLinejoin(1);
         this._clipX = clipX;
         this._clipY = clipY;
         this._clipW = clipW;
         this._clipH = clipH;
         this.checkModel();
         if (!this._doneFirstDirtyCalc && this._model.started) {
            this._allDirty = false;
            this.calculateDirty(0, this._originX + (this._panXAmount >> 16), this._originY + (this._panYAmount >> 16));
            this._allDirty = true;
            this._doneFirstDirtyCalc = true;
         }

         this._offsetX = this._offsetY = 0;
         if (!this._transparent) {
            this._meGraphic.setColor(this._model.getBkgColor());
            this._meGraphic.fillRect(gOffsetX, gOffsetY, this._width, this._height);
         }

         if (this._model.started) {
            this.renderVisualNode(0, g, this._originX + gOffsetX + (this._panXAmount >> 16), this._originY + gOffsetY + (this._panYAmount >> 16));
            this._model.startRenderSound();
         }

         if (!this._transparent) {
            this._meGraphic.setColor(this._model.getBkgColor());
            if (this._leftX > 0) {
               this._meGraphic.fillRect(gOffsetX, gOffsetY, this._leftX, this._height);
            }

            if (this._rightX < this._width) {
               this._meGraphic.fillRect(gOffsetX + this._rightX, gOffsetY, this._width - this._rightX, this._height);
            }

            if (this._topY > 0) {
               this._meGraphic.fillRect(gOffsetX, gOffsetY, this._width, this._topY);
            }

            if (this._botY < this._height) {
               this._meGraphic.fillRect(gOffsetX, gOffsetY + this._botY, this._width, this._height - this._botY);
            }
         }
      }
   }

   @Override
   public final void invalidate(int x, int y, int width, int height) {
      this.setDirty(x, y, width, height);
      this.invalidate();
   }

   @Override
   public final int getVirtualHeight() {
      return this._virtHeight;
   }

   @Override
   public final int getVirtualWidth() {
      return this._virtWidth;
   }

   @Override
   public final boolean isContentWidthAbsolute() {
      return true;
   }

   @Override
   public final boolean isContentHeightAbsolute() {
      return true;
   }

   @Override
   public final void dirtyAll() {
      this._allDirty = true;
   }

   @Override
   public final int getDirtyX() {
      if (this._allDirty) {
         return this._leftXOnScreen;
      } else {
         return this._allClean ? 0 : this._dirtyX1;
      }
   }

   @Override
   public final int getDirtyY() {
      if (this._allDirty) {
         return this._topYOnScreen;
      } else {
         return this._allClean ? 0 : this._dirtyY1;
      }
   }

   @Override
   public final int getDirtyWidth() {
      if (this._allDirty) {
         return this._rightXOnScreen - this._leftXOnScreen + 1;
      } else {
         return this._allClean ? 0 : this._dirtyX2 - this._dirtyX1 + 1;
      }
   }

   @Override
   public final int getDirtyHeight() {
      if (this._allDirty) {
         return this._botYOnScreen - this._topYOnScreen + 1;
      } else {
         return this._allClean ? 0 : this._dirtyY2 - this._dirtyY1 + 1;
      }
   }

   @Override
   public final void setStyle(int style) {
      this._style = style;
      if ((style & 1) == 1) {
         this._transparent = true;
         this._doneStatic = false;
         this._firstPass = true;
         this._allDirty = true;
      } else {
         this._transparent = false;
         this._doneStatic = false;
         this._firstPass = true;
         this._allDirty = true;
      }

      this.resetClip();
   }

   @Override
   public final int getStyle() {
      return this._style;
   }

   @Override
   public final int getOriginX() {
      return this._originX;
   }

   @Override
   public final int getOriginY() {
      return this._originY;
   }

   @Override
   public final void setOrigin(int x, int y) {
      if (this._originX != x || this._originY != y) {
         this._originX = x;
         this._originY = y;
         this._doneStatic = false;
         this._firstPass = true;
         this.setDirty(x, y, this._width, this._height);
         this.resetClip();
      }
   }

   @Override
   public final void setServices(MediaServices s) {
      this._services = s;
   }

   @Override
   public final void dispose() {
   }

   @Override
   public final boolean isPannable() {
      return true;
   }

   @Override
   public final void setPanX(int amount) {
      this._panXAmount = amount;
      this._doneStatic = false;
      this._firstPass = true;
      this.setDirty(this._originX + (this._panXAmount >> 16), this._originY + (this._panYAmount >> 16), this._width, this._height);
      this.resetClip();
   }

   @Override
   public final void setPanY(int amount) {
      this._panYAmount = amount;
      this._doneStatic = false;
      this._firstPass = true;
      this.setDirty(this._originX + (this._panXAmount >> 16), this._originY + (this._panYAmount >> 16), this._width, this._height);
      this.resetClip();
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
   public final void getPanBounds(XYEdges theEdge) {
   }

   @Override
   public final int getAlignX() {
      return this._originX;
   }

   @Override
   public final int getAlignY() {
      return this._originY;
   }

   private final void calculateDirty(int index, int xlateX, int xlateY) {
      if (!this._allDirty) {
         int type = this._nodes[index];
         int nodesPos = index + 1;
         if (type == 50) {
            int numChildren = this._nodes[index + 5];

            for (int i = numChildren - 1; i >= 0; i--) {
               int child = this._nodes[index + 6 + i];
               this._nodes[child + 1] = this._nodes[child + 1] | this._nodes[nodesPos] & 1;
               this.calculateDirty(child, xlateX + this._nodes[index + 2], xlateY + this._nodes[index + 3]);
            }

            this._nodes[nodesPos] = this._nodes[nodesPos] & -2;
         } else if ((this._nodes[nodesPos] & 1) != 0) {
            if (type == 40) {
               int imgIndex = this._nodes[index + 4];
               if (this._objects[imgIndex] != null) {
                  Object img = this._objects[imgIndex];
                  int idx = this._nodes[nodesPos] >> 16;
                  this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
                  this._delta[idx] = (short)(xlateX + this._nodes[index + 2]);
                  this._delta[idx + 1] = (short)(xlateY + this._nodes[index + 3]);
                  if (this._delta[idx + 4] == 0) {
                     this._delta[idx + 2] = (short)this._platform.getImageWidth(img);
                     this._delta[idx + 3] = (short)this._platform.getImageHeight(img);
                     this._delta[idx + 4] = 1;
                  }

                  this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
               }

               this._nodes[nodesPos] = this._nodes[nodesPos] & -2;
            } else if (type != 10) {
               if (type != 20) {
                  if (type != 30) {
                     if (type == 60) {
                        int idx = this._nodes[nodesPos] >> 16;
                        this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
                        this._delta[idx] = (short)(xlateX + this._nodes[index + 2] - this._nodes[index + 10]);
                        this._delta[idx + 1] = (short)(xlateY + this._nodes[index + 3] - this._nodes[index + 11]);
                        this._delta[idx + 2] = (short)(this._nodes[index + 10] << 1);
                        this._delta[idx + 3] = (short)(this._nodes[index + 11] << 1);
                        this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
                        this._nodes[nodesPos] = this._nodes[nodesPos] & -2;
                     }
                  } else {
                     int idx = this._nodes[nodesPos] >> 16;
                     int fontType = this._nodes[index + 7];
                     int textIdx = this._nodes[index + 8];
                     int textWidth = this._meGraphic.getFontAdvance(fontType, (String)this._objects[textIdx]);
                     this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
                     this._delta[idx] = (short)(xlateX + this._nodes[index + 2]);
                     this._delta[idx + 1] = (short)(xlateY + this._nodes[index + 3] - this._meGraphic.getFontAscent(fontType) - 1);
                     this._delta[idx + 2] = (short)textWidth;
                     this._delta[idx + 3] = (short)this._meGraphic.getFontHeight(fontType);
                     this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
                     this._nodes[nodesPos] = this._nodes[nodesPos] & -2;
                  }
               } else {
                  int xPos = xlateX + this._nodes[index + 2];
                  int yPos = xlateY + this._nodes[index + 3];
                  int idx = this._nodes[nodesPos] >> 16;
                  if (this._delta[idx + 2] == 0) {
                     int[] xCor = this._coords[this._nodes[index + 10]];
                     int[] yCor = this._coords[this._delta[idx + 5]];
                     int numPoints = xCor.length;
                     if (numPoints == 0) {
                        return;
                     }

                     int px2;
                     int px1 = px2 = xCor[0];
                     int py2;
                     int py1 = py2 = yCor[0];

                     for (int i = xCor.length - 1; i > 0; i--) {
                        int curX = xCor[i];
                        int curY = yCor[i];
                        if (curX > px2) {
                           px2 = curX;
                        } else if (curX < px1) {
                           px1 = curX;
                        }

                        if (curY > py2) {
                           py2 = curY;
                        } else if (curY < py1) {
                           py1 = curY;
                        }
                     }

                     this._delta[idx] = (short)px1;
                     this._delta[idx + 1] = (short)py1;
                     this._delta[idx + 2] = (short)(px2 - px1);
                     this._delta[idx + 3] = (short)(py2 - py1);
                     this._delta[idx + 4] = (short)xPos;
                     this._delta[idx + 5] = (short)yPos;
                     if (this._delta[idx + 2] == 0) {
                        this._delta[idx + 2] = 1;
                     }
                  }

                  this.setDirty(
                     (short)(this._delta[idx + 4] + this._delta[idx]),
                     (short)(this._delta[idx + 5] + this._delta[idx + 1]),
                     this._delta[idx + 2],
                     this._delta[idx + 3]
                  );
                  this._delta[idx + 4] = (short)xPos;
                  this._delta[idx + 5] = (short)yPos;
                  this.setDirty((short)(xPos + this._delta[idx]), (short)(yPos + this._delta[idx + 1]), this._delta[idx + 2], this._delta[idx + 3]);
                  this._nodes[nodesPos] = this._nodes[nodesPos] & -2;
               }
            } else {
               int idx = this._nodes[nodesPos] >> 16;
               this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
               this._delta[idx] = (short)(xlateX + this._nodes[index + 2]);
               this._delta[idx + 1] = (short)(xlateY + this._nodes[index + 3]);
               this._delta[idx + 2] = (short)this._nodes[index + 10];
               this._delta[idx + 3] = (short)this._nodes[index + 11];
               this.setDirty(this._delta[idx], this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
               this._nodes[nodesPos] = this._nodes[nodesPos] & -2;
            }
         }
      }
   }

   public AnimationViewport() {
      this._dirtyX1 = this._dirtyX2 = 0;
      this._dirtyY1 = this._width;
      this._dirtyY2 = this._height;
      this._firstPass = this._allClean = this._allDirty = true;
      this._offsetX = this._offsetY = 0;
      this._translateX = this._translateY = 0;
      this._platform = MediaFactory.getPlatform();
      this._meGraphic = this._platform.createGraphics(null);
      this._staticBuffer = this._staticBuffer;
      this._doneStatic = this._doneStatic;
   }

   private final void renderVisualNode(int index, Object g, int xlateX, int xlateY) {
      int type = this._nodes[index];
      this.visindent = this.visindent;
      if (this._model.bitsAreSet(index, 128)) {
         xlateX += this._nodes[index + 2];
         xlateY += this._nodes[index + 3];
         int oldAbsX = this._offsetX;
         int oldAbsY = this._offsetY;
         this._offsetX = xlateX;
         this._offsetY = xlateY;
         this._translateX = xlateX;
         this._translateY = xlateY;
         switch (type) {
            case 10:
            case 20:
            case 30:
            case 60:
               boolean visib = false;
               if (type == 30) {
                  visib = true;
               } else if (type == 10) {
                  visib = this.onScreen(this._offsetX, this._offsetY, this._nodes[index + 10], this._nodes[index + 11]);
               } else if (type == 20) {
                  int idx = this._nodes[index + 1] >> 16;
                  visib = this.onScreen(this._offsetX + this._delta[idx], this._offsetY + this._delta[idx + 1], this._delta[idx + 2], this._delta[idx + 3]);
               } else if (type == 60) {
                  visib = this.onScreen(
                     this._offsetX - this._nodes[index + 10],
                     this._offsetY - this._nodes[index + 11],
                     this._nodes[index + 10] << 1,
                     this._nodes[index + 11] << 1
                  );
               }

               if (visib) {
                  if (this._model.bitsAreSet(index, 32)) {
                     this.renderFill(index, g);
                  }

                  if (this._model.bitsAreSet(index, 64)) {
                     this.renderStroke(index, g);
                  }
               }
               break;
            case 40:
               int imgIndex = this._nodes[index + 4];
               if (this._objects[imgIndex] != null) {
                  label79:
                  try {
                     Object img = this._objects[imgIndex];
                     int idx = this._nodes[index + 1] >> 16;
                     if (this.onScreen(this._offsetX, this._offsetY, this._delta[idx + 2], this._delta[idx + 3])) {
                        this._meGraphic.drawImage(img, this._translateX, this._translateY);
                     }
                  } finally {
                     break label79;
                  }
               }
               break;
            case 50:
               int currentChild = this._nodes[index + 4];
               int numChildren = this._nodes[index + 5];
               int child = this._nodes[index + 6];
               if (currentChild != -1) {
                  if (currentChild != -2) {
                     child = this._nodes[index + 6 + currentChild];
                     this.renderVisualNode(child, g, xlateX, xlateY);
                  } else {
                     for (int i = 0; i < numChildren; i++) {
                        child = this._nodes[index + 6 + i];
                        this.renderVisualNode(child, g, xlateX, xlateY);
                     }
                  }
               }
         }

         this._offsetX = oldAbsX;
         this._offsetY = oldAbsY;
      }
   }

   private final boolean onScreen(int x, int y, int w, int h) {
      return x + w >= this._clipX && x <= this._clipX + this._clipW && y + h >= this._clipY && y <= this._clipY + this._clipH;
   }

   private final void renderFill(int index, Object g) {
      int type = this._nodes[index];
      int red = this._nodes[index + 4];
      int green = this._nodes[index + 5];
      int blue = this._nodes[index + 6];
      this._meGraphic.setColor(red, green, blue);
      switch (type) {
         case 10:
            int width = this._nodes[index + 10];
            int height = this._nodes[index + 11];
            this._meGraphic.fillRect(this._translateX, this._translateY, width, height);
            return;
         case 20:
            int xidx = this._nodes[index + 10];
            int yidx = this._nodes[index + 11];
            this._meGraphic.fillPolygon(this._translateX, this._translateY, this._coords[xidx], this._coords[yidx]);
            return;
         case 30:
            int fontType = this._nodes[index + 7];
            int textIdx = this._nodes[index + 8];
            String text = (String)this._objects[textIdx];
            int textWidth = this._meGraphic.getFontAdvance(fontType, text);
            if (this.onScreen(this._offsetX, this._offsetY - this._meGraphic.getFontAscent(fontType) - 1, textWidth, this._meGraphic.getFontHeight(fontType))) {
               this._meGraphic.drawText(fontType, text, this._translateX, this._translateY);
               return;
            }
            break;
         case 60:
            int rx = this._nodes[index + 10];
            int ry = this._nodes[index + 11];
            this._meGraphic.fillEllipse(this._translateX, this._translateY, rx, ry);
      }
   }

   private final void renderStroke(int index, Object g) {
      int type = this._nodes[index];
      int red = this._nodes[index + 7];
      int green = this._nodes[index + 8];
      int blue = this._nodes[index + 9];
      this._meGraphic.setColor(red, green, blue);
      switch (type) {
         case 10:
            int width = this._nodes[index + 10];
            int height = this._nodes[index + 11];
            this._meGraphic.drawRect(this._translateX, this._translateY, width, height);
            return;
         case 20:
            int xidx = this._nodes[index + 10];
            int yidx = this._nodes[index + 11];
            this._meGraphic.drawPolygon(this._translateX, this._translateY, this._coords[xidx], this._coords[yidx]);
            return;
         case 60:
            int rx = this._nodes[index + 10];
            int ry = this._nodes[index + 11];
            this._meGraphic.drawEllipse(this._translateX, this._translateY, rx, ry);
      }
   }

   public AnimationViewport(Object m) {
      this();
      this.setMedia(m);
   }

   private final void drawStaticBackground() {
   }

   private final void setDirty(int x, int y, int w, int h) {
      if (!this._allDirty) {
         if (x <= this._rightXOnScreen && y <= this._botYOnScreen && x + w >= this._leftXOnScreen && y + h >= this._topYOnScreen) {
            int x2 = x + w;
            int y2 = y + h;
            if (x < this._leftXOnScreen) {
               x = this._leftXOnScreen;
            }

            if (y < this._topYOnScreen) {
               y = this._topYOnScreen;
            }

            if (x2 > this._rightXOnScreen) {
               x2 = this._rightXOnScreen;
            }

            if (y2 > this._botYOnScreen) {
               y2 = this._botYOnScreen;
            }

            if (this._allClean) {
               this._allClean = false;
               this._dirtyX1 = x;
               this._dirtyX2 = x2;
               this._dirtyY1 = y;
               this._dirtyY2 = y2;
            } else {
               if (x < this._dirtyX1) {
                  this._dirtyX1 = x;
               }

               if (x2 > this._dirtyX2) {
                  this._dirtyX2 = x2;
               }

               if (y < this._dirtyY1) {
                  this._dirtyY1 = y;
               }

               if (y2 > this._dirtyY2) {
                  this._dirtyY2 = y2;
               }
            }
         }
      }
   }

   private final void resetClip() {
      if (this._model != null) {
         this._leftX = this._originX + (this._panXAmount >> 16);
         this._topY = this._originY + (this._panYAmount >> 16);
         this._rightX = this._originX + this._virtWidth + (this._panXAmount >> 16);
         this._botY = this._originY + this._virtHeight + (this._panYAmount >> 16);
         this._leftXOnScreen = 0 < this._leftX ? this._leftX : 0;
         this._topYOnScreen = 0 < this._topY ? this._topY : 0;
         this._rightXOnScreen = this._width > this._rightX ? this._rightX : this._width;
         this._botYOnScreen = this._height > this._botY ? this._botY : this._height;
         this._doneFirstDirtyCalc = false;
      }
   }
}
