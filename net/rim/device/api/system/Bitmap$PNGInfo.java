package net.rim.device.api.system;

public final class Bitmap$PNGInfo {
   public short width;
   public short height;
   public byte color_type;
   public byte bit_depth;
   public boolean transparency;
   public boolean alpha;
   public int alpha_bit_depth;

   public final void getPNGInfo(byte[] png, int offset, int length) {
      EncodedImage image = EncodedImage.createEncodedImage(png, offset, length);
      if (!(image instanceof PNGEncodedImage)) {
         throw new IllegalArgumentException();
      }

      PNGEncodedImage pngImage = (PNGEncodedImage)image;
      this.width = (short)pngImage.getWidth();
      this.height = (short)pngImage.getHeight();
      this.color_type = (byte)pngImage.getColorType();
      this.bit_depth = (byte)pngImage.getBitDepth();
      this.transparency = pngImage.hasTransparency();
      this.alpha = pngImage.hasAlpha();
      this.alpha_bit_depth = pngImage.getAlphaBitDepth();
   }
}
