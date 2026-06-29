package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.FontMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.util.SimpleStack;

public class PME12GraphicsImpl implements PME12Graphics {
   private Graphics _g;
   private Graphics _gScreen;
   private XYRect _fullScreenRect;
   private int _numBuffers;
   private int _activeBufferId = -1;
   private Graphics[] _gBuffers;
   private Bitmap[] _bitmapBuffers;
   private SimpleStack _contextStack;
   private int[] _tempXCoords = new int[4];
   private int[] _tempYCoords = new int[4];
   private int[] _transformedXCoords = new int[4];
   private int[] _transformedYCoords = new int[4];
   private int[] _tempMatrix = new int[]{
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      -804651005,
      0,
      0,
      1,
      -804651002,
      1,
      0,
      0,
      0,
      1,
      0,
      51,
      -804651004,
      -1,
      -1,
      -1,
      -1,
      712179968,
      1411080524,
      2191443,
      -1305840895,
      1398020978,
      16785776,
      -2104615050,
      527827200,
      1816363662,
      1979777255
   };
   private int[] _tempOrigin = new int[2];
   private int[] _inverse = new int[4];
   private int fontSize;
   private TextGraphics _textGraphics = (TextGraphics)(new Object("BBMillbank", 10));
   private FontMetrics _fontMetrics = (FontMetrics)(new Object());
   private TextMetrics _textMetrics = (TextMetrics)(new Object());
   private StringBuffer _textBuffer = (StringBuffer)(new Object());
   private XYRect _clip = (XYRect)(new Object());
   private int _offsetX;
   private int _offsetY;
   private static DrawTextParam _textParams = (DrawTextParam)(new Object());

   public void drawImage(Object image, int x, int y, int width, int height) {
      Bitmap bitmap = this.getBitmap(image);
      if (bitmap != null) {
         MEGraphics2dContext context = this.peekContext();
         if (context.getVisibility() == 0) {
            return;
         }

         this._clip.set(context.getClip());
         this.XYRect32ToXYRectInt(this._clip);
         this._g.pushContext(this._clip, 0, 0);
         XYRect gClipRect = this._g.getClippingRect();
         if (gClipRect.width == 0 || gClipRect.height == 0) {
            this._g.popContext();
            return;
         }

         this._g.setGlobalAlpha(context.getObjectOpacity());
         int[] transformMatrix = context.getCurrentTransformMatrix();
         if (VecMath.isIdentity(transformMatrix, 0)) {
            this._g.drawBitmap(x, y, width, height, bitmap, 0, 0);
         } else if (VecMath.isTranslation(transformMatrix, 0)) {
            x += Fixed32.toRoundedInt(transformMatrix[2]);
            y += Fixed32.toRoundedInt(transformMatrix[5]);
            this._g.drawBitmap(x, y, width, height, bitmap, 0, 0);
         } else {
            VecMath.invert2x2Mat(transformMatrix[0], transformMatrix[3], transformMatrix[1], transformMatrix[4], this._inverse, 0);
            this._tempXCoords[0] = Fixed32.toFP(x);
            this._tempYCoords[0] = Fixed32.toFP(y);
            this._tempXCoords[1] = Fixed32.toFP(x + width);
            this._tempYCoords[1] = Fixed32.toFP(y);
            this._tempXCoords[2] = Fixed32.toFP(x + width);
            this._tempYCoords[2] = Fixed32.toFP(y + height);
            this._tempXCoords[3] = Fixed32.toFP(x);
            this._tempYCoords[3] = Fixed32.toFP(y + height);
            VecMath.transformPoints32(transformMatrix, 0, this._tempXCoords, this._tempYCoords, this._transformedXCoords, this._transformedYCoords);

            for (int i = 0; i < 4; i++) {
               this._transformedXCoords[i] = Fixed32.toRoundedInt(this._transformedXCoords[i]);
               this._transformedYCoords[i] = Fixed32.toRoundedInt(this._transformedYCoords[i]);
            }

            this._g
               .drawTexturedPath(
                  this._transformedXCoords,
                  this._transformedYCoords,
                  null,
                  null,
                  this._transformedXCoords[0],
                  this._transformedYCoords[0],
                  this._inverse[0],
                  this._inverse[1],
                  this._inverse[2],
                  this._inverse[3],
                  bitmap
               );
         }

         this._g.popContext();
      }
   }

   public void drawImage(Object image, int x, int y) {
      Bitmap bitmap = this.getBitmap(image);
      if (bitmap != null) {
         this.drawImage(bitmap, x, y, bitmap.getWidth(), bitmap.getHeight());
      } else {
         if (image instanceof Object) {
            ForeignObject fo = (ForeignObject)image;
            this.drawForeignObject(fo);
         }
      }
   }

   @Override
   public int createNewBuffer() {
      return this.createNewBuffer(Display.getWidth(), Display.getHeight(), false);
   }

