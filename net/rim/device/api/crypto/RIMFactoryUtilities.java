package net.rim.device.api.crypto;

public final class RIMFactoryUtilities {
   private RIMFactoryUtilities() {
   }

   public static final String getBaseAlgorithm(String algorithm) {
      int algorithmLength = algorithm.length();
      int end1 = algorithm.indexOf(95);
      if (end1 == -1) {
         end1 = algorithmLength;
      }

      int end2 = algorithm.indexOf(47);
      if (end2 == -1) {
         end2 = algorithmLength;
      }

      int end = Math.min(end1, end2);
      return end == algorithmLength ? algorithm : algorithm.substring(0, end);
   }

   public static final String stripBaseAlgorithm(String algorithm) {
      int algorithmLength = algorithm.length();
      int end1 = algorithm.indexOf(95);
      if (end1 == -1) {
         end1 = algorithmLength;
      }

      int end2 = algorithm.indexOf(47);
      if (end2 == -1) {
         end2 = algorithmLength;
      }

      int end = Math.min(end1, end2);
      return end == algorithmLength ? null : algorithm.substring(end + 1, algorithmLength);
   }

   public static final int getKeyBitLength(String algorithm, int defaultBitLength) {
      int start = algorithm.indexOf(95);
      if (start < 0) {
         return defaultBitLength;
      }

      int end = algorithm.indexOf(95, ++start);
      if (end < 0) {
         end = algorithm.length();
      }

      return Integer.parseInt(algorithm.substring(start, end));
   }

   public static final int getBlockBitLength(String algorithm, int defaultLength) {
      int start = algorithm.indexOf(95);
      if (start < 0) {
         return defaultLength;
      }

      start = algorithm.indexOf(95, start + 1);
      if (start < 0) {
         return defaultLength;
      }

      int end = algorithm.indexOf(95, ++start);
      if (end < 0) {
         end = algorithm.length();
      }

      return Integer.parseInt(algorithm.substring(start, end));
   }

   public static final int getNumRounds(String algorithm, int defaultRounds) {
      int start = algorithm.indexOf(95);
      if (start < 0) {
         return defaultRounds;
      }

      start = algorithm.indexOf(95, start + 1);
      if (start < 0) {
         return defaultRounds;
      }

      start = algorithm.indexOf(95, start + 1);
      return start < 0 ? defaultRounds : Integer.parseInt(algorithm.substring(start + 1, algorithm.length()));
   }

   public static final String getRightMostSubAlgorithm(String algorithm) {
      int start = algorithm.lastIndexOf(47, algorithm.length());
      return start < 0 ? algorithm : algorithm.substring(start + 1, algorithm.length());
   }

   public static final String getLeftMostSubAlgorithm(String algorithm) {
      int end = algorithm.indexOf(47, 0);
      return end < 0 ? algorithm : algorithm.substring(0, end);
   }

   public static final String stripLeftMostSubAlgorithm(String algorithm) {
      int start = algorithm.indexOf(47, 0);
      return start < 0 ? null : algorithm.substring(start + 1, algorithm.length());
   }

   public static final String stripRightMostSubAlgorithm(String algorithm) {
      int start = algorithm.lastIndexOf(47, algorithm.length());
      return start < 0 ? null : algorithm.substring(0, start);
   }
}
