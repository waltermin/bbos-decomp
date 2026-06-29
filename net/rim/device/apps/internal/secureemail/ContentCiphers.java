package net.rim.device.apps.internal.secureemail;

public class ContentCiphers {
   public static final int NUM_CONTENT_CIPHERS;
   public static final int AES_256;
   public static final int AES_192;
   public static final int AES_128;
   public static final int CAST_128;
   public static final int RC2_128;
   public static final int TRIPLE_DES;
   public static final int RC2_64;
   public static final int RC2_40;
   public static final int DES;
   public static final int ALL;
   public static final int STRONG;
   public static final int FIPS;

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
