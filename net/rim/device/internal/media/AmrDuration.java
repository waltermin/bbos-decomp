package net.rim.device.internal.media;

final class AmrDuration {
   private static final int AUDIO_DATA_OFFSET = 6;
   private static final int[] bits = new int[]{
      95,
      103,
      118,
      134,
      148,
      159,
      204,
      244,
      39,
      -804782048,
      140249440,
      111413100,
      88475110,
      70124720,
      55706550,
      43909880,
      35390040,
      28180960,
      22282620,
      17695020,
      13762800,
      11141310,
      8519830,
      6553720,
      5242970,
      3932230,
      1870004480,
      290219371,
      -1258225653,
      262341,
      1889158148,
      1867348391,
      1761869830,
      67169311,
      -202629271,
      526976000
   };

   private AmrDuration() {
   }

   public static final long getDuration(byte[] buf, long length) {
      if (length >= 0 && buf.length >= 6) {
         if (buf[0] == 35 && buf[1] == 33 && buf[2] == 65 && buf[3] == 77 && buf[4] == 82 && buf[5] == 10) {
            int size = 6;
            int frames = 0;

            while (buf.length > size) {
               int type = buf[size] & 255;
               int ft = (type & 120) >> 3;
               if (ft < 8) {
                  int frameSize = bits[ft] + 8 + 7 >> 3;
                  size += frameSize;
                  frames++;
               } else if (ft < 12) {
                  size += 6;
                  frames += 6;
               } else {
                  if (ft < 15) {
                     return -1;
                  }

                  size++;
                  frames++;
               }
            }

            return (long)frames * 20 * 1000 * (length - 6) / (size - 6);
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }
}
