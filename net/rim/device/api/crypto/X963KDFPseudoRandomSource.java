package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class X963KDFPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private Digest _digest;
   private byte[] _sharedSecret;
   private byte[] _sharedInfo;
   private byte[] _counterBuffer;
   private int _counter;
   private byte[] _outputBuffer;
   private byte[] _previousOutputBuffer;
   private int _outputOffset;
   private int _outputLength;

   public X963KDFPseudoRandomSource(byte[] sharedSecret, byte[] sharedInfo) {
      this(sharedSecret, 0, sharedSecret == null ? 0 : sharedSecret.length, sharedInfo, new SHA1Digest());
   }

   public X963KDFPseudoRandomSource(byte[] sharedSecret, int offset, int length, byte[] sharedInfo) {
      this(sharedSecret, offset, length, sharedInfo, new SHA1Digest());
   }

   public X963KDFPseudoRandomSource(byte[] sharedSecret, byte[] sharedInfo, Digest digest) {
      this(sharedSecret, 0, sharedSecret == null ? 0 : sharedSecret.length, sharedInfo, digest);
   }

   public X963KDFPseudoRandomSource(byte[] sharedSecret, int offset, int length, byte[] sharedInfo, Digest digest) {
      if (digest != null && sharedSecret != null && length >= 0 && offset >= 0 && sharedSecret.length - length >= offset) {
         this._digest = digest;
         this._digest.reset();
         this._outputLength = digest.getDigestLength();
         this._sharedSecret = new byte[length];
         System.arraycopy(sharedSecret, offset, this._sharedSecret, 0, length);
         this._counter = 1;
         this._outputOffset = this._outputLength;
         this._sharedInfo = sharedInfo != null ? Arrays.copy(sharedInfo) : new byte[0];
         this._outputBuffer = new byte[this._outputLength];
         this._previousOutputBuffer = new byte[this._outputLength];
         this._counterBuffer = new byte[4];
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "X963KDF";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._outputOffset == this._outputLength) {
               do {
                  this._digest.update(this._sharedSecret);
                  this._counterBuffer[0] = (byte)(this._counter >> 24);
                  this._counterBuffer[1] = (byte)(this._counter >> 16);
                  this._counterBuffer[2] = (byte)(this._counter >> 8);
                  this._counterBuffer[3] = (byte)this._counter;
                  this._digest.update(this._counterBuffer);
                  this._digest.update(this._sharedInfo);
                  this._digest.getDigest(this._outputBuffer, 0);
                  this._outputOffset = 0;
                  this._counter++;
               } while (Arrays.equals(this._outputBuffer, this._previousOutputBuffer));

               System.arraycopy(this._outputBuffer, 0, this._previousOutputBuffer, 0, this._outputLength);
            }

            int xorLength = Math.min(length, this._outputLength - this._outputOffset);
            length -= xorLength;

            while (xorLength-- > 0) {
               buffer[offset++] ^= this._outputBuffer[this._outputOffset++];
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
         -111,
         -33,
         -12,
         -44,
         66,
         111,
         46,
         14,
         44,
         -49,
         -124,
         75,
         106,
         -88,
         -115,
         -67,
         -30,
         65,
         -55,
         81,
         -82,
         -101,
         -42,
         -58,
         -22,
         53,
         119,
         76,
         53,
         -75,
         73,
         -110
      };

      try {
         X963KDFPseudoRandomSource source = new X963KDFPseudoRandomSource(SelfTestData.RANDOM_DATA, null, new SHA1Digest());
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
      long ID_TEST_PRS_X963KDF = 6756509893652036338L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_X963KDF);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_X963KDF, appRegistry);
      }
   }
}
