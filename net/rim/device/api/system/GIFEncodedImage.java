package net.rim.device.api.system;

import net.rim.device.api.math.Fixed32;

public final class GIFEncodedImage extends EncodedImage {
   private GIFEncodedImage$GIFImageInfo _gifInfo;
   private GIFEncodedImage$GIFFrameInfo[] _gifFrameInfo;
   public static final int TRANSITION_DEFAULT;
   public static final int TRANSITION_COMBINE;
   public static final int TRANSITION_REPLACE;

   GIFEncodedImage(byte[] data, int offset, int length) {
      super._data = data;
      super._offset = offset;
      super._length = length;
      this.init();
   }

   GIFEncodedImage(String filename) {
      super._filename = filename;
      this.init();
   }

   private final void init() {
      this._gifInfo = new GIFEncodedImage$GIFImageInfo();
      super._info = this._gifInfo;
      this.populateGIFInfo();
      this._gifFrameInfo = new GIFEncodedImage$GIFFrameInfo[this._gifInfo.frameCount];
      super._frameInfo = this._gifFrameInfo;

      for (int i = 0; i < this._gifInfo.frameCount; i++) {
         this._gifFrameInfo[i] = new GIFEncodedImage$GIFFrameInfo();
      }

      this.populateGIFFrameInfo();
   }

   public final int getBackgroundColor() {
      return this._gifInfo.backgroundColour;
   }

   public final int getIterations() {
      return this._gifInfo.iterations;
   }

   public final int getFrameLeft(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return this._gifFrameInfo[frameIndex].left;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getFrameTop(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return this._gifFrameInfo[frameIndex].top;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getScaledFrameLeft(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return Fixed32.toInt(Fixed32.div(Fixed32.toFP(this._gifFrameInfo[frameIndex].left), super._scaleX));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getScaledFrameTop(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return Fixed32.toInt(Fixed32.div(Fixed32.toFP(this._gifFrameInfo[frameIndex].top), super._scaleY));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getFrameDelay(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return this._gifFrameInfo[frameIndex].frameDelay;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getFrameTransition(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return this._gifFrameInfo[frameIndex].frameTransition;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getFrameNumColors(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= super._info.frameCount) {
         return this._gifFrameInfo[frameIndex].numColours;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final boolean isBackgroundTransparent() {
      return this._gifInfo.backgroundTransparent;
   }

   @Override
   public final int getBitmapType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 2) == 0 && this._gifFrameInfo[frameIndex].isMonochrome ? Bitmap.DEFAULT_TYPE & 128 | 0 | 1 : Bitmap.DEFAULT_TYPE;
      }
   }

   @Override
   public final int getAlphaType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 1) != 0 && this.getFrameTransparency(frameIndex) ? 1 | Bitmap.DEFAULT_TYPE & 128 : 0;
      }
   }

   @Override
   final Bitmap getBitmapImpl(int frameIndex) {
      if (frameIndex >= 0 && frameIndex < super._info.frameCount) {
         boolean readonly = (super._decodeMode & 4) != 0;
         int type = this.getBitmapType(frameIndex);
         int alphaType = this.getAlphaType(frameIndex);
         int width = this.getFrameWidth(frameIndex);
         int height = this.getFrameHeight(frameIndex);
         Bitmap bitmap = new Bitmap(type, width, height, null, readonly, false);
         Bitmap alpha = null;
         if (alphaType != 0) {
            alpha = new Bitmap(alphaType, width, height, null, readonly, false);
         }

         this.getGIFImage(bitmap, alpha, super._scaleX, super._scaleY, -1, frameIndex, super._decodeMode);
         bitmap.setAlphaDirect(alpha);
         return bitmap;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getMIMEType() {
      return "image/gif";
   }

   @Override
   public final EncodedImage getStandardsCompliantEncodedImage() {
      byte[] stdData = getStandardEncodedData(super._data, super._offset, super._length);
      return stdData == super._data ? this : EncodedImage.createEncodedImage(stdData, 0, stdData.length);
   }

   private static final native byte[] getStandardEncodedData(byte[] var0, int var1, int var2);

   private final native void populateGIFInfo();

   private final native void populateGIFFrameInfo();

   private final native void getGIFImage(Bitmap var1, Bitmap var2, int var3, int var4, int var5, int var6, int var7);
}
