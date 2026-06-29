package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRC2CryptoToken extends RC2CryptoToken implements Persistable {
   private static SoftwareRC2CryptoToken _instance = new SoftwareRC2CryptoToken();
   private static final long ID_TEST_RC2 = -7173445221884717265L;

   static final SoftwareRC2CryptoToken getInstance() {
      return _instance;
   }

   private SoftwareRC2CryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey(int bitLength, int effectiveBitLength) {
      return new SoftwareRC2CryptoToken$RC2KeyData(bitLength, effectiveBitLength);
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset, int bitLength, int effectiveBitLength) {
      return new SoftwareRC2CryptoToken$RC2KeyData(data, offset, bitLength, effectiveBitLength);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC2CryptoToken$RC2KeyData)) {
         throw new Object();
      }

      SoftwareRC2CryptoToken$RC2KeyData key = (SoftwareRC2CryptoToken$RC2KeyData)data;
      return new SoftwareRC2CryptoToken$RC2CipherContext(NativeBlockCipher.initializeRC2(key.getData(), key.getEffectiveBitLength(), true));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareRC2CryptoToken$RC2CipherContext)) {
         throw new Object();
      }

      ((SoftwareRC2CryptoToken$RC2CipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC2CryptoToken$RC2KeyData)) {
         throw new Object();
      }

      SoftwareRC2CryptoToken$RC2KeyData key = (SoftwareRC2CryptoToken$RC2KeyData)data;
      return new SoftwareRC2CryptoToken$RC2CipherContext(NativeBlockCipher.initializeRC2(key.getData(), key.getEffectiveBitLength(), false));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareRC2CryptoToken$RC2CipherContext)) {
         throw new Object();
      }

      ((SoftwareRC2CryptoToken$RC2CipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC2CryptoToken$RC2KeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRC2CryptoToken$RC2KeyData)data).getData();
      }
   }

   @Override
   public final int extractKeyDataLength(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC2CryptoToken$RC2KeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRC2CryptoToken$RC2KeyData)data).getLength();
      }
   }

   @Override
   public final int extractKeyEffectiveBitLength(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC2CryptoToken$RC2KeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRC2CryptoToken$RC2KeyData)data).getEffectiveBitLength();
      }
   }

   @Override
   public final int hashCode() {
      return -1980838759;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareRC2CryptoToken;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1};
      byte[] CIPHER_TEXT = new byte[]{39, -117, 39, -28, 46, 47, 13, 73};
      byte[] TEST_KEY = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1};
      int length = PLAIN_TEXT.length;
      int effectiveKeyLength = 64;
      byte[] target = new byte[length];

      label61:
      try {
         RC2Key key = new RC2Key(TEST_KEY, effectiveKeyLength);
         RC2DecryptorEngine engine = new RC2DecryptorEngine(key);
         engine.decrypt(CIPHER_TEXT, 0, target, 0);
      } finally {
         break label61;
      }

      if (!Arrays.equals(target, 0, PLAIN_TEXT, 0, length)) {
         throw new Object();
      }

      target = new byte[length];

      label55:
      try {
         RC2Key key = new RC2Key(TEST_KEY, effectiveKeyLength);
         RC2EncryptorEngine engine = new RC2EncryptorEngine(key);
         engine.encrypt(PLAIN_TEXT, 0, target, 0);
      } finally {
         break label55;
      }

      if (!Arrays.equals(target, 0, CIPHER_TEXT, 0, length)) {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(-7173445221884717265L) == null) {
         selfTest();
         appRegistry.put(-7173445221884717265L, appRegistry);
      }
   }
}
