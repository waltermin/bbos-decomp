package net.rim.device.api.crypto;

final class NativeRSA {
   private NativeRSA() {
   }

   public static final native int getVersion();

   public static final native boolean isSupported(int var0, int var1);

   public static final native void generateKeyPair(
      int var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8
   );

   public static final native void privateKeyOperation(int var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7);

   public static final native void privateKeyOperation(
      int var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, int var6, byte[] var7, int var8
   );

   public static final native void privateKeyOperation(
      int var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, int var8, byte[] var9, int var10
   );

   public static final native void publicKeyOperation(int var0, byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, int var6);
}
