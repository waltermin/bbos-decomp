package net.rim.device.internal.compress;

public final class CompressUtilities {
   private CompressUtilities() {
   }

   public static final native int compress(Object var0, int var1, int var2, byte[] var3, int var4);

   public static final native int decompress(byte[] var0, int var1, int var2, Object var3, int var4, boolean var5, boolean var6);

   public static final native String convertToString(Object var0);

   public static final native int compressBlock(Object var0, int var1, int var2, Object var3, int var4, boolean var5);

   public static final native int decompressBlock(Object var0, int var1, Object var2, int var3, int var4, boolean var5);

   public static final native void zeroBuffers();
}
