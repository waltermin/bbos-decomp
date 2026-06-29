package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class P1363KDF1PseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private byte[] _outputBuffer;
   private int _outputOffset;

   public P1363KDF1PseudoRandomSource(byte[] sharedSecret, int offset, int length, byte[] optionalParameters) {
      this(sharedSecret, offset, length, optionalParameters, new SHA1Digest());
   }

   public P1363KDF1PseudoRandomSource(byte[] sharedSecret, int offset, int length, byte[] optionalParameters, Digest digest) {
      if (digest != null && sharedSecret != null && offset >= 0 && length >= 0 && sharedSecret.length - length >= offset) {
         if (optionalParameters == null) {
            optionalParameters = new byte[0];
         }

         digest.reset();
         digest.update(sharedSecret, offset, length);
         digest.update(optionalParameters);
         this._outputBuffer = digest.getDigest();
         this._outputOffset = 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "P1363KDF1";
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
   public final int getAvailable() {
      return this._outputBuffer.length - this._outputOffset;
   }

   @Override
   public final int getMaxAvailable() {
      return this._outputBuffer.length;
   }

   public static final void selfTest() {
      byte[] result = new byte[]{-47, -10, 88, -55, -41, -55, 61, -12, -114, -33, -68, 59, -2, 98, -104, -83, 68, 85, 70, 84};

      try {
         byte[] key = Arrays.copy(SelfTestData.RANDOM_DATA);
         P1363KDF1PseudoRandomSource source = new P1363KDF1PseudoRandomSource(SelfTestData.RANDOM_DATA, 0, SelfTestData.RANDOM_DATA.length, null);
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
      long ID_TEST_PRS_P1363KDF1 = 6927171745752725470L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_P1363KDF1);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_P1363KDF1, appRegistry);
      }
   }
}
