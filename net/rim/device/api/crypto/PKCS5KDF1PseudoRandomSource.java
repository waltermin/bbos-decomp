package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS5KDF1PseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private byte[] _outputBuffer;
   private int _outputOffset;

   public PKCS5KDF1PseudoRandomSource(byte[] password, byte[] salt, int iterationCount) {
      this(password, 0, password == null ? 0 : password.length, salt, iterationCount, new SHA1Digest());
   }

   public PKCS5KDF1PseudoRandomSource(byte[] password, int offset, int length, byte[] salt, int iterationCount) {
      this(password, offset, length, salt, iterationCount, new SHA1Digest());
   }

   public PKCS5KDF1PseudoRandomSource(byte[] password, int offset, int length, byte[] salt, int iterationCount, Digest digest) {
      if (password != null && iterationCount >= 1 && digest != null && length > 0 && offset >= 0 && password.length - length >= offset) {
         digest.reset();
         digest.update(password, offset, length);
         if (salt != null) {
            digest.update(salt, 0, salt.length);
         }

         this._outputBuffer = digest.getDigest();

         for (int i = 1; i < iterationCount; i++) {
            digest.update(this._outputBuffer);
            digest.getDigest(this._outputBuffer, 0);
         }

         this._outputOffset = 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "PKCS5KDF1";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer == null || offset < 0 || length < 0 || buffer.length - length < offset) {
         throw new IllegalArgumentException();
      }

      if (this._outputOffset + length > this._outputBuffer.length) {
         throw new IllegalStateException();
      }

      while (length-- > 0) {
         buffer[offset++] ^= this._outputBuffer[this._outputOffset++];
      }
   }

   @Override
   public final int getMaxAvailable() {
      return this._outputBuffer.length;
   }

   @Override
   public final int getAvailable() {
      return this._outputBuffer.length - this._outputOffset;
   }

   public static final void selfTest() {
      byte[] result = new byte[]{-9, -72, 5, 26, -23, 99, 14, -30, 25, -36, 65, -65, -124, -55, -97, -64, -26, 121, -36, 124};

      try {
         byte[] key = Arrays.copy(SelfTestData.RANDOM_DATA);
         PKCS5KDF1PseudoRandomSource source = new PKCS5KDF1PseudoRandomSource(key, key, 1);
         byte[] data = source.getBytes(20);
         if (Arrays.equals(data, result)) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_PRS_PKCS5KDF1 = 4093374189766305243L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_PKCS5KDF1);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_PKCS5KDF1, appRegistry);
      }
   }
}
