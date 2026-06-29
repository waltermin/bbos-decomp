package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.vm.Array;

public final class MEGraphics2dImpl implements MEGraphics2d {
   private Graphics g = null;
   private int[] matrixStack = new int[54];
   private int stackCurrentPosition = 0;
   private int[] viewboxClippingMatrix = new int[9];
   private int[] viewboxClippingRect = new int[4];
   private int[] tempPoint = new int[2];
   private static int[] hashKeys = new int[5];
   private static Object[] hashValues = new Object[5];
   private static FontFamily _family;
   private static boolean isIdentity = true;
   private static int[] tempx = new int[4];
   private static int[] tempy = new int[4];
   private static int[] xTempArray = new int[4];
   private static int[] yTempArray = new int[4];
   private static final boolean HAS_NATIVE_TRANSFORM = false;
   private static final boolean HAS_NATIVE_ALPHA = false;
   private static final int DEFAULT_UNITS = 0;
   private static final int DEFAULT_ANTI_ALIAS_MODE = 1;
   private static final int DEFAULT_EFFECTS = 0;
   private static final String DEFAULT_FONT_FAMILY = "System";
   private static final int DEFAULT_STYLE = 0;
   private static int _fontKey;
   private static Font _cachedFont;

   public MEGraphics2dImpl() {
      label20:
      try {
         _family = FontFamily.forName("System");
      } finally {
         break label20;
      }

      this.pushMatrix(VecMath.IDENTITY_3X3, 0);
   }

   @Override
   public final void pushMatrix(int[] matrix, int index) {
      if (this.stackCurrentPosition > 0) {
         VecMath.multiply3x3Affine(this.matrixStack, this.stackCurrentPosition - 9, matrix, index, this.matrixStack, this.stackCurrentPosition);
      }

      System.arraycopy(matrix, index, this.matrixStack, 0, 9);
      this.stackCurrentPosition += 9;
      if (this.stackCurrentPosition == this.matrixStack.length) {
         Array.resize(this.matrixStack, this.stackCurrentPosition + 18);
      }

      isIdentity = VecMath.isIdentity(this.matrixStack, this.stackCurrentPosition - 9);
   }

   @Override
   public final void popMatrix() {
      if (this.stackCurrentPosition > 0) {
         this.stackCurrentPosition -= 9;
      }

      if (this.stackCurrentPosition == 0) {
         isIdentity = true;
      } else {
         isIdentity = VecMath.isIdentity(this.matrixStack, this.stackCurrentPosition - 9);
      }
   }

   private final void calculateViewBoxMatrix(
      int scaleFactorX, int scaleFactorY, int xMin, int yMin, int preserveAspectRatio, int[] viewBoxMatrix, int viewBoxMatrixStartIndex
   ) {
      if (preserveAspectRatio != 0) {
         int factor = (preserveAspectRatio & 128) != 0 ? Math.max(scaleFactorX, scaleFactorY) : Math.min(scaleFactorX, scaleFactorY);
         viewBoxMatrix[viewBoxMatrixStartIndex] = factor;
         viewBoxMatrix[viewBoxMatrixStartIndex + 4] = factor;
      } else {
         viewBoxMatrix[viewBoxMatrixStartIndex] = scaleFactorX;
         viewBoxMatrix[viewBoxMatrixStartIndex + 4] = scaleFactorY;
      }

      viewBoxMatrix[viewBoxMatrixStartIndex + 2] = Fixed32.mul(viewBoxMatrix[viewBoxMatrixStartIndex], Fixed32.toFP(-xMin));
      viewBoxMatrix[viewBoxMatrixStartIndex + 5] = Fixed32.mul(viewBoxMatrix[viewBoxMatrixStartIndex + 4], Fixed32.toFP(-yMin));
      viewBoxMatrix[viewBoxMatrixStartIndex + 8] = 65536;
   }

   private final void calculateViewBoxOriginOffset(
      int x, int y, int width, int height, int scaledViewBoxWidth, int scaledViewBoxHeight, int preserveAspectRatio, int[] origin, int originStartIndex
   ) {
      origin[originStartIndex++] = this.calculateViewBoxOriginCoord(x, width, scaledViewBoxWidth, preserveAspectRatio, 2);
      origin[originStartIndex] = this.calculateViewBoxOriginCoord(y, height, scaledViewBoxHeight, preserveAspectRatio, 0);
   }

