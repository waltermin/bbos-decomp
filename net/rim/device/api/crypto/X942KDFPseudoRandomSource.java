package net.rim.device.api.crypto;

import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class X942KDFPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private Digest _digest;
   private byte[] _sharedSecret;
   private byte[] _algorithmID;
   private byte[] _parameters;
   private int _counter;
   private byte[] _outputBuffer;
   private byte[] _previousOutputBuffer;
   private int _outputOffset;
   private int _outputLength;
   private byte[] _counterBuffer;

   public X942KDFPseudoRandomSource(byte[] sharedSecret, OID algorithmID, byte[] partyAInfo, byte[] partyBInfo, byte[] suppPrivateInfo, byte[] suppPublicInfo) {
      this(
         sharedSecret,
         0,
         sharedSecret == null ? 0 : sharedSecret.length,
         algorithmID,
         partyAInfo,
         partyBInfo,
         suppPrivateInfo,
         suppPublicInfo,
         (Digest)(new Object())
      );
   }

   public X942KDFPseudoRandomSource(
      byte[] sharedSecret, int offset, int length, OID algorithmID, byte[] partyAInfo, byte[] partyBInfo, byte[] suppPrivateInfo, byte[] suppPublicInfo
   ) {
      this(sharedSecret, offset, length, algorithmID, partyAInfo, partyBInfo, suppPrivateInfo, suppPublicInfo, (Digest)(new Object()));
   }

   public X942KDFPseudoRandomSource(
      byte[] sharedSecret, OID algorithmID, byte[] partyAInfo, byte[] partyBInfo, byte[] suppPrivateInfo, byte[] suppPublicInfo, Digest digest
   ) {
      this(sharedSecret, 0, sharedSecret == null ? 0 : sharedSecret.length, algorithmID, partyAInfo, partyBInfo, suppPrivateInfo, suppPublicInfo, digest);
   }

   public X942KDFPseudoRandomSource(
      byte[] sharedSecret,
      int offset,
      int length,
      OID algorithmID,
      byte[] partyAInfo,
      byte[] partyBInfo,
      byte[] suppPrivateInfo,
      byte[] suppPublicInfo,
      Digest digest
   ) {
      if (sharedSecret != null && length >= 0 && offset >= 0 && sharedSecret.length - length >= offset && algorithmID != null && digest != null) {
         this._digest = digest;
         this._digest.reset();
         this._outputLength = digest.getDigestLength();
         this._sharedSecret = new byte[length];
         System.arraycopy(sharedSecret, offset, this._sharedSecret, 0, length);
         if (partyAInfo == null) {
            partyAInfo = new byte[0];
         }

         if (partyBInfo == null) {
            partyBInfo = new byte[0];
         }

         if (suppPrivateInfo == null) {
            suppPrivateInfo = new byte[0];
         }

         if (suppPublicInfo == null) {
            suppPublicInfo = new byte[0];
         }

         this._outputOffset = this._outputLength;
         this._algorithmID = algorithmID.toByteArray();
         this._counter = 1;
         this._parameters = new byte[partyAInfo.length + partyBInfo.length + suppPrivateInfo.length + suppPublicInfo.length];
         length = partyAInfo.length;
         System.arraycopy(partyAInfo, 0, this._parameters, 0, length);
         offset += length;
         length = partyBInfo.length;
         System.arraycopy(partyBInfo, 0, this._parameters, 0, length);
         offset += length;
         length = suppPrivateInfo.length;
         System.arraycopy(suppPrivateInfo, 0, this._parameters, 0, length);
         offset += length;
         length = suppPublicInfo.length;
         System.arraycopy(suppPublicInfo, 0, this._parameters, 0, length);
         this._outputBuffer = new byte[this._outputLength];
         this._previousOutputBuffer = new byte[this._outputLength];
         this._counterBuffer = new byte[4];
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "X942KDF";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._outputOffset == this._outputLength) {
               do {
                  this._digest.update(this._sharedSecret);
                  this._digest.update(this._algorithmID);
                  this._counterBuffer[0] = (byte)(this._counter >> 24);
                  this._counterBuffer[1] = (byte)(this._counter >> 16);
                  this._counterBuffer[2] = (byte)(this._counter >> 8);
                  this._counterBuffer[3] = (byte)this._counter;
                  this._digest.update(this._counterBuffer);
                  this._digest.update(this._parameters);
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
         throw new Object();
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
         119, 47, -52, 17, 93, 94, 75, -46, -112, -93, -82, 47, 32, 86, -118, -50, 64, -53, -32, 89, -21, 32, 122, 74, -59, 21, 92, 55, 23, 56, -124, 76
      };

      try {
         X942KDFPseudoRandomSource source = new X942KDFPseudoRandomSource(
            SelfTestData.RANDOM_DATA, OIDs.getOID(540861300), null, null, null, null, (Digest)(new Object())
         );
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
      long ID_TEST_PRS_X942KDF = -4474941857402693795L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_X942KDF);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_X942KDF, appRegistry);
      }
   }
}
