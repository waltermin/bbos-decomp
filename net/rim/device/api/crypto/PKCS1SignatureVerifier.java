package net.rim.device.api.crypto;

import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS1SignatureVerifier implements SignatureVerifier {
   private RSAPublicKey _key;
   private int _length;
   private OID _digestOid;
   private Digest _digest;
   private byte[] _signature;
   private boolean _encodingFailure;
   private boolean _version15;
   private int _encodedMessageOffset;
   private byte[] _encodedMessage;
   private static final long ID_TEST_VERIFIER_PKCS1 = 4084400956603387565L;

   public PKCS1SignatureVerifier(RSAPublicKey key, byte[] signature, int signatureOffset) {
      this(key, new SHA1Digest(), signature, signatureOffset);
   }

   public PKCS1SignatureVerifier(RSAPublicKey key, Digest digest, byte[] signature, int signatureOffset) throws InvalidSignatureEncodingException {
      if (key != null && digest != null && signature != null && signatureOffset >= 0) {
         this._key = key;
         this._length = key.getRSACryptoSystem().getModulusLength();
         this._digestOid = OIDs.getAssociatedOID(3134008036018563479L, digest.getAlgorithm());
         this._digest = digest;
         if (signatureOffset >= 0 && signatureOffset + 1 <= signature.length) {
            this._signature = new byte[this._length];
            int numberOfPaddingBytesRequired = this._length - Math.min(this._length, signature.length - signatureOffset);
            System.arraycopy(signature, signatureOffset, this._signature, numberOfPaddingBytesRequired, this._length - numberOfPaddingBytesRequired);
            this._encodedMessage = new byte[this._length];
            this._key
               .getRSACryptoToken()
               .verifyRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), this._signature, 0, this._encodedMessage, 0);
            this._encodedMessageOffset = 0;
            if (this._encodedMessage[this._encodedMessageOffset++] == 0 && this._encodedMessage[this._encodedMessageOffset++] == 1) {
               int paddingLength;
               for (paddingLength = 0; this._encodedMessage[this._encodedMessageOffset] == -1 && this._encodedMessageOffset < this._length; paddingLength++) {
                  this._encodedMessageOffset++;
               }

               if (this._encodedMessageOffset != this._length && this._encodedMessage[this._encodedMessageOffset++] == 0 && paddingLength >= 8) {
                  int digestDataLength = this._length - this._encodedMessageOffset;
                  if (digestDataLength == this._digest.getDigestLength()) {
                     this._version15 = true;
                  }
               } else {
                  this._encodingFailure = true;
               }
            } else {
               this._encodingFailure = true;
            }
         } else {
            throw new InvalidSignatureEncodingException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "RSA_PKCS1_V" + (this._version15 ? "15/" : "20/") + this._digest.getAlgorithm();
   }

   @Override
   public final void update(int data) {
      this._digest.update(data);
   }

   @Override
   public final void update(byte[] data) {
      this.update(data, 0, data == null ? 0 : data.length);
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      this._digest.update(data, offset, length);
   }

   @Override
   public final boolean verify() throws CryptoUnsupportedOperationException {
      if (this._encodingFailure) {
         return false;
      } else {
         byte[] message = this._digest.getDigest(false);
         int digestDataLength = this._length - this._encodedMessageOffset;
         if (digestDataLength == this._digest.getDigestLength()) {
            return Arrays.equals(this._encodedMessage, this._encodedMessageOffset, message, 0, digestDataLength);
         } else if (PKCS1v2SignaturesFacade.available()) {
            return PKCS1v2SignaturesFacade.verify(this._encodedMessage, this._encodedMessageOffset, message, this._digestOid);
         } else {
            throw new CryptoUnsupportedOperationException();
         }
      }
   }

   public static final void selfTest() {
      try {
         RSACryptoSystem cryptoSystemRSA = new RSACryptoSystem(1024);
         RSAPublicKey pubKey = new RSAPublicKey(cryptoSystemRSA, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N);
         PKCS1SignatureVerifier verifier = new PKCS1SignatureVerifier(pubKey, SelfTestData_PK1.SIGNATURE_PKCS1_RSA, 0);
         verifier.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
         if (verifier.verify()) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(4084400956603387565L) == null) {
         selfTest();
         appRegistry.put(4084400956603387565L, appRegistry);
      }
   }
}
