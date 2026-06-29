package net.rim.device.api.system;

public final class BMPEncodedImage extends EncodedImage {
   private BMPEncodedImage$BMPImageInfo _bmpInfo;

   BMPEncodedImage(byte[] data, int offset, int length) {
      super._data = data;
      super._offset = offset;
      super._length = length;
      this.init();
   }

   private final void init() {
      this._bmpInfo = new BMPEncodedImage$BMPImageInfo();
      super._info = this._bmpInfo;
      this.populateBMPInfo();
      super._frameInfo = new EncodedImage$FrameInfo[1];
      super._frameInfo[0] = new EncodedImage$FrameInfo();
      super._frameInfo[0].width = this._bmpInfo.width;
      super._frameInfo[0].height = this._bmpInfo.height;
      super._frameInfo[0].isMonochrome = this._bmpInfo.isMonochrome;
      super._frameInfo[0].hasTransparency = this._bmpInfo.hasTransparency;
   }

   BMPEncodedImage(String filename) {
      super._filename = filename;
      this.init();
   }

   public final int getBitDepth() {
      return this._bmpInfo.bitDepth;
   }

   @Override
   public final int getBitmapType(int frameIndex) {
      if (frameIndex >= 0 && frameIndex < super._info.frameCount) {
         return Bitmap.DEFAULT_TYPE;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getAlphaType(int frameIndex) {
      if (frameIndex >= 0 && frameIndex < super._info.frameCount) {
         return 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   final Bitmap getBitmapImpl(int frameIndex) {
      if (frameIndex != 0) {
         throw new IllegalArgumentException();
      }

      boolean readonly = (super._decodeMode & 4) != 0;
      int width = this.getScaledWidth();
      int height = this.getScaledHeight();
      Bitmap bitmap = new Bitmap(this.getBitmapType(frameIndex), width, height, null, readonly, false);
      this.getBMPImage(bitmap, super._scaleX, super._scaleY, super._decodeMode);
      return bitmap;
   }

   static final native boolean isBMPSupported();

   @Override
   public final String getMIMEType() {
      return "image/bmp";
   }

   private final native void populateBMPInfo();

   private final native void getBMPImage(Bitmap var1, int var2, int var3, int var4);
}
