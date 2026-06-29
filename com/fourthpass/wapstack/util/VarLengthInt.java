package com.fourthpass.wapstack.util;

import java.io.InputStream;

public final class VarLengthInt {
   public static final short encode(int value, byte[] storeIn) {
      return encode((long)value, storeIn);
   }

   public static final short encode(long value, byte[] storeIn) {
      if (storeIn == null) {
         return 0;
      }

      long current = value & 8589934591L;
      int bufLength = storeIn.length;
      short vLength = 1;

      for (storeIn[bufLength - vLength] = (byte)(current & 127 & 255); (current >>= 7) > 0; storeIn[bufLength - vLength] = (byte)(128 + (current & 127) & 255)) {
         vLength++;
      }

      return vLength;
   }

   public static final long decodeEx(InputStream encodedVal) {
      long value = 0;
      if (encodedVal == null) {
         return value;
      }

      try {
         int input;
         do {
            input = encodedVal.read();
            if (input == -1) {
               return value;
            }

            value = value << 7 | input & 127;
         } while ((input & 128) != 0);

         return value;
      } finally {
         return value;
      }
   }

   public static final long decode(byte[] encodedValBuffer, int startIndex) {
      long value = 0;
      if (encodedValBuffer == null) {
         return value;
      }

      byte input;
      do {
         input = encodedValBuffer[startIndex++];
         value = value << 7 | input & 127;
      } while ((input & 128) != 0);

      return value;
   }

   public static final int getVarLengthCount(byte[] encodedValBuffer, int startIndex) {
      int value = 0;
      if (encodedValBuffer == null) {
         return value;
      }

      byte input;
      do {
         value++;
         input = encodedValBuffer[startIndex++];
      } while ((input & 128) != 0);

      return value;
   }

   public static final short countEncodeBytes(int value) {
      long current = value & 8589934591L;
      short vLength = 1;

      while ((current >>= 7) > 0) {
         vLength++;
      }

      return vLength;
   }
}
