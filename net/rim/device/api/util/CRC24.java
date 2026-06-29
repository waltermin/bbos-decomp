package net.rim.device.api.util;

public final class CRC24 {
   public static final int INITIAL_VALUE = 13501623;

   private CRC24() {
   }

   public static final native int update(int var0, int var1);

   public static final int update(int crc, byte[] b) {
      return update(crc, b, 0, b.length);
   }

   public static final native int update(int var0, byte[] var1, int var2, int var3);
}
