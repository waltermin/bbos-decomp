package net.rim.device.internal.crypto;

public final class EncryptionUtilities {
   public static final int ECC_160_R1;
   public static final int ECC_256_R1;
   public static final int ECC_521_R1;
   public static final int ECC_283_K1;
   public static final int ECC_571_K1;
   private static final String ECC_160_R1_NAME;
   private static final int ECC_160_R1_PUBLIC_KEY_LENGTH;
   private static final int ECC_160_R1_PRIVATE_KEY_LENGTH;
   private static final int ECC_160_R1_SHARED_SECRET_LENGTH;
   private static final String ECC_256_R1_NAME;
   private static final int ECC_256_R1_PUBLIC_KEY_LENGTH;
   private static final int ECC_256_R1_PRIVATE_KEY_LENGTH;
   private static final int ECC_256_R1_SHARED_SECRET_LENGTH;
   private static final String ECC_521_R1_NAME;
   private static final int ECC_521_R1_PUBLIC_KEY_LENGTH;
   private static final int ECC_521_R1_PRIVATE_KEY_LENGTH;
   private static final int ECC_521_R1_SHARED_SECRET_LENGTH;
   private static final String ECC_283_K1_NAME;
   private static final int ECC_283_K1_PUBLIC_KEY_LENGTH;
   private static final int ECC_283_K1_PRIVATE_KEY_LENGTH;
   private static final int ECC_283_K1_SHARED_SECRET_LENGTH;
   private static final String ECC_571_K1_NAME;
   private static final int ECC_571_K1_PUBLIC_KEY_LENGTH;
   private static final int ECC_571_K1_PRIVATE_KEY_LENGTH;
   private static final int ECC_571_K1_SHARED_SECRET_LENGTH;

   private EncryptionUtilities() {
   }

   private static final String getCurveName(int curve) {
      switch (curve) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return "EC160R1";
         case 1:
            return "EC256R1";
         case 2:
            return "EC521R1";
         case 3:
            return "EC283K1";
         case 4:
            return "EC571K1";
      }
   }

   public static final boolean isSupported(int curve) {
      return isSupported(getCurveName(curve));
   }

   private static final native boolean isSupported(String var0);

   public static final int getPublicKeyLength(int curve) {
      switch (curve) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return 21;
         case 1:
            return 33;
         case 2:
            return 67;
         case 3:
            return 37;
         case 4:
            return 73;
      }
   }

   public static final int getPrivateKeyLength(int curve) {
      switch (curve) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return 21;
         case 1:
            return 32;
         case 2:
            return 66;
         case 3:
            return 36;
         case 4:
            return 72;
      }
   }

   public static final int getDerivedKeyLength(int curve) {
      switch (curve) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return 20;
         case 1:
            return 32;
         case 2:
            return 66;
         case 3:
            return 36;
         case 4:
            return 72;
      }
   }

   public static final void getGroupOrder(int curve, byte[] order) {
      getGroupOrder(getCurveName(curve), order);
   }

   private static final native void getGroupOrder(String var0, byte[] var1);

   public static final void scalarMultiply(int curve, byte[] multiplier, byte[] point, byte[] result) {
      scalarMultiply(getCurveName(curve), multiplier, point, result);
   }

   private static final native void scalarMultiply(String var0, byte[] var1, byte[] var2, byte[] var3);

   public static final void createKeyPair(int curve, byte[] publicKey, byte[] privateKey) {
      createKeyPair(getCurveName(curve), publicKey, privateKey);
   }

   private static final native void createKeyPair(String var0, byte[] var1, byte[] var2);

   public static final byte[] calculateKey(int curve, byte[] publicKey, byte[] privateKey) {
      byte[] key = new byte[getDerivedKeyLength(curve)];
      calculateKey(curve, publicKey, privateKey, key);
      return key;
   }

   public static final void calculateKey(int curve, byte[] publicKey, byte[] privateKey, byte[] key) {
      calculateKey(getCurveName(curve), publicKey, privateKey, key);
   }

   private static final native void calculateKey(String var0, byte[] var1, byte[] var2, byte[] var3);

   public static final byte[] generateCurvePointFromByteArray(int curve, byte[] byteArray) {
      byte[] curvePoint = new byte[getPublicKeyLength(curve)];
      generateCurvePointFromByteArray(curve, byteArray, curvePoint);
      return curvePoint;
   }

   public static final void generateCurvePointFromByteArray(int curve, byte[] byteArray, byte[] curvePoint) {
      generateCurvePointFromByteArray(getCurveName(curve), byteArray, curvePoint);
   }

   private static final native void generateCurvePointFromByteArray(String var0, byte[] var1, byte[] var2);

   public static final byte[] generateECMQVSharedSecret(
      int curve,
      byte[] localStaticPrivateKey,
      byte[] localEphemeralPrivateKey,
      byte[] localEphemeralPublicKey,
      byte[] remoteStaticPublicKey,
      byte[] remoteEphemeralPublicKey
   ) {
      byte[] sharedSecret = new byte[getDerivedKeyLength(curve)];
      generateECMQVSharedSecret(
         curve, localStaticPrivateKey, localEphemeralPrivateKey, localEphemeralPublicKey, remoteStaticPublicKey, remoteEphemeralPublicKey, sharedSecret
      );
      return sharedSecret;
   }

   public static final void generateECMQVSharedSecret(
      int curve,
      byte[] localStaticPrivateKey,
      byte[] localEphemeralPrivateKey,
      byte[] localEphemeralPublicKey,
      byte[] remoteStaticPublicKey,
      byte[] remoteEphemeralPublicKey,
      byte[] sharedSecret
   ) {
      generateECMQVSharedSecret(
         getCurveName(curve),
         localStaticPrivateKey,
         localEphemeralPrivateKey,
         localEphemeralPublicKey,
         remoteStaticPublicKey,
         remoteEphemeralPublicKey,
         sharedSecret
      );
   }

   private static final native void generateECMQVSharedSecret(String var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6);

   public static final native int getCiphertextLength(int var0);

   public static final native int encrypt(byte[] var0, Object var1, int var2, int var3, Object var4, int var5);

   public static final int decrypt(byte[] key, Object ciphertext, int cOffset, int length, Object plaintext, int pOffset) {
      return decrypt(key, ciphertext, cOffset, length, plaintext, pOffset, false);
   }

   public static final native int decrypt(byte[] var0, Object var1, int var2, int var3, Object var4, int var5, boolean var6);
}
