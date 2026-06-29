package net.rim.device.api.system;

public final class WBMPEncodedImage extends EncodedImage {
   private WBMPEncodedImage$WBMPImageInfo _wbmpInfo;

   WBMPEncodedImage(byte[] data, int offset, int length) {
      super._data = data;
      super._offset = offset;
      super._length = length;
      this.init();
   }

   private final void init() {
      this._wbmpInfo = new WBMPEncodedImage$WBMPImageInfo();
      super._info = this._wbmpInfo;
      this.populateWBMPInfo();
      super._frameInfo = new EncodedImage$FrameInfo[1];
      super._frameInfo[0] = new EncodedImage$FrameInfo();
      super._frameInfo[0].width = this._wbmpInfo.width;
      super._frameInfo[0].height = this._wbmpInfo.height;
      super._frameInfo[0].isMonochrome = this._wbmpInfo.isMonochrome;
      super._frameInfo[0].hasTransparency = this._wbmpInfo.hasTransparency;
   }

   WBMPEncodedImage(String filename) {
      super._filename = filename;
      this.init();
   }

   public final int getType() {
      return this._wbmpInfo.type;
   }

   @Override
   public final int getBitmapType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 2) != 0 ? Bitmap.DEFAULT_TYPE : Bitmap.DEFAULT_TYPE & 128 | 0 | 1;
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
      this.getWBMPImage(bitmap, super._scaleX, super._scaleY, super._decodeMode);
      return bitmap;
   }

   @Override
   public final String getMIMEType() {
      return "image/vnd.wap.wbmp";
   }

   private final native void populateWBMPInfo();

   private final native void getWBMPImage(Bitmap var1, int var2, int var3, int var4);
}