   @Override
   public int createNewBuffer(int width, int height, boolean isTransparent) {
      if (width > 0 && height > 0) {
         try {
            if (this._numBuffers == this._gBuffers.length) {
               Graphics[] newGraphics = new Object[this._numBuffers + 1];
               Bitmap[] newBitmaps = new Object[this._numBuffers + 1];
               System.arraycopy(this._gBuffers, 0, newGraphics, 0, this._numBuffers);
               System.arraycopy(this._bitmapBuffers, 0, newBitmaps, 0, this._numBuffers);
               this._gBuffers = newGraphics;
               this._bitmapBuffers = newBitmaps;
            }

            this._bitmapBuffers[this._numBuffers] = (Bitmap)(new Object(width, height));
            if (isTransparent) {
               this._bitmapBuffers[this._numBuffers].createAlpha(2);
            }

            this._gBuffers[this._numBuffers] = (Graphics)(new Object(this._bitmapBuffers[this._numBuffers]));
         } finally {
            ;
         }

         this._numBuffers++;
         return this._numBuffers - 1;
      } else {
         return -1;
      }
   }

   @Override
   public void switchDrawToBuffer(int bufferId) {
      this._activeBufferId = bufferId;
      this._g = this._gBuffers[this._activeBufferId];
   }

   @Override
   public int getActiveBufferId() {
      return this._activeBufferId;
   }

   @Override
   public Object getBuffer(int bufferId) {
      return bufferId >= 0 && bufferId < this._bitmapBuffers.length ? this._bitmapBuffers[bufferId] : null;
   }

   @Override
   public void panBuffer(int bufferId, int x, int y) {
      if (bufferId >= 0 && bufferId < this._bitmapBuffers.length && this._gBuffers[bufferId] != null) {
         this._gBuffers[bufferId].copyArea(0, 0, this._bitmapBuffers[bufferId].getWidth(), this._bitmapBuffers[bufferId].getHeight(), x, y);
      }
   }

   @Override
   public void switchDrawToScreen() {
      this._g = this._gScreen;
      this._activeBufferId = -1;
   }

   @Override
   public void applyBuffer(int bufferId, XYRect clipRect, int x, int y) {
      this.drawDirectImage(this._bitmapBuffers[bufferId], 255, (XYRect)(new Object(clipRect)), VecMath.IDENTITY_3X3, x, y);
   }

   @Override
   public void applyBuffer(int bufferId, XYRect clipRect) {
      this.applyBuffer(bufferId, clipRect, 0, 0);
   }

   @Override
   public void setOffset(int x, int y) {
      this._offsetX = x;
      this._offsetY = y;
   }

   @Override
   public void clear(int color) {
      this._g.setColor(color);
      if (this._activeBufferId != -1 && this._bitmapBuffers[this._activeBufferId].hasAlpha()) {
         this._g.setGlobalAlpha(0);
      }

      this._g.clear();
   }

   @Override
   public void clear(int x, int y, int width, int height, int color) {
      this._g.setColor(color);
      if (this._activeBufferId != -1 && this._bitmapBuffers[this._activeBufferId].hasAlpha()) {
         this._g.setGlobalAlpha(0);
      }

      this._g.clear(x, y, width, height);
   }

   @Override
   public void setBackgroundColor(int color) {
      this._g.setBackgroundColor(color);
   }

   @Override
   public void pushContext(MEGraphics2dContext ctx) {
      this._contextStack.push(ctx);
   }

   @Override
   public MEGraphics2dContext popContext() {
      return (MEGraphics2dContext)this._contextStack.pop();
   }

   @Override
   public MEGraphics2dContext peekContext() {
      return this._contextStack.isEmpty() ? null : (MEGraphics2dContext)this._contextStack.peek();
   }

   @Override
   public void drawEllipse(int cx, int cy, int rx, int ry) {
      if (rx > 0 && ry > 0) {
         int px = cx + (rx - 32768);
         int py = cy;
         int qx = cx;
         int qy = cy + (ry - 32768);
         this.drawEllipse(cx, cy, px, py, qx, qy);
      }
   }

   @Override
   public void drawImage(Object image) {
      MEGraphics2dContext ctx = this.peekContext();
      int x = Fixed32.toRoundedInt(ctx.getX());
      int y = Fixed32.toRoundedInt(ctx.getY());
      this.drawImage(image, x, y);
   }

   @Override
   public XYRect getFullScreenRect() {
      return this._fullScreenRect;
   }

   @Override
   public void setGraphics(Object nativeGraphics) {
      this._gScreen = (Graphics)nativeGraphics;
   }

   @Override
   public void fillImageBounds(Object image, XYRect rectBounds) {
      if (rectBounds != null) {
         Bitmap bitmap = this.getBitmap(image);
         if (bitmap != null) {
            rectBounds.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return;
         }

         if (image instanceof Object) {
            ForeignObject fo = (ForeignObject)image;
            rectBounds.set(0, 0, fo.getWidth(), fo.getHeight());
            return;
         }

         rectBounds.set(0, 0, 0, 0);
      }
   }

