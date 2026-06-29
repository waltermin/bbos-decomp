package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareRC5CryptoToken extends RC5CryptoToken implements Persistable {
   private static SoftwareRC5CryptoToken _instance = new SoftwareRC5CryptoToken();
   private static final long ID_TEST_RC5 = 964869778716240386L;

   static final SoftwareRC5CryptoToken getInstance() {
      return _instance;
   }

   private SoftwareRC5CryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey(int bitLength) {
      return new SoftwareRC5CryptoToken$RC5KeyData(bitLength);
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset, int bitLength) {
      return new SoftwareRC5CryptoToken$RC5KeyData(data, offset, bitLength);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data, int blockLength, int numberOfRounds) {
      if (!(data instanceof SoftwareRC5CryptoToken$RC5KeyData)) {
         throw new Object();
      }

      SoftwareRC5CryptoToken$RC5KeyData key = (SoftwareRC5CryptoToken$RC5KeyData)data;
      return new SoftwareRC5CryptoToken$RC5CipherContext(NativeBlockCipher.initializeRC5(key.getData(), numberOfRounds, blockLength, true));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareRC5CryptoToken$RC5CipherContext)) {
         throw new Object();
      }

      ((SoftwareRC5CryptoToken$RC5CipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data, int blockLength, int numberOfRounds) {
      if (!(data instanceof SoftwareRC5CryptoToken$RC5KeyData)) {
         throw new Object();
      }

      SoftwareRC5CryptoToken$RC5KeyData key = (SoftwareRC5CryptoToken$RC5KeyData)data;
      return new SoftwareRC5CryptoToken$RC5CipherContext(NativeBlockCipher.initializeRC5(key.getData(), numberOfRounds, blockLength, false));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareRC5CryptoToken$RC5CipherContext)) {
         throw new Object();
      }

      ((SoftwareRC5CryptoToken$RC5CipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final int extractKeyDataLength(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC5CryptoToken$RC5KeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRC5CryptoToken$RC5KeyData)data).getLength();
      }
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareRC5CryptoToken$RC5KeyData)) {
         throw new Object();
      } else {
         return ((SoftwareRC5CryptoToken$RC5KeyData)data).getData();
      }
   }

   @Override
   public final int hashCode() {
      return -799193425;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareRC5CryptoToken;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{101, -63, 120, -78, -124, -47, -105, -52};
      byte[] CIPHER_TEXT = new byte[]{-21, 68, -28, 21, -38, 49, -104, 36};
      byte[] TEST_KEY = new byte[]{82, 105, -15, 73, -44, 27, -96, 21, 36, -105, 87, 77, 127, 21, 49, 37};
      int rounds = 12;
      int length = 8;
      byte[] target = new byte[length];

      label61:
      try {
         RC5Key key = new RC5Key(TEST_KEY);
         RC5DecryptorEngine decryptorEngine = new RC5DecryptorEngine(key, length, rounds);
         decryptorEngine.decrypt(CIPHER_TEXT, 0, target, 0);
      } finally {
         break label61;
      }

      if (!Arrays.equals(target, 0, PLAIN_TEXT, 0, length)) {
         throw new Object();
      }

      target = new byte[length];

      label55:
      try {
         RC5Key key = new RC5Key(TEST_KEY);
         RC5EncryptorEngine encryptorEngine = new RC5EncryptorEngine(key, length, rounds);
         encryptorEngine.encrypt(PLAIN_TEXT, 0, target, 0);
      } finally {
         break label55;
      }

      if (!Arrays.equals(target, 0, CIPHER_TEXT, 0, length)) {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(964869778716240386L) == null) {
         selfTest();
         appRegistry.put(964869778716240386L, appRegistry);
      }
   }
}
