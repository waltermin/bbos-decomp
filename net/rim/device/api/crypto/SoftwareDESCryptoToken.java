package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareDESCryptoToken extends DESCryptoToken implements Persistable {
   private static SoftwareDESCryptoToken _instance = new SoftwareDESCryptoToken();
   private static final long ID_TEST_DES = -64388735954851049L;
   private static final byte[][] WEAK_KEYS;

   static final SoftwareDESCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareDESCryptoToken() {
   }

   @Override
   public final CryptoTokenSymmetricKeyData createKey() {
      return new SoftwareDESCryptoToken$DESKeyData();
   }

   @Override
   public final CryptoTokenSymmetricKeyData injectKey(byte[] data, int offset) {
      return new SoftwareDESCryptoToken$DESKeyData(data, offset);
   }

   @Override
   public final void deleteKey(CryptoTokenSymmetricKeyData data) {
   }

   @Override
   public final CryptoTokenCipherContext initializeEncrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareDESCryptoToken$DESKeyData)) {
         throw new Object();
      }

      SoftwareDESCryptoToken$DESKeyData key = (SoftwareDESCryptoToken$DESKeyData)data;
      return new SoftwareDESCryptoToken$DESCipherContext(NativeBlockCipher.initializeDES(key.getData(), true));
   }

   @Override
   public final void encrypt(CryptoTokenCipherContext context, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset) {
      if (!(context instanceof SoftwareDESCryptoToken$DESCipherContext)) {
         throw new Object();
      }

      ((SoftwareDESCryptoToken$DESCipherContext)context).getNativeBlockCipher().crypt(plaintext, plaintextOffset, ciphertext, ciphertextOffset);
   }

   @Override
   public final CryptoTokenCipherContext initializeDecrypt(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareDESCryptoToken$DESKeyData)) {
         throw new Object();
      }

      SoftwareDESCryptoToken$DESKeyData key = (SoftwareDESCryptoToken$DESKeyData)data;
      return new SoftwareDESCryptoToken$DESCipherContext(NativeBlockCipher.initializeDES(key.getData(), false));
   }

   @Override
   public final void decrypt(CryptoTokenCipherContext context, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset) {
      if (!(context instanceof SoftwareDESCryptoToken$DESCipherContext)) {
         throw new Object();
      }

      ((SoftwareDESCryptoToken$DESCipherContext)context).getNativeBlockCipher().crypt(ciphertext, ciphertextOffset, plaintext, plaintextOffset);
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenSymmetricKeyData data) {
      if (!(data instanceof SoftwareDESCryptoToken$DESKeyData)) {
         throw new Object();
      } else {
         return ((SoftwareDESCryptoToken$DESKeyData)data).getData();
      }
   }

   @Override
   public final int hashCode() {
      return -1203627785;
   }

   @Override
   public final boolean equals(Object obj) {
      return this == obj ? true : obj instanceof SoftwareDESCryptoToken;
   }

   public static final void selfTest() {
      byte[] CIPHER_TEXT = new byte[]{120, -9, 74, -23, -111, -116, -78, 80};
      byte[] TEST_KEY = new byte[]{49, 112, 93, 104, 124, 1, -85, 25};
      int length = SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT.length;
      byte[] target = new byte[length];

      label61:
      try {
         DESKey key = new DESKey(TEST_KEY);
         DESDecryptorEngine engine = new DESDecryptorEngine(key);
         engine.decrypt(CIPHER_TEXT, 0, target, 0);
      } finally {
         break label61;
      }

      if (!Arrays.equals(target, 0, SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, length)) {
         throw new Object();
      }

      target = new byte[length];

      label55:
      try {
         DESKey key = new DESKey(TEST_KEY);
         DESEncryptorEngine engine = new DESEncryptorEngine(key);
         engine.encrypt(SelfTestData_PK1.ENCRYPTION_PLAIN_TEXT, 0, target, 0);
      } finally {
         break label55;
      }

      if (!Arrays.equals(target, 0, CIPHER_TEXT, 0, length)) {
         throw new Object();
      }
   }

   static final void generateKey(byte[] data, int offset) {
      if (data != null && offset >= 0 && data.length - 8 >= offset) {
         boolean weak;
         do {
            RandomSource.getBytes(data, offset, 8);
            generateOddParityInLSB(data, offset, 8);
            weak = false;

            for (int i = 0; i < WEAK_KEYS.length; i++) {
               if (Arrays.equals(data, offset, WEAK_KEYS[i], 0, 8)) {
                  weak = true;
                  break;
               }
            }
         } while (weak);
      } else {
         throw new Object();
      }
   }

   static final void generateOddParityInLSB(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         while (length > 0) {
            data[offset] = (byte)(data[offset] | 1);
            byte tmp = data[offset];
            tmp = (byte)(tmp ^ tmp >> 4);
            tmp = (byte)(tmp ^ tmp >> 2);
            tmp = (byte)(tmp ^ tmp >> 1);
            tmp = (byte)(tmp | 254);
            data[offset] &= tmp;
            length--;
            offset++;
         }
      } else {
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(-64388735954851049L) == null) {
         selfTest();
         appRegistry.put(-64388735954851049L, appRegistry);
      }

      WEAK_KEYS = new byte[][]{
         {1, 1, 1, 1, 1, 1, 1, 1},
         {31, 31, 31, 31, 14, 14, 14, 14},
         {-32, -32, -32, -32, -15, -15, -15, -15},
         {-2, -2, -2, -2, -2, -2, -2, -2},
         {1, -2, 1, -2, 1, -2, 1, -2},
         {-2, 1, -2, 1, -2, 1, -2, 1},
         {31, -32, 31, -32, 14, -15, 14, -15},
         {-32, 31, -32, 31, -15, 14, -15, 14},
         {1, -32, 1, -32, 1, -15, 1, -15},
         {-32, 1, -32, 1, -15, 1, -15, 1},
         {31, -2, 31, -2, 14, -2, 14, -2},
         {-2, 31, -2, 31, -2, 14, -2, 14},
         {1, 31, 1, 31, 1, 14, 1, 14},
         {31, 1, 31, 1, 14, 1, 14, 1},
         {-32, -2, -32, -2, -15, -2, -15, -2},
         {-2, -32, -2, -32, -2, -15, -2, -15}
      };
   }
}
