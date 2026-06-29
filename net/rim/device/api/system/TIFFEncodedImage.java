package net.rim.device.api.system;

public final class TIFFEncodedImage extends EncodedImage {
   private TIFFEncodedImage$TIFFImageInfo _tiffInfo;
   private TIFFEncodedImage$TIFFFrameInfo[] _tiffFrameInfo;

   TIFFEncodedImage(byte[] data, int offset, int length) {
      super._data = data;
      super._offset = offset;
      super._length = length;
      this.init();
   }

   private final void init() {
      this._tiffInfo = new TIFFEncodedImage$TIFFImageInfo();
      super._info = this._tiffInfo;
      this.populateTIFFInfo();
      this._tiffFrameInfo = new TIFFEncodedImage$TIFFFrameInfo[this._tiffInfo.frameCount];
      super._frameInfo = this._tiffFrameInfo;

      for (int i = 0; i < this._tiffInfo.frameCount; i++) {
         this._tiffFrameInfo[i] = new TIFFEncodedImage$TIFFFrameInfo();
      }

      this.populateTIFFFrameInfo();
   }

   TIFFEncodedImage(String filename) {
      super._filename = filename;
      this.init();
   }

   @Override
   public final int getBitmapType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 2) == 0 && this._tiffFrameInfo[frameIndex].isMonochrome ? Bitmap.DEFAULT_TYPE & 128 | 0 | 1 : Bitmap.DEFAULT_TYPE;
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

         this.getTIFFImage(bitmap, alpha, super._scaleX, super._scaleY, -1, frameIndex, super._decodeMode);
         bitmap.setAlphaDirect(alpha);
         return bitmap;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getMIMEType() {
      return "image/tiff";
   }

   static final native boolean isTIFFSupported();

   private final native void populateTIFFInfo();

   private final native void populateTIFFFrameInfo();

   private final native void getTIFFImage(Bitmap var1, Bitmap var2, int var3, int var4, int var5, int var6, int var7);
}
