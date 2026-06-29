package net.rim.device.api.system;

import net.rim.vm.TraceBack;

public final class PNGEncodedImage extends EncodedImage {
   private PNGEncodedImage$PNGImageInfo _pngInfo;

   public PNGEncodedImage(byte[] data, int offset, int length) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      super._data = data;
      super._offset = offset;
      super._length = length;
      this.init();
   }

   PNGEncodedImage(String filename) {
      super._filename = filename;
      this.init();
   }

   private PNGEncodedImage(Bitmap bitmap, int x, int y, int width, int height) {
      try {
         byte[] data = getPNGData(bitmap, x, y, width, height);
         super._data = data;
         super._offset = 0;
         super._length = data.length;
         this.init();
      } catch (OutOfMemoryError ex) {
         net.rim.vm.Memory.maximizeContiguousRAM();
         byte[] data = getPNGData(bitmap, x, y, width, height);
         super._data = data;
         super._offset = 0;
         super._length = data.length;
         this.init();
      }
   }

   public static final PNGEncodedImage encode(Bitmap bitmap) {
      return new PNGEncodedImage(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
   }

   public static final PNGEncodedImage encode(Bitmap bitmap, int x, int y, int width, int height) {
      return new PNGEncodedImage(bitmap, x, y, width, height);
   }

   private final void init() {
      this._pngInfo = new PNGEncodedImage$PNGImageInfo();
      super._info = this._pngInfo;
      this.populatePNGInfo();
      super._frameInfo = new EncodedImage$FrameInfo[1];
      super._frameInfo[0] = new EncodedImage$FrameInfo();
      super._frameInfo[0].width = this._pngInfo.width;
      super._frameInfo[0].height = this._pngInfo.height;
      super._frameInfo[0].isMonochrome = this._pngInfo.isMonochrome;
      super._frameInfo[0].hasTransparency = this._pngInfo.hasTransparency;
   }

   public final int getAlphaBitDepth() {
      return this._pngInfo.alphaBitDepth;
   }

   @Override
   public final int getAlphaType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      }

      if ((super._decodeMode & 1) != 0 && this.getFrameTransparency(frameIndex)) {
         int alphaType = 0;
         if (this._pngInfo.alpha && Display.isRowwise()) {
            alphaType |= 3;
         } else {
            alphaType |= 1;
         }

         return alphaType | Bitmap.DEFAULT_TYPE & 128;
      } else {
         return 0;
      }
   }

   public final int getBitDepth() {
      return this._pngInfo.bitDepth;
   }

   @Override
   final Bitmap getBitmapImpl(int frameIndex) {
      if (frameIndex != 0) {
         throw new IllegalArgumentException();
      }

      boolean readonly = (super._decodeMode & 4) != 0;
      int type = this.getBitmapType(frameIndex);
      int alphaType = this.getAlphaType(frameIndex);
      int width = this.getScaledWidth();
      int height = this.getScaledHeight();
      Bitmap bitmap = new Bitmap(type, width, height, null, readonly, false);
      Bitmap alpha = null;
      if (alphaType != 0) {
         alpha = new Bitmap(alphaType, width, height, null, readonly, false);
      }

      this.getPNGImage(bitmap, alpha, super._scaleX, super._scaleY, -1, super._decodeSteps, super._decodeMode);
      bitmap.setAlphaDirect(alpha);
      return bitmap;
   }

   @Override
   public final int getBitmapType(int frameIndex) {
      if (frameIndex < 0 || frameIndex >= super._info.frameCount) {
         throw new IllegalArgumentException();
      } else {
         return (super._decodeMode & 2) == 0 && this._pngInfo.colourType == 0 && this._pngInfo.bitDepth == 1
            ? Bitmap.DEFAULT_TYPE & 128 | 0 | 1
            : Bitmap.DEFAULT_TYPE;
      }
   }

   public final int getColorType() {
      return this._pngInfo.colourType;
   }

   @Override
   public final String getMIMEType() {
      return "image/png";
   }

   private static final native byte[] getPNGData(Bitmap var0, int var1, int var2, int var3, int var4);

   private final native void getPNGImage(Bitmap var1, Bitmap var2, int var3, int var4, int var5, int var6, int var7);

   private final native void populatePNGInfo();

   public final boolean hasAlpha() {
      return this._pngInfo.alpha;
   }
}
