package net.rim.wica.transport.security;

public class SecurityAlgorithm {
   private String _name;
   private int _algorithmType;
   private int _blockSize;
   private boolean _needsIv;
   private KeyType _keyType;
   private static final int MAC_TYPE = 0;
   private static final int SIGNATURE_TYPE = 1;
   private static final int ENCRYPTION_DECRYPTION_TYPE = 2;
   private static final int KEY_ESTABLISHING_TYPE = 3;
   public static final SecurityAlgorithm AES_CBC_PKCS5 = new SecurityAlgorithm("AES/CBC/PKCS5Padding", 2, 16, true, KeyType.AES);
   public static final SecurityAlgorithm RSA_ECB_PKCS1 = new SecurityAlgorithm("RSA/ECB/PKCS1Padding", 2, -1, false, KeyType.RSA);
   public static final SecurityAlgorithm RSA_SHA1 = new SecurityAlgorithm("SHA1withRSA", 1, -1, false, KeyType.RSA);
   public static final SecurityAlgorithm ECDSA_SHA1 = new SecurityAlgorithm("SHA1withECDSA", 1, -1, false, KeyType.ECDSA);
   public static final SecurityAlgorithm HMAC_SHA1 = new SecurityAlgorithm("HmacSHA1", 0, -1, false, KeyType.AES);
   public static final SecurityAlgorithm ECDH = new SecurityAlgorithm("ECDH", 3, -1, false, KeyType.ECDSA);

   private SecurityAlgorithm(String name, int algorithmType, int blockSize, boolean needsIv, KeyType keyType) {
      this._name = name;
      this._algorithmType = algorithmType;
      this._blockSize = blockSize;
      this._needsIv = needsIv;
      this._keyType = keyType;
   }

   public String getName() {
      return this._name;
   }

   public boolean isMacCipher() {
      return this._algorithmType == 0;
   }

   public boolean isSignatureCipher() {
      return this._algorithmType == 1;
   }

   public boolean isEncryptionDecryptionCipher() {
      return this._algorithmType == 2;
   }

   public boolean isKeyEstablishingCipher() {
      return this._algorithmType == 3;
   }

   public KeyType getKeyType() {
      return this._keyType;
   }

   public int getBlockSize() {
      return this._blockSize;
   }

   public boolean needsIv() {
      return this._needsIv;
   }
}
