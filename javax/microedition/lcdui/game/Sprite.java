package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Sprite extends Layer {
   Image sourceImage;
   int numberFrames;
   int[] frameCoordsX;
   int[] frameCoordsY;
   int srcFrameWidth;
   int srcFrameHeight;
   int[] frameSequence;
   private int sequenceIndex;
   private boolean customSequenceDefined;
   int dRefX;
   int dRefY;
   int collisionRectX;
   int collisionRectY;
   int collisionRectWidth;
   int collisionRectHeight;
   int t_currentTransformation;
   int t_collisionRectX;
   int t_collisionRectY;
   int t_collisionRectWidth;
   int t_collisionRectHeight;
   public static final int TRANS_NONE = 0;
   public static final int TRANS_ROT90 = 5;
   public static final int TRANS_ROT180 = 3;
   public static final int TRANS_ROT270 = 6;
   public static final int TRANS_MIRROR = 2;
   public static final int TRANS_MIRROR_ROT90 = 7;
   public static final int TRANS_MIRROR_ROT180 = 1;
   public static final int TRANS_MIRROR_ROT270 = 4;
   private static final int INVERTED_AXES = 4;
   private static final int X_FLIP = 2;
   private static final int Y_FLIP = 1;
   private static final int ALPHA_BITMASK = -16777216;

   public Sprite(Image image) {
      super(image.getWidth(), image.getHeight());
      this.initializeFrames(image, image.getWidth(), image.getHeight(), false);
      this.initCollisionRectBounds();
      this.setTransformImpl(0);
   }

   public Sprite(Image image, int frameWidth, int frameHeight) {
      super(frameWidth, frameHeight);
      if (frameWidth >= 1 && frameHeight >= 1 && image.getWidth() % frameWidth == 0 && image.getHeight() % frameHeight == 0) {
         this.initializeFrames(image, frameWidth, frameHeight, false);
         this.initCollisionRectBounds();
         this.setTransformImpl(0);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Sprite(Sprite s) {
      super(s != null ? s.getWidth() : 0, s != null ? s.getHeight() : 0);
      if (s == null) {
         throw new NullPointerException();
      }

      this.sourceImage = Image.createImage(s.sourceImage);
      this.numberFrames = s.numberFrames;
      this.frameCoordsX = new int[this.numberFrames];
      this.frameCoordsY = new int[this.numberFrames];
      System.arraycopy(s.frameCoordsX, 0, this.frameCoordsX, 0, s.getRawFrameCount());
      System.arraycopy(s.frameCoordsY, 0, this.frameCoordsY, 0, s.getRawFrameCount());
      super.x = s.getX();
      super.y = s.getY();
      this.dRefX = s.dRefX;
      this.dRefY = s.dRefY;
      this.collisionRectX = s.collisionRectX;
      this.collisionRectY = s.collisionRectY;
      this.collisionRectWidth = s.collisionRectWidth;
      this.collisionRectHeight = s.collisionRectHeight;
      this.srcFrameWidth = s.srcFrameWidth;
      this.srcFrameHeight = s.srcFrameHeight;
      this.setTransformImpl(s.t_currentTransformation);
      this.setVisible(s.isVisible());
      this.frameSequence = new int[s.getFrameSequenceLength()];
      this.setFrameSequence(s.frameSequence);
      this.setFrame(s.getFrame());
      this.setRefPixelPosition(s.getRefPixelX(), s.getRefPixelY());
   }

   public void defineReferencePixel(int x, int y) {
      this.dRefX = x;
      this.dRefY = y;
   }

   public void setRefPixelPosition(int x, int y) {
      super.x = x - this.getTransformedPtX(this.dRefX, this.dRefY, this.t_currentTransformation);
      super.y = y - this.getTransformedPtY(this.dRefX, this.dRefY, this.t_currentTransformation);
   }

   public int getRefPixelX() {
      return super.x + this.getTransformedPtX(this.dRefX, this.dRefY, this.t_currentTransformation);
   }

   public int getRefPixelY() {
      return super.y + this.getTransformedPtY(this.dRefX, this.dRefY, this.t_currentTransformation);
   }

   public void setFrame(int sequenceIndex) {
      if (sequenceIndex >= 0 && sequenceIndex < this.frameSequence.length) {
         this.sequenceIndex = sequenceIndex;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int getFrame() {
      return this.sequenceIndex;
   }

   public int getRawFrameCount() {
      return this.numberFrames;
   }

   public int getFrameSequenceLength() {
      return this.frameSequence.length;
   }

   public void nextFrame() {
      this.sequenceIndex = (this.sequenceIndex + 1) % this.frameSequence.length;
   }

   public void prevFrame() {
      if (this.sequenceIndex == 0) {
         this.sequenceIndex = this.frameSequence.length - 1;
      } else {
         this.sequenceIndex--;
      }
   }

   @Override
   public final void paint(Graphics g) {
      if (g == null) {
         throw new NullPointerException();
      }

      if (super.visible) {
         g.drawRegion(
            this.sourceImage,
            this.frameCoordsX[this.frameSequence[this.sequenceIndex]],
            this.frameCoordsY[this.frameSequence[this.sequenceIndex]],
            this.srcFrameWidth,
            this.srcFrameHeight,
            this.t_currentTransformation,
            super.x,
            super.y,
            20
         );
      }
   }

   public void setFrameSequence(int[] sequence) {
      if (sequence == null) {
         this.sequenceIndex = 0;
         this.customSequenceDefined = false;
         this.frameSequence = new int[this.numberFrames];
         int i = 0;

         while (i < this.numberFrames) {
            this.frameSequence[i] = i++;
         }
      } else {
         if (sequence.length < 1) {
            throw new IllegalArgumentException();
         }

         for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] < 0 || sequence[i] >= this.numberFrames) {
               throw new ArrayIndexOutOfBoundsException();
            }
         }

         this.customSequenceDefined = true;
         this.frameSequence = new int[sequence.length];
         System.arraycopy(sequence, 0, this.frameSequence, 0, sequence.length);
         this.sequenceIndex = 0;
      }
   }

   public void setImage(Image img, int frameWidth, int frameHeight) {
      if (frameWidth >= 1 && frameHeight >= 1 && img.getWidth() % frameWidth == 0 && img.getHeight() % frameHeight == 0) {
         int noOfFrames = img.getWidth() / frameWidth * (img.getHeight() / frameHeight);
         boolean maintainCurFrame = true;
         if (noOfFrames < this.numberFrames) {
            maintainCurFrame = false;
            this.customSequenceDefined = false;
         }

         if (this.srcFrameWidth == frameWidth && this.srcFrameHeight == frameHeight) {
            this.initializeFrames(img, frameWidth, frameHeight, maintainCurFrame);
         } else {
            int oldX = super.x + this.getTransformedPtX(this.dRefX, this.dRefY, this.t_currentTransformation);
            int oldY = super.y + this.getTransformedPtY(this.dRefX, this.dRefY, this.t_currentTransformation);
            this.setWidthImpl(frameWidth);
            this.setHeightImpl(frameHeight);
            this.initializeFrames(img, frameWidth, frameHeight, maintainCurFrame);
            this.initCollisionRectBounds();
            super.x = oldX - this.getTransformedPtX(this.dRefX, this.dRefY, this.t_currentTransformation);
            super.y = oldY - this.getTransformedPtY(this.dRefX, this.dRefY, this.t_currentTransformation);
            this.computeTransformedBounds(this.t_currentTransformation);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void defineCollisionRectangle(int x, int y, int width, int height) {
      if (width >= 0 && height >= 0) {
         this.collisionRectX = x;
         this.collisionRectY = y;
         this.collisionRectWidth = width;
         this.collisionRectHeight = height;
         this.setTransformImpl(this.t_currentTransformation);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setTransform(int transform) {
      this.setTransformImpl(transform);
   }

   public final boolean collidesWith(Sprite s, boolean pixelLevel) {
      if (s.visible && super.visible) {
         int otherLeft = s.x + s.t_collisionRectX;
         int otherTop = s.y + s.t_collisionRectY;
         int otherRight = otherLeft + s.t_collisionRectWidth;
         int otherBottom = otherTop + s.t_collisionRectHeight;
         int left = super.x + this.t_collisionRectX;
         int top = super.y + this.t_collisionRectY;
         int right = left + this.t_collisionRectWidth;
         int bottom = top + this.t_collisionRectHeight;
         if (!this.intersectRect(otherLeft, otherTop, otherRight, otherBottom, left, top, right, bottom)) {
            return false;
         }

         if (!pixelLevel) {
            return true;
         }

         if (this.t_collisionRectX < 0) {
            left = super.x;
         }

         if (this.t_collisionRectY < 0) {
            top = super.y;
         }

         if (this.t_collisionRectX + this.t_collisionRectWidth > super.width) {
            right = super.x + super.width;
         }

         if (this.t_collisionRectY + this.t_collisionRectHeight > super.height) {
            bottom = super.y + super.height;
         }

         if (s.t_collisionRectX < 0) {
            otherLeft = s.x;
         }

         if (s.t_collisionRectY < 0) {
            otherTop = s.y;
         }

         if (s.t_collisionRectX + s.t_collisionRectWidth > s.width) {
            otherRight = s.x + s.width;
         }

         if (s.t_collisionRectY + s.t_collisionRectHeight > s.height) {
            otherBottom = s.y + s.height;
         }

         if (!this.intersectRect(otherLeft, otherTop, otherRight, otherBottom, left, top, right, bottom)) {
            return false;
         }

         int intersectLeft = left < otherLeft ? otherLeft : left;
         int intersectTop = top < otherTop ? otherTop : top;
         int intersectRight = right < otherRight ? right : otherRight;
         int intersectBottom = bottom < otherBottom ? bottom : otherBottom;
         int intersectWidth = Math.abs(intersectRight - intersectLeft);
         int intersectHeight = Math.abs(intersectBottom - intersectTop);
         int thisImageXOffset = this.getImageTopLeftX(intersectLeft, intersectTop, intersectRight, intersectBottom);
         int thisImageYOffset = this.getImageTopLeftY(intersectLeft, intersectTop, intersectRight, intersectBottom);
         int otherImageXOffset = s.getImageTopLeftX(intersectLeft, intersectTop, intersectRight, intersectBottom);
         int otherImageYOffset = s.getImageTopLeftY(intersectLeft, intersectTop, intersectRight, intersectBottom);
         return doPixelCollision(
            thisImageXOffset,
            thisImageYOffset,
            otherImageXOffset,
            otherImageYOffset,
            this.sourceImage,
            this.t_currentTransformation,
            s.sourceImage,
            s.t_currentTransformation,
            intersectWidth,
            intersectHeight
         );
      } else {
         return false;
      }
   }

   public final boolean collidesWith(TiledLayer t, boolean pixelLevel) {
      if (t.visible && super.visible) {
         int tLx1 = t.x;
         int tLy1 = t.y;
         int tLx2 = tLx1 + t.width;
         int tLy2 = tLy1 + t.height;
         int tW = t.getCellWidth();
         int tH = t.getCellHeight();
         int sx1 = super.x + this.t_collisionRectX;
         int sy1 = super.y + this.t_collisionRectY;
         int sx2 = sx1 + this.t_collisionRectWidth;
         int sy2 = sy1 + this.t_collisionRectHeight;
         int tNumCols = t.getColumns();
         int tNumRows = t.getRows();
         if (!this.intersectRect(tLx1, tLy1, tLx2, tLy2, sx1, sy1, sx2, sy2)) {
            return false;
         }

         int startCol = sx1 <= tLx1 ? 0 : (sx1 - tLx1) / tW;
         int startRow = sy1 <= tLy1 ? 0 : (sy1 - tLy1) / tH;
         int endCol = sx2 < tLx2 ? (sx2 - 1 - tLx1) / tW : tNumCols - 1;
         int endRow = sy2 < tLy2 ? (sy2 - 1 - tLy1) / tH : tNumRows - 1;
         if (!pixelLevel) {
            for (int row = startRow; row <= endRow; row++) {
               for (int col = startCol; col <= endCol; col++) {
                  if (t.getCell(col, row) != 0) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            if (this.t_collisionRectX < 0) {
               sx1 = super.x;
            }

            if (this.t_collisionRectY < 0) {
               sy1 = super.y;
            }

            if (this.t_collisionRectX + this.t_collisionRectWidth > super.width) {
               sx2 = super.x + super.width;
            }

            if (this.t_collisionRectY + this.t_collisionRectHeight > super.height) {
               sy2 = super.y + super.height;
            }

            if (!this.intersectRect(tLx1, tLy1, tLx2, tLy2, sx1, sy1, sx2, sy2)) {
               return false;
            }

            startCol = sx1 <= tLx1 ? 0 : (sx1 - tLx1) / tW;
            startRow = sy1 <= tLy1 ? 0 : (sy1 - tLy1) / tH;
            endCol = sx2 < tLx2 ? (sx2 - 1 - tLx1) / tW : tNumCols - 1;
            endRow = sy2 < tLy2 ? (sy2 - 1 - tLy1) / tH : tNumRows - 1;
            int cellTop = startRow * tH + tLy1;
            int cellBottom = cellTop + tH;

            for (int row = startRow; row <= endRow; cellBottom += tH) {
               int cellLeft = startCol * tW + tLx1;
               int cellRight = cellLeft + tW;

               for (int col = startCol; col <= endCol; cellRight += tW) {
                  int tileIndex = t.getCell(col, row);
                  if (tileIndex != 0) {
                     int intersectLeft = sx1 < cellLeft ? cellLeft : sx1;
                     int intersectTop = sy1 < cellTop ? cellTop : sy1;
                     int intersectRight = sx2 < cellRight ? sx2 : cellRight;
                     int intersectBottom = sy2 < cellBottom ? sy2 : cellBottom;
                     if (intersectLeft > intersectRight) {
                        int temp = intersectRight;
                        intersectRight = intersectLeft;
                        intersectLeft = temp;
                     }

                     if (intersectTop > intersectBottom) {
                        int temp = intersectBottom;
                        intersectBottom = intersectTop;
                        intersectTop = temp;
                     }

                     int intersectWidth = intersectRight - intersectLeft;
                     int intersectHeight = intersectBottom - intersectTop;
                     int image1XOffset = this.getImageTopLeftX(intersectLeft, intersectTop, intersectRight, intersectBottom);
                     int image1YOffset = this.getImageTopLeftY(intersectLeft, intersectTop, intersectRight, intersectBottom);
                     int image2XOffset = t.tileSetX[tileIndex] + (intersectLeft - cellLeft);
                     int image2YOffset = t.tileSetY[tileIndex] + (intersectTop - cellTop);
                     if (doPixelCollision(
                        image1XOffset,
                        image1YOffset,
                        image2XOffset,
                        image2YOffset,
                        this.sourceImage,
                        this.t_currentTransformation,
                        t.sourceImage,
                        0,
                        intersectWidth,
                        intersectHeight
                     )) {
                        return true;
                     }
                  }

                  col++;
                  cellLeft += tW;
               }

               row++;
               cellTop += tH;
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public final boolean collidesWith(Image image, int x, int y, boolean pixelLevel) {
      if (!super.visible) {
         return false;
      }

      int otherLeft = x;
      int otherTop = y;
      int otherRight = x + image.getWidth();
      int otherBottom = y + image.getHeight();
      int left = super.x + this.t_collisionRectX;
      int top = super.y + this.t_collisionRectY;
      int right = left + this.t_collisionRectWidth;
      int bottom = top + this.t_collisionRectHeight;
      if (!this.intersectRect(otherLeft, otherTop, otherRight, otherBottom, left, top, right, bottom)) {
         return false;
      }

      if (!pixelLevel) {
         return true;
      }

      if (this.t_collisionRectX < 0) {
         left = super.x;
      }

      if (this.t_collisionRectY < 0) {
         top = super.y;
      }

      if (this.t_collisionRectX + this.t_collisionRectWidth > super.width) {
         right = super.x + super.width;
      }

      if (this.t_collisionRectY + this.t_collisionRectHeight > super.height) {
         bottom = super.y + super.height;
      }

      if (!this.intersectRect(otherLeft, otherTop, otherRight, otherBottom, left, top, right, bottom)) {
         return false;
      }

      int intersectLeft = left < otherLeft ? otherLeft : left;
      int intersectTop = top < otherTop ? otherTop : top;
      int intersectRight = right < otherRight ? right : otherRight;
      int intersectBottom = bottom < otherBottom ? bottom : otherBottom;
      int intersectWidth = Math.abs(intersectRight - intersectLeft);
      int intersectHeight = Math.abs(intersectBottom - intersectTop);
      int thisImageXOffset = this.getImageTopLeftX(intersectLeft, intersectTop, intersectRight, intersectBottom);
      int thisImageYOffset = this.getImageTopLeftY(intersectLeft, intersectTop, intersectRight, intersectBottom);
      int otherImageXOffset = intersectLeft - x;
      int otherImageYOffset = intersectTop - y;
      return doPixelCollision(
         thisImageXOffset,
         thisImageYOffset,
         otherImageXOffset,
         otherImageYOffset,
         this.sourceImage,
         this.t_currentTransformation,
         image,
         0,
         intersectWidth,
         intersectHeight
      );
   }

   private void initializeFrames(Image image, int fWidth, int fHeight, boolean maintainCurFrame) {
      int imageW = image.getWidth();
      int imageH = image.getHeight();
      int numHorizontalFrames = imageW / fWidth;
      int numVerticalFrames = imageH / fHeight;
      this.sourceImage = image;
      this.srcFrameWidth = fWidth;
      this.srcFrameHeight = fHeight;
      this.numberFrames = numHorizontalFrames * numVerticalFrames;
      this.frameCoordsX = new int[this.numberFrames];
      this.frameCoordsY = new int[this.numberFrames];
      if (!maintainCurFrame) {
         this.sequenceIndex = 0;
      }

      if (!this.customSequenceDefined) {
         this.frameSequence = new int[this.numberFrames];
      }

      int currentFrame = 0;

      for (int yy = 0; yy < imageH; yy += fHeight) {
         for (int xx = 0; xx < imageW; xx += fWidth) {
            this.frameCoordsX[currentFrame] = xx;
            this.frameCoordsY[currentFrame] = yy;
            if (!this.customSequenceDefined) {
               this.frameSequence[currentFrame] = currentFrame;
            }

            currentFrame++;
         }
      }
   }

   private void initCollisionRectBounds() {
      this.collisionRectX = 0;
      this.collisionRectY = 0;
      this.collisionRectWidth = super.width;
      this.collisionRectHeight = super.height;
   }

   private boolean intersectRect(int r1x1, int r1y1, int r1x2, int r1y2, int r2x1, int r2y1, int r2x2, int r2y2) {
      return r2x1 < r1x2 && r2y1 < r1y2 && r2x2 > r1x1 && r2y2 > r1y1;
   }

   private static boolean doPixelCollision(
      int image1XOffset,
      int image1YOffset,
      int image2XOffset,
      int image2YOffset,
      Image image1,
      int transform1,
      Image image2,
      int transform2,
      int width,
      int height
   ) {
      int numPixels = height * width;
      int[] argbData1 = new int[numPixels];
      int[] argbData2 = new int[numPixels];
      int startY1;
      int xIncr1;
      int yIncr1;
      if (0 != (transform1 & 4)) {
         if (0 != (transform1 & 1)) {
            xIncr1 = -height;
            startY1 = numPixels - height;
         } else {
            xIncr1 = height;
            startY1 = 0;
         }

         if (0 != (transform1 & 2)) {
            yIncr1 = -1;
            startY1 += height - 1;
         } else {
            yIncr1 = 1;
         }

         image1.getRGB(argbData1, 0, height, image1XOffset, image1YOffset, height, width);
      } else {
         if (0 != (transform1 & 1)) {
            startY1 = numPixels - width;
            yIncr1 = -width;
         } else {
            startY1 = 0;
            yIncr1 = width;
         }

         if (0 != (transform1 & 2)) {
            xIncr1 = -1;
            startY1 += width - 1;
         } else {
            xIncr1 = 1;
         }

         image1.getRGB(argbData1, 0, width, image1XOffset, image1YOffset, width, height);
      }

      int startY2;
      int xIncr2;
      int yIncr2;
      if (0 != (transform2 & 4)) {
         if (0 != (transform2 & 1)) {
            xIncr2 = -height;
            startY2 = numPixels - height;
         } else {
            xIncr2 = height;
            startY2 = 0;
         }

         if (0 != (transform2 & 2)) {
            yIncr2 = -1;
            startY2 += height - 1;
         } else {
            yIncr2 = 1;
         }

         image2.getRGB(argbData2, 0, height, image2XOffset, image2YOffset, height, width);
      } else {
         if (0 != (transform2 & 1)) {
            startY2 = numPixels - width;
            yIncr2 = -width;
         } else {
            startY2 = 0;
            yIncr2 = width;
         }

         if (0 != (transform2 & 2)) {
            xIncr2 = -1;
            startY2 += width - 1;
         } else {
            xIncr2 = 1;
         }

         image2.getRGB(argbData2, 0, width, image2XOffset, image2YOffset, width, height);
      }

      int numIterRows = 0;
      int xLocalBegin1 = startY1;
      int xLocalBegin2 = startY2;

      while (numIterRows < height) {
         int numIterColumns = 0;
         int x1 = xLocalBegin1;
         int x2 = xLocalBegin2;

         while (numIterColumns < width) {
            if ((argbData1[x1] & 0xFF000000) != 0 && (argbData2[x2] & 0xFF000000) != 0) {
               return true;
            }

            x1 += xIncr1;
            x2 += xIncr2;
            numIterColumns++;
         }

         xLocalBegin1 += yIncr1;
         xLocalBegin2 += yIncr2;
         numIterRows++;
      }

      return false;
   }

   private int getImageTopLeftX(int x1, int y1, int x2, int y2) {
      int retX = 0;
      switch (this.t_currentTransformation) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            retX = x1 - super.x;
            break;
         case 2:
         case 3:
            retX = super.x + super.width - x2;
            break;
         case 4:
         case 5:
            retX = y1 - super.y;
            break;
         case 6:
         case 7:
            retX = super.y + super.height - y2;
      }

      return retX + this.frameCoordsX[this.frameSequence[this.sequenceIndex]];
   }

   private int getImageTopLeftY(int x1, int y1, int x2, int y2) {
      int retY = 0;
      switch (this.t_currentTransformation) {
         case -1:
            break;
         case 0:
         case 2:
         default:
            retY = y1 - super.y;
            break;
         case 1:
         case 3:
            retY = super.y + super.height - y2;
            break;
         case 4:
         case 6:
            retY = x1 - super.x;
            break;
         case 5:
         case 7:
            retY = super.x + super.width - x2;
      }

      return retY + this.frameCoordsY[this.frameSequence[this.sequenceIndex]];
   }

   private void setTransformImpl(int transform) {
      super.x = super.x
         + this.getTransformedPtX(this.dRefX, this.dRefY, this.t_currentTransformation)
         - this.getTransformedPtX(this.dRefX, this.dRefY, transform);
      super.y = super.y
         + this.getTransformedPtY(this.dRefX, this.dRefY, this.t_currentTransformation)
         - this.getTransformedPtY(this.dRefX, this.dRefY, transform);
      this.computeTransformedBounds(transform);
      this.t_currentTransformation = transform;
   }

   private void computeTransformedBounds(int transform) {
      switch (transform) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            this.t_collisionRectX = this.collisionRectX;
            this.t_collisionRectY = this.collisionRectY;
            this.t_collisionRectWidth = this.collisionRectWidth;
            this.t_collisionRectHeight = this.collisionRectHeight;
            super.width = this.srcFrameWidth;
            super.height = this.srcFrameHeight;
            return;
         case 1:
            this.t_collisionRectY = this.srcFrameHeight - (this.collisionRectY + this.collisionRectHeight);
            this.t_collisionRectX = this.collisionRectX;
            this.t_collisionRectWidth = this.collisionRectWidth;
            this.t_collisionRectHeight = this.collisionRectHeight;
            super.width = this.srcFrameWidth;
            super.height = this.srcFrameHeight;
            return;
         case 2:
            this.t_collisionRectX = this.srcFrameWidth - (this.collisionRectX + this.collisionRectWidth);
            this.t_collisionRectY = this.collisionRectY;
            this.t_collisionRectWidth = this.collisionRectWidth;
            this.t_collisionRectHeight = this.collisionRectHeight;
            super.width = this.srcFrameWidth;
            super.height = this.srcFrameHeight;
            return;
         case 3:
            this.t_collisionRectX = this.srcFrameWidth - (this.collisionRectWidth + this.collisionRectX);
            this.t_collisionRectY = this.srcFrameHeight - (this.collisionRectHeight + this.collisionRectY);
            this.t_collisionRectWidth = this.collisionRectWidth;
            this.t_collisionRectHeight = this.collisionRectHeight;
            super.width = this.srcFrameWidth;
            super.height = this.srcFrameHeight;
            return;
         case 4:
            this.t_collisionRectY = this.collisionRectX;
            this.t_collisionRectX = this.collisionRectY;
            this.t_collisionRectHeight = this.collisionRectWidth;
            this.t_collisionRectWidth = this.collisionRectHeight;
            super.width = this.srcFrameHeight;
            super.height = this.srcFrameWidth;
            return;
         case 5:
            this.t_collisionRectX = this.srcFrameHeight - (this.collisionRectHeight + this.collisionRectY);
            this.t_collisionRectY = this.collisionRectX;
            this.t_collisionRectHeight = this.collisionRectWidth;
            this.t_collisionRectWidth = this.collisionRectHeight;
            super.width = this.srcFrameHeight;
            super.height = this.srcFrameWidth;
            return;
         case 6:
            this.t_collisionRectX = this.collisionRectY;
            this.t_collisionRectY = this.srcFrameWidth - (this.collisionRectWidth + this.collisionRectX);
            this.t_collisionRectHeight = this.collisionRectWidth;
            this.t_collisionRectWidth = this.collisionRectHeight;
            super.width = this.srcFrameHeight;
            super.height = this.srcFrameWidth;
            return;
         case 7:
            this.t_collisionRectX = this.srcFrameHeight - (this.collisionRectHeight + this.collisionRectY);
            this.t_collisionRectY = this.srcFrameWidth - (this.collisionRectWidth + this.collisionRectX);
            this.t_collisionRectHeight = this.collisionRectWidth;
            this.t_collisionRectWidth = this.collisionRectHeight;
            super.width = this.srcFrameHeight;
            super.height = this.srcFrameWidth;
      }
   }

   int getTransformedPtX(int x, int y, int transform) {
      int t_x = 0;
      switch (transform) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            t_x = x;
            break;
         case 1:
            t_x = x;
            break;
         case 2:
            t_x = this.srcFrameWidth - x - 1;
            break;
         case 3:
            t_x = this.srcFrameWidth - x - 1;
            break;
         case 4:
            t_x = y;
            break;
         case 5:
            t_x = this.srcFrameHeight - y - 1;
            break;
         case 6:
            t_x = y;
            break;
         case 7:
            t_x = this.srcFrameHeight - y - 1;
      }

      return t_x;
   }

   int getTransformedPtY(int x, int y, int transform) {
      int t_y = 0;
      switch (transform) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            t_y = y;
            break;
         case 1:
            t_y = this.srcFrameHeight - y - 1;
            break;
         case 2:
            t_y = y;
            break;
         case 3:
            t_y = this.srcFrameHeight - y - 1;
            break;
         case 4:
            t_y = x;
            break;
         case 5:
            t_y = x;
            break;
         case 6:
            t_y = this.srcFrameWidth - x - 1;
            break;
         case 7:
            t_y = this.srcFrameWidth - x - 1;
      }

      return t_y;
   }
}
