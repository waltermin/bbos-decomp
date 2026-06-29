package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareCAST128CryptoToken extends CAST128CryptoToken implements Persistable {
   private static SoftwareCAST128CryptoToken _instance = new SoftwareCAST128CryptoToken();
   private static final long ID_TEST_CAST128 = 5824818956596953396L;

   static final SoftwareCAST128CryptoToken getInstance() {
      return _instance;
   }

   private SoftwareCAST128CryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey() {
      return new SoftwareCAST128CryptoToken$CAST128KeyData();
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset) {
      return new SoftwareCAST128CryptoToken$CAST128KeyData(data, offset);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareCAST128CryptoToken$CAST128KeyData)) {
         throw new Object();
      }

      SoftwareCAST128CryptoToken$CAST128KeyData key = (SoftwareCAST128CryptoToken$CAST128KeyData)data;
      return new SoftwareCAST128CryptoToken$CAST128CipherContext(NativeBlockCipher.initializeCAST128(key.getData(), true));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareCAST128CryptoToken$CAST128CipherContext)) {
         throw new Object();
      }

      ((SoftwareCAST128CryptoToken$CAST128CipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareCAST128CryptoToken$CAST128KeyData)) {
         throw new Object();
      }

      SoftwareCAST128CryptoToken$CAST128KeyData key = (SoftwareCAST128CryptoToken$CAST128KeyData)data;
      return new SoftwareCAST128CryptoToken$CAST128CipherContext(NativeBlockCipher.initializeCAST128(key.getData(), false));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareCAST128CryptoToken$CAST128CipherContext)) {
         throw new Object();
      }

      ((SoftwareCAST128CryptoToken$CAST128CipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareCAST128CryptoToken$CAST128KeyData)) {
         throw new Object();
      } else {
         return ((SoftwareCAST128CryptoToken$CAST128KeyData)data).getData();
      }
   }

   @Override
   public final int hashCode() {
      return 964080393;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareCAST128CryptoToken;
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{1, 35, 69, 103, -119, -85, -51, -17};
      byte[] CIPHER_TEXT = new byte[]{35, -117, 79, -27, -124, 126, 68, -78};
      byte[] TEST_KEY = new byte[]{1, 35, 69, 103, 18, 52, 86, 120, 35, 69, 103, -119, 52, 86, 120, -102};
      int length = PLAIN_TEXT.length;
      byte[] target = new byte[length];

      label61:
      try {
         CAST128Key key = new CAST128Key(TEST_KEY);
         CAST128DecryptorEngine engine = new CAST128DecryptorEngine(key);
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
         CAST128Key key = new CAST128Key(TEST_KEY);
         CAST128EncryptorEngine engine = new CAST128EncryptorEngine(key);
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
      if (appRegistry.getOrWaitFor(5824818956596953396L) == null) {
         selfTest();
         appRegistry.put(5824818956596953396L, appRegistry);
      }
   }
}
