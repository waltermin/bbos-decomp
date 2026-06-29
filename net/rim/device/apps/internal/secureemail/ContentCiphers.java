package net.rim.device.apps.internal.secureemail;

public class ContentCiphers {
   public static final int NUM_CONTENT_CIPHERS = 9;
   public static final int AES_256 = 1;
   public static final int AES_192 = 2;
   public static final int AES_128 = 4;
   public static final int CAST_128 = 8;
   public static final int RC2_128 = 16;
   public static final int TRIPLE_DES = 32;
   public static final int RC2_64 = 64;
   public static final int RC2_40 = 128;
   public static final int DES = 256;
   public static final int ALL = 511;
   public static final int STRONG = 63;
   public static final int FIPS = 39;

   public static String[] getLabels() {
      return SecureEmailResources.getStringArray(30);
   }

   public static String[] getDialogLabels() {
      return SecureEmailResources.getStringArray(115);
   }

   public static String getLabel(int contentCipher) {
      int bitIndex;
      for (bitIndex = 0; bitIndex < 9; bitIndex++) {
         int currentBit = 1 << bitIndex;
         if ((contentCipher & currentBit) != 0) {
            break;
         }
      }

      return getLabels()[bitIndex];
   }

   public static boolean isStrong(int contentCipher) {
      return (contentCipher & 63) != 0;
   }
}