   private final int calculateViewBoxOriginCoord(int origin, int viewBoxDimension, int contentDimension, int preserveAspectRatio, int styleShift) {
      int originOffset = origin;
      int style = (preserveAspectRatio & 3 << styleShift) >>> styleShift;
      switch (style) {
         case 1:
            break;
         case 2:
         default:
            originOffset += viewBoxDimension - contentDimension >> 1;
            break;
         case 3:
            originOffset += viewBoxDimension - contentDimension;
      }

      return originOffset;
   }

   private final void calculateViewBoxClip(
      int x, int y, int width, int height, int[] origin, int originStartIndex, int scaledViewBoxWidth, int scaledViewBoxHeight, int[] clip, int clipStartIndex
   ) {
      clip[clipStartIndex++] = Math.max(origin[originStartIndex], x);
      clip[clipStartIndex++] = Math.max(origin[originStartIndex + 1], y);
      clip[clipStartIndex++] = Math.min(scaledViewBoxWidth, width);
      clip[clipStartIndex] = Math.min(scaledViewBoxHeight, height);
   }

   private final void calculateViewBox(
      int x,
      int y,
      int width,
      int height,
      int viewBoxXMin,
      int viewBoxYMin,
      int viewBoxWidth,
      int viewBoxHeight,
      int preserveAspectRatio,
      int[] destMatrix,
      int[] clip
   ) {
      int fixedWidth = Fixed32.toFP(width);
      int fixedHeight = Fixed32.toFP(height);
      int fixedViewBoxWidth = Fixed32.toFP(viewBoxWidth);
      int fixedViewBoxHeight = Fixed32.toFP(viewBoxHeight);
      int scaleFactorX = Fixed32.div(fixedWidth, fixedViewBoxWidth);
      int scaleFactorY = Fixed32.div(fixedHeight, fixedViewBoxHeight);
      this.calculateViewBoxMatrix(scaleFactorX, scaleFactorY, viewBoxXMin, viewBoxYMin, preserveAspectRatio, destMatrix, 0);
      int scaledViewBoxWidth = Fixed32.toRoundedInt(Fixed32.mul(fixedViewBoxWidth, destMatrix[0]));
      int scaledViewBoxHeight = Fixed32.toRoundedInt(Fixed32.mul(fixedViewBoxHeight, destMatrix[4]));
      this.calculateViewBoxOriginOffset(x, y, width, height, scaledViewBoxWidth, scaledViewBoxHeight, preserveAspectRatio, this.tempPoint, 0);
      this.calculateViewBoxClip(x, y, width, height, this.tempPoint, 0, scaledViewBoxWidth, scaledViewBoxHeight, clip, 0);
      destMatrix[2] += Fixed32.toFP(this.tempPoint[0]);
      destMatrix[5] += Fixed32.toFP(this.tempPoint[1]);
   }

   @Override
   public final void pushViewbox(int x, int y, int width, int height, int viewBoxXMin, int viewBoxYMin, int viewBoxWidth, int viewBoxHeight, int aspectRatio) {
      this.calculateViewBox(
         x, y, width, height, viewBoxXMin, viewBoxYMin, viewBoxWidth, viewBoxHeight, aspectRatio, this.viewboxClippingMatrix, this.viewboxClippingRect
      );
      VecMath.pointMultiply3x3(
         this.matrixStack, this.stackCurrentPosition - 9, Fixed32.toFP(this.viewboxClippingRect[0]), Fixed32.toFP(this.viewboxClippingRect[1]), tempx, 0
      );
      VecMath.pointMultiply3x3(
         this.matrixStack,
         this.stackCurrentPosition - 9,
         Fixed32.toFP(this.viewboxClippingRect[0] + this.viewboxClippingRect[2]),
         Fixed32.toFP(this.viewboxClippingRect[1] + this.viewboxClippingRect[3]),
         tempy,
         0
      );
      this.g.pushContext(Fixed32.toInt(tempx[0]), Fixed32.toInt(tempx[1]), Fixed32.toInt(tempy[0] - tempx[0]), Fixed32.toInt(tempy[1] - tempx[1]), 0, 0);
      this.pushMatrix(this.viewboxClippingMatrix, 0);
   }

