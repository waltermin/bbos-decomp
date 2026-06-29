package net.rim.device.api.system;

import net.rim.vm.TraceBack;

public final class JPEGEncodedImage extends EncodedImage {
   private JPEGEncodedImage$JPEGImageInfo _jpegInfo;
   public static final int FILETYPE_UNKNOWN;
   public static final int FILETYPE_JFIF;
   public static final int FILETYPE_EXIF;
   public static final int FILETYPE_SPIFF;
   public static final int FRAMETYPE_BASELINE;
   public static final int FRAMETYPE_SEQUENTIAL;
   public static final int FRAMETYPE_PROGRESSIVE;

   JPEGEncodedImage(byte[] data, int offset, int length) {
      super._data = data;
      super._offset = offset;
      super._length = length;
      this.$initJPEGImage();
   }

   JPEGEncodedImage(String filename) {
      super._filename = filename;
      this.$initJPEGImage();
   }

   public JPEGEncodedImage(Bitmap bitmap, int quality) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      byte[] data = getJPEGData(bitmap, quality);
      super._data = data;
      super._offset = 0;
      super._length = data.length;
      this.$initJPEGImage();
   }

   private final void $initJPEGImage() {
      this._jpegInfo = new JPEGEncodedImage$JPEGImageInfo();
      super._info = this._jpegInfo;
      this.populateJPEGInfo();
      super._frameInfo = new EncodedImage$FrameInfo[1];
      super._frameInfo[0] = new EncodedImage$FrameInfo();
      super._frameInfo[0].width = this._jpegInfo.width;
      super._frameInfo[0].height = this._jpegInfo.height;
      super._frameInfo[0].isMonochrome = this._jpegInfo.isMonochrome;
      super._frameInfo[0].hasTransparency = this._jpegInfo.hasTransparency;
   }

   public static final JPEGEncodedImage encode(Bitmap bitmap, int quality) {
      byte[] data = getJPEGData(bitmap, quality);
      return new JPEGEncodedImage(data, 0, data.length);
   }

   public final boolean isColor() {
      return this._jpegInfo.isColour;
   }

   public final int getFileType() {
      return this._jpegInfo.fileType;
   }

   public final int getFrameType() {
      return this._jpegInfo.frameType;
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
      this.getJPEGImage(bitmap, super._scaleX, super._scaleY, super._decodeMode);
      bitmap.setAlphaDirect(null);
      return bitmap;
   }

   public static final boolean isJPEGSupported() {
      return true;
   }

   @Override
   public final String getMIMEType() {
      return "image/jpeg";
   }

   @Override
   public final EncodedImage getStandardsCompliantEncodedImage() {
      byte[] stdData = getStandardEncodedData(super._data, super._offset, super._length);
      return stdData == super._data ? this : EncodedImage.createEncodedImage(stdData, 0, stdData.length);
   }

   private static final native byte[] getStandardEncodedData(byte[] var0, int var1, int var2);

   private final native void populateJPEGInfo();

   private final native void getJPEGImage(Bitmap var1, int var2, int var3, int var4);

   private static final native byte[] getJPEGData(Bitmap var0, int var1);
}
