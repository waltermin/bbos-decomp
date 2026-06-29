package net.rim.device.apps.api.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.MathUtilities;

final class ZoomImageCropper$ZoomBitmapFieldExt extends ZoomBitmapField {
   private long _zicStyle;
   private int _originalCropX;
   private int _originalCropY;
   private int _cropX;
   private int _cropY;
   private int _cropWidth;
   private int _cropHeight;
   private boolean _modified;
   private final ZoomImageCropper this$0;

   public ZoomImageCropper$ZoomBitmapFieldExt(ZoomImageCropper _1, EncodedImage image, long zicStyle, long style) {
      super(style);
      this.this$0 = _1;
      this.setImage(image);
      this.setMinimumZoomFactorFlag(false);
      this._zicStyle = zicStyle;
   }

   private final int adjustCropOnAxis(int crop, int originalCrop, int fieldExtent, int imageExtent, int cropExtent, int amount) {
      if (!this.this$0._initialised) {
         return originalCrop;
      }

      int deltaLower;
      if (imageExtent < fieldExtent) {
         deltaLower = fieldExtent - imageExtent >> 1;
      } else {
         deltaLower = 0;
      }

      int deltaUpper = fieldExtent - cropExtent - deltaLower;
      int lower;
      int upper;
      if (crop > originalCrop) {
         lower = originalCrop;
         upper = deltaUpper;
      } else if (crop < originalCrop) {
         lower = deltaLower;
         upper = originalCrop;
      } else {
         lower = deltaLower;
         upper = deltaUpper;
      }

      return MathUtilities.clamp(lower, crop + amount, upper);
   }

   private final void adjustCropX(int amount) {
      int imWidth = this.getBitmapWidth();
      int oldCropX = this._cropX;
      if (this._cropWidth >= imWidth) {
         this._cropX = this._originalCropX;
      } else {
         this._cropX = this.adjustCropOnAxis(
            this._cropX, this._originalCropX, this.getContentWidth(), imWidth, this._cropWidth, amount * this.getScrollStepSetX()
         );
      }

      this.invalidate(Math.min(this._cropX, oldCropX), this._cropY, this._cropWidth + Math.abs(this._cropX - oldCropX), this._cropHeight);
   }

   private final void adjustCropY(int amount) {
      int imHeight = this.getBitmapHeight();
      int oldCropY = this._cropY;
      if (this._cropHeight >= imHeight) {
         this._cropY = this._originalCropY;
      } else {
         this._cropY = this.adjustCropOnAxis(
            this._cropY, this._originalCropY, this.getContentHeight(), imHeight, this._cropHeight, amount * this.getScrollStepSetY()
         );
      }

      this.invalidate(this._cropX, Math.min(this._cropY, oldCropY), this._cropWidth, this._cropHeight + Math.abs(this._cropY - oldCropY));
   }

   final int getCropHeight() {
      return this._cropHeight;
   }

   final int getCropWidth() {
      return this._cropWidth;
   }

   private final int getFinalOffset(int imageExtent, int imageOffset, int screenExtent, int crop, int cropExtent, int scale) {
      int offset = 0;
      if (imageExtent > cropExtent) {
         offset = crop;
         if (imageExtent <= screenExtent) {
            offset -= screenExtent - imageExtent >> 1;
         } else {
            offset += imageOffset;
         }

         if (offset > 0) {
            offset = Fixed32.toInt(Fixed32.mul(Fixed32.toFP(offset), scale));
         }
      }

      return offset;
   }

   final int getFinalOffsetX(int scale) {
      return this.getFinalOffset(this.getBitmapWidth(), this.getTopX(), this.getContentWidth(), this._cropX, this._cropWidth, scale);
   }

   final int getFinalOffsetY(int scale) {
      return this.getFinalOffset(this.getBitmapHeight(), this.getTopY(), this.getContentHeight(), this._cropY, this._cropHeight, scale);
   }

   final boolean isModified() {
      return this._modified;
   }

