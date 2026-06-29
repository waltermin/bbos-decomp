package net.rim.device.api.system;

import net.rim.device.api.math.Fixed32;

public class ProgressiveImage extends EncodedImage {
   private boolean _appMode;
   private int _nextMissingChunk;
   private ProgressiveImage$DataChunk[] _outOfOrderChunks;
   private EncodedImage _standardEncoding;
   private boolean _isReplacementNecessary;
   private boolean _isReplacementNecessaryDetermined;
   private static final int REPLACEMENT_IMAGE_THRESHOLD = 76800;

   public ProgressiveImage(byte[] progressiveFile, int offset, int length, boolean appMode) {
      this._appMode = appMode;
      int chunks = this.initInfo(progressiveFile, offset, length);
      this._standardEncoding = null;
      this._isReplacementNecessaryDetermined = false;
      if (appMode) {
         super._data = progressiveFile;
         super._offset = offset;
         super._length = length;
         this._nextMissingChunk = chunks;
      } else {
         if (chunks > 0) {
            this._outOfOrderChunks = new ProgressiveImage$DataChunk[chunks];

            for (int i = chunks - 1; i >= 0; i--) {
               this._outOfOrderChunks[i] = new ProgressiveImage$DataChunk(null);
            }
         }

         super._data = new byte[0];
         initializeImage(progressiveFile, offset, length, super._data);
         super._offset = 0;
         super._length = super._data.length;
         this.appendDataChunks(progressiveFile, offset, length);
      }
   }

   private int initInfo(byte[] file, int offset, int length) {
      ProgressiveImage$ProgressiveImageInfo imageInfo = new ProgressiveImage$ProgressiveImageInfo(null);
      getImageInfo(file, offset, length, imageInfo);
      super._info = imageInfo;
      EncodedImage$FrameInfo[] frameInfo = new EncodedImage$FrameInfo[imageInfo.frameCount];

      for (int i = imageInfo.frameCount - 1; i >= 0; i--) {
         frameInfo[i] = new EncodedImage$FrameInfo();
      }

      getFrameInfo(file, offset, length, frameInfo);
      super._frameInfo = frameInfo;
      return imageInfo._numChunks;
   }

   public boolean appendDataChunks(byte[] progressiveFile, int offset, int length) {
      if (this._appMode) {
         throw new UnsupportedOperationException();
      }

      if (this._outOfOrderChunks == null) {
         return false;
      }

      parseProgressiveChunks(progressiveFile, offset, length, this._outOfOrderChunks, this._nextMissingChunk);
      int numChunks = ((ProgressiveImage$ProgressiveImageInfo)super._info)._numChunks;

      boolean updated;
      for (updated = false; this._nextMissingChunk < numChunks && this._outOfOrderChunks[this._nextMissingChunk]._data != null; updated = true) {
         super._length = appendChunk(super._data, super._offset, super._length, this._outOfOrderChunks[this._nextMissingChunk]);
         this._outOfOrderChunks[this._nextMissingChunk++] = null;
      }

      if (this._nextMissingChunk == numChunks) {
         this._outOfOrderChunks = null;
      }

      return updated;
   }

   public int updateLength(int newLength) {
      if (!this._appMode) {
         throw new UnsupportedOperationException();
      }

      int length = super._length;
      super._length = newLength;
      return length;
   }

   public int getNumSegmentsObtained() {
      return this._appMode ? getSegmentsInFile(super._data, super._offset, super._length) : this._nextMissingChunk;
   }

   public int getNumTotalSegments() {
      return ((ProgressiveImage$ProgressiveImageInfo)super._info)._numChunks;
   }

   @Override
   public String getMIMEType() {
      switch (super._info.imageType) {
         case 6:
            return "image/x-rim-progressive";
         case 7:
         default:
            return "image/x-rwi";
         case 8:
            return "image/x-rgi";
      }
   }

