package net.rim.device.internal.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;

public class ScaleBitmap {
   public static final int FILTER_LANCZOS = 0;
   public static final int FILTER_BOX = 1;
   public static final int FILTER_BILINEAR = 2;
   static Filter _filterBox = new BoxFilter();
   static Filter _filterBilinear = new BilinearFilter();
   static Filter _filterLanczos = new LanczosFilter();

   public static int[] scale(Bitmap bitmap, int dst_width, int dst_height) {
      return scale(1, bitmap, dst_width, dst_height);
   }

   public static int[] scale(int filterType, Bitmap bitmap, int dst_width, int dst_height) {
      Filter filter;
      switch (filterType) {
         case -1:
            filter = _filterLanczos;
            break;
         case 0:
         default:
            filter = _filterLanczos;
            break;
         case 1:
            filter = _filterBox;
            break;
         case 2:
            filter = _filterBilinear;
      }

      return scale(filter, bitmap, dst_width, dst_height);
   }

   public static Bitmap scaleBitmap(Bitmap bitmap, int dst_width, int dst_height) {
      return scaleBitmap(1, bitmap, dst_width, dst_height);
   }

   public static Bitmap scaleBitmap(int filterType, Bitmap bitmap, int dst_width, int dst_height) {
      Bitmap scaled = bitmap.createScaledBitmap(dst_width, dst_height);
      bitmap.getScaled(scaled, filterType);
      return scaled;
   }

   public static int[] scale(Filter filter, Bitmap bitmap, int dst_width, int dst_height) {
      int src_width = bitmap.getWidth();
      int src_height = bitmap.getHeight();
      Weights[] weights = null;
      int[] intermediate = new int[dst_width * src_height];
      if (src_width == dst_width) {
         bitmap.getARGB(intermediate, 0, src_width, 0, 0, src_width, src_height);
      } else {
         HorizontalFetcher hfetcher = new HorizontalFetcher();
         weights = calcWeights(filter, src_width, dst_width);
         int iDst = 0;
         int[] data = new int[src_width];

         for (int y = 0; y < src_height; y++) {
            bitmap.getARGB(data, 0, src_width, 0, y, src_width, 1);
            hfetcher.set(data);

            for (int x = 0; x < dst_width; x++) {
               intermediate[iDst++] = weights[x].filter(hfetcher);
            }
         }
      }

      int[] result;
      if (src_height == dst_height) {
         result = intermediate;
      } else {
         result = new int[dst_width * dst_height];
         VerticalFetcher vfetcher = new VerticalFetcher(intermediate, dst_width);
         if (src_width != src_height || dst_width != dst_height || weights == null) {
            weights = calcWeights(filter, src_height, dst_height);
         }

         for (int x = 0; x < dst_width; x++) {
            vfetcher.set(x);

            for (int y = 0; y < dst_height; y++) {
               result[y * dst_width + x] = weights[y].filter(vfetcher);
            }
         }
      }

      return result;
   }

   private static Weights[] calcWeights(Filter filter, int src, int dst) {
      int scaleFP = Fixed32.toFP(dst) / src;
      int fscaleFP = 65536;
      int widthFP = filter.widthFP();
      if (scaleFP < 65536) {
         widthFP = Fixed32.div(widthFP, scaleFP);
         fscaleFP = scaleFP;
      }

      int window_size = Fixed32.toInt(Fixed32.ceil(widthFP) << 1) + 1;
      Weights[] weights = new Weights[dst];

      for (int i = 0; i < dst; i++) {
         int offsetFP = Fixed32.div(32768, scaleFP) - 32768;
         int centerFP = Fixed32.div(Fixed32.toFP(i), scaleFP) + offsetFP;
         int left = Math.max(0, Fixed32.toInt(Fixed32.floor(centerFP - widthFP)));
         int right = Math.min(Fixed32.toInt(Fixed32.ceil(centerFP + widthFP)), src - 1);
         if (right - left + 1 > window_size) {
            if (left < src - 0) {
               left++;
            } else {
               right++;
            }
         }

         weights[i] = new Weights(filter, left, right, centerFP, fscaleFP);
      }

      return weights;
   }
}