   final void layoutHack(int width, int height) {
      this.layout(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      super.paint(graphics);
      if ((this._zicStyle & 1) != 0) {
         graphics.invert(this._cropX, this._cropY, this._cropWidth, 1);
         graphics.invert(this._cropX, this._cropY, 1, this._cropHeight);
         graphics.invert(this._cropX, this._cropY + this._cropHeight - 1, this._cropWidth, 1);
         graphics.invert(this._cropX + this._cropWidth - 1, this._cropY, 1, this._cropHeight);
         int oldColour = graphics.getColor();
         graphics.setColor(16777215);
         int oldGlobalAlpha = graphics.getGlobalAlpha();
         graphics.setGlobalAlpha(128);
         int width = this.getContentWidth();
         int height = this.getContentHeight();
         boolean cropYGT0 = this._cropY > 0;
         int cropX2 = this._cropX + this._cropWidth;
         int cropY2 = this._cropY + this._cropHeight;
         boolean cropHeightLTHeight = cropY2 < height;
         int rightCropWidth = width - cropX2;
         int bottomCropHeight = height - cropY2;
         if (this._cropX > 0) {
            graphics.fillRect(0, this._cropY, this._cropX, this._cropHeight);
            if (cropYGT0) {
               graphics.fillRect(0, 0, this._cropX, this._cropY);
            }

            if (cropHeightLTHeight) {
               graphics.fillRect(0, cropY2, this._cropX, bottomCropHeight);
            }
         }

         if (cropX2 < width) {
            graphics.fillRect(cropX2, this._cropY, rightCropWidth, this._cropHeight);
            if (cropYGT0) {
               graphics.fillRect(cropX2, 0, rightCropWidth, this._cropY);
            }

            if (cropHeightLTHeight) {
               graphics.fillRect(cropX2, cropY2, rightCropWidth, bottomCropHeight);
            }
         }

         if (cropYGT0) {
            graphics.fillRect(this._cropX, 0, this._cropWidth, this._cropY);
         }

         if (cropHeightLTHeight) {
            graphics.fillRect(this._cropX, cropY2, this._cropWidth, bottomCropHeight);
         }

         graphics.setColor(oldColour);
         graphics.setGlobalAlpha(oldGlobalAlpha);
      }

      if ((this._zicStyle & 2) != 0) {
         int oldColour = graphics.getColor();
         graphics.setColor(16777215);
         int bWidth = this.getBitmapWidth();
         int bHeight = this.getBitmapHeight();
         int diameter = Math.min(this._cropWidth, this._cropHeight);
         int fieldHeight = this.getContentHeight();
         int yOffset = 0;
         if ((this._zicStyle & 1) != 0) {
            yOffset = Math.max(0, bHeight - diameter >> 1);
         }

         int x = diameter < this._cropWidth ? Math.max(this._cropX, this.getContentWidth() - bWidth >> 1) : this._cropX;
         int y = MathUtilities.clamp(
            this._cropY,
            Math.min(Math.max(this._cropY, fieldHeight - bHeight >> 1) + yOffset, (this._cropHeight - diameter >> 1) + this._cropY),
            fieldHeight - diameter
         );
         graphics.drawArc(x + 1, y + 1, diameter - 2, diameter - 2, 0, 360);
         graphics.setColor(oldColour);
      }
   }

   final void paintHack(Graphics graphics) {
      this.paint(graphics);
   }

   @Override
   public final boolean rotateStep() {
      boolean retVal = super.rotateStep();
      this._modified |= retVal;
      return retVal;
   }

   @Override
   public final boolean scroll(int dx, int dy) {
      boolean handled = false;
      if (dx == 0 && dy == 0 || dx != 0 && this._cropX == this._originalCropX || dy != 0 && this._cropY == this._originalCropY) {
         handled = super.scroll(dx, dy);
      }

      if (!handled) {
         if (dx != 0) {
            this.adjustCropX(dx);
         }

         if (dy != 0) {
            this.adjustCropY(dy);
         }

         handled = true;
      }

      return handled;
   }

   final void resetCropOrigin() {
      this.invalidate(
         Math.min(this._cropX, this._originalCropX),
         Math.min(this._cropY, this._originalCropY),
         this._cropWidth + Math.abs(this._cropX - this._originalCropX),
         this._cropHeight + Math.abs(this._cropY - this._originalCropY)
      );
      this._cropX = this._originalCropX;
      this._cropY = this._originalCropY;
      this._modified = false;
   }

   final void setCropRect(int cropX, int cropY, int cropWidth, int cropHeight) {
      this._originalCropX = cropX;
      this._originalCropY = cropY;
      this._cropX = cropX;
      this._cropY = cropY;
      this._cropWidth = cropWidth;
      this._cropHeight = cropHeight;
   }

   @Override
   final void zoom(int crtZoomValue, boolean doDefaultXCentre, boolean doDefaultYCentre) {
      super.zoom(crtZoomValue, doDefaultXCentre, doDefaultYCentre);
      this._cropX = this._originalCropX;
      this.adjustCropX(0);
      this._cropY = this._originalCropY;
      this.adjustCropY(0);
      this.invalidate();
      this._modified = true;
   }
}
