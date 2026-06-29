package net.rim.device.internal.media;

final class MpegDuration {
   private static final int[][][] mp3srate = new int[][][]{
      (int[][])({11025, 12000, 8000, 0, -804651004, 22050, 24000, 16000, 0, -805044223, 35, -804651007, 51, -804651004, 44100, 48000}),
      (int[][])({0, 0, 0, 0, -804650999, 0, 128, 192, 224, 240, 248, 252, 254, 255, -804782028, 17170432}),
      (int[][])({22050, 24000, 16000, 0, -805044223, 35, -804651007, 51, -804651004, 44100, 48000, 32000, 0, -804650999, 95, 103}),
      (int[][])({44100, 48000, 32000, 0, -804650999, 95, 103, 118, 134, 148, 159, 204, 244, 39, -804782048, 140249440})
   };

   private MpegDuration() {
   }

   public static final long getDuration(byte[] buf, long fileLength) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private static final int getDword(byte[] data, int offset) {
      return (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16 | (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
   }
}
