package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS1MGF1PseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private Digest _digest;
   private int _count;
   private byte[] _digestInput;
   private byte[] _digestOutput;
   private byte[] _previousDigestOutput;
   private int _digestOutputOffset;
   private int _available;

   public PKCS1MGF1PseudoRandomSource(byte[] seed, Digest digest) {
      this(seed, 0, seed == null ? 0 : seed.length, digest);
   }

   public PKCS1MGF1PseudoRandomSource(byte[] seed, int offset, int length, Digest digest) {
      if (seed != null && length >= 0 && offset >= 0 && seed.length - length >= offset && digest != null) {
         this._digest = digest;
         this._count = 0;
         this._digestInput = new byte[length + 4];
         this._digestOutput = new byte[this._digest.getDigestLength()];
         this._previousDigestOutput = new byte[this._digest.getDigestLength()];
         this._digestOutputOffset = 0;
         this._available = 0;
         System.arraycopy(seed, offset, this._digestInput, 0, length);
         this._digest.reset();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public PKCS1MGF1PseudoRandomSource(SymmetricKey seed, Digest digest) {
      this(seed != null ? seed.getData() : null, digest);
   }

   public PKCS1MGF1PseudoRandomSource(byte[] seed, int offset, int length) {
      this(seed, offset, length, new SHA1Digest());
   }

   public PKCS1MGF1PseudoRandomSource(byte[] seed) {
      this(seed, new SHA1Digest());
   }

   public PKCS1MGF1PseudoRandomSource(SymmetricKey seed) {
      this(seed != null ? seed.getData() : null, new SHA1Digest());
   }

   @Override
   public final String getAlgorithm() {
      return "PKCS1MGF1";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._available == 0) {
               do {
                  int inputOffset = this._digestInput.length - 4;
                  this._digestInput[inputOffset++] = (byte)(this._count >> 24);
                  this._digestInput[inputOffset++] = (byte)(this._count >> 16);
                  this._digestInput[inputOffset++] = (byte)(this._count >> 8);
                  this._digestInput[inputOffset] = (byte)this._count;
                  this._digest.update(this._digestInput);
                  this._available = this._digest.getDigest(this._digestOutput, 0);
                  this._digestOutputOffset = 0;
                  this._count++;
               } while (Arrays.equals(this._digestOutput, this._previousDigestOutput));

               System.arraycopy(this._digestOutput, 0, this._previousDigestOutput, 0, this._digestOutput.length);
            }

            int copyLength = Math.min(length, this._available);
            length -= copyLength;
            this._available -= copyLength;

            while (--copyLength >= 0) {
               buffer[offset++] ^= this._digestOutput[this._digestOutputOffset++];
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   public static final void selfTest() {
      byte[] result = new byte[]{
         -29, 115, -46, 116, 105, -63, 40, 76, 64, -81, 50, 127, 62, 65, 61, 66, -6, -32, -27, 27, -111, -33, -12, -44, 66, 111, 46, 14, 44, -49, -124, 75
      };

      try {
         PKCS1MGF1PseudoRandomSource source = new PKCS1MGF1PseudoRandomSource(SelfTestData.RANDOM_DATA);
         byte[] data = source.getBytes(32);
         if (Arrays.equals(data, result)) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_PRS_PKCS1MGF1 = -4097407853144487793L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_PKCS1MGF1);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_PKCS1MGF1, appRegistry);
      }
   }
}