   @Override
   public int getBitmapType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 2) == 0 && super._frameInfo[frameIndex].isMonochrome ? Bitmap.DEFAULT_TYPE & 128 | 0 | 1 : Bitmap.DEFAULT_TYPE;
      }
   }

   @Override
   public int getAlphaType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 1) != 0 && this.getFrameTransparency(frameIndex) ? Bitmap.DEFAULT_TYPE & 128 | 0 | 1 : 0;
      }
   }

   @Override
   Bitmap getBitmapImpl(int frameIndex) {
      if (frameIndex >= 0 && frameIndex < super._info.frameCount) {
         boolean readonly = (super._decodeMode & 4) != 0;
         int alphaType = this.getAlphaType(frameIndex);
         int width = this.getFrameWidth(frameIndex);
         int height = this.getFrameHeight(frameIndex);
         Bitmap bitmap = new Bitmap(this.getBitmapType(frameIndex), width, height, null, readonly, false);
         Bitmap alpha = null;
         if (alphaType != 0) {
            alpha = new Bitmap(alphaType, width, height, null, readonly, false);
         }

         decodeBitmaps(bitmap, alpha, super._data, super._offset, super._length, frameIndex, super._scaleX, super._scaleY, super._decodeMode);
         bitmap.setAlphaDirect(alpha);
         return bitmap;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public EncodedImage getStandardsCompliantEncodedImage() {
      if (this._standardEncoding == null) {
         byte[] stdData = getStandardEncodedData(super._data, super._offset, super._length);
         if (stdData == super._data) {
            return this;
         }

         this._standardEncoding = EncodedImage.createEncodedImage(stdData, 0, stdData.length);
      }

      return this._standardEncoding;
   }

   @Override
   public EncodedImage getReplacementImage(int width, int height) {
      return this.isReplacementImageNecessary(width, height) && this.getNumTotalSegments() == this.getNumSegmentsObtained()
         ? this.getStandardsCompliantEncodedImage()
         : this;
   }

   public boolean isReplacementImageNecessary(int width, int height) {
      if (!this._isReplacementNecessaryDetermined) {
         int imageType = this.getImageType();
         if (imageType == 8) {
            this._isReplacementNecessary = this.getWidth() * this.getHeight() > 76800;
         } else {
            if (imageType != 7) {
               throw new IllegalArgumentException();
            }

            int version = getRWIVersion(super._data, super._offset, super._length);
            if (version == 1) {
               this._isReplacementNecessary = true;
            } else {
               if (version != 2) {
                  throw new IllegalArgumentException();
               }

               int viewableWidth = Fixed32.toInt(Fixed32.mul(Fixed32.toFP(width), this.getScaleX32()));
               viewableWidth = Math.min(viewableWidth, this.getWidth());
               int viewableHeight = Fixed32.toInt(Fixed32.mul(Fixed32.toFP(height), this.getScaleY32()));
               viewableHeight = Math.min(viewableHeight, this.getHeight());
               this._isReplacementNecessary = viewableWidth * viewableHeight > 76800;
            }
         }

         this._isReplacementNecessaryDetermined = true;
      }

      return this._isReplacementNecessary;
   }

   public static native boolean isProgressiveSupported();

   public static native byte[] getImageIdentifier(byte[] var0, int var1, int var2);

   private static native void getImageInfo(byte[] var0, int var1, int var2, ProgressiveImage$ProgressiveImageInfo var3);

   private static native void getFrameInfo(byte[] var0, int var1, int var2, EncodedImage$FrameInfo[] var3);

   private static native void initializeImage(byte[] var0, int var1, int var2, byte[] var3);

   private static native void parseProgressiveChunks(byte[] var0, int var1, int var2, ProgressiveImage$DataChunk[] var3, int var4);

   private static native int appendChunk(byte[] var0, int var1, int var2, ProgressiveImage$DataChunk var3);

   private static native void decodeBitmaps(Bitmap var0, Bitmap var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8);

   private static final native byte[] getStandardEncodedData(byte[] var0, int var1, int var2);

   private static final native int getSegmentsInFile(byte[] var0, int var1, int var2);

   public static final native int getRWIVersion(byte[] var0, int var1, int var2);
}