   @Override
   public void drawDirectImage(Object image, int opacity, XYRect clipRect, int[] transformMatrix, int x, int y) {
      Bitmap bitmap = this.getBitmap(image);
      if (bitmap != null) {
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         this._g.pushContext(clipRect, 0, 0);
         XYRect gClipRect = this._g.getClippingRect();
         if (gClipRect.width == 0 || gClipRect.height == 0) {
            this._g.popContext();
            return;
         }

         this._g.setGlobalAlpha(opacity);
         if (VecMath.isIdentity(transformMatrix, 0)) {
            this._g.drawBitmap(gClipRect.x, gClipRect.y, gClipRect.width, gClipRect.height, bitmap, gClipRect.x - x, gClipRect.y - y);
         } else {
            VecMath.invert2x2Mat(transformMatrix[0], transformMatrix[3], transformMatrix[1], transformMatrix[4], this._inverse, 0);
            this._tempXCoords[0] = Fixed32.toFP(x);
            this._tempYCoords[0] = Fixed32.toFP(y);
            this._tempXCoords[1] = Fixed32.toFP(x + width);
            this._tempYCoords[1] = Fixed32.toFP(y);
            this._tempXCoords[2] = Fixed32.toFP(x + width);
            this._tempYCoords[2] = Fixed32.toFP(y + height);
            this._tempXCoords[3] = Fixed32.toFP(x);
            this._tempYCoords[3] = Fixed32.toFP(y + height);
            VecMath.transformPoints32(transformMatrix, 0, this._tempXCoords, this._tempYCoords, this._transformedXCoords, this._transformedYCoords);

            for (int i = 0; i < 4; i++) {
               this._transformedXCoords[i] = Fixed32.toRoundedInt(this._transformedXCoords[i]);
               this._transformedYCoords[i] = Fixed32.toRoundedInt(this._transformedYCoords[i]);
            }

            this._g
               .drawTexturedPath(
                  this._transformedXCoords,
                  this._transformedYCoords,
                  null,
                  null,
                  this._transformedXCoords[0],
                  this._transformedYCoords[0],
                  this._inverse[0],
                  this._inverse[1],
                  this._inverse[2],
                  this._inverse[3],
                  bitmap
               );
         }

         this._g.popContext();
      }
   }

   @Override
   public void drawPath(int[] xCoords, int[] yCoords, int[] finalXCoords, int[] finalYCoords, byte[] pointTypes, int[] offsets, boolean closed) {
      int length = xCoords.length;
      if (length >= 2) {
         MEGraphics2dContext context = this.peekContext();
         if (context.getVisibility() != 0) {
            this._clip.set(context.getClip());
            this.XYRect32ToXYRectInt(this._clip);
            this._g.pushContext(this._clip, 0, 0);
            XYRect gClipRect = this._g.getClippingRect();
            if (gClipRect.width != 0 && gClipRect.height != 0) {
               int[] transformMatrix = context.getCurrentTransformMatrix();
               if (VecMath.isIdentity(transformMatrix, 0)) {
                  for (int i = 0; i < length; i++) {
                     finalXCoords[i] = Fixed32.toRoundedInt(xCoords[i]);
                     finalYCoords[i] = Fixed32.toRoundedInt(yCoords[i]);
                  }
               } else {
                  VecMath.transformPoints32(transformMatrix, 0, xCoords, yCoords, finalXCoords, finalYCoords, length);

                  for (int i = 0; i < length; i++) {
                     finalXCoords[i] = Fixed32.toRoundedInt(finalXCoords[i]);
                     finalYCoords[i] = Fixed32.toRoundedInt(finalYCoords[i]);
                  }
               }

               int[] xPts = finalXCoords;
               int[] yPts = finalYCoords;
               MEGraphics2dContext strokePaintContext = context.resolveStrokePaintInheritance();
               int strokeType = strokePaintContext.getStrokePaint();
               boolean antialias = this.getAntialiasHints(context);
               if (strokeType != 1) {
                  this._g.setDrawingStyle(2, antialias);
               } else {
                  this._g.setDrawingStyle(2, false);
               }

               boolean isALine = false;
               if (length == 2) {
                  isALine = true;
               }

               if (!isALine || strokeType != 1) {
                  MEGraphics2dContext fillPaintContext = context.resolveFillPaintInheritance();
                  int fillType = fillPaintContext.getFillPaint();
                  switch (fillType) {
                     case 0:
                        break;
                     case 1:
                     default:
                        this.setFillData(context);
                        this._g.setColor(fillPaintContext.getFillColor());
                        this._g.drawFilledPath(xPts, yPts, pointTypes, offsets);
                        break;
                     case 2:
                        this._g.setDrawingStyle(2, false);
                        this.drawPathTexture(xPts, yPts, pointTypes, offsets, context, fillPaintContext);
                  }
               }

               if (Fixed32.toRoundedInt(context.getStrokeWidth()) != 0) {
                  switch (strokeType) {
                     case 1:
                        this._g.setDrawingStyle(1, antialias);
                        this._g.setDrawingStyle(2, antialias);
                        this.setStrokeData(context);
                        this._g.setColor(strokePaintContext.getStrokeColor());
                        this._g.drawPathOutline(xPts, yPts, pointTypes, offsets, closed);
                  }
               }

               this._g.popContext();
            } else {
               this._g.popContext();
            }
         }
      }
   }

