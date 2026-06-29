package net.rim.device.api.crypto;

final class NativeEC {
   private NativeEC() {
   }

   public static final native int getVersion();

   public static final native boolean isSupported(String var0, int var1);

   public static final native boolean verifyPublicKey(String var0, byte[] var1);

   public static final native void generatePublicKey(String var0, byte[] var1, byte[] var2);

   public static final native void generateKeyPair(String var0, byte[] var1, byte[] var2);

   public static final native void generateCurvePointFromByteArray(String var0, byte[] var1, byte[] var2);

   public static final native void generateDHSharedSecretNoCofactor(String var0, byte[] var1, byte[] var2, byte[] var3, int var4);

   public static final native void generateDHSharedSecretUseCofactor(String var0, byte[] var1, byte[] var2, byte[] var3, int var4);

   public static final native void signDSA(String var0, byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, int var6);

   public static final native boolean verifyDSA(String var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   public static final native void generateMQVSharedSecretUseCofactor(
      String var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, int var7
   );

   public static final native void signNR(String var0, byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, int var6);

   public static final native boolean verifyNR(String var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   public static final native void compressPublicKey(String var0, byte[] var1, byte[] var2);

   public static final native void uncompressPublicKey(String var0, byte[] var1, byte[] var2);

   public static final native void getGroupOrder(String var0, byte[] var1);

   public static final native void getA(String var0, byte[] var1);

   public static final native void getB(String var0, byte[] var1);

   public static final native void getCofactor(String var0, byte[] var1);

   public static final native void getFieldReductor(String var0, byte[] var1);

   public static final native void getBasePoint(String var0, byte[] var1);

   public static final native void multiply(String var0, byte[] var1, byte[] var2, byte[] var3);

   public static final native void multiplyAndAdd(String var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   public static final native void add(String var0, byte[] var1, byte[] var2, byte[] var3);

   public static final native void negate(String var0, byte[] var1, byte[] var2);
}