   @Override
   public final void popViewbox() {
      this.g.popContext();
      this.popMatrix();
   }

   @Override
   public final void setPen() {
   }

   @Override
   public final void setGraphics(Object graphicsObject) {
      this.g = (Graphics)graphicsObject;
   }

   @Override
   public final void setColor(int color) {
      this.g.setColor(color);
   }

   @Override
   public final void setColor(int red, int green, int blue) {
      this.g.setColor(0 | red << 16 | green << 8 | blue);
   }

   @Override
   public final void setStrokeWidth(int width) {
      this.g.setStrokeWidth(width);
   }

   @Override
   public final void setStrokeLinecap(int linecap) {
      this.g.setStrokeStyle(linecap & 240);
   }

   @Override
   public final void setStrokeLinejoin(int linejoin) {
      this.g.setStrokeStyle(linejoin & 15);
   }

   @Override
   public final void fillRect(int x, int y, int width, int height) {
      if (isIdentity) {
         this.g.fillRect(x, y, width, height);
      } else {
         this.transformPoints(x, y, width, height, tempx, tempy);
         this.g.drawFilledPath(tempx, tempy, null, null);
      }
   }

   @Override
   public final void drawRect(int x, int y, int width, int height) {
      if (isIdentity) {
         this.g.drawRect(x, y, width + 1, height + 1);
      } else {
         this.transformPoints(x, y, width, height, tempx, tempy);
         this.g.drawPathOutline(tempx, tempy, null, null, true);
      }
   }

   @Override
   public final void fillEllipse(int cx, int cy, int rx, int ry) {
      if (isIdentity) {
         this.g.fillArc(cx - rx, cy - ry, rx << 1, ry << 1, 0, 360);
      } else {
         this.transformPoints(cx - rx, cy - ry, cx + rx, cy + ry, tempx, tempy);
         this.g.fillArc(tempx[0], tempy[0], rx << 1, ry << 1, 0, 360);
      }
   }

   @Override
   public final void drawEllipse(int cx, int cy, int rx, int ry) {
      if (isIdentity) {
         this.g.drawArc(cx - rx, cy - ry, (rx << 1) + 1, (ry << 1) + 1, 0, 360);
      } else {
         this.transformPoints(cx - rx, cy - ry, cx + rx, cy + ry, tempx, tempy);
         this.g.drawArc(tempx[0], tempy[0], rx << 1, ry << 1, 0, 360);
      }
   }