   @Override
   public void drawRect(int x, int y, int width, int height) {
      int fX = x;
      int fY = y;
      int fWidth = width;
      int fHeight = height;
      if (width > 0 && height > 0) {
         MEGraphics2dContext context = this.peekContext();
         if (context.getVisibility() != 0) {
            MEGraphics2dContext fillPaintContext = context.resolveFillPaintInheritance();
            MEGraphics2dContext strokePaintContext = context.resolveStrokePaintInheritance();
            int[] transformMatrix = context.getCurrentTransformMatrix();
            this._clip.set(context.getClip());
            this.XYRect32ToXYRectInt(this._clip);
            this._g.pushContext(this._clip, 0, 0);
            XYRect gClipRect = this._g.getClippingRect();
            if (gClipRect.width != 0 && gClipRect.height != 0) {
               boolean antialias = this.getAntialiasHints(context);
               int strokeType = strokePaintContext.getStrokePaint();
               if (strokeType != 1) {
                  this._g.setDrawingStyle(2, antialias);
               } else {
                  this._g.setDrawingStyle(2, false);
               }

               this._g.setMatrix(transformMatrix);
               int fillType = fillPaintContext.getFillPaint();
               switch (fillType) {
                  case 0:
                     break;
                  case 1:
                  default:
                     this.setFillData(context);
                     this._g.setColor(fillPaintContext.getFillColor());
                     this._g.fillRect32(fX, fY, fWidth, fHeight);
                     break;
                  case 2:
                     this._g.setIdentity();
                     int wx = width - 65536;
                     int wy = 0;
                     int hx = 0;
                     int hy = height - 65536;
                     if (!VecMath.isIdentity(transformMatrix, 0)) {
                        this._tempXCoords[0] = x;
                        this._tempYCoords[0] = y;
                        this._tempXCoords[1] = wx;
                        this._tempYCoords[1] = wy;
                        this._tempXCoords[2] = hx;
                        this._tempYCoords[2] = hy;
                        this._tempXCoords[3] = 0;
                        this._tempYCoords[3] = 0;
                        VecMath.transformPoints32(transformMatrix, 0, this._tempXCoords, this._tempYCoords, this._transformedXCoords, this._transformedYCoords);
                        x = this._transformedXCoords[0];
                        y = this._transformedYCoords[0];
                        wx = this._transformedXCoords[1] - this._transformedXCoords[3];
                        wy = this._transformedYCoords[1] - this._transformedYCoords[3];
                        hx = this._transformedXCoords[2] - this._transformedXCoords[3];
                        hy = this._transformedYCoords[2] - this._transformedYCoords[3];
                     }

                     x = Fixed32.toRoundedInt(x);
                     y = Fixed32.toRoundedInt(y);
                     wx = Fixed32.toRoundedInt(wx);
                     wy = Fixed32.toRoundedInt(wy);
                     hx = Fixed32.toRoundedInt(hx);
                     hy = Fixed32.toRoundedInt(hy);
                     this._tempXCoords[0] = x;
                     this._tempYCoords[0] = y;
                     this._tempXCoords[1] = x + wx;
                     this._tempYCoords[1] = y + wy;
                     this._tempXCoords[2] = x + wx + hx;
                     this._tempYCoords[2] = y + wy + hy;
                     this._tempXCoords[3] = x + hx;
                     this._tempYCoords[3] = y + hy;
                     this._g.setDrawingStyle(2, false);
                     this.drawPathTexture(this._tempXCoords, this._tempYCoords, null, null, context, fillPaintContext);
                     this._g.setMatrix(transformMatrix);
               }

               if (Fixed32.toRoundedInt(context.getStrokeWidth()) != 0) {
                  switch (strokeType) {
                     case 1:
                        this._g.setDrawingStyle(1, antialias);
                        this._g.setDrawingStyle(2, antialias);
                        this.setStrokeData(context);
                        this._g.setColor(strokePaintContext.getStrokeColor());
                        this._g.drawRect32(fX, fY, fWidth, fHeight);
                  }
               }

               this._g.setIdentity();
               this._g.popContext();
            } else {
               this._g.popContext();
            }
         }
      }
   }

