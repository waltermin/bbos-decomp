package net.rim.device.api.crypto;

final class NativeDL {
   private NativeDL() {
   }

   public static final native int getVersion();

   public static final native boolean isSupported(byte[] var0, byte[] var1, byte[] var2, int var3, int var4);

   public static final native void generatePublicKey(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   public static final native void generateKeyPair(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   public static final native void generateDHSharedSecretNoCofactor(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, int var6);

   public static final native void signDSA(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, int var6, byte[] var7, int var8);

   public static final native boolean verifyDSA(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6);
}