   @Override
   public final void drawEllipse(int cx, int cy, int px, int py, int qx, int qy) {
      if (isIdentity) {
         int rx = cx < px ? px - cx : cx - px;
         int ry = cy < qy ? qy - cy : cy - qy;
         this.g.drawArc(cx - rx, cy - ry, rx << 1, ry << 1, 0, 360);
      } else {
         int[] xin = new int[3];
         int[] yin = new int[3];
         int[] xout = new int[3];
         int[] yout = new int[3];
         xin[0] = cx;
         yin[0] = cy;
         xin[1] = px;
         yin[1] = py;
         xin[2] = qx;
         yin[2] = qy;
         VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xin, yin, xout, yout);
         if (yout[0] == yout[1] && xout[0] == xout[2]) {
            cx = xout[0];
            cy = yout[0];
            px = xout[1];
            py = yout[1];
            qx = xout[2];
            qy = yout[2];
            int rx = cx < px ? px - cx : cx - px;
            int ry = cy < qy ? qy - cy : cy - qy;
            this.g.drawArc(cx - rx, cy - ry, rx << 1, ry << 1, 0, 360);
         } else {
            this.g.drawEllipse(xout[0], yout[0], xout[1], yout[1], xout[2], yout[2], 0, 360);
         }
      }
   }

   @Override
   public final void fillEllipse(int cx, int cy, int px, int py, int qx, int qy) {
      if (isIdentity) {
         int rx = cx < px ? px - cx : cx - px;
         int ry = cy < qy ? qy - cy : cy - qy;
         this.g.fillArc(cx - rx, cy - ry, rx << 1, ry << 1, 0, 360);
      } else {
         int[] xin = new int[3];
         int[] yin = new int[3];
         int[] xout = new int[3];
         int[] yout = new int[3];
         xin[0] = cx;
         yin[0] = cy;
         xin[1] = px;
         yin[1] = py;
         xin[2] = qx;
         yin[2] = qy;
         VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xin, yin, xout, yout);
         if (yout[0] == yout[1] && xout[0] == xout[2]) {
            cx = xout[0];
            cy = yout[0];
            px = xout[1];
            py = yout[1];
            qx = xout[2];
            qy = yout[2];
            int rx = cx < px ? px - cx : cx - px;
            int ry = cy < qy ? qy - cy : cy - qy;
            this.g.fillArc(cx - rx, cy - ry, rx << 1, ry << 1, 0, 360);
         } else {
            this.g.fillEllipse(xout[0], yout[0], xout[1], yout[1], xout[2], yout[2], 0, 360);
         }
      }
   }

   @Override
   public final void fillPolygon(int x, int y, int[] xCor, int[] yCor) {
      if (xCor.length >= 2 && yCor.length >= 2) {
         if (isIdentity) {
            this.g.translate(x, y);
            this.g.drawFilledPath(xCor, yCor, null, null);
            this.g.translate(-x, -y);
         } else {
            int[] tempPolyX = new int[xCor.length];
            int[] tempPolyY = new int[yCor.length];
            VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xCor, yCor, tempPolyX, tempPolyY);
            this.g.translate(x, y);
            this.g.drawFilledPath(tempPolyX, tempPolyY, null, null);
            this.g.translate(-x, -y);
         }
      }
   }

   @Override
   public final void drawPolygon(int x, int y, int[] xCor, int[] yCor) {
      if (xCor.length >= 2 && yCor.length >= 2) {
         if (isIdentity) {
            this.g.translate(x, y);
            this.g.drawPathOutline(xCor, yCor, null, null, false);
            this.g.translate(-x, -y);
         } else {
            int[] tempPolyX = new int[xCor.length];
            int[] tempPolyY = new int[yCor.length];
            VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xCor, yCor, tempPolyX, tempPolyY);

            for (int i = 0; i < tempPolyX.length - 1; i++) {
               this.g.drawLine(tempPolyX[i] + x, tempPolyY[i] + y, tempPolyX[i + 1] + x, tempPolyY[i + 1] + y);
            }
         }
      }
   }

   @Override
   public final void drawLine(int x1, int y1, int x2, int y2) {
      if (isIdentity) {
         this.g.drawLine(x1, y1, x2, y2);
      } else {
         int p = this.stackCurrentPosition - 9;
         int mx1 = (int)((long)this.matrixStack[p] * x1 + (long)this.matrixStack[p + 1] * y1 + this.matrixStack[p + 2] >> 16);
         int mx2 = (int)((long)this.matrixStack[p] * x2 + (long)this.matrixStack[p + 1] * y2 + this.matrixStack[p + 2] >> 16);
         int my1 = (int)((long)this.matrixStack[p + 3] * x1 + (long)this.matrixStack[p + 4] * y1 + this.matrixStack[p + 5] >> 16);
         int my2 = (int)((long)this.matrixStack[p + 3] * x2 + (long)this.matrixStack[p + 4] * y2 + this.matrixStack[p + 5] >> 16);
         this.g.drawLine(mx1, my1, mx2, my2);
      }
   }

   @Override
   public final void drawText(int font, String text, int x, int y) {
      this.g.setFont(getFont(font));
      if (isIdentity) {
         this.g.drawText(text, x, y, 14);
      } else {
         this.transformPoints(x, y, 0, 0, tempx, tempy);
         this.g.drawText(text, tempx[0], tempy[0], 14);
      }
   }

   @Override
   public final void drawText(
      char[] text,
      int offset,
      int textLength,
      int textWidth,
      int textAntiAliasMode,
      String fontFamilyString,
      int fontSize,
      int styleBitmask,
      int fontUnits,
      int x,
      int y,
      int[] nodesArray,
      int transformOffset,
      int key
   ) {
      this.g.setFont(getFont(textAntiAliasMode, fontFamilyString, fontSize, styleBitmask, fontUnits, nodesArray, transformOffset, key));
      this.transformPoints(x, y, 0, 0, tempx, tempy);
      this.g.drawText(text, offset, textLength, tempx[0], tempy[0], 14, textWidth);
   }

   @Override
   public final void setDrawStyle(int drawStyle, boolean set) {
      switch (drawStyle) {
         case 1:
            this.g.setDrawingStyle(1, set);
            this.g.setDrawingStyle(2, set);
      }
   }

   @Override
   public final void fillPath(
      int translateX, int translateY, int[] xCoords, int[] yCoords, int[] finalXCoords, int[] finalYCoords, byte[] pointTypes, int[] offsets
   ) {
      if (xCoords.length >= 2 && yCoords.length >= 2) {
         if (isIdentity) {
            this.g.translate(translateX, translateY);
            this.g.drawFilledPath(xCoords, yCoords, pointTypes, offsets);
            this.g.translate(-translateX, -translateY);
         } else {
            VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xCoords, yCoords, finalXCoords, finalYCoords);
            this.g.translate(translateX, translateY);
            this.g.drawFilledPath(finalXCoords, finalYCoords, pointTypes, offsets);
            this.g.translate(-translateX, -translateY);
         }
      }
   }

   @Override
   public final void drawPath(
      int translateX, int translateY, int[] xCoords, int[] yCoords, int[] finalXCoords, int[] finalYCoords, byte[] pointTypes, int[] offsets, boolean closed
   ) {
      if (xCoords.length >= 2 && yCoords.length >= 2) {
         if (isIdentity) {
            this.g.translate(translateX, translateY);
            this.g.drawPathOutline(xCoords, yCoords, pointTypes, offsets, closed);
            this.g.translate(-translateX, -translateY);
         } else {
            VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xCoords, yCoords, finalXCoords, finalYCoords);
            this.g.translate(translateX, translateY);
            this.g.drawPathOutline(finalXCoords, finalYCoords, pointTypes, offsets, closed);
            this.g.translate(-translateX, -translateY);
         }
      }
   }

   @Override
   public final void setAlpha(int alpha) {
      this.g.setGlobalAlpha(alpha);
   }

   @Override
   public final void getGraphicsClip(int[] dest, int destStartIndex) {
      XYRect clipRect = this.g.getClippingRect();
      dest[destStartIndex++] = clipRect.x;
      dest[destStartIndex++] = clipRect.y;
      dest[destStartIndex++] = clipRect.width;
      dest[destStartIndex] = clipRect.height;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void drawImage(Object image, int x, int y) {
      if (image instanceof ForeignObject) {
         ForeignObject fo = (ForeignObject)image;
         if (isIdentity) {
            fo.draw(this.g, x, y);
         } else {
            fo.draw(this.g, x, y);
         }
      } else {
         if (!(image instanceof EncodedImage)) {
            if (image instanceof Bitmap) {
               Bitmap cast = (Bitmap)image;
               if (isIdentity) {
                  this.g.drawBitmap(x, y, cast.getWidth(), cast.getHeight(), cast, 0, 0);
                  return;
               }

               this.transformPoints(x, y, cast.getWidth(), cast.getHeight(), tempx, tempy);
               int p = this.stackCurrentPosition - 9;
               int[] inverse = new int[4];
               VecMath.invert2x2Mat(this.matrixStack[p], this.matrixStack[p + 3], this.matrixStack[p + 1], this.matrixStack[p + 4], inverse, 0);
               this.g.drawTexturedPath(tempx, tempy, null, null, tempx[0], tempy[0], inverse[0], inverse[1], inverse[2], inverse[3], cast);
            }
         } else {
            EncodedImage cast = (EncodedImage)image;
            int oldDecodeMode = 0;
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               if (cast == null) {
                  var12 = false;
               } else {
                  oldDecodeMode = cast.getDecodeMode();
                  int bitmapType = cast.getBitmapType(0);
                  if (bitmapType != 129 && bitmapType != 1) {
                     this.g.drawImage(x, y, cast.getScaledWidth(), cast.getScaledHeight(), cast, 0, 0, 0);
                     var12 = false;
                  } else {
                     cast.setDecodeMode(oldDecodeMode | 4);
                     Bitmap bitmap = cast.getBitmap(0);
                     if (bitmap == null) {
                        var12 = false;
                     } else if (isIdentity) {
                        this.g.drawBitmap(x, y, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
                        var12 = false;
                     } else {
                        this.transformPoints(x, y, bitmap.getWidth(), bitmap.getHeight(), tempx, tempy);
                        int p = this.stackCurrentPosition - 9;
                        int[] inverse = new int[4];
                        VecMath.invert2x2Mat(this.matrixStack[p], this.matrixStack[p + 3], this.matrixStack[p + 1], this.matrixStack[p + 4], inverse, 0);
                        this.g.drawTexturedPath(tempx, tempy, null, null, tempx[0], tempy[0], inverse[0], inverse[1], inverse[2], inverse[3], bitmap);
                        var12 = false;
                     }
                  }
               }
            } finally {
               if (var12) {
                  if (cast != null) {
                     cast.setDecodeMode(oldDecodeMode);
                  }
               }
            }

            if (cast != null) {
               cast.setDecodeMode(oldDecodeMode);
               return;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void drawImage(Object image, int x, int y, int width, int height) {
      if (image instanceof ForeignObject) {
         ForeignObject fo = (ForeignObject)image;
         if (isIdentity) {
            fo.draw(this.g, x, y);
         } else {
            fo.draw(this.g, x, y);
         }
      } else {
         if (!(image instanceof EncodedImage)) {
            if (image instanceof Bitmap) {
               Bitmap cast = (Bitmap)image;
               this.transformPoints(x, y, width, height, tempx, tempy);
               int p = this.stackCurrentPosition - 9;
               int[] inverse = new int[4];
               VecMath.invert2x2Mat(this.matrixStack[p], this.matrixStack[p + 3], this.matrixStack[p + 1], this.matrixStack[p + 4], inverse, 0);
               this.g.drawTexturedPath(tempx, tempy, null, null, tempx[0], tempy[0], inverse[0], inverse[1], inverse[2], inverse[3], cast);
            }
         } else {
            EncodedImage cast = (EncodedImage)image;
            int oldDecodeMode = 0;
            boolean var14 = false /* VF: Semaphore variable */;

            try {
               var14 = true;
               if (cast == null) {
                  var14 = false;
               } else {
                  oldDecodeMode = cast.getDecodeMode();
                  int bitmapType = cast.getBitmapType(0);
                  if (bitmapType != 129 && bitmapType != 1) {
                     this.g.drawImage(x, y, width, height, cast, 0, 0, 0);
                     var14 = false;
                  } else {
                     cast.setDecodeMode(oldDecodeMode | 4);
                     Bitmap bitmap = cast.getBitmap(0);
                     if (bitmap == null) {
                        var14 = false;
                     } else {
                        this.transformPoints(x, y, width, height, tempx, tempy);
                        int p = this.stackCurrentPosition - 9;
                        int[] inverse = new int[4];
                        VecMath.invert2x2Mat(this.matrixStack[p], this.matrixStack[p + 3], this.matrixStack[p + 1], this.matrixStack[p + 4], inverse, 0);
                        this.g.drawTexturedPath(tempx, tempy, null, null, tempx[0], tempy[0], inverse[0], inverse[1], inverse[2], inverse[3], bitmap);
                        var14 = false;
                     }
                  }
               }
            } finally {
               if (var14) {
                  if (cast != null) {
                     cast.setDecodeMode(oldDecodeMode);
                  }
               }
            }

            if (cast != null) {
               cast.setDecodeMode(oldDecodeMode);
               return;
            }
         }
      }
   }

   private final void transformPoints(int x, int y, int width, int height, int[] destX, int[] destY) {
      xTempArray[0] = x;
      xTempArray[1] = x + width;
      xTempArray[2] = x + width;
      xTempArray[3] = x;
      yTempArray[0] = y;
      yTempArray[1] = y;
      yTempArray[2] = y + height;
      yTempArray[3] = y + height;
      VecMath.transformPoints(this.matrixStack, this.stackCurrentPosition - 9, xTempArray, yTempArray, destX, destY);
   }

   private static final Font getFont(int font) {
      if (_family == null) {
         return null;
      }

      int hash = font % 5;
      if (hashKeys[hash] == font) {
         return (Font)hashValues[hash];
      }

      synchronized (hashKeys) {
         if (hashKeys[hash] == font) {
            return (Font)hashValues[hash];
         }

         int prefSize = 0;
         int prefStyle = 0;
         switch (font & -251723776) {
            case 1880162304:
               prefStyle |= 1;
               break;
            case 1880227840:
               prefStyle |= 2;
               break;
            case 1880293376:
               prefStyle = 3;
         }

         byte var13;
         switch (font & -268431616) {
            case 1879048448:
               var13 = 8;
               break;
            case 1879048704:
               var13 = 10;
               break;
            case 1879048960:
               var13 = 12;
               break;
            default:
               throw new IllegalArgumentException();
         }

         if (!_family.isStyleSupported(prefStyle)) {
            int closestStyle = prefStyle & -3;
            if (_family.isStyleSupported(closestStyle)) {
               prefStyle = closestStyle;
            } else {
               closestStyle = prefStyle & -2;
               if (_family.isStyleSupported(closestStyle)) {
                  prefStyle = closestStyle;
               } else {
                  prefStyle = closestStyle & -3;
               }
            }
         }

         int[] heights = _family.getHeights();
         int distance = 9999;
         Font fontObject = null;

         for (int i = 0; i < heights.length && distance != 0; i++) {
            int currDistance = Math.abs(heights[i] - var13);
            if (currDistance < distance) {
               Font currFont = _family.getFont(prefStyle, heights[i]);
               if (currFont != null) {
                  distance = currDistance;
                  fontObject = currFont;
               }
            }
         }

         if (fontObject == null) {
            throw new RuntimeException("Could not find font match.");
         }

         hashValues[hash] = fontObject;
         hashKeys[hash] = font;
         return fontObject;
      }
   }

   private static final Font getFont(
      int textAntiAliasMode, String fontFamilyString, int fontSize, int styleBitmask, int fontUnits, int[] nodesArray, int transformOffset, int key
   ) {
      Font fontObject;
      if (key == _fontKey) {
         fontObject = _cachedFont;
      } else {
         FontFamily family;
         try {
            family = FontFamily.forName(fontFamilyString);
         } finally {
            ;
         }

         int prefFontSize = fontSize;
         int prefAntiAliasMode = 1;
         int prefStyle = 0;
         int prefEffects = 0;
         int prefUnits = 0;
         if (textAntiAliasMode > 0) {
            prefAntiAliasMode = setTextAntiAliasMode(textAntiAliasMode);
         }

         prefStyle |= setFontStyles(styleBitmask);
         prefEffects |= setFontEffects(styleBitmask);
         if (fontUnits > 0) {
            prefUnits = setFontUnits(fontUnits);
         }

         int A = nodesArray[transformOffset + 0];
         int B = nodesArray[transformOffset + 1];
         int Tx = 0;
         int C = nodesArray[transformOffset + 3];
         int D = nodesArray[transformOffset + 4];
         int Ty = 0;
         fontObject = family.getFont(prefStyle, prefFontSize, prefUnits, prefAntiAliasMode, prefEffects, A, C, B, D, Tx, Ty);
         if (fontObject == null) {
            int closestSize = -1;
            int[] heights = family.getHeights();
            int distance = 9999;

            for (int i = 0; i < heights.length && distance != 0; i++) {
               int currDistance = Math.abs(heights[i] - prefFontSize);
               if (currDistance < distance) {
                  distance = currDistance;
                  closestSize = heights[i];
               }
            }

            fontObject = family.getFont(prefStyle, closestSize, 0, prefAntiAliasMode, prefEffects, A, C, B, D, Tx, Ty);
         }

         _cachedFont = fontObject;
         _fontKey = key;
      }

      return fontObject;
   }

   @Override
   public final int getFontHeight(int font) {
      Font fontObject = getFont(font);
      return fontObject != null ? fontObject.getHeight() : 0;
   }

   @Override
   public final int getFontAscent(int font) {
      Font fontObject = getFont(font);
      return fontObject != null ? fontObject.getAscent() : 0;
   }

   @Override
   public final int getFontAdvance(int font, String text) {
      Font fontObject = getFont(font);
      return fontObject != null ? fontObject.getAdvance(text) : 0;
   }

   @Override
   public final int getFontAdvance(
      char[] text,
      int offset,
      int length,
      int textAntiAliasMode,
      String fontFamilyString,
      int fontSize,
      int styleBitmask,
      int fontUnits,
      int[] nodesArray,
      int transformOffset,
      int key
   ) {
      Font fontObject = getFont(textAntiAliasMode, fontFamilyString, fontSize, styleBitmask, fontUnits, nodesArray, transformOffset, key);
      return fontObject != null ? fontObject.getAdvance(text, offset, length) : 0;
   }

   private static final int setFontUnits(int fontUnits) {
      int prefUnits = 0;
      switch (fontUnits) {
         case 10:
            return 2;
         case 20:
            return 4194306;
         case 30:
            return 3;
         case 40:
            return 4194307;
         case 50:
            return 0;
         case 60:
            return 4;
         case 70:
            return 4194308;
         case 80:
            return 2097156;
         case 90:
            return 1048580;
         case 100:
            return 262148;
         default:
            throw new IllegalArgumentException();
      }
   }

   private static final int setTextAntiAliasMode(int textAntiAliasMode) {
      int prefAntiAliasMode = 0;
      switch (textAntiAliasMode) {
         case 10:
            return 4;
         case 20:
            return 1;
         case 30:
            return 2;
         case 40:
            return 3;
         default:
            throw new IllegalArgumentException();
      }
   }

   private static final int setFontStyles(int styleBitmask) {
      int prefStyle = 0;
      if ((styleBitmask & 32768) != 0) {
         prefStyle |= 1;
      }

      if ((styleBitmask & 16384) != 0) {
         prefStyle |= 16;
      }

      if ((styleBitmask & 8192) != 0) {
         prefStyle |= 8;
      }

      if ((styleBitmask & 4096) != 0) {
         prefStyle |= 2;
      }

      if ((styleBitmask & 2048) != 0) {
         prefStyle |= 0;
      }

      if ((styleBitmask & 1024) != 0) {
         prefStyle |= 32;
      }

      if ((styleBitmask & 512) != 0) {
         prefStyle |= 4;
      }

      return prefStyle;
   }

   private static final int setFontEffects(int styleBitmask) {
      int prefEffects = 0;
      if ((styleBitmask & 256) != 0) {
         prefEffects |= 131072;
      }

      if ((styleBitmask & 128) != 0) {
         prefEffects |= 1536;
      }

      if ((styleBitmask & 64) != 0) {
         prefEffects |= 2048;
      }

      if ((styleBitmask & 32) != 0) {
         prefEffects |= 1024;
      }

      if ((styleBitmask & 16) != 0) {
         prefEffects |= 1280;
      }

      if ((styleBitmask & 8) != 0) {
         prefEffects |= 512;
      }

      if ((styleBitmask & 4) != 0) {
         prefEffects |= 65536;
      }

      if ((styleBitmask & 2) != 0) {
         prefEffects |= 256;
      }

      return prefEffects;
   }

   @Override
   public final void pushClip(int x, int y, int width, int height) {
      this.g.pushContext(x, y, width, height, 0, 0);
   }

   @Override
   public final void popClip() {
      this.g.popContext();
   }

   @Override
   public final void clear(int color) {
      this.g.setBackgroundColor(color);
      this.g.clear();
   }

   @Override
   public final void getCurrentTransformation(int[] matrix, int index) {
      System.arraycopy(this.matrixStack, this.stackCurrentPosition - 9, matrix, index, 9);
   }
}