   @Override
   public void drawText(char[] text, int startIndex, int length) {
      if (length > 0) {
         MEGraphics2dContext ctx = this.peekContext();
         MEGraphics2dContext fillPaintContext = ctx.resolveFillPaintInheritance();
         int fillType = fillPaintContext.getFillPaint();
         MEGraphics2dContext strokePaintContext = ctx.resolveStrokePaintInheritance();
         int strokeType = strokePaintContext.getStrokePaint();
         if (fillType == 1) {
            this.setFillData(ctx);
            this._textGraphics.setEffectsFillColor(ctx.getFillColor());
         }

         if (strokeType != 1) {
            this._textGraphics.setEffects(0);
            this._g.setColor(ctx.getFillColor());
         } else {
            if (fillType == Integer.MIN_VALUE) {
               this._g.setColor(ctx.getStrokeColor());
               this._textGraphics.setEffects(256);
            } else {
               this._textGraphics.setEffects(768);
            }

            this._textGraphics.setEffectsStrokeColor(ctx.getStrokeColor());
         }

         this._clip.set(ctx.getClip());
         this.XYRect32ToXYRectInt(this._clip);
         this._g.pushContext(this._clip, 0, 0);
         XYRect gClipRect = this._g.getClippingRect();
         if (gClipRect.width != 0 && gClipRect.height != 0) {
            int[] transform = ctx.getCurrentTransformMatrix();
            System.arraycopy(transform, 0, this._tempMatrix, 0, 6);
            int dx = ctx.getDx();
            int dy = ctx.getDy();
            int x = ctx.getX();
            int y = ctx.getY();
            int tx = this._tempMatrix[2];
            int ty = this._tempMatrix[5];
            this._tempMatrix[2] = 0;
            this._tempMatrix[5] = 0;
            if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE) {
               VecMath.pointMultiply3x3(this._tempMatrix, 0, x, y, this._tempXCoords, 0);
               x = this._tempXCoords[0] + tx;
               y = this._tempXCoords[1] + ty;
            } else if (x == Integer.MIN_VALUE && y != Integer.MIN_VALUE) {
               VecMath.pointMultiply3x3(this._tempMatrix, 0, 0, y, this._tempXCoords, 0);
               x = ctx.getCurrentTextPosX();
               y = this._tempXCoords[1] + ty;
            } else if (x != Integer.MIN_VALUE && y == Integer.MIN_VALUE) {
               VecMath.pointMultiply3x3(this._tempMatrix, 0, x, 0, this._tempXCoords, 0);
               x = this._tempXCoords[0] + tx;
               y = ctx.getCurrentTextPosY();
            } else {
               x = ctx.getCurrentTextPosX();
               y = ctx.getCurrentTextPosY();
            }

            VecMath.pointMultiply3x3(this._tempMatrix, 0, dx, dy, this._tempXCoords, 0);
            x += this._tempXCoords[0];
            y += this._tempXCoords[1];
            String fontFamilyName = ctx.getFontFamilyName();
            MEGraphics2dContext fontSizeCtx = ctx.resolveFontSizeInheritance();
            this.fontSize = Fixed32.toInt(fontSizeCtx.getFontPixelSize());
            if (this.fontSize < 5) {
               this.fontSize = 5;
            }

            int style = getFontStyle(ctx.getFontStyle());
            int weight = getFontWeight(ctx.getFontWeight());
            style |= weight;
            int textDecoration = this.getTextDecoration(ctx.getTextDecoration());
            style |= textDecoration;
            int aaMode = this.getTextRenderingHints(ctx.getTextRenderingHints());
            this._textGraphics.setTypefaceName(fontFamilyName);
            this._textGraphics.setStyle(style);
            this._textGraphics.setHeightWithLeading(this.fontSize);
            this._textGraphics.setAntialiasingMode(aaMode);
            this._textGraphics.setTransform(this._tempMatrix[0], this._tempMatrix[3], this._tempMatrix[1], this._tempMatrix[4], 0, 0);
            int oldAlpha = this._g.getGlobalAlpha();
            if (ctx.getVisibility() == 0) {
               this._g.setGlobalAlpha(0);
            }

            x = Fixed32.toRoundedInt(x);
            y = Fixed32.toRoundedInt(y);
            this._textGraphics.getFontMetrics(this._fontMetrics);
            this._textBuffer.setLength(0);
            this._textBuffer.append(text);
            switch (ctx.getTextAnchor()) {
               case 0:
                  throw new Object("Invalid text anchor");
               case 1:
               default:
                  _textParams.iAlignment = 6;
                  _textParams.iMaxAdvance = Integer.MAX_VALUE;
                  break;
               case 2:
                  _textParams.iAlignment = 4;
                  _textParams.iMaxAdvance = 2 * x;
                  x = 0;
                  break;
               case 3:
                  _textParams.iAlignment = 5;
                  _textParams.iMaxAdvance = x;
                  x = 0;
            }

            this._textGraphics
               .drawText(
                  this._g,
                  this._textBuffer,
                  startIndex,
                  length,
                  x,
                  y - this._fontMetrics.iAscent - this._fontMetrics.iLeadingAbove,
                  _textParams,
                  this._textMetrics
               );
            this._g.setGlobalAlpha(oldAlpha);
            this._g.popContext();
         } else {
            this._g.popContext();
         }
      }
   }

   @Override
   public void fillTextBounds(char[] text, int startIndex, int length, MEGraphics2dContext ctx, int[] currentTextPos, XYRect rectBounds) {
      int currentTextPosX = ctx.getX();
      int currentTextPosY = ctx.getY();
      int dx = ctx.getDx();
      int dy = ctx.getDy();
      int[] transformMatrix = ctx.getCurrentTransformMatrix();
      if (currentTextPosY == Integer.MIN_VALUE) {
         currentTextPosY = ctx.getCurrentTextPosY();
         if (currentTextPosX == Integer.MIN_VALUE) {
            currentTextPosX = ctx.getCurrentTextPosX();
         } else {
            VecMath.pointMultiply3x3(transformMatrix, 0, currentTextPosX, 0, this._tempXCoords, 0);
            currentTextPosX = this._tempXCoords[0];
         }
      } else {
         if (currentTextPosX == Integer.MIN_VALUE) {
            VecMath.pointMultiply3x3(transformMatrix, 0, 0, currentTextPosY, this._tempXCoords, 0);
            currentTextPosX = ctx.getCurrentTextPosX();
         } else {
            VecMath.pointMultiply3x3(transformMatrix, 0, currentTextPosX, currentTextPosY, this._tempXCoords, 0);
            currentTextPosX = this._tempXCoords[0];
         }

         currentTextPosY = this._tempXCoords[1];
      }

      VecMath.pointMultiply3x3(transformMatrix, 0, dx, dy, this._tempXCoords, 0);
      currentTextPosX += this._tempXCoords[0] - transformMatrix[2];
      currentTextPosY += this._tempXCoords[1] - transformMatrix[5];
      if (length == 0) {
         currentTextPos[0] = currentTextPosX;
         currentTextPos[1] = currentTextPosY;
         rectBounds.set(Fixed32.toRoundedInt(currentTextPosX), Fixed32.toRoundedInt(currentTextPosY), 0, 0);
      } else {
         String fontFamilyName = ctx.getFontFamilyName();
         MEGraphics2dContext fontSizeCtx = ctx.resolveFontSizeInheritance();
         this.fontSize = Fixed32.toInt(fontSizeCtx.getFontPixelSize());
         if (this.fontSize < 5) {
            this.fontSize = 5;
         }

         int style = getFontStyle(ctx.getFontStyle());
         int weight = getFontWeight(ctx.getFontWeight());
         style |= weight;
         int textDecoration = this.getTextDecoration(ctx.getTextDecoration());
         style |= textDecoration;
         int aaMode = this.getTextRenderingHints(ctx.getTextRenderingHints());
         this._textGraphics.setTypefaceName(fontFamilyName);
         this._textGraphics.setStyle(style);
         this._textGraphics.setHeightWithLeading(this.fontSize);
         this._textGraphics.setAntialiasingMode(aaMode);
         MEGraphics2dContext fillPaintContext = ctx.resolveFillPaintInheritance();
         int fillType = fillPaintContext.getFillPaint();
         MEGraphics2dContext strokePaintContext = ctx.resolveStrokePaintInheritance();
         int strokeType = strokePaintContext.getStrokePaint();
         if (strokeType != 1) {
            this._textGraphics.setEffects(0);
         } else if (fillType == Integer.MIN_VALUE) {
            this._textGraphics.setEffects(256);
         } else {
            this._textGraphics.setEffects(768);
         }

         this._textGraphics.setTransform(transformMatrix[0], transformMatrix[3], transformMatrix[1], transformMatrix[4], 0, 0);
         _textParams.iAlignment = 6;
         _textParams.iMaxAdvance = Integer.MAX_VALUE;
         this._textBuffer.setLength(0);
         this._textBuffer.append(text);
         this._textGraphics.measureText(this._textBuffer, startIndex, length, _textParams, this._textMetrics);
         currentTextPos[0] = currentTextPosX + Fixed32.toFP(this._textMetrics.iAdvanceX);
         currentTextPos[1] = currentTextPosY + Fixed32.toFP(this._textMetrics.iAdvanceY);
         if (rectBounds != null) {
            currentTextPosX = Fixed32.toRoundedInt(currentTextPosX);
            currentTextPosY = Fixed32.toRoundedInt(currentTextPosY);
            int left = currentTextPosX + this._textMetrics.iBoundsTlX;
            int width = this._textMetrics.iBoundsBrX - this._textMetrics.iBoundsTlX;
            switch (ctx.getTextAnchor()) {
               case 0:
                  throw new Object("Invalid text anchor");
               case 1:
                  break;
               case 2:
               default:
                  left = left - (width + this._textMetrics.iBoundsTlX) / 2 - 1;
                  width++;
                  break;
               case 3:
                  left -= width + this._textMetrics.iBoundsTlX;
            }

            rectBounds.set(left, currentTextPosY + this._textMetrics.iBoundsTlY, width, this._textMetrics.iBoundsBrY - this._textMetrics.iBoundsTlY);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void drawForeignObject(ForeignObject fo) {
      MEGraphics2dContext ctx = this.peekContext();
      if (ctx.getVisibility() != 0) {
         this._clip.set(ctx.getClip());
         this.XYRect32ToXYRectInt(this._clip);
         this._clip.x = this._clip.x + this._offsetX;
         this._clip.y = this._clip.y + this._offsetY;
         this._g.pushContext(this._clip, 0, 0);
         int oldAlpha = this._g.getGlobalAlpha();
         this.setFillData(this._g, ctx);
         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            int[] transform = ctx.getCurrentTransformMatrix();
            int x = ctx.getX();
            int y = ctx.getY();
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            VecMath.pointMultiply3x3(transform, 0, x, y, this._tempXCoords, 0);
            VecMath.pointMultiply3x3(transform, 0, w + x, h + y, this._tempYCoords, 0);
            x = Fixed32.toRoundedInt(this._tempXCoords[0]);
            y = Fixed32.toRoundedInt(this._tempXCoords[1]);
            w = Fixed32.toRoundedInt(this._tempYCoords[0] - this._tempXCoords[0]);
            h = Fixed32.toRoundedInt(this._tempYCoords[1] - this._tempXCoords[1]);
            if (x != fo.getX() || y != fo.getY() || w != fo.getWidth() || h != fo.getHeight()) {
               fo.setPosition(x, y);
               fo.setExtent(w, h);
            }

            fo.draw(this._g, x + this._offsetX, y + this._offsetY);
            var11 = false;
         } finally {
            if (var11) {
               this._g.setGlobalAlpha(oldAlpha);
               this._g.popContext();
            }
         }

         this._g.setGlobalAlpha(oldAlpha);
         this._g.popContext();
      }
   }

   @Override
   public Object getBitmapObject(Object image) {
      return this.getBitmap(image);
   }

   @Override
   public void drawContextList(MEGraphics2dContext[] ctxList, int length) {
      this._g.draw2dContextList(ctxList, length);
   }

   public PME12GraphicsImpl() {
      this._contextStack = new SimpleStack();
      this._gBuffers = new Object[2];
      this._bitmapBuffers = new Object[2];
      this._numBuffers = 0;
      this._fullScreenRect = (XYRect)(new Object(0, 0, Fixed32.toFP(Display.getWidth()), Fixed32.toFP(Display.getHeight())));
   }

   private void drawPathTexture(int[] xPts, int[] yPts, byte[] pointTypes, int[] offsets, MEGraphics2dContext context, MEGraphics2dContext fillPaintContext) {
      this.setFillData(context);
      Bitmap texture = this.getBitmap(fillPaintContext.getFillTexture());
      if (texture != null
         && fillPaintContext._fillTextureDimensions.width != 0
         && texture.getWidth() != 0
         && fillPaintContext._fillTextureDimensions.height != 0
         && texture.getHeight() != 0) {
         int[] transformMatrix = context.getCurrentTransformMatrix();
         int[] textureTransformMatrix = context.getCurrentFillTextureTransformMatrix();
         int scaleFactorX = Fixed32.div(fillPaintContext._fillTextureDimensions.width, Fixed32.toFP(texture.getWidth()));
         int scaleFactorY = Fixed32.div(fillPaintContext._fillTextureDimensions.height, Fixed32.toFP(texture.getHeight()));
         textureTransformMatrix[0] = Fixed32.mul(textureTransformMatrix[0], scaleFactorX);
         textureTransformMatrix[1] = Fixed32.mul(textureTransformMatrix[1], scaleFactorY);
         textureTransformMatrix[3] = Fixed32.mul(textureTransformMatrix[3], scaleFactorX);
         textureTransformMatrix[4] = Fixed32.mul(textureTransformMatrix[4], scaleFactorY);
         VecMath.invert2x2Mat(textureTransformMatrix[0], textureTransformMatrix[3], textureTransformMatrix[1], textureTransformMatrix[4], this._inverse, 0);
         int xOrigin = fillPaintContext.getFillTextureDimensions().x;
         int yOrigin = fillPaintContext.getFillTextureDimensions().y;
         VecMath.pointMultiply3x3(transformMatrix, 0, xOrigin, yOrigin, this._tempOrigin, 0);
         xOrigin = Fixed32.toRoundedInt(this._tempOrigin[0]);
         yOrigin = Fixed32.toRoundedInt(this._tempOrigin[1]);
         this._g
            .drawTexturedPath(
               xPts, yPts, pointTypes, offsets, xOrigin, yOrigin, this._inverse[0], this._inverse[1], this._inverse[2], this._inverse[3], texture
            );
      }
   }

   public static int getFontStyle(int meStyle) {
      int result = 0;
      switch (meStyle) {
         case 0:
            throw new Object("Invalid font style");
         case 1:
            return 0;
         case 2:
         default:
            return 2;
         case 3:
            return 2;
      }
   }

   private final int getTextDecoration(int meTextDecoration) {
      int result = 0;
      switch (meTextDecoration) {
         case 0:
            throw new Object("Invalid text decoration");
         case 1:
         default:
            return 0;
         case 2:
            return 4;
         case 3:
            return 8;
         case 4:
            return 16;
         case 5:
            return 32;
      }
   }

   public static int getFontWeight(int meFontWeight) {
      int result = 0;
      switch (meFontWeight) {
         case 100:
         case 200:
         case 300:
         case 400:
            return 0;
         case 500:
         case 600:
         case 700:
         case 800:
         case 900:
            return 1;
         default:
            throw new Object("Invalid font weight");
      }
   }

   private final int getTextRenderingHints(int meTextRenderingHints) {
      meTextRenderingHints &= -5;
      int prefAntiAliasMode = 1;
      switch (meTextRenderingHints) {
         case -1:
            throw new Object("Invalid text rendering hints.");
         case 0:
         case 1:
         case 3:
            return 1;
         case 2:
         default:
            return 2;
      }
   }

   private void setStrokeData(MEGraphics2dContext context) {
      this._g.setGlobalAlpha(context.getStrokeOpacity() * context.getObjectOpacity() / 255);
      this._g.setStrokeWidth(Fixed32.toRoundedInt(context.getStrokeWidth()));
      this._g.setStrokeStyle(context.getStrokeLinecap());
      this._g.setStrokeStyle(context.getStrokeLinejoin());
   }

   private void setFillData(MEGraphics2dContext context) {
      this.setFillData(this._g, context);
   }

   private void setFillData(Graphics gfx, MEGraphics2dContext context) {
      gfx.setGlobalAlpha(context.getFillOpacity() * context.getObjectOpacity() / 255);
   }

   private void XYRect32ToXYRectInt(XYRect rect) {
      rect.x = Fixed32.toRoundedInt(rect.x);
      rect.y = Fixed32.toRoundedInt(rect.y);
      rect.width = Fixed32.toRoundedInt(rect.width);
      rect.height = Fixed32.toRoundedInt(rect.height);
   }

   private Bitmap getBitmap(Object obj) {
      Bitmap bitmap = null;
      if (obj instanceof Object) {
         return (Bitmap)obj;
      }

      if (obj instanceof Object) {
         EncodedImage cast = (EncodedImage)obj;
         if (cast != null) {
            int oldDecodeMode = cast.getDecodeMode();
            int bitmapType = cast.getBitmapType(0);
            if (bitmapType == 129 || bitmapType == 1) {
               cast.setDecodeMode(oldDecodeMode | 4);
            }

            bitmap = cast.getBitmap(0);
            cast.setDecodeMode(oldDecodeMode);
         }
      }

      return bitmap;
   }

   private boolean getAntialiasHints(MEGraphics2dContext ctx) {
      int shapeHints = ctx.getShapeRenderingHints();
      switch (shapeHints) {
         case 1:
         case 3:
            return false;
         default:
            return true;
      }
   }

   private void drawEllipse(int cx, int cy, int px, int py, int qx, int qy) {
      if ((cx != px || cy != py) && (cx != qx || cy != qy)) {
         MEGraphics2dContext context = this.peekContext();
         if (context.getVisibility() != 0) {
            MEGraphics2dContext fillPaintContext = context.resolveFillPaintInheritance();
            MEGraphics2dContext strokePaintContext = context.resolveStrokePaintInheritance();
            int[] transformMatrix = context.getCurrentTransformMatrix();
            this._g.setMatrix(transformMatrix);
            this._clip.set(context.getClip());
            this.XYRect32ToXYRectInt(this._clip);
            this._g.pushContext(this._clip, 0, 0);
            XYRect gClipRect = this._g.getClippingRect();
            if (gClipRect.width != 0 && gClipRect.height != 0) {
               boolean antialias = this.getAntialiasHints(context);
               this._g.setDrawingStyle(1, antialias);
               this._g.setDrawingStyle(2, antialias);
               int fillType = fillPaintContext.getFillPaint();
               switch (fillType) {
                  case 1:
                     this.setFillData(context);
                     this._g.setColor(fillPaintContext.getFillColor());
                     this._g.fillEllipse32(cx, cy, px, py, qx, qy, 0, 23592960);
                  default:
                     int strokeType = strokePaintContext.getStrokePaint();
                     if (Fixed32.toRoundedInt(context.getStrokeWidth()) != 0) {
                        switch (strokeType) {
                           case 1:
                              this.setStrokeData(context);
                              this._g.setColor(strokePaintContext.getStrokeColor());
                              this._g.drawEllipse32(cx, cy, px, py, qx, qy, 0, 23592960);
                        }
                     }

                     this._g.setIdentity();
                     this._g.popContext();
               }
            } else {
               this._g.popContext();
            }
         }
      }
   }
}
