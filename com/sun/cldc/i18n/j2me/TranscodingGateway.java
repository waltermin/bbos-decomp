package com.sun.cldc.i18n.j2me;

class TranscodingGateway {
   static String ASCII = "US-ASCII";
   static String ISO8859_1 = "ISO-8859-1";

   static int sizeOf(int aEncodingType, int aSourceLength, int aDestLength, boolean isL2U) {
      if (isL2U) {
         if (aEncodingType == 0 || aEncodingType == 1) {
            if (aSourceLength > aDestLength) {
               return aDestLength;
            }

            return aSourceLength;
         }
      } else {
         if (aEncodingType >= 0 && aEncodingType <= 26) {
            if (aSourceLength > aDestLength) {
               return aDestLength;
            }

            return aSourceLength;
         }

         switch (aEncodingType) {
            case 28:
            case 39:
               aSourceLength *= 2;
               if (aSourceLength <= aDestLength) {
                  return aSourceLength;
               }

               return aDestLength - (aDestLength & 1);
         }
      }

      return -1;
   }

   static native int L2U(int var0, byte[] var1, int var2, int var3, Object var4, int var5, int var6, int[] var7, byte[] var8, int var9);

   static native int U2L(int var0, char[] var1, int var2, int var3, byte[] var4, int var5, int var6, int[] var7, byte[] var8, int var9);
}
