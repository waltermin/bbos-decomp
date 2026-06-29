package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SPKMKDFPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private Digest _digest;
   private byte[] _sharedSecret;
   private byte[] _parameters;
   private byte[] _outputBuffer;
   private byte[] _previousOutputBuffer;
   private int _outputOffset;
   private int _outputLength;
   private boolean _alreadyRun;

   public SPKMKDFPseudoRandomSource(byte[] sharedSecret) {
      this(sharedSecret, 0, sharedSecret == null ? 0 : sharedSecret.length, false, 0, (Digest)(new Object()));
   }

   public SPKMKDFPseudoRandomSource(byte[] sharedSecret, int offset, int length, boolean confidentiality, int algorithmNumber, Digest digest) {
      if (sharedSecret != null && length >= 0 && offset >= 0 && sharedSecret.length - length >= offset && algorithmNumber >= 0 && digest != null) {
         this._parameters = new byte[3];
         this._parameters[0] = (byte)(confidentiality ? 67 : 73);
         this._parameters[1] = (byte)(48 + algorithmNumber);
         this._parameters[2] = 48;
         this._digest = digest;
         this._digest.reset();
         this._outputLength = digest.getDigestLength();
         this._sharedSecret = new byte[length];
         System.arraycopy(sharedSecret, offset, this._sharedSecret, 0, length);
         this._outputOffset = this._outputLength;
         this._outputBuffer = new byte[this._outputLength];
         this._previousOutputBuffer = new byte[this._outputLength];
         this._alreadyRun = false;
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "SPKMKDF";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         if (this._alreadyRun) {
            throw new Object();
         }

         int skipAmount = this._outputLength - length % this._outputLength;

         while (length > 0) {
            if (this._outputOffset == this._outputLength) {
               do {
                  this._digest.update(this._sharedSecret);
                  this._digest.update(this._parameters);
                  this._digest.update(this._sharedSecret);
                  this._parameters[2]++;
                  this._digest.getDigest(this._outputBuffer, 0);
                  this._outputOffset = 0;
               } while (Arrays.equals(this._outputBuffer, this._previousOutputBuffer));

               System.arraycopy(this._outputBuffer, 0, this._previousOutputBuffer, 0, this._outputLength);
            }

            this._outputOffset += skipAmount;
            skipAmount = 0;
            int xorLength = Math.min(length, this._outputLength - this._outputOffset);
            length -= xorLength;

            while (xorLength-- > 0) {
               buffer[offset++] ^= this._outputBuffer[this._outputOffset++];
            }
         }

         this._alreadyRun = true;
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getAvailable() {
      return this._alreadyRun ? 0 : Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   public static final void selfTest() {
      byte[] result = new byte[]{
         -13, 14, 47, -76, 23, 42, -67, 26, -32, -95, -16, -2, -120, 65, -43, -12, -76, -96, -96, -127, -22, -65, 37, -43, 105, -88, -43, -124, 23, 66, 59, -69
      };

      try {
         SPKMKDFPseudoRandomSource source = new SPKMKDFPseudoRandomSource(SelfTestData.RANDOM_DATA);
         byte[] data = source.getBytes(32);
         if (Arrays.equals(data, result)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_PRS_SPKMKDF = -6822042188185296029L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_SPKMKDF);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_SPKMKDF, appRegistry);
      }
   }
}
