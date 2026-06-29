package com.fourthpass.wapstack.util;

public final class Utils {
   public static final short longToBytes(long value, byte[] storeIn) {
      if (storeIn == null) {
         return 0;
      }

      long current = value;
      int bufLength = storeIn.length;
      short vLength = 1;
      if (current > 0) {
         while (current != 0) {
            storeIn[bufLength - vLength] = (byte)(current & 255 & 255);
            vLength++;
            current >>= 8;
         }

         return (short)(vLength - 1);
      } else {
         while (current != -1) {
            storeIn[bufLength - vLength] = (byte)(current & 255 & 255);
            vLength++;
            current >>= 8;
         }

         return (short)(vLength - 1);
      }
   }

   public static final boolean isCompleteStatus(int status) {
      return status < 48 && status >= 32 || status == 49 || status == 50 || status == 65 || status == 0;
   }
}
